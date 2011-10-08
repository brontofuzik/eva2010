package eva2010.cv9.strategies;

import eva2010.cv9.Result;
import eva2010.cv9.Strategy;

public class AlwaysDeceive extends Strategy {

    @Override
    public String getName() {
        return "Always deceive";
    }

    @Override
    public String authorName() {
        return "Martin Pilat";
    }

    @Override
    public Move nextMove() {
        return Move.DECEIVE;
    }

    @Override
    public void reward(Result result) {
    }

    @Override
    public void reset() {
    }
}
