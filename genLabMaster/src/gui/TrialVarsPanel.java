package gui;
//package genlab;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.io.*;
import java.util.*;

public class TrialVarsPanel extends JPanel {

	JTextField numtrialsJTF;
	JLabel trialsInJL;
	int numTrials, trialsIn;

	JComboBox numdispJCB;
	int numDisplays, numCategories;
	JTextField keyJTF, typeJTF;
	String key, type;

	ScriptCreatorDetailBox ddp1, ddp2, ddp3, ddp4, ddp5;
	ScriptCreatorDetailBox ddp6, ddp7, ddp8, ddp9, ddp10;

	JButton addJB, createJB, resetJB;

	Color varsOrange = new Color(245, 183, 87);
	Font f12b = new Font("Arial", Font.BOLD, 12);
	Font f14b = new Font("Arial", Font.BOLD, 14);

	boolean allVarsOk;
	String ts;
	String scriptString;

	public TrialVarsPanel(){

		Box hbox1, hbox2, btnbox; 
		Box vertBox, mainBox;

		setBackground(varsOrange);

		hbox1 = numtrialsBox();
		hbox2 = typeBox();
		ddp1 = new ScriptCreatorDetailBox(1);
		setupToolTips();
		ddp2 = new ScriptCreatorDetailBox(2);
		ddp3 = new ScriptCreatorDetailBox(3);
		ddp4 = new ScriptCreatorDetailBox(4);
		ddp5 = new ScriptCreatorDetailBox(5);
		ddp6 = new ScriptCreatorDetailBox(6);
		ddp7 = new ScriptCreatorDetailBox(7);
		ddp8 = new ScriptCreatorDetailBox(8);
		ddp9 = new ScriptCreatorDetailBox(9);
		ddp10 = new ScriptCreatorDetailBox(10);
		btnbox = btnBox();

		vertBox = Box.createVerticalBox();
		vertBox.add(Box.createVerticalStrut(10));
		vertBox.add(hbox1);
		hbox1.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertBox.add(Box.createVerticalStrut(5));
		vertBox.add(hbox2);
		hbox2.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertBox.add(Box.createVerticalStrut(10));
		vertBox.add(ddp1);
		ddp1.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertBox.add(ddp2);
		ddp2.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertBox.add(ddp3);
		ddp3.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertBox.add(ddp4);
		ddp4.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertBox.add(ddp5);
		ddp5.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertBox.add(ddp6);
		ddp6.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertBox.add(ddp7);
		ddp7.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertBox.add(ddp8);
		ddp8.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertBox.add(ddp9);
		ddp9.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertBox.add(ddp10);
		ddp10.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertBox.add(Box.createVerticalStrut(10));
		vertBox.add(btnbox);
		btnbox.setAlignmentX(Component.LEFT_ALIGNMENT);

		add(vertBox);

		ddp1.setVisible(false);
		ddp2.setVisible(false);
		ddp3.setVisible(false);
		ddp4.setVisible(false);
		ddp5.setVisible(false);
		ddp6.setVisible(false);
		ddp7.setVisible(false);
		ddp8.setVisible(false);
		ddp9.setVisible(false);
		ddp10.setVisible(false);
		scriptString = "";
		trialsIn = 0;
		numTrials = -1;
		numCategories = 0;
		createJB.setEnabled(false);
	}

//==========================================================
//actions and event handlers
//==========================================================

//=============================
//num displays combo box action
//=============================

	AbstractAction dispJCBaction = new AbstractAction(){
	
		public void actionPerformed(ActionEvent ae) {

			numDisplays = numdispJCB.getSelectedIndex();

			if (numDisplays >= 1){
				ddp1.setVisible(true);
				ddp2.setVisible(false);
				ddp3.setVisible(false);
				ddp4.setVisible(false);
				ddp5.setVisible(false);
				ddp6.setVisible(false);
				ddp7.setVisible(false);
				ddp8.setVisible(false);
				ddp9.setVisible(false);
				ddp10.setVisible(false);
			}
			if (numDisplays >= 2){
				ddp2.setVisible(true);
				ddp3.setVisible(false);
				ddp4.setVisible(false);
				ddp5.setVisible(false);
				ddp6.setVisible(false);
				ddp7.setVisible(false);
				ddp8.setVisible(false);
				ddp9.setVisible(false);
				ddp10.setVisible(false);
			}
			if (numDisplays >= 3){
				ddp3.setVisible(true);
				ddp4.setVisible(false);
				ddp5.setVisible(false);
				ddp6.setVisible(false);
				ddp7.setVisible(false);
				ddp8.setVisible(false);
				ddp9.setVisible(false);
				ddp10.setVisible(false);
			}
			if (numDisplays >= 4){
				ddp4.setVisible(true);
				ddp5.setVisible(false);
				ddp6.setVisible(false);
				ddp7.setVisible(false);
				ddp8.setVisible(false);
				ddp9.setVisible(false);
				ddp10.setVisible(false);
			}
			if (numDisplays >= 5){
				ddp5.setVisible(true);
				ddp6.setVisible(false);
				ddp7.setVisible(false);
				ddp8.setVisible(false);
				ddp9.setVisible(false);
				ddp10.setVisible(false);
			}
			if (numDisplays >= 6){
				ddp6.setVisible(true);
				ddp7.setVisible(false);
				ddp8.setVisible(false);
				ddp9.setVisible(false);
				ddp10.setVisible(false);
			}
			if (numDisplays >= 7){
				ddp7.setVisible(true);
				ddp8.setVisible(false);
				ddp9.setVisible(false);
				ddp10.setVisible(false);
			}
			if (numDisplays >= 8){
				ddp8.setVisible(true);
				ddp9.setVisible(false);
				ddp10.setVisible(false);
			}
			if (numDisplays >= 9){
				ddp9.setVisible(true);
				ddp10.setVisible(false);
			}
			if (numDisplays == 10){
				ddp10.setVisible(true);
			}
		}
	};


//=======================
//add trial button action
//=======================

