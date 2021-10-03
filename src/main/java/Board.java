import lombok.Data;
import playerProperty.PlayerColor;

import java.util.*;
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

    boolean isPlacementOutOfBoardBoundaries(int row, int column) {
        return row > LAST_ROW || column > LAST_COLUMN || row < FIRST_ROW || column < FIRST_COLUMN;
    }

    public List<Intersection> getIntersectionsOccupiedByPlayerInColumn(int column, PlayerColor playerColor) {
        return boardState.entrySet().stream()
                .filter(entry -> entry.getKey().getColumn() == column &&
                        entry.getValue() == playerColor)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Intersection> getIntersectionsOccupiedByPlayerInRow(int row, PlayerColor playerColor) {
        return boardState.entrySet().stream()
                .filter(entry -> entry.getKey().getRow() == row &&
                        entry.getValue() == playerColor)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
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
}
