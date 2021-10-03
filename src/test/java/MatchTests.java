import exception.PlacementViolationException;
import org.junit.jupiter.api.Test;
import playerProperty.PlayerColor;

import static org.junit.jupiter.api.Assertions.*;

public class MatchTests {

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
        assertEquals(PlayerColor.BLACK, match.getTurn().getCurrentPlayer());
    }

    @Test
    void whenStoneIsPositionedBoardShouldNotBeEmpty() throws PlacementViolationException {
        Match match = new Match();
        match.start();
        match.validatePositionAndPlaceStone(1, 1);
        assertEquals(match.getBoard().getBoardState().get(new Intersection(1, 1)), PlayerColor.BLACK);
    }

    /*
    @Test
    void whenBlackPlayerReachOtherSideOfTheBoardShouldWinTheMatch() {
        Match match = new Match();
        match.start();
        assertTrue(match.checkWinCondition(PlayerColor.BLACK));
    }*/

    @SneakyThrows
    @Test
    void whenWhitePlayerCreatesHorizontalConnectedPathAcrossBoardShouldWinTheMatch() {
        Board presetBoard = new Board();
        for (int i = presetBoard.getFIRST_COLUMN(); i <= presetBoard.getLAST_COLUMN(); i++) {
            presetBoard.placeStone(5, i, PlayerColor.WHITE);
        }
        Match match = new Match(presetBoard);
        assertTrue(match.checkWinCondition(PlayerColor.WHITE));
    }

}
