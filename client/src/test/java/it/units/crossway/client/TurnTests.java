package it.units.crossway.client;

import it.units.crossway.client.exception.PlacementViolationException;
import it.units.crossway.client.model.*;
import it.units.crossway.client.remote.Api;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TurnTests {

    @Mock
    private Api api;

    //test south-west violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenSouthWestDiagonalViolationShouldThrowException(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(1, 2), player1);
        presetBoard.placeStone(new Intersection(2, 3), player1);
        presetBoard.placeStone(new Intersection(2, 2), player2);
        StonePlacementIntent stonePlacementIntent = new StonePlacementIntent(1, 3, new Player("xxx", player2));
        assertThrows(PlacementViolationException.class, () -> Rules.validatePlacementIntent(presetBoard, stonePlacementIntent));
    }

    //test north-west violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenNorthWestDiagonalViolationShouldThrowException(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(1, 4), player1);
        presetBoard.placeStone(new Intersection(2, 3), player1);
        presetBoard.placeStone(new Intersection(1, 3), player2);
        StonePlacementIntent stonePlacementIntent = new StonePlacementIntent(2, 4, new Player("xxx", player2));
        assertThrows(PlacementViolationException.class, () -> Rules.validatePlacementIntent(presetBoard, stonePlacementIntent));
    }

    //test north-east violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenNorthEstDiagonalViolationShouldThrowException(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(3, 4), player1);
        presetBoard.placeStone(new Intersection(2, 3), player1);
        presetBoard.placeStone(new Intersection(2, 4), player2);
        StonePlacementIntent stonePlacementIntent = new StonePlacementIntent(3, 3, new Player("xxx", player2));
        assertThrows(PlacementViolationException.class, () -> Rules.validatePlacementIntent(presetBoard, stonePlacementIntent));
    }

    //test south-east violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenSouthEstDiagonalViolationShouldThrowException(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(2, 5), player1);
        presetBoard.placeStone(new Intersection(3, 4), player1);
        presetBoard.placeStone(new Intersection(3, 5), player2);
        StonePlacementIntent stonePlacementIntent = new StonePlacementIntent(2, 4, new Player("xxx", player2));
        assertThrows(PlacementViolationException.class, () -> Rules.validatePlacementIntent(presetBoard, stonePlacementIntent));
    }

    @Test
    void whenIsSecondTurnAndPieRuleAcceptedPlayersShouldSwitchColors() {
        Player player = new Player();
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(1, 4), PlayerColor.BLACK);
        Turn turn = new Turn(2, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, presetBoard, turn, api);
        gameHandler.startGameAtGivenState(presetBoard, turn);
        IOUtils.scanner = getRedirectedScannerForSimulatedUserInput("Y");
        gameHandler.playTurn();
        assertEquals(PlayerColor.WHITE, gameHandler.getTurn().getCurrentPlayer());
    }

    @Test
    void whenIsSecondTurnAndPieRuleNotAcceptedPlayersShouldNotSwitchColors() {
        Player player = new Player();
        Board presetBoard = new Board();
        presetBoard.placeStone(new Intersection(1, 4), PlayerColor.BLACK);
        Turn turn = new Turn(2, PlayerColor.WHITE);
        GameHandler gameHandler = new GameHandler(player, presetBoard, turn, api);
        gameHandler.startGameAtGivenState(presetBoard, turn);
        IOUtils.scanner =
                getRedirectedScannerForSimulatedUserInput(
                        "N" + System.getProperty("line.separator") + "6,6" + System.getProperty("line.separator")
                );
        gameHandler.playTurn();
        assertEquals(PlayerColor.BLACK, gameHandler.getTurn().getCurrentPlayer());
    }

    @Test
    void whenIsSecondTurnAndPieRuleNotAcceptedWhiteShouldMakeItsMove() {
        Board presetBoard = new Board();
        Player whitePlayer = new Player("xxx", PlayerColor.WHITE);
        Turn turn = new Turn(2, PlayerColor.WHITE);
        presetBoard.placeStone(new Intersection(1, 4), PlayerColor.BLACK);
        GameHandler gameHandler = new GameHandler(whitePlayer, presetBoard, turn, api);
        gameHandler.startGameAtGivenState(presetBoard, turn, whitePlayer);
        IOUtils.scanner =
                getRedirectedScannerForSimulatedUserInput(
                        "N" + System.getProperty("line.separator") + "6,6" + System.getProperty("line.separator")
                );
        gameHandler.playTurn();
        assertEquals(PlayerColor.WHITE, presetBoard.getStoneColorAt(new Intersection(6, 6)));
    }

    @Test
    void whenTurnEndsShouldSwitchCurrentPlayer() {
        Player player = new Player();
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        gameHandler.getTurn().initFirstTurn();
        IOUtils.scanner = getRedirectedScannerForSimulatedUserInput("2,1");
        gameHandler.playTurn();
        assertEquals(PlayerColor.WHITE, gameHandler.getTurn().getCurrentPlayer());
    }

    @Test
    void whenTurnEndsShouldIncrementTurnNumber() {
        Player player = new Player();
        Board board = new Board();
        Turn turn = new Turn();
        GameHandler gameHandler = new GameHandler(player, board, turn, api);
        gameHandler.getTurn().initFirstTurn();
        IOUtils.scanner = getRedirectedScannerForSimulatedUserInput("1,1");
        gameHandler.playTurn();
        assertEquals(2, gameHandler.getTurn().getTurnNumber());
    }

    @Test
    void whenPlayTurnShouldConvertUserInputToStonePlacement() {
        Board board = new Board();
        Player blackPlayer = new Player("xxx", PlayerColor.BLACK);
        Turn turn = new Turn(1, PlayerColor.BLACK);
        GameHandler gameHandler = new GameHandler(blackPlayer, board, turn, api);
        gameHandler.startGameAtGivenState(board, turn, blackPlayer);
        IOUtils.scanner = getRedirectedScannerForSimulatedUserInput("2,3");
        gameHandler.playTurn();
        assertEquals(PlayerColor.BLACK, gameHandler.getBoard().getStoneColorAt(new Intersection(2, 3)));
    }

    @ParameterizedTest
    @CsvSource({"-1, -1", "30, 30", "-1, 30", "30, -1"})
    void whenPlacementIsOutsideOfBoardShouldThrowException(int row, int column) {
        StonePlacementIntent stonePlacementIntent = new StonePlacementIntent(row, column, new Player("xxx", PlayerColor.WHITE));
        assertThrows(PlacementViolationException.class, () -> Rules.validatePlacementIntent(new Board(), stonePlacementIntent));
    }

    Scanner getRedirectedScannerForSimulatedUserInput(String input) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(byteArrayInputStream);
        return new Scanner(System.in);
    }
}
