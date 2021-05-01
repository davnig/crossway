import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TurnTests {

	@Test
	void isWhiteTurn() {
		Game game = new Game();
		assertEquals(game.getTurn(), PlayerColor.WHITE);
	}

	@Test
	void isIntersectionEmpty(){
		Intersection intersection = new Intersection(1,1,IntersectionState.EMPTY);
		assertEquals(intersection.getState(),IntersectionState.EMPTY);
	}

}
