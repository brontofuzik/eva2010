package eva2010.cv9.strategies;

import eva2010.cv9.Result;
import eva2010.cv9.Strategy;

public class Random extends Strategy {

    java.util.Random rnd = new java.util.Random();

    @Override
    public String getName() {
        return "Random";
    }

    @Override
    public String authorName() {
        return "Martin Pilat";
    }

    @Override
    public Move nextMove() {
        return Move.values()[rnd.nextInt(2)];
    }

    @Override
    public void reward(Result result) {
    }

    @Override
    public void reset() {
    }
}
