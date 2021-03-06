package student_player;

import Saboteur.SaboteurBoard;
import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.*;
import boardgame.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Actions:
 * 1. Put down a tile (choose position)
 * 2. Use Malus
 * 3. Use Bonus
 * 4. Destroy a card (choose the card to destroy)
 * 5. Use map
 * 6. Drop a card (choose the card to drop)
 *
 * 1. If malus: Bonus > map > Destroy > Drop
 * 2. If not malus: use malus card if any and if the other player is not malus
 *                  (know where gold is) tile that lead to gold if any
 *                  (else) map if any
 *                         tile that move down if any
 *                         destroy a rand
 *                         drop a rand card
 *
 */

public class NearlyRandomSaboteurPlayer extends SaboteurPlayer {
    public NearlyRandomSaboteurPlayer() {
        super("NearlyRandomPlayer");
    }

    public NearlyRandomSaboteurPlayer(String name) {
        super(name);
    }

    private ArrayList<SaboteurMove> moves = null;
    private ArrayList<SaboteurMove> bonusMoves = new ArrayList<>();
    private ArrayList<SaboteurMove> mapMoves = new ArrayList<>();
    private ArrayList<SaboteurMove> destroyMoves = new ArrayList<>();
    private ArrayList<SaboteurMove> dropMoves = new ArrayList<>();
    private ArrayList<SaboteurMove> tileMoves = new ArrayList<>();
    private ArrayList<SaboteurMove> malusMoves = new ArrayList<>();

    private ArrayList<SaboteurMove> removeMoves = new ArrayList<>();

    public SaboteurMove randomMoveFromList(ArrayList<SaboteurMove> moves) {
        if (moves.size() < 1) return null;
        return moves.get(new Random().nextInt(moves.size()));
    }

//    public ArrayList<SaboteurMove> chooseTileMove(ArrayList<SaboteurMove> moves, SaboteurBoardState state) {
//    	SaboteurTile[][] board = state.getHiddenBoard();
//    	int[] pos=getPosClosestToGold(board);
//		ArrayList<SaboteurMove> possiblePos = new ArrayList<SaboteurMove>();
//		int[][] changes = {{0, -1},{0, 1},{1, 0},{-1, 0}}; //to make the test faster, we simply verify around all already placed tiles.
//	    
//	    for (SaboteurMove move: moves) {
//            SaboteurTile card = new SaboteurTile(move.getCardPlayed().getName());
//            for (int i = pos[0]; i < state.BOARD_SIZE; i++) {
//    	        for (int j = pos[1]; j < state.BOARD_SIZE; j++) {
//    	            if (board[i][j] != null) {
//    	                for (int m = 0; m < 4; m++) {
//    	                    if (0 <= i+changes[m][0] && i+changes[m][0] < state.BOARD_SIZE && 0 <= j+changes[m][1] && j+changes[m][1] < state.BOARD_SIZE) {
//    	                    	if (state.verifyLegit(card.getPath(), new int[]{i + changes[m][0], j + changes[m][1]} )){
//    	                    		possiblePos.add(move);		                         }
//    	                    }
//    	                }
//    	            }
//    	        }
//    	        
//    	    }
//            
//	    }return possiblePos;
//	    
//	   
//		 
//		    
//	}	 
        

    public ArrayList<SaboteurMove> chooseDropOrDestroy(ArrayList<SaboteurMove> moves, SaboteurBoardState state) {
        SaboteurTile[][] board = state.getHiddenBoard();
        ArrayList<SaboteurCard> hand = state.getCurrentPlayerCards();
        ArrayList<SaboteurMove> movesToConsider = new ArrayList<>();
        for (SaboteurMove m: moves) {
            SaboteurCard card = m.getCardPlayed();
            if (m.getCardPlayed() instanceof SaboteurDrop) {
            	
                int[] pos = m.getPosPlayed();
                if (pos[0] < hand.size()) {
                    card = hand.get(pos[0]);
                }

            } else if (m.getCardPlayed() instanceof SaboteurDestroy) {
            	
                int[] pos = m.getPosPlayed();
                card = board[pos[0]][pos[1]];
            }
            if (card instanceof SaboteurTile) {
                String idx = ((SaboteurTile) card).getIdx();
                if (((SaboteurTile)card).getPath()[1][1] == 0 || idx.equals("4") || idx.equals("4_flipped") || idx.equals("12") || idx.equals("12_flipped")) {
                    movesToConsider.add(m);
                }
            }
        }
//        if (movesToConsider.size() < 1) {
//            return moves.get(new Random().nextInt(moves.size()));
//        }
//        return movesToConsider.get(new Random().nextInt(movesToConsider.size()));
        return movesToConsider;
    }

