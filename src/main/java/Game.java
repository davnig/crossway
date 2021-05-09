import exception.PlacementViolationException;
import lombok.Data;
import playerProperty.PlayerColor;
import playerProperty.PlayerID;

import java.util.Scanner;

@Data
public class Game {

    private Board board;
    private Turn currentTurn;
    private Player player1, player2;
    private Scanner scanner;

    Game() {
        this.scanner = new Scanner(System.in);
        this.board = new Board();
        this.player1 = new Player(PlayerID.ONE, PlayerColor.NONE);
        this.player2 = new Player(PlayerID.TWO, PlayerColor.NONE);
        this.currentTurn = new Turn();
    }

    Game(Board presetBoard) {
        this.scanner = new Scanner(System.in);
        this.board = presetBoard;
        this.player1 = new Player(PlayerID.ONE, PlayerColor.NONE);
        this.player2 = new Player(PlayerID.TWO, PlayerColor.NONE);
        this.currentTurn = new Turn();
    }

    Game(Board presetBoard, Turn turn) {
        this.scanner = new Scanner(System.in);
        this.board = presetBoard;
        this.player1 = new Player(PlayerID.ONE, PlayerColor.NONE);
        this.player2 = new Player(PlayerID.TWO, PlayerColor.NONE);
        this.currentTurn = turn;
    }

    void start() {
        this.initFirstTurn();
        System.out.println("New game started.\nIt's black turn.");
    }

    public void playTurn() throws PlacementViolationException {
        if (this.currentTurn.getCurrentTurn() == 2) {
            //boolean whiteDecisionOnPieRule = true;
            pieRule(true);
            return;
        }
        String input = scanner.nextLine();
        int row = getIntRowFromStringInput(input);
        int column = getIntColumnFromStringInput(input);
        placeStoneAt(row, column);
        switchTurn();
    }

    private int getIntColumnFromStringInput(String input) {
        return Integer.parseInt(input.substring(input.indexOf(",") + 1));
    }

    private int getIntRowFromStringInput(String input) {
        return Integer.parseInt(input.substring(0, input.indexOf(",")));
    }

    public void placeStoneAt(int row, int column) throws PlacementViolationException {
        if (this.board.isLastMoveDiagonalViolation(row, column, getCurrentPlayerColor(), getOppositePlayerColor()))
            throw new PlacementViolationException("Placement not allowed: diagonal violation");

        this.board.getIntersectionAt(row, column).setStone(getCurrentPlayerColor());
    }

    private void incrementTurnNumber() {
        this.currentTurn.setCurrentTurn(currentTurn.getCurrentTurn() + 1);
    }

    public void switchTurn() {
        this.currentTurn.setCurrentPlayer(getPlayerNotInTurn());
        incrementTurnNumber();
    }

    public void switchColors() {
        PlayerColor playerONEOldColor = this.player1.getColor();
        this.player1.setColor(this.player2.getColor());
        this.player2.setColor(playerONEOldColor);
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

    public void switchTurnAndColorWithPieRule() {
        switchTurn();
        switchColors();
    }

    private void pieRule(boolean whiteWantsToChangeColor) {
        if (whiteWantsToChangeColor) {
            switchTurnAndColorWithPieRule();
        }
    }
}

