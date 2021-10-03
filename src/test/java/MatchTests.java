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


    @Test
    void whenBlackPlayerCreatesFullVerticalConnectedPathAcrossBoardShouldWinTheMatch() {
        Board presetBoard = new Board();
        for (int i = presetBoard.getFIRST_ROW(); i <= presetBoard.getLAST_ROW(); i++) {
            presetBoard.placeStone(i, 5, PlayerColor.BLACK);
        }
        Match match = new Match(presetBoard);
        match.start();
        assertTrue(match.checkWinCondition(PlayerColor.BLACK));
        assertFalse(match.checkWinCondition(PlayerColor.WHITE));
    }

    @SneakyThrows
    @Test
    void whenWhitePlayerCreatesFullHorizontalConnectedPathAcrossBoardShouldWinTheMatch() {
        Board presetBoard = new Board();
        for (int i = presetBoard.getFIRST_COLUMN(); i <= presetBoard.getLAST_COLUMN(); i++) {
            presetBoard.placeStone(5, i, PlayerColor.WHITE);
        }
        Match match = new Match(presetBoard);
        match.start();
        match.getTurn().nextTurn();
        assertTrue(match.checkWinCondition(PlayerColor.WHITE));
        assertFalse(match.checkWinCondition(PlayerColor.BLACK));
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"7", "18", "15", "10", "9"})
    void whenWhitePlayerDoesNotHaveFullConnectedPathFromLeftToRightShouldFailWinCondition(int columnEndPath) {
        int columnStartPath = 1;
        Board presetBoard = new Board();
        for (int i = columnStartPath; i <= columnEndPath; i++) {
            presetBoard.placeStone(5, i, PlayerColor.WHITE);
        }
        Match match = new Match(presetBoard);
        assertFalse(match.checkWinCondition(PlayerColor.WHITE));
    }

}
