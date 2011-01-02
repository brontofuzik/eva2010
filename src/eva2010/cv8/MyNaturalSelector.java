package eva2010.cv8;

import java.util.Random;
import org.jgap.Configuration;
import org.jgap.IChromosome;
import org.jgap.NaturalSelector;
import org.jgap.Population;
import org.jgap.util.ICloneable;

public class MyNaturalSelector extends NaturalSelector {
	
    private static final long serialVersionUID = 5941428227604964880L;

    private static Random random = new Random();

    private Configuration configuration;
    
    public MyNaturalSelector(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void empty() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean returnsUniqueChromosomes() {
        return false;
    }

    @Override
    public void select(int howManyToSelect, Population fromPopulation, Population toPopulation) {
        double averageFitness = calculateAverageFitness(fromPopulation);
        for (IChromosome chromosome : fromPopulation.getChromosomes()) {
            int offspringCount = calculateOffspringCount(chromosome, averageFitness);
            for (int i = 0; i < offspringCount; i++) {
                IChromosome offspring = (IChromosome)chromosome.clone();
                toPopulation.addChromosome(offspring);
            }
        }
    }

    @Override
    protected void add(IChromosome aChromosomeToAdd) {
        // TODO Auto-generated method stub
    }

    private double calculateAverageFitness(Population fromPopulation) {
       double fitnessSum = 0.0;
        for (IChromosome chromosome : fromPopulation.getChromosomes()) {
            fitnessSum += chromosome.getFitnessValue();
        }
        return fitnessSum / fromPopulation.size();
    }

    private int calculateOffspringCount(IChromosome chromosome, double averageFitness) {
        double relativeFitness = chromosome.getFitnessValue() / averageFitness;
        int integerPart = (int) Math.floor(relativeFitness);
        double fractionPart = relativeFitness - integerPart;
        int offspringCount = integerPart;
        if (Math.random() < fractionPart) {
            offspringCount++;
        }
        return offspringCount;
    }
}
