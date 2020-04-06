package student_player;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.SaboteurPlayer;
import boardgame.Move;

import java.util.ArrayList;
import java.util.Random;

public class MinimaxPlayer extends SaboteurPlayer {
    SaboteurMove theMove = null;

    @Override
    public Move chooseMove(SaboteurBoardState boardState) {
        return null;
    }

    /**
     * MiniMax with alpha-beta pruning to a certain depth
     *
     * @param depth
     * @param bs
     * @param alpha
     * @param beta
     * @return alpha or beta depends on the player role
     */
    public int alphaBetaPruning(int depth, SaboteurBoardState bs,int alpha,int beta){
        int player = bs.getTurnPlayer();
        bs.getHiddenBoard();
        // Re-initiate the best move, so that only the move at current depth will be retrieved only
        SaboteurMove bestMove=null;
        // The array to store all the best moves at current depth. Since will be re-generated every depth, only the top level moves will be retrieved
        ArrayList<SaboteurMove> bestMoves=new ArrayList<>();
        int value;
        // Find all legal moves available to current player
        ArrayList<SaboteurMove> moves=bs.getAllLegalMoves();

        /*
         *  PseudoCode:
         * int MaxValue(s,a,b)
         * 	if cutoff(s), return evaluation(s)
         * 	for each state s' in successors(s)
         * 		let a=max(a,min(s',a,b))
         * 		if a>=b return b
         * 	return a
         * int MinValue(s,a,b)
         * 	if cutoff(s), return evaluation(s)
         * 	for each state s' in successor(s)
         * 		let b=min(b,max(s',a,b))
         * 		if a>=b return a
         * 	return b
         */

        // If the game is over (someone won) or the leaf node is reached, get the value from heuristic
        if(bs.gameOver()||depth==0||moves.isEmpty()){
            return heuristic(bs);
            /* If not at a leaf node:
             * Find all possible next states, then for each state, find the maxValue of its sucessors if it is Max Player or the MinValue of its sucessors if it is Min Player
             */
        }else{
            for(SaboteurMove move:moves){
                SaboteurBoardState cloneBs=(SaboteurBoardState) bs.clone(); // Make a copy of the current Tablut board state
                cloneBs.processMove(move); // Find the resulting state through process the move on the copy
                if(bs.getTurnPlayer()==player){ //self as Max Player
                    value=alphaBetaPruning(depth-1,cloneBs,alpha,beta);
                    if(value>alpha){
                        alpha=value;
                        bestMove=move;
//						bestMoves.clear();
//						bestMoves.add(bestMove);
                        if(alpha==100){ // the cutoff is game over
                            break;
                        }
                    }
//					else if(value==alpha){
//						bestMoves.add(move);
//					}
                }else{ // Muscovite as Mini Player
                    value=alphaBetaPruning(depth-1,cloneBs,alpha,beta);
                    if(value<beta){
                        beta=value;
                        bestMove=move;
//						bestMoves.clear();
//						bestMoves.add(bestMove);
                        if(beta==-100){ // the cutoff is game over
                            break;
                        }
                    }
//					else if(value==alpha){
//						bestMoves.add(move);
//					}
                }
                // cutoff
                if(alpha>=beta){
                    break;
                }
            }
            if(bestMove!=null){
                bestMoves.add(bestMove);
            }
            if(!bestMoves.isEmpty()){
                theMove=bestMoves.get(new Random(System.currentTimeMillis()).nextInt(bestMoves.size()));
            }
//			if(bestMove!=null){
//				bestMoves.add(bestMove);
//			}
//			if(!bestMoves.isEmpty()){
//				theMove=bestMoves.get(bestMoves.size()-1);
//			}
//			theMove=(TablutMove) ((bestMove==null)?bs.getRandomMove():bestMove);
            if(bs.getTurnPlayer()==player){
                return alpha;
            }else{
                return beta;
            }
        }
    }

    private int heuristic(SaboteurBoardState bs) {
        return 0;
    }
}
