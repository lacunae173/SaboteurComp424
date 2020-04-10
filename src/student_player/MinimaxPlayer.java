package student_player;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.SaboteurPlayer;
import boardgame.Move;

import java.util.ArrayList;
import java.util.Random;

public class MinimaxPlayer extends SaboteurPlayer {
    SaboteurMove theMove = null;
    SaboteurBoardState state = null;

    @Override
    public Move chooseMove(SaboteurBoardState boardState) {
        this.state = boardState;
        int player = boardState.getTurnPlayer();
        BeliefState startStates = new BeliefState(player);
        for (int i = 0; i < 3; i++) {
            startStates.addState(new AvailableState(boardState, i));
        }
        alphaBetaPruning(2, startStates, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        if(boardState.getTurnNumber()==0||boardState.getTurnNumber()==-1){
//            abpMiniMax.alphaBetaPruning(1, cloneBs, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        }else{
//            abpMiniMax.alphaBetaPruning(1, cloneBs, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        }
        //abpMiniMax.alphaBetaPruning(3, cloneBs, Integer.MIN_VALUE, Integer.MAX_VALUE);
        //Move myMove=abpMiniMax.getMove();

        // Return your move to be processed by the server.
        return theMove;
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
    public int alphaBetaPruning(int depth, BeliefState bs,int alpha,int beta) {
        int player = bs.getPlayer();
        int ret = 0;
        for (AvailableState s : bs.states) {
            //s.getHiddenBoard();
            // Re-initiate the best move, so that only the move at current depth will be retrieved only
            SaboteurMove bestMove = null;
            // The array to store all the best moves at current depth. Since will be re-generated every depth, only the top level moves will be retrieved
            ArrayList<SaboteurMove> bestMoves = new ArrayList<>();
            int value;
            // Find all legal moves available to current player
            ArrayList<SaboteurMove> moves = state.getAllLegalMoves();

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
            if (s.gameOver() || depth == 0 || moves.isEmpty()) {
                ret += heuristic(s);
                /* If not at a leaf node:
                 * Find all possible next states, then for each state, find the maxValue of its sucessors if it is Max Player or the MinValue of its sucessors if it is Min Player
                 */
            } else {
                for (SaboteurMove move : moves) {
                    AvailableState stateCopy = new AvailableState(s); // Make a copy of the current Tablut board state
                    stateCopy.processMove(move); // Find the resulting state through process the move on the copy
                    if (s.getTurnPlayer() == 0) { //moving first as Max Player
                        value = alphaBetaPruning(depth - 1, stateCopy.beliefState, alpha, beta);
                        if (value > alpha) {
                            alpha = value;
                            bestMove = move;
//						bestMoves.clear();
//						bestMoves.add(bestMove);
                            if (alpha == 100) { // the cutoff is game over
                                break;
                            }
                        }
//					else if(value==alpha){
//						bestMoves.add(move);
//					}
                    } else { // moving after as Mini Player
                        value = alphaBetaPruning(depth - 1, stateCopy.beliefState, alpha, beta);
                        if (value < beta) {
                            beta = value;
                            bestMove = move;
//						bestMoves.clear();
//						bestMoves.add(bestMove);
                            if (beta == -100) { // the cutoff is game over
                                break;
                            }
                        }
//					else if(value==alpha){
//						bestMoves.add(move);
//					}
                    }
                    // cutoff
                    if (alpha >= beta) {
                        break;
                    }
                }
                if (bestMove != null) {
                    bestMoves.add(bestMove);
                }
                if (!bestMoves.isEmpty()) {
                    theMove = bestMoves.get(new Random(System.currentTimeMillis()).nextInt(bestMoves.size()));
                }
//			if(bestMove!=null){
//				bestMoves.add(bestMove);
//			}
//			if(!bestMoves.isEmpty()){
//				theMove=bestMoves.get(bestMoves.size()-1);
//			}
//			theMove=(TablutMove) ((bestMove==null)?bs.getRandomMove():bestMove);
                if (s.getTurnPlayer() == player) {
                    ret += alpha;
                } else {
                    ret += beta;
                }
            }
        }
        return ret;


//        bs.getHiddenBoard();
//        // Re-initiate the best move, so that only the move at current depth will be retrieved only
//        SaboteurMove bestMove=null;
//        // The array to store all the best moves at current depth. Since will be re-generated every depth, only the top level moves will be retrieved
//        ArrayList<SaboteurMove> bestMoves=new ArrayList<>();
//        int value;
//        // Find all legal moves available to current player
//        ArrayList<SaboteurMove> moves=bs.getAllLegalMoves();
//
//        /*
//         *  PseudoCode:
//         * int MaxValue(s,a,b)
//         * 	if cutoff(s), return evaluation(s)
//         * 	for each state s' in successors(s)
//         * 		let a=max(a,min(s',a,b))
//         * 		if a>=b return b
//         * 	return a
//         * int MinValue(s,a,b)
//         * 	if cutoff(s), return evaluation(s)
//         * 	for each state s' in successor(s)
//         * 		let b=min(b,max(s',a,b))
//         * 		if a>=b return a
//         * 	return b
//         */
//
//        // If the game is over (someone won) or the leaf node is reached, get the value from heuristic
//        if(bs.gameOver()||depth==0||moves.isEmpty()){
//            return heuristic(bs);
//            /* If not at a leaf node:
//             * Find all possible next states, then for each state, find the maxValue of its sucessors if it is Max Player or the MinValue of its sucessors if it is Min Player
//             */
//        }else{
//            for(SaboteurMove move:moves){
//                SaboteurBoardState cloneBs=(SaboteurBoardState) bs.clone(); // Make a copy of the current Tablut board state
//                cloneBs.processMove(move); // Find the resulting state through process the move on the copy
//                if(bs.getTurnPlayer()==player){ //self as Max Player
//                    value=alphaBetaPruning(depth-1,cloneBs,alpha,beta);
//                    if(value>alpha){
//                        alpha=value;
//                        bestMove=move;
////						bestMoves.clear();
////						bestMoves.add(bestMove);
//                        if(alpha==100){ // the cutoff is game over
//                            break;
//                        }
//                    }
////					else if(value==alpha){
////						bestMoves.add(move);
////					}
//                }else{ // Muscovite as Mini Player
//                    value=alphaBetaPruning(depth-1,cloneBs,alpha,beta);
//                    if(value<beta){
//                        beta=value;
//                        bestMove=move;
////						bestMoves.clear();
////						bestMoves.add(bestMove);
//                        if(beta==-100){ // the cutoff is game over
//                            break;
//                        }
//                    }
////					else if(value==alpha){
////						bestMoves.add(move);
////					}
//                }
//                // cutoff
//                if(alpha>=beta){
//                    break;
//                }
//            }
//            if(bestMove!=null){
//                bestMoves.add(bestMove);
//            }
//            if(!bestMoves.isEmpty()){
//                theMove=bestMoves.get(new Random(System.currentTimeMillis()).nextInt(bestMoves.size()));
//            }
////			if(bestMove!=null){
////				bestMoves.add(bestMove);
////			}
////			if(!bestMoves.isEmpty()){
////				theMove=bestMoves.get(bestMoves.size()-1);
////			}
////			theMove=(TablutMove) ((bestMove==null)?bs.getRandomMove():bestMove);
//            if(bs.getTurnPlayer()==player){
//                return alpha;
//            }else{
//                return beta;
//            }
//        }
    }

    private int heuristic(AvailableState bs) {
        int ret = 0;
        int scale = bs.getTurnPlayer() == 0 ? 1 : -1;
        if (bs.gameOver() && bs.getWinner() == 0) {
            return 1000;
        } else if (bs.gameOver() && bs.getWinner() == 1) {
            return -1000;
        }
        if (bs.nuggetRevealed() != -1) {
            ret += scale * 10;
        }
        ret += scale * 100 * (- bs.getPlayer1nbMalus() + bs.getPlayer2nbMalus());
        ret += scale * (30 - bs.distanceToHidden());
        return ret;
    }
}
