package it.units.crossway.client;

import it.units.crossway.client.exception.PlacementViolationException;
import it.units.crossway.client.model.*;
import it.units.crossway.client.model.dto.GameCreationIntent;
import it.units.crossway.client.model.dto.GameDto;
import it.units.crossway.client.model.dto.PlayerDto;
import it.units.crossway.client.remote.Api;
import it.units.crossway.client.remote.StompMessageHandler;
import lombok.Data;
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

    private static final String newGameChoice = "1";
    private static final String joinGameChoice = "2";
    private static final String quitGameChoice = "q";

    private Player player;
    private Board board;
    private Turn turn;
    private WebSocketStompClient stompClient;
    private Api api;
    private final String WS_ENDPOINT = "ws://localhost:9111/endpoint";

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

    public void startGame() {
        System.out.println("Welcome to crossway! \n");
        System.out.println("choose a nickname!");
        String nickname = IOUtils.getInputLine();
        nickname = nickname.strip();
        player.setNickname(nickname);
        PlayerDto playerDto = new PlayerDto(nickname);
        api.addPlayer(playerDto);
        System.out.println(newGameChoice + " -> Create a new game...\n" + joinGameChoice + " -> Join a game...\n" + quitGameChoice + " -> quit...");
        String choice;
        do {
            choice = IOUtils.getInputLine();
            if(IOUtils.isChoiceToQuit(choice, quitGameChoice)) {
                System.exit(0);
            }
        } while ((!choice.equals(newGameChoice)) && (!choice.equals(joinGameChoice)));
        if (choice.equals(newGameChoice)) {
            createNewGame();
        } else {
            joinExistingGame();
        }
        playGame();
    }

    private void joinExistingGame() {
        String choice;
        List<GameDto> allAvailableGames = api.getAllAvailableGames();
        System.out.println("choose from the list of available games:");
        IntStream.range(0, allAvailableGames.size())
                .forEach(i ->
                        System.out.println(i + " -> opponent is " + allAvailableGames.get(i).getBlackPlayer())
                );
        do {
            choice = IOUtils.getInputLine();
            if(IOUtils.isChoiceToQuit(choice, quitGameChoice)) {
                System.exit(0);
            }
        } while (!IOUtils.isChoiceAValidInteger(choice) && (Integer.parseInt(choice) > allAvailableGames.size()));
        api.joinGame(allAvailableGames.get(Integer.parseInt(choice)).getUuid(), new PlayerDto(player.getNickname()));
        player.setColor(PlayerColor.WHITE);
    }

    private void createNewGame() {
        GameDto gameDto = api.createGame(new GameCreationIntent(player.getNickname()));
        stompClient.connect(WS_ENDPOINT, new StompSessionHandlerAdapter() {
            @SneakyThrows
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/topic/" + gameDto.getUuid(), new StompMessageHandler());
            }
        });
        player.setColor(PlayerColor.BLACK);
    }

    private void playGame() {
        turn.initFirstTurn();
        System.out.println("Game start!!");
        while (true) {
            try {
                IOUtils.clearCLI();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            board.printBoard();
            IOUtils.printCurrentPlayer(turn);
            IOUtils.printAskNextMove();
            playTurn();
        }
    }

    public void startGameAtGivenState(Board board, Turn turn) {
        this.board = board;
        this.turn = turn;
        this.player = new Player();
    }

    public void startGameAtGivenState(Board board, Turn turn, Player player) {
        this.board = board;
        this.turn = turn;
        this.player = player;
    }

    public void playTurn() {
        if (Rules.isPieRuleTurn(turn) && IOUtils.isPieRuleRequested()) {
            turn.applyPieRule();
            return;
        }
        placeStone();
        endTurnChecks();
        turn.nextTurn();
    }

    public void placeStone() {
        StonePlacementIntent stonePlacementIntent = getValidStonePlacementIntent();
        board.placeStone(
                new Intersection(
                        stonePlacementIntent.getRow(),
                        stonePlacementIntent.getColumn()
                ),
                stonePlacementIntent.getPlayer().getColor()
        );
        // todo: send to server
    }

    private StonePlacementIntent getValidStonePlacementIntent() {
        while (true) {
            StonePlacementIntent stonePlacementIntent = getStonePlacementIntent();
            try {
                Rules.validatePlacementIntent(board, stonePlacementIntent);
                return stonePlacementIntent;
            } catch (PlacementViolationException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private StonePlacementIntent getStonePlacementIntent() {
        String input = IOUtils.getInputLine();
        int row = getIntRowFromPlayerInput(input);
        int column = getIntColumnFromPlayerInput(input);
        return new StonePlacementIntent(row, column, player);
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


    private int getIntColumnFromPlayerInput(String input) {
        return Integer.parseInt(input.substring(input.indexOf(",") + 1));
    }

    private int getIntRowFromPlayerInput(String input) {
        return Integer.parseInt(input.substring(0, input.indexOf(",")));
    }
}

