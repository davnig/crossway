import exception.InvalidUserInputException;
import exception.PlacementViolationException;
import lombok.Data;
import playerProperty.PlayerColor;

import java.util.HashSet;
import java.util.List;
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
        System.out.println("New game started.\nIt's black turn.");
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
        this.board.placeStone(row, column, turn.getCurrentPlayer());
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
        return board.getStoneColorAt(row + 1, column) == oppositeColor &&
                board.getStoneColorAt(row, column - 1) == oppositeColor &&
                board.getStoneColorAt(row + 1, column - 1) == turnColor;
    }

    private boolean isNorthWestDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {
        return board.getStoneColorAt(row - 1, column) == oppositeColor &&
                board.getStoneColorAt(row, column - 1) == oppositeColor &&
                board.getStoneColorAt(row - 1, column - 1) == turnColor;
    }

    private boolean isNorthEastDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {
        return board.getStoneColorAt(row - 1, column) == oppositeColor &&
                board.getStoneColorAt(row, column + 1) == oppositeColor &&
                board.getStoneColorAt(row - 1, column + 1) == turnColor;
    }

    private boolean isSouthEastDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {
        return board.getStoneColorAt(row + 1, column) == oppositeColor &&
                board.getStoneColorAt(row, column + 1) == oppositeColor &&
                board.getStoneColorAt(row + 1, column + 1) == turnColor;
    }

    public boolean checkWinCondition(PlayerColor playerColor) {
        if (playerColor == PlayerColor.BLACK) {
            return checkBlackWinCondition();
        }
        return checkWhiteWinCondition();
    }

    private boolean checkWhiteWinCondition() {
        int columnToCheck = getEmptierColumnBetweenFirstAndLast();
        final List<Intersection> nonEmptyIntersectionsInColumn = board.getNonEmptyIntersectionsInColumn(columnToCheck, PlayerColor.WHITE);
        Set<Intersection> visited = new HashSet<>();
        if (columnToCheck == board.getFIRST_COLUMN()) {
            for (Intersection i : nonEmptyIntersectionsInColumn) {
                if (whiteRecursionMethodFromLeftToRight(i.getRow(), i.getColumn(), visited)) {
                    return true;
                }
            }
        } else {
            for (Intersection i : nonEmptyIntersectionsInColumn) {
                if (whiteRecursionMethodFromRightToLeft(i.getRow(), i.getColumn(), visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkBlackWinCondition() {
        int rowToCheck = getEmptierRowBetweenFirstAndLast();
        final List<Intersection> nonEmptyIntersectionsInRow = board.getNonEmptyIntersectionsInRow(rowToCheck, PlayerColor.BLACK);
        Set<Intersection> visited = new HashSet<>();
        for (Intersection i : nonEmptyIntersectionsInRow) {
            if (blackRecursionMethodFromDownToUp(i.getRow(), i.getColumn(), visited)) {
                return true;
            }
        }
        return false;
    }

    private int getEmptierColumnBetweenFirstAndLast() {
        int firstColumnCount = (int) board.getBoardState().entrySet().stream()
                .filter(entry -> entry.getKey().getColumn() == board.getFIRST_COLUMN() &&
                        entry.getValue() == turn.getCurrentPlayer())
                .count();
        int lastColumnCount = (int) board.getBoardState().entrySet().stream()
                .filter(entry -> entry.getKey().getColumn() == board.getLAST_COLUMN() &&
                        entry.getValue() == turn.getCurrentPlayer())
                .count();
        return firstColumnCount < lastColumnCount ? board.getFIRST_COLUMN() : board.getLAST_COLUMN();
    }

    private int getEmptierRowBetweenFirstAndLast() {
        int firstRowCount = (int) board.getBoardState().entrySet().stream()
                .filter(entry -> entry.getKey().getRow() == board.getFIRST_ROW() &&
                        entry.getValue() == turn.getCurrentPlayer())
                .count();
        int lastRowCount = (int) board.getBoardState().entrySet().stream()
                .filter(entry -> entry.getKey().getRow() == board.getLAST_ROW() &&
                        entry.getValue() == turn.getCurrentPlayer())
                .count();
        return firstRowCount < lastRowCount ? board.getFIRST_ROW() : board.getLAST_ROW();
    }

    private boolean whiteRecursionMethodFromLeftToRight(int row, int column, Set<Intersection> visited) {
        visited.add(new Intersection(row, column));
        if (column == board.getLAST_COLUMN())
            return true;
        for (Intersection intersection : board.getAdjIntersections(row, column)) {
            if(board.getBoardState().get(intersection) == PlayerColor.WHITE && !(visited.contains(intersection))) {
                return whiteRecursionMethodFromLeftToRight(intersection.getRow(), intersection.getColumn(), visited);
            }
        }
        return false;
    }

    private boolean whiteRecursionMethodFromRightToLeft(int row, int column, Set<Intersection> visited) {
        visited.add(new Intersection(row, column));
        if (column == board.getFIRST_COLUMN())
            return true;
        for (Intersection intersection : board.getAdjIntersections(row, column)) {
            if(board.getBoardState().get(intersection) == PlayerColor.WHITE && !(visited.contains(intersection))) {
                return whiteRecursionMethodFromLeftToRight(intersection.getRow(), intersection.getColumn(), visited);
            }
        }
        return false;
    }

    private boolean blackRecursionMethodFromUpToDown(int row, int column, Set<Intersection> visited) {
        visited.add(new Intersection(row, column));
        if (row == board.getFIRST_ROW())
            return true;
        for (Intersection intersection : board.getAdjIntersections(row, column)) {
            if(board.getBoardState().get(intersection) == PlayerColor.BLACK && !(visited.contains(intersection))) {
                return whiteRecursionMethodFromLeftToRight(intersection.getRow(), intersection.getColumn(), visited);
            }
        }
        return false;
    }

    private boolean blackRecursionMethodFromDownToUp(int row, int column, Set<Intersection> visited) {
        visited.add(new Intersection(row, column));
        if (row == board.getLAST_ROW())
            return true;
        for (Intersection intersection : board.getAdjIntersections(row, column)) {
            if(board.getBoardState().get(intersection) == PlayerColor.BLACK && !(visited.contains(intersection))) {
                return whiteRecursionMethodFromLeftToRight(intersection.getRow(), intersection.getColumn(), visited);
            }
        }
        return false;
    }

}

