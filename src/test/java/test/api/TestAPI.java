package test.api;

import amalgama.alekseymazurov.test.BuilderIntersection;
import amalgama.alekseymazurov.test.Intersection;
import amalgama.alekseymazurov.test.Point;
import amalgama.alekseymazurov.test.Segment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestAPI {

    static Intersection builder;

    @BeforeAll
    static void initialize() {
        builder = new BuilderIntersection();
    }

    @AfterEach
    void clean() {
        builder.clear();
    }

    @Test
    void testIntersection1() {
        List<Segment> intersection = builder.add("(-inf,10.8]u(20,+inf)").add("(-5,25.3)").calculate().getIntersection();
        List<Segment> expected = List.of(
                new Segment(
                        new Point(-5d, false, true),
                        new Point(10.8, true, false)
                ),
                new Segment(
                        new Point(20d, false, true),
                        new Point(25.3, false, false)
                )
        );
        assertEquals(expected, intersection, "answer should be (-5,10.8]u(20,25.3)");
    }

    @Test
    void testIntersection2() {
        List<Segment> intersection = builder.add("(0,5)u(5,10)").add("[5]").calculate().getIntersection();
        assertNull(intersection, "answer should be null");
    }

    @Test
    void testIntersection3() {
        List<Segment> intersection = builder
                .add("(-inf,-10]u(-5,5)u[10,+inf)")
                .add("(-inf,-100]u[0,5]u(100,+inf)")
                .add("(-inf,-50)u[1]u[100,101]")
                .calculate().getIntersection();

        List<Segment> expected = List.of(
                new Segment(
                        new Point(Double.NEGATIVE_INFINITY, false, true),
                        new Point(-100d, true, false)
                ),
                new Segment(
                        new Point(1d, true, true),
                        new Point(1d, true, false)
                ),
                new Segment(
                        new Point(100d, false, true),
                        new Point(101d, true, false)
                )
        );
        assertEquals(expected, intersection, "answer should be (-inf,-100]u[1]u(100,101]");
    }

    @Test
    void testIntersection4() {
        List<Segment> intersection = builder.add("(0,2)u[5,10]u(11,20]").add("[-5,15]").calculate().getIntersection();
        List<Segment> expected = List.of(
                new Segment(
                        new Point(0d,false, true),
                        new Point(2d, false, false)
                ),
                new Segment(
                        new Point(5d,true, true),
                        new Point(10d, true, false)
                ),
                new Segment(
                        new Point(11d,false, true),
                        new Point(15d, true, false)
                )
        );
        assertEquals(expected, intersection, "answer should be [5,10]");
    }

    @Test
    void testIntersection5() {
        List<Segment> intersection = builder.add("(0,2)u(5,10)").add("(3,4)u(5,10)").calculate().getIntersection();
        List<Segment> expected = List.of(
                new Segment(
                        new Point(5d,false, true),
                        new Point(10d, false, false)
                )
        );
        assertEquals(expected, intersection, "answer should be (5,10)");
    }

    @Test
    void testIntersection6() {
        List<Segment> intersection = builder.add("(5,10)").calculate().getIntersection();
        List<Segment> expected = List.of(
                new Segment(
                        new Point(5d,false, true),
                        new Point(10d, false, false)
                )
        );
        assertEquals(expected, intersection, "answer should be (5,10)");
    }

    @Test
    void testInPoint1() {
        Double near = builder.add("(5,10)").calculate().getNearPoint(7d);
        assertEquals(7d, near, "answer should be 7");
    }

    @Test
    void testInPoint2() {
        Double near = builder.add("(5,10)").calculate().getNearPoint(2d);
        assertEquals(5d, near, "answer should be 5");
    }

    @Test
    void testInPoint3() {
        Double near = builder.add("(5,10]").calculate().getNearPoint(11d);
        assertEquals(10d, near, "answer should be 10");
    }

    @Test
    void testInPoint4() {
        Double near = builder.add("(0,3)u[5,10)").calculate().getNearPoint(4d);
        assertEquals(5d, near, "answer should be 5");
    }

    @Test
    void testInPoint5() {
        Double near = builder.add("(0,3]u(5,10)").calculate().getNearPoint(4d);
        assertEquals(3d, near, "answer should be 3");
    }
}
