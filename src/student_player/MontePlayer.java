package student_player;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.SaboteurPlayer;
import boardgame.Move;

import java.util.ArrayList;

public class MontePlayer extends SaboteurPlayer {
    @Override
    public Move chooseMove(SaboteurBoardState boardState) {
        int player = boardState.getTurnPlayer();
        ArrayList<SaboteurMove> moves = boardState.getAllLegalMoves();
        SaboteurMove maxMove = null;
        int maxWin = Integer.MIN_VALUE;
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
            int win = 0;
            for (int i = 0; i < 1; i++) {
                for (AvailableState s: startStates.states) {
                    win += s.beliefState.simulate(0);
                }
            }
            if (win > maxWin) {
                maxWin = win;
                maxMove = m;
            }

//            int win = 0;
//            RandomState s = new RandomState(boardState);
//            s.processMove(m);
//            for (int i = 0; i < 30; i++) {
//
//                while (!s.gameOver()) {
//                    s.processMove(s.getRandomMove());
//                }
//                if (s.getWinner() == player) {
//                    win++;
//                }
//            }
//            if (win > maxWin) {
//                maxWin = win;
//                maxMove = m;
//            }
        }
        return maxMove;
    }
}