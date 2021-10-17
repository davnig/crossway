package it.units.crossway.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StonePlacementIntent {

    private int row;
    private int column;
    private String nickname;

}
