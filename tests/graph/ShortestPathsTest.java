package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ShortestPathsTest {
    /** The graph example from Prof. Myers's notes. There are 7 vertices labeled a-g, as
     *  described by vertices1. 
     *  Edges are specified by edges1 as triples of the form {src, dest, weight}
     *  where src and dest are the indices of the source and destination
     *  vertices in vertices1. For example, there is an edge from a to d with
     *  weight 15.
     */
    static final String[] vertices1 = { "a", "b", "c", "d", "e", "f", "g" };
    static final int[][] edges1 = {
        {0, 1, 9}, {0, 2, 14}, {0, 3, 15},
        {1, 4, 23},
        {2, 4, 17}, {2, 3, 5}, {2, 5, 30},
        {3, 5, 20}, {3, 6, 37},
        {4, 5, 3}, {4, 6, 20},
        {5, 6, 16}
    };
    static class TestGraph implements WeightedDigraph<String, int[]> {
        int[][] edges;
        String[] vertices;
        Map<String, Set<int[]>> outgoing;

        TestGraph(String[] vertices, int[][] edges) {
            this.vertices = vertices;
            this.edges = edges;
            this.outgoing = new HashMap<>();
            for (String v : vertices) {
                outgoing.put(v, new HashSet<>());
            }
            for (int[] edge : edges) {
                outgoing.get(vertices[edge[0]]).add(edge);
            }
        }
        public Iterable<int[]> outgoingEdges(String vertex) { return outgoing.get(vertex); }
        public String source(int[] edge) { return vertices[edge[0]]; }
        public String dest(int[] edge) { return vertices[edge[1]]; }
        public double weight(int[] edge) { return edge[2]; }
    }
    static TestGraph testGraph1() {
        return new TestGraph(vertices1, edges1);
    }

    @Test
    void lectureNotesTest() {
        TestGraph graph = testGraph1();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(50, ssp.getDistance("g"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("g")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" g");
        assertEquals("best path: a c e f g", sb.toString());

        assertEquals(34, ssp.getDistance("f"));
        sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("f")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" f");
        assertEquals("best path: a c e f", sb.toString());
    }

    static final String[] vertices2 = { "a", "b", "c", "d" };
    static final int[][] edges2 = {
            {0, 1, 4}, {0, 2, 15},
            {1, 2, 10},
            {2, 3, 13},
            {3, 0, 2}
    };

    static TestGraph testGraph2() {
        return new TestGraph(vertices2, edges2);
    }

    @Test
    void nonDAGTest() {
        TestGraph graph = testGraph2();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(27, ssp.getDistance("d"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("d")) {
            sb.append(" " + vertices2[e[0]]);
        }
        sb.append(" d");
        assertEquals("best path: a b c d", sb.toString());

        assertEquals(14, ssp.getDistance("c"));
        sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("c")) {
            sb.append(" " + vertices2[e[0]]);
        }
        sb.append(" c");
        assertEquals("best path: a b c", sb.toString());

        assertEquals(0, ssp.getDistance("a"));
        sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("a")) {
            sb.append(" " + vertices2[e[0]]);
        }
        sb.append(" a");
        assertEquals("best path: a", sb.toString());
    }

    static final String[] vertices3 = { "a", "b", "c", "d", "e", "f", "g" };
    static final int[][] edges3 = {
            {0, 1, 10}, {0, 4, 6},
            {1, 2, 5},
            {2, 3, 40}, {2, 4, 7},
            {3, 1, 3}, {3, 4, 23},
            {6, 0, 50}
    };

    static TestGraph testGraph3() {
        return new TestGraph(vertices3, edges3);
    }

    @Test
    void unconnectedVertexTest() {
        TestGraph graph = testGraph3();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(55, ssp.getDistance("d"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("d")) {
            sb.append(" " + vertices3[e[0]]);
        }
        sb.append(" d");
        assertEquals("best path: a b c d", sb.toString());

        assertEquals(15, ssp.getDistance("c"));
        sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("c")) {
            sb.append(" " + vertices3[e[0]]);
        }
        sb.append(" c");
        assertEquals("best path: a b c", sb.toString());

        ssp.singleSourceDistances("f");
        assertEquals(0, ssp.getDistance("f"));
        sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("f")) {
            sb.append(" " + vertices3[e[0]]);
        }
        sb.append(" f");
        assertEquals("best path: f", sb.toString());

    }

    static final String[] vertices4 = { "a", "b", "c", "d", "e", "f", "g" };
    static final int[][] edges4 = {
            {0, 1, 15}, {0, 4, 9},
            {1, 2, 10}, {1, 4, 20},
            {2, 2, 2}, {2, 3, 4}, {2, 4, 17},
            {3, 2, 5},
            {4, 4, 3}
    };

    static TestGraph testGraph4() {
        return new TestGraph(vertices4, edges4);
    }

    @Test
    void hasSelfLoopTest(){
        TestGraph graph = testGraph4();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(29, ssp.getDistance("d"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("d")) {
            sb.append(" " + vertices3[e[0]]);
        }
        sb.append(" d");
        assertEquals("best path: a b c d", sb.toString());

        assertEquals(9, ssp.getDistance("e"));
        sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("e")) {
            sb.append(" " + vertices3[e[0]]);
        }
        sb.append(" e");
        assertEquals("best path: a e", sb.toString());

        assertEquals(25, ssp.getDistance("c"));
        sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("c")) {
            sb.append(" " + vertices3[e[0]]);
        }
        sb.append(" c");
        assertEquals("best path: a b c", sb.toString());
    }
}
