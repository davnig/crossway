import exception.InvalidUserInputException;
import exception.PlacementViolationException;
import lombok.Data;
import playerProperty.PlayerColor;

import java.util.HashSet;
import java.util.Set;

@Data
public class Match {

    private Board board;
    private Turn turn;

    Match() {
        this.board = new Board();
        this.turn = new Turn();
    }

    Match(Board presetBoard) {
        this.board = presetBoard;
        this.turn = new Turn();
    }

    Match(Board presetBoard, Turn turn) {
        this.board = presetBoard;
        this.turn = turn;
    }

    void start() {
        turn.initFirstTurn();
    }

    public void playTurn() throws PlacementViolationException, InvalidUserInputException {
        if (turn.getTurnNumber() == 2 && IOUtils.isPieRuleRequested()) {
            turn.applyPieRule();
            return;
        }
        String input = IOUtils.getInputLine();
        int row = getIntRowFromPlayerInput(input);
        int column = getIntColumnFromPlayerInput(input);
        validatePositionAndPlaceStone(row, column);
        if (turn.getTurnNumber() >= 19) {
            checkWinCondition(turn.getCurrentPlayer());
        }
        turn.nextTurn();
    }

    public void validatePositionAndPlaceStone(int row, int column) throws PlacementViolationException {
        validatePosition(row, column);
        Intersection intersection = new Intersection(row, column);
        this.board.placeStone(intersection, turn.getCurrentPlayer());
    }

    private void validatePosition(int row, int column) throws PlacementViolationException {
        if (board.isPlacementOutOfBoardBoundaries(row, column))
            throw new PlacementViolationException("Placement not allowed: out of board violation");
        if (isDiagonalViolation(row, column))
            throw new PlacementViolationException("Placement not allowed: diagonal violation");
    }

    private int getIntColumnFromPlayerInput(String input) {
        return Integer.parseInt(input.substring(input.indexOf(",") + 1));
    }

    private int getIntRowFromPlayerInput(String input) {
        return Integer.parseInt(input.substring(0, input.indexOf(",")));
    }

    private boolean isDiagonalViolation(int row, int column) {
        PlayerColor turnColor = turn.getCurrentPlayer();
        PlayerColor oppositeColor = turn.getCurrentPlayerOpponent();
        if (board.isFirstRow(row))
            return isSouthEastDiagonalViolation(row, column, turnColor, oppositeColor) ||
                    isSouthWestDiagonalViolation(row, column, turnColor, oppositeColor);
        if (board.isLastRow(row))
            return isNorthEastDiagonalViolation(row, column, turnColor, oppositeColor) ||
                    isNorthWestDiagonalViolation(row, column, turnColor, oppositeColor);
        if (board.isFirstColumn(column))
            return isNorthEastDiagonalViolation(row, column, turnColor, oppositeColor) ||
                    isSouthEastDiagonalViolation(row, column, turnColor, oppositeColor);
        if (board.isLastColumn(column))
            return isNorthWestDiagonalViolation(row, column, turnColor, oppositeColor) ||
                    isSouthWestDiagonalViolation(row, column, turnColor, oppositeColor);
        return
                isNorthEastDiagonalViolation(row, column, turnColor, oppositeColor) ||
                        isSouthEastDiagonalViolation(row, column, turnColor, oppositeColor) ||
                        isSouthWestDiagonalViolation(row, column, turnColor, oppositeColor) ||
                        isNorthWestDiagonalViolation(row, column, turnColor, oppositeColor);
    }

    private boolean isSouthWestDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {
        return board.getStoneColorAt(new Intersection(row + 1, column)) == oppositeColor &&
                board.getStoneColorAt(new Intersection(row, column - 1)) == oppositeColor &&
                board.getStoneColorAt(new Intersection(row + 1, column - 1)) == turnColor;
    }

    private boolean isNorthWestDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {
        return board.getStoneColorAt(new Intersection(row - 1, column)) == oppositeColor &&
                board.getStoneColorAt(new Intersection(row, column - 1)) == oppositeColor &&
                board.getStoneColorAt(new Intersection(row - 1, column - 1)) == turnColor;
    }

    private boolean isNorthEastDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {
        return board.getStoneColorAt(new Intersection(row - 1, column)) == oppositeColor &&
                board.getStoneColorAt(new Intersection(row, column + 1)) == oppositeColor &&
                board.getStoneColorAt(new Intersection(row - 1, column + 1)) == turnColor;
    }

    private boolean isSouthEastDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {
        return board.getStoneColorAt(new Intersection(row + 1, column)) == oppositeColor &&
                board.getStoneColorAt(new Intersection(row, column + 1)) == oppositeColor &&
                board.getStoneColorAt(new Intersection(row + 1, column + 1)) == turnColor;
    }

