package MoonTravel.main;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import edu.ftdev.Map.MoonMap;

/**
 * Class representing the entire moon map.
 * It contains color constants for the various rendering needs as well as all the MapArea objects defining
 * mapped on the moon terrain.
 */
public class MapModel {
    // routing algorithms supported by the map model
    public enum RouteAlgo {
        BFS,
        DIJKSTRA,
        ASTAR
    };

    // colors used for various rendering needs: the colors for the start and target map areas,
    // the semi-transparent color used to reflect the progression of the route algorithm exploration,
    // the color in which the final route, if found, is to be drawn.
    private final static Color _START_COLOR = Color.ORANGE;
    private final static Color _TARGET_COLOR = Color.GREEN;
    private final static Color _SEARCH_COLOR = new Color(255, 200, 0, 60);
    private final static Color _ROUTE_COLOR = Color.RED;

    // the rendering engine
    private MoonMap _moonMap;
    // the size of a side of the MapArea
    private int _areaSize;
    // a unique to be used as a marker of all impassable areas of the terrain
    private MapArea _blockedArea = new MapArea();
    // the start endpoint of the route, or null if none is defined
    private MapArea _startArea = null;
    // the target endpoint of the route, or null if none is defined
    private MapArea _targetArea = null;
    // the grid representation of the terrain. Before calculating a route, the only positions filled in the grid
    // are the impassable areas (filled with _blockedArea) and the _startArea and _targetArea. As the route gets
    // calculated, areas explored are filled with new MapArea instances, each with their calculated cost and chained
    // to their previous area leading to the start endpoint.
    private MapArea[][] _terrain;

    /**
     * Constructs the map model for the given background image and the terrain information.
     * @param moonMap
     * @param terrain
     */
    public MapModel(MoonMap moonMap, boolean[][] terrain) { 
        _moonMap = moonMap;
        _areaSize = (moonMap.getHeight() / terrain.length);
        int nRows = terrain.length;
        int nCols = terrain[0].length;
        _terrain = new MapArea[nRows][nCols];
        for (int r = 0; r < terrain.length; r++) {
            for (int c = 0; c < terrain[r].length; c++) {
                _terrain[r][c] = terrain[r][c] ? _blockedArea : null;
            }
        }
    }

    /**
     * Resets the terrain to only the _blockedArea, _startArea and _targetArea.
     */
    public void resetTerrain() {
        for (int r = 0; r < _terrain.length; r++) {
            for (int c = 0; c < _terrain[r].length; c++) {
                if (_terrain[r][c] == _blockedArea) {
                    continue;
                }
                if (_startArea != null && _startArea.getRow() == r && _startArea.getCol() == c) {
                    _terrain[r][c] = _startArea;
                    _startArea.resetState(0);
                    _startArea.repaint();
                } else if (_targetArea != null && _targetArea.getRow() == r && _targetArea.getCol() == c) {
                    _terrain[r][c] = _targetArea;
                    _targetArea.resetState(Double.MAX_VALUE);
                    _targetArea.repaint();
                } else {
                    _terrain[r][c] = null;
                }
            }
        }
    }

    /**
     * Repaints overlays for all non-blocking map areas.
     * @param name
     */
    public void repaint() {
        for (int r = 0; r < _terrain.length; r++) {
            for (int c = 0; c < _terrain[r].length; c++) {
                if (_terrain[r][c] != null && _terrain[r][c] != _blockedArea) {
                    _terrain[r][c].repaint();
                }
            }
        }
        repaintRoute();
    }

    /**
     * Repaints the route overlay linking the startArea with the targetArea.
     * @return route distance (pixel units) from _start to _target
     */
    public double repaintRoute() {
        double distance = Double.MAX_VALUE;
        if  (_startArea != null && _targetArea != null) {
            distance = _targetArea.traceRoute(_startArea, _ROUTE_COLOR);
            _startArea.repaint();
            _targetArea.repaint();
        }
        _moonMap.repaint();
        return distance;
    }

