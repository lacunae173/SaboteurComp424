package student_player;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.*;
import boardgame.Move;

import java.util.ArrayList;
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

    public SaboteurMove randomMoveFromList(ArrayList<SaboteurMove> moves) {
        return moves.get(new Random().nextInt(moves.size()));
    }

    @Override
    public Move chooseMove(SaboteurBoardState boardState) {
        ArrayList<SaboteurMove> moves = boardState.getAllLegalMoves();
        bonusMoves.clear();
        mapMoves.clear();
        destroyMoves.clear();
        dropMoves.clear();
        tileMoves.clear();
        malusMoves.clear();
        for (SaboteurMove m: moves) {
            if (m.getCardPlayed() instanceof SaboteurBonus) {
                bonusMoves.add(m);
            }
            else if (m.getCardPlayed() instanceof SaboteurMap) {
                mapMoves.add(m);
            }
            else if (m.getCardPlayed() instanceof SaboteurDestroy) {
                destroyMoves.add(m);
            }
            else if (m.getCardPlayed() instanceof SaboteurDrop) {
                dropMoves.add(m);
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
            } else if (mapMoves.size() > 0) {
                return randomMoveFromList(mapMoves);
            } else if (destroyMoves.size() > 0) {
                return randomMoveFromList(destroyMoves);
            } else if (dropMoves.size() > 0) {
                return randomMoveFromList(dropMoves);
            }
        } else {
            SaboteurTile[][] tiles = boardState.getHiddenBoard();
            int[][] hiddenPos = boardState.hiddenPos;
            SaboteurTile[] hiddenTiles = new SaboteurTile[3];
            int[] nugget = null;
            for(int i = 0; i < 3; i++){
                SaboteurTile tile = tiles[hiddenPos[i][0]][hiddenPos[i][1]];
                if (tile.getIdx().equals("nugget")) {
                    nugget = hiddenPos[i];
                    break;
                }
            }
            SaboteurMove minMove = null;
            if (nugget != null) {
                int minDistance = Integer.MAX_VALUE;
                for (SaboteurMove m: tileMoves) {
                    if (m.getCardPlayed() instanceof SaboteurTile) {
                        int dist = distance(m.getPosPlayed(), nugget);
                        if (dist < minDistance) {
                            minDistance = dist;
                            minMove = m;
                        }
                    }
                }
            } else {
                if (mapMoves.size() > 0) {
                    return randomMoveFromList(mapMoves);
                }
                int minDistance = Integer.MAX_VALUE;
                for (SaboteurMove m: tileMoves) {
                    if (m.getCardPlayed() instanceof  SaboteurTile) {
                        int distance = distance(m.getPosPlayed(), hiddenPos[0]);
                        for (int i = 1; i <3; i++) {
                            distance = Math.min(distance(m.getPosPlayed(), hiddenPos[i]), distance);
                        }
                        if (distance < minDistance) {
                            minDistance = distance;
                            minMove = m;
                        }
                    }
                }
            }
            if (minMove != null) {
                return minMove;
            } else {
                if (malusMoves.size() > 0) {
                    return randomMoveFromList(malusMoves);

                }
                destroyMoves.addAll(dropMoves);
                return randomMoveFromList(destroyMoves);
            }

        }
        return randomMoveFromList(moves);
    }

    private int distance(int[] pos1, int[] pos2) {
        return Math.abs(pos1[0] - pos2[0]) + Math.abs(pos1[1] - pos2[1]);
    }
}
