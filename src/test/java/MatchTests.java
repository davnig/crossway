import exception.PlacementViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
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

    @ParameterizedTest
    @EnumSource(PlayerColor.class)
    void whenPlayerCreatesLinearConnectedPathBetweenTopAndBottomShouldWinTheMatch(PlayerColor playerColor) {
        Board presetBoard = new Board();
        for (int i = presetBoard.getFIRST_ROW(); i <= presetBoard.getLAST_ROW(); i++) {
            if (playerColor == PlayerColor.BLACK) {
                presetBoard.placeStone(new Intersection(i, 5), playerColor);
            } else {
                presetBoard.placeStone(new Intersection(5, i), playerColor);
            }
        }
        Match match = new Match(presetBoard);
        assertTrue(match.checkWinCondition(playerColor));
    }

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

    @ParameterizedTest
    @CsvSource({"2,7", "1,18", "1,15", "8,10", "2,19"})
    void whenBlackPlayerDoesNotHaveConnectedPathBetweenTopAndBottomThenWinConditionShouldFail(int rowStartPath, int rowEndPath) {
        Board presetBoard = new Board();
        for (int i = rowStartPath; i <= rowEndPath; i++) {
            presetBoard.placeStone(new Intersection(i, 7), PlayerColor.BLACK);
        }
        Match match = new Match(presetBoard);
        assertFalse(match.checkWinCondition(PlayerColor.WHITE));
        assertFalse(match.checkWinCondition(PlayerColor.BLACK));
    }

    @ParameterizedTest
    @EnumSource(value = PlayerColor.class, names = {"WHITE", "BLACK"})
    void whenPlayerCreatesDiagonalConnectedPathBetweenTopAndBottomShouldWinTheMatch(PlayerColor playerColor) {
        Board presetBoard = new Board();
        for (int i = presetBoard.getFIRST_ROW(); i <= presetBoard.getLAST_ROW(); i++) {
            presetBoard.placeStone(new Intersection(i, i), playerColor);
        }
        Match match = new Match(presetBoard);
        assertTrue(match.checkWinCondition(playerColor));
    }

}
