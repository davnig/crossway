package it.units.crossway.client;

import it.units.crossway.client.model.PlayerColor;
import it.units.crossway.client.model.Turn;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TurnTests {

    @Test
    void whenNextTurnShouldSwitchTurnColorAndIncrementTurnNumber() {
        Turn turn = new Turn(3, PlayerColor.BLACK);
        turn.nextTurn();
        assertEquals(PlayerColor.WHITE, turn.getTurnColor());
        assertEquals(4, turn.getTurnNumber());
    }

}
