package experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;


public class Trial implements Iterable<Display> {

	//@JsonManagedReference
	public List<Display> displays = new LinkedList<Display>();
	//@JsonBackReference
	//public Block parent;

	public String correctKey = "";
	public String trialType = "";
	public Boolean randomizeDisplayOrder = false;
	public String trialPrompt = "";
	
	//Metadata
	public String name = "Untitled Trial";
	/**
	 * Default constructor to help make Jackson (JSON library for save/load) happy.
	 */
	public Trial()
	{
		
	}
	
	public Trial(String correctKey, String trialType, Display[] displays) {
		this(correctKey,trialType,new ArrayList<Display>(Arrays.asList(displays)));
	}
	
	public Trial(String correctKey, String trialType, List<Display> displays) {
		this.correctKey = correctKey;
		this.trialType = trialType;
		this.displays = displays;
	}

//	public void setParent(Block parent) {
//		this.parent = parent;
//	}
//
//	public Block getParent() {
//		return parent;
//	}

	@Override
	public Iterator<Display> iterator() {
		LinkedList<Display> displaysCopy = new LinkedList<Display>();
		Collections.copy(displays, displaysCopy);
		Collections.shuffle(displaysCopy);
		return displaysCopy.iterator();
	}

	@Override
	public String toString()
	{
		return "" + this.trialType.toString().toUpperCase() + ", key:" + this.correctKey;
	}
}
