package eva2010.cv2;

import java.io.BufferedReader;
import java.io.FileInputStream;
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
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.TournamentSelector;
import org.jgap.impl.SwappingMutationOperator;
import org.jgap.impl.StandardPostSelector;

import eva2010.StatsLogger;

public class Hromadky {

    // <editor-fold defaultstate="collapsed" desc="Fields">

    /**
     * The number of heaps.
     */
    static int heapCount;

    /**
     * The weights of items.
     */
    static Vector<Double> itemWeights;

    /**
     * The number of runs.
     */
    static int runCount;

    /**
     * The maximum number of generations.
     */
    static int maxGenerationCount;

    /**
     * The size of the population.
     */
    static int populationSize;

    static String fitnessLogFilename;

    static String differenceLogFilename;

    static String fittestChromosomeLogFilename;

    // </editor-fold>

    public static void main(String[] args) {
        // Read the properties from file.
        Properties properties = new Properties();
        try {
            InputStream propertiesInputStream = new FileInputStream("cv2.properties");
            properties.load(propertiesInputStream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        heapCount = Integer.parseInt(properties.getProperty("heap_count", "0"));
        runCount = Integer.parseInt(properties.getProperty("run_count", "0"));
        maxGenerationCount = Integer.parseInt(properties.getProperty("max_generation_count", "0"));
        populationSize = Integer.parseInt(properties.getProperty("population_size", "0"));  
        fitnessLogFilename = properties.getProperty("fitness_log_filename", "");
        differenceLogFilename = properties.getProperty("difference_log_filename", "");
        fittestChromosomeLogFilename = properties.getProperty("fittest_chromosome_log_filename", "");

        // Read item weights from file.
	String inputFilename = properties.getProperty("input_filename");
        itemWeights = new Vector<Double>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(inputFilename));
            String line;
            while ((line = in.readLine()) != null) {
                itemWeights.add(Double.parseDouble(line));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }

        for (int runIndex = 0; runIndex < runCount; runIndex++) {
            run(runIndex);
        }

        processResults(fitnessLogFilename);
        processResults(differenceLogFilename);
    }

    private static void run(int runIndex) {
        Configuration configuration = new DefaultConfiguration();
        Configuration.reset();
        try {
            configuration.setPopulationSize(populationSize);
            configuration.setSampleChromosome(createSampleChromosome(configuration));
            configuration.setFitnessFunction(new HromadkyFitness(itemWeights, heapCount));        
            configuration.setPreservFittestIndividual(true);
            
            // Natural selectors applied BEFORE genetic operators.
            configuration.removeNaturalSelectors(true);
            configuration.addNaturalSelector(new TournamentSelector(configuration, 15, 0.95), true);
            
            // Genetic operators.
            configuration.getGeneticOperators().remove(1);
            configuration.addGeneticOperator(new SwappingMutationOperator(configuration));
            
            // Natural selectors applied AFTER genetic operators.
            configuration.removeNaturalSelectors(false);
            configuration.addNaturalSelector(new StandardPostSelector(configuration), false);

            // Create the population.
            Genotype population = Genotype.randomInitialGenotype(configuration);
            System.out.println("Generation -1: " + population.getFittestChromosome().toString());

            OutputStreamWriter fitnessStreamWriter = new OutputStreamWriter(
                new FileOutputStream(fitnessLogFilename + "." + runIndex));

            OutputStreamWriter differenceStreamWriter = new OutputStreamWriter(
                new FileOutputStream(differenceLogFilename + "." + runIndex));

            // Evolve the population.
            for (int generationIndex = 0; generationIndex < maxGenerationCount; generationIndex++) {
                population.evolve();
                StatsLogger.log(population, fitnessStreamWriter);
                Double difference = (Double)population.getFittestChromosome().getApplicationData();
                System.out.println("Generation " + generationIndex + ": " + difference);
                differenceStreamWriter.write(difference.toString() + System.getProperty("line.separator"));
            }

            OutputStreamWriter fittestChromosomeStreamWriter = new OutputStreamWriter(
                new FileOutputStream(fittestChromosomeLogFilename + "." + runIndex));

            IChromosome fittestChromosome = population.getFittestChromosome();
            for (int geneIndex = 0; geneIndex < fittestChromosome.getGenes().length; geneIndex++) {
                fittestChromosomeStreamWriter.write(itemWeights.get(geneIndex) + " " + fittestChromosome.getGene(geneIndex).getAllele() + System.getProperty("line.separator"));
            }

            fitnessStreamWriter.close();
            differenceStreamWriter.close();
            fittestChromosomeStreamWriter.close();
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
    
    private static Chromosome createSampleChromosome(Configuration configuration) throws InvalidConfigurationException {
        Gene[] sampleGenes = new Gene[itemWeights.size()];
        for (int geneIndex = 0; geneIndex < itemWeights.size(); geneIndex++) {
            sampleGenes[geneIndex] = new IntegerGene(configuration, 0, heapCount - 1);
        }
        Chromosome sampleChromosome = new Chromosome(configuration, sampleGenes);
        return sampleChromosome;
    }
	
    private static void processResults(String logFilename) {
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
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
