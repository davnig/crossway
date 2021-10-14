package it.units.crossway;

import it.units.crossway.exception.InvalidUserInputException;
import it.units.crossway.exception.PlacementViolationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TurnTests {

    //test south-west violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenPlayerPlacesStoneAtPosition13AndSWDiagonalViolationShouldOutputError(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(1, 2), player1);
        presetBoard.placeStone(new Intersection(2, 3), player1);
        presetBoard.placeStone(new Intersection(2, 2), player2);
        Turn turn = new Turn(4, player2);
        Match match = new Match(presetBoard, turn);
        assertThrows(PlacementViolationException.class, () -> match.validatePositionAndPlaceStone(1, 3));
    }

    //test north-west violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenPlayerPlacesStoneAtPosition24AndNWDiagonalViolationShouldOutputError(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(1, 4), player1);
        presetBoard.placeStone(new Intersection(2, 3), player1);
        presetBoard.placeStone(new Intersection(1, 3), player2);
        Turn turn = new Turn(4, player2);
        Match match = new Match(presetBoard, turn);
        assertThrows(PlacementViolationException.class, () -> match.validatePositionAndPlaceStone(2, 4));
    }

    //test north-east violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenPlayerPlacesStoneAtPosition33AndNEDiagonalViolationShouldOutputError(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(3, 4), player1);
        presetBoard.placeStone(new Intersection(2, 3), player1);
        presetBoard.placeStone(new Intersection(2, 4), player2);
        Turn turn = new Turn(4, player2);
        Match match = new Match(presetBoard, turn);
        assertThrows(PlacementViolationException.class, () -> match.validatePositionAndPlaceStone(3, 3));
    }

    //test south-east violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenPlayerPlacesStoneAtPosition24AndSEDiagonalViolationShouldOutputError(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(2, 5), player1);
        presetBoard.placeStone(new Intersection(3, 4), player1);
        presetBoard.placeStone(new Intersection(3, 5), player2);
        Turn turn = new Turn(4, player2);
        Match match = new Match(presetBoard, turn);
        assertThrows(PlacementViolationException.class, () -> match.validatePositionAndPlaceStone(2, 4));
    }

    @SneakyThrows
    @Test
    void whenIsSecondTurnAndPieRuleAcceptedPlayersShouldSwitchColors() {
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(1, 4), PlayerColor.BLACK);
        Match match = new Match(presetBoard);
        match.getTurn().initFirstTurn();
        match.getTurn().nextTurn();
        IOUtils.scanner = getRedirectedScannerForSimulatedUserInput("Y");
        match.playTurn();
        Assertions.assertEquals(match.getTurn().getCurrentPlayer(), PlayerColor.WHITE);
    }

    @SneakyThrows
    @Test
    void whenIsSecondTurnAndPieRuleNotAcceptedPlayersShouldNotSwitchColors() {
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(1, 4), PlayerColor.BLACK);
        Match match = new Match(presetBoard);
        match.getTurn().initFirstTurn();
        match.getTurn().nextTurn();
        IOUtils.scanner =
                getRedirectedScannerForSimulatedUserInput(
                        "N" + System.getProperty("line.separator") + "6,6" + System.getProperty("line.separator")
        );
        match.playTurn();
        Assertions.assertEquals(match.getTurn().getCurrentPlayer(), PlayerColor.BLACK);
    }

    @SneakyThrows
    @Test
    void whenIsSecondTurnAndPieRuleNotAcceptedWhiteShouldMakeItsMove() {
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(1, 4), PlayerColor.BLACK);
        Match match = new Match(presetBoard);
        match.getTurn().initFirstTurn();
        match.getTurn().nextTurn();
        IOUtils.scanner =
                getRedirectedScannerForSimulatedUserInput(
                        "N" + System.getProperty("line.separator") + "6,6" + System.getProperty("line.separator")
                );
        match.playTurn();
        Assertions.assertEquals(presetBoard.getStoneColorAt(new Intersection(6, 6)), PlayerColor.WHITE);
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"yes,no,asd,12-.,-,.-.-.,213,wedfokn"})
    void whenIsSecondTurnAndPlayerInsertWrongInputForPieRuleThenError(String playerResponseToPieRule) {
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(1, 4), PlayerColor.BLACK);
        Match match = new Match(presetBoard);
        match.getTurn().initFirstTurn();
        match.getTurn().nextTurn();
        IOUtils.scanner = getRedirectedScannerForSimulatedUserInput(playerResponseToPieRule);
        assertThrows(InvalidUserInputException.class, match::playTurn);
    }

    @Test
    void whenTurnEndsShouldSwitchCurrentPlayer() throws PlacementViolationException, InvalidUserInputException {
        Match match = new Match();
        match.getTurn().initFirstTurn();
        IOUtils.scanner = getRedirectedScannerForSimulatedUserInput("2,1");
        match.playTurn();
        Assertions.assertEquals(PlayerColor.WHITE, match.getTurn().getCurrentPlayer());
    }

    @Test
    void whenTurnEndsShouldIncrementTurnNumber() throws PlacementViolationException, InvalidUserInputException {
        Match match = new Match();
        match.getTurn().initFirstTurn();
        IOUtils.scanner = getRedirectedScannerForSimulatedUserInput("1,1");
        match.playTurn();
        Assertions.assertEquals(match.getTurn().getTurnNumber(), 2);
    }

    @Test
    void whenPlayTurnShouldConvertUserInputToStonePlacement() throws PlacementViolationException, InvalidUserInputException {
        Match match = new Match();
        match.getTurn().initFirstTurn();
        IOUtils.scanner = getRedirectedScannerForSimulatedUserInput("2,3");
        match.playTurn();
        Assertions.assertEquals(match.getBoard().getStoneColorAt(new Intersection(2, 3)), PlayerColor.BLACK);
    }

    Scanner getRedirectedScannerForSimulatedUserInput(String input) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(byteArrayInputStream);
        return new Scanner(System.in);
    }

    @ParameterizedTest
    @CsvSource({"-1, -1", "30, 30", "-1, 30", "30, -1"})
    void whenPlayerPlacesStoneOutsideOfBoardShouldOutputError(int row, int column) {
        Board board = new Board();
        Match match = new Match(board);
        assertThrows(PlacementViolationException.class, () -> match.validatePositionAndPlaceStone(row, column));
    }
}
