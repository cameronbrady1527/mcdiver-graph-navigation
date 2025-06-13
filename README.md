# CS2110 Assignment 6 - McDiver: Sewer Navigation

## Overview
An implementation of graph algorithms to help McDiver navigate London's sewer system, find a magical ring, and escape with treasure while avoiding toxic fumes. This assignment demonstrates proficiency in graph traversal, Dijkstra's shortest path algorithm, and concurrent programming.

## Learning Objectives
- Implement and test Dijkstra's single-source shortest-paths algorithm
- Design efficient graph traversal strategies using DFS/BFS
- Optimize pathfinding using heuristics and data structures
- Practice synchronization in concurrent programming
- Work within a large existing codebase

## Game Description
The game consists of two phases:

### Seek Phase
- Navigate an unknown maze to find a magical ring
- Only local visibility (current location and immediate neighbors)
- Manhattan distance to ring is known
- Goal: Find the ring in minimal steps

### Scram Phase
- Escape the sewer within a time limit while collecting treasure
- Full maze visibility with complete map
- Must balance treasure collection with escape time
- Score = coins collected × seek phase multiplier

## Technical Implementation

### Core Algorithms
1. **Dijkstra's Algorithm** (`graph.ShortestPaths`)
    - Generic implementation for weighted directed graphs
    - Used for maze generation and optimal pathfinding

2. **Graph Traversal** (`diver.McDiver`)
    - Seek phase: Modified DFS with distance heuristics
    - Scram phase: Optimized pathfinding with treasure collection

3. **Concurrency** (`game.GUIControl` & `gui.GUI`)
    - Efficient thread synchronization replacing spin loops
    - Proper wait/notify implementation for animations

## Project Structure
```
a6/
├── src/
│   ├── cms.util/        # Utility classes (Maybe type)
│   ├── datastructures/  # Priority queue implementations
│   ├── diver/          # McDiver implementation (YOUR CODE HERE)
│   ├── game/           # Game logic and state management
│   ├── graph/          # Dijkstra's algorithm (YOUR CODE HERE)
│   └── gui/            # GUI components (YOUR CODE HERE)
├── tests/
│   └── graph/          # ShortestPaths tests (YOUR CODE HERE)
├── res/                # Game sprites and images
├── reflection.txt      # Implementation notes
└── summary.txt         # Project summary
```

## Running the Application

### Basic Run
```bash
java -ea -cp out game.Main
```

### Command Line Options
- `--nographics` - Run without GUI (performance testing)
- `-n <count>` - Run multiple times (headless mode only)
- `-s <seed>` - Use specific maze seed (debugging/testing)
- `--help` - Display usage information

### Special Test Seeds
- No backtracking required: `-280019746129361794`
- Backtracking required: `1908492650781828577`
- No reachable coins: `-3026730162232494481`
- Trivial scram phase: `8035820871068432943`
- Interesting scram phase: `2805343804353418701`

## Build Instructions
1. Compile all source files:
   ```bash
   javac -d out src/**/*.java tests/**/*.java
   ```

2. Run tests:
   ```bash
   java -ea -cp out org.junit.runner.JUnitCore graph.ShortestPathsTest
   ```

3. Run the game:
   ```bash
   java -ea -cp out game.Main
   ```

## Implementation Notes
- Assertions must be enabled (`-ea` flag) for proper error checking
- No static variables allowed (thread safety)
- Solutions must always find the ring and escape within time limit
- Performance target: < 10 seconds per maze in headless mode
- Average score target: 20,000+ (over 30 runs)

## Files Modified
- `src/diver/McDiver.java` - Implement seek() and scram() methods
- `src/graph/ShortestPaths.java` - Complete Dijkstra's algorithm
- `src/game/GUIControl.java` - Fix concurrent waiting
- `src/gui/GUI.java` - Add animation completion signaling
- `tests/graph/ShortestPathsTest.java` - Add test coverage

## Scoring
- Basic functionality (find ring + escape): 85%
- Optimization (fewer steps, more treasure): 15%
- Full points achievable with ~20,000 average score

## Academic Integrity
This is a CS2110 assignment at Cornell University. Students must follow the course collaboration policy and work only with their declared partner.

## Authors
[Your Name] and [Partner Name if applicable]

## Acknowledgments
Assignment created by the CS2110 course staff at Cornell University.