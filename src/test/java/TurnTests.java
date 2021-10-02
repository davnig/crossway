import exception.InvalidUserInputException;
import exception.PlacementViolationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import playerProperty.PlayerColor;
import playerProperty.PlayerID;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TurnTests {

    //test south-west violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenPlayerPlacesStoneAtPosition13AndSWDiagonalViolationShouldOutputError(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.getBoardState().put(new Intersection(1, 2), player1);
        presetBoard.getBoardState().put(new Intersection(2, 3), player1);
        presetBoard.getBoardState().put(new Intersection(2, 2), player2);
        Player playerTWO = new Player(PlayerID.TWO, player2);
        Turn turn = new Turn(4, playerTWO);
        Match match = new Match(presetBoard, turn);
        assertThrows(PlacementViolationException.class, () -> match.placeStoneAt(1, 3));
    }

    //test north-west violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenPlayerPlacesStoneAtPosition24AndNWDiagonalViolationShouldOutputError(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.getBoardState().put(new Intersection(1, 4), player1);
        presetBoard.getBoardState().put(new Intersection(2, 3), player1);
        presetBoard.getBoardState().put(new Intersection(1, 3), player2);
        Player playerTWO = new Player(PlayerID.TWO, player2);
        Turn turn = new Turn(4, playerTWO);
        Match match = new Match(presetBoard, turn);
        assertThrows(PlacementViolationException.class, () -> match.placeStoneAt(2, 4));
    }

    //test north-east violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenPlayerPlacesStoneAtPosition33AndNEDiagonalViolationShouldOutputError(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.getBoardState().put(new Intersection(3, 4), player1);
        presetBoard.getBoardState().put(new Intersection(2, 3), player1);
        presetBoard.getBoardState().put(new Intersection(2, 4), player2);
        Player playerTWO = new Player(PlayerID.TWO, player2);
        Turn turn = new Turn(4, playerTWO);
        Match match = new Match(presetBoard, turn);
        assertThrows(PlacementViolationException.class, () -> match.placeStoneAt(3, 3));
    }

    //test south-east violation
    @ParameterizedTest
    @CsvSource({"BLACK,WHITE", "WHITE,BLACK"})
    void whenPlayerPlacesStoneAtPosition24AndSEDiagonalViolationShouldOutputError(PlayerColor player1, PlayerColor player2) {
        Board presetBoard = new Board();
        presetBoard.getBoardState().put(new Intersection(2, 5), player1);
        presetBoard.getBoardState().put(new Intersection(3, 4), player1);
        presetBoard.getBoardState().put(new Intersection(3, 5), player2);
        Player playerTWO = new Player(PlayerID.TWO, player2);
        Turn turn = new Turn(4, playerTWO);
        Match match = new Match(presetBoard, turn);
        assertThrows(PlacementViolationException.class, () -> match.placeStoneAt(2, 4));
    }

    @SneakyThrows
    @Test
    void whenIsSecondTurnAndPieRuleAcceptedPlayersShouldSwitchColors() {
        Board presetBoard = new Board();
        presetBoard.getBoardState().put(new Intersection(1, 4), PlayerColor.BLACK);
        Match match = new Match(presetBoard);
        Turn turn = new Turn(2, match.getPlayer2());
        match.setTurn(turn);
        match.setScanner(getRedirectedScannerForSimulatedUserInput("Y"));
        match.playTurn();
        assertEquals(match.getPlayer1().getColor(), PlayerColor.WHITE);
        assertEquals(match.getPlayer2().getColor(), PlayerColor.BLACK);
    }

    @SneakyThrows
    @Test
    void whenIsSecondTurnAndPieRuleNOTAcceptedPlayersShouldNOTSwitchColors() {
        Board presetBoard = new Board();
        presetBoard.getBoardState().put(new Intersection(1, 4), PlayerColor.BLACK);
        Match match = new Match(presetBoard);
        Turn turn = new Turn(2, match.getPlayer2());
        match.setTurn(turn);
        match.setScanner(getRedirectedScannerForSimulatedUserInput("N" + System.getProperty("line.separator")
                + "6,6" + System.getProperty("line.separator")));
        match.playTurn();
        assertEquals(PlayerColor.BLACK, match.getPlayer1().getColor());
        assertEquals(PlayerColor.WHITE, match.getPlayer2().getColor());
    }

    @SneakyThrows
    @Test
    void whenIsSecondTurnAndPieRuleNOTAcceptedWhiteShouldMakeItsMove() {
        Board presetBoard = new Board();
        presetBoard.getBoardState().put(new Intersection(1, 4), PlayerColor.BLACK);
        Match match = new Match(presetBoard);
        Turn turn = new Turn(2, match.getPlayer2());
        match.setTurn(turn);
        match.setScanner(getRedirectedScannerForSimulatedUserInput("N" + System.getProperty("line.separator")
                + "6,6" + System.getProperty("line.separator")));
        match.playTurn();
        assertEquals(PlayerColor.WHITE, presetBoard.getStoneColorAt(6, 6));
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"yes,no,asd,12-.,-,.-.-.,213,wedfokn"})
    void whenIsSecondTurnAndPlayerInsertWrongInputForPieRulePlayTurnShouldThrowError(String playerResponseToPieRule) {
        Board presetBoard = new Board();
        presetBoard.getBoardState().put(new Intersection(1, 4), PlayerColor.BLACK);
        Match match = new Match(presetBoard);
        Turn turn = new Turn(2, match.getPlayer2());
        match.setTurn(turn);
        match.setScanner(getRedirectedScannerForSimulatedUserInput(playerResponseToPieRule));
        assertThrows(InvalidUserInputException.class, match::playTurn);
    }

    @Test
    void whenTurnEndsShouldSwitchCurrentPlayer() throws PlacementViolationException, InvalidUserInputException {
        Match match = new Match();
        match.start();
        match.setScanner(getRedirectedScannerForSimulatedUserInput("2,1"));
        match.playTurn();
        assertEquals(match.getCurrentPlayerColor(), PlayerColor.WHITE);
    }

    @Test
    void whenTurnEndsShouldIncrementTurnNumber() throws PlacementViolationException, InvalidUserInputException {
        Match match = new Match();
        match.start();
        match.setScanner(getRedirectedScannerForSimulatedUserInput("1,1"));
        match.playTurn();
        assertEquals(match.getTurn().getTurnNumber(), 2);
    }

    @Test
    void whenPlayTurnShouldConvertUserInputToStonePlacement() throws PlacementViolationException, InvalidUserInputException {
        String userInput;
        ByteArrayInputStream byteArrayInputStream;
        Match match = new Match();
        match.start();
        match.setScanner(getRedirectedScannerForSimulatedUserInput("2,3"));
        match.playTurn();
        assertEquals(match.getBoard().getStoneColorAt(2, 3), PlayerColor.BLACK);
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
        assertThrows(PlacementViolationException.class, () -> match.placeStoneAt(row, column));
    }
}
