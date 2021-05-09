import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputOutputTests {

    @BeforeEach
    void init() {

    }

    @Test
    void givenSystemInRedirectWhenScannerNextShouldReadPreMadeString() {
        String data = "Hello, World!";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Scanner scanner = new Scanner(System.in);
        assertEquals(scanner.nextLine(), data);
    }

}
