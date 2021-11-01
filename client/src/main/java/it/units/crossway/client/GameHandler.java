package it.units.crossway.client;

import feign.FeignException;
import it.units.crossway.client.exception.PlacementViolationException;
import it.units.crossway.client.model.*;
import it.units.crossway.client.model.dto.GameCreationIntent;
import it.units.crossway.client.model.dto.GameDto;
import it.units.crossway.client.model.dto.PlayerDto;
import it.units.crossway.client.model.dto.StonePlacementIntentDto;
import it.units.crossway.client.remote.Api;
import it.units.crossway.client.remote.StompMessageHandler;
import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

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
    @Value("${ws-endpoint}")
    private String WS_ENDPOINT;
    private String uuid;

    public GameHandler(Player player, Board board, Turn turn, Api api) {
        this.player = player;
        this.board = board;
        this.turn = turn;
        this.api = api;
        stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    public void init() {
        System.out.println("Welcome to crossway! \n");
        chooseNickname();
        chooseGameType();
        subscribeToTopic();
    }

    void chooseNickname() {
        while(true) {
            System.out.println("choose a nickname!");
            String nickname = IOUtils.getInputLine();
            player.setNickname(nickname);
            PlayerDto playerDto = new PlayerDto(nickname);
            try {
                api.addPlayer(playerDto);
                return;
            }
            catch(FeignException e) {
                System.out.println("Duplicate Player Exception!");
            }
        }

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
            System.out.println("Waiting for an opponent...");
        } else {
            joinExistingGame();
            startGame();
        }
    }

    public void startGame() {
        System.out.println("Game start!!");
        System.out.println("You play as " + player.getColor());
        turn.initFirstTurn();
        playTurnIfSupposedTo();
    }

    void playTurn() {
        if (Rules.isPieRuleTurn(turn) && IOUtils.isPieRuleRequested()) {
            player.setColor(PlayerColor.BLACK);
            turn.setTurnColor(PlayerColor.BLACK);
            return;
        }
        createAndSendStonePlacementIntent();
        checkWinnerIfCouldExist();
    }

    public void playTurnIfSupposedTo() {
        if (isPlayerTurn()) {
            playTurn();
        } else {
            System.out.println("Waiting for opponent move...");
        }
    }

    public void endTurn() {
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
                        System.out.println(i + " -> opponent is " + allAvailableGames.get(i).getBlackPlayerNickname())
                );
        return allAvailableGames;
    }

    private void subscribeToTopic() {
        StompMessageHandler stompMessageHandler = (StompMessageHandler) ApplicationContextUtils.getContext().getBean("stompMessageHandler");
        stompClient.connect(WS_ENDPOINT, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(@NonNull StompSession session, @NonNull StompHeaders connectedHeaders) {
                session.subscribe("/topic/" + uuid, stompMessageHandler);
            }
        });
    }

    private void createAndSendStonePlacementIntent() {
        StonePlacementIntent stonePlacementIntent = getValidStonePlacementIntent();
        api.placeStone(uuid, new StonePlacementIntentDto(stonePlacementIntent));
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

    private void checkWinnerIfCouldExist() {
        if (Rules.couldExistsWinner(turn) && Rules.checkWin(board, player.getColor())) {
            endGame();
        }
    }

    private void endGame() {
        api.winGame(uuid, new PlayerDto(player.getNickname()));
        System.out.println("YOU WIN!!!");
//        System.exit(0);
    }

    private boolean isPlayerTurn() {
        return player.getColor().equals(turn.getTurnColor());
    }

}

