import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Board {

    private final int MAX_ROW = 19;
    private final int MAX_COLUMN = 19;
    private List<Intersection> intersections;


    Board() {
        initIntersections();
    }

    private void initIntersections() {

        this.intersections = new ArrayList<>();

        for (int row = 1; row <= MAX_ROW; row++)
            for (int column = 1; column <= MAX_COLUMN; column++)
                this.intersections.add(new Intersection(row, column, PlayerColor.NONE));

    }

    Intersection getIntersectionAt(int row, int column) {

        return this.intersections.stream()
                .filter(intersection -> intersection.getRow() == row && intersection.getColumn() == column)
                .collect(Collectors.toList())
                .get(0);

    }

    PlayerColor getStoneColorAt(int row, int column) {
        return getIntersectionAt(row, column).getStone();
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
