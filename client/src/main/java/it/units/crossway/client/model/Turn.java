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
    }

    public void nextTurn() {
        turnNumber++;
        switchTurnColor();
    }

    public void switchTurnColor() {
        turnColor = turnColor.getOpposite();
    }

    public String getTurnInfoAsString() {
        return "\n===================\n" +
                String.format("Turn %d: %s plays%n", turnNumber, turnColor) +
                "===================\n";
    }

}
