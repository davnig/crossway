import lombok.Data;
import playerProperty.PlayerColor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class Board {

    private final int FIRST_ROW = 1;
    private final int FIRST_COLUMN = 1;
    private final int LAST_ROW = 19;
    private final int LAST_COLUMN = 19;
    private Map<Intersection, PlayerColor> boardState;


    Board() {
        initIntersections();
    }

    private void initIntersections() {
        this.boardState = new HashMap<>();
    }

    /**
     * Gets the stone color at the specified intersection.
     *
     * @param intersection the {@code Intersection}
     * @return a {@code PlayerColor}
     */
    PlayerColor getStoneColorAt(Intersection intersection) {
        if (boardState.containsKey(intersection)) {
            return boardState.get(intersection);
        }
        return PlayerColor.NONE;
    }

    /**
     * Places a stone of the given player color on the board in the specified intersection.
     *
     * @param intersection the {@code Intersection}
     * @param playerColor  the {@code PlayerColor} of the stone
     */
    public void placeStone(Intersection intersection, PlayerColor playerColor) {
        if (playerColor != PlayerColor.NONE) {
            boardState.put(intersection, playerColor);
        }
    }

    public boolean isPlacementOutOfBoardBoundaries(int row, int column) {
        return row > LAST_ROW || column > LAST_COLUMN || row < FIRST_ROW || column < FIRST_COLUMN;
    }

    public int getNumberOfStonesInColumnByPlayerColor(int column, PlayerColor playerColor) {
        return (int) boardState.entrySet().stream()
                .filter(entry -> entry.getKey().getColumn() == column &&
                        entry.getValue() == playerColor)
                .count();
    }

    public int getNumberOfStonesInRowByPlayerColor(int row, PlayerColor playerColor) {
        return (int) boardState.entrySet().stream()
                .filter(entry -> entry.getKey().getRow() == row &&
                        entry.getValue() == playerColor)
                .count();
    }

    public Set<Intersection> getIntersectionsOccupiedByPlayerInColumn(PlayerColor playerColor, int column) {
        return boardState.entrySet().stream()
                .filter(entry -> entry.getKey().getColumn() == column &&
                        entry.getValue() == playerColor)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Set<Intersection> getIntersectionsOccupiedByPlayerInRow(PlayerColor playerColor, int row) {
        return boardState.entrySet().stream()
                .filter(entry -> entry.getKey().getRow() == row &&
                        entry.getValue() == playerColor)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Set<Intersection> getAdjIntersections(Intersection intersection) {
        Set<Intersection> adjIntersections = new HashSet<>();
        int row = intersection.getRow();
        int column = intersection.getColumn();
        if (column == LAST_COLUMN && row > FIRST_ROW && row < LAST_ROW) {
            adjIntersections.add(new Intersection(row - 1, column));
            adjIntersections.add(new Intersection(row + 1, column));
            adjIntersections.add(new Intersection(row - 1, column - 1));
            adjIntersections.add(new Intersection(row, column - 1));
            adjIntersections.add(new Intersection(row + 1, column - 1));
            return adjIntersections;
        }
        if (row == LAST_ROW && column > FIRST_COLUMN && column < LAST_COLUMN) {
            adjIntersections.add(new Intersection(row - 1, column - 1));
            adjIntersections.add(new Intersection(row-1, column));
            adjIntersections.add(new Intersection(row-1, column+1));
            adjIntersections.add(new Intersection(row, column+1));
            adjIntersections.add(new Intersection(row, column-1));
            return adjIntersections;
        }
        if (column == FIRST_COLUMN && row > FIRST_ROW && row < LAST_ROW) {
            adjIntersections.add(new Intersection(row-1, column));
            adjIntersections.add(new Intersection(row+1, column));
            adjIntersections.add(new Intersection(row, column+1));
            adjIntersections.add(new Intersection(row-1, column+1));
            adjIntersections.add(new Intersection(row+1, column+1));
            return adjIntersections;
        }
        if (row == FIRST_ROW && column > FIRST_COLUMN && column < LAST_COLUMN) {
            adjIntersections.add(new Intersection(row, column+1));
            adjIntersections.add(new Intersection(row, column-1));
            adjIntersections.add(new Intersection(row+1, column));
            adjIntersections.add(new Intersection(row+1, column+1));
            adjIntersections.add(new Intersection(row+1, column-1));
            return adjIntersections;
        }
        adjIntersections.add(new Intersection(row, column + 1));
        adjIntersections.add(new Intersection(row - 1, column + 1));
        adjIntersections.add(new Intersection(row + 1, column + 1));
        adjIntersections.add(new Intersection(row - 1, column));
        adjIntersections.add(new Intersection(row + 1, column));
        adjIntersections.add(new Intersection(row - 1, column - 1));
        adjIntersections.add(new Intersection(row, column - 1));
        adjIntersections.add(new Intersection(row + 1, column - 1));
        return adjIntersections;
    }

    public boolean isLastColumn(int column) {
        return column == LAST_COLUMN;
    }

    public boolean isFirstColumn(int column) {
        return column == FIRST_COLUMN;
    }

    public boolean isLastRow(int row) {
        return row == LAST_ROW;
    }

    public boolean isFirstRow(int row) {
        return row == FIRST_ROW;
    }

    /**
     * Print the current board state to the standard out
     */
    public void printBoard() {
        printRowSeparator();
        for (int row = FIRST_ROW; row <= LAST_ROW; row++) {
            printRow(row);
        }
        printRowSeparator();
    }

    private void printRowSeparator() {
        System.out.print("-");
        for (int col = FIRST_COLUMN; col <= LAST_COLUMN; col++) {
            System.out.print("----");
        }
        System.out.println();
    }

    private void printRow(int row) {
        for (int col = FIRST_COLUMN; col <= LAST_COLUMN; col++) {
            System.out.print("| " + getPrintSymbolForIntersection(new Intersection(row, col)) + " ");
        }
        System.out.println("|");
    }

    private String getPrintSymbolForIntersection(Intersection intersection) {
        PlayerColor playerColorAtIntersection = boardState.get(intersection);
        if (playerColorAtIntersection != null) {
            switch (playerColorAtIntersection) {
                case BLACK:
                    return "O";
                case WHITE:
                    return "x";
                default:
                    return " ";
            }
        }
        return " ";
    }

}
