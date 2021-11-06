package it.units.crossway.client.model;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class Board {

    public static int FIRST_ROW = 1;
    public static int FIRST_COLUMN = 1;
    public static int LAST_ROW = 19;
    public static int LAST_COLUMN = 19;
    private Map<Pair<Integer, Integer>, PlayerColor> boardState;


    public Board() {
        initIntersections();
    }

    private void initIntersections() {
        this.boardState = new HashMap<>();
    }

    /**
     * Gets the color of the stone located at the intersection identified by the
     * given row and column numbers.
     *
     * @param row    the row number of the intersection
     * @param column the column number of the intersection
     * @return a {@link PlayerColor}
     */
    public PlayerColor getStoneColorAt(int row, int column) {
        Pair<Integer, Integer> intersection = new Pair<>(row, column);
        if (boardState.containsKey(intersection)) {
            return boardState.get(intersection);
        }
        return PlayerColor.NONE;
    }

    public PlayerColor getStoneColorAtIntersection(Pair<Integer, Integer> intersection) {
        if (boardState.containsKey(intersection)) {
            return boardState.get(intersection);
        }
        return PlayerColor.NONE;
    }

    /**
     * Places a stone of the given player color on the board in the intersection identified
     * by the given row and column numbers.
     *
     * @param row         the row number of the intersection
     * @param column      the column number of the intersection
     * @param playerColor the {@link PlayerColor} of the stone
     */
    public void placeStone(int row, int column, PlayerColor playerColor) {
        if (playerColor != PlayerColor.NONE) {
            boardState.put(new Pair<>(row, column), playerColor);
        }
    }

    public boolean isPlacementOutOfBoardBoundaries(int row, int column) {
        return row > LAST_ROW || column > LAST_COLUMN || row < FIRST_ROW || column < FIRST_COLUMN;
    }

    public int getNumberOfStonesInColumnByPlayerColor(int column, PlayerColor playerColor) {
        return (int) boardState.entrySet().stream()
                .filter(entry -> entry.getKey().getValue1() == column &&
                        entry.getValue() == playerColor)
                .count();
    }

    public int getNumberOfStonesInRowByPlayerColor(int row, PlayerColor playerColor) {
        return (int) boardState.entrySet().stream()
                .filter(entry -> entry.getKey().getValue0() == row &&
                        entry.getValue() == playerColor)
                .count();
    }

    public Set<Pair<Integer, Integer>> getIntersectionsOccupiedByPlayerInColumn(PlayerColor playerColor, int column) {
        return boardState.entrySet().stream()
                .filter(entry -> entry.getKey().getValue1() == column &&
                        entry.getValue() == playerColor)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Set<Pair<Integer, Integer>> getIntersectionsOccupiedByPlayerInRow(PlayerColor playerColor, int row) {
        return boardState.entrySet().stream()
                .filter(entry -> entry.getKey().getValue0() == row &&
                        entry.getValue() == playerColor)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Set<Pair<Integer, Integer>> getAdjIntersections(Pair<Integer, Integer> intersection) {
        Set<Pair<Integer, Integer>> adjIntersections = new HashSet<>();
        int row = intersection.getValue0();
        int column = intersection.getValue1();
        if (column == LAST_COLUMN && row > FIRST_ROW && row < LAST_ROW) {
            adjIntersections.add(new Pair<>(row - 1, column));
            adjIntersections.add(new Pair<>(row + 1, column));
            adjIntersections.add(new Pair<>(row - 1, column - 1));
            adjIntersections.add(new Pair<>(row, column - 1));
            adjIntersections.add(new Pair<>(row + 1, column - 1));
            return adjIntersections;
        }
        if (row == LAST_ROW && column > FIRST_COLUMN && column < LAST_COLUMN) {
            adjIntersections.add(new Pair<>(row - 1, column - 1));
            adjIntersections.add(new Pair<>(row - 1, column));
            adjIntersections.add(new Pair<>(row - 1, column + 1));
            adjIntersections.add(new Pair<>(row, column + 1));
            adjIntersections.add(new Pair<>(row, column - 1));
            return adjIntersections;
        }
        if (column == FIRST_COLUMN && row > FIRST_ROW && row < LAST_ROW) {
            adjIntersections.add(new Pair<>(row - 1, column));
            adjIntersections.add(new Pair<>(row + 1, column));
            adjIntersections.add(new Pair<>(row, column + 1));
            adjIntersections.add(new Pair<>(row - 1, column + 1));
            adjIntersections.add(new Pair<>(row + 1, column + 1));
            return adjIntersections;
        }
        if (row == FIRST_ROW && column > FIRST_COLUMN && column < LAST_COLUMN) {
            adjIntersections.add(new Pair<>(row, column + 1));
            adjIntersections.add(new Pair<>(row, column - 1));
            adjIntersections.add(new Pair<>(row + 1, column));
            adjIntersections.add(new Pair<>(row + 1, column + 1));
            adjIntersections.add(new Pair<>(row + 1, column - 1));
            return adjIntersections;
        }
        adjIntersections.add(new Pair<>(row, column + 1));
        adjIntersections.add(new Pair<>(row - 1, column + 1));
        adjIntersections.add(new Pair<>(row + 1, column + 1));
        adjIntersections.add(new Pair<>(row - 1, column));
        adjIntersections.add(new Pair<>(row + 1, column));
        adjIntersections.add(new Pair<>(row - 1, column - 1));
        adjIntersections.add(new Pair<>(row, column - 1));
        adjIntersections.add(new Pair<>(row + 1, column - 1));
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

    public void resetBoard() {
        FIRST_ROW = 1;
        FIRST_COLUMN = 1;
        LAST_ROW = 19;
        LAST_COLUMN = 19;
    }

    public boolean isIntersectionOccupied(int row, int column) {
        return !getStoneColorAt(row, column).equals(PlayerColor.NONE);
    }

    public String getAsString(Player player) {
        StringJoiner sj = new StringJoiner("");
        sj.add(constructBoardLegend(player));
        String rowSeparator = constructRowSeparator();
        sj.add(rowSeparator);
        IntStream.range(Board.FIRST_ROW, Board.LAST_ROW + 1)
                .forEach(row -> {
                    sj.add(String.valueOf(row));
                    if (row < 10) {
                        sj.add("  ");
                    } else {
                        sj.add(" ");
                    }
                    sj.add(constructRow(row));
                });
        sj.add(rowSeparator);
        sj.add(constructColumnEnumeration());
        return sj.toString();
    }

    private String constructColumnEnumeration() {
        StringJoiner sj = new StringJoiner("");
        sj.add("     ");
        IntStream.range(Board.FIRST_COLUMN, Board.LAST_COLUMN + 1)
                .forEach(col -> {
                    sj.add(String.valueOf(col));
                    if (col < 10) {
                        sj.add("   ");
                    } else {
                        sj.add("  ");
                    }
                });
        sj.add("\n\n");
        return sj.toString();
    }

    private String constructRow(int row) {
        return IntStream.range(Board.FIRST_COLUMN, Board.LAST_COLUMN + 1)
                .mapToObj(col -> "| " + getStoneColorAt(row, col).asSymbol() + " ")
                .collect(Collectors.joining()) +
                "| \n";
    }

    private String constructBoardLegend(Player player) {
        return Arrays.stream(PlayerColor.values())
                .sorted()
                .filter(color -> !color.equals(PlayerColor.NONE))
                .map(color -> color.asSymbol() + " --> " + color + " stones" +
                        (player.getColor().equals(color) ? " (you)\n" : "\n"))
                .collect(Collectors.joining());
    }

    private String constructRowSeparator() {
        StringJoiner sj = new StringJoiner("");
        sj.add("   -");
        IntStream.range(Board.FIRST_ROW, Board.LAST_ROW + 1)
                .forEach(i -> sj.add("----"));
        sj.add(" \n");
        return sj.toString();
    }
}
