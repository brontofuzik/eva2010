package eva2010.cv5;

import java.util.ArrayList;
import java.util.List;

import org.jgap.Configuration;
import org.jgap.GeneticOperator;
import org.jgap.IChromosome;
import org.jgap.Population;
import org.jgap.RandomGenerator;
import org.jgap.impl.StockRandomGenerator;

public class MyCrossoverOperator implements GeneticOperator {
	
	private static final long serialVersionUID = 1035531098296797345L;
	
	@SuppressWarnings("unused")
	private Configuration configuration;
	
	private double crossoverRate;
	
	private RandomGenerator random;

	public MyCrossoverOperator(Configuration configuration, double crossoverRate) {
		this.configuration = configuration;
		this.crossoverRate = crossoverRate;
		random = new StockRandomGenerator();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void operate(Population population, List candidateChromosomes) {
		
		// Create the intermediate population.
		List intermediatePopulation = new ArrayList();
		for (IChromosome chromosome : population.getChromosomes()) {
			intermediatePopulation.add(chromosome.clone());
		}
		
		// Ensure there is an even number of chromosomes in the intermediate population.
		if (intermediatePopulation.size() % 2 != 0) {
			IChromosome extraChromosome = (IChromosome)((IChromosome)intermediatePopulation.get(0)).clone();
			intermediatePopulation.add(extraChromosome);
		}
		
		while (!intermediatePopulation.isEmpty()) {
			IChromosome parent1 = (IChromosome)intermediatePopulation.remove(random.nextInt(intermediatePopulation.size()));
			IChromosome parent2 = (IChromosome)intermediatePopulation.remove(random.nextInt(intermediatePopulation.size()));
			IChromosome offspring1 = (IChromosome)parent1.clone();
			IChromosome offspring2 = (IChromosome)parent2.clone();
			if (random.nextDouble() < crossoverRate) {
				crossoverChromosomes(parent1, parent2, offspring1, offspring2);
			}
			candidateChromosomes.add(offspring1);
			candidateChromosomes.add(offspring2);
		}
	}
	
	private void crossoverChromosomes(IChromosome parent1, IChromosome parent2, IChromosome offspring1, IChromosome offspring2) {
		double a = random.nextDouble();
		for (int geneIndex = 0; geneIndex < parent1.size(); geneIndex++) {
			Double parent1Allele = (Double)parent1.getGene(geneIndex).getAllele();
			Double parent2Allele = (Double)parent2.getGene(geneIndex).getAllele();
			
			Double offspring1Allele = a * parent1Allele + (1 - a) * parent2Allele;
			Double offspring2Allele = (1 - a) * parent1Allele + a * parent2Allele;
			
			offspring1.getGene(geneIndex).setAllele(offspring1Allele);
			offspring2.getGene(geneIndex).setAllele(offspring2Allele);
		}
	}
}
