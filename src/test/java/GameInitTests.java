import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameInitTests {

    @Test
    void whenGameStartsBoardShouldBeEmpty() {
        Board board = new Board();
        assertTrue(board.isEmpty());
    }

    @Test
    void whenGameStartsTurnShouldBeBlack() {
        Game game = new Game();
        assertTrue(game.isBlackTurn());
    }
}
