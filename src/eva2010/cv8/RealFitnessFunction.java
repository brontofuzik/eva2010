package eva2010.cv8;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

public class RealFitnessFunction extends FitnessFunction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8504625049026536754L;
	RealFunction realFunction;
	
    public RealFitnessFunction(RealFunction realFunction) {
        this.realFunction = realFunction;
    }
	
    @Override
    protected double evaluate(IChromosome chromosome) {
        double value = realFunction.value(chromosomeToDoubleArray(chromosome));
        chromosome.setApplicationData(value);
        return value;
    }

    private double[] chromosomeToDoubleArray(IChromosome chromosome) {
        double[] doubleArray = new double[chromosome.size()];
        for (int geneIndex = 0; geneIndex < chromosome.size(); geneIndex++) {
            doubleArray[geneIndex] = (Double)chromosome.getGene(geneIndex).getAllele();
        }
        return doubleArray;
    }
}