	AbstractAction addJBaction = new AbstractAction(){
	
		public void actionPerformed(ActionEvent ae) {

			addTrialString();

		}
	};

	public void addTrialString(){
		
		ts = "";
		allVarsOk = true;

		try{	
			numTrials = Integer.parseInt(numtrialsJTF.getText());
			if ((numTrials <= 0)  || (numTrials > 200)) {
					varError(1);
					allVarsOk = false;
			}
		} catch (NumberFormatException nfe) {
				varError(1);
				numTrials = -2;
				allVarsOk = false;
		}

		if (numTrials > 0){
			if (numTrials != trialsIn){

//check vars and add them to trialstring
//get number of displays
				if (numDisplays == 0){
					varError(2);
					allVarsOk = false;
				} else {
					ts = numDisplays + " ";
				}		
//get correct response key
				key = keyJTF.getText();
				if (!(key.length() == 1)) {
					varError(3);
					allVarsOk = false;
				} else {
					ts = ts + key + " ";
				}
//get trial category
				type = typeJTF.getText();
				if ((type.equals("")) || (type.indexOf(" ") >= 0)) {
					varError(4);
					allVarsOk = false;
				} else {
					ts = ts + type + "\n";
				}				
				if (numDisplays >= 1){
					checkString(ddp1.getDetails());
				}
				if (numDisplays >= 2){
					checkString(ddp2.getDetails());
				}
				if (numDisplays >= 3){
					checkString(ddp3.getDetails());
				}
				if (numDisplays >= 4){
					checkString(ddp4.getDetails());
				}
				if (numDisplays >= 5){
					checkString(ddp5.getDetails());
				}
				if (numDisplays >= 6){
					checkString(ddp6.getDetails());
				}
				if (numDisplays >= 7){
					checkString(ddp7.getDetails());
				}
				if (numDisplays >= 8){
					checkString(ddp8.getDetails());
				}
				if (numDisplays >= 9){
					checkString(ddp9.getDetails());
				}
				if (numDisplays == 10){
					checkString(ddp10.getDetails());
				}

//if allVarsOk...
				if (allVarsOk){
					if (trialsIn == 0){
						scriptString = String.valueOf(numTrials);
						scriptString = scriptString + "\n";
					}
					trialsIn++;
					trialsInJL.setText("Current number of trials in file: " + trialsIn);
					scriptString = scriptString + ts + "\n";
					if (numTrials == trialsIn){
						createJB.setEnabled(true);
					}
				} // close allVarsOk

			} else {
				createJB.setEnabled(true);
				String msgdone = "All of the " + numTrials + " trials to be run have been added to the script.  Choose Create file to save them.";
				JOptionPane.showMessageDialog(this, msgdone, "Create trials done", JOptionPane.INFORMATION_MESSAGE);
			}
		}// close numTrials > 0

	}

	private void checkString(String ds){
		
		if (ds.equals("999")){
			allVarsOk = false;
		} else {
			ts = ts + ds + "\n";
		}

	}

//===================
//reset button action
//===================

	AbstractAction resetJBaction = new AbstractAction(){
	
		public void actionPerformed(ActionEvent ae) {

			ts = "";
			scriptString = "";
			trialsIn = 0;
			numCategories = 0;
			trialsInJL.setText("Current number of trials in file: " + trialsIn);
			createJB.setEnabled(false);
		}
	};

//===================
//create button action
//===================

	AbstractAction createJBaction = new AbstractAction(){
	
		public void actionPerformed(ActionEvent ae) {

			String filename = "";
			JFileChooser jfc = new JFileChooser();
			Container parent = createJB.getParent();
			int userchoice = jfc.showSaveDialog(parent);
			if (userchoice == JFileChooser.APPROVE_OPTION){
				filename = jfc.getSelectedFile().getAbsolutePath();
			}
			try{
				BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
				bw.write(scriptString);
				bw.flush();
				bw.close();
			} catch(IOException ioe){
System.out.println("cannot write to file: " + ioe);
				JOptionPane.showMessageDialog(parent, "Cannot write to file " + filename, "I/O error", JOptionPane.ERROR_MESSAGE); 
			}
		}
	};

//=============================================================================
//GUI set up
//=============================================================================

//=================================
//creates box with numtrials fields
//=================================

