package eva2010.cv6;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.IntegerGene;

public class MyInterleavedRealFitnessFunction extends FitnessFunction {
	
	private static final long serialVersionUID = 2274261765244005248L;
	
	RealFunction realFunction;
	
	public MyInterleavedRealFitnessFunction(RealFunction realFunction) {
		this.realFunction = realFunction;
	}

	@Override
	protected double evaluate(IChromosome chromosome) {
		double value = realFunction.value(toDoubleArray(chromosome));
		chromosome.setApplicationData(value);
		return value;
	}

	private double[] toDoubleArray(IChromosome chromosome) {
		Gene[] genes = chromosome.getGenes();
		
		double[] applicationData = (double[])genes[0].getApplicationData();
		double lowerBound = applicationData[0];
		double upperBound = applicationData[1];
		int dimension = (int)Math.round(applicationData[2]);
		int precision = (int)Math.round(applicationData[3]);

		double[] doubleArray = new double[dimension];		
		for (int i = 0; i < dimension; i++) {
			if (genes[0] instanceof IntegerGene) {
				// Binary encoding.
				long value = 0;
				for (int j = 0; j < precision; j++) {
					int index = j * dimension + i;
					value = value * 2 + (Integer)genes[index].getAllele();
				}
				doubleArray[i] = (1.0 * value / (1L << precision)) * (upperBound - lowerBound) + lowerBound;
			} else {
				// Double encoding.
				doubleArray[i] = (Double)genes[i].getAllele();
			}		
		}	
		return doubleArray;
	}
}
