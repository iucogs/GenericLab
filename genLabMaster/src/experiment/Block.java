package experiment;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;


public class Block implements Iterable<Trial>{
	//Data
	//@JsonManagedReference
	public List<Trial> trials = new ArrayList<Trial>(5);
	//@JsonBackReference
	//public Experiment parent;
	
	//Configuration
	public Integer reps = null;
	public boolean randomizeTrialOrder;
	public boolean leaveDisplaysOn;
	public Font font = null;
	public int delayBetweenTrials;

	/**
	 * Empty default constructor for Jackson's use.
	 */
	public Block()
	{
		
	}
	
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
