package it.units.crossway.client.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GameDto {

    private String uuid;
    private String whitePlayer;
    private String blackPlayer;

}
