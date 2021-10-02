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

        Game game = new Game(presetBoard, turn);

        assertThrows(PlacementViolationException.class, () -> game.placeStoneAt(1, 3));

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

        Game game = new Game(presetBoard, turn);

        assertThrows(PlacementViolationException.class, () -> game.placeStoneAt(2, 4));

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

        Game game = new Game(presetBoard, turn);

        assertThrows(PlacementViolationException.class, () -> game.placeStoneAt(3, 3));

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

        Game game = new Game(presetBoard, turn);

        assertThrows(PlacementViolationException.class, () -> game.placeStoneAt(2, 4));

    }

    @SneakyThrows
    @Test
    void whenIsSecondTurnAndPieRuleAcceptedPlayersShouldSwitchColors() {
        Board presetBoard = new Board();
        presetBoard.getBoardState().put(new Intersection(1, 4), PlayerColor.BLACK);

        Game game = new Game(presetBoard);
        Turn turn = new Turn(2, game.getPlayer2());

        game.setTurn(turn);

        game.setScanner(getRedirectedScannerForSimulatedUserInput("Y"));
        game.playTurn();

        assertEquals(game.getPlayer1().getColor(), PlayerColor.WHITE);
        assertEquals(game.getPlayer2().getColor(), PlayerColor.BLACK);
    }

    @SneakyThrows
    @Test
    void whenIsSecondTurnAndPieRuleNOTAcceptedPlayersShouldNOTSwitchColors() {
        Board presetBoard = new Board();
        presetBoard.getBoardState().put(new Intersection(1, 4), PlayerColor.BLACK);

        Game game = new Game(presetBoard);
        Turn turn = new Turn(2, game.getPlayer2());

        game.setTurn(turn);

        game.setScanner(getRedirectedScannerForSimulatedUserInput("N" + System.getProperty("line.separator")
                + "6,6" + System.getProperty("line.separator")));
        game.playTurn();

        assertEquals(PlayerColor.BLACK, game.getPlayer1().getColor());
        assertEquals(PlayerColor.WHITE, game.getPlayer2().getColor());
    }

    @SneakyThrows
    @Test
    void whenIsSecondTurnAndPieRuleNOTAcceptedWhiteShouldMakeItsMove() {
        Board presetBoard = new Board();
        presetBoard.getBoardState().put(new Intersection(1, 4), PlayerColor.BLACK);

        Game game = new Game(presetBoard);
        Turn turn = new Turn(2, game.getPlayer2());

        game.setTurn(turn);

        game.setScanner(getRedirectedScannerForSimulatedUserInput("N" + System.getProperty("line.separator")
                + "6,6" + System.getProperty("line.separator")));
        game.playTurn();

        assertEquals(PlayerColor.WHITE, presetBoard.getStoneColorAt(6, 6));
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"yes,no,asd,12-.,-,.-.-.,213,wedfokn"})
    void whenIsSecondTurnAndPlayerInsertWrongInputForPieRulePlayTurnShouldThrowError(String playerResponseToPieRule) {
        Board presetBoard = new Board();
        presetBoard.getBoardState().put(new Intersection(1, 4), PlayerColor.BLACK);

        Game game = new Game(presetBoard);
        Turn turn = new Turn(2, game.getPlayer2());

        game.setTurn(turn);
        game.setScanner(getRedirectedScannerForSimulatedUserInput(playerResponseToPieRule));

        assertThrows(InvalidUserInputException.class, game::playTurn);

    }

    @Test
    void whenTurnEndsShouldSwitchCurrentPlayer() throws PlacementViolationException, InvalidUserInputException {

        Game game = new Game();
        game.start();
        game.setScanner(getRedirectedScannerForSimulatedUserInput("2,1"));
        game.playTurn();

        assertEquals(game.getCurrentPlayerColor(), PlayerColor.WHITE);
    }

    @Test
    void whenTurnEndsShouldIncrementTurnNumber() throws PlacementViolationException, InvalidUserInputException {

        Game game = new Game();
        game.start();
        game.setScanner(getRedirectedScannerForSimulatedUserInput("1,1"));
        game.playTurn();

        assertEquals(game.getTurn().getTurnNumber(), 2);

    }

    @Test
    void whenPlayTurnShouldConvertUserInputToStonePlacement() throws PlacementViolationException, InvalidUserInputException {

        String userInput;
        ByteArrayInputStream byteArrayInputStream;

        Game game = new Game();
        game.start();

        game.setScanner(getRedirectedScannerForSimulatedUserInput("2,3"));
        game.playTurn();

        assertEquals(game.getBoard().getStoneColorAt(2, 3), PlayerColor.BLACK);

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
        Game game = new Game(board);

        assertThrows(PlacementViolationException.class, () -> game.placeStoneAt(row, column));
    }
}
