package eva2010.cv9.strategies;

import eva2010.cv9.Result;
import eva2010.cv9.Strategy;

/**
 *
 * @author hp
 */
public class LukasKudelaStrategy extends Strategy {

    private static final double initialCooperationProbability = 0.9;

    private static final double initialDefectionProbability = 0.1;

    private Result previousResult = null;

    private java.util.Random random = new java.util.Random();

    private double cooperationProbability = initialCooperationProbability;

    private double defectionProbability = initialDefectionProbability;

    @Override
    public String getName() {
        return "LukasKudelaStrategy";
    }

    @Override
    public String authorName() {
        return "Lukas Kudela";
    }

    @Override
    public Move nextMove() {
        if (previousResult == null) {
            return Move.values()[random.nextInt(2)];
        }

        if (random.nextDouble() < cooperationProbability) {
            // Spontanneous cooperation.
            return Move.COOPERATE;
        }

        if (random.nextDouble() < defectionProbability) {
            // Spontanneous defection.
            return Move.DECEIVE;
        }

        // Tit for tat.
        return previousResult.getOponentsMove();
    }

    @Override
    public void reward(Result result) {

        if (CooperationWasNotRewarded(result)) {
            DecreaseCooperationProbability();
        }

        if (DefectionWasNotPunished(result)) {
            IncreaseDefectionProbability();
        }

        previousResult = result;
    }

    @Override
    public void reset() {
        previousResult = null;
        cooperationProbability = initialCooperationProbability;
        defectionProbability = initialDefectionProbability;
    }

    private boolean CooperationWasNotRewarded(Result result) {
        return previousResult != null
            && previousResult.getMyMove() == Move.COOPERATE
            && result.getMyMove() == Move.DECEIVE;
    }
    
    private boolean DefectionWasNotPunished(Result result) {
        return previousResult != null
            && previousResult.getMyMove() == Move.DECEIVE
            && result.getOponentsMove() == Move.COOPERATE;
    }

    private void IncreaseCooperationProbability() {
        cooperationProbability = Math.min(cooperationProbability + 0.1, 1.0);
    }

    private void DecreaseCooperationProbability() {
        cooperationProbability = Math.max(cooperationProbability - 0.1, 0.0);
    }

    private void IncreaseDefectionProbability() {
        defectionProbability = Math.min(defectionProbability + 0.1, 1.0);
    }

    private void DecreaseDefectionProbability() {
        defectionProbability = Math.max(defectionProbability - 0.1, 0.0);
    }
}
