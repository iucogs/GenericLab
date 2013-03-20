package experiment;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Experiment implements Iterable<Block>{

	public Boolean randomizeBlockOrder;
	public Point windowPosition;
	public Dimension windowSize;
	public Boolean giveFeedback;
	public String promptString;
	
	//Meta Settings
	public String scriptFilename;
	public String scriptDirectory;
	
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
