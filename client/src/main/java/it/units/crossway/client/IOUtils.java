package it.units.crossway.client;

import it.units.crossway.client.model.Turn;
import lombok.Data;

import java.io.IOException;
import java.util.Scanner;

@Data
public class IOUtils {

    public static Scanner scanner = new Scanner(System.in);

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
}
