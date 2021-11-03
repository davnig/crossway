package it.units.crossway.client;

import it.units.crossway.client.model.*;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

@Data
public class IOUtils {

	public static Scanner scanner = new Scanner(System.in);

	public static void redirectScannerToSimulatedInput(String input) {
		scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
	}

	public static ByteArrayOutputStream redirectSystemOutToByteArrayOS() {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(byteArrayOutputStream));
		return byteArrayOutputStream;
	}

	public static void printCurrentPlayer(Turn turn) {
		String currentPlayer = turn.getTurnColor().toString();
		System.out.println("It is the turn of the " + currentPlayer + " player!");
	}

	public static void printAskNextMove() {
		System.out.println("Insert a valid placement for your stone...");
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

	public static void printWinner(PlayerColor playerColor) {
		String currentPlayer = playerColor.toString();
		System.out.println(currentPlayer + " has won!!!!");
	}

	public static void printBoard(Board board, Player player) {
		printLegend(player);
		printRowSeparator();
		for (int row = Board.FIRST_ROW; row <= Board.LAST_ROW; row++) {
			System.out.print(row);
			if (row < 10) {
				System.out.print("  ");
			} else {
				System.out.print(" ");
			}
			printRow(board, row);
		}
		printRowSeparator();
		printColumnEnumeration();
	}

	private static void printLegend(Player player) {
		System.out.println();
		Arrays.stream(PlayerColor.values())
				.sorted()
				.filter(color -> !color.equals(PlayerColor.NONE))
				.forEach(color -> System.out.println(
						getPrintSymbolByPlayerColor(color) + " --> " + color + " stones" +
								(player.getColor().equals(color) ? " (you)" : "")
				));
		System.out.println();
	}

	private static void printColumnEnumeration() {
		System.out.print("     ");
		for (int col = Board.FIRST_ROW; col <= Board.LAST_ROW; col++) {
			System.out.print(col);
			if (col < 10) {
				System.out.print("   ");
			} else {
				System.out.print("  ");
			}
		}
		System.out.println();
	}

	private static void printRowSeparator() {
		System.out.print("   -");
		for (int col = Board.FIRST_ROW; col <= Board.LAST_ROW; col++) {
			System.out.print("----");
		}
		System.out.print(" ");
		System.out.println();
	}

	private static void printRow(Board board, int row) {
		for (int col = Board.FIRST_ROW; col <= Board.LAST_ROW; col++) {
			System.out.print("| " + getPrintSymbolForIntersection(board, row, col) + " ");
		}
		System.out.println("| ");
	}

	private static String getPrintSymbolForIntersection(Board board, int row, int column) {
		PlayerColor playerColorAtIntersection = board.getStoneColorAt(row, column);
		if (playerColorAtIntersection != null) {
			return getPrintSymbolByPlayerColor(playerColorAtIntersection);
		}
		return " ";
	}

	private static String getPrintSymbolByPlayerColor(PlayerColor playerColor) {
		switch (playerColor) {
			case BLACK:
				return "O";
			case WHITE:
				return "x";
			default:
				return " ";
		}
	}

	public static StonePlacementIntent getStonePlacementIntentFromInput(Player player) {
		printAskNextMove();
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
