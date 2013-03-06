//package genlab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DisplayDetailsPanel extends JPanel {

	Box ddhbox, ddvbox;

	JComboBox stimtypeJCB, stimlocJCB;
	JTextField stimJTF, xJTF, yJTF, durJTF;

	Color varsOrange = new Color(245, 183, 87);
	Font f12b = new Font("Arial", Font.BOLD, 12);

	public DisplayDetailsPanel(int dn){

		setBackground(varsOrange);
		JLabel stimJL = new JLabel("Stimulus" + dn + ":", JLabel.RIGHT);
		stimJL.setFont(f12b);
		String[] stims = {"word", "picture", "sound", "video", "blank"};
		stimtypeJCB = new JComboBox(stims);
		stimtypeJCB.setSelectedIndex(0);

		stimJTF = new JTextField("", 10);
		stimJTF.setMaximumSize(new Dimension(stimJTF.getPreferredSize()));
	
		String[] locs = {"center", "random", "position"};
		stimlocJCB = new JComboBox(locs);
		stimlocJCB.setSelectedIndex(0);

		JLabel xJL = new JLabel("x:  ", JLabel.RIGHT);
		xJL.setFont(f12b);
		xJTF = new JTextField("", 3);
		xJTF.setMaximumSize(new Dimension(xJTF.getPreferredSize()));
		xJTF.setEditable(false);
		JLabel yJL = new JLabel("y:  ", JLabel.RIGHT);
		yJL.setFont(f12b);
		yJTF = new JTextField("", 3);
		yJTF.setMaximumSize(new Dimension(yJTF.getPreferredSize()));
		yJTF.setEditable(false);
		AbstractAction stimlocJCBaction = new AbstractAction(){
			public void actionPerformed(ActionEvent ae) {
				if (stimlocJCB.getSelectedIndex() == 2){
					xJTF.setEditable(true);
					yJTF.setEditable(true);
				} else {
					xJTF.setText("");
					yJTF.setText("");
					xJTF.setEditable(false);
					yJTF.setEditable(false);
				}
			}
		};
		stimlocJCB.addActionListener(stimlocJCBaction);

		JLabel durJL = new JLabel("Duration(sec):", JLabel.RIGHT);
		durJTF = new JTextField("", 3);
		durJTF.setMaximumSize(new Dimension(durJTF.getPreferredSize()));		
		AbstractAction stimtypeJCBaction = new AbstractAction(){
			public void actionPerformed(ActionEvent ae) {
				if (stimtypeJCB.getSelectedIndex() == 3){
					stimJTF.setText("");
					stimJTF.setEditable(false);
					stimlocJCB.setEnabled(false);
					xJTF.setText("");
					xJTF.setEditable(false);
					yJTF.setText("");
					yJTF.setEditable(false);
					durJTF.setEditable(true);
				} else {if (stimtypeJCB.getSelectedIndex() == 2){
					stimJTF.setEditable(true);
					stimlocJCB.setEnabled(false);
					xJTF.setText("");
					xJTF.setEditable(false);
					yJTF.setText("");
					yJTF.setEditable(false);
					durJTF.setText("");
					durJTF.setEditable(false);
				} else {
					stimJTF.setEditable(true);
					stimlocJCB.setEnabled(true);
					durJTF.setEditable(true);
				}}
			}
		};
		stimtypeJCB.addActionListener(stimtypeJCBaction);

		ddhbox = Box.createHorizontalBox();
		ddhbox.add(Box.createHorizontalStrut(10));
		ddhbox.add(stimtypeJCB);
		ddhbox.add(Box.createHorizontalStrut(10));
		ddhbox.add(stimJTF);
		ddhbox.add(Box.createHorizontalStrut(10));
		ddhbox.add(stimlocJCB);
		ddhbox.add(Box.createHorizontalStrut(5));
		ddhbox.add(xJL);
		ddhbox.add(xJTF);
		ddhbox.add(Box.createHorizontalStrut(5));
		ddhbox.add(yJL);
		ddhbox.add(yJTF);
		ddhbox.add(Box.createHorizontalStrut(10));
		ddhbox.add(durJL);
		ddhbox.add(durJTF);

		ddvbox = Box.createVerticalBox();
		ddvbox.add(stimJL);
		stimJL.setAlignmentX(Component.LEFT_ALIGNMENT);
		ddvbox.add(Box.createVerticalStrut(5));
		ddvbox.add(ddhbox);
		ddhbox.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(ddvbox);

	}

	public String getDetails(){

		String st, s, sl, x, y, d, detailsStr;
		int i1, dot, dotExt;
		boolean allvarsOk;

		detailsStr = "";
		allvarsOk = true;

//get stimulus type
		st = (String)stimtypeJCB.getSelectedItem();
		i1 = stimtypeJCB.getSelectedIndex();

//get word, phrase, or filename
		s = stimJTF.getText();
		if (!(i1 == 3)){
			if (s.equals("")){
				errorMsg(8);
				allvarsOk = false;
			} else {
				if (i1 == 0){ //word
					dot = s.indexOf(".");
					dotExt = s.indexOf(".txt");
					if ((dot == 0) || (dot != dotExt)){
						errorMsg(1);
						allvarsOk = false;
					}						
			} else {
//START HERE 
				if (i1 == 1){ //picture
					if (!(checkExt("img"))){
						errorMsg(2);
						allvarsOk = false;
					}
			} else {
				if (i1 == 2){ //sound
					if (!(checkExt("snd"))){
						errorMsg(3);
						allvarsOk = false;
					}
				}
			}}}
		}

//get stumulus location
		sl = (String)stimlocJCB.getSelectedItem();
		x = xJTF.getText();
		y = yJTF.getText();
		if (sl.equals("position")){
			try {
				int xi = Integer.parseInt(x);
				int yi = Integer.parseInt(y);
				if ((xi < 0) || (xi > 1024)){
					errorMsg(5);
					allvarsOk = false;
				}
				if ((yi < 0) || (yi > 768)){
					errorMsg(6);
					allvarsOk = false;
				}
			} catch(NumberFormatException nfe){
				errorMsg(7);
				allvarsOk = false;
			}
		}

//get duration time -- except for sound, add tool tip
			d = durJTF.getText();
			if (!(i1 == 2)){
				if (d.equals("")){
					errorMsg(9);
					allvarsOk = false;
				}
				if (d.length() > 0){
					try {
						Float df = new Float(d);
					} catch(NumberFormatException nfe2){
						errorMsg(9);
						allvarsOk = false;
					}
				}
			}
//if allvarsOk, make string
		if (allvarsOk){
			if (i1 == 3){
				detailsStr = st + " " + d;
			} else {if (i1 == 2){
				detailsStr = st + " " + s;
			} else {if (!(sl.equals("position"))){
				detailsStr = st + " " + s + " " + sl + " " + d;

			} else {
				detailsStr = st + " " + s + " " + sl + " " + x + " " + y + " " + d;
			}}}
			return detailsStr;
		} else {
			return "999";
		}

	}

	private void errorMsg(int em){
	
		String s1 = "Stimulus must be a word, phrase or text file.";
		String s2 = "If stimulus is picture, source must be an image file of type .jpg, .gif or .png or a directory of image files:  directoryname\\*.*";
		String s3 = "If stimulus is sound, source must be a sound file or directory of sounds. Sounds must be of type .aiff, .au, .wav, .midi, .rmf, or a directory of sound files: directoryname\\*.* ";

		String s5 = "x must be an integer between 0 and 1024.";
		String s6 = "y must be an integer between 0 and 768.";
		String s7 = "x and y positions must be integers.";
		String s8 = "Need word, phrase, or source of stimulus.";
		String s9 = "The duration in seconds must be numeric.";
		String ms = "";
		
		if (em == 1){
			ms = s1;
		} else {if (em == 2){
			ms = s2;
		} else {if (em == 3){
			ms = s3;
		} else {if (em == 5){
			ms = s5;
		} else {if (em == 6){
			ms = s6;
		} else {if (em == 7){
			ms = s7;
		} else {if (em == 8){
			ms = s8;
		} else {if (em == 9){
			ms = s9;
		}}}}}}}}

		JOptionPane.showMessageDialog(stimJTF, ms, "Variable error", JOptionPane.ERROR_MESSAGE);

	}

	private boolean checkExt(String type){

		String stim;
		int dot, stardotstar, dotExt;
		boolean b;

		stim = stimJTF.getText();
		stardotstar = stim.indexOf("*.*");

		if (stardotstar == -1){
			dot = stim.indexOf(".");
			if (dot <= 0){
				b = false;
			} else {
				if (type.equals("img")){
					dotExt = stim.indexOf(".gif");
					if (dotExt == -1){
						dotExt = stim.indexOf(".jpg");
						if (dotExt == -1){
							dotExt = stim.indexOf(".png");
							if(dotExt == -1){
								dotExt = stim.indexOf(".mpg");
							}
						}
					}
					if (dot == dotExt){
						b = true;
					} else {
						b = false;
					}
				} else {
					dotExt = stim.indexOf(".aiff");
					if (dotExt == -1){
						dotExt = stim.indexOf(".au");
						if (dotExt == -1){
							dotExt = stim.indexOf(".wav");
							if (dotExt == -1){
								dotExt = stim.indexOf(".midi");
								if (dotExt == -1){
									dotExt = stim.indexOf(".rmf");
								}
							}
						}
					}
					if (dot == dotExt){
						b = true;
					} else {
						b = false;
					}
				}
			}
		} else {
			if (stardotstar < 2){
				b = false;
			} else {
				b = true;
			}
		} 

		return b;
	}
}

