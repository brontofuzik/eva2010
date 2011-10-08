package eva2010.cv9.strategies;

import eva2010.cv9.Strategy;
import eva2010.cv9.Result;

public class AlwaysCooperate extends Strategy {

    @Override
    public String getName() {
        return "Always cooperate";
    }

    @Override
    public String authorName() {
        return "Martin Pilat";
    }

    @Override
    public Move nextMove() {
        return Move.COOPERATE;
    }

    @Override
    public void reward(Result result) {
    }

    @Override
    public void reset() {
    }
}
