# ChessAI

## Features

### Alpha-Beta Pruning Implementation
- Implemented the `AlphaBetaAgent.AlphaBetaSearcher.alphaBetaSearch` method.
- Ensures correctness by comparing results with the Minimax algorithm.
- Validated against a provided game file to ensure identical utility values to Minimax.

### Custom Heuristics
- Developed a set of heuristics to evaluate board states based on:
  - **Offense**: Evaluating threats to opponent pieces.
  - **Defense**: Ensuring piece safety and controlling strategic board positions.
  - **Positional Play**: Encouraging control over key squares.
- Heuristics values range between `-Double.MAX_VALUE` and `+Double.MAX_VALUE`, reflecting the true cost of losing or winning.

### Custom Move Ordering
- Optimized the move ordering process to maximize Alpha-Beta pruning efficiency.
- Prioritized capturing moves and strategically advantageous actions.
- Avoided computationally expensive sorting by leveraging lightweight heuristics for prioritization.

## Project Structure
- **`src/agents/AlphaBetaAgent.java`**: Contains the Alpha-Beta pruning implementation.
- **`src/heuristics/CustomHeuristics.java`**: Defines heuristics for board evaluation.
- **`src/move_ordering/CustomMoveOrderer.java`**: Implements custom move ordering to enhance pruning.
- **Test File**: Used for validation against Minimax.

## Validation
- Verified the correctness of the Alpha-Beta pruning algorithm by running parallel comparisons with Minimax.
- Performance improvements observed due to optimized move ordering, reducing the search space.

## Learning Outcomes
- Gained practical experience with search algorithms in AI.
- Explored the impact of heuristics on agent decision-making.
- Improved algorithmic efficiency through strategic node ordering.

## Skills Demonstrated
- Artificial Intelligence: Alpha-Beta Pruning, Minimax, Heuristics.
- Problem Solving: Strategic optimization of search algorithms.
- Programming: Java, object-oriented design, and debugging.


