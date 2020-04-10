package student_player;

import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurCard;

import java.util.ArrayList;

public class BeliefState {
    ArrayList<AvailableState> states = new ArrayList<>();

    public int getPlayer() {
        return player;
    }

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

//    public int simulate() {
//        int win = 0;
//        for (AvailableState s: states) {
//
//            if (s.gameOver() || s.getTurnNumber() > 3) {

    public int simulate(int win) {
        int dist = Integer.MAX_VALUE;
        for (AvailableState s : states) {
            if (s.gameOver() || s.getTurnNumber() > 50) {
                if (s.getWinner() == player) {
                    win++;
                    //continue;
                }
            } else {
//                SaboteurMove move = s.getRandomMove();
//                AvailableState scp = new AvailableState(s);
//                scp.processMove(move);
//                win += scp.beliefState.simulate();
                SaboteurMove tmp = s.getRandomMove();
                //For a current state, generate 10 possible moves and use heuristic function to select the best one
                for(int i =10;i>0;i--){
                    SaboteurMove move = s.getRandomMoveFromPM();
                    AvailableState scp = new AvailableState(s);
                    scp.processMove(move);
                    if(scp.distanceToHidden()<dist){
                        dist = scp.distanceToHidden();
                        tmp = move;
                    }

                }
                SaboteurMove move = tmp;
                AvailableState scp = new AvailableState(s);
                scp.processMove(move);
                addState(scp);
            }
            return s.beliefState.simulate(win);
        }

        //System.out.println("returning");
        return win;
    }

}
