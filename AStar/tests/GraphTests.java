package AStar.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.io.FileNotFoundException;
import org.junit.Test;

import AStar.main.Graph;

public class GraphTests extends TestsCore {

    /**
     * Unit test for reading a Graph from a file and verifying
     * the resulting Graph has the same structure as in the input file.
     * @throws FileNotFoundException
     */
    @Test
    public void test_readGraph() throws FileNotFoundException {
        Graph gp = readGraph("/AStar/data/graph1.txt");
        assertEquals(12, gp.size());
        assertSameGraph("/AStar/data/graph1.txt", gp);
    }

    @Test
    public void test_routeFirstPath() throws FileNotFoundException {
        Graph gp = readGraph("/AStar/data/graph1.txt");
        assertNull(gp.routeFirstPath("A", "I"));
        assertEquals(10, gp.getQueueAddCount());
        assertEquals(0.0, gp.getTraveledDistance(), 0.001);

        assertEquals("[K, L, G, H, E, A, C]", gp.routeFirstPath("K", "C").toString());
        assertEquals(10, gp.getQueueAddCount());
        assertEquals(732.590, gp.getTraveledDistance(), 0.001);

        assertEquals("[A, B, D, G, K]", gp.routeFirstPath("A", "K").toString());
        assertEquals(10, gp.getQueueAddCount());
        assertEquals(446.119, gp.getTraveledDistance(), 0.001);

        gp = readGraph("/AStar/data/graph2.txt");
        assertEquals("[A, B, D, G, K]", gp.routeFirstPath("A", "K").toString());
        assertEquals(10, gp.getQueueAddCount());
        assertEquals(494.706, gp.getTraveledDistance(), 0.001);

        gp = readGraph("/AStar/data/graph3.txt");
        assertEquals("[A, F, D, K, J, I, L, X, Z]", gp.routeFirstPath("A", "Z").toString());
        assertEquals(27, gp.getQueueAddCount());
        assertEquals(667.125, gp.getTraveledDistance(), 0.001);
    }

    @Test
    public void test_routeDijkstra() throws FileNotFoundException {
        Graph gp = readGraph("/AStar/data/graph1.txt");
        assertNull(gp.routeDijkstra("A", "I"));
        assertEquals(11, gp.getQueueAddCount());
        assertEquals(0.0, gp.getTraveledDistance(), 0.001);

        assertEquals("[K, L, G, H, E, A, C]", gp.routeDijkstra("K", "C").toString());
        assertEquals(10, gp.getQueueAddCount());
        assertEquals(732.590, gp.getTraveledDistance(), 0.001);

        assertEquals("[A, B, D, G, K]", gp.routeDijkstra("A", "K").toString());
        assertEquals(11, gp.getQueueAddCount());
        assertEquals(446.119, gp.getTraveledDistance(), 0.001);

        gp = readGraph("/AStar/data/graph2.txt");
        assertEquals("[A, C, E, G, K]", gp.routeDijkstra("A", "K").toString());
        assertEquals(12, gp.getQueueAddCount());
        assertEquals(452.563, gp.getTraveledDistance(), 0.001);

        gp = readGraph("/AStar/data/graph3.txt");
        assertEquals("[A, F, D, K, U, P, V, L, X, Z]", gp.routeDijkstra("A", "Z").toString());
        assertEquals(28, gp.getQueueAddCount());
        assertEquals(615.698, gp.getTraveledDistance(), 0.001);
    }

    @Test
    public void test_routeAStar() throws FileNotFoundException {
        Graph gp = readGraph("/AStar/data/graph1.txt");
        assertNull(gp.routeDijkstra("A", "I"));
        assertEquals(11, gp.getQueueAddCount()); // FindFirst < Dijkstra = A*
        assertEquals(0.0, gp.getTraveledDistance(), 0.001); // FindFirst = Dijkstra = A*

        assertEquals("[K, L, G, H, E, A, C]", gp.routeAStar("K", "C").toString());
        assertEquals(10, gp.getQueueAddCount()); // FindFirst = Dijkstra = A*
        assertEquals(732.590, gp.getTraveledDistance(), 0.001); // FindFirst = Dijkstra = A*

        assertEquals("[A, B, D, G, K]", gp.routeAStar("A", "K").toString());
        assertEquals(10, gp.getQueueAddCount()); // Dijkstra < FindFirst < A*
        assertEquals(446.119, gp.getTraveledDistance(), 0.001); // FindFirst = Dijkstra = A*

        gp = readGraph("/AStar/data/graph2.txt");
        assertEquals("[A, C, E, G, K]", gp.routeAStar("A", "K").toString());
        assertEquals(10, gp.getQueueAddCount()); // Dijkstra < FindFirst = A*
        assertEquals(452.563, gp.getTraveledDistance(), 0.001); // FindFirst < Dijkstra = A*

        gp = readGraph("/AStar/data/graph3.txt");
        assertEquals("[A, F, D, K, U, P, V, L, X, Z]", gp.routeAStar("A", "Z").toString());
        assertEquals(20, gp.getQueueAddCount()); // Dijkstra < FindFirst < A*
        assertEquals(615.698, gp.getTraveledDistance(), 0.001); // FindFirst < Dijkstra = A*
    }
}
