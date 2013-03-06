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

	String instructions1, instructions2, scriptInstructions = "";
	String instructionsFilename = "";

	Timer timer1a; // Shows next display, starts 1b
	Timer timer1b; // Clears away latest display
	Timer timer2a; // Displays feedback, starts 2b
	Timer timer2b; //
	// Action KeyAction stops 2b, starts 1.
	Action timer1aAction, timer1bAction, timer2aAction, timer2bAction,
			keyAction;

	// variables used for running experiment
	String script, fontFace, promptString, keyStruck;
	int reps, fontSize, horiz, vert, delay;
	boolean randomTrialOrder, randomDisplayOrder, leaveDisplayOn, prompt,
			feedback;
	int ctr = 0;
	int trialCtr = 0, displayCtr = 0;
	int hPosition = 0, vPosition = 0, hVal = 0, vVal = 0;
	// int parseScriptReturnVal = 0; //Replaced by simply enabling/disabling the
	// start button
	boolean acceptKeyStroke = false, eraseAfterLastDisplay = true,
			runningExperiment = false;
	boolean includeAllLetters = false, includeAllNumbers = false;
	double startRxnTimeMeasure = 0, stopRxnTimeMeasure = 0, rxnTime = 0;
	double tempstart, tempstop, temptime;

	Vector vectorOfTrials;
	Vector vct;
	Vector vectorOfUsableKeys, vectorOfTrialTypes;
	Vector userResponsesVector = new Vector();

	Trial oneTrial;
	Vector oneTrialVector;
	StreamTokenizer st, streamTokenizer;
	Clip clip;
	Vector audioClipVector = new Vector();
	String directoryString, imagePath;
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

		vectorOfTrials = new Vector();
		runP.presPan.videoPanVector = new Vector();
		runP.presPan.mediaPlayerVector = new Vector();
		runP.presPan.videoNameVector = new Vector();

		runP.startJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getAndSetVariables();
				runExperiment(); // This button should be disabled => this
									// action unperformable
			} // if the parse script failed.
		});

		runP.instructionsJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(runP, scriptInstructions,
						"Instructions", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		timer1aAction = new AbstractAction() {
			boolean shouldDraw = false;

			public void actionPerformed(ActionEvent e) {

				tempstop = System.currentTimeMillis();
				temptime = tempstop - tempstart;
				tempstart = System.currentTimeMillis();
				Display currentDisplay = (Display) oneTrialVector
						.elementAt(displayCtr);

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

				if (feedback) {
					runP.feedbackJL.setText("");
				}
				//TODO : Move this functionality to runP.  Just pass in the display.
				switch(currentDisplay.getDisplayType())
				{
				case TEXT:
					runP.presPan.drawWord(currentDisplay.getItemDisplayed(),
							fontFace, fontSize, hVal, vVal, horiz, vert,
							leaveDisplayOn);
					break;
				case IMAGE:
					// runP.presPan.showVideo(currentDisplay.itemDisplayed,
					// hVal, vVal, horiz, vert, leaveDisplayOn);
					runP.presPan.drawPicture(currentDisplay.getItemDisplayed(),
							hVal, vVal, horiz, vert, leaveDisplayOn);
					break;
				case SOUND:
					((Clip) audioClipVector.elementAt(Integer
							.parseInt(currentDisplay.getItemDisplayed())))
							.setFramePosition(0);

					((Clip) audioClipVector.elementAt(Integer
							.parseInt(currentDisplay.getItemDisplayed()))).start();
					break;
				case VIDEO:
					runP.presPan.showVideo(currentDisplay.getItemDisplayed(), hVal,
							vVal, horiz, vert, leaveDisplayOn);
					break;
				}

				//TODO: Should this be based on Persist time?
				if (((Display) oneTrialVector.elementAt(displayCtr)).getDurationSecs() != 0) {
					eraseAfterLastDisplay = true;
				} else {
					eraseAfterLastDisplay = false;
				}

				// If display is last in trial
				if (displayCtr == (oneTrialVector.size() - 1)) {
					startRxnTimeMeasure = System.currentTimeMillis();
					acceptKeyStroke = true;
					if (prompt) {
						runP.promptJL.setText(promptString);
					}
				}

				timer1a.stop();
				timer1b.setInitialDelay((int) (((Display) oneTrialVector
						.elementAt(displayCtr)).getDurationSecs() * 1000));
				displayCtr++;

				timer1b.restart();

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

					if ((!includeAllNumbers) && (!includeAllLetters)
							&& (feedback)) {
						if (keyStruck.equals(oneTrial.correctKey)) {
							runP.feedbackJL.setText("Correct");
						} else {
							runP.feedbackJL.setText("Incorrect");
						}
					} else if ((includeAllNumbers) || (includeAllLetters)) {
						runP.feedbackJL.setText(keyStruck);
					}
					if (prompt) {
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

		OneResponse oneResponse = new OneResponse();

		oneResponse.buildResponse(oneTrial.trialType, oneTrial.correctKey,
				keyStruck, rxnTime);

		userResponsesVector.addElement(oneResponse);

		timer1b.stop();
		runP.presPan.clearMediaPlayer();
		if ((displayCtr >= oneTrialVector.size())
				&& (trialCtr < vectorOfTrials.size())) {
			trialCtr++;
			displayCtr = 0;
			runP.presPan.clearVector();
		}

		if (trialCtr >= vectorOfTrials.size()) {
			printResults();
			runP.promptJL.setText("Experiment Over");
			if (!scriptInstructions.equals("")) {
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

		if (displayCtr >= oneTrialVector.size()) {
			if (eraseAfterLastDisplay == true) {
				runP.presPan.eraseAll();
			}
		} else {
			if (leaveDisplayOn == false) {
				runP.presPan.eraseAll();
			}
			timer1b.stop();
			timer1a.start();
		}
	}

	public Clip loadAudioClip(String filename) {
		try {

			AudioInputStream stream = AudioSystem.getAudioInputStream(new File(
					filename));

			// At present, ALAW and ULAW encodings must be converted
			// to PCM_SIGNED before it can be played
			AudioFormat format = stream.getFormat();

			if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
				stream = AudioSystem.getAudioInputStream(
						AudioFormat.Encoding.PCM_SIGNED, stream);
				format = stream.getFormat();
			}

			// Create the clip
			DataLine.Info info = new DataLine.Info(Clip.class,
					stream.getFormat(),
					((int) stream.getFrameLength() * format.getFrameSize()));
			clip = (Clip) AudioSystem.getLine(info);

			// This method does not return until the audio file is completely
			// loaded
			clip.open(stream);

		} catch (IOException e) {
		} catch (LineUnavailableException e) {
		} catch (UnsupportedAudioFileException e) {

		}
		return clip;

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
		instructions1 = "genlabInstr1.jpg";
		addInstructions(instruct1Panel, instructions1);
		trialVarsP = new TrialVarsPanel();
		instruct2Panel = new JPanel();
		instruct2Panel.setBackground(Color.white);
		instructions2 = "genlabInstr2.jpg";
		addInstructions(instruct2Panel, instructions2);
		exptVarsP = new ExptVarsPanel();
		runP = new RunPanel();
		runP.addComponentListener(this);
		resultsP = new ResultsPanel();

		JPanel testFirsty = new JPanel();
		testFirsty.setBackground(Color.cyan);
		addInstructions(testFirsty, "homestar.jpg");
		tabbedPane.addTab("Welcome", testFirsty);

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
				if (index == 5) {
					getAndSetVariables();
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

	private void getAndSetVariables() {

		boolean allVarsOk;

		// initializeGlobalVariables();

		allVarsOk = true;
		// get script name
		script = exptVarsP.getScript();
		if (script.equals("")) {
			varError(0, "");
			allVarsOk = false;
			return;
		}
		directoryString = exptVarsP.getScriptDirectory();

		// get repetitions
		try {
			reps = Integer.parseInt(exptVarsP.getReps());
			if (reps <= 0) {
				varError(1, "");
				allVarsOk = false;
			}
		} catch (NumberFormatException nfe) {
			varError(1, "");
			allVarsOk = false;
		}
		randomTrialOrder = exptVarsP.getTrialOrder();
		randomDisplayOrder = exptVarsP.getDisplayOrder();
		fontFace = exptVarsP.getFontFace();
		fontSize = exptVarsP.getFontSize();
		leaveDisplayOn = exptVarsP.getDisplayOn();
		try {
			horiz = Integer.parseInt(exptVarsP.getHorizRange());
			if ((horiz <= 0) || (horiz > 390)) {
				varError(2, "");
				allVarsOk = false;
			}
		} catch (NumberFormatException nfe) {
			varError(2, "");
			allVarsOk = false;
		}

		try {
			vert = Integer.parseInt(exptVarsP.getVertRange());
			if ((vert <= 0) || (vert > 250)) {
				varError(3, "");
				allVarsOk = false;
			}
		} catch (NumberFormatException nfe) {
			varError(3, "");
			allVarsOk = false;
		}

		prompt = exptVarsP.getPrompt();
		promptString = exptVarsP.getPromptString();
		feedback = exptVarsP.getFeedback();
		try {
			delay = Integer.parseInt(exptVarsP.getDelay());
			if (delay < 0) {
				varError(4, "");
				allVarsOk = false;
			}
		} catch (NumberFormatException nfe) {
			varError(4, "");
			allVarsOk = false;
		}

		// Parse the script!
		int parseSuccess = parseScript();

		if (allVarsOk && parseSuccess == 1) {
			runP.startJB.setEnabled(true);
			runP.startJB.setToolTipText("Begin the experiment.");

		} else {
			runP.startJB.setEnabled(false);
			runP.startJB
					.setToolTipText("Error in parsing script setup.  Check script and environment page.");
		}
	}

	public void initializeGlobalVariables() {

		ctr = 0;
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
		userResponsesVector.removeAllElements();
		eraseAfterLastDisplay = true;
		// includeAllLetters = false;
		// includeAllNumbers = false;
	}

	// ===================================================
	// Parse script
	// ===================================================

	private int parseScript() {

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
		vectorOfTrials.removeAllElements();
		runP.presPan.mediaPlayerVector.removeAllElements();
		runP.presPan.videoNameVector.removeAllElements();
		runP.presPan.videoPanCtr = 0;

		scriptString = "";
		trialString = "";
		instructionsFilename = "";

		String tempDebugString = "";
		try {
			File scriptFile = new File(script);
			BufferedReader br = new BufferedReader(new FileReader(scriptFile)); 
			//TODO: Change reader for applet?
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
							textOrPath = directoryString + (String) tokenVector.elementAt(tokenCtr++);
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
								runP.presPan.prepVideo(directoryString + textOrPath);
							}
							break;
						case SOUND:
							textOrPath = "" + audioClipVector.size();
							String soundFilePath = directoryString
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
				vectorOfTrials.addElement(oneTrial);
			}

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

		} catch (FileNotFoundException fnfe) {
			JOptionPane.showMessageDialog(this, "File not found error:  "
					+ fnfe, "File error", JOptionPane.ERROR_MESSAGE);
			System.out.println("trying to read file  " + fnfe);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(this, "Error reading from file:  "
					+ ioe, "File error", JOptionPane.ERROR_MESSAGE);
		}

		if (reps > 1) {
			Vector cloneOfOriginalVectorOfTrials = (Vector) vectorOfTrials
					.clone();

			for (int i = 1; i <= reps - 1; i++) {
				for (int j = 0; j < cloneOfOriginalVectorOfTrials.size(); j++) {
					vectorOfTrials
							.addElement((Trial) cloneOfOriginalVectorOfTrials
									.elementAt(j));
				}
			}

		}

		// AFTER FILE PARSED
		// once script data in vector...run experiment number-of-repetitions
		// times

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

		// reorder trial vector if necessary
		if (randomTrialOrder) {
			java.util.Collections.shuffle(vectorOfTrials);
		}

		if (randomDisplayOrder) {
			for (int i = 0; i < vectorOfTrials.size(); i++) {
				Trial trial = (Trial) vectorOfTrials.elementAt(i);
				Collections.shuffle(trial.displays);
			}
		}

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
		runP.promptJL.setText("");
		if (scriptInstructions.equals("")) {
			runP.instructionsJB.setVisible(false);
		} else {
			runP.instructionsJB.setVisible(true);
		}

		// runExperiment();
		return (1);

	}

	// ===================================================
	// Run experiment -- THIS NEEDS TO BE DONE
	// ===================================================

	private void runExperiment() {

		runP.requestFocus();

		if (runningExperiment) {
			abortExperiment();
			if (!scriptInstructions.equals("")) {
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

			timer2a = new Timer(delay / 2, timer2aAction);
			timer2b = new Timer(delay / 2, timer2bAction);
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

		oneTrial = (Trial) vectorOfTrials.elementAt(trialCtr);

		oneTrialVector = new Vector(oneTrial.displays);

		// timer1b.setDelay((int)(((OneDisplay)oneTrialVector.elementAt(displayCtr)).durationInSecs
		// * 1000));

		tempstart = System.currentTimeMillis();

		timer1a.start();

	}

//	public void readPositionFromFile() {
//
//		try {
//			if (streamTokenizer.nextToken() != StreamTokenizer.TT_EOF) {
//				thePosition = streamTokenizer.sval;
//
//				if (thePosition.equals("position")) {
//					if (streamTokenizer.nextToken() != StreamTokenizer.TT_EOF) {
//						hPosition = (int) streamTokenizer.nval;
//					}
//					if (streamTokenizer.nextToken() != StreamTokenizer.TT_EOF) {
//						vPosition = (int) streamTokenizer.nval;
//					}
//
//				} else if (thePosition.equals("center")) {
//				} else if (thePosition.equals("random")) {
//				}
//			}
//		} catch (IOException ioe) {
//			JOptionPane.showMessageDialog(this, "Error reading from file:  "
//					+ ioe, "File error", JOptionPane.ERROR_MESSAGE);
//		}
//	}

	public void printResults() {

		String theWord = "";
		String rawDataResultsString = "trial-num category correct-key-response user-response-key response-time-in secs\n\n";

		int[][] resultsArray_numTrials;
		resultsArray_numTrials = new int[vectorOfTrialTypes.size()][vectorOfUsableKeys
				.size()];

		long[][] resultsArray_totalResponseTime;
		resultsArray_totalResponseTime = new long[vectorOfTrialTypes.size()][vectorOfUsableKeys
				.size()];

		int[][] resultsArray_numCorrectResponses;
		resultsArray_numCorrectResponses = new int[vectorOfTrialTypes.size()][2];

		long[][] resultsArray_totalResponseTime_correct_incorrect;
		resultsArray_totalResponseTime_correct_incorrect = new long[vectorOfTrialTypes
				.size()][2];

		int[][] resultsArray_NUMS_numTrials;
		resultsArray_NUMS_numTrials = new int[vectorOfTrialTypes.size()][10];

		long[][] resultsArray_NUMS_totalResponseTime;
		resultsArray_NUMS_totalResponseTime = new long[vectorOfTrialTypes
				.size()][10];

		java.util.Collections.sort(vectorOfUsableKeys);
		if (vectorOfUsableKeys.elementAt(0).equals("*")
				|| vectorOfUsableKeys.elementAt(0).equals("#")) {
			Object s = vectorOfUsableKeys.elementAt(0);
			vectorOfUsableKeys.removeElementAt(0);
			vectorOfUsableKeys.add(s);
		}
		if (vectorOfUsableKeys.elementAt(0).equals("*")
				|| vectorOfUsableKeys.elementAt(0).equals("#")) {
			Object s = vectorOfUsableKeys.elementAt(0);
			vectorOfUsableKeys.removeElementAt(0);
			vectorOfUsableKeys.add(s);
		}

		for (int i = 0; i < userResponsesVector.size(); i++) {

			OneResponse oneResponse = (OneResponse) userResponsesVector
					.elementAt(i);
			Trial oneTrial = (Trial) vectorOfTrials.elementAt(i);

			if (includeAllNumbers) {
				rawDataResultsString += (int) (i + 1) + " "
						+ oneResponse.trialType + " "
						+ oneResponse.userResponseKey + " "
						+ (double) (oneResponse.responseTime / 1000) + "\n";
				for (int j = 0; j < vectorOfTrialTypes.size(); j++) {

					if (vectorOfTrialTypes.elementAt(j).equals(
							oneResponse.trialType)) {
						for (int k = 0; k < 10; k++) {

							if ((oneResponse.userResponseKey).equals(String
									.valueOf(k))) {
								resultsArray_NUMS_numTrials[j][k]++;
								resultsArray_NUMS_totalResponseTime[j][k] += oneResponse.responseTime;

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
						+ oneResponse.trialType + " " + oneResponse.correctKey
						+ " " + oneResponse.userResponseKey + " "
						+ (double) (oneResponse.responseTime / 1000) + "\n";
				for (int j = 0; j < vectorOfTrialTypes.size(); j++) {

					if (vectorOfTrialTypes.elementAt(j).equals(
							oneResponse.trialType)) {
						for (int k = 0; k < vectorOfUsableKeys.size(); k++) {

							if ((((oneResponse.userResponseKey)
									.equals(vectorOfUsableKeys.elementAt(k)))
									|| ((vectorOfUsableKeys.elementAt(k)
											.equals("*")) && (isInArray(
											oneResponse.userResponseKey,
											arrayOfLetters))) || ((vectorOfUsableKeys
									.elementAt(k).equals("#")) && (isInArray(
									oneResponse.userResponseKey, arrayOfNumbers))))) {
								resultsArray_numTrials[j][k]++;
								resultsArray_totalResponseTime[j][k] += oneResponse.responseTime;

								if ((oneResponse.correctKey)
										.equals(oneResponse.userResponseKey)) {

									resultsArray_numCorrectResponses[j][0]++;
									resultsArray_totalResponseTime_correct_incorrect[j][0] += oneResponse.responseTime;
									break;
								} else if ((((oneResponse.correctKey)
										.equals("*")) && (isInArray(
										oneResponse.userResponseKey,
										arrayOfLetters)))
										|| (((oneResponse.correctKey)
												.equals("#")) && (isInArray(
												oneResponse.userResponseKey,
												arrayOfNumbers)))) {
									resultsArray_numCorrectResponses[j][0]++;
									resultsArray_totalResponseTime_correct_incorrect[j][0] += oneResponse.responseTime;
								} else {
									resultsArray_numCorrectResponses[j][1]++;
									resultsArray_totalResponseTime_correct_incorrect[j][1] += oneResponse.responseTime;
									break;
								}

							}
						}
					}
				}
			}
		}

		String resultsString = "";

		for (int j = 0; j < vectorOfTrialTypes.size(); j++) {
			resultsString += "Results for all Category "
					+ vectorOfTrialTypes.elementAt(j) + " items:\n";

			if (includeAllNumbers) {

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

				for (int k = 0; k < vectorOfUsableKeys.size(); k++) {

					if (resultsArray_numTrials[j][k] != 0) {

						double avgResponseTime = (double) ((double) (resultsArray_totalResponseTime[j][k] / resultsArray_numTrials[j][k]) / 1000);

						resultsString += "   Responded with "
								+ vectorOfUsableKeys.elementAt(k) + " on "
								+ resultsArray_numTrials[j][k]
								+ " trials with an average response time of "
								+ avgResponseTime + " seconds.\n";
					} else {
						resultsString += "   Responded with "
								+ vectorOfUsableKeys.elementAt(k) + " on "
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

	public void componentResized(ComponentEvent e) {
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

//	public static void main(String[] args) {
//		GenLab glab = new GenLab();
//		glab.setSize(875, 735);
//	}
}


class OneResponse {

	String trialType = "";
	String correctKey = "";
	String userResponseKey = "";
	double responseTime = 0;

	OneResponse buildResponse(String trialType, String correctKey,
			String userResponseKey, double responseTime) {
		this.trialType = trialType;
		this.correctKey = correctKey;
		this.userResponseKey = userResponseKey;
		this.responseTime = responseTime;
		return this;
	}

}
