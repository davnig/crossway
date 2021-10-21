package it.units.crossway.client;

import it.units.crossway.client.model.PlayerColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameHandlerTests {

    @Test
    void whenGameStartsBoardShouldBeEmpty() {
        GameHandler gameHandler = new GameHandler();
        gameHandler.getTurn().initFirstTurn();
        Assertions.assertTrue(gameHandler.getBoard().getBoardState().isEmpty());
    }

    @Test
    void whenGameStartsTurnShouldBeBlack() {
        GameHandler gameHandler = new GameHandler();
        gameHandler.getTurn().initFirstTurn();
        Assertions.assertEquals(PlayerColor.BLACK, gameHandler.getTurn().getCurrentPlayer());
    }

}
