import exception.PlacementViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TurnTests {


    @Test
    void isIntersectionEmpty() {
        Intersection intersection = new Intersection(1, 1, PlayerColor.NONE);
        assertEquals(intersection.getStone(), PlayerColor.NONE);
    }

    //test south-west violation
    @Test
    void whenPlayerPlacesStoneAtPosition13AndSWDiagonalViolationShouldOutputError() {
        Board presetBoard = new Board();

        presetBoard.getIntersectionAt(1, 2).setStone(PlayerColor.BLACK);
        presetBoard.getIntersectionAt(2, 3).setStone(PlayerColor.BLACK);
        presetBoard.getIntersectionAt(2, 2).setStone(PlayerColor.WHITE);

        Game game = new Game(presetBoard);
        game.setTurnColor(PlayerColor.WHITE);

        assertThrows(PlacementViolationException.class, () -> game.placeStoneAt(1, 3));
    }

    //test north-west violation
    @Test
    void whenPlayerPlacesStoneAtPosition24AndNWDiagonalViolationShouldOutputError() {
        Board presetBoard = new Board();

        presetBoard.getIntersectionAt(1, 4).setStone(PlayerColor.BLACK);
        presetBoard.getIntersectionAt(2, 3).setStone(PlayerColor.BLACK);
        presetBoard.getIntersectionAt(1, 3).setStone(PlayerColor.WHITE);

        Game game = new Game(presetBoard);
        game.setTurnColor(PlayerColor.WHITE);

        assertThrows(PlacementViolationException.class, () -> game.placeStoneAt(2, 4));
    }

    //test north-east violation
    @Test
    void whenPlayerPlacesStoneAtPosition33AndNEDiagonalViolationShouldOutputError() {
        Board presetBoard = new Board();

        presetBoard.getIntersectionAt(3, 4).setStone(PlayerColor.BLACK);
        presetBoard.getIntersectionAt(2, 3).setStone(PlayerColor.BLACK);
        presetBoard.getIntersectionAt(2, 4).setStone(PlayerColor.WHITE);

        Game game = new Game(presetBoard);
        game.setTurnColor(PlayerColor.WHITE);

        assertThrows(PlacementViolationException.class, () -> game.placeStoneAt(3, 3));
    }

    //test south-east violation
    @Test
    void whenPlayerPlacesStoneAtPosition24AndSEDiagonalViolationShouldOutputError() {
        Board presetBoard = new Board();

        presetBoard.getIntersectionAt(2, 5).setStone(PlayerColor.BLACK);
        presetBoard.getIntersectionAt(3, 4).setStone(PlayerColor.BLACK);
        presetBoard.getIntersectionAt(3, 5).setStone(PlayerColor.WHITE);

        Game game = new Game(presetBoard);
        game.setTurnColor(PlayerColor.WHITE);

        assertThrows(PlacementViolationException.class, () -> game.placeStoneAt(2, 4));
    }
}
