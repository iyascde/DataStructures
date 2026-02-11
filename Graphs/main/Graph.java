package Graphs.main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class definition for a generic (Directed) Graph.
 * The Graph contains a collection of Nodes, each Node contains
 * a collection of references (edges) to their neighboring Nodes.
 * @param <T> - reference type of Nodes contained in the Graph.
 * The type T is expected to implement the Comparable interface, 
 * such that Nodes can be compared to each other.<br>
 * E.g.:<pre>Graph&lt;String&gt; g = new Graph&ltString&gt();</pre>
 * @see Node
 */
public class Graph<T extends Comparable<T>> {

    /**
     * Private Map keying each Node in the Graph by the hashCode of its data
     * E.g: Given <pre>Node<String> n = new Node<String>("abc");</pre> added to the graph,
     * the _nodes map contains a Map.Entry with
     * <pre>key="abc".hashCode()<br>value=n<pre>
     * @see java.lang.Object#hashCode()
     */
    private Map<Integer, Node<T>> _nodes;
    
    /**
     * Constructs a new Graph as an empty container fit for Nodes of the type T.
     * @see Graph
     * @see Node
     */
    public Graph() {
        _nodes = new TreeMap<Integer, Node<T>>();
    }
    
    /**
     * Gets the size of this Graph. The size of the Graph is equal to the number
     * of Nodes it contains.
     * @return number of Nodes in this Graph.
     */
    public int size() {
        return _nodes.size();
    }
    
