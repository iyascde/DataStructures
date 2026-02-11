package AStar.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import AStar.main.Point;

public class PointTests {
    
    @Test
    public void test_distance() {
        Point p1 = new Point("A", 1, 1);
        Point p2 = new Point("B", 2, 2);
        assertEquals(Math.sqrt(2), p1.distance(p2), 0);
        assertEquals(0, p1.distance(p1), 0);
    }

    @Test
    public void test_compareTo() {
        Point p1 = new Point("Origin", 0, 0);
        Point p2 = new Point("A", 2, 0);
        assertEquals(1, p2.compareTo(p1));
        assertEquals(-1, p1.compareTo(p2));
        assertEquals(0, p2.compareTo(p2));
    }
}
