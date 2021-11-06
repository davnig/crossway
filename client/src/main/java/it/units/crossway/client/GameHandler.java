package it.units.crossway.client;

import feign.FeignException;
import it.units.crossway.client.exception.InvalidUserInputException;
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

import static it.units.crossway.client.IOUtils.*;

@Component
@Data
public class GameHandler {

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
        chooseNickname();
        chooseGameType();
        subscribeToTopic();
    }

    void chooseNickname() {
        while(true) {
            System.out.println(IOUtils.IO_CHOOSE_NICKNAME);
            String nickname = IOUtils.getInputLine();
            player.setNickname(nickname);
            PlayerDto playerDto = new PlayerDto(nickname);
            try {
                api.addPlayer(playerDto);
                clearConsole();
                printBanner();
                return;
            } catch (FeignException.BadRequest e) {
                System.out.println("A player with that nickname already exists!");
            }
        }
    }

    void chooseGameType() {
        System.out.println(
                NEW_GAME_CHOICE + " -> Create a new game\n" +
                        JOIN_GAME_CHOICE + " -> Join a game\n" +
                        QUIT_GAME_CHOICE + " -> Quit"
        );
        String choice;
        do {
            choice = IOUtils.getInputLine();
            if (IOUtils.isChoiceToQuit(choice, QUIT_GAME_CHOICE)) {
                api.deletePlayer(new PlayerDto(player.getNickname()));
                System.exit(0);
            }
        } while ((!choice.equals(NEW_GAME_CHOICE)) && (!choice.equals(JOIN_GAME_CHOICE)));
        if (choice.equals(NEW_GAME_CHOICE)) {
            createNewGame();
            System.out.println("Waiting for an opponent...");
        } else {
            joinExistingGame();
            clearConsole();
            printBanner();
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
        if (Rules.isPieRuleTurn(turn) && Rules.isPieRuleNotAlreadyAccepted() && IOUtils.isPieRuleRequested()) {
            Rules.applyPieRule(player, turn);
            api.acceptPieRule(uuid, new PlayerDto(player.getNickname()));
            System.out.println(IOUtils.IO_WAITING_FOR_OPPONENT_MOVE);
            return;
        }
        createAndSendStonePlacementIntent();
    }

    public void playTurnIfSupposedTo() {
        printBoard(board, player);
        if (isPlayerTurn()) {
            playTurn();
        } else {
            System.out.println(IOUtils.IO_WAITING_FOR_OPPONENT_MOVE);
        }
    }

    public void endTurn() {
        clearConsole();
        printBanner();
        checkWinnerIfCouldExist();
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
                api.deletePlayer(new PlayerDto(player.getNickname()));
                System.exit(0);
            }
        } while (!IOUtils.isChoiceAValidInteger(choice) && (Integer.parseInt(choice) > allAvailableGames.size()));
        String uuid = allAvailableGames.get(Integer.parseInt(choice) - 1).getUuid();
        GameDto gameDto = api.joinGame(uuid, new PlayerDto(player.getNickname()));
        this.uuid = gameDto.getUuid();
        player.setColor(PlayerColor.WHITE);
    }

    private List<GameDto> getAllAvailableGamesDto() {
        List<GameDto> allAvailableGames = api.getAllAvailableGames();
        System.out.println("\nChoose from the list of available games:");
        IntStream.range(0, allAvailableGames.size())
                .forEach(i ->
                        System.out.println(i + 1 + " -> opponent is " + allAvailableGames.get(i).getBlackPlayerNickname())
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
            try {
                StonePlacementIntent stonePlacementIntent = IOUtils.getStonePlacementIntentFromInput(player);
                Rules.validatePlacementIntent(board, stonePlacementIntent);
                return stonePlacementIntent;
            } catch (PlacementViolationException | InvalidUserInputException e) {
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
        System.exit(0);
    }

    private boolean isPlayerTurn() {
        return player.getColor().equals(turn.getTurnColor());
    }

}

