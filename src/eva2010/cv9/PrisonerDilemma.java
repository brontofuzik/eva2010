package eva2010.cv9;

import java.io.File;

import eva2010.cv9.Strategy.Move;

public class PrisonerDilemma {

    public static void main(String[] args) {		
        Strategy[] strategies = loadStrategies();

        int[] scores = playStrategies(strategies);

        printScores(strategies, scores);
    }

    // Private support
    
    private static Strategy[] loadStrategies() {
        
        //change this to wherever your .class files are
        File strategyDirectory = new File("C:\\DATA\\projects\\EVA\\build\\classes\\eva2010\\cv9\\strategies");
        String[] strategyNames = strategyDirectory.list();
		
        Strategy[] strategies = new Strategy[strategyNames.length];
        for (int i = 0; i < strategies.length; i++) {
            String strategyName = strategyNames[i].substring(0, strategyNames[i].length() - ".class".length());
            strategyName = "eva2010.cv9.strategies." + strategyName;		
            try {
                Strategy strategy = (Strategy)Class.forName(strategyName).newInstance();
                System.err.println(strategy.getName());
                strategies[i] = strategy;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return strategies;
    }

    private static int[] playStrategies(Strategy[] strategies) {
        int[] scores = new int[strategies.length];
	for (int strategy1Index = 0; strategy1Index < strategies.length; strategy1Index++) {
            for (int strategy2Index = 0; strategy2Index < strategies.length; strategy2Index++) {
                if (strategy1Index == strategy2Index) {
                    continue;
                }

                Strategy strategy1 = strategies[strategy1Index];
                Strategy strategy2 = strategies[strategy2Index];

                System.err.print(strategy1.getName() + " vs. " + strategy2.getName() + ": ");

                int strategy1Score = 0;
                int strategy2Score = 0;
                String strategy1Moves = "";
                String strategy2Moves = "";

                for (int round = 0; round < 100; round++) {
                    // 1. Determine strategy moves.
                    Move strategy1Move = strategy1.nextMove();
                    Move strategy2Move = strategy2.nextMove();

                    // 2 Determine strategy results.
                    Result strategy1Result = new Result(strategy1Move, strategy2Move);
                    Result strategy2Result = new Result(strategy2Move, strategy1Move);

                    // 3. Reward strategies.
                    strategy1.reward(strategy1Result);
                    strategy2.reward(strategy2Result);

                    strategy1Score += strategy1Result.getMyScore();
                    strategy2Score += strategy2Result.getMyScore();
                    strategy1Moves += strategy1Move.getLabel();
                    strategy2Moves += strategy2Move.getLabel();
                }

                System.err.println(strategy1Score + ":" + strategy2Score);
                System.err.println("\t" + strategy1Moves);
                System.err.println("\t" + strategy2Moves);

                scores[strategy1Index] += strategy1Score;
                scores[strategy2Index] += strategy2Score;

                strategy1.reset();
                strategy2.reset();
            }
        }
        return scores;
    }
    
    private static void printScores(Strategy[] strategies, int[] scores) {
        for (int j = 0; j < scores.length; j++) {
            int maxScore = Integer.MIN_VALUE;
            int maxIndex = 0;

            for (int i = 0; i < scores.length; i++) {
                if (scores[i] > maxScore) {
                    maxScore = scores[i];
                    maxIndex = i;
                }
            }

            System.out.printf("%50s  %d", strategies[maxIndex].getName(), scores[maxIndex]);
            System.out.println();
            scores[maxIndex] = Integer.MIN_VALUE;
        }
    }
}
