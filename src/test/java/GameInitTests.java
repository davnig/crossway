import exception.PlacementViolationException;
import org.junit.jupiter.api.Test;
import playerProperty.PlayerColor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameInitTests {

    @Test
    void whenGameStartsBoardShouldBeEmpty() {
        Game game = new Game();
        game.start();
        assertTrue(game.getBoard().getBoardState().entrySet().stream()
                .allMatch(entry -> entry.getValue().equals(PlayerColor.NONE)));
    }

    @Test
    void whenGameStartsTurnShouldBeBlack() {
        Game game = new Game();
        game.start();
        assertTrue(game.isBlackTurn());
    }

    @Test
    void whenStoneIsPositionedBoardShouldNotBeEmpty() throws PlacementViolationException {
        Game game = new Game();
        game.start();
        game.placeStoneAt(1, 1);
        assertEquals(game.getBoard().getBoardState().get(new Intersection(1, 1)), PlayerColor.BLACK);
    }

}