    @Override
    public Move chooseMove(SaboteurBoardState boardState) throws NullPointerException{
        ArrayList<SaboteurMove> moves = boardState.getAllLegalMoves();
        bonusMoves.clear();
        mapMoves.clear();
        destroyMoves.clear();
        dropMoves.clear();

        tileMoves.clear();
        malusMoves.clear();
        SaboteurTile[][] tiles = boardState.getHiddenBoard();
        int[][] hiddenPos = boardState.hiddenPos;
        SaboteurTile[] hiddenTiles = new SaboteurTile[3];
        int[] nugget = null;
        boolean[] hidden = {false, false, false};
        int numHidden = 0;

        for(int i = 0; i < 3; i++){
            SaboteurTile tile = tiles[hiddenPos[i][0]][hiddenPos[i][1]];
            if (tile.getIdx().equals("nugget")) {
                nugget = hiddenPos[i];
                break;
            } else if (tile.getIdx().equals("8")) {
                hidden[i] = true;
                numHidden++;
            }
        }
        if (numHidden == 1) {
            for (int i = 0; i < 3; i++) {
                if (hidden[i]) {
                    nugget = hiddenPos[i];
                }
            }
        }

        SaboteurMove ret = null;

        for (SaboteurMove m: moves) {
            if (m.getCardPlayed() instanceof SaboteurBonus) {
                bonusMoves.add(m);
            }
            else if (m.getCardPlayed() instanceof SaboteurMap && boardState.nuggetRevealed()==-1) {
                mapMoves.add(m);
            }
            else if (m.getCardPlayed() instanceof SaboteurDestroy) {
                destroyMoves.add(m);
                removeMoves.add(m);
            }
            else if (m.getCardPlayed() instanceof SaboteurDrop) {
                dropMoves.add(m);
                removeMoves.add(m);
            }
            else if (m.getCardPlayed() instanceof SaboteurTile) {
                tileMoves.add(m);
            }
            else if (m.getCardPlayed() instanceof SaboteurMalus) {
                malusMoves.add(m);
            }
        }

        boolean isBlocked = boardState.getNbMalus(boardState.getTurnPlayer()) > 0;

        if (isBlocked) {
            if (bonusMoves.size() > 0) {
                return randomMoveFromList(bonusMoves);
            } else if (mapMoves.size() > 0 && nugget == null) {

                return randomMoveFromList(mapMoves);
            } else {
                ArrayList<SaboteurMove> dropOrDestroy = chooseDropOrDestroy(removeMoves, boardState);
                if (dropOrDestroy.size() > 0) {
                    ret = randomMoveFromList(dropOrDestroy);
                } else {
                    ret = randomMoveFromList(moves);
                }
            }
//            } else if (destroyMoves.size() > 0) {
//                return randomMoveFromList(destroyMoves);
//            } else if (dropMoves.size() > 0) {
//                return randomMoveFromList(dropMoves);
//            }

        } else {

            ArrayList<SaboteurMove> badTiles = new ArrayList<>();
            SaboteurMove minMove = null;
            int minDistance = 21;
//            for (SaboteurMove m: chooseTileMove(tileMoves,boardState)) {
            for (SaboteurMove m: tileMoves) {
                int[][] intBoard = boardState.getHiddenIntBoard();
                int[] pos = m.getPosPlayed();
                SaboteurTile tile = (SaboteurTile) m.getCardPlayed();

                if (!tileHasTunnel(tile) && pos[0] > 5) {
                    int[][] path = tile.getPath();
                    putCardOnIntBoard(intBoard, path, pos);
                    int distance = getDistanceToGold(intBoard, nugget);
                    if (distance < minDistance) {
                        minDistance = distance;
                        minMove = m;
                    }
                }
                else {
                    badTiles.add(m);
                }
            }

//            if (nugget != null) {
//                int minDistance = Integer.MAX_VALUE;
//                for (SaboteurMove m: tileMoves) {
//                    if (m.getCardPlayed() instanceof SaboteurTile) {
//                        int dist = distance(m.getPosPlayed(), nugget);
//                        if (dist < minDistance) {
//                            minDistance = dist;
//                            minMove = m;
//                        }
//                    }
//                }
//            } else {
//                if (mapMoves.size() > 0) {
//                    return randomMoveFromList(mapMoves);
//                }
//                int minDistance = Integer.MAX_VALUE;
//                for (SaboteurMove m: tileMoves) {
//                    if (m.getCardPlayed() instanceof  SaboteurTile) {
//                        int distance = distance(m.getPosPlayed(), hiddenPos[0]);
//                        for (int i = 1; i <3; i++) {
//                            distance = Math.min(distance(m.getPosPlayed(), hiddenPos[i]), distance);
//                        }
//                        if (distance < minDistance) {
//                            minDistance = distance;
//                            minMove = m;
//                        }
//                    }
//                }
//            }
            if (minMove != null && minDistance < 20) {
                return minMove;
            } else {
                if (mapMoves.size() > 0 && nugget == null) {
                    return randomMoveFromList(mapMoves);
                }
                if (malusMoves.size() > 0) {
                    return randomMoveFromList(malusMoves);

                }
                ArrayList<SaboteurMove> dropOrDestroy = chooseDropOrDestroy(removeMoves, boardState);
                badTiles.addAll(dropOrDestroy);
                if(badTiles.size()>0) {
                	ret = randomMoveFromList(badTiles);
                }
                else ret = boardState.getRandomMove();
            }

        }
        System.out.println("*********************"+boardState.getTurnPlayer()+"*********"+ret.getCardPlayed().getName()+"********");
        Move tmp;
       
        if(boardState.isLegal(ret)) return ret;
        else try {
        	tmp = boardState.getRandomMove();
        }catch(NullPointerException e) {
        	System.out.println("Try again");
        	tmp = boardState.getRandomMove();
        	
        }
        return tmp;
        //return randomMoveFromList(moves);
    }


