package it.units.crossway.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameJoinIntent {

    private String uuid;
    private String playerNickname;

}
