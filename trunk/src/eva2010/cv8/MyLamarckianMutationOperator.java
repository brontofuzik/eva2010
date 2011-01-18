package eva2010.cv8;

import java.util.List;
import org.jgap.GeneticOperator;
import org.jgap.IChromosome;
import org.jgap.Population;

public class MyLamarckianMutationOperator implements GeneticOperator {

	// -----------------------------------------------------------
	// Fields
	// -----------------------------------------------------------
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6255911486613397906L;
	
	private int improvementStepCount;
	
	private double improvementRate;
	
	// -----------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------
	
	public MyLamarckianMutationOperator(int improvementStepCount, double improvementRate) {
		this.improvementStepCount = improvementStepCount;
		this.improvementRate = improvementRate;
	}
	
	// -----------------------------------------------------------
	// Methods
	// -----------------------------------------------------------

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void operate(Population population, List candidateChromosomes) {
		int populationSize = population.size();
		for (int chromosomeIndex = 0; chromosomeIndex < populationSize; chromosomeIndex++) {
			IChromosome chromosome = (IChromosome)population.getChromosome(chromosomeIndex).clone();
			mutateChromosome(chromosome);
			candidateChromosomes.add(chromosome);
		}
	}
	
	// ***** PRIVATE *****
	
	private void mutateChromosome(IChromosome chromosome) {
		double[] alleles = getAlleles(chromosome);
		for (int improvementStepIndex = 0; improvementStepIndex < improvementStepCount; improvementStepIndex++) {
			double[] gradient = calculateGradient(alleles);
			adjustAlleles(alleles, gradient);
		}
		setAlleles(chromosome, alleles);
	}
	
	private double[] getAlleles(IChromosome chromosome) {
		double[] alleles = new double[chromosome.size()];
		for (int geneIndex = 0; geneIndex < chromosome.size(); geneIndex++) {
			alleles[geneIndex] = (Double)chromosome.getGene(geneIndex).getAllele();
		}
		return alleles;
	}
	
	private double[] calculateGradient(double[] x) {
		double[] gradient = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			gradient[i] = (-2 * x[i] -20 * Math.PI * Math.sin(2 * Math.PI * x[i])) / (45 * x.length);
		}
		return gradient;
	}
	
	private void adjustAlleles(double[] alleles, double[] gradient) {
		for (int i = 0; i < alleles.length; i++) {
			alleles[i] += (improvementRate * gradient[i]);
		}
	}
	
	private void setAlleles(IChromosome chromosome, double[] alleles) {
		for (int geneIndex = 0; geneIndex < chromosome.size(); geneIndex++) {
			chromosome.getGene(geneIndex).setAllele(alleles[geneIndex]);
		}
	}
}
