package it.units.crossway.client;

import it.units.crossway.client.model.Board;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
                        "   --------- " + System.lineSeparator() +
                        "1  |   |   | " + System.lineSeparator() +
                        "2  |   |   | " + System.lineSeparator() +
                        "   --------- " + System.lineSeparator() +
                        "     1   2   " + System.lineSeparator();

        ByteArrayOutputStream baos = IOUtils.redirectSystemOutToByteArrayOS();

        Board board = new Board();
        Board.LAST_COLUMN = 2;
        Board.LAST_ROW = 2;
        IOUtils.printBoard(board);
        String printedBoard = baos.toString();
        assertEquals(board2x2, printedBoard);
        board.resetBoard();
    }

}
