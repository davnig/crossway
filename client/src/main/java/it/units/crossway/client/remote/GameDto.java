package it.units.crossway.client.remote;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GameDto {

    private String uuid;
    private String whitePlayer;
    private String blackPlayer;

}
