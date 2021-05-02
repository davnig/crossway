import java.util.ArrayList;
import java.util.List;

public class Board {

    final int MAX_ROW = 19;
    final int MAX_COLUMN = 19;
    List<Intersection> intersections;


    public Board() {
        initIntersections();
    }


    public boolean isEmpty() {
        return intersections.stream()
                .allMatch(intersection -> intersection.state.equals(IntersectionState.EMPTY));
    }

    void initIntersections() {

        this.intersections = new ArrayList<>();

        for (int row = 1; row <= MAX_ROW; row++)
            for (int column = 1; column <= MAX_COLUMN; column++)
                this.intersections.add(new Intersection(row, column, IntersectionState.EMPTY));

    }

}
