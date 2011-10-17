package eva2010.cv10.evolution;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.StandardPostSelector;
import org.jgap.impl.WeightedRouletteSelector;

import eva2010.cv9.Result;
import eva2010.cv9.Strategy;
import eva2010.cv9.Strategy.Move;

public class PrisonerEvolution {

    // <editor-fold defaultstate="collapsed" desc="Fields">

    /**
     * @param args
     */
    static int maxGenerationCount;
    static int populationSize;
    static String inputFilename;

    static ArrayList<Strategy> strategies;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods">
	
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        loadProperties();

        loadStrategies();
        
        // Add the evolved strategy.
        Move[] moves = run(0);	
        strategies.add(new EvolvedStrategy(moves));

        int[] scores = playStrategies();

        printScores(strategies.toArray(new Strategy[0]), scores);
    }

    // Private support

    private static void loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream propertiesInputStream = new FileInputStream("cv10-evolution.properties");
            properties.load(propertiesInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        maxGenerationCount = Integer.parseInt(properties.getProperty("max_generation_count", "0"));
        populationSize = Integer.parseInt(properties.getProperty("population_size", "0"));
        inputFilename = properties.getProperty("input_filename", "");
    }

    private static void loadStrategies() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        strategies = new ArrayList<Strategy>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilename));
            String line;
            while ((line = reader.readLine()) != null) {
                Strategy strategy = (Strategy)Class.forName("eva2010.cv9.strategies." + line).newInstance();
                strategies.add(strategy);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
		
    private static Move[] run(int runIndex) {
        Configuration configuration = new DefaultConfiguration();
        Configuration.reset();
        try {
            configuration.setPopulationSize(populationSize);
            configuration.setSampleChromosome(createSampleChromosome(configuration));
            configuration.setFitnessFunction(new PrisonerFitness(strategies));

            // Natural selectors applied BEFORE genetic operators.
            configuration.addNaturalSelector(new WeightedRouletteSelector(configuration), true);

            // Natural selectors applied AFTER genetic operators.
            configuration.removeNaturalSelectors(false);
            configuration.addNaturalSelector(new StandardPostSelector(configuration), false);
			
            // Create the population.
            Genotype population = Genotype.randomInitialGenotype(configuration);
            System.out.println("Generation -1: " + population.getFittestChromosome().toString());

            // Evolve the population.
            for (int generationIndex = 0; generationIndex < maxGenerationCount; generationIndex++) {
                population.evolve();
                System.out.println("Generation " + generationIndex + ": " + population.getFittestChromosome().getFitnessValue());
            }
			
            return PrisonerFitness.toMoveArray(population.getFittestChromosome());
        }
        catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Chromosome createSampleChromosome(Configuration configuration) throws InvalidConfigurationException {
        Gene[] sampleGenes = new Gene[64];
        for (int geneIndex = 0; geneIndex < 64; geneIndex++) {
            sampleGenes[geneIndex] = new IntegerGene(configuration, 0, 1);
        }
        Chromosome sampleChromosome = new Chromosome(configuration, sampleGenes);
        return sampleChromosome;
    }

    private static int[] playStrategies() {
        Random random = new Random();

        int[] scores = new int[strategies.size()];
        for (int runIndex = 0; runIndex < 10; runIndex++) {
            for (int strategy1Index = 0; strategy1Index < strategies.size(); strategy1Index++) {
                for (int strategy2Index = 0; strategy2Index < strategies.size(); strategy2Index++) {
                    if (strategy1Index == strategy2Index) {
                        continue;
                    }

                    Strategy strategy1 = strategies.get(strategy1Index);
                    Strategy strategy2 = strategies.get(strategy2Index);

                    System.err.print(strategy1.getName() + " vs. " + strategy2.getName() + ": ");

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

                    // Reset the strategies.
                    strategy1.reset();
                    strategy2.reset();

                    scores[strategy1Index] += strategy1Score;
                    scores[strategy2Index] += strategy2Score;
                    System.err.println(strategy1Score + ":" + strategy2Score);
                    System.err.println("\t" + strategy1Moves);
                    System.err.println("\t" + strategy2Moves);
                } // for (int strategy2Index = 0; strategy2Index < strategies.size(); strategy2Index++)
            } // for (int strategy1Index = 0; strategy1Index < strategies.size(); strategy1Index++)
        } // for (int runIndex = 0; runIndex < 10; runIndex++)
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

    // </editor-fold>
}
