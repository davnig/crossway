package it.units.crossway.client.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDto {

    private String uuid;
    private String whitePlayerNickname;
    private String blackPlayerNickname;

}
