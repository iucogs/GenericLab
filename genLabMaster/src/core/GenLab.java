package core;


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


import experiment.Block;
import experiment.Display;
import experiment.Experiment;
import experiment.Trial;
import experiment.Display.DisplayType;
import experiment.Display.PositionType;
import gui.AbstractGenlabPanel;
import gui.ExperimentBuilderPanel;
import gui.HolderPanel;
import gui.ScriptSetupPanel;
import gui.HomePanel;
import gui.NetPanel;
import gui.PresentationPanel;
import gui.ResultsPanel;
import gui.RunPanel;
import gui.ScriptCreatorPanel;

//import javax.media.bean.playerbean.MediaPlayer;

//TODO: Remove unused imports in all classes
//TODO: Delete undeeded resources

/**
 * Program entry point and logical director of running experiments.
 * Initializes 
 */
public class GenLab extends JApplet implements ComponentListener {

	private static GenLab instance;  //Singleton Instance
	
	//'''Data Structures
	private Experiment experiment; //// PRIMARY DATA STRUCTURE ////
	List<Response> userResponses = new ArrayList<Response>();

	//'''GUI Components
	public JTabbedPane tabbedPaneDeprecated;
	public JPanel instruct1Panel, instruct2Panel;
	public ScriptCreatorPanel scriptCreatorP;
	public ScriptSetupPanel scriptSetupP;
	public RunPanel runP;
	public ResultsPanel resultsP;
	public HomePanel homeP;
	public NetPanel loadP;
	public ExperimentBuilderPanel builderP;
	public HolderPanel holderP;
	
	String instructionsScreen1Path, instructionsScreen2Path;

