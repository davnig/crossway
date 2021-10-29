package it.units.crossway.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Turn {

    private int turnNumber;
    private PlayerColor turnColor;

    public void initFirstTurn() {
        turnNumber = 1;
        turnColor = PlayerColor.BLACK;
        printTurnInfo();
    }

    public void nextTurn() {
        turnNumber++;
        switchTurnColor();
        printTurnInfo();
    }

    public void switchTurnColor() {
        turnColor = getCurrentPlayerOpponent();
    }

    public PlayerColor getCurrentPlayerOpponent() {
        switch (turnColor) {
            case BLACK:
                return PlayerColor.WHITE;
            case WHITE:
                return PlayerColor.BLACK;
            default:
                return PlayerColor.NONE;
        }
    }

    private void printTurnInfo() {
        System.out.printf("Turn %d: %s plays%n", turnNumber, turnColor);
    }
}
