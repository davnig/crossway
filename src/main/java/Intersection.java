import lombok.AllArgsConstructor;
import lombok.Data;
import playerProperty.PlayerColor;

@Data
@AllArgsConstructor
public class Intersection {

    int row;
    int column;
    PlayerColor stone;

}
