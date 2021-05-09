import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Turn {

    private int turnNumber;
    private Player currentPlayer;

    public void incrementTurnNumber() {
        this.turnNumber++;
    }

}
