import exception.InvalidUserInputException;
import lombok.Data;

import java.util.Scanner;

@Data
public class IOUtils {

	public static Scanner scanner;

	IOUtils() {
		scanner = new Scanner(System.in);
	}

	public static String getInputLine() {
		return scanner.nextLine();
	}

	public static boolean isPieRuleRequested() throws InvalidUserInputException {
		System.out.println("Do you Want to switch colors? Y-yes N-No");
		String whiteResponse = scanner.nextLine();
		if (whiteResponse.equalsIgnoreCase("Y"))
			return true;
		if (whiteResponse.equalsIgnoreCase("N"))
			return false;
		throw new InvalidUserInputException("Input not allowed, insert either Y or N");
	}

}
