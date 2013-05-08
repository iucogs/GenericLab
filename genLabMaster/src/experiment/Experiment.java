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

	public Boolean randomizeBlockOrder;
	public Point windowPosition;
	public Dimension windowSize;
	public Boolean giveFeedback,includeAllNumbers,includeAllLetters;
	public String promptString;
	
	//Meta Settings
	public String scriptFilename;  //This path is absolute, from JFC
	public String instructionsFilename;
	public String instructions;
	public String directory;  //This is the current directory of the JFC when the file was chosen.   Includes a trailing separator.
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
	
	

}
