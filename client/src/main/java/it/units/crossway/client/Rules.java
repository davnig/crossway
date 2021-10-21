package it.units.crossway.client;

import it.units.crossway.client.exception.PlacementViolationException;
import it.units.crossway.client.model.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * This class contains all the game rules logic
 */
public class Rules {

    public static boolean isPieRuleTurn(Turn turn) {
        return turn.getTurnNumber() == 2;
    }

    public static boolean isWinValidTurn(Turn turn) {
        return turn.getTurnNumber() >= 19;
    }

    public static void validatePlacementIntent(Board board, StonePlacementIntent stonePlacementIntent)
            throws PlacementViolationException {
        if (board.isPlacementOutOfBoardBoundaries(stonePlacementIntent.getRow(), stonePlacementIntent.getColumn()))
            throw new PlacementViolationException("Placement not allowed: out of board violation");
        if (isDiagonalViolation(board, stonePlacementIntent))
            throw new PlacementViolationException("Placement not allowed: diagonal violation");
    }

    private static boolean isDiagonalViolation(Board board, StonePlacementIntent stonePlacementIntent) {
        int row = stonePlacementIntent.getRow();
        int column = stonePlacementIntent.getColumn();
        if (board.isFirstRow(row))
            return checkGivenDiagonalViolations(board, stonePlacementIntent, isSouthEastDiagonalViolation(),
                    isSouthWestDiagonalViolation());
        if (board.isLastRow(row))
            return checkGivenDiagonalViolations(board, stonePlacementIntent, isNorthEastDiagonalViolation(),
                    isNorthWestDiagonalViolation());
        if (board.isFirstColumn(column))
            return checkGivenDiagonalViolations(board, stonePlacementIntent, isNorthEastDiagonalViolation(),
                    isSouthEastDiagonalViolation());
        if (board.isLastColumn(column))
            return checkGivenDiagonalViolations(board, stonePlacementIntent, isNorthWestDiagonalViolation(),
                    isSouthWestDiagonalViolation());
        return checkGivenDiagonalViolations(
                board, stonePlacementIntent,
                isNorthEastDiagonalViolation(),
                isSouthEastDiagonalViolation(),
                isSouthWestDiagonalViolation(),
                isNorthWestDiagonalViolation()
        );
    }

    @SafeVarargs
    private static boolean checkGivenDiagonalViolations(Board board, StonePlacementIntent stonePlacementIntent,
                                                        BiFunction<Board, StonePlacementIntent, Boolean>... functions) {
        return Arrays.stream(functions)
                .map(fun -> fun.apply(board, stonePlacementIntent))
                .reduce(Boolean.FALSE, Boolean::logicalOr);
    }

    private static BiFunction<Board, StonePlacementIntent, Boolean> isSouthWestDiagonalViolation() {
        return (((board, stonePlacementIntent) -> {
            PlayerColor playerColor = stonePlacementIntent.getPlayer().getColor();
            int row = stonePlacementIntent.getRow();
            int column = stonePlacementIntent.getColumn();
            return board.getStoneColorAt(new Intersection(row + 1, column)) != playerColor &&
                    board.getStoneColorAt(new Intersection(row, column - 1)) != playerColor &&
                    board.getStoneColorAt(new Intersection(row + 1, column - 1)) == playerColor;
        }));
    }

    private static BiFunction<Board, StonePlacementIntent, Boolean> isNorthWestDiagonalViolation() {
        return (((board, stonePlacementIntent) -> {
            PlayerColor playerColor = stonePlacementIntent.getPlayer().getColor();
            int row = stonePlacementIntent.getRow();
            int column = stonePlacementIntent.getColumn();
            return board.getStoneColorAt(new Intersection(row - 1, column)) != playerColor &&
                    board.getStoneColorAt(new Intersection(row, column - 1)) != playerColor &&
                    board.getStoneColorAt(new Intersection(row - 1, column - 1)) == playerColor;
        }));
    }

    private static BiFunction<Board, StonePlacementIntent, Boolean> isNorthEastDiagonalViolation() {
        return (((board, stonePlacementIntent) -> {
            PlayerColor playerColor = stonePlacementIntent.getPlayer().getColor();
            int row = stonePlacementIntent.getRow();
            int column = stonePlacementIntent.getColumn();
            return board.getStoneColorAt(new Intersection(row - 1, column)) != playerColor &&
                    board.getStoneColorAt(new Intersection(row, column + 1)) != playerColor &&
                    board.getStoneColorAt(new Intersection(row - 1, column + 1)) == playerColor;
        }));
    }

    private static BiFunction<Board, StonePlacementIntent, Boolean> isSouthEastDiagonalViolation() {
        return ((board, stonePlacementIntent) -> {
            PlayerColor playerColor = stonePlacementIntent.getPlayer().getColor();
            int row = stonePlacementIntent.getRow();
            int column = stonePlacementIntent.getColumn();
            return board.getStoneColorAt(new Intersection(row + 1, column)) != playerColor &&
                    board.getStoneColorAt(new Intersection(row, column + 1)) != playerColor &&
                    board.getStoneColorAt(new Intersection(row + 1, column + 1)) == playerColor;
        });
    }

