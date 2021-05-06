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
        if (isWhiteTurn()) {
            if (isLastMoveDiagonalViolationFromWhite(row, column)) {
                throw new PlacementViolationException("Exception: Placement not allowed (Diagonal Violation)");
            } else {
                board.getIntersectionAt(row, column).setStone(turnColor);
            }
        } else {
            board.getIntersectionAt(row, column).setStone(turnColor);
        }
    }

    private boolean isLastMoveDiagonalViolationFromWhite(int row, int column) {
        Intersection northIntersection = null, southIntersection = null, eastIntersection = null, westIntersection = null;

        //To avoid IndexOutOfBounds Exception
        if (row != 1) {
            northIntersection = board.getIntersectionAt(row - 1, column);
        }
        if (row != 19) {
            southIntersection = board.getIntersectionAt(row + 1, column);
        }
        if (column != 1) {
            westIntersection = board.getIntersectionAt(row, column - 1);
        }
        if (column != 19) {
            eastIntersection = board.getIntersectionAt(row, column + 1);
        }

        if (westIntersection != null && southIntersection != null) { //south-west violation i.e.
            if (westIntersection.getStone() == PlayerColor.BLACK && southIntersection.getStone() == PlayerColor.BLACK)
                return board.getIntersectionAt(row + 1, column - 1).getStone() == this.turnColor;
        }
        if (northIntersection != null && westIntersection != null) { //north-west violation i.e.
            if (northIntersection.getStone() == PlayerColor.BLACK && westIntersection.getStone() == PlayerColor.BLACK)
                return board.getIntersectionAt(row - 1, column - 1).getStone() == this.turnColor;
        }
        if (northIntersection != null && eastIntersection != null) { //north-east violation i.e.
            if (northIntersection.getStone() == PlayerColor.BLACK && eastIntersection.getStone() == PlayerColor.BLACK)
                return board.getIntersectionAt(row - 1, column + 1).getStone() == this.turnColor;
        }
        if (eastIntersection != null && southIntersection != null) { //south-east violation i.e.
            if (eastIntersection.getStone() == PlayerColor.BLACK && southIntersection.getStone() == PlayerColor.BLACK)
                return board.getIntersectionAt(row + 1, column + 1).getStone() == this.turnColor;
        }
        return false;
    }
}

