import exception.PlacementViolationException;
import lombok.Data;

@Data
public class Game {

    private Board board;
    private Turn currentTurn;
    private Player player1, player2;


    Game() {
        this.board = new Board();
        this.player1 = new Player(PlayerID.ONE, PlayerColor.NONE);
        this.player2 = new Player(PlayerID.TWO, PlayerColor.NONE);
        this.currentTurn = new Turn();
    }

    Game(Board presetBoard) {
        this.board = presetBoard;
        this.player1 = new Player(PlayerID.ONE, PlayerColor.NONE);
        this.player2 = new Player(PlayerID.TWO, PlayerColor.NONE);
        this.currentTurn = new Turn();
    }

    Game(Board presetBoard, Turn turn) {
        this.board = presetBoard;
        this.player1 = new Player(PlayerID.ONE, PlayerColor.NONE);
        this.player2 = new Player(PlayerID.TWO, PlayerColor.NONE);
        this.currentTurn = turn;
    }

    void start() {
        this.initFirstTurn();
        System.out.println("New game started.\nIt's black turn.");
    }

    public String playTurn() {
        if (this.currentTurn.getCurrentTurn() == 2) {
            return pieRule();
        }
        return "It's just another turn.";
    }

    public void placeStoneAt(int row, int column) throws PlacementViolationException {
        if (this.board.isLastMoveDiagonalViolation(row, column, getCurrentPlayerColor(), getOppositePlayerColor()))
            throw new PlacementViolationException("Placement not allowed: diagonal violation");

        this.board.getIntersectionAt(row, column).setStone(getCurrentPlayerColor());
    }

    public void switchTurn() {
        this.currentTurn.setCurrentPlayer(getPlayerNotInTurn());
    }

    public Player getPlayerNotInTurn() {
        if (this.currentTurn.getCurrentPlayer().equals(player1))
            return player2;
        else
            return player1;
    }

    public PlayerColor getCurrentPlayerColor() {
        return this.currentTurn.getCurrentPlayer().getColor();
    }

    private PlayerColor getOppositePlayerColor() {
        return isWhiteTurn() ? PlayerColor.BLACK : PlayerColor.WHITE;
    }

    boolean isWhiteTurn() {
        return getCurrentPlayerColor() == PlayerColor.WHITE;
    }

    boolean isBlackTurn() {
        return getCurrentPlayerColor() == PlayerColor.BLACK;
    }

    private void initFirstTurn() {
        this.currentTurn.setCurrentTurn(1);
        this.currentTurn.setCurrentPlayer(player1);
        this.player1.setColor(PlayerColor.BLACK);
    }


    private String pieRule() {
        //here you should ask a player whether or not he/she wants to switch colors
        //implementation depends on future choices, now returns a string just to see if game responds
        return "Pie Rule!";
    }
}

