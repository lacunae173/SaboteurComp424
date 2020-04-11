package student_player;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.SaboteurPlayer;
import boardgame.Move;

import java.util.ArrayList;
import java.util.Collections;

public class MontePlayer extends SaboteurPlayer {
	public MontePlayer() {
        super("MontePlayer");
    }

    @Override
    public Move chooseMove(SaboteurBoardState boardState) {
        int player = boardState.getTurnPlayer();
        ArrayList<SaboteurMove> moves = boardState.getAllLegalMoves();
        SaboteurMove maxMove = null;
//        int maxWin = Integer.MIN_VALUE;
        int heu = Integer.MAX_VALUE;
        for (SaboteurMove m: moves) {
//            SaboteurBoardState state = (SaboteurBoardState) boardState.clone();
//            int win = 0;
//            state.processMove(m);
//            for (int i = 0; i < 20; i++) {
//                SaboteurBoardState bs = (SaboteurBoardState) state.clone();
//                while (!bs.gameOver()) {
//                    bs.processMove(bs.getRandomMove());
//                }
//                if (bs.getWinner() == player) {
//                    win++;
//                }
//            }
//            if (win > maxWin) {
//                maxWin = win;
//                maxMove = m;
//            }

//            BeliefState startStates = new BeliefState(player);
//            for (int i = 0; i < 3; i++) {
//                startStates.addState(new AvailableState(boardState, i));
//            }
//            startStates.processMove(m);

            BeliefState startStates = new BeliefState(player);
            for (int i = 0; i < 3; i++) {
                startStates.addState(new AvailableState(boardState, i));
            }
            try {
                startStates.processMove(m);
            }catch(ArrayIndexOutOfBoundsException e){
                System.out.println("it's ok, let's try another path.");
                continue;
            }
            ArrayList<SaboteurMove> potenMoves = new ArrayList<SaboteurMove>();
            ArrayList<Integer> heuristic =new ArrayList<Integer>();
            int win = 0;
            for (int i = 0; i < 5; i++) {
                for (AvailableState s: startStates.states) {
                	SaboteurMove move = s.getRandomMove();
                	s.processMove(move);
                	potenMoves.add(move);
                	
                    
                }
            }


//            int win = 0;
//            for (int i = 0; i < 1; i++) {
//                for (AvailableState s: startStates.states) {
//                    win += s.beliefState.simulate();
//                }
//            }
//
//            if (win > maxWin) {
//                maxWin = win;
//                maxMove = m;
//            }

//            int win = 0;
//            RandomState s = new RandomState(boardState);
//            s.processMove(m);
//            for (int i = 0; i < 40; i++) {
//                RandomState stateCopy = new RandomState(s);
//                while (!stateCopy.gameOver() && stateCopy.getTurnNumber() < 10) {
//                    stateCopy.processMove(stateCopy.getRandomMove());
//                }
//                win += heuristic(stateCopy, player);
//            }
//            if (win > maxWin) {
//                maxWin = win;
//                maxMove = m;
//            }
        }
        return maxMove;
    }

    private int heuristic(RandomState bs, int player) {
        int ret = 0;
        int scale = bs.getTurnPlayer() == player ? 1 : -1;
        if (bs.gameOver() && bs.getWinner() == 0) {
            return 300;
        } else if (bs.gameOver() && bs.getWinner() == 1) {
            return -300;
        }
        if (bs.nuggetRevealed() != -1) {
            ret += scale * 10;
        }
        if (player == 1) {
            ret += bs.getPlayer2nbMalus() * 5 - bs.getPlayer1nbMalus() * 10;
        } else {
            ret += bs.getPlayer1nbMalus() * 5 - bs.getPlayer2nbMalus() * 10;
        }
        ret += (30 - bs.distanceToHidden()) * 0.3;
        ret += 0.7 * bs.scoreTiles();
        ret += 0.6 * bs.scoreHand();

        return ret;
    }

}
