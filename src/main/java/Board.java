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
}
