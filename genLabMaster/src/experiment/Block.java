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
	public Integer reps = 1;  //TODO: Broken or Not Yet Implemented!
	public boolean randomizeTrialOrder = false;
	public boolean leaveDisplaysOn = false;
	public Font font = null;
	public int delayBetweenTrials = 0;
	public String blockInstructions = "";
	public String blockPrompt = "";

	//Metadata
	public String name = super.toString();
	
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
	
	@Override
	public String toString()
	{
		return this.name;
	}
}
