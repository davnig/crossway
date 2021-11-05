package it.units.crossway.client.model;

import it.units.crossway.client.IOUtils;
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
        IOUtils.printTurnInfo(this);
    }

    public void nextTurn() {
        turnNumber++;
        switchTurnColor();
        IOUtils.printTurnInfo(this);
    }

    public void switchTurnColor() {
        turnColor = turnColor.getOpposite();
    }

}
