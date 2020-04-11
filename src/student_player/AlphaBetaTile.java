package student_player;
import Saboteur.SaboteurBoard;
import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurTile;

import java.util.ArrayList;
import java.util.Random;

import static Saboteur.SaboteurBoardState.*;


public class AlphaBetaTile {

    private SaboteurMove theMove; // to be returned to StudentPlayer
    public final int maxPlayer;

    // Constructor
    public AlphaBetaTile(int maxPlayer){
        this.maxPlayer = maxPlayer;
    }

    /**
     * MiniMax with alpha-beta pruning to a certain depth
     *
     * @param depth
     * @param alpha
     * @param beta
     * @return alpha or beta depends on the player role
     */
    public int alphaBetaPruning(int depth, SaboteurTile[][] board, int[][] intBoard, ArrayList<SaboteurMove> moves, int alpha, int beta, int turnPlayer){
        // Re-initiate the best move, so that only the move at current depth will be retrieved only
        SaboteurMove bestMove=null;
        // The array to store all the best moves at current depth. Since will be re-generated every depth, only the top level moves will be retrieved
        ArrayList<SaboteurMove> bestMoves=new ArrayList<>();
        int value;
        // Find all legal moves available to current player
//        ArrayList<TablutMove> moves=bs.getAllLegalMoves();

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
        int[] nugget = NearlyRandomSaboteurPlayer.findNugget(board);
        if(NearlyRandomSaboteurPlayer.getDistanceToGold(intBoard, nugget) == 0||depth==0||moves.isEmpty()){
            return heuristic(board, intBoard, nugget, turnPlayer);
            /* If not at a leaf node:
             * Find all possible next states, then for each state, find the maxValue of its sucessors if it is Max Player or the MinValue of its sucessors if it is Min Player
             */
        }else{
            for(SaboteurMove move:moves){
                SaboteurTile[][] boardCopy = new SaboteurTile[BOARD_SIZE][BOARD_SIZE];
                int[][] intBoardCopy = new int[BOARD_SIZE * 3][BOARD_SIZE * 3];
                for (int i = 0; i < BOARD_SIZE; i++) {
                    System.arraycopy(board[i], 0, boardCopy[i], 0, BOARD_SIZE);
                }
                for (int i = 0; i < BOARD_SIZE * 3; i++) {
                    System.arraycopy(intBoard[i], 0, intBoardCopy[i], 0, BOARD_SIZE * 3);
                }

                // Make a copy of the current Tablut board state
                processMove(move, boardCopy, intBoardCopy); // Find the resulting state through process the move on the copy
                ArrayList<SaboteurTile> hand = new ArrayList<>();
                String[] tiles = {"0", "5", "6", "7", "8", "9", "10"};
                for (int i = 0; i < tiles.length; i++) {
                    hand.add(new SaboteurTile(tiles[i]));
                }
                ArrayList<SaboteurMove> legalMoves = new ArrayList<>();
                for (SaboteurTile card: hand) {
                    ArrayList<int[]> allowedPositions = possiblePositions(boardCopy, card);
                    for(int[] pos:allowedPositions){
                        legalMoves.add(new SaboteurMove(card,pos[0],pos[1],turnPlayer));
                    }
                    //if the card can be flipped, we also had legal moves where the card is flipped;
                    if(SaboteurTile.canBeFlipped(card.getIdx())){
                        SaboteurTile flippedCard = card.getFlipped();
                        ArrayList<int[]> allowedPositionsflipped = possiblePositions(boardCopy, flippedCard);
                        for(int[] pos:allowedPositionsflipped){
                            legalMoves.add(new SaboteurMove(flippedCard,pos[0],pos[1],turnPlayer));
                        }
                    }

                }
                if(turnPlayer == maxPlayer){ //Swede as Max Player

                    value=alphaBetaPruning(depth-1,boardCopy, intBoardCopy, legalMoves, alpha,beta, 1-turnPlayer);
                    if(value>alpha){
                        alpha=value;
                        bestMove=move;
//						bestMoves.clear();
//						bestMoves.add(bestMove);
                        if(alpha==1000){ // the cutoff is game over
                            break;
                        }
                    }
//					else if(value==alpha){
//						bestMoves.add(move);
//					}
                }else{ // Muscovite as Mini Player
                    value=alphaBetaPruning(depth-1,boardCopy, intBoardCopy, legalMoves,alpha,beta, 1-turnPlayer);
                    if(value<beta){
                        beta=value;
                        bestMove=move;
//						bestMoves.clear();
//						bestMoves.add(bestMove);
                        if(beta==-1000){ // the cutoff is game over
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
            if(turnPlayer == maxPlayer){
                return alpha;
            }else{
                return beta;
            }
        }
    }



    private void processMove(SaboteurMove m, SaboteurTile[][] board, int[][] intBoard) {
        if (m.getCardPlayed() instanceof SaboteurTile) {
            board[m.getPosPlayed()[0]][m.getPosPlayed()[1]] = (SaboteurTile) m.getCardPlayed();
            NearlyRandomSaboteurPlayer.putCardOnIntBoard(intBoard, ((SaboteurTile) m.getCardPlayed()).getPath(), m.getPosPlayed());
        }

    }

    /**
     * Get the best move
     * @return theMove
     */
    public SaboteurMove getMove(){
        return theMove;
    }

    public int heuristic(SaboteurTile[][] board, int[][] intBoard, int[] nugget, int turnPlayer){
        int scale = turnPlayer == maxPlayer ? -1: 1;
        int distance = NearlyRandomSaboteurPlayer.getDistanceToGold(intBoard, nugget);
        if (distance == 0) {
            return scale * 1000;
        }
        int ret = 800;
        ret -= distance;
        for (int i = 0; i <= 5; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != null) {
                    ret -= 5;
                }
            }
        }
        for (int i = 5; i < BOARD_SIZE; i++) {
            for (int j = 3; j <= 7; j++) {
                if (board[i][j] != null && !NearlyRandomSaboteurPlayer.tileHasTunnel(board[i][j])) {
                    ret += 2;
                } else {
                    ret -= 2;
                }
            }
        }
        return ret * scale;
    }

    public ArrayList<int[]> possiblePositions(SaboteurTile[][] board, SaboteurTile card) {
        // Given a card, returns all the possiblePositions at which the card could be positioned in an ArrayList of int[];
        // Note that the card will not be flipped in this test, a test for the flipped card should be made by giving to the function the flipped card.
        ArrayList<int[]> possiblePos = new ArrayList<int[]>();
        int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0}}; //to make the test faster, we simply verify around all already placed tiles.
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = originPos; j < BOARD_SIZE; j++) {
                if (board[i][j] != null) {
                    for (int m = 0; m < 4; m++) {
                        if (0 <= i+moves[m][0] && i+moves[m][0] < BOARD_SIZE && 0 <= j+moves[m][1] && j+moves[m][1] < BOARD_SIZE) {
                            if (this.verifyLegit(board, card.getPath(), new int[]{i + moves[m][0], j + moves[m][1]} )){
                                possiblePos.add(new int[]{i + moves[m][0], j +moves[m][1]});
                            }
                        }
                    }
                }
            }
        }
        return possiblePos;
    }
    public boolean verifyLegit(SaboteurTile[][] board, int[][] path,int[] pos){
        // Given a tile's path, and a position to put this path, verify that it respects the rule of positionning;
        if (!(0 <= pos[0] && pos[0] < BOARD_SIZE && 0 <= pos[1] && pos[1] < BOARD_SIZE)) {
            return false;
        }
        if(board[pos[0]][pos[1]] != null) return false;

        //the following integer are used to make sure that at least one path exists between the possible new tile to be added and existing tiles.
        // There are 2 cases:  a tile can't be placed near an hidden objective and a tile can't be connected only by a wall to another tile.
        int requiredEmptyAround=4;
        int numberOfEmptyAround=0;

        ArrayList<SaboteurTile> objHiddenList=new ArrayList<>();
        for(int i=0;i<3;i++) {
            objHiddenList.add(board[hiddenPos[i][0]][hiddenPos[i][1]]);

        }
        //verify left side:
        if(pos[1]>0) {
            SaboteurTile neighborCard = board[pos[0]][pos[1] - 1];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                if (path[0][0] != neighborPath[2][0] || path[0][1] != neighborPath[2][1] || path[0][2] != neighborPath[2][2] ) return false;
                else if(path[0][0] == 0 && path[0][1]== 0 && path[0][2] ==0 ) numberOfEmptyAround +=1;
            }
        }
        else numberOfEmptyAround+=1;

        //verify right side
        if(pos[1]<BOARD_SIZE-1) {
            SaboteurTile neighborCard = board[pos[0]][pos[1] + 1];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                if (path[2][0] != neighborPath[0][0] || path[2][1] != neighborPath[0][1] || path[2][2] != neighborPath[0][2]) return false;
                else if(path[2][0] == 0 && path[2][1]== 0 && path[2][2] ==0 ) numberOfEmptyAround +=1;
            }
        }
        else numberOfEmptyAround+=1;

        //verify upper side
        if(pos[0]>0) {
            SaboteurTile neighborCard = board[pos[0]-1][pos[1]];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                int[] p={path[0][2],path[1][2],path[2][2]};
                int[] np={neighborPath[0][0],neighborPath[1][0],neighborPath[2][0]};
                if (p[0] != np[0] || p[1] != np[1] || p[2] != np[2]) return false;
                else if(p[0] == 0 && p[1]== 0 && p[2] ==0 ) numberOfEmptyAround +=1;
            }
        }
        else numberOfEmptyAround+=1;

        //verify bottom side:
        if(pos[0]<BOARD_SIZE-1) {
            SaboteurTile neighborCard = board[pos[0]+1][pos[1]];
            if (neighborCard == null) numberOfEmptyAround += 1;
            else if(objHiddenList.contains(neighborCard)) requiredEmptyAround -= 1;
            else {
                int[][] neighborPath = neighborCard.getPath();
                int[] p={path[0][0],path[1][0],path[2][0]};
                int[] np={neighborPath[0][2],neighborPath[1][2],neighborPath[2][2]};
                if (p[0] != np[0] || p[1] != np[1] || p[2] != np[2]) return false;
                else if(p[0] == 0 && p[1]== 0 && p[2] ==0 ) numberOfEmptyAround +=1; //we are touching by a wall
            }
        }
        else numberOfEmptyAround+=1;

        if(numberOfEmptyAround==requiredEmptyAround)  return false;

        return true;
    }

}
