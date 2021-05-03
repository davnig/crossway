import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameInitTests {

    @Test
    void whenGameStartsBoardShouldBeEmpty() {
        Game game = new Game();
        game.start();
        assertTrue(game.getBoard().getIntersections().stream()
                .allMatch(intersection -> intersection.state.equals(IntersectionState.EMPTY)));
    }

    @Test
    void whenGameStartsTurnShouldBeBlack() {
        Game game = new Game();
        game.start();
        assertTrue(game.isBlackTurn());
    }

}
