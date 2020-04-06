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
            SaboteurBoardState state = (SaboteurBoardState) boardState.clone();
            int win = 0;
            state.processMove(m);
            for (int i = 0; i < 100; i++) {
                SaboteurBoardState bs = (SaboteurBoardState) state.clone();
                while (!bs.gameOver()) {
                    bs.processMove(bs.getRandomMove());
                }
                if (bs.getWinner() == player) {
                    win++;
                }
            }
            if (win > maxWin) {
                maxWin = win;
                maxMove = m;
            }
        }
        return maxMove;
    }
}
