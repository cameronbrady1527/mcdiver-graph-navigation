package diver;

import game.*;

import graph.ShortestPaths;
import java.util.*;


/** This is the place for your implementation of the {@code SewerDiver}.
 */
public class McDiver implements SewerDiver {
    /**
     * A predefined constant representing the maximum distance McDiver is willing
     * to traverse in a single seek run. This constant is used in the algorithm to influence
     * traversing the graph, ensuring that McDiver does not make excessively long "run"
     * without considering the proximity to the ring. It helps balance exploration and
     * efficiency in the traversal process and will switch to a new run if unsuccessful.
     */
    private final int RUN_DISTANCE = 100;

    /**
     * The current run seek value (length) representing McDiver's traversal. The run value
     * dynamically adjusts during traversal based on specific conditions, affecting how McDiver
     * explores the graph, especially helpful with backtracking in seek.
     */
    private int run = 0;

    /** See {@code SewerDriver} for specification. */
    @Override
    public void seek(SeekState state) {
        seekTraversal(state);
    }

    /**
     * McDiver traverses the graph using a modified BFS algorithm to locate and move towards the
     * ring. The traversal is guided by specific conditions and priorities, aiming to optimize
     * the path towards the ring while avoiding unnecessary backtracking.
     */
    private void seekTraversal(SeekState state) {
        if (state.currentLocation() == 0) {
            return;
        }

        Set<NodeStatus> visited = new HashSet<>();
        PriorityQueue<NodeStatus> queue = new PriorityQueue<>(state.neighbors());
        Map<Long, Set<NodeStatus>> graph = new HashMap<>();
        graph.put(state.currentLocation(), new HashSet<>(state.neighbors()));
        long start = state.currentLocation();

        while (!queue.isEmpty()) {
            NodeStatus curr = queue.poll();
            if (!visited.contains(curr) && curr.getId() != start) {
                graph.compute(state.currentLocation(), (k, v) -> {
                    if (v == null) {
                        v = new HashSet<>();
                    }
                    v.addAll(state.neighbors());
                    return v;
                });

                if (!state.neighbors().contains(curr) && run < RUN_DISTANCE) {
                    state.neighbors().stream()
                            .filter(neighbor -> !visited.contains(neighbor))
                            .min(NodeStatus::compareTo)
                            .ifPresentOrElse(node -> {
                                run = node.getDistanceToRing() > state.distanceToRing()
                                        ? run + 2 : run + 1;
                                state.moveTo(node.getId());
                                queue.add(curr);
                                visited.add(node);
                            }, () -> {
                                run += RUN_DISTANCE / 5;
                                List<NodeStatus> path = bfsToPath(state.currentLocation(),
                                        curr, graph);
                                path.stream().map(NodeStatus::getId).forEach(state::moveTo);
                                visited.addAll(path);
                            });

                    queue.addAll(state.neighbors());
                    if (state.distanceToRing() == 0) {
                        return;
                    }
                } else {
                    List<NodeStatus> path = bfsToPath(state.currentLocation(), curr, graph);
                    path.stream().map(NodeStatus::getId).forEach(state::moveTo);
                    visited.addAll(path);

                    if (state.distanceToRing() == 0) {
                        return;
                    }

                    queue.addAll(state.neighbors());

                    run = Math.max(run - 20, 0);
                }
            }
        }
    }

    /**
     * Generates class BfsNode with baseline functionality in methods taking in
     * `path` and `nodeId`.
     */
    private record BfsNode(List<NodeStatus> path, long nodeId) {}

    /**
     * Helper: When hit dead end, generates a path to node you popped off the priority queue by BFS
     * to explore a new path.
     */
    private List<NodeStatus> bfsToPath(long sourceId, NodeStatus destination,
            Map<Long, Set<NodeStatus>> graph) {
        Deque<BfsNode> deque = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        deque.add(new BfsNode(new ArrayList<>(), sourceId));

        while (!deque.isEmpty()) {
            BfsNode node = deque.removeFirst();
            if (!visited.contains(node.nodeId)) {
                if (node.nodeId == destination.getId()) {
                    return node.path;
                }

                Optional.ofNullable(graph.get(node.nodeId)).orElse(Collections.emptySet())
                        .forEach(neighbor -> {
                    final BfsNode nextBfsNode = new BfsNode(new ArrayList<>(node.path),
                            neighbor.getId());
                    nextBfsNode.path.add(neighbor);
                    deque.addLast(nextBfsNode);
                });

                visited.add(node.nodeId);
            }
        }

        throw new NoSuchElementException("Can't find node, this shouldn't happen...");
    }

