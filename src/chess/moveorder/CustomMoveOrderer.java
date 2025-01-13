package src.pas.chess.moveorder;

// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;
import edu.bu.chess.game.move.MoveType;

import java.util.ArrayList;
import java.util.List;

public class CustomMoveOrderer {

    /**
     * Orders moves to prioritize promotion moves first, followed by capture moves, then all other moves.
     * @param nodes The child nodes to order.
     * @return Ordered list of nodes with prioritized moves.
     */
    public static List<DFSTreeNode> order(List<DFSTreeNode> nodes) {
        List<DFSTreeNode> promotePawnMoves = new ArrayList<>();
        List<DFSTreeNode> captureMoves = new ArrayList<>();
        List<DFSTreeNode> otherMoves = new ArrayList<>();
        List<DFSTreeNode> orderedMoves = new ArrayList<>();

        // My agents will always castle and promote pawn

        for (DFSTreeNode node : nodes) {
            if (node.getMove() != null) {
                MoveType moveType = node.getMove().getType();
                if (moveType == MoveType.CASTLEMOVE) {
                    orderedMoves.add(node);
                } else if (moveType == MoveType.PROMOTEPAWNMOVE) {
                    promotePawnMoves.add(node); 
                } else if (moveType == MoveType.CAPTUREMOVE) {
                    captureMoves.add(node); 
                } else {
                    otherMoves.add(node); 
                }
            } else {
                otherMoves.add(node); 
            }
        }

        // castle -> promote pawn -> capture -> other moves
        orderedMoves.addAll(promotePawnMoves);
        orderedMoves.addAll(captureMoves);
        orderedMoves.addAll(otherMoves);

        return orderedMoves;
    }
}
