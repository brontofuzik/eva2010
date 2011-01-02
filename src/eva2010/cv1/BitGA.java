package eva2010.cv1;

import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.Vector;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.StandardPostSelector;
import org.jgap.impl.WeightedRouletteSelector;

import eva2010.StatsLogger;

public class BitGA {

    // <editor-fold defaultstate="collapsed" desc="Fields">

    /**
     * The number of runs.
     */
    static int runCount;

    /**
     * The maximum number of generations.
     */
    static int maxGenerationCount;

    /*
     * The size of the population.
     */
    static int populationSize;

    /**
     * The size of the chromosome.
     */
    static int chromosomeSize;

    static String fitnessLogFilename;
 
    // </editor-fold>
		
    public static void main(String[] args)  {
        Properties properties = new Properties();
        try {
            InputStream propertiesInputStream = new FileInputStream("cv1.properties");
            properties.load(propertiesInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        runCount = Integer.parseInt(properties.getProperty("run_count", "10"));
        maxGenerationCount = Integer.parseInt(properties.getProperty("max_generation_count","20"));
        populationSize = Integer.parseInt(properties.getProperty("population_size", "30"));
        chromosomeSize = Integer.parseInt(properties.getProperty("chromosome_size","25"));
        fitnessLogFilename = properties.getProperty("fitness_log_filename", "ga.log");

        for (int runIndex = 0; runIndex < runCount; runIndex++) {
            run(runIndex);
        }

        processResults(fitnessLogFilename);
    }

    static void run(int number) {
        Configuration configuration = new DefaultConfiguration();
        Configuration.reset();

        try {
            // Create the sample chromosome.
            Gene[] sampleGenes = new Gene[chromosomeSize];
            for (int geneIndex = 0; geneIndex < chromosomeSize; geneIndex++) {
                sampleGenes[geneIndex] = new IntegerGene(configuration, 0, 1);
            }
            Chromosome sampleChromosome = new Chromosome(configuration, sampleGenes);

            // Configure the genetic algorithm.
            configuration.setSampleChromosome(sampleChromosome);
            configuration.setFitnessFunction(new BitGAFitness());
            configuration.setPopulationSize(populationSize);
            // Natural selectors applied BEFORE genetic operators.
            configuration.addNaturalSelector(new WeightedRouletteSelector(configuration), true);
            // Natural selectors applied AFTER genetic operators.
            configuration.removeNaturalSelectors(false);
            configuration.addNaturalSelector(new StandardPostSelector(configuration), false);

            // Create the population.
            Genotype population = Genotype.randomInitialGenotype(configuration);

            System.out.println("Generation -1: " + population.getFittestChromosome().toString());

            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fitnessLogFilename + "." + number));
            for (int generationIndex = 0; generationIndex < maxGenerationCount; generationIndex++) {
                population.evolve();
                System.out.println("Generation " + generationIndex + ": " + population.getFittestChromosome().toString());
                StatsLogger.log(population, out);
            }
            out.close();
        }
        catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void processResults(String logFilename) {
        Vector<Vector<Double>> bestFitnessesTable = new Vector<Vector<Double>>();
        try {
            for (int runIndex = 0; runIndex < runCount; runIndex++) {
                Vector<Double> bestFitnessesColumn = new Vector<Double>();
                BufferedReader in = new BufferedReader(new FileReader(logFilename + "." + runIndex));
                String line = null;
                while ((line = in.readLine()) != null) {
                    double bestFitness = Double.parseDouble(line.split(" ")[0]);
                    bestFitnessesColumn.add(bestFitness);
                }
                bestFitnessesTable.add(bestFitnessesColumn);
            }

            FileWriter out = new FileWriter(logFilename);
            for (int generationIndex = 0; generationIndex < maxGenerationCount; generationIndex++) {
                double minimumFitness = Double.MAX_VALUE;
                double maximumFitness = -Double.MAX_VALUE;
                double fitnessSum = 0;
                for (int runIndex = 0; runIndex < runCount; runIndex++) {
                    double fitness = bestFitnessesTable.get(runIndex).get(generationIndex);
                    minimumFitness = Math.min(fitness, minimumFitness);
                    maximumFitness = Math.max(fitness, maximumFitness);
                    fitnessSum += fitness;
                }
                double averageFitness = fitnessSum / runCount;
                out.write(minimumFitness + " " + averageFitness + " " + maximumFitness + System.getProperty("line.separator"));
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