    /** See {@code SewerDriver} for specification. */
    @Override
    public void scram(ScramState state) {
        // scramScared(state);
        scramGreedier(state);
    }

    /**
     * McDiver runs straight to the exit by shortest path computed with Dijkstra's Algorithm
     */
    private void scramScared(ScramState state) {
        if (state.currentNode() == state.exit()) {
            return;
        }

        Maze graph = new Maze(new HashSet<>(state.allNodes()));
        ShortestPaths<Node, Edge> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances(state.currentNode());

        runToExit(ssp, graph, state);
    }

    /**
     * Effect: moves McDiver to the exit. Param `ssp` used to compute the path to the exit,
     * `graph` to represent the maze board, and `state` to represent McDiver in the Scram state.
     */
    private void runToExit(ShortestPaths<Node, Edge> ssp, Maze graph, ScramState state) {
        for(Edge e : ssp.bestPath(state.exit())) {
            state.moveTo(graph.dest(e));
        }
    }

    /**
     * A more optimized version of method scramScared, which will then be called from scram.
     * McDiver visits tiles with coins based on the coin density of the path, which is found by
     * taking the total coins able to be collected on the path and dividing by the path distance.
     * If he does not have enough steps to get more coins, he will get out.
     */
    public void scramGreedier(ScramState state) {
        Set<Node> nodeTracker = (Set<Node>) state.allNodes();

        Maze weightedDimaze = new Maze(nodeTracker);

        ShortestPaths<Node, Edge> shortTracker1 = new ShortestPaths<>(weightedDimaze);
        ShortestPaths<Node, Edge> shortTracker2 = new ShortestPaths<>(weightedDimaze);

        Node[] coinNodes = state.allNodes().toArray(new Node[state.allNodes().size()]);

        for (int i = 0; i < state.allNodes().size(); i++) {
            shortTracker2.singleSourceDistances(state.currentNode());
            Node hereNode = state.currentNode();
            Arrays.sort(coinNodes,
                    (a, b) -> {
                        List<Edge> bList = shortTracker2.bestPath(b);
                        int bCoin = 0;
                        for (Edge edge : bList) {
                            bCoin += edge.destination().getTile().coins();
                        }
                        List<Edge> aList = shortTracker2.bestPath(a);
                        int aCoin = 0;
                        for (Edge edge : aList) {
                            aCoin += edge.destination().getTile().coins();
                        }
                        return Double.compare(bCoin / shortTracker2.getDistance(b),
                                aCoin / shortTracker2.getDistance(a));
                    });
            for (Node coinNode : coinNodes) {
                shortTracker1.singleSourceDistances(coinNode);
                shortTracker2.singleSourceDistances(state.currentNode());
                List<Edge> coinCounterPathList = shortTracker2.bestPath(coinNode);
                int coinCounter = 0;
                for (Edge edge : coinCounterPathList) {
                    coinCounter += edge.destination().getTile().coins();
                }
                if (shortTracker1.getDistance(state.exit())
                        > state.stepsToGo() - shortTracker2.getDistance(coinNode)
                        || coinCounter == 0) {
                    continue;
                }
                List<Edge> coinPathList = shortTracker2.bestPath(coinNode);
                for (Edge shortEdge : coinPathList) {
                    state.moveTo(shortEdge.destination());
                }
                break;
            }

            if (state.currentNode().equals(hereNode)) {
                break;
            }
        }
        List<Edge> shortPathList = shortTracker2.bestPath(state.exit());
        for (Edge shortEdge : shortPathList) {
            state.moveTo(shortEdge.destination());
        }
    }
}