    /**
     * Checks if the state of all the Nodes in the Graph matches a given value.
     * @param state - the value to check against all Nodes in the Graph.
     * @return true if all the Nodes in the Graph have a state matching the
     * given value, false otherwise.
     * @see Node#getState()
     */
    public boolean checkState(int state) {
        for (Node<?> n : _nodes.values()) {
            if (state != n.getState()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Adds a new Node to the Graph containing the <i>data</i>. The method 
     * throws if the Graph already contains a Node with data having the same
     * hashCode().
     * @param data - the data reference (of type T) contained in the new Node.
     * @throws RuntimeException if the Graph already contains a Node for the
     * given data.
     * @see java.lang.Object#hashCode()
     */
    public void addNode(T data) {
        int nodeHash = data.hashCode();
        if (_nodes.containsKey(nodeHash)) {
            throw new RuntimeException("Ambiguous graph!");
        }
        
        _nodes.put(nodeHash, new Node<T>(data));
    }
    
    /**
     * Adds a new directed Edge to the Graph, linking the Nodes containing
     * <i>from</i> and <i>to</i> data. It is expected the two Nodes exist
     * otherwise the method throws an exception.
     * @param from - Node where the Edge is starting.
     * @param to - Node where the Edge is ending.
     * @throws RuntimeException if either of the two Nodes are not present in the Graph.
     * @see Node
     * @see Graph#removeEdge(Comparable, Comparable)
     */
    public void addEdge(T from, T to) {
        Node<T> fromNode = _nodes.get(from.hashCode());
        Node<T> toNode = _nodes.get(to.hashCode());
        if (fromNode == null || toNode == null) {
            throw new RuntimeException("Node(s) not in the graph!");
        }
        
        fromNode.addEdge(toNode);
    }
    
    /**
     * Removes an existent directed Edge from the Graph, if one exists. 
     * The Edge to be removed is linking the nodes containing the <i>from</i> 
     * and <i>to</i> data references. The two Nodes must exist, otherwise the 
     * method throws an exception.
     * @param from - Node at the starting point of the Edge.
     * @param to - Node at the ending point of the Edge.
     * @throws RuntimeException if either of the two Nodes are not present in the Graph.
     * @see Node
     * @see Graph#addEdge(Comparable, Comparable)
     */
    public void removeEdge(T from, T to) {
        Node <T> fromNode = _nodes.get(from.hashCode());
        Node <T> toNode = _nodes.get(to.hashCode());
        if(fromNode == null || toNode == null) { 
            throw new RuntimeException("Node(s) not in the graph!");
        }
        fromNode.removeEdge(toNode);
    }
    
    /**
     * Removes a Node from the Graph if one exists, along with all
     * its outgoing (egress) and incoming (ingress) edges. If there
     * is no Node hosting the <i>data</i> reference the method does
     * nothing.
     * @param data - Node to be removed from the Graph.
     */
    public void removeNode(T data) {
        Node<T> node = _nodes.get(data.hashCode());
        if(node == null){
            return;
        } else {
            for(Node<T> n : _nodes.values()) {
                n.removeEdge(node);
            }
            _nodes.remove(data.hashCode());
        }
    }



    /**
     * Checks if the Graph is undirected.
     * @return true if Graph is undirected, false otherwise.
     */
    public boolean isUGraph() {
        for(Node<T> n : _nodes.values()){
            for(Node<T> m : n.getEdges().values()){
                if(!m.getEdges().containsValue(n)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks is the Graph is connected.
     * @return true if the Graph is connected, false otherwise.
     */
    public boolean isConnected() {
        for(Node<T> n : _nodes.values()){
            n.reset();
        }
        if(_nodes.isEmpty()){
            return true;
        }
        
        int count = 0;
        
        Stack<Node<T>> stack = new Stack<Node<T>>();
        Node<T> start = _nodes.values().iterator().next();
        stack.push(start);
        while(!stack.isEmpty()){
            Node<T> current = stack.pop();
            if(current.getState() != 0){
                continue;
            }
            current.setState();
            
            count++;
            for(Node<T> n : current.getEdges().values()){
                if(n.getState() == 0){
                    stack.push(n);
                }
            }
        }

        return count == _nodes.size();
    }

    /**
     * Checks if the Graph is Directed Acyclic graph.
     * @return true if Graph is Directed Acyclic, false otherwise.
     */
    public boolean isDAGraph() {
        for (Node<T> n : _nodes.values()) {
            n.reset();
        }
    
        Set<Node<T>> recursionStack = new HashSet<>();
    
        for (Node<T> node : _nodes.values()) {
            if (node.getState() == 0) { 
                if (hasCycle(node, recursionStack)) {
                    return false; 
                }
            }
        }
    
        return true; 
    }
    
    private boolean hasCycle(Node<T> node, Set<Node<T>> recursionStack) {
        node.setState();
        recursionStack.add(node);
    
        for (Node<T> neighbor : node.getEdges().values()) {
            if (neighbor.getState() == 0) { 
                if (hasCycle(neighbor, recursionStack)) {
                    return true; 
                }
            } else if (recursionStack.contains(neighbor)) {
                return true; 
            }
        }
    
        
        recursionStack.remove(node);
        
        return false; 
    }

    /**
     * Generates the adjacency matrix for this Graph.
     * @return the adjacency matrix.
     */
    public int[][] getAdjacencyMatrix() {
        int size = _nodes.size();
        int[][] adjMatrix = new int[size][size];
        Map<Node<T>, Integer> nodeIndexMap = new HashMap<>();
        int index = 0;
        for (Node<T> node : _nodes.values()) {
            nodeIndexMap.put(node, index++);
        }
        
        for(Node<T> node : _nodes.values()){
            for(Node<T> neighbor : node.getEdges().values()){
                adjMatrix[nodeIndexMap.get(node)][nodeIndexMap.get(neighbor)] = 1;
            }
        }

        return adjMatrix;
    }

    public Map<Node<T>, Integer> getNodeIndexMap(){
        Map<Node<T>, Integer> nodeIndexMap = new HashMap<>();
        int index = 0;
        for (Node<T> node : _nodes.values()) {
            nodeIndexMap.put(node, index++);
        }
        return nodeIndexMap;
    }

    public Map<Integer, Node<T>> getReverseIndexMap(){
        Map<Integer, Node<T>> reverseIndexMap = new HashMap<>();
        int index = 0;
        for (Node<T> node : _nodes.values()) {
            reverseIndexMap.put(index++, node);
        }
        return reverseIndexMap;
    }


    /**
     * Gives a multi-line String representation of this Graph. Each line in the returned
     * string represent a Node in the graph, followed by its outgoing (egress) Edges.
     * E.g: If the graph contains 3 nodes, A, B an C, where A and B point to each other and
     * both of them point to C, the value returned by toString() will be as follows:
     * <pre>
     * A > B C
     * B > A C
     * C > 
     * </pre>
     * <u>Note:</u> Each line is a space-separated sequence of token. A Node with no
     * outgoing (egress) edges, like C in the example above still has a line where 
     * the ' > ' token is surrounded by the space characters.
     * @return multi-line String reflecting the content and structure of this Graph.
     */
    @Override
    public String toString() {
        String output = "";
        boolean first = true;
        for(Node<?> n : _nodes.values()) {
            if (!first) {
                output += "\n";
            }
            output += n.toString();
            first = false;
        }
        
        return output;
    }

    /**
     * Generates a map grouping all nodes in the graph by their out-degree.
     * @return a map where each out-degree value in the graph (key) is associated
     * with the set of nodes (value) having that out-degree.
     */
    public TreeMap<Integer, TreeSet<T>> getOutDegrees() {
        TreeMap<Integer, TreeSet<T>> ans = new TreeMap<Integer, TreeSet<T>>();
        for(int x : _nodes.keySet()){
            int degree = _nodes.get(x).getOutDegree();
            if(!ans.containsKey(degree)){
                ans.put(degree, new TreeSet<T>());
                ans.get(degree).add(_nodes.get(x).getData());
            } else {
                ans.get(degree).add(_nodes.get(x).getData());
            }
        }
        return ans;
    }

    /**
     * Generates a map grouping all nodes in the graph by their in-degree.
     * @return a map where each in-degree value in the graph (key) is associated
     * with the set of nodes (value) having that in-degree.
     */
    public TreeMap<Integer, TreeSet<T>> getInDegrees() {
        int[][] adjMatrix = getAdjacencyMatrix();
        TreeMap<Integer, TreeSet<T>> ans = new TreeMap<Integer, TreeSet<T>>();
        for(int i = 0; i < adjMatrix.length; i++){
            int degree = 0;
            for(int j = 0; j < adjMatrix.length; j++){
                degree += adjMatrix[j][i];
            }
            if(!ans.containsKey(degree)){
                ans.put(degree, new TreeSet<T>());
                ans.get(degree).add(getReverseIndexMap().get(i).getData());
            } else {
                ans.get(degree).add(getReverseIndexMap().get(i).getData());
            }
        }
        return ans;

        
    }
    
    public TreeMap<Integer, TreeSet<Node<T>>> getInDegreeNodes(){
        int[][] adjMatrix = getAdjacencyMatrix();
        TreeMap<Integer, TreeSet<Node<T>>> ans = new TreeMap<Integer, TreeSet<Node<T>>>();
        for(int i = 0; i < adjMatrix.length; i++){
            int degree = 0;
            for(int j = 0; j < adjMatrix.length; j++){
                degree += adjMatrix[j][i];
            }
            if(!ans.containsKey(degree)){
                ans.put(degree, new TreeSet<Node<T>>());
                ans.get(degree).add(getReverseIndexMap().get(i));
            } else {
                ans.get(degree).add(getReverseIndexMap().get(i));
            }
        }
        return ans;
    }

    public int getInDegree(Node<T> n){
        int[][] adjMatrix = getAdjacencyMatrix();
        int degree = 0;
        for(int i = 0; i < adjMatrix.length; i++){
            degree += adjMatrix[i][getNodeIndexMap().get(n)];
        }
        return degree;
    }

    /**
     * Generates the topological sort of this graph, where all nodes in the graph
     * are grouped by their index in topological order. The first index is 0.
     * @return a map associating each position in the topological sort (key)
     * with the set of Nodes at that position (value). If the Graph is not DAG, the method 
     * returns null.
     */
    
    public TreeMap<Integer, TreeSet<T>> topoSort() {
        if (!isDAGraph()) {
            return null;
        }
    
        Map<Node<T>, Integer> inDegreeMap = new HashMap<>();
        for (Node<T> n : _nodes.values()) {
            inDegreeMap.put(n, getInDegree(n));
        }
    
        TreeMap<Integer, TreeSet<T>> ans = new TreeMap<>();
        int level = 0;
        Queue<Node<T>> queue = new LinkedList<>();
    
        for (Node<T> n : _nodes.values()) {
            if (inDegreeMap.get(n) == 0) {
                queue.offer(n);
            }
        }
    
        while (!queue.isEmpty()) {
            int size = queue.size();
            TreeSet<T> levelSet = new TreeSet<>();

            Queue<Node<T>> nextQueue = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                Node<T> current = queue.poll();
                levelSet.add(current.getData());
                for (Node<T> neighbor : current.getEdges().values()) {
                    inDegreeMap.put(neighbor, inDegreeMap.get(neighbor) - 1);
                    if (inDegreeMap.get(neighbor) == 0) {
                        nextQueue.offer(neighbor);
                    }
                }
            }
            ans.put(level, levelSet);
            level++;
            queue = nextQueue;
        }
        return ans;
    }
    
    

    /**
     * Generates the count of the partitions in the graph.
     * @return count of partitions.
     */
    public int countPartitions() {
        this.makeUndirected();
        int ans = 0;
        for(Node<T> n : _nodes.values()){
            n.reset();
        }
        
        for(Node<T> n : _nodes.values()){
            if(n.getState() == 0){
                Stack<Node<T>> stack = new Stack<Node<T>>();
                stack.push(n);
                while(stack.isEmpty() == false){
                    Node<T> crt = stack.pop();
                    if(crt.getState() != 0){
                        continue;
                    }
                    crt.setState(1);
                    for(Node<T> m : crt.getEdges().values()){
                        if(m.getState() == 0){
                            stack.push(m);
                        }
                    }
                }
                ans++;
            }
        }
        return ans;


    }

    public void makeUndirected(){
        for(Node<T> n : _nodes.values()){
            for(Node<T> m : n.getEdges().values()){
                if(!m.getEdges().containsValue(n)){
                    m.addEdge(n);
                }
            }
        }
    }

    Node<T> getNode(T data) {
        return _nodes.get(data.hashCode());
    }


    /**
     * Generates the Dijkstra distances between the node containing fromData and all the
     * other nodes in the graph.
     * @param fromData
     * @return a map where the key is each Node in the Graph (given by its data)
     * and the value is the Dijkstra distance from the <i>source</i> Node to that node.
     */
    public TreeMap<T, Integer> dijkstra(T fromData) {
        Node<T> startNode = getNode(fromData);
        Set<Node<T>> sptSet = new TreeSet<>();
        TreeMap<Node<T>, Integer> distances = new TreeMap<>();
        for(Node<T> n : _nodes.values()){
            distances.put(n, Integer.MAX_VALUE);
        }
        distances.put(startNode, 0);

        while(sptSet.size() != _nodes.size()){
            // pick u
            Node<T> u = null;
            int min = Integer.MAX_VALUE;
            for(Entry<Node<T>, Integer> n : distances.entrySet()){
                if(n.getValue() < min && !sptSet.contains(n.getKey())){
                    u = n.getKey();
                    min = n.getValue();
                }
            }

            if(u == null){
                break;
            }
            sptSet.add(u);

            for(Node<T> neighbor : u.getEdges().values()){
                if(distances.get(u) + 1 < distances.get(neighbor)){
                    distances.put(neighbor, distances.get(u) + 1);
                }
            }
        }

        TreeMap<T, Integer> ans = new TreeMap<>();
        //convert back
        for(Entry<Node<T>, Integer> n : distances.entrySet()){
            if(n.getValue() != Integer.MAX_VALUE){
                ans.put(n.getKey().getData(), n.getValue());
            } else {
                ans.put(n.getKey().getData(), -1);
            }
        }
        return ans;
    }

    public boolean hasEulerianCircuit() {
        if(!isStronglyConnected()){
            return false;
        }

        for(Node<T> n : _nodes.values()){
            if(n.getOutDegree() != getInDegree(n)){
                return false;
            }
        }
        return true;
    }

    public boolean isStronglyConnected() {
        Node<T> start = _nodes.values().iterator().next();
        for(Node<T> n : _nodes.values()){
            n.reset();
        }
        if(!isReachableFrom(start)){
            return false;
        }

        Graph<T> reversed = getReversedGraph();
        for(Node<T> n : reversed._nodes.values()){
            n.reset();
        }
        return reversed.isReachableFrom(reversed.getNode(start.getData()));


    }

    public boolean isReachableFrom(Node<T> start){
        Stack<Node<T>> stack = new Stack<Node<T>>();
        Set<Node<T>> visited = new HashSet<Node<T>>();
        stack.push(start);
        while(!stack.isEmpty()){
            Node<T> current = stack.pop();
            if(visited.contains(current)){
                continue;
            }
            visited.add(current);
            for(Node<T> n : current.getEdges().values()){
                stack.push(n);
            }
        }
        return visited.size() == _nodes.size();
    }

    public Graph<T> getReversedGraph() {
        Graph<T> ans = new Graph<T>();
        for(Node<T> n : _nodes.values()){
            ans.addNode(n.getData());
        }
        for(Node<T> n : _nodes.values()){
            for(Node<T> m : n.getEdges().values()){
                ans.addEdge(m.getData(), n.getData());
            }
        }
        return ans;

    } 


}
