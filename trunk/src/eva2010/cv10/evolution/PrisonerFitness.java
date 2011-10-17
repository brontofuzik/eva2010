package eva2010.cv10.evolution;

import java.util.ArrayList;
import java.util.Random;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import eva2010.cv9.Result;
import eva2010.cv9.Strategy;
import eva2010.cv9.Strategy.Move;

public class PrisonerFitness extends FitnessFunction {

    // <editor-fold defaultstate="collapsed" desc="Fields">

    ArrayList<Strategy> strategies;

    Random random = new Random();

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
	
    public PrisonerFitness(ArrayList<Strategy> strategies) {
        this.strategies = strategies;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods">
	
    public static Move[] toMoveArray(IChromosome chromosome) {
        Move[] moveArray = new Move[chromosome.getGenes().length];
	for (int i = 0; i < chromosome.getGenes().length; i++) {
            moveArray[i] = Move.values()[(Integer)chromosome.getGene(i).getAllele()];
        }
	return moveArray;
    }
	
    @Override
    protected double evaluate(IChromosome aSubject) {
        EvolvedStrategy evolvedStrategy = new EvolvedStrategy(toMoveArray(aSubject));

        int score = 0;

        for (Strategy strategy : strategies) {
            int strategy1Score = 0;
            int strategy2Score = 0;
            String strategy1Moves = "";
            String strategy2Moves = "";

            int rounds = 150 + random.nextInt(100);

            for (int round = 0; round < rounds; round++) {
                // 1. Determine strategy moves.
                Move strategy1Move = null;
                //try {
                    strategy1Move = evolvedStrategy.nextMove();
                //} catch (Exception e) {
                //	e.printStackTrace();
                //	sstrategy1Score = 0;
                //}
                Move strategy2Move = null;
                try {
                    strategy2Move = strategy.nextMove();
                } catch (Exception e) {
                    strategy2Score = 0;
                }

                // 2. Determine strategy results.
                Result strategy1Result = new Result(strategy1Move, strategy2Move);
                Result strategy2Result = new Result(strategy2Move, strategy1Move);

                // 3. Reward strategies.
                try {
                    evolvedStrategy.reward(strategy1Result);
                } catch (Exception e) {
                    strategy1Score = 0;
                }
                try {
                    strategy.reward(strategy2Result);
                } catch (Exception e) {
                    strategy2Score = 0;
                }

                strategy1Score += strategy1Result.getMyScore();
                strategy2Score += strategy2Result.getMyScore();
                strategy1Moves += (strategy1Move == null) ? "E" : strategy1Move.getLabel();
                strategy2Moves += (strategy2Move == null) ? "E" : strategy2Move.getLabel();
            }

            score += strategy1Score;
        }

        return score;
    }

    // </editor-fold>
}
