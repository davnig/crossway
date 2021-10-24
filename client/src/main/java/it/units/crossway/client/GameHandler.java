package it.units.crossway.client;

import it.units.crossway.client.exception.PlacementViolationException;
import it.units.crossway.client.model.*;
import it.units.crossway.client.model.dto.GameCreationIntent;
import it.units.crossway.client.model.dto.GameDto;
import it.units.crossway.client.model.dto.PlayerDto;
import it.units.crossway.client.remote.Api;
import it.units.crossway.client.remote.StompMessageHandler;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

@Component
@Data
public class GameHandler {

    private static final String NEW_GAME_CHOICE = "1";
    private static final String JOIN_GAME_CHOICE = "2";
    private static final String QUIT_GAME_CHOICE = "q";

    private Player player;
    private Board board;
    private Turn turn;
    private WebSocketStompClient stompClient;
    private Api api;
    private final String WS_ENDPOINT = "ws://localhost:9111/endpoint";
    private String uuid;

    public GameHandler(Player player, Board board, Turn turn, Api api) {
        this.player = player;
        this.board = board;
        this.turn = turn;
        this.api = api;
//        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    public void play() {
        initGame();
        chooseNickname();
        chooseGameType();
        subscribeToTopic();
        playGame();
    }

    void initGame() {
        System.out.println("Welcome to crossway! \n");
        turn.initFirstTurn();
    }

    void chooseNickname() {
        System.out.println("choose a nickname!");
        String nickname = IOUtils.getInputLine();
        player.setNickname(nickname);
        PlayerDto playerDto = new PlayerDto(nickname);
        api.addPlayer(playerDto);
    }

    void chooseGameType() {
        System.out.println(
                NEW_GAME_CHOICE + " -> Create a new game...\n" +
                        JOIN_GAME_CHOICE + " -> Join a game...\n" +
                        QUIT_GAME_CHOICE + " -> quit..."
        );
        String choice;
        do {
            choice = IOUtils.getInputLine();
            if (IOUtils.isChoiceToQuit(choice, QUIT_GAME_CHOICE)) {
                System.exit(0);
            }
        } while ((!choice.equals(NEW_GAME_CHOICE)) && (!choice.equals(JOIN_GAME_CHOICE)));
        if (choice.equals(NEW_GAME_CHOICE)) {
            createNewGame();
        } else {
            joinExistingGame();
        }
    }

    void playGame() {
        System.out.println("Game start!!");
        while (true) {
            try {
                IOUtils.clearCLI();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            IOUtils.printBoard(board);
            IOUtils.printCurrentPlayer(turn);
            IOUtils.printAskNextMove();
            playTurn();
        }
    }

    void playTurn() {
        if (Rules.isPieRuleTurn(turn) && IOUtils.isPieRuleRequested()) {
            turn.applyPieRule();
            return;
        }
        placeStone();
        endTurnChecks();
        turn.nextTurn();
    }

    private void createNewGame() {
        GameDto gameDto = api.createGame(new GameCreationIntent(player.getNickname()));
        this.uuid = gameDto.getUuid();
        player.setColor(PlayerColor.BLACK);
    }

    private void joinExistingGame() {
        String choice;
        List<GameDto> allAvailableGames = getAllAvailableGamesDto();
        do {
            choice = IOUtils.getInputLine();
            if (IOUtils.isChoiceToQuit(choice, QUIT_GAME_CHOICE)) {
                System.exit(0);
            }
        } while (!IOUtils.isChoiceAValidInteger(choice) && (Integer.parseInt(choice) > allAvailableGames.size()));
        String uuid = allAvailableGames.get(Integer.parseInt(choice)).getUuid();
        GameDto gameDto = api.joinGame(uuid, new PlayerDto(player.getNickname()));
        this.uuid = gameDto.getUuid();
        player.setColor(PlayerColor.WHITE);
    }

    private List<GameDto> getAllAvailableGamesDto() {
        List<GameDto> allAvailableGames = api.getAllAvailableGames();
        System.out.println("choose from the list of available games:");
        IntStream.range(0, allAvailableGames.size())
                .forEach(i ->
                        System.out.println(i + " -> opponent is " + allAvailableGames.get(i).getBlackPlayer())
                );
        return allAvailableGames;
    }

    private void subscribeToTopic() {
        StompMessageHandler stompMessageHandler = (StompMessageHandler) ApplicationContextUtils.getContext().getBean("stompMessageHandler");
        stompClient.connect(WS_ENDPOINT, new StompSessionHandlerAdapter() {
            @SneakyThrows
            @Override
            public void afterConnected(@NonNull StompSession session, @NonNull StompHeaders connectedHeaders) {
                session.subscribe("/topic/" + uuid, stompMessageHandler);
            }
        });
    }

    public void placeStone() {
        StonePlacementIntent stonePlacementIntent = getValidStonePlacementIntent();
        board.placeStone(
                stonePlacementIntent.getRow(),
                stonePlacementIntent.getColumn(),
                stonePlacementIntent.getPlayer().getColor()
        );
        // todo: send to server
    }

    private StonePlacementIntent getValidStonePlacementIntent() {
        while (true) {
            StonePlacementIntent stonePlacementIntent = IOUtils.getStonePlacementIntentFromInput(player);
            try {
                Rules.validatePlacementIntent(board, stonePlacementIntent);
                return stonePlacementIntent;
            } catch (PlacementViolationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void endTurnChecks() {
        if (Rules.isWinValidTurn(turn) && Rules.checkWin(board, turn.getCurrentPlayer())) {
            endGame();
        }
    }

    private void endGame() {
        IOUtils.printWinner(turn);
        System.exit(0);
    }

}

