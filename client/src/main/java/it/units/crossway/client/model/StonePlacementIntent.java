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

}
