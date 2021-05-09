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
        presetBoard.getIntersectionAt(1, 2).setStone(player1);
        presetBoard.getIntersectionAt(2, 3).setStone(player1);
        presetBoard.getIntersectionAt(2, 2).setStone(player2);

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
        presetBoard.getIntersectionAt(1, 4).setStone(player1);
        presetBoard.getIntersectionAt(2, 3).setStone(player1);
        presetBoard.getIntersectionAt(1, 3).setStone(player2);

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
        presetBoard.getIntersectionAt(3, 4).setStone(player1);
        presetBoard.getIntersectionAt(2, 3).setStone(player1);
        presetBoard.getIntersectionAt(2, 4).setStone(player2);

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

        presetBoard.getIntersectionAt(2, 5).setStone(player1);
        presetBoard.getIntersectionAt(3, 4).setStone(player1);
        presetBoard.getIntersectionAt(3, 5).setStone(player2);

        Player playerTWO = new Player(PlayerID.TWO, player2);
        Turn turn = new Turn(4, playerTWO);

        Game game = new Game(presetBoard, turn);

        assertThrows(PlacementViolationException.class, () -> game.placeStoneAt(2, 4));

    }

    @SneakyThrows
    @Test
    void whenIsSecondTurnAndPieRuleAcceptedPlayersShouldSwitchColors() {
        Board presetBoard = new Board();
        presetBoard.getIntersectionAt(1, 4).setStone(PlayerColor.BLACK);

        Game game = new Game(presetBoard);
        Turn turn = new Turn(2, game.getPlayer2());

        game.setTurn(turn);

        game.setScanner(getRedirectedScannerForSimulatedUserInput("2,2"));
        game.playTurn();

        assertEquals(game.getPlayer1().getColor(), PlayerColor.WHITE);
        assertEquals(game.getPlayer2().getColor(), PlayerColor.BLACK);
    }

    @Test
    void whenPlayTurnShouldConvertUserInputToStonePlacement() throws PlacementViolationException {

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

}
