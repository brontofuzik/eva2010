package eva2010.cv6;

import java.util.List;
import java.util.Random;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.GeneticOperator;
import org.jgap.IChromosome;
import org.jgap.Population;

public class MyBinaryMutationOperator implements GeneticOperator {
	
	private static final long serialVersionUID = 0L;
	
	@SuppressWarnings("unused")
	private Configuration configuration;
	
	private double mutationRate;
	
	private int dimension;
	
	private int precision;
	
	private Random random;
	
	public MyBinaryMutationOperator(Configuration configuration, double mutationRate, int dimension, int precision) {
		this.configuration = configuration;
		this.mutationRate = mutationRate;
		this.dimension = dimension;
		this.precision = precision;
		random = new Random();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void operate(Population population, List candidateChromosomes) {
		int populationSize = population.size();
		for (int chromosomeIndex = 0; chromosomeIndex < populationSize; chromosomeIndex++) {
			IChromosome chromosome = population.getChromosome(chromosomeIndex);
			IChromosome chromosomeClone = (IChromosome)chromosome.clone();
			mutateChromosome(chromosomeClone);
			candidateChromosomes.add(chromosomeClone);
		}
	}

	/**
	 * Mutates a chromosome.
	 * @param chromosome The chromosome to mutate.
	 */
	private void mutateChromosome(IChromosome chromosome) {
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < precision; j++) {
				double factor = (precision - j) / (double)precision;
				if (random.nextDouble() < mutationRate * factor) {
					int geneIndex = i * precision + j;
					Integer newAllele = random.nextInt(2);
					chromosome.getGene(geneIndex).setAllele(newAllele);
				}
			}
		}
	}
}
