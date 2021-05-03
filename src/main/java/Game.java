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
        /*
         *Specific case for the test, White turn
         *White tries to place stone at 1,3
         *Has black stones on its West and South Side
         *Has white stone on 2,2
         */
        if (this.turnColor == PlayerColor.WHITE) {
            Intersection N, S, E, W;
            S = new Intersection(0, 0, PlayerColor.NONE);
            W = new Intersection(0, 0, PlayerColor.NONE);
            //To avoid IndexOutOfBounds Exception
            if (row != 1)/*
                N = board.getIntersectionAt(row - 1, column);*/
                if (row != 19)
                    S = board.getIntersectionAt(row + 1, column);
            if (column != 1)
                W = board.getIntersectionAt(row, column - 1);/*
            if (column != 19)
                E = board.getIntersectionAt(row, column + 1);*/

            if (W.getStone() == PlayerColor.BLACK && S.getStone() == PlayerColor.BLACK) {
                if (board.getIntersectionAt(row + 1, column - 1).getStone() == this.turnColor)
                    throw new PlacementViolationException("Exception: Placement not allowed (Diagonal Violation)");
            }
        }
        board.getIntersectionAt(row, column).setStone(turnColor);
    }
}
