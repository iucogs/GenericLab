package gui;
//package genlab;

import javax.swing.*;

import core.GenLab;

import java.awt.*;
import java.awt.event.*;

public class RunPanel extends AbstractGenlabPanel {

	public PresentationPanel presPan;
	public JButton startJB, instructionsJB;
	public  JLabel feedbackJL, promptJL, blankJL;

	@Override
	public boolean loadPanel() {
		//TODO:  Do init here
		return true;
	}
	
	@Override
	public boolean leavePanel() {
		return true;
	}
	
	
	public RunPanel(){
		///Setup Components
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
		///Setup Listeners
		startJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GenLab.getInstance().runPressed(); // This button should be disabled => this
									// action unperformable
			} // if the parse script failed.
		});
		instructionsJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(GenLab.getInstance(), GenLab.getInstance().getExperiment().instructions,
						"Instructions", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		///Setup GUI
		Box vb, vb2;
		setBackground(Color.white);
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

