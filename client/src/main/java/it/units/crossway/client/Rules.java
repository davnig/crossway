package it.units.crossway.client;

import it.units.crossway.client.exception.PlacementViolationException;
import it.units.crossway.client.model.*;
import org.javatuples.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class Rules {

    static boolean isPieRuleAccepted = false;

    public static void applyPieRule(Player player, Turn turn) {
        if (player.getColor().equals(PlayerColor.WHITE)) {
            player.setColor(PlayerColor.BLACK);
        } else {
            player.setColor(PlayerColor.WHITE);
        }
        turn.setTurnColor(PlayerColor.WHITE);
        isPieRuleAccepted = true;
    }

    public static boolean isPieRuleNotAlreadyAccepted() {
        return !isPieRuleAccepted;
    }

    public static boolean isPieRuleTurn(Turn turn) {
        return turn.getTurnNumber() == 2;
    }

    public static boolean couldExistsWinner(Turn turn) {
        return turn.getTurnNumber() >= Board.LAST_ROW;
    }

    public static void validatePlacementIntent(Board board, StonePlacementIntent stonePlacementIntent)
            throws PlacementViolationException {
        if (board.isIntersectionOccupied(stonePlacementIntent.getRow(), stonePlacementIntent.getColumn()))
            throw new PlacementViolationException("Placement not allowed: intersection already occupied");
        if (board.isPlacementOutOfBoardBoundaries(stonePlacementIntent.getRow(), stonePlacementIntent.getColumn()))
            throw new PlacementViolationException("Placement not allowed: out of board");
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
            PlayerColor oppositeColor = playerColor.getOpposite();
            int row = stonePlacementIntent.getRow();
            int column = stonePlacementIntent.getColumn();
            return board.getStoneColorAt(row + 1, column) == oppositeColor &&
                    board.getStoneColorAt(row, column - 1) == oppositeColor &&
                    board.getStoneColorAt(row + 1, column - 1) == playerColor;
        }));
    }

    private static BiFunction<Board, StonePlacementIntent, Boolean> isNorthWestDiagonalViolation() {
        return (((board, stonePlacementIntent) -> {
            PlayerColor playerColor = stonePlacementIntent.getPlayer().getColor();
            PlayerColor oppositeColor = playerColor.getOpposite();
            int row = stonePlacementIntent.getRow();
            int column = stonePlacementIntent.getColumn();
            return board.getStoneColorAt(row - 1, column) == oppositeColor &&
                    board.getStoneColorAt(row, column - 1) == oppositeColor &&
                    board.getStoneColorAt(row - 1, column - 1) == playerColor;
        }));
    }

    private static BiFunction<Board, StonePlacementIntent, Boolean> isNorthEastDiagonalViolation() {
        return (((board, stonePlacementIntent) -> {
            PlayerColor playerColor = stonePlacementIntent.getPlayer().getColor();
            PlayerColor oppositeColor = playerColor.getOpposite();
            int row = stonePlacementIntent.getRow();
            int column = stonePlacementIntent.getColumn();
            return board.getStoneColorAt(row - 1, column) == oppositeColor &&
                    board.getStoneColorAt(row, column + 1) == oppositeColor &&
                    board.getStoneColorAt(row - 1, column + 1) == playerColor;
        }));
    }

    private static BiFunction<Board, StonePlacementIntent, Boolean> isSouthEastDiagonalViolation() {
        return ((board, stonePlacementIntent) -> {
            PlayerColor playerColor = stonePlacementIntent.getPlayer().getColor();
            PlayerColor oppositeColor = playerColor.getOpposite();
            int row = stonePlacementIntent.getRow();
            int column = stonePlacementIntent.getColumn();
            return board.getStoneColorAt(row + 1, column) == oppositeColor &&
                    board.getStoneColorAt(row, column + 1) == oppositeColor &&
                    board.getStoneColorAt(row + 1, column + 1) == playerColor;
        });
    }

    public static boolean checkWin(Board board, PlayerColor playerColor) {
        if (playerColor == PlayerColor.BLACK) {
            return hasBlackWon(board);
        }
        return hasWhiteWon(board);
    }

    private static boolean hasWhiteWon(Board board) {
        int startingColumn = getColumnWithLeastAmountOfWhiteStonesBetweenFirstAndLast(board);
        int targetColumn = getTargetColumnFromStartingColumn(startingColumn);
        final Set<Pair<Integer, Integer>> intersectionsOccupiedByPlayerInEmptierColumn =
                board.getIntersectionsOccupiedByPlayerInColumn(PlayerColor.WHITE, startingColumn);
        Set<Pair<Integer, Integer>> visited = new HashSet<>();
        for (Pair<Integer, Integer> intersection : intersectionsOccupiedByPlayerInEmptierColumn) {
            return hasWhiteWonRecursive(board, intersection, visited, targetColumn);
        }
        return false;
    }

    private static int getTargetColumnFromStartingColumn(int startingColumn) {
        if (startingColumn == Board.FIRST_COLUMN)
            return Board.LAST_COLUMN;
        return Board.FIRST_COLUMN;
    }

    private static boolean hasBlackWon(Board board) {
        int startingRow = getRowWithLeastAmountOfBlackStonesBetweenFirstAndLast(board);
        int targetRow = getTargetRowFromStartingRow(startingRow);
        final Set<Pair<Integer, Integer>> intersectionsOccupiedByPlayerInEmptierRow =
                board.getIntersectionsOccupiedByPlayerInRow(PlayerColor.BLACK, startingRow);
        Set<Pair<Integer, Integer>> visited = new HashSet<>();
        for (Pair<Integer, Integer> intersection : intersectionsOccupiedByPlayerInEmptierRow) {
            return hasBlackWonRecursive(board, intersection, visited, targetRow);
        }
        return false;
    }

    private static int getTargetRowFromStartingRow(int startingRow) {
        if (startingRow == Board.FIRST_ROW)
            return Board.LAST_ROW;
        return Board.FIRST_ROW;
    }

    private static boolean hasWhiteWonRecursive(Board board, Pair<Integer, Integer> currentIntersection, Set<Pair<Integer, Integer>> visited, int targetColumn) {
        visited.add(currentIntersection);
        if (currentIntersection.getValue1() == targetColumn)
            return true;
        for (Pair<Integer, Integer> adjIntersection : board.getAdjIntersections(currentIntersection)) {
            if (board.getStoneColorAtIntersection(adjIntersection) == PlayerColor.WHITE && !(visited.contains(adjIntersection))) {
                if (hasWhiteWonRecursive(board, adjIntersection, visited, targetColumn)) return true;
            }
        }
        return false;
    }

    private static boolean hasBlackWonRecursive(Board board, Pair<Integer, Integer> currentIntersection, Set<Pair<Integer, Integer>> visited, int targetRow) {
        visited.add(currentIntersection);
        if (currentIntersection.getValue0() == targetRow)
            return true;
        for (Pair<Integer, Integer> adjIntersection : board.getAdjIntersections(currentIntersection)) {
            if (board.getStoneColorAtIntersection(adjIntersection) == PlayerColor.BLACK && !(visited.contains(adjIntersection))) {
                if (hasBlackWonRecursive(board, adjIntersection, visited, targetRow)) return true;
            }
        }
        return false;
    }

    private static int getColumnWithLeastAmountOfWhiteStonesBetweenFirstAndLast(Board board) {
        int firstColumnCount = board.getNumberOfStonesInColumnByPlayerColor(Board.FIRST_COLUMN, PlayerColor.WHITE);
        int lastColumnCount = board.getNumberOfStonesInColumnByPlayerColor(Board.LAST_COLUMN, PlayerColor.WHITE);
        return firstColumnCount < lastColumnCount ? Board.FIRST_COLUMN : Board.LAST_COLUMN;
    }

    private static int getRowWithLeastAmountOfBlackStonesBetweenFirstAndLast(Board board) {
        int firstRowCount = board.getNumberOfStonesInRowByPlayerColor(Board.FIRST_ROW, PlayerColor.WHITE);
        int lastRowCount = board.getNumberOfStonesInRowByPlayerColor(Board.LAST_ROW, PlayerColor.WHITE);
        return firstRowCount < lastRowCount ? Board.FIRST_ROW : Board.LAST_ROW;
    }

    public static boolean areThereEmptyAndValidIntersections(Player player, Board board) {
        for (Pair<Integer, Integer> intersection: board.getEmptyIntersections()) {
            int row = intersection.getValue0();
            int column = intersection.getValue1();
            StonePlacementIntent stonePlacementIntent = new StonePlacementIntent(row, column, player);
            if (!isDiagonalViolation(board, stonePlacementIntent)) {
                return true;
            }
        }
        return false;
    }
}
