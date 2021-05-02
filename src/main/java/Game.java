import lombok.Data;

@Data
public class Game {

	private PlayerColor turn;

	public Game() {

	}

	private boolean isWhiteTurn() {
		return this.getTurn() == PlayerColor.WHITE;
	}

	private boolean isBlackTurn() {
		return this.getTurn() == PlayerColor.BLACK;
	}

}
