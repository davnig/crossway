package it.units.crossway.client;

import it.units.crossway.client.model.Board;
import it.units.crossway.client.model.Player;
import it.units.crossway.client.model.PlayerColor;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputOutputTests {

    @Test
    void givenSystemInRedirectWhenScannerNextShouldReadPreMadeString() {
        String data = "Hello, World!";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Scanner scanner = new Scanner(System.in);
        assertEquals(scanner.nextLine(), data);
    }

    @Test
    void print2x2EmptyBoard() {
        String board2x2 =
                System.lineSeparator() +
                        "x --> WHITE stones" + System.lineSeparator() +
                        "O --> BLACK stones (you)" + System.lineSeparator() +
                        System.lineSeparator() +
                        "   --------- " + System.lineSeparator() +
                        "1  |   |   | " + System.lineSeparator() +
                        "2  |   |   | " + System.lineSeparator() +
                        "   --------- " + System.lineSeparator() +
                        "     1   2   " + System.lineSeparator();

        ByteArrayOutputStream baos = IOUtils.redirectSystemOutToByteArrayOS();
        Player player = new Player("blackP", PlayerColor.BLACK);
        Board board = new Board();
        Board.LAST_COLUMN = 2;
        Board.LAST_ROW = 2;
        IOUtils.printBoard(board, player);
        String printedBoard = baos.toString();
        assertEquals(board2x2, printedBoard);
        board.resetBoard();
    }

    @Test
    void print10x10EmptyBoard() {
        String expectedBoard10x10 =
                System.lineSeparator() +
                        "x --> WHITE stones" + System.lineSeparator() +
                        "O --> BLACK stones (you)" + System.lineSeparator() +
                        System.lineSeparator() +
                        "   ----------------------------------------- " + System.lineSeparator() +
                        "1  |   |   |   |   |   |   |   |   |   |   | " + System.lineSeparator() +
                        "2  |   |   |   |   |   |   |   |   |   |   | " + System.lineSeparator() +
                        "3  |   |   |   |   |   |   |   |   |   |   | " + System.lineSeparator() +
                        "4  |   |   |   |   |   |   |   |   |   |   | " + System.lineSeparator() +
                        "5  |   |   |   |   |   |   |   |   |   |   | " + System.lineSeparator() +
                        "6  |   |   |   |   |   |   |   |   |   |   | " + System.lineSeparator() +
                        "7  |   |   |   |   |   |   |   |   |   |   | " + System.lineSeparator() +
                        "8  |   |   |   |   |   |   |   |   |   |   | " + System.lineSeparator() +
                        "9  |   |   |   |   |   |   |   |   |   |   | " + System.lineSeparator() +
                        "10 |   |   |   |   |   |   |   |   |   |   | " + System.lineSeparator() +
                        "   ----------------------------------------- " + System.lineSeparator() +
                        "     1   2   3   4   5   6   7   8   9   10  " + System.lineSeparator();

        ByteArrayOutputStream baos = IOUtils.redirectSystemOutToByteArrayOS();
        Player player = new Player("blackP", PlayerColor.BLACK);
        Board board = new Board();
        Board.LAST_COLUMN = 10;
        Board.LAST_ROW = 10;
        IOUtils.printBoard(board, player);
        String printedBoard = baos.toString();
        assertEquals(expectedBoard10x10, printedBoard);
        board.resetBoard();
    }

}
