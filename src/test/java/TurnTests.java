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

	@Test
	void whenPlayerPlacesStoneAndDiagonalViolationShouldOutputError() {
		Board presetBoard = new Board();

		presetBoard.getIntersectionAt(1, 2).setStone(PlayerColor.BLACK);
		presetBoard.getIntersectionAt(2, 3).setStone(PlayerColor.BLACK);
		presetBoard.getIntersectionAt(2, 2).setStone(PlayerColor.WHITE);

		Game game = new Game(presetBoard);
		game.setTurnColor(PlayerColor.WHITE);

		assertThrows(PlacementViolationException.class, () -> game.placeStoneAt(1, 3));
	}

}
