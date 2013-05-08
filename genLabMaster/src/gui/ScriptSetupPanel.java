package gui;
//package genlab;


import javax.swing.*;

import core.ExperimentUtilities;
import core.GenLab;

import java.awt.*;
import java.awt.event.*;
import java.awt.GraphicsEnvironment.*;
import java.lang.*;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.io.DataInputStream;
import java.net.MalformedURLException;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Vector;

public class ScriptSetupPanel extends JPanel {

	JTextField fileJTF, repJTF, delayJTF, horizJTF, vertJTF, promptJTF;
	JButton browseJB, browseRemoteJB, saveToJsonJB;

	JRadioButton tOrderJRB, tRandomJRB, nofbJRB, fbJRB, dOrderJRB, dRandomJRB;
	JRadioButton donJRB, nodonJRB, pstrJRB, nopstrJRB;
	boolean tOrderRandom, feedback, dOrderRandom, displayOn, prompt;

	JComboBox fjcb, fsjcb, remoteFilesCB;

	String scriptname = "";

	Color gold = new Color(225, 221, 95);
	Font f12b = new Font("Arial", Font.BOLD, 12);

	private File scriptDirectory;

	public ScriptSetupPanel(){

		Box hbox1, hbox2, hbox3, hbox4, hbox5, vertbox;
		setBackground(gold);

		hbox1 = filebox();
		hbox2 = trialsbox();
		hbox3 = displaybox();
		hbox4 = delaybox();
		hbox5 = savebox();

		vertbox = Box.createVerticalBox();
		vertbox.add(Box.createVerticalStrut(40));
		vertbox.add(hbox1);
		hbox1.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertbox.add(Box.createVerticalStrut(30));
		vertbox.add(hbox2);
		hbox2.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertbox.add(Box.createVerticalStrut(20));
		vertbox.add(hbox3);
		hbox3.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertbox.add(Box.createVerticalStrut(20));
		vertbox.add(hbox4);
		hbox4.setAlignmentX(Component.LEFT_ALIGNMENT);
		vertbox.add(Box.createVerticalStrut(20));
		vertbox.add(hbox5);
		hbox5.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(vertbox);
	}

	private Box filebox(){

		Box fbox;

		JLabel fileJL = new JLabel("Script filename:", JLabel.RIGHT);
		fileJL.setFont(f12b);
		fileJTF = new JTextField("Use browse button or menu", 20);
		fileJTF.setMaximumSize(new Dimension(fileJTF.getPreferredSize()));
		fileJTF.setEditable(false);
		browseJB = new JButton("Browse Local Files");

		AbstractAction browseJBaction = new AbstractAction(){	
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent ae) {
				//Todo: Get this to work in browser applet
				scriptname = "";
				AccessController.doPrivileged(
				        new PrivilegedAction() {
				            public Object run()  {
				            	JFileChooser jfc = new JFileChooser(".");
								//File dir1 = new File(System.getProperty("user.dir"));
								//jfc.setCurrentDirectory(dir1);
								Container parent = browseJB.getParent();
								int userchoice = jfc.showOpenDialog(parent);
								if (userchoice == JFileChooser.APPROVE_OPTION){
									scriptDirectory = jfc.getCurrentDirectory();
									scriptname = jfc.getSelectedFile().getAbsolutePath();
									fileJTF.setText(scriptname);
									return scriptname;
								}
								return "";  
				            }
				        });
				
				
			}
		};

		browseJB.addActionListener(browseJBaction);

            //    browseRemoteJB = new JButton("Browse Remote");



            //    AbstractAction browseRemoteJBaction = new AbstractAction(){
             //       Vector str = new Vector();
             //       str.addElement("Browse Remote Files");
		//	public void actionPerformed(ActionEvent ae) {
                         //   try
                         //   {



                         //       URL url = new URL("http://dev.cogs.indiana.edu/ruth/test.php");
                        //        URLConnection urlConn = url.openConnection();
                        //        urlConn.setDoInput(true);
                        //        urlConn.setUseCaches(false);

                       //         DataInputStream dis = new DataInputStream(urlConn.getInputStream());

                     //           String s;

                      //          while ((s = dis.readLine()) != null)
                    //            {
                    //               str.addElement(s);
                    //            }
                 //               dis.close();

              //               }
              //              catch (MalformedURLException mue) {}
             //               catch (IOException ioe) {}

//String[] aa = {"1"};
         //     remoteFilesCB = new JComboBox(str);
         //       remoteFilesCB.setMaximumSize(new Dimension(remoteFilesCB.getPreferredSize()));
	//	remoteFilesCB.setSelectedItem("1");
                          // fileJTF.setText(str);


		//	}
		//};

	//	browseRemoteJB.addActionListener(browseRemoteJBaction);

