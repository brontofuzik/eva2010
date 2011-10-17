package eva2010.cv9.strategies;

import eva2010.cv9.Result;
import eva2010.cv9.Strategy;

public class TitForTat extends Strategy {

    Result lastMove = null;

    @Override
    public String getName() {
        return "Tit for Tat";
    }
	
    @Override
    public String authorName() {
        return "Martin Pilat";
    }

    @Override
    public Move nextMove() {
        if (lastMove == null) {
            return Move.COOPERATE;
        }
        return lastMove.getOponentsMove();
    }

    @Override
    public void reward(Result result) {
        lastMove = result;
    }

    @Override
    public void reset() {
        lastMove = null;
    }
}
