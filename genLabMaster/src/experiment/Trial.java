package experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class Trial implements Iterable<Display> {

	public String correctKey;
	public String trialType;
	public List<Display> displays;
	public Block parent;
	
	//Configuration Vars
	public Boolean randomizeDisplayOrder;


	public Trial(String correctKey, String trialType, Display[] displays) {
		this(correctKey,trialType,new ArrayList<Display>(Arrays.asList(displays)));
	}
	
	public Trial(String correctKey, String trialType, List<Display> displays) {
		this.correctKey = correctKey;
		this.trialType = trialType;
		this.displays = displays;
	}

	public void setParent(Block parent) {
		this.parent = parent;
	}

	public Block getParent() {
		return parent;
	}

	@Override
	public Iterator<Display> iterator() {
		LinkedList<Display> displaysCopy = new LinkedList<Display>();
		Collections.copy(displays, displaysCopy);
		Collections.shuffle(displaysCopy);
		return displaysCopy.iterator();
	}

}
