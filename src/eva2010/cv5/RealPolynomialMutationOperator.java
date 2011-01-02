package eva2010.cv5;

import java.util.*;

import org.jgap.*;
import org.jgap.impl.*;

public class RealPolynomialMutationOperator implements GeneticOperator {

    /**
     *
     */
    private static final long serialVersionUID = 478521898268270468L;
    private RandomGenerator rand;
    private double mutRate;
    private final double ETA_M = 100;
    private Configuration config;

    public RealPolynomialMutationOperator(Configuration conf, double mutationRate) {
        rand = new StockRandomGenerator();
        mutRate = mutationRate;
        config = conf;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void operate(Population a_population, List chromosomes) {
        for (int i = config.getPopulationSize(); i < a_population.size(); i++) {
            mutate((Chromosome) a_population.getChromosome(i));
        }
    }

    private void mutate(Chromosome ch) {
        for (Gene g1 : ch.getGenes()) {
            DoubleGene g = (DoubleGene) g1;
            if (rand.nextDouble() > mutRate) {
                continue;
            }
            double y = (Double) g.getAllele();
            double yLow = g.getLowerBound();
            double yHi = g.getUpperBound();
            double delta1 = (y - yLow) / (yHi - yLow);
            double delta2 = (yHi - y) / (yHi - yLow);
            double rnd = rand.nextDouble();
            double mut_pow = 1.0 / (ETA_M + 1.0);
            double deltaq;
            if (rnd <= 0.5) {
                double xy = 1.0 - delta1;
                double val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, (ETA_M + 1.0)));
                deltaq = Math.pow(val, mut_pow) - 1.0;
            } else {
                double xy = 1.0 - delta2;
                double val = 2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (Math.pow(xy, (ETA_M + 1.0)));
                deltaq = 1.0 - (Math.pow(val, mut_pow));
            }
            y = y + deltaq * (yHi - yLow);
            if (y < yLow) {
                y = yLow;
            }
            if (y > yHi) {
                y = yHi;
            }
            g.setAllele(y);
        }
    }
}
