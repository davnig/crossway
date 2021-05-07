import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Turn {
    private int currentTurn;
    private Player currentPlayer;

    Turn() {
    }
}
