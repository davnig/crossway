package it.units.crossway.client.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StonePlacementIntent {

    private int row;
    private int column;
    private String nickname;

}