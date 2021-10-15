package it.units.crossway.server.model.dto;

import it.units.crossway.server.model.entity.Game;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GameDto {

    private String uuid;
    private String whitePlayer;
    private String blackPlayer;

    public GameDto(Game game) {
        this.uuid = game.getUuid();
        this.whitePlayer = game.getWhitePlayer();
        this.blackPlayer = game.getBlackPlayer();
    }

}
