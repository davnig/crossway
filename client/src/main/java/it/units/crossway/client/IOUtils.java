package it.units.crossway.client;

import it.units.crossway.client.model.*;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Scanner;

@Data
public class IOUtils {

	public static Scanner scanner = new Scanner(System.in);

	public static void redirectScannerToSimulatedInput(String input) {
		scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
	}

	public static void printCurrentPlayer(Turn turn) {
		String currentPlayer = turn.getCurrentPlayer().toString();
		System.out.println("It is the turn of the " + currentPlayer + " player!");
	}

	public static void printAskNextMove() {
		System.out.println("insert a valid placement for your stone...");
	}

    public static String getInputLine() {
        return scanner.nextLine();
    }

    public static boolean isPieRuleRequested() {
        while (true) {
            System.out.println("Do you Want to switch colors? Y-yes N-No");
            String whiteResponse = scanner.nextLine();
            if (whiteResponse.equalsIgnoreCase("Y"))
                return true;
            if (whiteResponse.equalsIgnoreCase("N"))
                return false;
            System.out.println("Input not allowed, insert either Y or N");
        }
    }

	public static boolean isChoiceToQuit(String choice, String quitString) {
		return choice.equals(quitString);
	}

    public static boolean isChoiceAValidInteger(String choice) {
        return choice.matches("\\d+");
    }

	public static void clearCLI() throws IOException, InterruptedException {
		new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	}

	public static void printWinner(Turn turn) {
		String currentPlayer = turn.getCurrentPlayer().toString();
		System.out.println(currentPlayer + " has won!!!!");
	}

	public static void printBoard(Board board) {
		printRowSeparator(board);
		for (int row = Board.FIRST_ROW; row <= Board.LAST_ROW; row++) {
			printRow(board, row);
		}
		printRowSeparator(board);
	}

	private static void printRowSeparator(Board board) {
		System.out.print("-");
		for (int col = Board.FIRST_ROW; col <= Board.LAST_ROW; col++) {
			System.out.print("----");
		}
		System.out.println();
	}

	private static void printRow(Board board, int row) {
		for (int col = Board.FIRST_ROW; col <= Board.LAST_ROW; col++) {
			System.out.print("| " + getPrintSymbolForIntersection(board, row, col) + " ");
		}
		System.out.println("|");
	}

	private static String getPrintSymbolForIntersection(Board board, int row, int column) {
		PlayerColor playerColorAtIntersection = board.getStoneColorAt(row, column);
		if (playerColorAtIntersection != null) {
			switch (playerColorAtIntersection) {
				case BLACK:
					return "O";
				case WHITE:
					return "x";
				default:
					return " ";
			}
		}
		return " ";
	}

	public static StonePlacementIntent getStonePlacementIntentFromInput(Player player) {
		String input = IOUtils.getInputLine();
		int row = IOUtils.getIntRowFromPlayerInput(input);
		int column = IOUtils.getIntColumnFromPlayerInput(input);
		return new StonePlacementIntent(row, column, player);
	}

	public static int getIntColumnFromPlayerInput(String input) {
		return Integer.parseInt(input.substring(input.indexOf(",") + 1));
	}

	public static int getIntRowFromPlayerInput(String input) {
		return Integer.parseInt(input.substring(0, input.indexOf(",")));
	}

}
