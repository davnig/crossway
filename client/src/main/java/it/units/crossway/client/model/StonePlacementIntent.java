package it.units.crossway.client.model;

import it.units.crossway.client.IOUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StonePlacementIntent {

    private int row;
    private int column;
    private Player player;

    public static StonePlacementIntent getStonePlacementIntentFromInput(Player player) {
        String input = IOUtils.getInputLine();
        int row = IOUtils.getIntRowFromPlayerInput(input);
        int column = IOUtils.getIntColumnFromPlayerInput(input);
        return new StonePlacementIntent(row, column, player);
    }

}
