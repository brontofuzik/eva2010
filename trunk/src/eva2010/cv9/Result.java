package eva2010.cv9;

import eva2010.cv9.Strategy.Move;

public class Result {
	
    int[][] rewards = {{3,0},{5,1}};
	
    Move myMove;

    Move opponentsMove;
	
    Result(Move myMove, Move opponentsMove) {
        this.myMove = myMove;
        this.opponentsMove = opponentsMove;
    }

    public Move getMyMove() {
            return myMove;
    }
	
    public Move getOpponentsMove() {
            return opponentsMove;
    }
	
    public int getMyScore() {
        return rewards[myMove.ordinal()][opponentsMove.ordinal()];
    }
	
    public int getOpponentsScore() {
        return rewards[myMove.ordinal()][opponentsMove.ordinal()];
    }
}