    private void putCardOnIntBoard(int[][] board, int[][] path, int[] pos) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[pos[0] * 3 + i][pos[1] * 3 + j] = path[i][2-j];
            }
        }
    }

//    private int[] getPosClosestToGold(SaboteurTile[][] board) {
//        int[] ret = new int[2];
//        for (int i = 0; i < SaboteurBoardState.originPos ; i++) {
//            for (int j = 0; j <= SaboteurBoardState.BOARD_SIZE; j++) {
//            	if(board[j][i] instanceof SaboteurTile) {
//            		ret[0]=j*3;
//            		ret[1]=i*3;
//            	}
//            }
//        }
//        return ret;
//    }

    private int getDistanceToGold(int[][] intBoard, int[] nugget) {
        int originPos = SaboteurBoardState.originPos;
        int dist = Integer.MAX_VALUE;

        ArrayList<int[]> queue = new ArrayList<>(); //will store the current neighboring tile. Composed of position (int[]).
        ArrayList<int[]> visited = new ArrayList<int[]>();
        ArrayList<int[]> origin = new ArrayList<>();
        //the starting points
        queue.add(new int[]{originPos*3+1, originPos*3+1});
        queue.add(new int[]{originPos*3+1, originPos*3+2});
        queue.add(new int[]{originPos*3+1, originPos*3});
        queue.add(new int[]{originPos*3, originPos*3+1});
        queue.add(new int[]{originPos*3+2, originPos*3+1});

        while(queue.size()>0){
            int[] visitingPos = queue.remove(0);
//            if(containsIntArray(originTargets,visitingPos)){
//                return true;
//            }
            visited.add(visitingPos);
            int size = queue.size();
//            if(usingCard) addUnvisitedNeighborToQueue(visitingPos,queue,visited,BOARD_SIZE,usingCard);
//            else
            addUnvisitedNeighborToQueue(intBoard, visitingPos,queue,visited,SaboteurBoardState.BOARD_SIZE*3);
            if (queue.size() == size) {
                int d;
                if (nugget != null) {
                    d = Math.abs(visitingPos[0] - nugget[0] * 3) + Math.abs(visitingPos[1] - nugget[1] * 3);
                }
                else  d = Math.abs(visitingPos[0] - SaboteurBoardState.hiddenPos[0][0] * 3);
                dist = Math.min(dist, d);
            }
//            System.out.println(queue.size());
        }


        return dist;
    }

    private void addUnvisitedNeighborToQueue(int[][] intBoard, int[] pos,ArrayList<int[]> queue, ArrayList<int[]> visited,int maxSize){
        int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0}};
        int i = pos[0];
        int j = pos[1];
        for (int m = 0; m < 4; m++) {
            if (0 <= i+moves[m][0] && i+moves[m][0] < maxSize && 0 <= j+moves[m][1] && j+moves[m][1] < maxSize) { //if the hypothetical neighbor is still inside the board
                int[] neighborPos = new int[]{i+moves[m][0],j+moves[m][1]};
                if(!containsIntArray(visited,neighborPos)){
                    if(intBoard[neighborPos[0]][neighborPos[1]]==1) queue.add(neighborPos);
                }
            }
        }
    }
    private boolean containsIntArray(ArrayList<int[]> a,int[] o){ //the .equals used in Arraylist.contains is not working between arrays..
        if (o == null) {
            for (int i = 0; i < a.size(); i++) {
                if (a.get(i) == null)
                    return true;
            }
        } else {
            for (int i = 0; i < a.size(); i++) {
                if (Arrays.equals(o, a.get(i)))
                    return true;
            }
        }
        return false;
    }

    private boolean tileHasTunnel(SaboteurTile tile) {
        String idx = tile.getIdx();
        return tile.getPath()[1][1] == 0 || idx.equals("4") || idx.equals("4_flipped") || idx.equals("12") || idx.equals("12_flipped");
    }

    private int distance(int[] pos1, int[] pos2) {
        return Math.abs(pos1[0] - pos2[0]) + Math.abs(pos1[1] - pos2[1]);
    }
}
