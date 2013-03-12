package experiment;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import display.Display;

public class Block implements Iterable<Trial>{
	//Data
	public List<Trial> trials;
	public Experiment parent;
	
	//Configuration
	public Integer reps;
	public boolean randomizeTrialOrder;

	@Override
	public Iterator<Trial> iterator() {
		LinkedList<Trial> trialsCopy = new LinkedList<Trial>();
		//Number of Reps
		for (int i = 0; i < reps; i++){
			trialsCopy.addAll(trials);
		}
		//Randomize?
		if (randomizeTrialOrder){
		Collections.shuffle(trialsCopy);
		}

		return trialsCopy.iterator();
	}
}
