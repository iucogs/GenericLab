package experiment;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import display.Display;

public class Trial {

	public String correctKey;
	public String trialType;
	public List<Display> displays;

	public Trial(String correctKey, String trialType, Display[] displays) {
		this(correctKey,trialType,new LinkedList<Display>(Arrays.asList(displays)));
	}
	
	public Trial(String correctKey, String trialType, List<Display> displays) {
		this.correctKey = correctKey;
		this.trialType = trialType;
		this.displays = displays;
	}
}
