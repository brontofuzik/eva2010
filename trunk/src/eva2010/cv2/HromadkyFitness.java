package eva2010.cv2;

import java.util.Vector;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

public class HromadkyFitness extends FitnessFunction {

    // <editor-fold defaultstate="collapsed" desc="Fields">

    private static final long serialVersionUID = 3635743112978793713L;

    /**
     * The item weights.
     */
    Vector<Double> itemWeights;

    /**
     * The number of heaps.
     */
    int heapCount;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public HromadkyFitness(Vector<Double> itemWeights, int heapCount) {
        this.itemWeights = itemWeights;
        this.heapCount = heapCount;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods">
	
    @Override
    protected double evaluate(IChromosome chromosome) {
        // Calculate the heap weights.
        double[] heapWeights = new double[heapCount];
        for (int geneIndex = 0; geneIndex < chromosome.getGenes().length; geneIndex++) {
            Gene gene = chromosome.getGene(geneIndex);
            heapWeights[(Integer)gene.getAllele()] += itemWeights.get(geneIndex);
        }

        // Calculate the difference between the heaviest and the lightest heap.
        double minimumHeapWeight = Integer.MAX_VALUE;
        double maximumHeapWeight = Integer.MIN_VALUE;
        for (int heapIndex = 0; heapIndex < heapCount; heapIndex++) {
            if (heapWeights[heapIndex] < minimumHeapWeight) {
                minimumHeapWeight = heapWeights[heapIndex];
            }
            if (heapWeights[heapIndex] > maximumHeapWeight) {
                maximumHeapWeight = heapWeights[heapIndex];
            }
        }
        double heapWeightDifference = maximumHeapWeight - minimumHeapWeight;
        chromosome.setApplicationData(new Double(heapWeightDifference));

        // Calculate the heap weight mean and variance.
        double heapWeightMean = heapWeightMean(heapWeights);
        double heapWeightStandardDeviation = heapWeightStandardDeviation(heapWeights, heapWeightMean);

        return 1 / heapWeightStandardDeviation;
    }

    private double heapWeightMean(double[] heapWeights) {
        double heapWeightSum = 0.0;
        for (int heapIndex = 0; heapIndex < heapCount; heapIndex++) {
            heapWeightSum += heapWeights[heapIndex];
        }
        return heapWeightSum / heapCount;
    }

    private double heapWeightStandardDeviation(double[] heapWeights, double heapWeightMean) {
        double squareSum = 0.0;
        for (int heapIndex = 0; heapIndex < heapCount; heapIndex++) {
            squareSum += Math.pow(heapWeights[heapIndex] - heapWeightMean, 2.0);
        }
        double heapWeightVariance = squareSum / heapCount;
        return Math.sqrt(heapWeightVariance);
    }

    // </editor-fold>
}
