import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TurnTests {

	@Test
	void isWhiteTurn() {
		Game game = new Game();
		assertEquals(game.getTurn(), Player.WHITE);
	}

}
