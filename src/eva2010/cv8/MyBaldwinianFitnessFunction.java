package eva2010.cv8;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

public class MyBaldwinianFitnessFunction extends FitnessFunction {

	// -----------------------------------------------------------
	// Fields
	// -----------------------------------------------------------
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3252949683268371731L;
	
	private RealFunction realFunction;
	
	private int improvementStepCount;
	
	private double improvementRate;
	
	// -----------------------------------------------------------
	// Constructors
	// -----------------------------------------------------------
	
	public MyBaldwinianFitnessFunction(RealFunction realFunction, int improvementStepCount, double improvementRate) {
		this.realFunction = realFunction;
		this.improvementStepCount = improvementStepCount;
		this.improvementRate = improvementRate;
	}
	
	// -----------------------------------------------------------
	// Methods
	// -----------------------------------------------------------

	@Override
	protected double evaluate(IChromosome chromosome) {
		double[] alleles = getAlleles(chromosome);
		for (int improvementStepIndex = 0; improvementStepIndex < improvementStepCount; improvementStepIndex++) {
			double[] gradient = calculateGradient(alleles);
			adjustAlleles(alleles, gradient);
		}
		
        double value = realFunction.value(alleles);
        chromosome.setApplicationData(value);
        return value;
	}
	
	// ***** PRIVATE ****
	
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
}