    /**
     * Resets the start and target areas based on the x,y pixel clicked on the map.
     * If the pixel falls in a pre-existent start/target area, that specific area is
     * cleared (reset to null). Otherwise, the non-configured area covering that pixel
     * is configured as start/target in that order.
     * @param x
     * @param y
     */
    public boolean resetRoute(int x, int y) {
        // make sure x and y are valid
        int row = y / _areaSize;
        int col = x / _areaSize;
        if ((row >= _terrain.length) || (col >= _terrain[0].length)) {
            // invalid click
            return false;
        } else if (_startArea != null && _startArea.isTarget(x, y)) {
            // clicked on an existent start area -> clear it
            _startArea = null;
        } else if (_targetArea != null && _targetArea.isTarget(x, y)) {
            // clicked on an existent target area -> clear it
            _targetArea = null;
        } else if (_startArea != null && _targetArea != null) {
            // clicked on some other area with both start and target defined -> clear both
            _startArea = null;
            _targetArea = null;
        } else if (_startArea == null && _terrain[row][col] != _blockedArea) {
            // clicked on an open area with no start defined -> set new startArea
            _startArea = new MapArea(_moonMap, x, y, Filters.PIXELATE_AREA_SIZE, _START_COLOR);
        } else if (_targetArea == null && _terrain[row][col] != _blockedArea) {
            // clicked on an open area with no target defined -> set new endArea
            _targetArea = new MapArea(_moonMap, x, y, _areaSize, _TARGET_COLOR);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Gets the valid, non-blocking areas neighboring a given map area.  
     * @param area map area for which to get the neighbors.
     * @return a list of all neighboring areas.
     */
    private List<MapArea> getNeighbors(MapArea area) {
        List<MapArea> next = new LinkedList<MapArea>();
        for (int oR = -1; oR <= 1; oR++) {
            for (int oC = -1; oC <= 1; oC++) {
                if (oR == 0 && oC == 0) {
                    continue;
                }
                int r = area.getRow() + oR;
                int c = area.getCol() + oC;
                if (r < 0 || r >= _terrain.length || c < 0 || c >= _terrain[r].length) {
                    continue;
                }
                if (_terrain[r][c] == null) {
                    _terrain[r][c] = new MapArea(_moonMap, c * _areaSize, r * _areaSize, _areaSize, _SEARCH_COLOR);
                    _moonMap.breakStep();
                }
                if (_terrain[r][c] != _blockedArea) {
                    next.add(_terrain[r][c]);
                }
            }
        }
        return next;
    }

     /**
     * Executes BFS route finding algorithm from start to target.
     * (ref: https://en.wikipedia.org/wiki/Breadth-first_search)
     * @return true on success, false otherwise.
     */
    public boolean routeBFS() {
        if (_startArea == null || _targetArea == null) {
            return false;
        }

        // create the working queue, add the _startArea area to the queue
        // then loop through it until either we reach the _targetArea or we exhaust the search space.
        Queue<MapArea> queue = new LinkedList<MapArea>();
        queue.add(_startArea);
        while(!queue.isEmpty()) {
            _moonMap.breakStep(1);
            MapArea crt = queue.remove();
            for(MapArea neighbor : getNeighbors(crt)) {
                if (neighbor.newBFSNode(crt)) {
                    queue.add(neighbor);
                    if (neighbor == _targetArea) {
                        return true;
                    }
                }
            }        }
        return false;
    }

    /**
     * Executes Dijkstra route finding algorithm from _startArea to _targetArea.
     * (ref: https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm)
     * @return true on success, false otherwise.
     */
    public boolean routeDijkstra() {
        if (_startArea == null || _targetArea == null) {
            return false;
        }

        PriorityQueue<MapArea> unvisited = new PriorityQueue<MapArea>();
        
        _startArea.resetState(0);
        unvisited.add(_startArea);
        boolean reached = false;
        while (!unvisited.isEmpty()) {
            _moonMap.breakStep(1);
            MapArea current = unvisited.remove();
            
            if (current == _targetArea) {
                reached = true;
            }
            
            for (MapArea neighbor : getNeighbors(current)) {
                if (neighbor.newDijkstraCost(current)) {
                    unvisited.remove(neighbor);
                    unvisited.add(neighbor);
                }
            }
        }        
        return reached;
    }

    /**
     * Executes AStar route finding algorithm from _startArea to _targetArea.
     * (ref: https://en.wikipedia.org/wiki/A*_search_algorithm)
     * @return true on success, false otherwise.
     */
    public boolean routeAStar() {
        if (_startArea == null || _targetArea == null) {
            return false;
        }

        PriorityQueue<MapArea> openSet = new PriorityQueue<MapArea>();
        
        _startArea.resetState(0);
        openSet.add(_startArea);
        
        while (!openSet.isEmpty()) {
            _moonMap.breakStep(1);
            MapArea current = openSet.remove();
           
            if (current == _targetArea) {
                return true;
            }
            
            for (MapArea neighbor : getNeighbors(current)) {
                if (neighbor.newAStarCost(current, _targetArea)) {
                    openSet.remove(neighbor);
                    openSet.add(neighbor);
                }
            }
        }
        
        return false;
    }
}
