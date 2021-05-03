import lombok.Data;

@Data
public class Game {

	private Board board;
	private PlayerColor turn;


	Game() {
		this.board = new Board();
	}

	void start() {
		this.initFirstTurn();
		System.out.println("Nuova partita avviata.\nInizia il nero.");
	}

	boolean isWhiteTurn() {
		return this.getTurn() == PlayerColor.WHITE;
	}

	boolean isBlackTurn() {
		return this.getTurn() == PlayerColor.BLACK;
	}

	private void initFirstTurn() {
		setTurn(PlayerColor.BLACK);
	}

}