    public boolean checkWinCondition(PlayerColor playerColor) {
        if (playerColor == PlayerColor.BLACK) {
            return hasBlackWon();
        }
        return hasWhiteWon();
    }

    /**
     * Check if white has won the match
     *
     * @return a boolean indicating if white has won
     */
    private boolean hasWhiteWon() {
        int startingColumn = getColumnWithLeastAmountOfWhiteStonesBetweenFirstAndLast();
        int targetColumn = getTargetColumnFromStartingColumn(startingColumn);
        final Set<Intersection> intersectionsOccupiedByPlayerInEmptierColumn =
                board.getIntersectionsOccupiedByPlayerInColumn(PlayerColor.WHITE, startingColumn);
        Set<Intersection> visited = new HashSet<>();
        for (Intersection intersection : intersectionsOccupiedByPlayerInEmptierColumn) {
            return hasWhiteWonRecursive(intersection, visited, targetColumn);
        }
        return false;
    }

    private int getTargetColumnFromStartingColumn(int startingColumn) {
        if(startingColumn == board.getFIRST_COLUMN())
            return board.getLAST_COLUMN();
        return board.getFIRST_COLUMN();
    }

    /**
     * Check if black has won the match
     *
     * @return a boolean indicating if black has won
     */
    private boolean hasBlackWon() {
        int startingRow = getRowWithLeastAmountOfBlackStonesBetweenFirstAndLast();
        int targetRow = getTargetRowFromStartingRow(startingRow);
        final Set<Intersection> intersectionsOccupiedByPlayerInEmptierRow =
                board.getIntersectionsOccupiedByPlayerInRow(PlayerColor.BLACK, startingRow);
        Set<Intersection> visited = new HashSet<>();
            for (Intersection intersection : intersectionsOccupiedByPlayerInEmptierRow) {
                return hasBlackWonRecursive(intersection, visited, targetRow);
            }
        return false;
    }

    private int getTargetRowFromStartingRow(int startingRow) {
        if(startingRow == board.getFIRST_ROW())
            return board.getLAST_ROW();
        return board.getFIRST_ROW();
    }

    /**
     * Recursively searches for a path of white stones connecting the vertical edges of the board
     *
     * @param currentIntersection the current {@code Intersection} analyzed
     * @param visited             the {@code Set} of already visited {@code Intersection}s
     * @param targetColumn        the column where the path should end for the method to be successful
     * @return a boolean indicating if white has won
     */
    private boolean hasWhiteWonRecursive(Intersection currentIntersection, Set<Intersection> visited, int targetColumn) {
        visited.add(currentIntersection);
        if (currentIntersection.getColumn() == targetColumn)
            return true;
        for (Intersection adjIntersection : board.getAdjIntersections(currentIntersection)) {
            if (board.getBoardState().get(adjIntersection) == PlayerColor.WHITE && !(visited.contains(adjIntersection))) {
                if (hasWhiteWonRecursive(adjIntersection, visited, targetColumn)) return true;
            }
        }
        return false;
    }

    /**
     * Recursively searches for a path of black stones connecting the horizontal edges of the board
     *
     * @param currentIntersection the current {@code Intersection} analyzed
     * @param visited             the {@code Set} of already visited {@code Intersection}s
     * @param targetRow           the row where the path should end for the method to be successful
     * @return a boolean indicating if black has won
     */
    private boolean hasBlackWonRecursive(Intersection currentIntersection, Set<Intersection> visited, int targetRow) {
        visited.add(currentIntersection);
        if (currentIntersection.getRow() == targetRow)
            return true;
        for (Intersection adjIntersection : board.getAdjIntersections(currentIntersection)) {
            if (board.getBoardState().get(adjIntersection) == PlayerColor.BLACK && !(visited.contains(adjIntersection))) {
                if (hasBlackWonRecursive(adjIntersection, visited, targetRow)) return true;
            }
        }
        return false;
    }

    private int getColumnWithLeastAmountOfWhiteStonesBetweenFirstAndLast() {
        int firstColumnCount = board.getNumberOfStonesInColumnByPlayerColor(board.getFIRST_COLUMN(), PlayerColor.WHITE);
        int lastColumnCount = board.getNumberOfStonesInColumnByPlayerColor(board.getLAST_COLUMN(), PlayerColor.WHITE);
        return firstColumnCount < lastColumnCount ? board.getFIRST_COLUMN() : board.getLAST_COLUMN();
    }

    private int getRowWithLeastAmountOfBlackStonesBetweenFirstAndLast() {
        int firstRowCount = board.getNumberOfStonesInRowByPlayerColor(board.getFIRST_ROW(), PlayerColor.WHITE);
        int lastRowCount = board.getNumberOfStonesInRowByPlayerColor(board.getLAST_ROW(), PlayerColor.WHITE);
        return firstRowCount < lastRowCount ? board.getFIRST_ROW() : board.getLAST_ROW();
    }

}

