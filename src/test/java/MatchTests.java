import exception.PlacementViolationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import playerProperty.PlayerColor;

import static org.junit.jupiter.api.Assertions.*;

public class MatchTests {

    @Test
    void whenGameStartsBoardShouldBeEmpty() {
        Match match = new Match();
        match.start();
        assertTrue(match.getBoard().getBoardState().isEmpty());
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
        assertFalse(match.getBoard().getBoardState().isEmpty());
        assertEquals(match.getBoard().getBoardState().get(new Intersection(1, 1)), PlayerColor.BLACK);
    }

    @SneakyThrows
    @Test
    void whenWhitePlayerCreatesConnectedPathBetweenLeftAndRightShouldWinTheMatch() {
        Board presetBoard = new Board();
        for (int i = presetBoard.getFIRST_COLUMN(); i <= presetBoard.getLAST_COLUMN(); i++) {
            presetBoard.placeStone(new Intersection(5, i), PlayerColor.WHITE);
        }
        Match match = new Match(presetBoard);
        assertTrue(match.checkWinCondition(PlayerColor.WHITE));
        assertFalse(match.checkWinCondition(PlayerColor.BLACK));
    }

    @Test
    void whenBlackPlayerCreatesConnectedPathBetweenTopAndBottomShouldWinTheMatch() {
        Board presetBoard = new Board();
        for (int i = presetBoard.getFIRST_ROW(); i <= presetBoard.getLAST_ROW(); i++) {
            presetBoard.placeStone(new Intersection(i, 5), PlayerColor.BLACK);
        }
        Match match = new Match(presetBoard);
        assertTrue(match.checkWinCondition(PlayerColor.BLACK));
        assertFalse(match.checkWinCondition(PlayerColor.WHITE));
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"2,7", "1,18", "1,15", "8,10", "2,19"})
    void whenWhitePlayerDoesNotHaveConnectedPathBetweenLeftAndRightThenWinConditionShouldFail(int columnStartPath, int columnEndPath) {
        Board presetBoard = new Board();
        for (int i = columnStartPath; i <= columnEndPath; i++) {
            presetBoard.placeStone(new Intersection(5, i), PlayerColor.WHITE);
        }
        Match match = new Match(presetBoard);
        assertFalse(match.checkWinCondition(PlayerColor.WHITE));
        assertFalse(match.checkWinCondition(PlayerColor.BLACK));
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"2,7", "1,18", "1,15", "8,10", "2,19"})
    void whenBlackPlayerDoesNotHaveConnectedPathBetweenLeftAndRightThenWinConditionShouldFail(int rowStartPath, int rowEndPath) {
        Board presetBoard = new Board();
        for (int i = rowStartPath; i <= rowEndPath; i++) {
            presetBoard.placeStone(new Intersection(i, 7), PlayerColor.BLACK);
        }
        Match match = new Match(presetBoard);
        assertFalse(match.checkWinCondition(PlayerColor.WHITE));
        assertFalse(match.checkWinCondition(PlayerColor.BLACK));
    }


}
