package it.units.crossway.client.model.dto;

import it.units.crossway.client.model.StonePlacementIntent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StonePlacementIntentDto {

    private int row;
    private int column;
    private String nickname;

    public StonePlacementIntentDto(StonePlacementIntent stonePlacementIntent) {
        this(
                stonePlacementIntent.getRow(),
                stonePlacementIntent.getColumn(),
                stonePlacementIntent.getPlayer().getNickname()
        );
    }

}
