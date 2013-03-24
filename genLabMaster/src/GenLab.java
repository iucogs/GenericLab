//package genlab;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;
import java.lang.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.applet.*;
import java.net.URL;
//import javax.media.CannotRealizeException;
//import javax.media.Manager;
//import javax.media.NoPlayerException;
//import javax.media.Player;
import java.net.MalformedURLException;
import java.applet.Applet.*;
import javax.sound.sampled.*;

import display.Display;
import display.Display.DisplayType;
import display.Display.PositionType;
import experiment.Block;
import experiment.Experiment;
import experiment.Trial;

//import javax.media.bean.playerbean.MediaPlayer;

public class GenLab extends JApplet implements ComponentListener {

	// panel variables
	JTabbedPane tabbedPane;
	JPanel instruct1Panel, instruct2Panel;
	TrialVarsPanel trialVarsP;
	ExptVarsPanel exptVarsP;
	RunPanel runP;
	ResultsPanel resultsP;

	String instructionsScreen1Path, instructionsScreen2Path;

	Timer timer1a; // Shows next display, starts 1b
	Timer timer1b; // Clears away latest display
	Timer timer2a; // Displays feedback, starts 2b
	Timer timer2b; //
	
	Action timer1aAction, timer1bAction, timer2aAction, timer2bAction;
    Action keyAction;	// stops 2b, starts 1.

	String keyStruck;
	
	Experiment experiment; //// NEW DATA STRUCTURE ////
	Block currBlock;

	int trialCtr = 0, displayCtr = 0;
	int hPosition = 0, vPosition = 0, hVal = 0, vVal = 0;

	boolean acceptKeyStroke = false, eraseAfterLastDisplay = true;
	boolean runningExperiment = false;
	
	double startRxnTimeMeasure = 0, stopRxnTimeMeasure = 0, rxnTime = 0;
	double tempstart, tempstop, temptime;

	//Vector vectorOfTrialsz;
	Vector vct;
	//Vector vectorOfUsableKeys, vectorOfTrialTypes;
	List<Response> userResponses = new ArrayList<Response>();

	Trial oneTrial;
	List<Display> currentDisplays;
	StreamTokenizer st, streamTokenizer;
	String imagePath; //directoryString
	// Player mediaPlayer;
	Component videoComponent;

