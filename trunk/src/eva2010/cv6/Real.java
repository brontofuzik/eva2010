package eva2010.cv6;

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
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.StandardPostSelector;

import eva2010.ElitistBreeder;
import eva2010.StatsLogger;

public class Real {
	
    /**
     * The number of runs.
     */
    static int runCount;

    /**
     * The maximum number of generations.
     */
    static int maxGenerationCount;
    
    /**
     * The population size.
     */
    static int populationSize;
     
	static String encoding;
    
	static int dimension;
	
	static int precision;
	
	static double elitePercentage;

	static String fitnessLogFilename;

	static String differenceLogFilename;
	
	static String fittestChromosomeLogFilename;

	public static void main(String[] args) {
		// Read properties from a file.
		Properties properties = new Properties();
		try {
			InputStream propertiesInputStream = new FileInputStream("cv6.properties");
			properties.load(propertiesInputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		runCount = Integer.parseInt(properties.getProperty("run_count", ""));
		maxGenerationCount = Integer.parseInt(properties.getProperty("max_generation_count", ""));
		populationSize = Integer.parseInt(properties.getProperty("population_size", ""));
		encoding = properties.getProperty("encoding", "binary");
		dimension = Integer.parseInt(properties.getProperty("dimension", ""));
		precision = Integer.parseInt(properties.getProperty("precision", ""));
		elitePercentage = Double.parseDouble(properties.getProperty("elite_percentage", ""));
        fitnessLogFilename = properties.getProperty("fitness_log_filename", "");
        differenceLogFilename = properties.getProperty("difference_log_filename", "");
        fittestChromosomeLogFilename = properties.getProperty("fittest_chromosome_log_filename", "");

		for (int runIndex = 0; runIndex < runCount; runIndex++) {
			run(runIndex);
		}

		processResults(fitnessLogFilename);
		processResults(differenceLogFilename);
	}
	
	static void run(int runIndex) {
		Configuration configuration = new DefaultConfiguration();
		Configuration.reset();
		try {
			configuration.setPopulationSize(populationSize);
			configuration.setSampleChromosome(sampleChromosome(configuration));
			//configuration.setFitnessFunction(new RealFitnessFunction(new RastriginFunction()));
			configuration.setFitnessFunction(new MyInterleavedRealFitnessFunction(new RastriginFunction()));
			//configuration.setPreservFittestIndividual(true);
			configuration.setBreeder(new ElitistBreeder(0.1));
			
            // Natural selectors applied BEFORE genetic operators.
            configuration.removeNaturalSelectors(true);
            //configuration.addNaturalSelector(new WeightedRouletteSelector(configuration), true);
            //configuration.addNaturalSelector(new TournamentSelector(configuration, 10, 0.95), true);
            configuration.addNaturalSelector(new MyNaturalSelector(configuration), true);
			
            // Genetic operators.
            //configuration.getGeneticOperators().remove(1);
            //configuration.getGeneticOperators().add(new MyBinaryMutationOperator(configuration, 0.1, dimension, precision));
            
			// Natural selectors applied AFTER genetic operators.
			configuration.removeNaturalSelectors(false);
			configuration.addNaturalSelector(new StandardPostSelector(configuration), false);
			
			Genotype population = Genotype.randomInitialGenotype(configuration);

			System.out.println("Generation -1: " + population.getFittestChromosome().toString());

            OutputStreamWriter fitnessStreamWriter = new OutputStreamWriter(
                    new FileOutputStream(fitnessLogFilename + "." + runIndex));
			
            OutputStreamWriter differenceStreamWriter = new OutputStreamWriter(
                    new FileOutputStream(differenceLogFilename + "." + runIndex));

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
                fittestChromosomeStreamWriter.write(fittestChromosome.getGene(geneIndex).getAllele() + System.getProperty("line.separator"));
            }
			
            fitnessStreamWriter.close();
            differenceStreamWriter.close();
            fittestChromosomeStreamWriter.close();	
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Chromosome sampleChromosome(Configuration configuration) throws InvalidConfigurationException {
		double[] applicationData = new double[4];
		applicationData[0] = -5;
		applicationData[1] = 5;
		applicationData[2] = dimension;
		applicationData[3] = precision;
		
		Gene[] sampleGenes = null;
		if (encoding.equals("binary")) {
			sampleGenes = new Gene[dimension * precision];
			for (int geneIndex = 0; geneIndex < dimension * precision; geneIndex++) {
				sampleGenes[geneIndex] = new IntegerGene(configuration, 0, 1);
				sampleGenes[geneIndex].setApplicationData(applicationData);
			}
		} else {
			sampleGenes = new Gene[dimension];
			for (int geneIndex = 0; geneIndex < dimension; geneIndex++) {
				sampleGenes[geneIndex] = new DoubleGene(configuration, -5, 5);
				sampleGenes[geneIndex].setApplicationData(applicationData);
			}
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
