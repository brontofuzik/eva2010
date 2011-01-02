package eva2010.cv3;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.GeneticOperator;
import org.jgap.IChromosome;
import org.jgap.Population;
import org.jgap.RandomGenerator;
import org.jgap.impl.StockRandomGenerator;

public class MyMutationOperator implements GeneticOperator {

    // <editor-fold defaultstate="collapsed" desc="Fields">

    private static final long serialVersionUID = 4340195510279367566L;

    private Configuration configuration;

    private double mutationRate;

    private int heapCount;

    private Vector<Double> itemWeights;
    
//    private double averageItemWeight;
//    
//    private int[] sequence;
    
    private RandomGenerator randomGenerator;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public MyMutationOperator(Configuration configuration, double mutationRate, int heapCount, Vector<Double> itemWeights) {
        this.configuration = configuration;
        this.mutationRate = mutationRate;
        this.heapCount = heapCount;
        this.itemWeights = itemWeights;
        
//        // Calculate the average item weight.
//        int itemCount = itemWeights.size();
//        double itemWeightSum = 0.0;
//        for (int itemIndex = 0; itemIndex < itemCount; itemIndex++) {
//        	itemWeightSum += itemWeights.get(itemIndex);
//        }
//        averageItemWeight = itemWeightSum / itemWeights.size();
//        
//        // Initialize the sequence.
//        sequence = new int[itemCount];
//		for (int i = 0; i < itemCount; ++i) {
//			sequence[i] = i;
//		}
		
		randomGenerator = new StockRandomGenerator();
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Methods">

    @SuppressWarnings("unchecked")
    @Override
    public void operate(Population population, List candidateChromosomes) {
        List candidates = new ArrayList();

        for (IChromosome chromosome : population.getChromosomes()) {
            if (randomGenerator.nextDouble() > mutationRate) {
                continue;
            }
            
            IChromosome newChromosome = (IChromosome)chromosome.clone();

            // Calculate the heap weights.
            double[] heapWeights = new double[heapCount];
            for (int geneIndex = 0; geneIndex < newChromosome.size(); geneIndex++) {
                int heapIndex = (Integer)newChromosome.getGene(geneIndex).getAllele();
                heapWeights[heapIndex] += itemWeights.get(geneIndex);
            }

            // Determine the lightest and heaviest heap and their weights.
            double lightestHeapWeight = Double.MAX_VALUE;
            int lightestHeap = -1;
            double heaviestHeapWeight = -Double.MAX_VALUE;
            int heaviestHeap = -1;
            for (int heapIndex = 0; heapIndex < heapCount; heapIndex++)
            {
                // Update the minimum heap and its weight.
                if (heapWeights[heapIndex] < lightestHeapWeight) {
                    lightestHeapWeight = heapWeights[heapIndex];
                    lightestHeap = heapIndex;
                }

                // Update the maximum heap and its weight.
                if (heapWeights[heapIndex] > heaviestHeapWeight) {
                    heaviestHeapWeight = heapWeights[heapIndex];
                    heaviestHeap = heapIndex;
                }
            }
            
			double heapWeightDifference = heaviestHeapWeight - lightestHeapWeight;
			double p = heapWeightDifference / (10000);
			
			if (randomGenerator.nextDouble() < p) {
				//
				// The difference between the heaviest and the lightest heap is relatively big.
				//
				
	            // Determine the average heap weight.
	            double heapWeightSum = 0.0;
	            for (int heapIndex = 0; heapIndex < heapCount; heapIndex++) {
	            	heapWeightSum += heapWeights[heapIndex];
	            }
	            double averageHeapWeight = heapWeightSum / heapCount;
	            
	            // Determine the above average and below average heaps.
	            Vector<Integer> aboveAverageHeaps = new Vector<Integer>();
	            Vector<Integer> belowAverageHeaps = new Vector<Integer>();
	            for (int heapIndex = 0; heapIndex < heapCount; heapIndex++) {
	            	if (heapWeights[heapIndex] >= averageHeapWeight) {
	            		aboveAverageHeaps.add(heapIndex);
	            	} else {
	            		belowAverageHeaps.add(heapIndex);
	            	}
	            }
	            
	            // Choose an above average heap randomly.
	            int aboveAverageHeap = aboveAverageHeaps.get(randomGenerator.nextInt(aboveAverageHeaps.size()));
	            
	            // Choose a below average heap randomly.
	            int belowAverageHeap = belowAverageHeaps.get(randomGenerator.nextInt(belowAverageHeaps.size()));
	            
	            // Determine the items belonging to the above average heap.
	            Vector<Integer> items = new Vector<Integer>();
	            for (int itemIndex = 0; itemIndex < newChromosome.size(); itemIndex++) {
	            	if ((Integer)newChromosome.getGene(itemIndex).getAllele() == aboveAverageHeap) {
	            		items.add(itemIndex);
	            	}
	            }
	            
	            // Choose an item randomly.
	            int item = items.get(randomGenerator.nextInt(items.size()));
	            
	            // Move the random item from the above average heap to the below average heap.
	            newChromosome.getGene(item).setAllele(belowAverageHeap);    
			} else {
				//
				// The difference between the heaviest and the lightest heaps is relatively small.
				//
				
				int fromGeneIndex = randomGenerator.nextInt(newChromosome.size());
				int toGeneIndex = (fromGeneIndex == newChromosome.size() - 1) ?
						fromGeneIndex :
						fromGeneIndex + randomGenerator.nextInt(newChromosome.size() - fromGeneIndex - 1);
				for (int geneIndex1 = fromGeneIndex; geneIndex1 < toGeneIndex; geneIndex1++) {
					if (randomGenerator.nextDouble() < p) {
						int geneIndex2 = randomGenerator.nextInt(newChromosome.size());
						int temporaryGeneIndex = (Integer)newChromosome.getGenes()[geneIndex1].getAllele();
						newChromosome.getGenes()[geneIndex1].setAllele(newChromosome.getGenes()[geneIndex2].getAllele());
						newChromosome.getGenes()[geneIndex2].setAllele(temporaryGeneIndex);
					}
				}
			}
            
            candidates.add(newChromosome);   
        }

        for (Object obj : candidates) {
            candidateChromosomes.add(obj);
        }
    }

    // </editor-fold>
}