	private Box numtrialsBox(){

		Box trialsbox;

		JLabel numtrialsJL = new JLabel("Number of trials to run:   ", JLabel.RIGHT);
		numtrialsJL.setFont(f14b);
		numtrialsJTF = new JTextField("", 3);
		numtrialsJTF.setMaximumSize(new Dimension(numtrialsJTF.getPreferredSize()));
		numtrialsJTF.setToolTipText("This is the number of trials in the file.");
		trialsInJL = new JLabel("Current number of trials in file: 0");
		trialsInJL.setFont(f14b);

		trialsbox = Box.createHorizontalBox();
		trialsbox.add(numtrialsJL);
		trialsbox.add(numtrialsJTF);
		trialsbox.add(Box.createHorizontalStrut(50));
		trialsbox.add(trialsInJL);
		return trialsbox;
	}

//===================================================
//creates box with numDisplays, correct key, category
//===================================================
		
	private Box typeBox(){

		Box typebox;
		String[] tenStrings = {"--", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

		JLabel numDisplayJL = new JLabel("Number of displays within this trial:  ", JLabel.RIGHT);
		numDisplayJL.setFont(f12b);

		numdispJCB = new JComboBox(tenStrings);
		numdispJCB.addActionListener(dispJCBaction);
		numdispJCB.setMaximumSize(new Dimension(numdispJCB.getPreferredSize()));
		numdispJCB.setSelectedIndex(0);

		JLabel keyJL = new JLabel("Correct response key:   ", JLabel.RIGHT);
		keyJL.setFont(f12b);
		keyJTF = new JTextField("", 3);
		keyJTF.setMaximumSize(new Dimension(keyJTF.getPreferredSize()));
		keyJTF.setToolTipText("Caps lock should be OFF.  Response key should not require use of shift key.");

		JLabel typeJL = new JLabel("Category of trial:  ", JLabel.RIGHT);
		typeJL.setFont(f12b);
		typeJTF = new JTextField("", 12);
		typeJTF.setMaximumSize(new Dimension(typeJTF.getPreferredSize()));
		typeJTF.setToolTipText("The trial category should not contain spaces.");

		typebox = Box.createHorizontalBox();
		typebox.add(numDisplayJL);
		typebox.add(numdispJCB);
		typebox.add(Box.createHorizontalStrut(25));
		typebox.add(keyJL);
		typebox.add(keyJTF);
		typebox.add(Box.createHorizontalStrut(25));
		typebox.add(typeJL);
		typebox.add(typeJTF);
		return typebox;
	
	}

//========================
//creates box with buttons
//========================
		
	private Box btnBox(){
	
		Box bb;

		addJB = new JButton("Add trial");
		addJB.addActionListener(addJBaction);

		createJB = new JButton("Create file");
		createJB.addActionListener(createJBaction);

		resetJB = new JButton("Reset script");
		resetJB.setToolTipText("Clears entire script.");
		resetJB.addActionListener(resetJBaction);

		bb = Box.createHorizontalBox();
		bb.add(addJB);
		bb.add(Box.createHorizontalStrut(20));
		bb.add(createJB);
		bb.add(Box.createHorizontalStrut(20));
		bb.add(resetJB);

		return bb;		
	}

//==============================================
//set up tool tips on first row of trial details
//==============================================

	private void setupToolTips(){

	String tt2 = "<html>To specify the stumulus, type a word or phrase <br> or an image or sound filename. <br><br> To randomly choose a stimulus from a given set, <br> type a text filename (containing a list of words) or <br> the name of a directory containing image or <br> sound files as <b>directory/*.*</b></html>";

		ddp1.xJTF.setToolTipText("value must be an integer between 0 and 1024.");
		ddp1.yJTF.setToolTipText("value must be an integer between 0 and 768.");	
		ddp1.stimJTF.setToolTipText(tt2);
		ddp1.durJTF.setToolTipText("<html>Stimulus duration in seconds.<br>0 = stimulus stays on until<br>a response key is pressed.</html>"); 
		ToolTipManager.sharedInstance().setInitialDelay(250);
		ToolTipManager.sharedInstance().setDismissDelay(7500);
	}
	
//======================================================================
//error checking
//======================================================================

	public void varError (int errorNum){

//		startJB.setEnabled(false);
//		tabbedPane.setSelectedIndex(1);
		JDialog.setDefaultLookAndFeelDecorated(true);
//		badVar = true;


		String s1 = "Number of trials must be an integer greater than zero and less than 201.";
		String s2 = "Must have at least 1 display in trial.";
		String s3 = "Response key must be a single character on the keyboard.";
		String s4 = "Trial categories should not contain spaces.";
		String err = "";

		if (errorNum == 1){
			err = s1;
		} else {if (errorNum == 2){
			err = s2;
		} else {if (errorNum == 3){
			err = s3;
		} else {if (errorNum == 4){
			err = s4;
		}}}}
		JOptionPane.showMessageDialog(this, err, "Variable error", JOptionPane.ERROR_MESSAGE); 
	
	}
}