	//'''Control Variables For Running Experiment
	Block currBlock;
	Trial currTrial;
	List<Display> currDisplays;
	private Timer timer1a; // Shows next display, starts 1b
	private Timer timer1b; // Clears away latest display
	private Timer timer2a; // Displays feedback, starts 2b
	private Timer timer2b; //
	private Action timer1aAction, timer1bAction, timer2aAction, timer2bAction;
	private Action keyAction;	// stops 2b, starts 1.
	private String keyStruck;
	int trialCtr = 0, displayCtr = 0;
	int hPosition = 0, vPosition = 0, hVal = 0, vVal = 0;
	boolean acceptKeyStroke = false, eraseAfterLastDisplay = true;
	boolean runningExperiment = false;
	double startRxnTimeMeasure = 0, stopRxnTimeMeasure = 0, rxnTime = 0;
	double tempstart, tempstop, temptime;
	String imagePath; //directoryString
	Component videoComponent;



	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
		holderP.updateLabel();
	}

	public Experiment getExperiment() {
		return experiment;
	}

	/**
	 * Accessor method to the GenLab singleton.
	 * @return
	 */
	public static GenLab getInstance()
	{
		if (instance == null)
		{
			instance = new GenLab();
		}
		return instance;
	}
	
	/**
	 * Creates a Generic Lab.  Note setup happens primarily in the init method.
	 * TODO: add the standard applet methods
	 * TODO: TAKE applet security advice in bottom of this page:
	 * 	http://stackoverflow.com/questions/235258/jfilechooser-use-within-japplet
	 **/
	public GenLab() {
		synchronized (GenLab.class) {  
			if (instance != null) {  
				throw new IllegalStateException();  
	        }  
	        instance = this;  
	      }
	}
	
	public void init(){
		/// Setup LAF
		try
		{
			//TODO: Hunt down and standardize other UI / LaF changes.
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		/// Setup Panels
		instruct1Panel = new JPanel();
		instruct1Panel.setBackground(Color.white);
		instructionsScreen1Path = "genlabInstr1.jpg";
		addInstructions(instruct1Panel, instructionsScreen1Path);
		instruct2Panel = new JPanel();
		instruct2Panel.setBackground(Color.white);
		instructionsScreen2Path = "genlabInstr2.jpg";
		addInstructions(instruct2Panel, instructionsScreen2Path);
		scriptCreatorP = new ScriptCreatorPanel();
		scriptSetupP = new ScriptSetupPanel();
		runP = new RunPanel();
		resultsP = new ResultsPanel();
		homeP = new HomePanel();
		loadP = new NetPanel();
		builderP = new ExperimentBuilderPanel();
		holderP = new HolderPanel();
		/// Setup Tabs
//		tabbedPane = new JTabbedPane();
		//setupTabbedPane();
		getContentPane().add(holderP);
		holderP.showPanel(homeP);

		//getContentPane().add(tabbedPane);
		

		
		


		setupTimerAndKeyActions();
	}
	
	private void setupTimerAndKeyActions()
	{

		timer1aAction = new AbstractAction() {
			boolean shouldDraw = false;

			public void actionPerformed(ActionEvent e) {

				tempstop = System.currentTimeMillis();
				temptime = tempstop - tempstart;
				tempstart = System.currentTimeMillis();
				Display currentDisplay = (Display) currDisplays.get(displayCtr);

				PositionType pt = currentDisplay.getPositionType();

				if (pt.equals(PositionType.EXACT)) {
					hVal = currentDisplay.position.x;
					vVal = currentDisplay.position.y;
				} else if (pt.equals(PositionType.CENTER)) {
					hVal = -1;
					vVal = -1;
				} else if (pt.equals(PositionType.RANDOM)) {
					hVal = -2;
					vVal = -2;
				}

				if (getExperiment().giveFeedback) {
					runP.feedbackJL.setText("");
				}
				//TODO : Move this functionality to runP.  Just pass in the display.
				int horiz = currentDisplay.getRandomOffset().width;
				int vert = currentDisplay.getRandomOffset().height;
				boolean leaveDisplayOn = currBlock.leaveDisplaysOn;
				System.out.println("Dir:" + getExperiment().directory + " path:" + currentDisplay.getTextOrPath());

				System.out.println("Drwing display " + currentDisplay + " with type " + currentDisplay.getDisplayType());

				
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
					runP.presPan.drawPicture(getExperiment().directory + currentDisplay.getTextOrPath(),
							hVal, vVal, horiz, vert, leaveDisplayOn);
					break;
				case SOUND:
					Clip c = PresentationPanel.loadAudioClip(getExperiment().directory + currentDisplay.getTextOrPath());
					c.start();
					break;
				case VIDEO:
					runP.presPan.showVideo(getExperiment().directory + currentDisplay.getTextOrPath(), hVal,
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
				if (displayCtr == (currDisplays.size() - 1)) {
					startRxnTimeMeasure = System.currentTimeMillis();
					acceptKeyStroke = true;
					runP.promptJL.setText(currBlock.blockPrompt);
					/*TODO: replace updating the prompt string somewhere smarter, 
					perhaps with runP.setPrompt or .updatePrompt */
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

					if ((!getExperiment().includeAllLetters) && (!getExperiment().includeAllNumbers)
							&& (getExperiment().giveFeedback)) {
						if (keyStruck.equals(currTrial.correctKey)) {
							runP.feedbackJL.setText("Correct");
						} else {
							runP.feedbackJL.setText("Incorrect");
						}
					} else if ((getExperiment().includeAllNumbers) || (getExperiment().includeAllLetters)) {
						runP.feedbackJL.setText(keyStruck);
					}
					runP.promptJL.setText("");
					//TODO clear the prompt somewhere better? prolly part of taking all run functionality out of GenLab.java

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

		Response response = new Response(currTrial.trialType, currTrial.correctKey,
				keyStruck, rxnTime);

		userResponses.add(response);

		timer1b.stop();
		runP.presPan.clearMediaPlayer();
		if ((displayCtr >= currDisplays.size())
				&& (trialCtr < currBlock.trials.size())) {
			trialCtr++;
			displayCtr = 0;
			runP.presPan.clearVector();
		}

		if (trialCtr >= currBlock.trials.size()) { //Experiment over!  //TODO: Restructure this?
			printResults();
			runP.promptJL.setText("Experiment Over");
			if (!getExperiment().experimentInstructions.equals("")) {
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

		if (displayCtr >= currDisplays.size()) {
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

//	/**
//	 * Populate the tabs with panels. Add a listener to detect changes from
//	 * Panes. Load the script and setup when moving to the run pane.
//	 */
//	private void setupTabbedPane() {
//		tabbedPane.addTab("Welcome", introP);
//		tabbedPane.addTab("Experiment Builder", builderP);
//		tabbedPane.addTab("Script Creation Help", instruct1Panel);
//		tabbedPane.addTab("Create Script", null, scriptCreatorP,
//				"Set trial details and create a script file");
//		tabbedPane.addTab("Script Setup Help", instruct2Panel);
//		tabbedPane.addTab("Set up Experiment", null, scriptSetupP,
//				"Set experiment variables");
//		tabbedPane.addTab("Run Experiment", runP);
//		tabbedPane.addTab("Results", resultsP);
//		tabbedPane.setEnabledAt(tabbedPane.indexOfComponent(resultsP),false);
//
//
//		ChangeListener changeListener = new ChangeListener() {
//			public void stateChanged(ChangeEvent changeEvent) {
//				JTabbedPane tabbedPane = (JTabbedPane) changeEvent.getSource();
//				Component c = tabbedPane.getSelectedComponent();
//				System.out.println("Detected! class is " + c.getClass());
//
//				if (c instanceof IntroPanel)
//				{
//					introP.doIntroMenu();
//				}
//				else if (c instanceof RunPanel)
//				{
//					//TODO: Find a better way to load the experiment from script (aka on page)
//					if (experiment == null)//Hasn't been setup by JSON.
//					{
//						setupExperimentFromOldScript();
//					}
//					int runIndex = tabbedPane.indexOfComponent(runP);
//					tabbedPane.setEnabledAt(runIndex,true);
//				}
//				else if (c instanceof ExperimentBuilderPanel)
//				{
//					ExperimentBuilderPanel builderP = (ExperimentBuilderPanel)c;
//					builderP.doBuildLayout();
//				}
//			}
//		};
//		tabbedPane.addChangeListener(changeListener);
//	}

	/**
	 * Switch the view to a given panel.
	 * @param scriptCreatorP2
	 */
	public void switchToPanel(AbstractGenlabPanel panel) {
		holderP.showPanel(panel);
	}

	public void addInstructions(JPanel ip, String jpg) {
		//TODO: what is this for?  Can we update it?
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

	/**
	 * Initializes the variables used to run the experiment.
	 */
	public void initRunVariables() {

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
		currBlock = getExperiment().blocks.get(0);
		// includeAllLetters = false;
		// includeAllNumbers = false;
	}
	
	public boolean setupExperimentFromOldScript()
	{
		setExperiment(ExperimentUtilities.loadScriptExperiment());
		if (getExperiment() != null) 
		{
			ExperimentUtilities.experimentToJson(getExperiment(), "first_test.json");
			prepRunPanel();
			return true;
		} 
		else
		{
			runP.startJB.setEnabled(false);
			runP.startJB.setToolTipText("Error parsing script.  Check script and setup page.");
			return false;
		}
	}
	
	public boolean saveExperimentToJson()
	{
		String filename = "";
		JFileChooser jfc = new JFileChooser(".");
		//File dir1 = new File(System.getProperty("user.dir"));
		//jfc.setCurrentDirectory(dir1);
		int userchoice = jfc.showSaveDialog(GenLab.getInstance());
		if (userchoice == JFileChooser.APPROVE_OPTION){
			filename = jfc.getSelectedFile().getAbsolutePath();
			ExperimentUtilities.experimentToJson(GenLab.getInstance().getExperiment(), filename);
			return true;
		}
		else
		{
			System.err.println("JFileChooser for JSON save failed.");
			return false;
		}
	}
	
	public boolean setupExperimentFromJson()
	{
		String filename = "";
		JFileChooser jfc = new JFileChooser(".");
		//File dir1 = new File(System.getProperty("user.dir"));
		//jfc.setCurrentDirectory(dir1);
		int userchoice = jfc.showOpenDialog(this);
		if (userchoice == JFileChooser.APPROVE_OPTION){
			filename = jfc.getSelectedFile().getAbsolutePath();
		}
		else
		{
			//TODO: Improve this error catch to JDialog for front end user
			System.err.println("OOPS, failed on filechooser, no approve option.");
			return false;
		}
		setExperiment(ExperimentUtilities.loadJsonExperiment(filename));
		if (getExperiment() != null) 
		{
			prepRunPanel();
			return true;
		} 
		else
		{
			//TODO: Remove this, as it should already be the case.
			runP.startJB.setEnabled(false);
			runP.startJB.setToolTipText("Error loading script from JSON.");
			return false;
		}
	}


	/**
	 * Prepare the presentation panel based a loaded experiment.
	 * TODO: Move this to Run Panel 
	 */
	public void prepRunPanel() {
		runP.presPan.mediaPlayerVector.removeAllElements();
		runP.presPan.videoNameVector.removeAllElements();
		runP.presPan.videoPanCtr = 0;

		runP.getInputMap().clear();
		runP.getActionMap().clear();
		runP.presPan.videoPanCtr = 0;
		for (int i = 0; i < getExperiment().usableKeys.size(); i++) {

			char c = ((String) getExperiment().usableKeys.get(i)).charAt(0);

			if (c == '*') {

				for (int j = 0; j < 26; j++) {

					runP.getInputMap().put(KeyStroke.getKeyStroke("abcdefghijklmnopqrstuvwxyz".charAt(j)), "doKeyAction");
					runP.getActionMap().put("doKeyAction", this.keyAction);
				}

			} else if (c == '#') {
				for (int j = 0; j < 10; j++) {
					runP.getInputMap().put(KeyStroke.getKeyStroke("" + j), "doKeyAction");
					runP.getActionMap().put("doKeyAction", this.keyAction);
				}

			} else {
				runP.getInputMap().put(KeyStroke.getKeyStroke(c), "doKeyAction");
				runP.getActionMap().put("doKeyAction", this.keyAction);
			}
		}
		runP.promptJL.setText("");
		if (getExperiment().experimentInstructions.equals("")) {
			runP.instructionsJB.setVisible(false);
		} else {
			runP.instructionsJB.setVisible(true);
		}
		
		runP.startJB.setEnabled(true);
		runP.startJB.setToolTipText("Begin the experiment.");
	}

	/**
	 * Begins/Ends an experiment.  
	 * Should be called from the 'Start/Abort Experiment' button on the Run Panel.  
	 */
	public void runPressed() {
		System.out.println("PRESSED! Running is " + runningExperiment);
		if (runningExperiment) 
			abortExperiment();
		else
			runExperiment();
	}
	
	/**
	 * Begins an experiment.
	 * 	-initializes control variables in GenLab
	 * 	-sets up control timers
	 * 	-hides instructions, toggles run button to abort
	 */
	private void runExperiment() {
		runningExperiment = true;
		initRunVariables();
		runP.requestFocus();
		runP.instructionsJB.setVisible(false);
		runP.startJB.setText("Abort Experiment");
		runP.promptJL.setText("");
		runP.startJB.repaint();
		runP.promptJL.repaint();

		timer1a = new Timer(0, timer1aAction);
		timer1b = new Timer(5000, timer1bAction);

		startTimer1();

		timer2a = new Timer(currBlock.delayBetweenTrials / 2, timer2aAction);
		timer2b = new Timer(currBlock.delayBetweenTrials / 2, timer2bAction);
		timer2a.setInitialDelay(0);
		timer2b.setInitialDelay(0);
	}

	/**
	 * Aborts an experiment.
	 * 	-stops all timers
	 * 	-prints out incomplete results
	 */
	private void abortExperiment() {
		runningExperiment = false;
		runP.startJB.setText("Start Experiment");
		if (!getExperiment().experimentInstructions.equals("")) 
			runP.instructionsJB.setVisible(true);
		timer1a.stop();
		timer1b.stop();
		timer2a.stop();
		timer2b.stop();

		printResults();

	}

	// TODO: what's this comment?
	// 780, 500 fix so not hardcoded,

	public void startTimer1() {

		currTrial = (Trial) currBlock.trials.get(trialCtr);

		currDisplays = currTrial.displays;

		// timer1b.setDelay((int)(((OneDisplay)oneTrialVector.elementAt(displayCtr)).durationInSecs
		// * 1000));

		tempstart = System.currentTimeMillis();

		timer1a.start();

	}


	public void printResults() {

		//tabbedPane.setEnabledAt(tabbedPane.indexOfComponent(resultsP),true);
		
		char[] arrayOfLetters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();
		char[] arrayOfNumbers = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
		//TODO:  Did changes to the results panel logic cause a POSSIBLE BUG in capitalization letters?
		
		String theWord = "";
		String rawDataResultsString = "trial-num category correct-key-response user-response-key response-time-in secs\n\n";

		int[][] resultsArray_numTrials;
		resultsArray_numTrials = new int[getExperiment().trialTypes.size()][getExperiment().usableKeys
				.size()];

		long[][] resultsArray_totalResponseTime;
		resultsArray_totalResponseTime = new long[getExperiment().trialTypes.size()][getExperiment().usableKeys
				.size()];

		int[][] resultsArray_numCorrectResponses;
		resultsArray_numCorrectResponses = new int[getExperiment().trialTypes.size()][2];

		long[][] resultsArray_totalResponseTime_correct_incorrect;
		resultsArray_totalResponseTime_correct_incorrect = new long[getExperiment().trialTypes
				.size()][2];

		int[][] resultsArray_NUMS_numTrials;
		resultsArray_NUMS_numTrials = new int[getExperiment().trialTypes.size()][10];

		long[][] resultsArray_NUMS_totalResponseTime;
		resultsArray_NUMS_totalResponseTime = new long[getExperiment().trialTypes
				.size()][10];

		java.util.Collections.sort(getExperiment().usableKeys);
		if (getExperiment().usableKeys.get(0).equals("*")
				|| getExperiment().usableKeys.get(0).equals("#")) {
			String s = getExperiment().usableKeys.get(0);
			getExperiment().usableKeys.remove(0);
			getExperiment().usableKeys.add(s);
		}
		if (getExperiment().usableKeys.get(0).equals("*")
				|| getExperiment().usableKeys.get(0).equals("#")) {
			String s = getExperiment().usableKeys.get(0);
			getExperiment().usableKeys.remove(0);
			getExperiment().usableKeys.add(s);
		}

		for (int i = 0; i < userResponses.size(); i++) {

			Response r = (Response) userResponses.get(i);
			Trial trial = (Trial) currBlock.trials.get(i);  //TODO: Why isn't this used?  whats happening here in general.

			if (getExperiment().includeAllNumbers) {
				rawDataResultsString += (int) (i + 1) + " "
						+ r.trialType + " "
						+ r.userResponseKey + " "
						+ (double) (r.responseTime / 1000) + "\n";
				for (int j = 0; j < getExperiment().trialTypes.size(); j++) {

					if (getExperiment().trialTypes.get(j).equals(
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
				for (int j = 0; j < getExperiment().trialTypes.size(); j++) {

					if (getExperiment().trialTypes.get(j).equals(
							r.trialType)) {
						for (int k = 0; k < getExperiment().usableKeys.size(); k++) {

							if ((((r.userResponseKey)
									.equals(getExperiment().usableKeys.get(k)))
									|| ((getExperiment().usableKeys.get(k)
											.equals("*")) && (isInArray(
											r.userResponseKey,
											arrayOfLetters))) || ((getExperiment().usableKeys
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

		for (int j = 0; j < getExperiment().trialTypes.size(); j++) {
			resultsString += "Results for all Category "
					+ getExperiment().trialTypes.get(j) + " items:\n";

			if (getExperiment().includeAllNumbers) {

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

				for (int k = 0; k < getExperiment().usableKeys.size(); k++) {

					if (resultsArray_numTrials[j][k] != 0) {

						double avgResponseTime = (double) ((double) (resultsArray_totalResponseTime[j][k] / resultsArray_numTrials[j][k]) / 1000);

						resultsString += "   Responded with "
								+ getExperiment().usableKeys.get(k) + " on "
								+ resultsArray_numTrials[j][k]
								+ " trials with an average response time of "
								+ avgResponseTime + " seconds.\n";
					} else {
						resultsString += "   Responded with "
								+ getExperiment().usableKeys.get(k) + " on "
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

	public Response(String trialType, String correctKey, String userResponseKey, double responseTime) {
		this.trialType = trialType;
		this.correctKey = correctKey;
		this.userResponseKey = userResponseKey;
		this.responseTime = responseTime;
	}

}
