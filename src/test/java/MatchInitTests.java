import exception.PlacementViolationException;
import org.junit.jupiter.api.Test;
import playerProperty.PlayerColor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatchInitTests {

    @Test
    void whenGameStartsBoardShouldBeEmpty() {
        Match match = new Match();
        match.start();
        assertTrue(match.getBoard().getBoardState().entrySet().stream()
                .allMatch(entry -> entry.getValue().equals(PlayerColor.NONE)));
    }

    @Test
    void whenGameStartsTurnShouldBeBlack() {
        Match match = new Match();
        match.start();
        assertTrue(match.isBlackTurn());
    }

    @Test
    void whenStoneIsPositionedBoardShouldNotBeEmpty() throws PlacementViolationException {
        Match match = new Match();
        match.start();
        match.placeStoneAt(1, 1);
        assertEquals(match.getBoard().getBoardState().get(new Intersection(1, 1)), PlayerColor.BLACK);
    }

}
