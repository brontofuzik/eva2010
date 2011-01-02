package eva2010.cv4;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

public class RealFitnessFunction extends FitnessFunction {

    // <editor-fold defaultstate="collapsed" desc="Fields">

    private static final long serialVersionUID = 2274261765244005248L;

    RealFunction realFunction;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">
	
    public RealFitnessFunction(RealFunction realFunction) {
        this.realFunction = realFunction;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods">
	
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

    // </editor-fold>
}
