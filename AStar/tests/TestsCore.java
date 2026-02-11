package AStar.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import AStar.main.Point;
import AStar.main.Graph;

public class TestsCore {
    private static final Pattern _POINT_REGEX = Pattern.compile("([0-9A-Za-z])\\s*:\\s*(\\d+),(\\d+)");
    
    private Scanner getScanner(String graphFile) throws FileNotFoundException {
        URL url = this.getClass().getResource(graphFile);
        String filePath = graphFile;
        try {
            File file = new File(url.getFile());
            filePath = file.getAbsolutePath();
            filePath = java.net.URLDecoder.decode(filePath, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
        }
        return new Scanner(new File(filePath));
    }
    
    public Point parsePoint(String strPoint) {
        Matcher matcher = _POINT_REGEX.matcher(strPoint.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Invalid format: '%s' is not a Point.", strPoint));
        }
        String label = matcher.group(1);
        int x = Integer.parseInt(matcher.group(2));
        int y = Integer.parseInt(matcher.group(3));
        return new Point(label, x, y);
    }

    public Graph readGraph(String graphFile) throws FileNotFoundException {
        Scanner input = getScanner(graphFile);
        Map<String, List<String>> linksMap = new HashMap<String, List<String>>();
        Map<String, Point> pointsMap = new HashMap<String, Point>();
        while(input.hasNextLine()) {
            String line = input.nextLine();
            String[] tokens = line.split(">");
            if (tokens.length < 1) {
                input.close();
                throw new RuntimeException("Syntax error in parsing graph!");
            }
            Point point = parsePoint(tokens[0]);
            String[] links = (tokens.length > 1) ? tokens[1].trim().split("\\s+") : new String[0];
            pointsMap.put(point.getLabel(), point);
            linksMap.put(point.getLabel(), Arrays.asList(links));

        }
        input.close();
        
        Graph graph = new Graph();
        for(Point p : pointsMap.values()) {
            graph.addNode(p);
        }
        
        for(Map.Entry<String, Point> kvp : pointsMap.entrySet()) {
            Point fromPoint = kvp.getValue();
            for(String v : linksMap.get(kvp.getKey())) {
                Point toPoint = pointsMap.get(v);
                if (toPoint != null) {
                    graph.addEdge(fromPoint.getLabel(), toPoint.getLabel());
                }
            }
        }

        return graph;
    }
    
    public void assertSameGraph(String graphFile, Graph g) throws FileNotFoundException {
        Scanner parser = getScanner(graphFile);
        Set<String> expected = new TreeSet<String>();
        Set<String> actual = new TreeSet<String>();
        while(parser.hasNextLine()) {
            String line = parser.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            expected.add(line);
        }
        for(String line : g.toString().split("\n")) {
            actual.add(line);
        }
        parser.close();
        assertEquals(expected.size(), actual.size());
        Iterator<String> iExpected = expected.iterator();
        Iterator<String> iActual = actual.iterator();
        while(iExpected.hasNext()) {
            assertSameNode(iExpected.next(), iActual.next());
        }
    }

    public void assertSameNode(String expectedNode, String actualNode) {
        Set<String> expectedTokens = new TreeSet<String>(Arrays.asList(expectedNode.split("\\s+")));
        Set<String> actualTokens = new TreeSet<String>(Arrays.asList(actualNode.split("\\s+")));
        assertTrue(expectedTokens.equals(actualTokens));
    }
}
