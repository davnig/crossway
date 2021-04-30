import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameInitTests {

    @Test
    void isBoardEmpty() {
        Board board = new Board();
        assertTrue(board.isEmpty());
    }

}
