package it.units.crossway.client;

import it.units.crossway.client.exception.PlacementViolationException;
import it.units.crossway.client.model.Board;
import it.units.crossway.client.model.Intersection;
import it.units.crossway.client.model.PlayerColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

public class MatchTests {

    @Test
    void whenGameStartsBoardShouldBeEmpty() {
        Match match = new Match();
        match.getTurn().initFirstTurn();
        Assertions.assertTrue(match.getBoard().getBoardState().isEmpty());
    }

    @Test
    void whenGameStartsTurnShouldBeBlack() {
        Match match = new Match();
        match.getTurn().initFirstTurn();
        Assertions.assertEquals(PlayerColor.BLACK, match.getTurn().getCurrentPlayer());
    }

    @Test
    void whenStoneIsPositionedBoardShouldNotBeEmpty() throws PlacementViolationException {
        Match match = new Match();
        match.getTurn().initFirstTurn();
        match.validatePositionAndPlaceStone(1, 1);
        Assertions.assertFalse(match.getBoard().getBoardState().isEmpty());
        Assertions.assertEquals(match.getBoard().getBoardState().get(new Intersection(1, 1)), PlayerColor.BLACK);
    }

    @ParameterizedTest
    @EnumSource(value = PlayerColor.class, names = {"WHITE", "BLACK"})
    void whenPlayerCreatesLinearConnectedPathShouldWinTheMatch(PlayerColor playerColor) {
        Board presetBoard = new Board();
        for (int i = presetBoard.getFIRST_ROW(); i <= presetBoard.getLAST_ROW(); i++) {
            if (playerColor == PlayerColor.BLACK) {
                presetBoard.placeStone(new Intersection(i, 5), playerColor);
            } else {
                presetBoard.placeStone(new Intersection(5, i), playerColor);
            }
        }
        Match match = new Match(presetBoard);
        presetBoard.printBoard();
        Assertions.assertTrue(match.checkWinCondition(playerColor));
    }

    @ParameterizedTest
    @CsvSource({"2,7", "1,18", "1,15", "8,10", "2,19"})
    void whenWhitePlayerDoesNotHaveConnectedPathBetweenLeftAndRightThenWinConditionShouldFail(int columnStartPath, int columnEndPath) {
        Board presetBoard = new Board();
        for (int i = columnStartPath; i <= columnEndPath; i++) {
            presetBoard.placeStone(new Intersection(5, i), PlayerColor.WHITE);
        }
        Match match = new Match(presetBoard);
        presetBoard.printBoard();
        Assertions.assertFalse(match.checkWinCondition(PlayerColor.WHITE));
        Assertions.assertFalse(match.checkWinCondition(PlayerColor.BLACK));
    }

    @ParameterizedTest
    @CsvSource({"2,7", "1,18", "1,15", "8,10", "2,19"})
    void whenBlackPlayerDoesNotHaveConnectedPathBetweenTopAndBottomThenWinConditionShouldFail(int rowStartPath, int rowEndPath) {
        Board presetBoard = new Board();
        for (int i = rowStartPath; i <= rowEndPath; i++) {
            presetBoard.placeStone(new Intersection(i, 7), PlayerColor.BLACK);
        }
        Match match = new Match(presetBoard);
        presetBoard.printBoard();
        Assertions.assertFalse(match.checkWinCondition(PlayerColor.WHITE));
        Assertions.assertFalse(match.checkWinCondition(PlayerColor.BLACK));
    }

    @ParameterizedTest
    @EnumSource(value = PlayerColor.class, names = {"WHITE", "BLACK"})
    void whenPlayerCreatesDiagonalConnectedPathBetweenTopAndBottomShouldWinTheMatch(PlayerColor playerColor) {
        Board presetBoard = new Board();
        for (int i = presetBoard.getFIRST_ROW(); i <= presetBoard.getLAST_ROW(); i++) {
            presetBoard.placeStone(new Intersection(i, i), playerColor);
        }
        Match match = new Match(presetBoard);
        presetBoard.printBoard();
        Assertions.assertTrue(match.checkWinCondition(playerColor));
    }

    @Test
    void whenWhitePlayerCreatesVShapedConnectedPathBetweenLeftAndRightShouldWinTheMatch() {
        Board presetBoard = new Board();
        for (int i = presetBoard.getFIRST_ROW(); i <= presetBoard.getLAST_ROW() / 2; i++) {
            presetBoard.placeStone(new Intersection(i, i), PlayerColor.WHITE);
        }
        for (int i = 9; i <= 19; i++) {
            presetBoard.placeStone(new Intersection(9, i), PlayerColor.WHITE);
        }
        presetBoard.placeStone(new Intersection(10, 10), PlayerColor.WHITE);
        Match match = new Match(presetBoard);
        presetBoard.printBoard();
        Assertions.assertTrue(match.checkWinCondition(PlayerColor.WHITE));
    }

    @Test
    void whenWhitePlayerCreatesConnectedPathBetweenLeftAndRightShouldWinTheMatch() {
        Board presetBoard = new Board();
        // from 3,1 to 3,6
        for (int i = presetBoard.getFIRST_COLUMN(); i <= 6; i++) {
            presetBoard.placeStone(new Intersection(3, i), PlayerColor.WHITE);
        }
        // from 1,3 to 19,3
        for (int i = presetBoard.getFIRST_ROW(); i <= presetBoard.getLAST_ROW(); i++) {
            presetBoard.placeStone(new Intersection(i, 3), PlayerColor.WHITE);
        }
        // from 4,2 to 4,4
        for (int i = 2; i <= 4; i++) {
            presetBoard.placeStone(new Intersection(4, i), PlayerColor.WHITE);
        }
        // from 5,5 to 5,18
        for (int i = 5; i <= presetBoard.getLAST_COLUMN() - 1; i++) {
            presetBoard.placeStone(new Intersection(5, i), PlayerColor.WHITE);
        }
        // from 5,18 to 3,18
        for (int i = 5; i >= 3; i--) {
            presetBoard.placeStone(new Intersection(i, 18), PlayerColor.WHITE);
        }
        presetBoard.placeStone(new Intersection(2, 19), PlayerColor.WHITE);
        Match match = new Match(presetBoard);
        presetBoard.printBoard();
        Assertions.assertTrue(match.checkWinCondition(PlayerColor.WHITE));
    }

}
