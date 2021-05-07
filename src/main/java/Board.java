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

    boolean isLastMoveDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {

        if (isFirstRowPosition(row))
            return isSouthDiagonalViolation(row, column, turnColor, oppositeColor);

        if (isLastRowPosition(row))
            return isNorthDiagonalViolation(row, column, turnColor, oppositeColor);

        if (isFirstColumnPosition(column))
            return isEastDiagonalViolation(row, column, turnColor, oppositeColor);

        if (isLastColumnPosition(column))
            return isWestDiagonalViolation(row, column, turnColor, oppositeColor);

        return
                isNorthDiagonalViolation(row, column, turnColor, oppositeColor) ||
                        isSouthDiagonalViolation(row, column, turnColor, oppositeColor) ||
                        isWestDiagonalViolation(row, column, turnColor, oppositeColor) ||
                        isEastDiagonalViolation(row, column, turnColor, oppositeColor);


    }


    private boolean isWestDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {

        if (getStoneColorAt(row, column + 1) != oppositeColor)
            return false;

        if (getStoneColorAt(row - 1, column) == oppositeColor
                && getStoneColorAt(row - 1, column + 1) == turnColor)
            return true;

        return getStoneColorAt(row + 1, column) == oppositeColor
                && getStoneColorAt(row + 1, column + 1) == turnColor;

    }

    private boolean isEastDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {

        if (getStoneColorAt(row, column - 1) != oppositeColor)
            return false;

        if (getStoneColorAt(row - 1, column) == oppositeColor
                && getStoneColorAt(row - 1, column - 1) == turnColor)
            return true;

        return getStoneColorAt(row + 1, column) == oppositeColor
                && getStoneColorAt(row + 1, column - 1) == turnColor;

    }

    private boolean isNorthDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {

        if (getStoneColorAt(row - 1, column) != oppositeColor)
            return false;

        if (getStoneColorAt(row, column - 1) == oppositeColor
                && getStoneColorAt(row - 1, column - 1) == turnColor)
            return true;

        return getStoneColorAt(row, column + 1) == oppositeColor
                && getStoneColorAt(row - 1, column + 1) == turnColor;

    }

    private boolean isSouthDiagonalViolation(int row, int column, PlayerColor turnColor, PlayerColor oppositeColor) {

        if (getStoneColorAt(row + 1, column) != oppositeColor)
            return false;

        if (getStoneColorAt(row, column - 1) == oppositeColor
                && getStoneColorAt(row + 1, column - 1) == turnColor)
            return true;

        return getStoneColorAt(row, column + 1) == oppositeColor
                && getStoneColorAt(row + 1, column + 1) == turnColor;

    }

    private boolean isLastColumnPosition(int column) {
        return column == MAX_COLUMN;
    }

    private boolean isFirstColumnPosition(int column) {
        return column == 1;
    }

    private boolean isLastRowPosition(int row) {
        return row == getMAX_ROW();
    }

    private boolean isFirstRowPosition(int row) {
        return row == 1;
    }
}
