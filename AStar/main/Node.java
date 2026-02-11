package AStar.main;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * The Node class represents a Node in a graph of Points. In addition to the  point itself,
 * a Node contains references to the neighboring nodes in the graph, and other internal state information
 * needed to track progress in route calculations.
 * Nodes can be compared to each other based on a specific heuristic applicable to the routing algorithm (i.e. A*)
 */
public class Node implements Comparable<Node> {

    // Point object (data) stored in this node. Each point contains a _label and _x, _y coordinates.
    private Point _point;
    // Map of nodes neighboring this node, indexed by the labels of their _point.
    private Map<String, Node> _neighbors;
    // Internal state of the node: null (if node was not visited) or reference to the previous node that reached to this node.
    private Node _previous;
    // Accumulated distance from the beginning of the route: sum of the lengths of all edges along the route from start to this node.
    private double _distanceSoFar;

    public double getDistanceSoFar(){
        return _distanceSoFar;
    }
    public void setDistanceSoFar(double distance){
        this._distanceSoFar = distance;
    }
    
    public Node(Point data) {
        _point = data;
        _neighbors = new HashMap<String, Node>();
        _previous = null;
        _distanceSoFar = 0;
    }
    
    public Point getPoint() {
        return _point;
    }


    public String getLabel() {
        return _point.getLabel();
    }
        
    public Node getState() {
        return _previous;
    }

    public void setState(Node previous) {
        _previous = previous;
        if(_previous != null)
        _distanceSoFar = previous._distanceSoFar + _point.distance(previous._point);
    }
    
    // Add a method to set state without recalculating distance
    public void setStateWithoutDistance(Node previous) {
        _previous = previous;
    }

    public void addNeighbor(Node otherNode) {
        _neighbors.put(otherNode.getLabel(), otherNode);
    }

    public void removeNeighbor(Node otherNode) {
        _neighbors.remove(otherNode.getLabel());
    }

    public Collection<Node> getNeighbors() {
        return new LinkedList<Node>(_neighbors.values());
    }
    
   @Override
    public String toString() {
        String output = _point.toString() + " > ";
        boolean first = true;
        for(Node n : _neighbors.values()) {
            if (!first) {
                output += " ";
            }
            output += n.getLabel();
            first = false;
        }
        return output;
    }

    @Override
    public int compareTo(Node other) {
        return this._point.compareTo(other._point);
    }
}
