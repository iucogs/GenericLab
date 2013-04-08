package experiment;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class Block implements Iterable<Trial>{
	//Data
	public List<Trial> trials = new ArrayList<Trial>(5);
	public Experiment parent;
	
	//Configuration
	public Integer reps;
	public boolean randomizeTrialOrder;
	public boolean leaveDisplaysOn;
	public Font font;
	public int delayBetweenTrials;

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
