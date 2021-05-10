import exception.InvalidUserInputException;
import exception.PlacementViolationException;
import lombok.Data;
import playerProperty.PlayerColor;
import playerProperty.PlayerID;

import java.util.Scanner;

@Data
public class Game {

    private Board board;
    private Turn turn;
    private Player player1, player2;
    private Scanner scanner;

    Game() {
        this.scanner = new Scanner(System.in);
        this.board = new Board();
        this.initPlayers();
        this.turn = new Turn();
    }

    Game(Board presetBoard) {
        this.scanner = new Scanner(System.in);
        this.board = presetBoard;
        this.initPlayers();
        this.turn = new Turn();
    }

    Game(Board presetBoard, Turn turn) {
        this.scanner = new Scanner(System.in);
        this.board = presetBoard;
        this.initPlayers();
        this.turn = turn;
    }

    void start() {
        this.initFirstTurn();
        System.out.println("New game started.\nIt's black turn.");
    }

    public void playTurn() throws PlacementViolationException, InvalidUserInputException {

        if (this.turn.getTurnNumber() == 2) {
            System.out.println("Do you Want to switch colors? Y -yes N-No");
            String whiteResponseToPieRule = scanner.nextLine();
            if (pieRule(whiteResponseToPieRule)) {
                return;
            }
        }

        String input = scanner.nextLine();
        int row = getIntRowFromPlayerInput(input);
        int column = getIntColumnFromPlayerInput(input);

        placeStoneAt(row, column);
        endTurn();
    }

    public void placeStoneAt(int row, int column) throws PlacementViolationException {
        if (this.board.isLastMoveDiagonalViolation(row, column, getCurrentPlayerColor(), getOppositePlayerColor()))
            throw new PlacementViolationException("Placement not allowed: diagonal violation");

        this.board.getIntersectionAt(row, column).setStone(getCurrentPlayerColor());
    }

    public PlayerColor getCurrentPlayerColor() {
        return this.turn.getCurrentPlayer().getColor();
    }

    public void switchTurnAndColorWithPieRule() {
        endTurn();
        switchColors();
    }


    private void endTurn() {
        turn.setCurrentPlayer(getPlayerNotInTurn());
        turn.incrementTurnNumber();
    }

    private void switchColors() {
        PlayerColor playerONEOldColor = this.player1.getColor();
        this.player1.setColor(this.player2.getColor());
        this.player2.setColor(playerONEOldColor);
    }

    private Player getPlayerNotInTurn() {
        return this.turn.getCurrentPlayer().equals(player1) ? player2 : player1;
    }

    private PlayerColor getOppositePlayerColor() {
        return isWhiteTurn() ? PlayerColor.BLACK : PlayerColor.WHITE;
    }

    private void initFirstTurn() {
        this.turn.setTurnNumber(1);
        this.turn.setCurrentPlayer(player1);
    }

    private void initPlayers() {
        this.player1 = new Player(PlayerID.ONE, PlayerColor.BLACK);
        this.player2 = new Player(PlayerID.TWO, PlayerColor.WHITE);
    }

    private int getIntColumnFromPlayerInput(String input) {
        return Integer.parseInt(input.substring(input.indexOf(",") + 1));
    }

    private int getIntRowFromPlayerInput(String input) {
        return Integer.parseInt(input.substring(0, input.indexOf(",")));
    }

    private boolean pieRule(String whiteResponse) throws InvalidUserInputException {
        if (whiteResponse.equalsIgnoreCase("Y")) {
            switchTurnAndColorWithPieRule();
            return true;
        } else if (whiteResponse.equalsIgnoreCase("N")) {
            return false;
        } else {
            throw new InvalidUserInputException("Input not allowed, insert either Y or N");
        }
    }

    boolean isWhiteTurn() {
        return getCurrentPlayerColor() == PlayerColor.WHITE;
    }

    boolean isBlackTurn() {
        return getCurrentPlayerColor() == PlayerColor.BLACK;
    }

}

