import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Player {
    private PlayerID id;
    private PlayerColor color;
}
