package it.units.crossway.client;

import it.units.crossway.client.exception.InvalidUserInputException;
import it.units.crossway.client.model.Player;
import it.units.crossway.client.model.StonePlacementIntent;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

@Data
public class IOUtils {

    public static Scanner scanner = new Scanner(System.in);
    public static final String IO_CHOOSE_NICKNAME = "Enter a nickname: ";
    public static final String IO_WAITING_FOR_OPPONENT_MOVE = "Waiting for opponent move...";
    public static final String IO_INSERT_VALID_PLACEMENT = "Insert a valid placement for your stone (e.g. 3,4)...";
    public static final String NEW_GAME_CHOICE = "1";
    public static final String JOIN_GAME_CHOICE = "2";
    public static final String QUIT_GAME_CHOICE = "q";

    public static void redirectScannerToSimulatedInput(String input) {
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    }

    public static ByteArrayOutputStream redirectSystemOutToByteArrayOS() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));
        return byteArrayOutputStream;
    }

    public static String getInputLine() {
        return scanner.nextLine();
    }

    public static boolean isChoiceToQuit(String choice, String quitString) {
        return choice.equals(quitString);
    }

    public static boolean isChoiceAValidInteger(String choice) {
        return choice.matches("\\d+");
    }

    public static StonePlacementIntent getStonePlacementIntentFromInput(Player player) throws InvalidUserInputException {
        String input = IOUtils.getInputLine();
        isValidStonePlacementInput(input);
        int row = IOUtils.getIntRowFromPlayerInput(input);
        int column = IOUtils.getIntColumnFromPlayerInput(input);
        return new StonePlacementIntent(row, column, player);
    }

    private static void isValidStonePlacementInput(String input) throws InvalidUserInputException {
        if (!input.matches("\\d+,\\d+")) {
            throw new InvalidUserInputException(input + " is an invalid input!");
        }
    }

    public static int getIntColumnFromPlayerInput(String input) {
        return Integer.parseInt(input.substring(input.indexOf(",") + 1));
    }

    public static int getIntRowFromPlayerInput(String input) {
        return Integer.parseInt(input.substring(0, input.indexOf(",")));
    }

}
