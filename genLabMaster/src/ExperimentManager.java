import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.sound.sampled.Clip;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import display.Display;
import display.Display.DisplayType;
import display.Display.PositionType;
import experiment.Block;
import experiment.Experiment;
import experiment.Trial;


public class ExperimentManager {

	/**
	 * Loads an Experiment from an old script file.  Returns null if loading fails.
	 * @param genLab
	 * @param varsP
	 * @return
	 */
	public static Experiment loadExperiment(ExptVarsPanel varsP)
	{
		Experiment ex = new Experiment();
		Block block = new Block(); //Default Block
		ex.blocks.add(block);
		boolean success; 
		success = initializeExperiment(ex,varsP);//Initial Experiment setup from ExptVarsPanel
		if (!success)
			return null;
		success = parseScriptFile(ex,varsP);
		if (!success)
			return null;
		success = loadExptPanelSettings(ex,varsP);
		if (!success)
			return null;
		
		return ex;
	}
	
	private static boolean initializeExperiment(Experiment ex, ExptVarsPanel exptVarsP)
	{
		Block block = ex.blocks.get(0);
		ex.scriptFilename = exptVarsP.getScript();
		if (ex.scriptFilename.equals("")) {
			varError(exptVarsP,0, "");  //TODO varError: keep this setup?
			return false;
		}
		ex.scriptDirectory = exptVarsP.getScriptDirectory();
		// get repetitions
		int reps;
		try {
			block.reps = Integer.parseInt(exptVarsP.getReps());
			reps = block.reps;
			if (reps <= 0) {
				varError(exptVarsP,1, "");
				return false;
			}
		} catch (NumberFormatException nfe) {
			varError(exptVarsP,1, "");
			return false;
		}
		
		block.randomizeTrialOrder = exptVarsP.getTrialOrder(); 
		
		String fontFace = exptVarsP.getFontFace();
		int fontSize = exptVarsP.getFontSize();
		block.font = new Font(fontFace, Font.PLAIN, fontSize);
		
		boolean usePrompt = exptVarsP.getPrompt();
		if (usePrompt)
			ex.promptString = exptVarsP.getPromptString();
		else
			ex.promptString = "";
		
		ex.giveFeedback = exptVarsP.getFeedback();
		try {
			block.delayBetweenTrials = Integer.parseInt(exptVarsP.getDelay());
			if (block.delayBetweenTrials < 0) {
				varError(exptVarsP,4, "");
				return false;
			}
		} catch (NumberFormatException nfe) {
			varError(exptVarsP,4, "");
			return false;
		}
		return true;
	}
	
