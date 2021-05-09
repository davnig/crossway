import lombok.AllArgsConstructor;
import lombok.Data;
import playerProperty.PlayerColor;
import playerProperty.PlayerID;

@Data
@AllArgsConstructor
public class Player {

    private PlayerID id;
    private PlayerColor color;

}
