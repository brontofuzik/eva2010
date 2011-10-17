package eva2010.cv10.simulation;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.StandardPostSelector;
import org.jgap.impl.TournamentSelector;
import org.jgap.impl.WeightedRouletteSelector;

import eva2010.cv9.Result;
import eva2010.cv9.Strategy;
import eva2010.cv9.Strategy.Move;

public class PrisonerSimulation {

    // <editor-fold defaultstate="collapsed" desc="Fields">

    /**
     * @param args
     */
    static int maxGenerationCount;
    static int populationSize;
    static int maxEncounters;
    static String inputFilename;

    static ArrayList<Strategy> strategies;
    static ArrayList<Integer> strategyCounts;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
	
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        loadProperties();

        loadStrategies();
	
        populationSize = 0;
        for (int i = 0; i < strategyCounts.size(); i++) {
            populationSize += strategyCounts.get(i);
        }
			
        run(0);
    }

    // Private support
	
    static void run(int number) {
        Configuration configuration = new DefaultConfiguration();
        Configuration.reset();
        try {
            configuration.setPopulationSize(populationSize);
            IChromosome sampleChromosome = createSampleChromosome(configuration);
            configuration.setSampleChromosome(sampleChromosome);
            configuration.setFitnessFunction(new PrisonerSimulationFitness(strategies,maxEncounters));

            // Natural selectors applied BEFORE genetic operators.
            //conf.addNaturalSelector(new WeightedRouletteSelector(conf), true);

            // Genetic operators.
            configuration.getGeneticOperators().clear();
            configuration.addGeneticOperator(new DummyOperator());

            // Natural selectors applied AFTER genetic operators.
            configuration.removeNaturalSelectors(false);
            //conf.addNaturalSelector(new StandardPostSelector(conf), false);
            configuration.addNaturalSelector(new TournamentSelector(configuration, 2, 1.0), false);

            List chromosomes = new ArrayList();		
            for (int i = 0; i < strategies.size(); i++) {
                for (int j = 0; j < strategyCounts.get(i); j++) {
                    Chromosome chromosome = (Chromosome)sampleChromosome.clone();
                    chromosome.getGene(0).setAllele(i);
                    chromosomes.add(chromosome);
                }
            }
			
            Genotype population = Genotype.randomInitialGenotype(configuration);
            population.getPopulation().setChromosomes(chromosomes);
	
            int[] lastCounts = new int[strategyCounts.size()];
            for (int i = 0; i < strategyCounts.size(); i++) {
                lastCounts[i] = strategyCounts.get(i);
            }
			
            for (int generationIndex = 0; generationIndex < maxGenerationCount; generationIndex++) {
                population.evolve();
				
                int[] counts = new int[strategies.size()];
				
                for (IChromosome chromosome : population.getPopulation().getChromosomes()) {
                    counts[(Integer)chromosome.getGene(0).getAllele()]++;
                }
				
                System.out.println("Generation " + (generationIndex + 1) + ": " + population.getFittestChromosome().getFitnessValue());
				
                for (int j = 0; j < lastCounts.length; j++) {
                    if (lastCounts[j] > 0 && counts[j] == 0) {
                        System.out.println("Strategy \"" + strategies.get(j).getName() + "\" died out");
                    }
                }
				
                lastCounts = counts;
            }
			
            for (int i = 0; i < lastCounts.length; i++) {
                if (lastCounts[i] > 0) {
                    System.out.println("Strategy \"" + strategies.get(i).getName() + "\" survived with " + lastCounts[i] + " individuals");
                }
            }
        }
        catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private static void loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream propertiesInputStream = new FileInputStream("cv10-simulation.properties");
            properties.load(propertiesInputStream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        maxGenerationCount = Integer.parseInt(properties.getProperty("max_generation_count", ""));
        maxEncounters = Integer.parseInt(properties.getProperty("max_encounters", ""));
        inputFilename = properties.getProperty("input_filename", "");
    }

    private static void loadStrategies() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        strategies = new ArrayList<Strategy>();
        strategyCounts = new ArrayList<Integer>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilename));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("[\t ]+");
                String name = words[0];
                Strategy strategy = (Strategy)Class.forName("eva2010.cv9.strategies." + name).newInstance();
                strategies.add(strategy);
                int count = Integer.parseInt(words[1]);
                strategyCounts.add(count);
            }
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static Chromosome createSampleChromosome(Configuration configuration) throws InvalidConfigurationException {
        Gene[] sampleGenes = new Gene[1];
        sampleGenes[0] = new IntegerGene(configuration, 0, strategies.size() - 1);
        Chromosome sampleChromosome = new Chromosome(configuration, sampleGenes);
        return sampleChromosome;
    }
}
