package experiment;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

public class Experiment implements Iterable<Block>{

	/*TODO LIST:
	 *	ROB CHANGES:
	 *		CHECK implement block level prompts
	 *		implement display level Prompt strings
	 *			CHECK field has been added
	 *			-add them to builder  
	 *			-actually use them during run
	 *		-implement Experiment and Block level instructions
	 *			*fields have been added
	 *			-need to actually display them during run
	 *		CHECK update 'use all letters/numbers'
	 *		-Move giveFeedback to block level	
	 *			-add field
	 *			-implement in builder
	 *			-actually implement
	 *		-AS A RESULT, REDRAW RUN PANEL: Invisible box for prompts, 
	 *				display feedback below that, display block instructs below that, THEN run/abort button.
	 *				Make experimentInstructions a pop-up.
	*/
	
	//TODO: Implement BlockSet? 
	
	public Boolean randomizeBlockOrder = false;
	public Point windowPosition; // TODO Use this? Has not been implemented 7/2013
	public Dimension windowSize; // TODO Use this? Has not been implemented 7/2013
	public Boolean giveFeedback = false,
					includeAllNumbers = false,
					includeAllLetters = false;
	//public String promptString = "";
	
	//Meta Settings
	public String name = "Untitled Experiment";
	public String experimentFilename = "";  //Name of the script or json file
	public String instructionsFilename_SCRIPT_LEGACY = "";
	public String experimentInstructions = "";
	public String directory = "";  //This is the current directory of the JFC when the file was chosen.   Includes a trailing separator.
	public List<String> usableKeys;
	public List<String> trialTypes;
	
	//@JsonManagedReference
	public List<Block> blocks = new ArrayList<Block>(1);
	
	@Override
	public Iterator<Block> iterator() {
		LinkedList<Block> blocksCopy = new LinkedList<Block>();
		blocksCopy.addAll(blocks);
		if(randomizeBlockOrder)
		{
			Collections.shuffle(blocksCopy);
		}
		return blocksCopy.iterator();
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	

}