	private static boolean parseScriptFile(Experiment ex, Component container){
		String scriptString, trialString;
		int numTrials = 0;
		int numDisplays = 0;
		String trialType = "", correctKeyString = "";

		ex.usableKeys = new ArrayList<String>();
		ex.trialTypes = new ArrayList<String>();

		// steps for parsing script into vector of vectors containing trial
		// specifics
		// ...
		//
		ex.blocks.get(0).trials = new ArrayList<Trial>();
		List<Trial> trials = ex.blocks.get(0).trials;		


		scriptString = "";
		trialString = "";
		ex.instructionsFilename = "";
		
		String tempDebugString = "";
		try {
			File scriptFile = new File(ex.scriptFilename);
			BufferedReader br = new BufferedReader(new FileReader(scriptFile)); 
			//TODO: Change reader for applet?  Seems OK for now!.
			scriptString = br.readLine();
			if (scriptString.startsWith("instructions")) {
				String[] temp = scriptString.split(" ", 2);
				ex.instructionsFilename = temp[1];
				scriptString = br.readLine();
				if (scriptString.trim().equals("")) {
					scriptString = br.readLine();
				}
			}
			try {
				numTrials = Integer.parseInt(scriptString);

				tempDebugString = tempDebugString + scriptString;

				while ((trialString = br.readLine()) != null) {
					tempDebugString = tempDebugString + "\n" + trialString;
				}

			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(container,
						"Number of trials must be an integer >= 0",
						"Variable error", JOptionPane.ERROR_MESSAGE);
				return false;
			}

			if (numTrials < 0) {
				varError(container,5, "");
				return false;
			}

			Vector tokenVector = new Vector();
			String[] results = tempDebugString.split("\\s");
			String tempString = "";
			for (int x = 0; x < results.length; x++) {
				tempString = results[x].trim();
				if (tempString.equals("")) {

				} else {
					tokenVector.addElement(tempString);
				}
			}

			int tokenCtr = 0;

			numTrials = Integer.parseInt((String) tokenVector
					.elementAt(tokenCtr++));

			// - Read in the Trials
			ex.includeAllLetters = false;
			ex.includeAllNumbers = false;
			for (int j = 0; j < numTrials; j++) {
				numDisplays = Integer.parseInt((String) tokenVector
						.elementAt(tokenCtr++));

				if (tokenVector.elementAt(tokenCtr).equals("#")) {
					ex.includeAllNumbers = true;
				} else if (tokenVector.elementAt(tokenCtr).equals("*")) {
					ex.includeAllLetters = true;
				}

				correctKeyString = (String) tokenVector.elementAt(tokenCtr++);

				if (!ex.usableKeys.contains(correctKeyString)) {
					ex.usableKeys.add(correctKeyString);
				}

				trialType = (String) tokenVector.elementAt(tokenCtr++);

				if (!ex.trialTypes.contains(trialType)) {
					ex.trialTypes.add(trialType);
				}

				Display[] displays = new Display[numDisplays];
				//- Load in each Display -//
				for (int i = 0; i < numDisplays; i++) {

					String stimulusTypeStr = (String) tokenVector.elementAt(tokenCtr++);
					DisplayType displayType = DisplayType.getValueOf(stimulusTypeStr.toUpperCase());
					double duration = 1;
					String textOrPath = "NOT-YET-SET";
					String thePosition = "CENTER";
					
					switch(displayType)
					{
						case TEXT:
							boolean foundPosition = false;
							textOrPath = "";
							System.out.println("Loading Text!");
							while (!foundPosition) {
								int tempCtr = 0;
								String tempString2 = (String) tokenVector.elementAt(tokenCtr++);
	
								if ((tempString2.equals("position"))
										|| (tempString2.equals("center"))
									 	|| (tempString2.equals("random"))) {
									foundPosition = true;
									tokenCtr--;
								} else {
									textOrPath = textOrPath + " " + tempString2;
									tempCtr++;
									if (tempCtr > 10) {
										foundPosition = true;
									}
								}
	
							}
							textOrPath = textOrPath.trim();
							break;
						case IMAGE:
							textOrPath = ex.scriptDirectory + (String) tokenVector.elementAt(tokenCtr++);
							Image tempImg = Toolkit.getDefaultToolkit().getImage(textOrPath);
	
							try {
								//Load it up , just to verify if it works.
								MediaTracker tracker = new MediaTracker(container);
								tracker.addImage(tempImg, 0);
								tracker.waitForID(0);
								int tempWidth = tempImg.getWidth(container);
								int tempHeight = tempImg.getHeight(container);
								if ((tempWidth <= 0) || (tempHeight <= 0)) {
									varError(container,8, textOrPath);
									return false;
								}
								// TODO: Clean up these comments
								// if (tempWidth > horiz) {
								// varError(6, itemPresented);
								// return(0);
								// }
								//
								// if (tempHeight > vert) {
								// varError(7, itemPresented);
								// return(0);
								// }
							} catch (Exception e) {e.printStackTrace();}
							break;
						case VIDEO: 
							textOrPath = (String) tokenVector.elementAt(tokenCtr++);
//					TODO: Make sure this disabling is OK.  No video until new model is used.
//							int tempIndex = runP.presPan.videoNameVector.indexOf(textOrPath);
//							if (tempIndex == -1) {
//								runP.presPan.videoNameVector.addElement(textOrPath);
//								runP.presPan.prepVideo(experiment.scriptDirectory + textOrPath);
//							}
							break;
						case SOUND:
							textOrPath =  ex.scriptDirectory
									+ (String) tokenVector.elementAt(tokenCtr++);
							//Load it up , just to verify if it works.
						Clip theClip = PresentationPanel.loadAudioClip(textOrPath);
							if (theClip == null) {
								varError(container,9, textOrPath);
								return false;
							}
							duration = theClip.getMicrosecondLength() / 1000000.0;
							break;
					}//End Switch
					int hPosition = -1, vPosition = -1;
					//Set Duration and Position for non-sounds
					if (!displayType.equals(DisplayType.SOUND))
					{
						thePosition = (String) tokenVector.elementAt(tokenCtr++);
					// THis is implied
					//	thePosition = (String) tokenVector
					//	.elementAt(tokenCtr - 1);

						if (thePosition.equals("position")) {
							hPosition = Integer.parseInt((String) tokenVector
									.elementAt(tokenCtr++));
							vPosition = Integer.parseInt((String) tokenVector
									.elementAt(tokenCtr++));
						}
						//TODO: 1. fix 'thePosition' type. 2. cases for other PositionTypes
						String tempString3 = (String) tokenVector
								.elementAt(tokenCtr++);
						try{
							duration = Double.parseDouble(tempString3);
						} 
						catch (NumberFormatException nfe) {
							System.out.println("NumberFormatException: "
									+ nfe.getMessage());
						}
					}
//					OneDisplay oneDisplay = new OneDisplay();
//					oneDisplay.buildDisplay(stimulusType, textOrPath,
//							thePosition, hPosition, vPosition, theDuration);
					Display disp = new Display(displayType,PositionType.getValueOf(thePosition.toUpperCase()),textOrPath,duration);
					disp.setPosition(hPosition,vPosition);
					displays[i] = disp;
				}
				Trial oneTrial = new Trial(correctKeyString,trialType,displays);
				trials.add(oneTrial);
			}
		} catch (FileNotFoundException fnfe) {
			JOptionPane.showMessageDialog(container, "File not found error:  "
					+ fnfe, "File error", JOptionPane.ERROR_MESSAGE);
			System.out.println("trying to read file  " + fnfe);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(container, "Error reading from file:  "
					+ ioe, "File error", JOptionPane.ERROR_MESSAGE);
		}
		
		return true;
	}

