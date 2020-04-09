package student_player;

import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurCard;

import java.util.ArrayList;

public class BeliefState {
    ArrayList<AvailableState> states = new ArrayList<>();
    private int player;

    public BeliefState(int player) {
        this.player = player;
    }

    public void addState(AvailableState state) {
        states.add(state);
    }

    public void processMove(SaboteurMove m) {
        for (AvailableState s: states) {
            s.processMove(m);
        }
    }

    public int simulate() {
        int win = 0;
        for (AvailableState s: states) {

            if (s.gameOver() || s.getTurnNumber() > 10) {

                if (s.getWinner() == player) {
                    win++;
                    //continue;
                }
            } else {
                for (SaboteurMove move : s.getAllLegalMoves()) {
                    AvailableState scp = new AvailableState(s);
                    scp.processMove(move);
                    win += scp.beliefState.simulate();
                }
            }

        }
        //System.out.println("returning");
        return win;

    }

}
