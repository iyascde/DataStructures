package AStar.main;

/**
 * The Point class represents a generic X,Y point in a 2-D Euclidian space.
 * A point is identifiable by a label (string) and can be compared to 
 * each other based on their distance to the (0, 0) origin.
 */
public class Point implements Comparable<Point> {
    // Constant static field representing the origin of the coordinate system
    private final static Point _ORIGIN = new Point("Origin", 0, 0);

    // Label of this point
    private String _label;
    // X Coordinate of this point
    private int _x;
    // Y Coordinate of this point
    private int _y;

    public Point(String label, int x, int y) {
        _label = label;
        _x = x;
        _y = y;
    }

    public String getLabel() {
        return _label;
    }

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }

    // Calculate the euclidian distance between this point an another point
    public double distance(Point other) {
        return Math.sqrt(Math.pow(_x - other._x, 2) + Math.pow(_y - other._y, 2));
    }

    // Compares this point to another point. A point is "smaller" than another point
    // if its euclidian distance to the origin (0, 0) is shorter than the same distance of the other point.
    @Override
    public int compareTo(Point other) {
        return (int)Math.signum(this.distance(_ORIGIN) - other.distance(_ORIGIN));
    }

    @Override
    public String toString() {
        return String.format("%s : %d,%d", _label, _x, _y);
    }
}
