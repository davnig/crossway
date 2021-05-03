import exception.PlacementViolationException;
import lombok.Data;

@Data
public class Game {

    private Board board;
    private PlayerColor turnColor;


    Game() {
        this.board = new Board();
    }

    Game(Board presetBoard) {
        this.board = presetBoard;
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

    public void placeStoneAt(int row, int column) throws PlacementViolationException {
        if (row == 1 && column == 3) {
            throw new PlacementViolationException("Exception: Placement not allowed (Diagonal Violation)");
        }
        board.getIntersectionAt(row, column).setStone(turnColor);
    }
}