		fbox = Box.createHorizontalBox();
		fbox.add(fileJL);
		fbox.add(Box.createHorizontalStrut(10));
		fbox.add(fileJTF);
		fbox.add(Box.createHorizontalStrut(20));
		fbox.add(browseJB);
                fbox.add(Box.createHorizontalStrut(20));
	//	fbox.add(remoteFilesCB);
		return fbox;
	
	}

	private Box trialsbox(){

		Box hbox1, hbox2, tbox;

		JLabel repJL = new JLabel("Repetitions of each trial:", JLabel.RIGHT);
		repJL.setFont(f12b);
		repJTF = new JTextField("1", 3);
		repJTF.setMaximumSize(new Dimension(repJTF.getPreferredSize()));

		JLabel tOrderJL = new JLabel("Trial order:", JLabel.RIGHT);
		tOrderJL.setFont(f12b);

		tOrderJRB = new JRadioButton("Use order in file");
		tOrderJRB.setOpaque(true);
		tOrderJRB.setBackground(gold);
		tOrderRandom = true;

		tRandomJRB = new JRadioButton("Use random order", true);
		tRandomJRB.setOpaque(true);
		tRandomJRB.setBackground(gold);

		ButtonGroup tOrderGroup = new ButtonGroup();
		tOrderGroup.add(tOrderJRB);
		tOrderGroup.add(tRandomJRB);

		tOrderJRB.setActionCommand("tord");
		tRandomJRB.setActionCommand("trand");
		AbstractAction tOrderGroupaction = new AbstractAction () {
			public void actionPerformed(ActionEvent e) {			
				String torStr;
				torStr = e.getActionCommand();
				if (torStr.equals("tord")){
					tOrderRandom = false;
				} else {
					tOrderRandom = true;
				}
			}
		};
		tOrderJRB.addActionListener(tOrderGroupaction);
		tRandomJRB.addActionListener(tOrderGroupaction);
	
		hbox1 = Box.createHorizontalBox();
		hbox1.add(repJL);
		hbox1.add(Box.createHorizontalStrut(10));
		hbox1.add(repJTF);

		hbox2 = Box.createHorizontalBox();
		hbox2.add(tOrderJL);
		hbox2.add(Box.createHorizontalStrut(5));
		hbox2.add(tOrderJRB);
		hbox2.add(Box.createHorizontalStrut(10));
		hbox2.add(tRandomJRB);

		tbox = Box.createVerticalBox();
		tbox.add(hbox1);
		hbox1.setAlignmentX(Component.LEFT_ALIGNMENT);
		tbox.add(Box.createVerticalStrut(20));
		tbox.add(hbox2);
		hbox2.setAlignmentX(Component.LEFT_ALIGNMENT);

		return tbox;
	}

	private Box displaybox(){

		Box hb0, hb1, hb2, hb3, hb4, hb5, vb;

		JLabel dOrderJL = new JLabel("Order of displays within trial:", JLabel.RIGHT);
		dOrderJL.setFont(f12b);

		dOrderJRB = new JRadioButton("Use order in file:", true);
		dOrderJRB.setOpaque(true);
		dOrderJRB.setBackground(gold);
		dOrderRandom = false;

		dRandomJRB = new JRadioButton("Use random order");
		dRandomJRB.setOpaque(true);
		dRandomJRB.setBackground(gold);

		ButtonGroup dOrderGroup = new ButtonGroup();
		dOrderGroup.add(dOrderJRB);
		dOrderGroup.add(dRandomJRB);

		dOrderJRB.setActionCommand("dord");
		dRandomJRB.setActionCommand("drand");
		AbstractAction dOrderGroupaction = new AbstractAction () {
			public void actionPerformed(ActionEvent e) {			
				String dorStr;
				dorStr = e.getActionCommand();
				if (dorStr.equals("dord")){
					dOrderRandom = false;
				} else {
					dOrderRandom = true;
				}
			}
		};
		dOrderJRB.addActionListener(dOrderGroupaction);
		dRandomJRB.addActionListener(dOrderGroupaction);
		
		hb0 = Box.createHorizontalBox();
		hb0.add(dOrderJL);
		hb0.add(Box.createHorizontalStrut(5));
		hb0.add(dOrderJRB);
		hb0.add(Box.createHorizontalStrut(10));
		hb0.add(dRandomJRB);


		JLabel fontJL = new JLabel("Font and size of word stimuli:", JLabel.RIGHT);
		fontJL.setFont(f12b);
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		fjcb = new JComboBox(fonts);
		fjcb.setMaximumSize(new Dimension(fjcb.getPreferredSize()));
		fjcb.setSelectedItem("Arial");
		String[] fontsizes = {"8", "10", "12", "14", "16", "18", "20", "24", "28", "32", "36", "40", "48"};
		fsjcb = new JComboBox(fontsizes);
		fsjcb.setMaximumSize(new Dimension(fsjcb.getPreferredSize()));
		fsjcb.setSelectedItem("18");

		hb1 = Box.createHorizontalBox();
		hb1.add(fontJL);
		hb1.add(Box.createHorizontalStrut(10));
		hb1.add(fjcb);
		hb1.add(Box.createHorizontalStrut(20));
		hb1.add(fsjcb);

		JLabel dispOnJL = new JLabel("Leave old display on when drawing new?", JLabel.RIGHT);
		dispOnJL.setFont(f12b);

		nodonJRB = new JRadioButton("No", true);
		nodonJRB.setOpaque(true);
		nodonJRB.setBackground(gold);
		displayOn = false;

		donJRB = new JRadioButton("Yes");
		donJRB.setOpaque(true);
		donJRB.setBackground(gold);

		ButtonGroup dispOnGroup = new ButtonGroup();
		dispOnGroup.add(nodonJRB);
		dispOnGroup.add(donJRB);

		nodonJRB.setActionCommand("nodon");
		donJRB.setActionCommand("don");
		AbstractAction dispOnGroupaction = new AbstractAction () {
			public void actionPerformed(ActionEvent e) {			
				String donStr;
				donStr = e.getActionCommand();
				if (donStr.equals("nodon")){
					displayOn = false;
				} else {
					displayOn = true;
				}
			}
		};
		nodonJRB.addActionListener(dispOnGroupaction);
		donJRB.addActionListener(dispOnGroupaction);

		hb2 = Box.createHorizontalBox();
		hb2.add(dispOnJL);
		hb2.add(Box.createHorizontalStrut(5));
		hb2.add(nodonJRB);
		hb2.add(Box.createHorizontalStrut(10));
		hb2.add(donJRB);

		JLabel posJL = new JLabel("Ranges for random display position:", JLabel.RIGHT);
		posJL.setFont(f12b);
		JLabel horizJL = new JLabel("Horizontal (1 - 390):  ", JLabel.RIGHT);
		horizJL.setFont(f12b);
		horizJTF = new JTextField("390", 3);
		horizJTF.setMaximumSize(new Dimension(horizJTF.getPreferredSize()));
		JLabel vertJL = new JLabel("Vertical (1 - 250):  ", JLabel.RIGHT);
		vertJL.setFont(f12b);
		vertJTF = new JTextField("250", 3);
		vertJTF.setMaximumSize(new Dimension(vertJTF.getPreferredSize()));

		hb3 = Box.createHorizontalBox();
		hb3.add(posJL);
		hb3.add(Box.createHorizontalStrut(10));
		hb3.add(horizJL);
		hb3.add(Box.createHorizontalStrut(5));
		hb3.add(horizJTF);
		hb3.add(Box.createHorizontalStrut(20));
		hb3.add(vertJL);
		hb3.add(Box.createHorizontalStrut(5));
		hb3.add(vertJTF);

		JLabel promptJL = new JLabel("Present prompt string after displays?", JLabel.RIGHT);
		promptJL.setFont(f12b);
	
		nopstrJRB = new JRadioButton("No", true);
		nopstrJRB.setOpaque(true);
		nopstrJRB.setBackground(gold);
		prompt = false;

		pstrJRB = new JRadioButton("Yes:");
		pstrJRB.setOpaque(true);
		pstrJRB.setBackground(gold);

		ButtonGroup promptGroup = new ButtonGroup();
		promptGroup.add(nopstrJRB);
		promptGroup.add(pstrJRB);

		nopstrJRB.setActionCommand("nopstr");
		pstrJRB.setActionCommand("pstr");
		AbstractAction promptGroupaction = new AbstractAction () {
			public void actionPerformed(ActionEvent e) {			
				String pstrStr;
				pstrStr = e.getActionCommand();
				if (pstrStr.equals("nopstr")){
					prompt = false;
				} else {
					prompt = true;
				}
			}
		};
		nopstrJRB.addActionListener(promptGroupaction);
		pstrJRB.addActionListener(promptGroupaction);
		promptJTF = new JTextField("", 25);
		promptJTF.setMaximumSize(new Dimension(promptJTF.getPreferredSize()));

		hb4 = Box.createHorizontalBox();
		hb4.add(promptJL);
		hb4.add(Box.createHorizontalStrut(10));
		hb4.add(nopstrJRB);
		hb4.add(Box.createHorizontalStrut(10));
		hb4.add(pstrJRB);
		hb4.add(Box.createHorizontalStrut(5));
		hb4.add(promptJTF);

		JLabel fbJL = new JLabel("Give feedback after response?", JLabel.RIGHT);
		fbJL.setFont(f12b);
		nofbJRB = new JRadioButton("No");
		nofbJRB.setOpaque(true);
		nofbJRB.setBackground(gold);

		fbJRB = new JRadioButton("Yes", true);
		fbJRB.setOpaque(true);
		fbJRB.setBackground(gold);
		feedback = true;

		ButtonGroup fbGroup = new ButtonGroup();
		fbGroup.add(nofbJRB);
		fbGroup.add(fbJRB);

		nofbJRB.setActionCommand("nofbk");
		fbJRB.setActionCommand("fbk");
		AbstractAction fbGroupaction = new AbstractAction () {
			public void actionPerformed(ActionEvent e) {			
				String fbStr;
				fbStr = e.getActionCommand();
				if (fbStr.equals("nofbk")){
					feedback = false;
				} else {
					feedback = true;
				}
			}
		};
		nofbJRB.addActionListener(fbGroupaction);
		fbJRB.addActionListener(fbGroupaction);

		hb5 = Box.createHorizontalBox();
		hb5.add(fbJL);
		hb5.add(Box.createHorizontalStrut(10));
		hb5.add(nofbJRB);
		hb5.add(Box.createHorizontalStrut(10));
		hb5.add(fbJRB);	

		vb = Box.createVerticalBox();
		vb.add(hb0);
		hb0.setAlignmentX(Component.LEFT_ALIGNMENT);
		vb.add(Box.createVerticalStrut(20));
		vb.add(hb1);
		hb1.setAlignmentX(Component.LEFT_ALIGNMENT);
		vb.add(Box.createVerticalStrut(20));
		vb.add(hb2);
		hb2.setAlignmentX(Component.LEFT_ALIGNMENT);
		vb.add(Box.createVerticalStrut(20));
		vb.add(hb3);
		hb3.setAlignmentX(Component.LEFT_ALIGNMENT);		
		vb.add(Box.createVerticalStrut(20));
		vb.add(hb4);
		hb4.setAlignmentX(Component.LEFT_ALIGNMENT);		
		vb.add(Box.createVerticalStrut(20));
		vb.add(hb5);
		hb5.setAlignmentX(Component.LEFT_ALIGNMENT);		
		return vb;

	}

	private Box delaybox(){

		Box dbox;

		JLabel delayJL = new JLabel("Delay between trials (ms):", JLabel.RIGHT);
		delayJL.setFont(f12b);
		delayJTF = new JTextField("1200", 3);
		delayJTF.setMaximumSize(new Dimension(delayJTF.getPreferredSize()));

		dbox = Box.createHorizontalBox();
		dbox.add(delayJL);
		dbox.add(Box.createHorizontalStrut(10));
		dbox.add(delayJTF);
		return dbox;
	}

	private Box savebox(){
		JLabel saveJL = new JLabel("Save this script + settings to new JSON format?", JLabel.RIGHT);
		saveJL.setFont(f12b);
		saveToJsonJB = new JButton("Save To JSON");
		saveToJsonJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String filename = "";
				JFileChooser jfc = new JFileChooser(".");
				//File dir1 = new File(System.getProperty("user.dir"));
				//jfc.setCurrentDirectory(dir1);
				int userchoice = jfc.showSaveDialog(GenLab.getInstance());
				if (userchoice == JFileChooser.APPROVE_OPTION){
					filename = jfc.getSelectedFile().getAbsolutePath();
					ExperimentUtilities.experimentToJson(GenLab.getInstance().experiment, filename);
				}
				else
				{
					System.err.println("JFileChooser for JSON save failed.");
				}
			}
		});
		Box sbox = Box.createHorizontalBox();
		sbox.add(saveJL);
		sbox.add(Box.createHorizontalStrut(10));
		sbox.add(saveToJsonJB);
		return sbox;
		//TODO: hook up listener to saveToJsonJB
	}
	
	public String getScript(){
		return scriptname;	
	}

	public String getScriptDirectory(){
		return scriptDirectory.getAbsolutePath() + File.separator;	
	}

	public String getReps(){
		String reps = repJTF.getText();
		return reps;
	}

	public boolean getTrialOrder(){
		return tOrderRandom;
	}

	public boolean getDisplayOrder(){
		return dOrderRandom;
	}

	public String getFontFace(){
		String ff = (String)fjcb.getSelectedItem();
		return ff;
	}

	public int getFontSize(){
		int fs = Integer.parseInt((String)fsjcb.getSelectedItem());
		return fs;
	}

	public boolean getDisplayOn(){
		return displayOn;
	}

	public String getHorizRange(){
		String hr = horizJTF.getText();
		return hr;
	}

	public String getVertRange(){
		String vr = vertJTF.getText();
		return vr;
	}

	public boolean getPrompt(){
		return prompt;
	}

	public String getPromptString(){
		String ps = promptJTF.getText();
		return ps;
	}

	public boolean getFeedback(){
		return feedback;
	}

	public String getDelay(){
		String d = delayJTF.getText();
		return d;
	}

}