	private static boolean loadExptPanelSettings(Experiment ex, ExptVarsPanel exptVarsP)
	{
		// Replicate each trial based on reps number
		int reps = ex.blocks.get(0).reps;
		List<Trial> trials = ex.blocks.get(0).trials;
		if (reps > 1) {
			int initialSize = trials.size();
			for (int i = 0; i < reps - 1; i++) // each rep past 1
			{
				for (int j = 0; j < initialSize; j++)
				{
					trials.add(trials.get(j));
				}
			}
		}
		// Reorder trial vector if necessary
		if (ex.blocks.get(0).randomizeTrialOrder) {
			java.util.Collections.shuffle(trials);
		}
		// Setup trial boundaries and display orders
		int horiz, vert;
		try {
			horiz = Integer.parseInt(exptVarsP.getHorizRange());
			if ((horiz <= 0) || (horiz > 390)) {
				varError(exptVarsP,2, "");
				return false;
			}
		} catch (NumberFormatException nfe) {
			varError(exptVarsP,2, "");
			return false;
		}

		try {
			vert = Integer.parseInt(exptVarsP.getVertRange());
			if ((vert <= 0) || (vert > 250)) {
				varError(exptVarsP,3, "");
				return false;
			}
		} catch (NumberFormatException nfe) {
			varError(exptVarsP,3, "");
			return false;
		}
		for (Trial t : trials){
			t.randomizeDisplayOrder = exptVarsP.getDisplayOrder();
			for (Display d : t.displays){
				d.setRandomOffset(horiz,vert);
			}
		}
		for (int i = 0; i < trials.size(); i++) {
			Trial t = (Trial) trials.get(i);
			if (t.randomizeDisplayOrder) {
				Collections.shuffle(t.displays);
			}
		}
		ex.blocks.get(0).trials = trials;
		// JOptionPane.showMessageDialog(this, "test");
		ex.instructions = "";
		if (!ex.instructionsFilename.equals("")) {
			String instructionsString = "";
			try {

				String instructionsPathPlusFilename = exptVarsP.scriptDirectory
						+ "\\" + ex.instructionsFilename;
				File scriptFile = new File(instructionsPathPlusFilename);
				BufferedReader br = new BufferedReader(new FileReader(
						scriptFile));

				while ((instructionsString = br.readLine()) != null) {
					ex.instructions = ex.instructions + "\n"
							+ instructionsString;
				}
			} catch (FileNotFoundException fnfe) {
				// JOptionPane.showMessageDialog(this, "File not found error:  "
				// + fnfe, "File error", JOptionPane.ERROR_MESSAGE);
				System.out.println("trying to read file  " + fnfe);
			} catch (IOException ioe) {
				// JOptionPane.showMessageDialog(this,
				// "Error reading from file:  " + ioe, "File error",
				// JOptionPane.ERROR_MESSAGE);
			}
		}
		return true;
	}
	
	private static void varError(Component container, int errorNum, String errorString) {

		JDialog.setDefaultLookAndFeelDecorated(true);

		String[] errStrings = new String[] { // indexed by errorNum
				"No script name specified.",
				"Must have at least 1 repetition of trials.",
				"Horizontal range must be an integer greater than 0 and less than or equal to 390.",
				"Vertical range must be an integer greater than 0 and less than or equal to 250.",
				"The delay in milliseconds must be an integer >= 0.",
				"Number of trials must be an integer >= 0.",
				"The width of image " + errorString + " is too large.",
				"The height of image " + errorString + " is too large.",
				"Cannot find image " + errorString,
				"Cannot find sound file " + errorString };

		JOptionPane.showMessageDialog(container, errStrings[errorNum],
				"Variable error", JOptionPane.ERROR_MESSAGE);

	}
}
