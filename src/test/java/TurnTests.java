import exception.PlacementViolationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

        Player playerONE = new Player(PlayerID.ONE, player1);
        Player playerTWO = new Player(PlayerID.TWO, player2);
        Turn turn = new Turn(4, playerTWO);

        Game game = new Game(presetBoard);
        game.setCurrentTurn(turn);

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

        Player playerONE = new Player(PlayerID.ONE, player1);
        Player playerTWO = new Player(PlayerID.TWO, player2);
        Turn turn = new Turn(4, playerTWO);

        Game game = new Game(presetBoard);
        game.setCurrentTurn(turn);

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

        Player playerONE = new Player(PlayerID.ONE, player1);
        Player playerTWO = new Player(PlayerID.TWO, player2);
        Turn turn = new Turn(4, playerTWO);

        Game game = new Game(presetBoard);
        game.setCurrentTurn(turn);

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

        Player playerONE = new Player(PlayerID.ONE, player1);
        Player playerTWO = new Player(PlayerID.TWO, player2);
        Turn turn = new Turn(4, playerTWO);

        Game game = new Game(presetBoard);
        game.setCurrentTurn(turn);

        assertThrows(PlacementViolationException.class, () -> game.placeStoneAt(2, 4));

    }
    /*
    @Test
    void whenIsSecondTurnGameShouldAskForPieRule() {
        Board presetBoard = new Board();
        presetBoard.getIntersectionAt(1, 4).setStone(PlayerColor.BLACK);

        Game game = new Game(presetBoard, 2);

        assertEquals("Pie Rule!", game.playTurn());
    }

    @Test
    void whenIsNotSecondTurnGameShouldNotAskForPieRule() {
        Board presetBoard = new Board();
        presetBoard.getIntersectionAt(1, 4).setStone(PlayerColor.BLACK);
        presetBoard.getIntersectionAt(2, 4).setStone(PlayerColor.WHITE);
        Game game = new Game(presetBoard, 3);

        assertEquals("It's just another turn.", game.playTurn());
    }*/
}
