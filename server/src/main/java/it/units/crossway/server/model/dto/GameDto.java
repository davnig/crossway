package it.units.crossway.server.model.dto;

import it.units.crossway.server.model.entity.Game;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GameDto {

    private String uuid;
    private String whitePlayerNickname;
    private String blackPlayerNickname;

    public GameDto(Game game) {
        this.uuid = game.getUuid();
        this.whitePlayerNickname = game.getWhitePlayerNickname();
        this.blackPlayerNickname = game.getBlackPlayerNickname();
    }

}
