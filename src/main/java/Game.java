import lombok.Data;

@Data
public class Game {

	private PlayerColor turn;

	public Game() {
		this.initFirstTurn();
	}

	private void initFirstTurn() {
		setTurn(PlayerColor.BLACK);
	}

	public boolean isWhiteTurn() {
		return this.getTurn() == PlayerColor.WHITE;
	}

	public boolean isBlackTurn() {
		return this.getTurn() == PlayerColor.BLACK;
	}

}
