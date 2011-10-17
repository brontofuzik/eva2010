package eva2010.cv10.simulation;

import java.util.ArrayList;
import java.util.Random;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import eva2010.cv10.evolution.EvolvedStrategy;
import eva2010.cv9.Result;
import eva2010.cv9.Strategy;
import eva2010.cv9.Strategy.Move;

public class PrisonerSimulationFitness extends FitnessFunction {

    // <editor-fold defaultstate="collapsed" desc="Fields">

    ArrayList<Strategy> strategies;

    Random random = new Random();

    int maxEncounters;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
	
    public PrisonerSimulationFitness(ArrayList<Strategy> strategies, int maxEncounters) {
        this.strategies = strategies;
        this.maxEncounters = maxEncounters;
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
    protected double evaluate(IChromosome chromosome) {
		
        Strategy strategy1 = strategies.get((Integer)chromosome.getGene(0).getAllele());
				
        int score = 0;
	
        for (int i = 0; i < maxEncounters; i++) {
            Strategy strategy2 = strategies.get(random.nextInt(strategies.size()));
			
            int strategy1Score = 0;
            int strategy2Score = 0;
            String strategy1Moves = "";
            String strategy2Moves = "";

            int rounds = 150 + random.nextInt(100);
            for (int round = 0; round < rounds; round++) {
                // 1. Determine the strategy moves.
                Move strategy1Move = null;
                try {
                    strategy1Move = strategy1.nextMove();
                }
                catch (Exception e) {
                    strategy1Score = 0;
                }
                Move strategy2Move = null;
                try {
                    strategy2Move = strategy2.nextMove();
                }
                catch (Exception e) {
                    strategy2Score = 0;
                }

                // 2. Determine the strategy results.
                Result strategy1Result = new Result(strategy1Move, strategy2Move);
                Result strategy2Result = new Result(strategy2Move, strategy1Move);

                // 3. Reward the strategies.
                try {
                    strategy1.reward(strategy1Result);
                }
                catch (Exception e) {
                    strategy1Score = 0;
                }
                try {
                    strategy2.reward(strategy2Result);
                }
                catch (Exception e) {
                    strategy2Score = 0;
                }

                strategy1Score += strategy1Result.getMyScore();
                strategy2Score += strategy2Result.getMyScore();
                strategy1Moves += (strategy1Move == null) ? "E" : strategy1Move.getLabel();
                strategy2Moves += (strategy2Move == null) ? "E" : strategy2Move.getLabel();
            } // for (int round = 0; round < rounds; round++)
			
            score += strategy1Score;
        } // for (int i = 0; i < maxEncounters; i++)
		
        return score;
    }

    // </editor-fold>
}
