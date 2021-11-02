package it.units.crossway.server.model.dto;

import it.units.crossway.server.model.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDto {

    private String nickname;

    public PlayerDto(Player player) {
        this.nickname = player.getNickname();
    }

}
