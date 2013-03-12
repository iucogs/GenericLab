package experiment;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * 
 * @author Jay
 *	TODO: Finialize how this class works.  Decided on Merge func for now.
 *	-UNMANAGED
 *	-PARENTS (at construction time)
 *	-MANAGER CLASS
 *	-MERGE METHOD <-------- going with this for now!
 *	-merge + manager
 */

public class Configuration {

	private static Configuration defaultConfiguration;
	//Experiment 
	public Boolean randomizeBlockOrder;
	public Boolean leaveDisplayOn;
	public Point windowPosition;
	public Dimension windowSize;
	public Boolean giveFeedback;
	public String promptString;

	//Block
	public Integer timesToRun; //# of times to display each trial
	//random order, prompt, block title?
	
	//Trial
	public Boolean randomizeDisplayOrder;
	public Character correctResponseKey;
	public String reportingCategory;
	//prompt string, trial title?
		
	public Configuration(){
		//Set to defaults by merging with the default configuration.
		mergeInto(this,defaultConfiguration);
	}

	/**
	 * Update one configuration with settings from another.
	 * @param c1 Configuration to be updated
	 * @param c2 Configuration to merge in 
	 * @return
	 */
	public static Configuration mergeInto(Configuration c1, Configuration c2)
	{
		c1.randomizeBlockOrder = c2.randomizeBlockOrder != null ? c2.randomizeBlockOrder : c1.randomizeBlockOrder;
		return c1;
	}
	
	/**
	 * Merge two configurations in to a new one.
	 * @param c1 Configuration to act as default
	 * @param c2 Configuration to merge in 
	 * @return
	 */
	public static Configuration merge(Configuration c1, Configuration c2)
	{
		Configuration c = new Configuration();
		mergeInto(c,c1);
		mergeInto(c,c2);
		return c;
	}

	public static Configuration getDefaultConfiguration(){
		if (defaultConfiguration == null)
		{
			//Defaults!
			Configuration c = new Configuration();
			c.randomizeDisplayOrder = false;
			c.randomizeBlockOrder = false;
			//TODO: FINISH THIS LIST
			defaultConfiguration = c;
		}
		return defaultConfiguration;

	}
}
