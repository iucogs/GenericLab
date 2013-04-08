package experiment;

import java.awt.Dimension;
import java.awt.Point;

public class Display {
	
	public static enum DisplayType {TEXT,IMAGE,SOUND,VIDEO;
		public static DisplayType getValueOf(String value) {
				value = value.toUpperCase();
				if (value.compareTo("PICTURE") == 0)
					return DisplayType.IMAGE;
				if (value.compareTo("WORD") == 0)
					return DisplayType.TEXT;
				for (DisplayType dt : DisplayType.values()) {
					if (dt.toString().compareTo(value) == 0) {
						return dt;
					}
				}
				throw new IllegalArgumentException("Invalid DisplayType value: " + value);
			}
	}
	public static enum PositionType {EXACT,RANDOM,RANDOM_OFFSET,CENTER,N,S,E,W,NE,NW,SE,SW;
		public static PositionType getValueOf(String value) {
			value = value.toUpperCase();
			if (value.compareTo("POSITION") == 0)
				return PositionType.EXACT;
			for (PositionType pt : PositionType.values()) {
				if (pt.toString().compareTo(value) == 0) {
					return pt;
				}
			}
			throw new IllegalArgumentException("Invalid PositionType value: " + value);
		}
	}
	

	/*  Replacing oneDisplay's 
	String typeOfStimulus = "";
	String itemDisplayed = "";
	String thePosition = "";
	int hPosition = 0;
	int vPosition = 0;
	double durationInSecs = 0;
	
	(Random,Center,N,NE,E,SE,S,SW,W,NW,Exactly,
	RandomFrom x y x-randomness y-randomness)
	-Duration(ms)
	-Persists(Default 0.  is how long in ms to keep the Display on the screen after the duration has expired, meaning the next display will be shown)
	*/
	public Trial parent;
	
	private DisplayType displayType;
	private PositionType positionType;
	private String textOrPath;  // Is built from experiment.directory + (filename of this resource)
	private Point position = new Point(0,0);
	private Dimension randomOffset = new Dimension(0,0);
	private double durationSecs;  //in seconds
	private double persistTime = 0.000;  //in seconds
	
	public Display(DisplayType dt, PositionType pt, String textOrFilename, double durationInSecs)
	{
		this.displayType = dt;
		this.positionType = pt;
		this.textOrPath = textOrFilename;
		this.durationSecs = durationInSecs;
		this.persistTime = 0;
	}
	
	public void setPosition(int xPos, int yPos) {
		this.position = new Point(xPos,yPos);
	}
	
	public void setPosition (Point newPos)
	{
		this.position = newPos;
	}
	
	public void setRandomOffset(int xOffset, int yOffset) {
		this.randomOffset = new Dimension(xOffset, yOffset);
	}
	
	public Dimension getRandomOffset() {
		return randomOffset;
	}

	public void setDurationSecs(double durationSecs) {
		this.durationSecs = durationSecs;
	}
	public double getDurationSecs() {
		return durationSecs;
	}

	public void setPersistTime(double persistTime) {
		this.persistTime = persistTime;
	}
	public double getPersistTime() {
		return persistTime;
	}


	public Point getPosition() {
		return position;
	}

	public void setPositionType(PositionType pt) {
		this.positionType = pt;
	}
	public void setPositionType(PositionType positionType, int xPos, int yPos) {
		this.position = new Point(xPos, yPos);
		this.positionType = positionType;
	}
	public void setPositionType(PositionType positionType, int xPos, int yPos, int xWiggle, int yWiggle) {
		this.position = new Point(xPos, yPos);
		this.randomOffset = new Dimension(xWiggle, yWiggle);
		this.positionType = positionType;
	}
	public PositionType getPositionType() {
		return positionType;
	}

	public String getTextOrPath() {
		return textOrPath;
	}

	public DisplayType getDisplayType() {
		return displayType;
	}
	
	
	
	
	
	
	
}
