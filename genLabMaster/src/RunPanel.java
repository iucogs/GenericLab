//package genlab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RunPanel extends JPanel {

	PresentationPanel presPan;
	JButton startJB, instructionsJB;
	JLabel feedbackJL, promptJL, blankJL;

	public RunPanel(){

		Box vb, vb2;

		setBackground(Color.white);
		presPan = new PresentationPanel();
		presPan.setVisible(true);
		this.setVisible(true);
		startJB = new JButton("Start Experiment");
		startJB.setEnabled(false); // The user has not loaded a script yet.  Keep button disabled.
        instructionsJB = new JButton("Instructions");
		promptJL = new JLabel("");
		blankJL = new JLabel("");


		promptJL.setFont(new Font("Arial", Font.BOLD, 14));
		feedbackJL = new JLabel("");

		feedbackJL.setFont(new Font("Arial", Font.BOLD, 14));

		vb = Box.createVerticalBox();
		vb.add(Box.createVerticalStrut(10));
		vb.add(presPan);
		vb.add(Box.createVerticalStrut(30));
		vb.add(startJB);
		startJB.setAlignmentX(Component.CENTER_ALIGNMENT);
		vb.add(Box.createVerticalStrut(30));
		vb.add(promptJL);
		promptJL.setAlignmentX(Component.CENTER_ALIGNMENT);
		vb.add(Box.createVerticalStrut(5));
		vb.add(feedbackJL);
		feedbackJL.setAlignmentX(Component.CENTER_ALIGNMENT);
		vb.add(blankJL);
		blankJL.setAlignmentX(Component.CENTER_ALIGNMENT);
                vb.add(Box.createVerticalStrut(30));
		vb.add(instructionsJB);
                instructionsJB.setAlignmentX(Component.CENTER_ALIGNMENT);
                instructionsJB.setVisible(true);
                
		add(vb);
	}

}

