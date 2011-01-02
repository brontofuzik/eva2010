package eva2010.cv7;

import java.util.List;
import java.util.Random;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.GeneticOperator;
import org.jgap.Population;

public class MyDEMutationOperator implements GeneticOperator {
	
	//
	// Fields
	//
	
	private static final long serialVersionUID = -7683587026884787055L;
	
	private static final Random random = new Random();

	@SuppressWarnings("unused")
	private Configuration configuration;
	
	private double mutationRate;
	
	//
	// Constructors
	//
	
	public MyDEMutationOperator(Configuration configuration, double mutationRate) {
		this.configuration = configuration;
		this.mutationRate = mutationRate;
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
			
			// 1st
			int randomChromosome1Index;
			do
				randomChromosome1Index = random.nextInt(populationSize);
			while (randomChromosome1Index == chromosomeIndex);
			Chromosome randomChromosome1 = (Chromosome)population.getChromosome(randomChromosome1Index);
			
			// 2nd
			int randomChromosome2Index;
			do
				randomChromosome2Index = random.nextInt(populationSize);
			while (randomChromosome2Index == randomChromosome1Index || randomChromosome2Index == chromosomeIndex);
			Chromosome randomChromosome2 = (Chromosome)population.getChromosome(randomChromosome2Index);
			
			// 3rd
			int randomChromosome3Index;
			do
				randomChromosome3Index = random.nextInt(populationSize);
			while (randomChromosome3Index == randomChromosome2Index || randomChromosome3Index == randomChromosome1Index || randomChromosome3Index == chromosomeIndex);
			Chromosome randomChromosome3 = (Chromosome)population.getChromosome(randomChromosome3Index);
			
            mutateChromosome(chromosome, randomChromosome1, randomChromosome2, randomChromosome3);
		}
	}
	
	private void mutateChromosome(Chromosome chromosome, Chromosome randomChromosome1, Chromosome randomChromosome2, Chromosome randomChromosome3) {
		double[] mutantVector = new double[chromosome.size()];
		for (int i = 0; i < chromosome.size(); i++) {
			mutantVector[i] = (Double)randomChromosome1.getGene(i).getAllele() +
				mutationRate * ((Double)randomChromosome2.getGene(i).getAllele() - (Double)randomChromosome3.getGene(i).getAllele());
		}
		chromosome.setApplicationData(mutantVector);
	}
}