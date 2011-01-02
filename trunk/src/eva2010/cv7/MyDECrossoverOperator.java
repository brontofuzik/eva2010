package eva2010.cv7;

import java.util.List;
import java.util.Random;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.GeneticOperator;
import org.jgap.Population;

public class MyDECrossoverOperator implements GeneticOperator {

	//
	// Fields
	//
	
	private static final long serialVersionUID = 7192061886732597001L;
	
	private static final Random random = new Random();
	
	@SuppressWarnings("unused")
	private Configuration configuration;
	
	private double crossoverRate;
	
	//
	// Constructors
	//
	
	public MyDECrossoverOperator(Configuration configuration, double crossoverRate) {
		this.configuration = configuration;
		this.crossoverRate = crossoverRate;
	}
	
	//
	// Methods
	//

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void operate(Population population, List candidateChromosomes) {
		int populationSize = population.size();
		for (int chromosomeIndex = 0; chromosomeIndex < populationSize; chromosomeIndex++) {
			Chromosome chromosome = (Chromosome)population.getChromosome(chromosomeIndex);
			crossoverChromosome(chromosome);
		}
	}
	
	private void crossoverChromosome(Chromosome chromosome) {
		double[] mutatorVector = (double[])chromosome.getApplicationData();
		double[] trialVector = new double[mutatorVector.length];
		int randomIndex = random.nextInt(trialVector.length);
		for (int i = 0; i < mutatorVector.length; i++) {
			if (random.nextDouble() < crossoverRate || i == randomIndex) {
				trialVector[i] = mutatorVector[i];
			} else {
				trialVector[i] = (Double)chromosome.getGene(i).getAllele();
			}
		}
		chromosome.setApplicationData(trialVector);
	}
}