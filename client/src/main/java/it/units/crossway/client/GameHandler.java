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
import java.util.StringJoiner;
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
        while (true) {
            IOUtils.printNicknameMenu();
            String nickname = IOUtils.getInputLine();
            player.setNickname(nickname);
            PlayerDto playerDto = new PlayerDto(nickname);
            try {
                api.addPlayer(playerDto);
                return;
            } catch (FeignException.BadRequest e) {
                IOUtils.setHeader("A player with that nickname already exists!");
            }
        }
    }

    void chooseGameType() {
        IOUtils.printGameTypeMenu();
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
            IOUtils.refreshHeader("Waiting for an opponent...");
            // post-join actions in StompMessageHandler
        } else {
            joinExistingGame();
            startGame();
        }
    }

    public void startGame() {
        IOUtils.appendHeader("Game start!!\nYou play as " + player.getColor());
        turn.initFirstTurn();
        IOUtils.appendBody(board.getAsString(player));
        playTurnIfSupposedTo();
    }

    public void startTurn() {
        turn.nextTurn();
        IOUtils.setBody(turn.getTurnInfoAsString());
        IOUtils.appendBody(board.getAsString(player));
        playTurnIfSupposedTo();
    }

    public void playTurnIfSupposedTo() {
        if (isPlayerTurn()) {
            playTurn();
        } else {
            IOUtils.appendFooterAndRefresh(IO_WAITING_FOR_OPPONENT_MOVE);
        }
    }

    void playTurn() {
        if (Rules.isPieRuleTurn(turn) && Rules.isPieRuleNotAlreadyAccepted() && isPieRuleRequested()) {
            Rules.applyPieRule(player, turn);
            api.acceptPieRule(uuid, new PlayerDto(player.getNickname()));
            IOUtils.appendFooterAndRefresh(IOUtils.IO_WAITING_FOR_OPPONENT_MOVE);
            return;
        }
        createAndSendStonePlacementIntent();
    }

    public void endTurn() {
        IOUtils.reset();
        checkWinnerIfCouldExist();
    }

    private void createNewGame() {
        GameDto gameDto = api.createGame(new GameCreationIntent(player.getNickname()));
        this.uuid = gameDto.getUuid();
        player.setColor(PlayerColor.BLACK);
    }

    private void joinExistingGame() {
        String choice;
        List<GameDto> allAvailableGames = api.getAllAvailableGames();
        IOUtils.refreshHeader(constructAvailableGamesMenu(allAvailableGames));
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
        IOUtils.resetHeader();
    }

    private String constructAvailableGamesMenu(List<GameDto> availableGames) {
        StringJoiner sj = new StringJoiner("");
        IntStream.range(0, availableGames.size())
                .forEach(i ->
                        sj.add(i + 1 + " -> opponent is " + availableGames.get(i).getBlackPlayerNickname()
                                + System.lineSeparator())
                );
        return sj.toString();
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
            IOUtils.appendFooterAndRefresh(IOUtils.IO_INSERT_VALID_PLACEMENT);
            try {
                StonePlacementIntent stonePlacementIntent = IOUtils.getStonePlacementIntentFromInput(player);
                Rules.validatePlacementIntent(board, stonePlacementIntent);
                return stonePlacementIntent;
            } catch (PlacementViolationException | InvalidUserInputException e) {
                IOUtils.setFooter(e.getMessage());
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

    public boolean isPieRuleRequested() {
        while (true) {
            IOUtils.appendFooterAndRefresh("Do you want to claim the pie rule? Y-yes N-No");
            String whiteResponse = scanner.nextLine();
            if (whiteResponse.equalsIgnoreCase("Y"))
                return true;
            if (whiteResponse.equalsIgnoreCase("N"))
                return false;
            IOUtils.setFooter("Input not allowed, insert either Y or N");
        }
    }

}

