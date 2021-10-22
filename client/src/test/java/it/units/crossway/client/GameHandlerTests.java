package it.units.crossway.client;

import it.units.crossway.client.model.PlayerColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class GameHandlerTests {

    @Test
    void whenGameStartsBoardShouldBeEmpty() {
        GameHandler gameHandler = new GameHandler();
        gameHandler.getTurn().initFirstTurn();
        assertTrue(gameHandler.getBoard().getBoardState().isEmpty());
    }

    @Test
    void whenGameStartsTurnShouldBeBlack() {
        GameHandler gameHandler = new GameHandler();
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
