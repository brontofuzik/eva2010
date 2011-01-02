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

public class PeterBasistaMutationOperator implements GeneticOperator {


	private static final long serialVersionUID = 4340195510279367566L;
	private double mutationRate;
	private Configuration conf;
	private RandomGenerator rand;
	private Vector<Double> weights;
	private int[] sequence;
	private int chromosome_length;
	private int average_weight;
	private int K;
	
	public PeterBasistaMutationOperator(Configuration conf, double mutationRate) {
		this.mutationRate = mutationRate;
		this.conf = conf;
		rand = new StockRandomGenerator();
	}

	public PeterBasistaMutationOperator(Vector<Double> init_weights, int init_K, Configuration conf, double mutationRate) {
		weights = init_weights;
		K = init_K;
		chromosome_length = weights.size();
		this.sequence = new int[chromosome_length];
		this.mutationRate = mutationRate;
		this.conf = conf;
		rand = new StockRandomGenerator();
		average_weight = 0;
		for (int i = 0; i < chromosome_length; ++i) {
			average_weight += weights.get(i);
			this.sequence[i] = i;
		}
		average_weight /= chromosome_length;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void operate(Population aPopulation, List aCandidateChromosomes) {
		
		List candidates = new ArrayList();
		
		for (IChromosome ch : aPopulation.getChromosomes()) {

			if (rand.nextDouble() > mutationRate) {
				continue;
			}

			IChromosome newCh = (IChromosome)ch.clone();
			Gene[] genes = newCh.getGenes();

			double[] pile_weights = new double[K];
			for (int i = 0; i < genes.length; ++i) {
				pile_weights[(Integer)genes[i].getAllele()] += weights.get(i);
			}

			int j = 0;
			int g = 0;
			double avg = 0;
			double lightest_pile = Double.MAX_VALUE;
			double heaviest_pile = -Double.MAX_VALUE;

			for (int i = 0; i < K; ++i) {
				if (pile_weights[i] > heaviest_pile) {
					heaviest_pile = pile_weights[i];
				} else if (pile_weights[i] < lightest_pile) {
					lightest_pile = pile_weights[i];
				}
				avg += pile_weights[i];
			}
			
//			double average_item_weight = avg / ch.size();
			avg /= K;
//			System.out.printf("lightest_pile: %f, heaviest_pile: %f, average_item_weight: %f\n", lightest_pile, heaviest_pile, average_item_weight);
			
//			double pile_weight_range = heaviest_pile - lightest_pile;
//			double small_changes_probability = pile_weight_range / (7000);
//
//			if (rand.nextDouble() >= small_changes_probability) {
//				int b = rand.nextInt(chromosome_length);
//				int n = 0;
//				if (b == chromosome_length - 1) {
//					n = b;
//				} else {
//					n = b + rand.nextInt(chromosome_length - b - 1);
//				}
//				for (int i = b; i < n; ++i) {
//					if (rand.nextDouble() >= small_changes_probability) {
//						continue;
//					} else {
//						j = rand.nextInt(chromosome_length);
//						g = (Integer)genes[i].getAllele();
//						genes[i].setAllele(genes[j].getAllele());
//						genes[j].setAllele(g);
//					}
//				}
//				candidates.add(newCh);
////				System.out.printf("small_changes_probability: %f\n", small_changes_probability);
//				continue;
//			}
			
			int[] permutation = sequence.clone();
			for (int i = 0; i < chromosome_length; ++i) {
				j = rand.nextInt(chromosome_length);
				g = permutation[i];
				permutation[i] = permutation[j];
				permutation[j] = g;
			}

			Vector<Integer> first_list = new Vector<Integer>(), second_list = new Vector<Integer>();
			int[] balanced = new int[K];
			
			for (int i = 0; i < chromosome_length; ++i) {
				g = (Integer)genes[permutation[i]].getAllele();
				if (pile_weights[g] > avg && balanced[g] == 0) {
					first_list.add(permutation[i]);
					pile_weights[g] -= weights.get(permutation[i]);
					if (pile_weights[g] <= avg) {
						balanced[g] = 1;
					}
				} else if (pile_weights[g] < avg && balanced[g] == 0) {
					second_list.add(permutation[i]);
					pile_weights[g] += average_weight;
					if (pile_weights[g] >= avg) {
						balanced[g] = 1;
					}					
				}
			}
			
			int min = Math.min(first_list.size(), second_list.size());
//			System.out.printf("first: %d, second: %d\n", first_list.size(), second_list.size());
			
			for (int i = 0; i < min; ++i) {
				if (rand.nextDouble() > mutationRate) {
					g = (Integer)genes[first_list.get(i)].getAllele();
					genes[first_list.get(i)].setAllele(genes[second_list.get(i)].getAllele());
					pile_weights[(Integer)genes[second_list.get(i)].getAllele()] += weights.get(first_list.get(i)) - average_weight;
					genes[second_list.get(i)].setAllele(g);
					pile_weights[g] += weights.get(second_list.get(i));
				} else {
					genes[first_list.get(i)].setAllele(genes[second_list.get(i)].getAllele());
					pile_weights[(Integer)genes[second_list.get(i)].getAllele()] += weights.get(first_list.get(i)) - average_weight;
				}
			}

			int max = Math.max(first_list.size(), second_list.size());;
			int a = 0;
			if (first_list.size() >= second_list.size()) {
				for (int i = min; i < max; ++i) {
					if (rand.nextDouble() > mutationRate) {
						a = rand.nextInt(K);
						genes[first_list.get(i)].setAllele(a);
					}
				}
			} else {
				for (int i = min; i < max; ++i) {
					if (rand.nextDouble() > mutationRate) {
						a = rand.nextInt(chromosome_length);
						genes[a].setAllele(genes[second_list.get(i)].getAllele());
					}
				}				
			}

			
			/*************************
			 * 
			 * svoji mutaci napiste sem, newCh muzete libovolne upravovat,
			 * nakonec ho nezapomente pridat mezi candidates
			 */
			
			
			candidates.add(newCh);
//			newCh.setIsSelectedForNextGeneration(true);
		}		

		for (Object o : candidates) {
			aCandidateChromosomes.add(o);
		}
		
	}

}