	char[] arrayOfLetters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	char[] arrayOfNumbers = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };

	public GenLab() {

		// super("Generic Lab");
		// addWindowListener(new WindowAdapter() {
		// public void windowClosing(WindowEvent e) {
		// System.exit(0);
		// }
		// });
		
		/*
		 * TODO: add the standard applet methods
		 * TAKE applet security advice in bottom of this page:
		 * http://stackoverflow.com/questions/235258/jfilechooser-use-within-japplet
		 */
		tabbedPane = new JTabbedPane();
		setupTabbedPane();
		getContentPane().add(tabbedPane);
		// pack();

		runP.presPan.videoPanVector = new Vector();
		runP.presPan.mediaPlayerVector = new Vector();
		runP.presPan.videoNameVector = new Vector();

		runP.startJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runExperiment(); // This button should be disabled => this
									// action unperformable
			} // if the parse script failed.
		});

		runP.instructionsJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(runP, experiment.instructions,
						"Instructions", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		timer1aAction = new AbstractAction() {
			boolean shouldDraw = false;

			public void actionPerformed(ActionEvent e) {

				tempstop = System.currentTimeMillis();
				temptime = tempstop - tempstart;
				tempstart = System.currentTimeMillis();
				Display currentDisplay = (Display) currentDisplays.get(displayCtr);

				PositionType pt = currentDisplay.getPositionType();

				if (pt.equals(PositionType.EXACT)) {
					hVal = currentDisplay.getPosition().x;
					vVal = currentDisplay.getPosition().y;
				} else if (pt.equals(PositionType.CENTER)) {
					hVal = -1;
					vVal = -1;
				} else if (pt.equals(PositionType.RANDOM)) {
					hVal = -2;
					vVal = -2;
				}

				if (experiment.giveFeedback) {
					runP.feedbackJL.setText("");
				}
				//TODO : Move this functionality to runP.  Just pass in the display.
				int horiz = currentDisplay.getRandomOffset().width;
				int vert = currentDisplay.getRandomOffset().height;
				boolean leaveDisplayOn = currBlock.leaveDisplaysOn;
				switch(currentDisplay.getDisplayType())
				{
				case TEXT:
					runP.presPan.drawWord(currentDisplay.getTextOrPath(),
							currBlock.font.getFontName(), currBlock.font.getSize(), hVal, vVal, horiz, vert,
							leaveDisplayOn);
					break;
				case IMAGE:
					// runP.presPan.showVideo(currentDisplay.itemDisplayed,
					// hVal, vVal, horiz, vert, leaveDisplayOn);
					runP.presPan.drawPicture(currentDisplay.getTextOrPath(),
							hVal, vVal, horiz, vert, leaveDisplayOn);
					break;
				case SOUND:
					Clip c = PresentationPanel.loadAudioClip(currentDisplay.getTextOrPath());
					c.start();
					break;
				case VIDEO:
					runP.presPan.showVideo(currentDisplay.getTextOrPath(), hVal,
							vVal, horiz, vert, leaveDisplayOn);
					break;
				}

				//TODO: Should this be based on Persist time?
				if (currentDisplay.getDurationSecs() != 0) {
					eraseAfterLastDisplay = true;
				} else {
					eraseAfterLastDisplay = false;
				}

				// If display is last in trial
				if (displayCtr == (currentDisplays.size() - 1)) {
					startRxnTimeMeasure = System.currentTimeMillis();
					acceptKeyStroke = true;
					runP.promptJL.setText(experiment.promptString);
				}

				timer1a.stop();
				
				timer1b.setInitialDelay((int) (currentDisplay.getDurationSecs() * 1000));
				timer1b.restart();
				displayCtr++;

			}
		};

		timer1bAction = new AbstractAction() {
			boolean shouldDraw = false;

			public void actionPerformed(ActionEvent e) {
				clearTheScreen();

			}
		};

		keyAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {

				if (acceptKeyStroke) {
					keyStruck = e.getActionCommand();
					keyWasStruck();
					acceptKeyStroke = false;
				}
			}
		};

		timer2aAction = new AbstractAction() {
			boolean shouldDraw = false;

			public void actionPerformed(ActionEvent e) {

				if (shouldDraw = !shouldDraw) {

					if ((!experiment.includeAllLetters) && (!experiment.includeAllNumbers)
							&& (experiment.giveFeedback)) {
						if (keyStruck.equals(oneTrial.correctKey)) {
							runP.feedbackJL.setText("Correct");
						} else {
							runP.feedbackJL.setText("Incorrect");
						}
					} else if ((experiment.includeAllNumbers) || (experiment.includeAllLetters)) {
						runP.feedbackJL.setText(keyStruck);
					}
					if (!experiment.promptString.equals("")) {
						runP.promptJL.setText("");
					}

				} else {

					runP.feedbackJL.setText("");
					timer2a.stop();
					timer2b.start();

				}
			}
		};

		timer2bAction = new AbstractAction() {
			boolean shouldDraw = false;

			public void actionPerformed(ActionEvent e) {

				if (shouldDraw = !shouldDraw) {

				} else {

					timer2b.stop();
					startTimer1();

				}
			}
		};

	}

	void keyWasStruck() {

		stopRxnTimeMeasure = System.currentTimeMillis();
		rxnTime = stopRxnTimeMeasure - startRxnTimeMeasure;

		keyStruck = keyStruck.toLowerCase();

		Response response = new Response(oneTrial.trialType, oneTrial.correctKey,
				keyStruck, rxnTime);

		userResponses.add(response);

		timer1b.stop();
		runP.presPan.clearMediaPlayer();
		if ((displayCtr >= currentDisplays.size())
				&& (trialCtr < currBlock.trials.size())) {
			trialCtr++;
			displayCtr = 0;
			runP.presPan.clearVector();
		}

		if (trialCtr >= currBlock.trials.size()) {
			printResults();
			runP.promptJL.setText("Experiment Over");
			if (!experiment.instructions.equals("")) {
				runP.instructionsJB.setVisible(true);
			}
			runP.startJB.setText("Start Experiment");
			runP.presPan.videoPanCtr = 0;
			runningExperiment = false;
		} else {
			timer2a.start();
		}
		runP.presPan.setFocusable(true);
	}

	public void clearTheScreen() {

		if (displayCtr >= currentDisplays.size()) {
			if (eraseAfterLastDisplay == true) {
				runP.presPan.eraseAll();
			}
		} else {
			if (currBlock.leaveDisplaysOn == false) {
				runP.presPan.eraseAll();
			}
			timer1b.stop();
			timer1a.start();
		}
	}

	// ********************************
	// set up interface
	// ********************************

	/**
	 * Populate the tabs with panels. Add a listener to detect changes from
	 * Panes. Load the script and setup when moving to the run pane.
	 */
	private void setupTabbedPane() {

		instruct1Panel = new JPanel();
		instruct1Panel.setBackground(Color.white);
		instructionsScreen1Path = "genlabInstr1.jpg";
		addInstructions(instruct1Panel, instructionsScreen1Path);
		trialVarsP = new TrialVarsPanel();
		instruct2Panel = new JPanel();
		instruct2Panel.setBackground(Color.white);
		instructionsScreen2Path = "genlabInstr2.jpg";
		addInstructions(instruct2Panel, instructionsScreen2Path);
		exptVarsP = new ExptVarsPanel();
		runP = new RunPanel();
		runP.addComponentListener(this);
		resultsP = new ResultsPanel();

		JPanel introPanel = new IntroPanel(tabbedPane);
		tabbedPane.addTab("Welcome", introPanel);

		tabbedPane.addTab("Instructions", instruct1Panel);
		
		tabbedPane.addTab("Instructions", instruct1Panel);
		tabbedPane.addTab("Create Trials", null, trialVarsP,
				"Set trial details and create a script file");
		tabbedPane.addTab("Instructions", instruct2Panel);
		tabbedPane.addTab("Set up Experiment", null, exptVarsP,
				"Set experiment variables");
		tabbedPane.addTab("Run Experiment", runP);
		tabbedPane.addTab("Results", resultsP);

		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JTabbedPane tabbedPane = (JTabbedPane) changeEvent.getSource();
				int index = tabbedPane.getSelectedIndex();
				switch(index){
					case 5:
					setupExperimentFromOldScript();
					tabbedPane.setEnabledAt(6,true);
					break;
					default:
					break;
				}
				//TODO: Change the Results Pane disabling to be smarter-
				// Only enable it after an experiment has started, and then leave it enabled.
				if(index != 5 && index != 6){
					tabbedPane.setEnabledAt(6,false);
				}
			}
		};
		tabbedPane.addChangeListener(changeListener);

	}

	// ===================================================
	// GUI set up
	// ===================================================

	public void addInstructions(JPanel ip, String jpg) {

		URL urlImage = getClass().getResource(jpg);
		// System.out.println(urlImage);
		Image img = Toolkit.getDefaultToolkit().getImage(urlImage);
		ImageIcon inst = new ImageIcon(img);
		// ImageIcon inst = new ImageIcon(jpg);
		JLabel instructions = new JLabel(inst);
		ip.setLayout(new GridLayout(1, 1));
		ip.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		ip.add(instructions);
	}

	// ===================================================
	// Get experiment variables
	// ===================================================

	public void initializeGlobalVariables() {

	//	ctr = 0;
		trialCtr = 0;
		displayCtr = 0;
		hPosition = 0;
		vPosition = 0;
		hVal = 0;
		vVal = 0;
//		thePosition = "";
		startRxnTimeMeasure = 0;
		stopRxnTimeMeasure = 0;
		rxnTime = 0;
		acceptKeyStroke = false;
		userResponses.clear();
		eraseAfterLastDisplay = true;
		currBlock = experiment.blocks.get(0);
		// includeAllLetters = false;
		// includeAllNumbers = false;
	}
	

	
	private boolean setupExperimentFromOldScript()
	{
		experiment = ExperimentManager.loadExperiment(exptVarsP);
		if (experiment != null) 
		{
			setupRunPanel();
			runP.startJB.setEnabled(true);
			runP.startJB.setToolTipText("Begin the experiment.");
			return true;
		} 
		else
		{
			runP.startJB.setEnabled(false);
			runP.startJB.setToolTipText("Error parsing script.  Check script and setup page.");
			return false;
		}
	}

	
	/*
	private boolean setupExperimentFromOldScript2()
	{
		// get script name
		experiment = new Experiment();
		Experiment ex = experiment;
		Block block = new Block(); //Default Block
		ex.blocks.add(block);
		
		ex.scriptFilename = exptVarsP.getScript();
		if (ex.scriptFilename.equals("")) {
			varError(0, "");  //TODO varError: keep this setup?
			return false;
		}
		ex.scriptDirectory = exptVarsP.getScriptDirectory();
		// get repetitions
		int reps;
		try {
			block.reps = Integer.parseInt(exptVarsP.getReps());
			reps = block.reps;
			if (reps <= 0) {
				varError(1, "");
				return false;
			}
		} catch (NumberFormatException nfe) {
			varError(1, "");
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
				varError(4, "");
				return false;
			}
		} catch (NumberFormatException nfe) {
			varError(4, "");
			return false;
		}

		// Parse the script!
		int parseSuccess = parseScript2();

		if (parseSuccess == 1) {
			runP.startJB.setEnabled(true);
			runP.startJB.setToolTipText("Begin the experiment.");
			return true;
		} else {
			runP.startJB.setEnabled(false);
			runP.startJB
					.setToolTipText("Error in parsing script setup.  Check script and environment page.");
			return false;
		}
	}
*/
	/*
	private int parseScript2(){
		String scriptString, trialString;
		int numTrials = 0;
		int numDisplays = 0;
		String trialType = "", correctKeyString = "";

		vectorOfUsableKeys = new Vector();
		vectorOfTrialTypes = new Vector();

		// steps for parsing script into vector of vectors containing trial
		// specifics
		// ...
		//
		trials.clear();
		runP.presPan.mediaPlayerVector.removeAllElements();
		runP.presPan.videoNameVector.removeAllElements();
		runP.presPan.videoPanCtr = 0;

		scriptString = "";
		trialString = "";
		instructionsFilename = "";
		
		// START HERE
		// Get characters to first white space and parseInt --> Num displays
		// cerate new vector and put in first slot
		// Get next character after whitesepace, check that is only one
		// before next whitespace --> response key
		// Get rest substring after whitespace and delete white space at end
		// (if any) --> category
		// read in as many lines as there are displays separately or read to
		// blank line
		// repeat above for number of trials
		String tempDebugString = "";
		try {
			File scriptFile = new File(experiment.scriptFilename);
			BufferedReader br = new BufferedReader(new FileReader(scriptFile)); 
			//TODO: Change reader for applet?  Seems OK for now!.
			scriptString = br.readLine();
			if (scriptString.startsWith("instructions")) {
				String[] temp = scriptString.split(" ", 2);
				instructionsFilename = temp[1];
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
				JOptionPane.showMessageDialog(this,
						"Number of trials must be an integer >= 0",
						"Variable error", JOptionPane.ERROR_MESSAGE);
				return (0);
			}

			if (numTrials < 0) {
				varError(5, "");
				return (0);
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
			for (int j = 0; j < numTrials; j++) {
				numDisplays = Integer.parseInt((String) tokenVector
						.elementAt(tokenCtr++));

				if (tokenVector.elementAt(tokenCtr).equals("#")) {
					includeAllNumbers = true;
				} else if (tokenVector.elementAt(tokenCtr).equals("*")) {
					includeAllLetters = true;
				}

				correctKeyString = (String) tokenVector.elementAt(tokenCtr++);

				if (!vectorOfUsableKeys.contains(correctKeyString)) {
					vectorOfUsableKeys.addElement(correctKeyString);
				}

				trialType = (String) tokenVector.elementAt(tokenCtr++);

				if (!vectorOfTrialTypes.contains(trialType)) {
					vectorOfTrialTypes.addElement(trialType);
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
							textOrPath = experiment.scriptDirectory + (String) tokenVector.elementAt(tokenCtr++);
							Image tempImg = Toolkit.getDefaultToolkit().getImage(textOrPath);
	
							try {
								MediaTracker tracker = new MediaTracker(this);
								tracker.addImage(tempImg, 0);
								tracker.waitForID(0);
	
								int tempWidth = tempImg.getWidth(this);
								int tempHeight = tempImg.getHeight(this);
	
								if ((tempWidth <= 0) || (tempHeight <= 0)) {
									varError(8, textOrPath);
									return (0);
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
	
							int tempIndex = runP.presPan.videoNameVector.indexOf(textOrPath);
							if (tempIndex == -1) {
								runP.presPan.videoNameVector.addElement(textOrPath);
								runP.presPan.prepVideo(experiment.scriptDirectory + textOrPath);
							}
							break;
						case SOUND:
							textOrPath = "" + audioClipVector.size();
						String soundFilePath = experiment.scriptDirectory
									+ (String) tokenVector.elementAt(tokenCtr++);
	
							Clip theClip = loadAudioClip(soundFilePath);
							if (theClip == null) {
								varError(9, soundFilePath);
								return (0);
							}
							duration = theClip.getMicrosecondLength() / 1000000.0;
							audioClipVector.addElement(theClip);
							break;
					}//End Switch
					
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
			JOptionPane.showMessageDialog(this, "File not found error:  "
					+ fnfe, "File error", JOptionPane.ERROR_MESSAGE);
			System.out.println("trying to read file  " + fnfe);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(this, "Error reading from file:  "
					+ ioe, "File error", JOptionPane.ERROR_MESSAGE);
		}
		
		// Replicate each trial based on reps number
		int reps = experiment.blocks.get(0).reps;
		if (reps > 1) {
			int initialSize = trials.size();
			trials.ensureCapacity(initialSize * reps);
			for (int i = 0; i < reps - 1; i++) // each rep past 1
			{
				for (int j = 0; j < initialSize; j++)
				{
					trials.add(trials.get(j));
				}
			}
		}
		// Reorder trial vector if necessary
		if (experiment.blocks.get(0).randomizeTrialOrder) {
			java.util.Collections.shuffle(trials);
		}
		// Setup trial boundaries and display orders
		int horiz, vert;
		try {
			horiz = Integer.parseInt(exptVarsP.getHorizRange());
			if ((horiz <= 0) || (horiz > 390)) {
				varError(2, "");
				return 0;
			}
		} catch (NumberFormatException nfe) {
			varError(2, "");
			return 0;
		}

		try {
			vert = Integer.parseInt(exptVarsP.getVertRange());
			if ((vert <= 0) || (vert > 250)) {
				varError(3, "");
				return 0;
			}
		} catch (NumberFormatException nfe) {
			varError(3, "");
			return 0;
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
		experiment.blocks.get(0).trials = trials;
		// JOptionPane.showMessageDialog(this, "test");
		scriptInstructions = "";
		if (!instructionsFilename.equals("")) {
			String instructionsString = "";
			try {

				String instructionsPathPlusFilename = exptVarsP.scriptDirectory
						+ "\\" + instructionsFilename;
				File scriptFile = new File(instructionsPathPlusFilename);
				BufferedReader br = new BufferedReader(new FileReader(
						scriptFile));

				while ((instructionsString = br.readLine()) != null) {
					scriptInstructions = scriptInstructions + "\n"
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
		
		// AFTER FILE PARSED
		// once script data in vector...run experiment 
		//Run Panel Setup
		
		runP.getInputMap().clear();
		runP.getActionMap().clear();
		runP.presPan.videoPanCtr = 0;
		for (int i = 0; i < vectorOfUsableKeys.size(); i++) {

			char c = ((String) vectorOfUsableKeys.elementAt(i)).charAt(0);

			if (c == '*') {

				for (int j = 0; j < arrayOfLetters.length; j++) {

					runP.getInputMap().put(
							KeyStroke.getKeyStroke(arrayOfLetters[j]),
							"doKeyAction");
					runP.getActionMap().put("doKeyAction", keyAction);
				}

			} else if (c == '#') {
				for (int j = 0; j < arrayOfNumbers.length; j++) {
					runP.getInputMap().put(
							KeyStroke.getKeyStroke(arrayOfNumbers[j]),
							"doKeyAction");
					runP.getActionMap().put("doKeyAction", keyAction);
				}

			} else {
				runP.getInputMap()
						.put(KeyStroke.getKeyStroke(c), "doKeyAction");
				runP.getActionMap().put("doKeyAction", keyAction);
			}
		}
		runP.promptJL.setText("");
		if (scriptInstructions.equals("")) {
			runP.instructionsJB.setVisible(false);
		} else {
			runP.instructionsJB.setVisible(true);
		}

		return (1);
	}
	*/
	public void setupRunPanel()
	{
		runP.presPan.mediaPlayerVector.removeAllElements();
		runP.presPan.videoNameVector.removeAllElements();
		runP.presPan.videoPanCtr = 0;
		
		runP.getInputMap().clear();
		runP.getActionMap().clear();
		runP.presPan.videoPanCtr = 0;
		for (int i = 0; i < experiment.usableKeys.size(); i++) {

			char c = ((String) experiment.usableKeys.get(i)).charAt(0);

			if (c == '*') {

				for (int j = 0; j < 26; j++) {

					runP.getInputMap().put(
							KeyStroke.getKeyStroke("abcdefghijklmnopqrstuvwxyz".charAt(j)),
							"doKeyAction");
					runP.getActionMap().put("doKeyAction", this.keyAction);
				}

			} else if (c == '#') {
				for (int j = 0; j < 10; j++) {
					runP.getInputMap().put(
							KeyStroke.getKeyStroke("" + j),
							"doKeyAction");
					runP.getActionMap().put("doKeyAction", this.keyAction);
				}

			} else {
				runP.getInputMap()
						.put(KeyStroke.getKeyStroke(c), "doKeyAction");
				runP.getActionMap().put("doKeyAction", this.keyAction);
			}
		}
		runP.promptJL.setText("");
		if (experiment.instructions.equals("")) {
			runP.instructionsJB.setVisible(false);
		} else {
			runP.instructionsJB.setVisible(true);
		}
	}

	private void runExperiment() {

		runP.requestFocus();

		if (runningExperiment) {
			abortExperiment();
			if (!experiment.instructions.equals("")) {
				runP.instructionsJB.setVisible(true);
			}
		} else {
			runP.instructionsJB.setVisible(false);
			runP.startJB.setText("Abort Experiment");
			runP.promptJL.setText("");

			initializeGlobalVariables();
			
			
			runningExperiment = true;
			timer1a = new Timer(0, timer1aAction);
			timer1b = new Timer(5000, timer1bAction);

			startTimer1();

			timer2a = new Timer(currBlock.delayBetweenTrials / 2, timer2aAction);
			timer2b = new Timer(currBlock.delayBetweenTrials / 2, timer2bAction);
			timer2a.setInitialDelay(0);
			timer2b.setInitialDelay(0);

		}

	}

	public void abortExperiment() {
		runP.startJB.setText("Start Experiment");
		runningExperiment = false;

		timer1a.stop();
		timer1b.stop();
		timer2a.stop();
		timer2b.stop();

		printResults();

	}

	// TODO: what's this comment?
	// 780, 500 fix so not hardcoded,

	public void startTimer1() {

		oneTrial = (Trial) currBlock.trials.get(trialCtr);

		currentDisplays = new Vector(oneTrial.displays);

		// timer1b.setDelay((int)(((OneDisplay)oneTrialVector.elementAt(displayCtr)).durationInSecs
		// * 1000));

		tempstart = System.currentTimeMillis();

		timer1a.start();

	}


	public void printResults() {

		String theWord = "";
		String rawDataResultsString = "trial-num category correct-key-response user-response-key response-time-in secs\n\n";

		int[][] resultsArray_numTrials;
		resultsArray_numTrials = new int[experiment.trialTypes.size()][experiment.usableKeys
				.size()];

		long[][] resultsArray_totalResponseTime;
		resultsArray_totalResponseTime = new long[experiment.trialTypes.size()][experiment.usableKeys
				.size()];

		int[][] resultsArray_numCorrectResponses;
		resultsArray_numCorrectResponses = new int[experiment.trialTypes.size()][2];

		long[][] resultsArray_totalResponseTime_correct_incorrect;
		resultsArray_totalResponseTime_correct_incorrect = new long[experiment.trialTypes
				.size()][2];

		int[][] resultsArray_NUMS_numTrials;
		resultsArray_NUMS_numTrials = new int[experiment.trialTypes.size()][10];

		long[][] resultsArray_NUMS_totalResponseTime;
		resultsArray_NUMS_totalResponseTime = new long[experiment.trialTypes
				.size()][10];

		java.util.Collections.sort(experiment.usableKeys);
		if (experiment.usableKeys.get(0).equals("*")
				|| experiment.usableKeys.get(0).equals("#")) {
			String s = experiment.usableKeys.get(0);
			experiment.usableKeys.remove(0);
			experiment.usableKeys.add(s);
		}
		if (experiment.usableKeys.get(0).equals("*")
				|| experiment.usableKeys.get(0).equals("#")) {
			String s = experiment.usableKeys.get(0);
			experiment.usableKeys.remove(0);
			experiment.usableKeys.add(s);
		}

		for (int i = 0; i < userResponses.size(); i++) {

			Response r = (Response) userResponses.get(i);
			Trial trial = (Trial) currBlock.trials.get(i);  //TODO: Why isn't this used?  whats happening here in general.

			if (experiment.includeAllNumbers) {
				rawDataResultsString += (int) (i + 1) + " "
						+ r.trialType + " "
						+ r.userResponseKey + " "
						+ (double) (r.responseTime / 1000) + "\n";
				for (int j = 0; j < experiment.trialTypes.size(); j++) {

					if (experiment.trialTypes.get(j).equals(
							r.trialType)) {
						for (int k = 0; k < 10; k++) {

							if ((r.userResponseKey).equals(String
									.valueOf(k))) {
								resultsArray_NUMS_numTrials[j][k]++;
								resultsArray_NUMS_totalResponseTime[j][k] += r.responseTime;

								// if
								// ((oneResponse.correctKey).equals(oneResponse.userResponseKey))
								// {
								//
								// resultsArray_numCorrectResponses[j][0]++;
								// resultsArray_totalResponseTime_correct_incorrect[j][0]
								// += oneResponse.responseTime;
								// break;
								// }
								// else if
								// ((((oneResponse.correctKey).equals("*")) &&
								// (isInArray(oneResponse.userResponseKey,arrayOfLetters)))
								// || (((oneResponse.correctKey).equals("#")) &&
								// (isInArray(oneResponse.userResponseKey,arrayOfNumbers))))
								// {
								// resultsArray_numCorrectResponses[j][0]++;
								// resultsArray_totalResponseTime_correct_incorrect[j][0]
								// += oneResponse.responseTime;
								// }
								// else {
								// resultsArray_numCorrectResponses[j][1]++;
								// resultsArray_totalResponseTime_correct_incorrect[j][1]
								// += oneResponse.responseTime;
								// break;
								// }

							}
						}
					}
				}
			} else {
				rawDataResultsString += (int) (i + 1) + " "
						+ r.trialType + " " + r.correctKey
						+ " " + r.userResponseKey + " "
						+ (double) (r.responseTime / 1000) + "\n";
				for (int j = 0; j < experiment.trialTypes.size(); j++) {

					if (experiment.trialTypes.get(j).equals(
							r.trialType)) {
						for (int k = 0; k < experiment.usableKeys.size(); k++) {

							if ((((r.userResponseKey)
									.equals(experiment.usableKeys.get(k)))
									|| ((experiment.usableKeys.get(k)
											.equals("*")) && (isInArray(
											r.userResponseKey,
											arrayOfLetters))) || ((experiment.usableKeys
									.get(k).equals("#")) && (isInArray(
									r.userResponseKey, arrayOfNumbers))))) {
								resultsArray_numTrials[j][k]++;
								resultsArray_totalResponseTime[j][k] += r.responseTime;

								if ((r.correctKey)
										.equals(r.userResponseKey)) {

									resultsArray_numCorrectResponses[j][0]++;
									resultsArray_totalResponseTime_correct_incorrect[j][0] += r.responseTime;
									break;
								} else if ((((r.correctKey)
										.equals("*")) && (isInArray(
										r.userResponseKey,
										arrayOfLetters)))
										|| (((r.correctKey)
												.equals("#")) && (isInArray(
												r.userResponseKey,
												arrayOfNumbers)))) {
									resultsArray_numCorrectResponses[j][0]++;
									resultsArray_totalResponseTime_correct_incorrect[j][0] += r.responseTime;
								} else {
									resultsArray_numCorrectResponses[j][1]++;
									resultsArray_totalResponseTime_correct_incorrect[j][1] += r.responseTime;
									break;
								}

							}
						}
					}
				}
			}
		}

		String resultsString = "";

		for (int j = 0; j < experiment.trialTypes.size(); j++) {
			resultsString += "Results for all Category "
					+ experiment.trialTypes.get(j) + " items:\n";

			if (experiment.includeAllNumbers) {

				double avgResponse_NUMS = 0;
				int totalResponses_NUMS = 0;
				double avgResponseTime_NUMS = 0;

				for (int k = 0; k < 10; k++) {

					// resultsString += "k: " + k + " " +
					// resultsArray_NUMS_numTrials[j][k] + "\n";

					if (resultsArray_NUMS_numTrials[j][k] != 0) {

						double avgResponseTime = (double) ((double) (resultsArray_NUMS_totalResponseTime[j][k] / resultsArray_NUMS_numTrials[j][k]) / 1000);

						resultsString += "   Responded with " + k + " on "
								+ resultsArray_NUMS_numTrials[j][k]
								+ " trials with an average response time of "
								+ avgResponseTime + " seconds.\n";

						avgResponse_NUMS += k
								* resultsArray_NUMS_numTrials[j][k];
						avgResponseTime_NUMS += avgResponseTime
								* resultsArray_NUMS_numTrials[j][k];
						totalResponses_NUMS += resultsArray_NUMS_numTrials[j][k];
					}

				}
				resultsString += "   Average Response: "
						+ (avgResponse_NUMS / totalResponses_NUMS) + "\n";
				resultsString += "   Average Response Time: "
						+ (avgResponseTime_NUMS / totalResponses_NUMS) + "\n";

			} else {

				for (int k = 0; k < experiment.usableKeys.size(); k++) {

					if (resultsArray_numTrials[j][k] != 0) {

						double avgResponseTime = (double) ((double) (resultsArray_totalResponseTime[j][k] / resultsArray_numTrials[j][k]) / 1000);

						resultsString += "   Responded with "
								+ experiment.usableKeys.get(k) + " on "
								+ resultsArray_numTrials[j][k]
								+ " trials with an average response time of "
								+ avgResponseTime + " seconds.\n";
					} else {
						resultsString += "   Responded with "
								+ experiment.usableKeys.get(k) + " on "
								+ resultsArray_numTrials[j][k] + " trials.\n";
					}
				}

				for (int k = 0; k < 2; k++) {

					if (k == 0) {
						theWord = "correct";
					} else {
						theWord = "incorrect";
					}

					if (resultsArray_numCorrectResponses[j][k] != 0) {

						double avgResponseTime = (double) ((double) (resultsArray_totalResponseTime_correct_incorrect[j][k] / resultsArray_numCorrectResponses[j][k]) / 1000);

						resultsString += "   Number of " + theWord
								+ " responses = "
								+ resultsArray_numCorrectResponses[j][k]
								+ ", with an average response time of "
								+ avgResponseTime + " seconds.\n";
					} else {
						resultsString += "   Number of " + theWord
								+ " responses = "
								+ resultsArray_numCorrectResponses[j][k]
								+ ".\n";
					}
				}

			}

		}

		resultsP.resultsJTA.setText(resultsString);
		resultsP.setFileContent(resultsString);
		resultsP.setRawDataFileContent(rawDataResultsString);
		resultsP.setVerticalScrollbarToTop();

	}

	public boolean isInArray(String theString, char[] theArray) {

		boolean found = false;

		char charValOfString = theString.charAt(0);

		for (int i = 0; i < theArray.length; i++) {
			if (theArray[i] == charValOfString) {
				found = true;

				// break out of loop
			}
		}

		return found;

	}

	// *********************************
	// events for when panels shown - to stop and start expt when user changes
	// tabs - see //STMScanJ2 if this isn't clear
	// *********************************

	public void componentHidden(ComponentEvent e) {
		// presArea.eraseAll();
		// startJB.setEnabled(false);
		// feedbackJL.setText("   ");
	}
	//TODO: use these listeners to ensure window sizes?  Currently fixed, just maybe not on screen.. hmm...
	public void componentResized(ComponentEvent e) {
		//System.out.println("Resized!");
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
		// trialPanel.requestFocus();
		// startJB.setEnabled(true);

	}

	// ======================================================================
	// error checking/reporting
	// ======================================================================

	public void varError(int errorNum, String errorString) {

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

		JOptionPane.showMessageDialog(this, errStrings[errorNum],
				"Variable error", JOptionPane.ERROR_MESSAGE);

	}

	// ===================================================
	// MAIN
	// ===================================================

	public static void main(String[] args) {
		//Do Nothing
		System.out.println("Do not run this as a java application; it is an applet.");
	}
}



class Response {

	String trialType = "";
	String correctKey = "";
	String userResponseKey = "";
	double responseTime = 0;

	public Response(String trialType, String correctKey,
			String userResponseKey, double responseTime) {
		this.trialType = trialType;
		this.correctKey = correctKey;
		this.userResponseKey = userResponseKey;
		this.responseTime = responseTime;
	}

}
