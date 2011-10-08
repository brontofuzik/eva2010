package eva2010.cv9;

import java.io.File;

import eva2010.cv9.Strategy.Move;

public class PrisonerDilemma {

    public static void main(String[] args) {		
        Strategy[] strategies = loadStrategies();
        int[] scores = playStrategies(strategies);
        printScores(strategies, scores);
    }
    
    private static Strategy[] loadStrategies() {
        
        //change this to wherever your .class files are
        File strategyDirectory = new File("D:\\projects\\eva2010\\build\\classes\\eva2010\\cv9\\strategies");		
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
	for (int strategyIndex1 = 0; strategyIndex1 < strategies.length; strategyIndex1++) {
            for (int strategyIndex2 = 0; strategyIndex2 < strategies.length; strategyIndex2++) {
                if (strategyIndex1 == strategyIndex2) {
                    continue;
                }

                Strategy strategy1 = strategies[strategyIndex1];
                Strategy strategy2 = strategies[strategyIndex2];

                System.err.print(strategy1.getName() + " vs. " + strategy2.getName() + ": ");

                int strategy1Score = 0;
                int strategy2Score = 0;
                String strategy1Moves = "";
                String strategy2Moves = "";

                for (int roundIndex = 0; roundIndex < 100; roundIndex++) {
                    Move strategy1Move = strategy1.nextMove();
                    Move strategy2Move = strategy2.nextMove();

                    Result result1 = new Result(strategy1Move, strategy2Move);
                    Result result2 = new Result(strategy2Move, strategy1Move);

                    strategy1Score += result1.getMyScore();
                    strategy2Score += result2.getMyScore();
                    strategy1Moves += strategy1Move.getLabel();
                    strategy2Moves += strategy2Move.getLabel();

                    strategy1.reward(result1);
                    strategy2.reward(result2);
                }

                System.err.println(strategy1Score + ":" + strategy2Score);
                System.err.println("\t" + strategy1Moves);
                System.err.println("\t" + strategy2Moves);

                scores[strategyIndex1] += strategy1Score;
                scores[strategyIndex2] += strategy2Score;

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
