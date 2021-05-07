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

        if (board.isLastMoveDiagonalViolation(row, column, turnColor, getOppositePlayerColor()))
            throw new PlacementViolationException("Placement not allowed: diagonal violation");

        board.getIntersectionAt(row, column).setStone(turnColor);

    }

    private PlayerColor getOppositePlayerColor() {
        if (isWhiteTurn())
            return PlayerColor.BLACK;

        return PlayerColor.WHITE;
    }

}

