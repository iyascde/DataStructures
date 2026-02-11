package MoonTravel.main;

import java.awt.Color;
import java.util.Arrays;

import edu.ftdev.Map.MoonMap;

/**
 * Class representing a small rectangular area on the map.
 * The entire map is a grid of such areas, each at a specific row and column.
 * Each area object contains the (X, Y) pixel coordinates of its top-left corner,
 * the size of its side, the [size x size] matrix of background colors it is covering,
 * and its specific foreground color overlayed on top of the map.
 * In addition, it contains cost fields needed for specific route algorithms
 * and a reference to the previous area in the route, leading to the start endpoint of the route.
 */
public class MapArea implements Comparable<MapArea> {
    private MoonMap _moonMap;
    private int _row;
    private int _col;
    private int _origX;
    private int _origY;
    private int _size;
    private Color _color;
    private Color[][] _background;
    private double _cost;
    private double _fCost;
    private MapArea _previous;

    public MapArea() {
        _moonMap = null;
        _row = _col = -1;
        _origX = _origY = -1;
        _size = 0;
        _color = Color.BLACK;
        _background = null;
        _cost = _fCost = Double.MAX_VALUE;
        _previous = null;
    }
    
    public MapArea(MoonMap moonMap, int x, int y, int size, Color color) {
        _moonMap = moonMap;
        _size = size;
        _row = y / _size;
        _col = x / _size;
        _origX =  _col * size;
        _origY =  _row * size;
        _color = color;
        _size = size;
        _cost = _fCost = Double.MAX_VALUE;
        _previous = null;
        repaint();
    }

    // #region: [Public] accessors
    public int getRow() {
        return _row;
    }

    public int getCol() {
        return _col;
    }

    public boolean isTarget(int x, int y) {
        return (x >= _origX && x < _origX + _size) 
            && (y >= _origY && y < _origY + _size);
    }
    // #endregion: [Public] accessors

    // #region: [Public] cost-related methods
    public void resetState(double cost) {
        _cost = _fCost = cost;
        _previous = null;
    }

    public boolean newBFSNode(MapArea previous) {
        if (_previous == null) {
            _previous = previous;
            return true;
        }
        return false;
    }

    public boolean newDijkstraCost(MapArea previous) {
        double newCost = previous._cost;
        newCost += Math.sqrt(Math.pow(_row - previous._row,2) + Math.pow(_col - previous._col, 2));
        if (newCost < _cost) {
            _cost = newCost;
            _previous = previous;
            return true;
        } else {
            return false;
        }
    }

    public boolean newAStarCost(MapArea previous, MapArea target) {
        double g = Math.sqrt(Math.pow(_row - previous._row,2) + Math.pow(_col - previous._col, 2));
        double h = Math.sqrt(Math.pow(target._row - _row, 2) + Math.pow(target._col - _col, 2));
        double newCost = previous._fCost + g + h;
        if (newCost < _cost) {
            _fCost = previous._fCost + g;
            _cost = newCost;
            _previous = previous;
            return true;
        } else {
            return false;
        }
    }
    // #endregion: [Public] cost-related methods    

    // #region: [Public] rendering methods
    public void repaint() {
        _background = new Color[_size][_size];
        for(Color[] row : _background) {
            Arrays.fill(row, _color);
        }
        _moonMap.setArea(_origX, _origY, _background);
    }

    public double traceRoute(MapArea startArea, Color color) {
        if (_previous == null) {
            return Double.MAX_VALUE;
        }
        double distance = 0;
        startArea._previous = null;
        int fromX = _origX + _size / 2;
        int fromY = _origY + _size / 2;
        MapArea crt = _previous;
        while(crt != null) {
            int toX = crt._origX + _size / 2;
            int toY = crt._origY + _size / 2;
            _moonMap.drawSegment(fromX, fromY, toX, toY, 5, Color.RED);
            distance += Math.sqrt(Math.pow(toX - fromX,2) + Math.pow(toY - fromY, 2));
            _moonMap.breakStep(10);
            fromX = toX;
            fromY = toY;
            crt = crt._previous;
        }
        return distance;
    }
    // #endregion: [Public] rendering methods

    /**
     * Implementation of Comparable<MapArea>. This map area is "smaller" than another area if
     * its cost is smaller and "greater" if its cost is greater.
     */
    @Override
    public int compareTo(MapArea other) {
        return _cost < other._cost 
            ? -1
            : _cost > other._cost
                ? 1
                : 0; 
    }
}