    public static boolean checkWin(Board board, PlayerColor playerColor) {
        if (playerColor == PlayerColor.BLACK) {
            return hasBlackWon(board);
        }
        return hasWhiteWon(board);
    }

    /**
     * Check if white has won the match
     *
     * @return a boolean indicating if white has won
     */
    private static boolean hasWhiteWon(Board board) {
        int startingColumn = getColumnWithLeastAmountOfWhiteStonesBetweenFirstAndLast(board);
        int targetColumn = getTargetColumnFromStartingColumn(board, startingColumn);
        final Set<Intersection> intersectionsOccupiedByPlayerInEmptierColumn =
                board.getIntersectionsOccupiedByPlayerInColumn(PlayerColor.WHITE, startingColumn);
        Set<Intersection> visited = new HashSet<>();
        for (Intersection intersection : intersectionsOccupiedByPlayerInEmptierColumn) {
            return hasWhiteWonRecursive(board, intersection, visited, targetColumn);
        }
        return false;
    }

    private static int getTargetColumnFromStartingColumn(Board board, int startingColumn) {
        if (startingColumn == board.getFIRST_COLUMN())
            return board.getLAST_COLUMN();
        return board.getFIRST_COLUMN();
    }

    /**
     * Check if black has won the match
     *
     * @return a boolean indicating if black has won
     */
    private static boolean hasBlackWon(Board board) {
        int startingRow = getRowWithLeastAmountOfBlackStonesBetweenFirstAndLast(board);
        int targetRow = getTargetRowFromStartingRow(board, startingRow);
        final Set<Intersection> intersectionsOccupiedByPlayerInEmptierRow =
                board.getIntersectionsOccupiedByPlayerInRow(PlayerColor.BLACK, startingRow);
        Set<Intersection> visited = new HashSet<>();
        for (Intersection intersection : intersectionsOccupiedByPlayerInEmptierRow) {
            return hasBlackWonRecursive(board, intersection, visited, targetRow);
        }
        return false;
    }

    private static int getTargetRowFromStartingRow(Board board, int startingRow) {
        if (startingRow == board.getFIRST_ROW())
            return board.getLAST_ROW();
        return board.getFIRST_ROW();
    }

    /**
     * Recursively searches for a path of white stones connecting the vertical edges of the board
     *
     * @param currentIntersection the current {@link Intersection} analyzed
     * @param visited             the {@code Set} of already visited {@link Intersection}s
     * @param targetColumn        the column where the path should end for the method to be successful
     * @return a boolean indicating if white has won
     */
    private static boolean hasWhiteWonRecursive(Board board, Intersection currentIntersection, Set<Intersection> visited, int targetColumn) {
        visited.add(currentIntersection);
        if (currentIntersection.getColumn() == targetColumn)
            return true;
        for (Intersection adjIntersection : board.getAdjIntersections(currentIntersection)) {
            if (board.getBoardState().get(adjIntersection) == PlayerColor.WHITE && !(visited.contains(adjIntersection))) {
                if (hasWhiteWonRecursive(board, adjIntersection, visited, targetColumn)) return true;
            }
        }
        return false;
    }

    /**
     * Recursively searches for a path of black stones connecting the horizontal edges of the board
     *
     * @param currentIntersection the current {@link Intersection} analyzed
     * @param visited             the {@code Set} of already visited {@link Intersection}s
     * @param targetRow           the row where the path should end for the method to be successful
     * @return a boolean indicating if black has won
     */
    private static boolean hasBlackWonRecursive(Board board, Intersection currentIntersection, Set<Intersection> visited, int targetRow) {
        visited.add(currentIntersection);
        if (currentIntersection.getRow() == targetRow)
            return true;
        for (Intersection adjIntersection : board.getAdjIntersections(currentIntersection)) {
            if (board.getBoardState().get(adjIntersection) == PlayerColor.BLACK && !(visited.contains(adjIntersection))) {
                if (hasBlackWonRecursive(board, adjIntersection, visited, targetRow)) return true;
            }
        }
        return false;
    }

    private static int getColumnWithLeastAmountOfWhiteStonesBetweenFirstAndLast(Board board) {
        int firstColumnCount = board.getNumberOfStonesInColumnByPlayerColor(board.getFIRST_COLUMN(), PlayerColor.WHITE);
        int lastColumnCount = board.getNumberOfStonesInColumnByPlayerColor(board.getLAST_COLUMN(), PlayerColor.WHITE);
        return firstColumnCount < lastColumnCount ? board.getFIRST_COLUMN() : board.getLAST_COLUMN();
    }

    private static int getRowWithLeastAmountOfBlackStonesBetweenFirstAndLast(Board board) {
        int firstRowCount = board.getNumberOfStonesInRowByPlayerColor(board.getFIRST_ROW(), PlayerColor.WHITE);
        int lastRowCount = board.getNumberOfStonesInRowByPlayerColor(board.getLAST_ROW(), PlayerColor.WHITE);
        return firstRowCount < lastRowCount ? board.getFIRST_ROW() : board.getLAST_ROW();
    }

}
