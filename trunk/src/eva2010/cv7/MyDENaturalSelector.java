package eva2010.cv7;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.IChromosome;
import org.jgap.NaturalSelector;
import org.jgap.Population;

public class MyDENaturalSelector extends NaturalSelector {
	
	//
	// Fields
    //
	
	private static final long serialVersionUID = -6641643781074357480L;
	
	@SuppressWarnings("unused")
	private static final Random random = new Random();
	
	@SuppressWarnings("unused")
	private Configuration configuration;
	
	//
	// Constructors
	//
	
	public MyDENaturalSelector(Configuration configuration) {
		this.configuration = configuration;
	}
	
	//
	// Methods
	//

	@Override
	public void empty() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean returnsUniqueChromosomes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void select(int howManyToSelect, Population fromPopulation, Population toPopulation) {
		for (IChromosome chromosome : fromPopulation.getChromosomes()) {
			// Create trial chromosome.
			Chromosome trialChromosome = (Chromosome)chromosome.clone();
			double[] trialVector = (double[])chromosome.getApplicationData();
			for (int geneIndex = 0; geneIndex < trialChromosome.size(); geneIndex++) {
				trialChromosome.getGene(geneIndex).setAllele(trialVector[geneIndex]);
			}
			
			Class<?> klass = chromosome.getClass();
			try {
				Method method = klass.getDeclaredMethod("calcFitnessValue");
				method.setAccessible(true);
				// Calculate fitness value of the chromosome.
				method.invoke(chromosome);
				// Calculate fitness value of the trial chromosome.
				method.invoke(trialChromosome);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (chromosome.getFitnessValue() >= trialChromosome.getFitnessValue()) {
				toPopulation.addChromosome(chromosome);
			} else {
				toPopulation.addChromosome(trialChromosome);
			}
		}
	}
	
	// ***** Protected *****

	@Override
	protected void add(IChromosome chromosome) {
		// TODO Auto-generated method stub
	}
}
