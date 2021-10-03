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
        for (int row = 1; row <= LAST_ROW; row++)
            for (int column = 1; column <= LAST_COLUMN; column++)
                this.boardState.put(new Intersection(row, column), PlayerColor.NONE);
    }

    PlayerColor getStoneColorAt(int row, int column) {
        return boardState.get(new Intersection(row, column));
    }

    public void placeStone(int row, int column, PlayerColor playerColor) {
        boardState.put(new Intersection(row, column), playerColor);
    }

    boolean isPlacementOutOfBoardBoundaries(int row, int column) {
        return row > LAST_ROW || column > LAST_COLUMN || row < FIRST_ROW || column < FIRST_COLUMN;
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

    public List<Intersection> getNonEmptyIntersectionsInColumn(int columnToCheck, PlayerColor playerColor) {
        return boardState.entrySet().stream()
                .filter(entry -> entry.getKey().getColumn() == columnToCheck &&
                        entry.getValue() == playerColor)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Intersection> getNonEmptyIntersectionsInRow(int rowToCheck, PlayerColor playerColor) {
        return boardState.entrySet().stream()
                .filter(entry -> entry.getKey().getRow() == rowToCheck &&
                        entry.getValue() == playerColor)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
