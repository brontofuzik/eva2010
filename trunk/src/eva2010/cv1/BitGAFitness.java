package eva2010.cv1;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

public class BitGAFitness extends FitnessFunction {

    private static final long serialVersionUID = 210164789836068426L;

    @Override
    protected double evaluate(IChromosome chromosome) {
        double fitnessZeroFirst = 0.0;
        double fitnessOneFirst = 0.0;
        for (int geneIndex = 0; geneIndex < chromosome.getGenes().length; geneIndex++) {
            Gene gene = chromosome.getGene(geneIndex);
            if (geneIndex % 2 == 0) {
                if (gene.getAllele().equals(0)) {
                    fitnessZeroFirst += 1.0;
                }
                else { // (gene.getAllele().equals(1))
                    fitnessOneFirst += 1.0;
                }
            }
            else { // (geneIndex % 2 == 1)
                if (gene.getAllele().equals(0)) {
                    fitnessOneFirst += 1.0;
                }
                else { // (gene.getAllele()/equals(1))
                    fitnessZeroFirst += 1.0;
                }
            }
        }
        return Math.max(fitnessZeroFirst, fitnessOneFirst) / chromosome.getGenes().length;
    }
}
