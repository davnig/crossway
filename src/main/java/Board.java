import lombok.Data;
import playerProperty.PlayerColor;

import java.util.HashMap;
import java.util.Map;

@Data
public class Board {

    private final int MAX_ROW = 19;
    private final int MAX_COLUMN = 19;
    private Map<Intersection, PlayerColor> boardState;


    Board() {
        initIntersections();
    }

    private void initIntersections() {

        this.boardState = new HashMap<>();

        for (int row = 1; row <= MAX_ROW; row++)
            for (int column = 1; column <= MAX_COLUMN; column++)
                this.boardState.put(new Intersection(row, column), PlayerColor.NONE);

    }
/*
    Intersection getIntersectionAt(int row, int column) {

        return this.boardState.get(new Intersection(row,column));

    }*/

    PlayerColor getStoneColorAt(int row, int column) {
        return boardState.get(new Intersection(row, column));
    }

    boolean isPlacementOutOfBoardBoundaries(int row, int column) {
        return row > getMAX_ROW() || column > getMAX_COLUMN() || row < 1 || column < 1;
    }

    boolean isLastMoveDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {

        if (isFirstRow(row))
            return isSouthEastDiagonalViolation(row, column, turnColor, oppositeColor) ||
                    isSouthWestDiagonalViolation(row, column, turnColor, oppositeColor);

        if (isLastRow(row))
            return isNorthEastDiagonalViolation(row, column, turnColor, oppositeColor) ||
                    isNorthWestDiagonalViolation(row, column, turnColor, oppositeColor);

        if (isFirstColumn(column))
            return isNorthEastDiagonalViolation(row, column, turnColor, oppositeColor) ||
                    isSouthEastDiagonalViolation(row, column, turnColor, oppositeColor);

        if (isLastColumn(column))
            return isNorthWestDiagonalViolation(row, column, turnColor, oppositeColor) ||
                    isSouthWestDiagonalViolation(row, column, turnColor, oppositeColor);

        return
                isNorthEastDiagonalViolation(row, column, turnColor, oppositeColor) ||
                        isSouthEastDiagonalViolation(row, column, turnColor, oppositeColor) ||
                        isSouthWestDiagonalViolation(row, column, turnColor, oppositeColor) ||
                        isNorthWestDiagonalViolation(row, column, turnColor, oppositeColor);

    }


    private boolean isSouthWestDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {

        return getStoneColorAt(row + 1, column) == oppositeColor &&
                getStoneColorAt(row, column - 1) == oppositeColor &&
                getStoneColorAt(row + 1, column - 1) == turnColor;

    }

    private boolean isNorthWestDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {

        return getStoneColorAt(row - 1, column) == oppositeColor &&
                getStoneColorAt(row, column - 1) == oppositeColor &&
                getStoneColorAt(row - 1, column - 1) == turnColor;

    }

    private boolean isNorthEastDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {

        return getStoneColorAt(row - 1, column) == oppositeColor &&
                getStoneColorAt(row, column + 1) == oppositeColor &&
                getStoneColorAt(row - 1, column + 1) == turnColor;

    }

    private boolean isSouthEastDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {

        return getStoneColorAt(row + 1, column) == oppositeColor &&
                getStoneColorAt(row, column + 1) == oppositeColor &&
                getStoneColorAt(row + 1, column + 1) == turnColor;

    }

    private boolean isLastColumn(int column) {
        return column == MAX_COLUMN;
    }

    private boolean isFirstColumn(int column) {
        return column == 1;
    }

    private boolean isLastRow(int row) {
        return row == getMAX_ROW();
    }

    private boolean isFirstRow(int row) {
        return row == 1;
    }

}
