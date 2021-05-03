import lombok.Data;

@Data
public class Game {

	private Board board;
	private PlayerColor turnColor;


	Game() {
		this.board = new Board();
	}

	void start() {
		this.initFirstTurn();
		System.out.println("Nuova partita avviata.\nInizia il nero.");
	}

	boolean isWhiteTurn() {
		return this.getTurnColor() == PlayerColor.WHITE;
	}

	boolean isBlackTurn() {
		return this.getTurnColor() == PlayerColor.BLACK;
	}

	private void initFirstTurn() {
		setTurnColor(PlayerColor.BLACK);
	}

	public void placeStoneAt(int row, int column) {
		board.getIntersectionAt(row, column).setStone(turnColor);
	}
}
