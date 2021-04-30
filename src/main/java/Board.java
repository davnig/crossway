import java.util.ArrayList;
import java.util.List;

public class Board {

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

        for (int row = 1; row <= 19; row++)
            for (int column = 1; column <= 19; column++)
                this.intersections.add(new Intersection(row, column, IntersectionState.EMPTY));

    }

}
