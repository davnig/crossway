import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import playerProperty.PlayerColor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Turn {

    private int turnNumber;
    private PlayerColor currentPlayer;

    public void initFirstTurn() {
        turnNumber = 1;
        currentPlayer = PlayerColor.BLACK;
    }

    public void nextTurn() {
        turnNumber++;
        switchCurrentPlayer();
    }

    public void switchCurrentPlayer() {
        currentPlayer = getCurrentPlayerOpponent();
    }

    public void applyPieRule() {
        switchCurrentPlayer();
        nextTurn();
    }

    public PlayerColor getCurrentPlayerOpponent() {
        return switch (currentPlayer) {
            case BLACK -> PlayerColor.WHITE;
            case WHITE -> PlayerColor.BLACK;
            default -> PlayerColor.NONE;
        };
    }
}
