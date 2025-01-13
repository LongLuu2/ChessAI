package src.pas.chess.heuristics;

// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;
import edu.bu.chess.game.move.PromotePawnMove;
import edu.bu.chess.game.piece.Piece;
import edu.bu.chess.game.piece.PieceType;
import edu.bu.chess.game.player.Player;
import edu.bu.chess.game.player.PlayerType;
import edu.bu.chess.utils.Coordinate;
import edu.cwru.sepia.util.Direction;
import edu.bu.chess.game.move.Move;
import edu.bu.chess.game.move.MovementMove;
import edu.bu.chess.game.move.MoveType;


// JAVA PROJECT IMPORTS


public class CustomHeuristics extends Object {
    /**
	 * Get the max player from a node
	 * @param node
	 * @return
	 */
	public static Player getMaxPlayer(DFSTreeNode node)
	{
		return node.getMaxPlayer();
	}

	/**
	 * Get the min player from a node
	 * @param node
	 * @return
	 */
	public static Player getMinPlayer(DFSTreeNode node)
	{
		return CustomHeuristics.getMaxPlayer(node).equals(node.getGame().getCurrentPlayer()) ? node.getGame().getOtherPlayer() : node.getGame().getCurrentPlayer();
	}

    ////////////////////////////////// Control Heuristics \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private static double getPositionalAdvantageMaxPlayer(DFSTreeNode node) {
        double positionalScore = 0.0;

        positionalScore += ControlHeuristics.getBoardControlScore(node);
        positionalScore += ControlHeuristics.getCenterControlScore(node);

        //System.out.println(ControlHeuristics.getBoardControlScore(node) + " + " +ControlHeuristics.getCenterControlScore(node));
        
