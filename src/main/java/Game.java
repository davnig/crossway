import exception.PlacementViolationException;
import lombok.Data;

@Data
public class Game {

    private Board board;
    private int currentTurn;
    private boolean hasPieRuleAlreadyBeenUsed = false;
    private PlayerColor turnColor;


    Game() {
        this.board = new Board();
    }

    Game(Board presetBoard) {
        this.board = presetBoard;
    }

    Game(Board presetBoard, int turn) {
        this.board = presetBoard;
        this.currentTurn = turn;
    }

    void start() {
        this.initFirstTurn();
        System.out.println("Nuova partita avviata.\nInizia il nero.");
    }

    public String playTurn() {
        if (currentTurn == 2) {
            if (!hasPieRuleAlreadyBeenUsed) {
                return pieRule();
            }
        }
        return "It's just another turn.";
    }

    public void placeStoneAt(int row, int column) throws PlacementViolationException {
        if (board.isLastMoveDiagonalViolation(row, column, turnColor, getOppositePlayerColor()))
            throw new PlacementViolationException("Placement not allowed: diagonal violation");

        board.getIntersectionAt(row, column).setStone(turnColor);
    }

    public void switchTurn() {
        this.turnColor = getOppositePlayerColor();
    }

    boolean isWhiteTurn() {
        return this.getTurnColor() == PlayerColor.WHITE;
    }

    boolean isBlackTurn() {
        return this.getTurnColor() == PlayerColor.BLACK;
    }

    private void initFirstTurn() {
        setTurnColor(PlayerColor.BLACK);
        currentTurn = 1;
    }

    private PlayerColor getOppositePlayerColor() {
        return isWhiteTurn() ? PlayerColor.BLACK : PlayerColor.WHITE;
    }

    private String pieRule() {
        //here you should ask a player whether or not he/she wants to switch colors
        //implementation depends on future choices, now returns a string just to see if game responds
        hasPieRuleAlreadyBeenUsed = true;
        return "Pie Rule!";
    }
}

