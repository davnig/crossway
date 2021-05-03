import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
                this.intersections.add(new Intersection(row, column, IntersectionState.EMPTY));

    }

}
