package eva2010.cv5;

import java.util.*;
import org.jgap.*;
import org.jgap.impl.*;

public class SBXCrossoverOperator implements GeneticOperator {

    /**
     *
     */
    private static final long serialVersionUID = -4994609314340986323L;
    private double xover_rate;
    private final double EPS = 0.00001;
    private final double ETA_C = 20;
    private RandomGenerator random;
    private Configuration conf;

    public SBXCrossoverOperator(Configuration config, double cross_rate) {
        conf = config;
        xover_rate = cross_rate;
        random = new StockRandomGenerator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void operate(Population a_population, List chromosomes) {
        int popSize = conf.getPopulationSize();
        for (int i = 0; i < popSize; i += 2) {
            Chromosome p1 = (Chromosome) a_population.getChromosome(random.nextInt(popSize));
            Chromosome p2 = (Chromosome) a_population.getChromosome(random.nextInt(popSize));
            Chromosome ch1 = null;
            if (p1.getFitnessValue() > p2.getFitnessValue()) {
                ch1 = (Chromosome) p1.clone();
            } else {
                ch1 = (Chromosome) p2.clone();
            }
            p1 = (Chromosome) a_population.getChromosome(random.nextInt(popSize));
            p2 = (Chromosome) a_population.getChromosome(random.nextInt(popSize));
            Chromosome ch2 = null;
            if (p1.getFitnessValue() > p2.getFitnessValue()) {
                ch2 = (Chromosome) p1.clone();
            } else {
                ch2 = (Chromosome) p2.clone();
            }
            if (random.nextDouble() < xover_rate) {
                cross(ch1, ch2);
            }
            chromosomes.add(ch1);
            chromosomes.add(ch2);
        }
    }

    private void cross(Chromosome a, Chromosome b) {
        double y1, y2, y_low, y_hi, tmp;
        for (int i = 0; i < a.getGenes().length; i++) {
            y1 = (Double) a.getGene(i).getAllele();
            y2 = (Double) b.getGene(i).getAllele();
            if (Math.abs(y1 - y2) < EPS) {
                continue;
            }
            if (y1 > y2) {
                tmp = y1;
                y1 = y2;
                y2 = tmp;
            }
            y_low = ((DoubleGene) a.getGene(i)).getLowerBound();
            y_hi = ((DoubleGene) a.getGene(i)).getUpperBound();
            double rand = random.nextDouble();
            double beta = 1.0 + (2.0 * (y1 - y_low) / (y2 - y1));
            double alpha = 2.0 - Math.pow(beta, -(ETA_C + 1.0));
            double betaq = 0;
            if (rand <= (1.0 / alpha)) {
                betaq = Math.pow((rand * alpha), (1.0 / (ETA_C + 1.0)));
            } else {
                betaq = Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (ETA_C + 1.0)));
            }
            double c1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
            beta = 1.0 + (2.0 * (y_hi - y2) / (y2 - y1));
            alpha = 2.0 - Math.pow(beta, -(ETA_C + 1.0));
            if (rand <= (1.0 / alpha)) {
                betaq = Math.pow((rand * alpha), (1.0 / (ETA_C + 1.0)));
            } else {
                betaq = Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (ETA_C + 1.0)));
            }
            double c2 = 0.5 * ((y1 + y2) + betaq * (y2 - y1));
            if (c1 < y_low) {
                c1 = y_low;
            }
            if (c2 < y_low) {
                c2 = y_low;
            }
            if (c1 > y_hi) {
                c1 = y_hi;
            }
            if (c2 > y_hi) {
                c2 = y_hi;
            }
            if (random.nextDouble() <= 0.5) {
                a.getGene(i).setAllele(c2);
                b.getGene(i).setAllele(c1);
            } else {
                a.getGene(i).setAllele(c1);
                b.getGene(i).setAllele(c2);
            }
        }
    }
}
