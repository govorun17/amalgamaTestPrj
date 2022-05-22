package amalgama.alekseymazurov.test.model;

import java.util.List;

public interface Intersection {
    BuilderIntersection add(String... bunches);

    BuilderIntersection add(List<Segment>... segmentsList);

    BuilderIntersection calculate();

    List<Segment> getIntersection();

    Double getNearPoint(Double point);

    void clear();
}