        return positionalScore;
    }

    public static class ControlHeuristics {
        
        // gets all the total sqaures that can be targeted
        public static double getBoardControlScore(DFSTreeNode node) {
            double amountOfTargetableSquares = 0.0;

            for (Piece piece : node.getGame().getBoard().getPieces(getMaxPlayer(node))) {
                amountOfTargetableSquares += piece.getAllMoves(node.getGame()).size(); 
            }

            return amountOfTargetableSquares;
        }
        // insentivise moving to the center
        public static double getCenterControlScore(DFSTreeNode node) {
            Coordinate[] centerSquares = {
                new Coordinate(4, 4), 
                new Coordinate(4, 5), 
                new Coordinate(5, 4), 
                new Coordinate(5, 5)
            };

            double centerControlScore = 0.0;

            for (Piece piece : node.getGame().getBoard().getPieces(getMaxPlayer(node))) {
                for (Move move : piece.getAllMoves(node.getGame())) {
                    if (move instanceof MovementMove) {
                        MovementMove movementMove = (MovementMove) move;
                        Coordinate destination = movementMove.getTargetPosition();

                        for (Coordinate centerSquare : centerSquares) {
                            if (destination.equals(centerSquare)) {
                                centerControlScore += 9.0;
                            }
                        }
                    }
                }
            }

            return centerControlScore;
        }

    }

    ////////////////////////////////// Offensive Heuristics \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private static double getOffensiveMaxPlayerHeuristicValue(DFSTreeNode node) {
        double offensiveScore = 0.0;

        offensiveScore += OffensiveHeuristics.getNumberOfPiecesMaxPlayerIsThreatening(node);//from default

        offensiveScore += OffensiveHeuristics.getPawnPromote(node);
        offensiveScore += OffensiveHeuristics.getMaterialAdvantageScore(node);
        offensiveScore += OffensiveHeuristics.getPieceDevelopmentScore(node);
        offensiveScore += OffensiveHeuristics.freeTheRook(node);
        return offensiveScore;
    }

    public static class OffensiveHeuristics {

        //from default
        public static double getNumberOfPiecesMaxPlayerIsThreatening(DFSTreeNode node) {
            double attackScore = 0.0;

            for (Piece piece : node.getGame().getBoard().getPieces(getMaxPlayer(node))) {
                attackScore += piece.getAllCaptureMoves(node.getGame()).size();  
            }

            return attackScore;  
        }

        //incentivse promoting pawn high
        public static double getPawnPromote(DFSTreeNode node) {
            if (node.getMove() instanceof PromotePawnMove) {
                return 1000.0;
            }
            return 0;
        }

        //incentivise states that have more matterial over the op
        public static double getMaterialAdvantageScore(DFSTreeNode node) {
            double maxPiecesScore = node.getGame().getBoard().getPointsEarned(getMaxPlayer(node));
            double minPieceScore = node.getGame().getBoard().getPointsEarned(getMinPlayer(node));
            if (maxPiecesScore > minPieceScore) {
                maxPiecesScore += (maxPiecesScore - minPieceScore) * 2;  
            }
            return maxPiecesScore;
        }

        

    // incenstive activing pieces (moving from their orignal positon)
    public static double getPieceDevelopmentScore(DFSTreeNode node) {
        double activation = 0.0;

        for (Piece piece : node.getGame().getBoard().getPieces(getMaxPlayer(node))) {
            Coordinate currentPosition = node.getGame().getCurrentPosition(piece);

            if ((piece.getType() == PieceType.KNIGHT || piece.getType() == PieceType.BISHOP) &&
                ((getMaxPlayer(node).equals(piece.getPlayer()) && currentPosition.getYPosition() > 8) ||
                (getMinPlayer(node).equals(piece.getPlayer()) && currentPosition.getYPosition() < 1))) {
                activation += 3.0;
            }

            if (piece.getType() == PieceType.PAWN) {
                int posOfStart = (getMaxPlayer(node).equals(piece.getPlayer())) ? 7 : 2;
                if (currentPosition.getYPosition() != posOfStart) {
                    activation += 2.5;
                }
            }
        }

        return activation;
    }

    // making room for rooks to be active
    public static double freeTheRook(DFSTreeNode node) {
        double openPathScore = 0.0;

        for (Piece piece : node.getGame().getBoard().getPieces(getMaxPlayer(node))) {
            if (piece.getType() == PieceType.ROOK) {
                Coordinate rookPosition = node.getGame().getCurrentPosition(piece);
                boolean openFlag = true;

                for (Piece otherPiece : node.getGame().getBoard().getPieces(getMaxPlayer(node), PieceType.PAWN)) {
                    Coordinate pawnPosition = node.getGame().getCurrentPosition(otherPiece);

                    // If a pawn is in the same line
                    if (pawnPosition.getXPosition() == rookPosition.getXPosition()) {
                        openFlag = false;
                        break;
                    }   
                }
                // If no pawn is blocking the rook's file, increase the score
                if (openFlag) {
                    openPathScore += 6.0;
                }      
            }
        }   

        return openPathScore;
    }


    }

    ////////////////////////////////// Defensive Heuristics \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static double getDefensiveMaxPlayerHeuristicValue(DFSTreeNode node) {
        double defenseScore = 0.0;

        //this is from default, but subtracts as I think the agent should avoid states with more enenies
        //threatening us
        defenseScore -= DefensiveHeuristics.getNumberOfPiecesThreateningMaxPlayer(node);
 
        defenseScore += DefensiveHeuristics.getStaySafeKing(node);  
        defenseScore += DefensiveHeuristics.PieceProtection(node);  

        return defenseScore;
    }

    public static class DefensiveHeuristics {

        public static int getNumberOfPiecesThreateningMaxPlayer(DFSTreeNode node) {
            // How many pieces are threatening us?
            int numPiecesThreateningMaxPlayer = 0;
            for (Piece piece : node.getGame().getBoard().getPieces(getMinPlayer(node))) {
                numPiecesThreateningMaxPlayer += piece.getAllCaptureMoves(node.getGame()).size();
            }
            return numPiecesThreateningMaxPlayer;
        }

        // kings keeps sending himself into the front line so trying to get him to castle
        public static double getStaySafeKing(DFSTreeNode node) {
            double kingSafetyScore = 0.0;

            Piece kingPiece = node.getGame().getBoard().getPieces(getMaxPlayer(node), PieceType.KING).iterator().next();
            Coordinate kingPosition = node.getGame().getCurrentPosition(kingPiece);
            int kingPieceID = kingPiece.getPieceID();

            Move move = node.getMove();
            MoveType moveType = move.getType();
            //System.out.println("Move type: " + moveType);

            // Penalize the king moving unless its a castle move
            if (move.getActorPieceID() == kingPieceID && moveType != MoveType.CASTLEMOVE) {
                //System.out.println("Penalty applied to non-castling king move");
                kingSafetyScore -= 100.0; 
            } else if (moveType == MoveType.CASTLEMOVE) {
                //System.out.println("Castling move: +50");
                kingSafetyScore += 500.0;
            }
    
            return kingSafetyScore;
        }

        // Making it valuble for pieces to cover each other
        public static double PieceProtection(DFSTreeNode node) {
            double protectionScore = 0.0;

            for (Piece piece : node.getGame().getBoard().getPieces(getMaxPlayer(node))) {
                for (Move move : piece.getAllMoves(node.getGame())) {
                    if (move instanceof MovementMove) {
                        Coordinate movePosition = ((MovementMove) move).getTargetPosition();

                        if (node.getGame().getBoard().isPositionOccupied(movePosition)) {
                            Piece targetPiece = node.getGame().getBoard().getPieceAtPosition(movePosition);

                            if (targetPiece != null && !piece.isEnemyPiece(targetPiece)) {
    
                                protectionScore += 4.0;  
                            }
                        }
            }       
                }
            }

            return protectionScore;
        }

    }

    public static double getMaxPlayerHeuristicValue(DFSTreeNode node) {
        double offenseHeuristic = getOffensiveMaxPlayerHeuristicValue(node);
        double defenseHeuristic = getDefensiveMaxPlayerHeuristicValue(node);
        double positionalHeuristic = getPositionalAdvantageMaxPlayer(node);

        return offenseHeuristic + defenseHeuristic + positionalHeuristic;
    }
}
