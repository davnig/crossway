import exception.InvalidUserInputException;
import exception.PlacementViolationException;
import lombok.Data;
import playerProperty.PlayerColor;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Data
public class Match {

    private Board board;
    private Turn turn;
    private Scanner scanner;

    Match() {
        this.scanner = new Scanner(System.in);
        this.board = new Board();
        this.turn = new Turn();
    }

    Match(Board presetBoard) {
        this.scanner = new Scanner(System.in);
        this.board = presetBoard;
        this.turn = new Turn();
    }

    Match(Board presetBoard, Turn turn) {
        this.scanner = new Scanner(System.in);
        this.board = presetBoard;
        this.turn = turn;
    }

    void start() {
        turn.initFirstTurn();
    }

    public void playTurn() throws PlacementViolationException, InvalidUserInputException {
        if (turn.getTurnNumber() == 2 && isPieRuleRequested()) {
            turn.applyPieRule();
            return;
        }
        String input = scanner.nextLine();
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

    private boolean isPieRuleRequested() throws InvalidUserInputException {
        System.out.println("Do you Want to switch colors? Y-yes N-No");
        String whiteResponse = scanner.nextLine();
        if (whiteResponse.equalsIgnoreCase("Y"))
            return true;
        if (whiteResponse.equalsIgnoreCase("N"))
            return false;
        throw new InvalidUserInputException("Input not allowed, insert either Y or N");
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

