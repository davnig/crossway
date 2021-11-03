package it.units.crossway.client;

import it.units.crossway.client.exception.PlacementViolationException;
import it.units.crossway.client.model.Board;
import it.units.crossway.client.model.Player;
import it.units.crossway.client.model.PlayerColor;
import it.units.crossway.client.model.StonePlacementIntent;
import it.units.crossway.client.remote.Api;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RulesTests {

    @Mock
    private Api api;


    //test south-west violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenSouthWestDiagonalViolationShouldThrowException(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.placeStone(1, 2, player1);
        presetBoard.placeStone(2, 3, player1);
        presetBoard.placeStone(2, 2, player2);
        StonePlacementIntent stonePlacementIntent = new StonePlacementIntent(1, 3, new Player("xxx", player2));
        assertThrows(PlacementViolationException.class, () -> Rules.validatePlacementIntent(presetBoard, stonePlacementIntent));
    }

    //test north-west violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenNorthWestDiagonalViolationShouldThrowException(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.placeStone(1, 4, player1);
        presetBoard.placeStone(2, 3, player1);
        presetBoard.placeStone(1, 3, player2);
        StonePlacementIntent stonePlacementIntent = new StonePlacementIntent(2, 4, new Player("xxx", player2));
        assertThrows(PlacementViolationException.class, () -> Rules.validatePlacementIntent(presetBoard, stonePlacementIntent));
    }

    //test north-east violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenNorthEstDiagonalViolationShouldThrowException(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.placeStone(3, 4, player1);
        presetBoard.placeStone(2, 3, player1);
        presetBoard.placeStone(2, 4, player2);
        StonePlacementIntent stonePlacementIntent = new StonePlacementIntent(3, 3, new Player("xxx", player2));
        assertThrows(PlacementViolationException.class, () -> Rules.validatePlacementIntent(presetBoard, stonePlacementIntent));
    }

    //test south-east violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenSouthEstDiagonalViolationShouldThrowException(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.placeStone(2, 5, player1);
        presetBoard.placeStone(3, 4, player1);
        presetBoard.placeStone(3, 5, player2);
        StonePlacementIntent stonePlacementIntent = new StonePlacementIntent(2, 4, new Player("xxx", player2));
        assertThrows(PlacementViolationException.class, () -> Rules.validatePlacementIntent(presetBoard, stonePlacementIntent));
    }

    @ParameterizedTest
    @EnumSource(value = PlayerColor.class, names = {"WHITE", "BLACK"})
    void whenPlayerCreatesLinearConnectedPathShouldWinTheMatch(PlayerColor playerColor) {
        Board presetBoard = new Board();
        Player player = new Player("nickname", playerColor);
        for (int i = Board.FIRST_ROW; i <= Board.LAST_ROW; i++) {
            if (playerColor == PlayerColor.BLACK) {
                presetBoard.placeStone(i, 5, playerColor);
            } else {
                presetBoard.placeStone(5, i, playerColor);
            }
        }
        IOUtils.printBoard(presetBoard, player);
        Assertions.assertTrue(Rules.checkWin(presetBoard, playerColor));
    }

    @ParameterizedTest
    @CsvSource({"2,7", "1,18", "1,15", "8,10", "2,19"})
    void whenWhitePlayerDoesNotHaveConnectedPathBetweenLeftAndRightThenWinConditionShouldFail(int columnStartPath, int columnEndPath) {
        Board presetBoard = new Board();
        Player player = new Player("nickname", PlayerColor.WHITE);
        for (int i = columnStartPath; i <= columnEndPath; i++) {
            presetBoard.placeStone(5, i, PlayerColor.WHITE);
        }
        IOUtils.printBoard(presetBoard, player);
        Assertions.assertFalse(Rules.checkWin(presetBoard, PlayerColor.WHITE));
        Assertions.assertFalse(Rules.checkWin(presetBoard, PlayerColor.BLACK));
    }

    @ParameterizedTest
    @CsvSource({"2,7", "1,18", "1,15", "8,10", "2,19"})
    void whenBlackPlayerDoesNotHaveConnectedPathBetweenTopAndBottomThenWinConditionShouldFail(int rowStartPath, int rowEndPath) {
        Board presetBoard = new Board();
        Player player = new Player("nickname", PlayerColor.BLACK);
        for (int i = rowStartPath; i <= rowEndPath; i++) {
            presetBoard.placeStone(i, 7, PlayerColor.BLACK);
        }
        IOUtils.printBoard(presetBoard, player);
        Assertions.assertFalse(Rules.checkWin(presetBoard, PlayerColor.WHITE));
        Assertions.assertFalse(Rules.checkWin(presetBoard, PlayerColor.BLACK));
    }

    @ParameterizedTest
    @EnumSource(value = PlayerColor.class, names = {"WHITE", "BLACK"})
    void whenPlayerCreatesDiagonalConnectedPathBetweenTopAndBottomShouldWinTheMatch(PlayerColor playerColor) {
        Board presetBoard = new Board();
        Player player = new Player("nickname", PlayerColor.WHITE);
        for (int i = Board.FIRST_ROW; i <= Board.LAST_ROW; i++) {
            presetBoard.placeStone(i, i, playerColor);
        }

        IOUtils.printBoard(presetBoard, player);
        Assertions.assertTrue(Rules.checkWin(presetBoard, playerColor));
    }

    @Test
    void whenWhitePlayerCreatesVShapedConnectedPathBetweenLeftAndRightShouldWinTheMatch() {
        Board presetBoard = new Board();
        Player player = new Player("nickname", PlayerColor.WHITE);
        for (int i = Board.FIRST_ROW; i <= Board.LAST_ROW / 2; i++) {
            presetBoard.placeStone(i, i, PlayerColor.WHITE);
        }
        for (int i = 9; i <= 19; i++) {
            presetBoard.placeStone(9, i, PlayerColor.WHITE);
        }
        presetBoard.placeStone(10, 10, PlayerColor.WHITE);
        IOUtils.printBoard(presetBoard, player);
        Assertions.assertTrue(Rules.checkWin(presetBoard, PlayerColor.WHITE));
    }

    @Test
    void whenWhitePlayerCreatesConnectedPathBetweenLeftAndRightShouldWinTheMatch() {
        Board presetBoard = new Board();
        Player player = new Player("nickname", PlayerColor.WHITE);
        // from 3,1 to 3,6
        for (int i = Board.FIRST_COLUMN; i <= 6; i++) {
            presetBoard.placeStone(3, i, PlayerColor.WHITE);
        }
        // from 1,3 to 19,3
        for (int i = Board.FIRST_ROW; i <= Board.LAST_ROW; i++) {
            presetBoard.placeStone(i, 3, PlayerColor.WHITE);
        }
        // from 4,2 to 4,4
        for (int i = 2; i <= 4; i++) {
            presetBoard.placeStone(4, i, PlayerColor.WHITE);
        }
        // from 5,5 to 5,18
        for (int i = 5; i <= Board.LAST_COLUMN - 1; i++) {
            presetBoard.placeStone(5, i, PlayerColor.WHITE);
        }
        // from 5,18 to 3,18
        for (int i = 5; i >= 3; i--) {
            presetBoard.placeStone(i, 18, PlayerColor.WHITE);
        }
        presetBoard.placeStone(2, 19, PlayerColor.WHITE);
        IOUtils.printBoard(presetBoard, player);
        Assertions.assertTrue(Rules.checkWin(presetBoard, PlayerColor.WHITE));
    }

    @ParameterizedTest
    @CsvSource({"-1, -1", "30, 30", "-1, 30", "30, -1"})
    void whenPlacementIsOutsideOfBoardShouldThrowException(int row, int column) {
        StonePlacementIntent stonePlacementIntent = new StonePlacementIntent(row, column, new Player("xxx", PlayerColor.WHITE));
        assertThrows(PlacementViolationException.class, () -> Rules.validatePlacementIntent(new Board(), stonePlacementIntent));
    }

    @Test
    void whenPlayerTriesToPlaceStoneInOccupiedIntersectionShouldThrowException() {
        int row = 1;
        int column = 1;
        Board board = new Board();
        board.placeStone(row, column, PlayerColor.WHITE);
        StonePlacementIntent stonePlacementIntent =
                new StonePlacementIntent(row, column, new Player("whiteP", PlayerColor.WHITE));
        assertThrows(PlacementViolationException.class, () ->
                Rules.validatePlacementIntent(board, stonePlacementIntent));
    }

}
