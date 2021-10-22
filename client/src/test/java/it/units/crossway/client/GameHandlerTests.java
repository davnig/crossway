package it.units.crossway.client;

import it.units.crossway.client.model.Board;
import it.units.crossway.client.model.Player;
import it.units.crossway.client.model.PlayerColor;
import it.units.crossway.client.model.Turn;
import it.units.crossway.client.remote.Api;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GameHandlerTests {

    @Mock
    private Player player;
    @Mock
    private Api api;
    private GameHandler gameHandler;

    @BeforeEach
    void init() {
        Turn turn = new Turn();
        Board board = new Board();
        gameHandler = new GameHandler(player, board, turn, api);
    }

    @Test
    void whenGameStartsBoardShouldBeEmpty() {
        gameHandler.getTurn().initFirstTurn();
        assertTrue(gameHandler.getBoard().getBoardState().isEmpty());
    }

    @Test
    void whenGameStartsTurnShouldBeBlack() {
        gameHandler.getTurn().initFirstTurn();
        Assertions.assertEquals(PlayerColor.BLACK, gameHandler.getTurn().getCurrentPlayer());
    }

    @Test
    void whenPlayerInsertsNicknameShouldSendAddPlayerReq() {
        // TODO
        fail();
    }

    @Test
    void whenPlayerSelectsNewGameShouldSendCreateGameReq() {
        // TODO
        fail();
    }

    @Test
    void whenPlayerSelectsEnterGameShouldSendGetAvailableGamesReq() {
        // TODO
        fail();
    }

    @Test
    void whenPlayerSelectsAvailableGameShouldSendJoinGameReq() {
        // TODO
        fail();
    }

    @Test
    void whenCreateGameAndGameStartsThenPlayerShouldBeBlack() {
        // TODO
        fail();
    }

    @Test
    void whenJoinGameAndGameStartsThenPlayerShouldBeWhite() {
        // TODO
        fail();
    }

    @Test
    void whenPlayerPlaysTurnShouldSendStonePlacementIntent() {
        // TODO
        fail();
    }

    @Test
    void whenStonePlacementIntentIsReceivedThenBoardShouldBeUpdated() {
        // TODO
        fail();
    }

}
