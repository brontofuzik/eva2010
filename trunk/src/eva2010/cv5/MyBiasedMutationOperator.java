package eva2010.cv5;

import java.util.List;
import java.util.Random;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.GeneticOperator;
import org.jgap.IChromosome;
import org.jgap.Population;

public class MyBiasedMutationOperator implements GeneticOperator {

    private static final long serialVersionUID = 5950941397421717716L;

    @SuppressWarnings("unused")
	private Configuration configuration;

    private double mutationRate;

    private Random random;

    public MyBiasedMutationOperator(Configuration configuration, double mutationRate) {
        this.configuration = configuration;
        this.mutationRate = mutationRate;
        random = new Random();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void operate(Population population, List candidateChromosomes)  {
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
    	for (Gene gene : chromosome.getGenes()) {
    		if (random.nextDouble() < mutationRate) {
    			Double newAllele = (Double)gene.getAllele() + random.nextGaussian();
    			gene.setAllele(newAllele);
    		}
    	}
    }
}
