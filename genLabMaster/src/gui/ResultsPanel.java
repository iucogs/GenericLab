package gui;
//package genlab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.io.*;
import java.util.*;

public class ResultsPanel extends JPanel {

	public JTextArea resultsJTA;
	JScrollPane pane;
	JButton saveJB, saveRawDataJB;
	Color resultsGreen = new Color(169, 202, 109);	
	String fileContent = "";
        String rawDataFileContent = "";

	public ResultsPanel(){

		setBackground(resultsGreen);
  // Create a scrollable text area

		resultsJTA = new JTextArea("", 30, 60);
	
		resultsJTA.setMaximumSize(new Dimension(resultsJTA.getPreferredSize()));
//		resultsJTA.setLineWrap(true);
//		resultsJTA.setWordWrapStyle(true);
		resultsJTA.setEditable(false);
//	resultsJTA.setText("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\na\nb\nc\nd\ne\nf\ng\nh\n\n\n\n\n\n\n\nij");

    	 	pane = new JScrollPane(resultsJTA);


    // Get the default scrollbar policy
    int hpolicy = pane.getHorizontalScrollBarPolicy();
    // JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
    
    int vpolicy = pane.getVerticalScrollBarPolicy();
    // JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
    
    
    // Make the scrollbars always appear
    pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
 //   pane.getVerticalScrollBar().setValue(pane.getVerticalScrollBar().getMaximum());


		saveJB = new JButton("Save data in file");
		AbstractAction saveJBaction = new AbstractAction(){
			public void actionPerformed(ActionEvent ae) {
				String filename = "";
				JFileChooser jfc = new JFileChooser();
				Container parent = saveJB.getParent();
				int userchoice = jfc.showSaveDialog(parent);
				if (userchoice == JFileChooser.APPROVE_OPTION){
					filename = jfc.getSelectedFile().getAbsolutePath();
				}
				try{
					BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
					bw.write(fileContent);
					bw.flush();
					bw.close();
				} catch(IOException ioe){
					JOptionPane.showMessageDialog(parent, "Cannot write to file " + filename, "I/O error", JOptionPane.ERROR_MESSAGE); 
				}
			}
		};
		saveJB.addActionListener(saveJBaction);


                saveRawDataJB = new JButton("Save raw data in file");
		AbstractAction saveRawDataJBaction = new AbstractAction(){
			public void actionPerformed(ActionEvent ae) {
				String filename = "";
				JFileChooser jfc = new JFileChooser();
				Container parent = saveRawDataJB.getParent();
				int userchoice = jfc.showSaveDialog(parent);
				if (userchoice == JFileChooser.APPROVE_OPTION){
					filename = jfc.getSelectedFile().getAbsolutePath();
				}
				try{
					BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
					bw.write(rawDataFileContent);
					bw.flush();
					bw.close();
				} catch(IOException ioe){
					JOptionPane.showMessageDialog(parent, "Cannot write to file " + filename, "I/O error", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		saveRawDataJB.addActionListener(saveRawDataJBaction);


		Box vb = Box.createVerticalBox();
		vb.add(Box.createVerticalStrut(10));
		vb.add(pane);
		resultsJTA.setAlignmentX(Component.CENTER_ALIGNMENT);
		vb.add(Box.createVerticalStrut(30));
                add(vb);
                Box vb2 = Box.createHorizontalBox();
		vb2.add(saveJB);
                vb2.add(Box.createHorizontalStrut(30));
	//	saveJB.setAlignmentX(Component.LEFT_ALIGNMENT);
                vb2.add(saveRawDataJB);
	//	saveRawDataJB.setAlignmentX(Component.RIGHT_ALIGNMENT);
		add(vb2);
	}

	public void setFileContent(String fileContent) {

		this.fileContent = fileContent;
	}

        public void setRawDataFileContent(String fileContent) {

		this.rawDataFileContent = fileContent;
	}

	public void setVerticalScrollbarToTop() {

		this.pane.getVerticalScrollBar().setValue(10);
	}


}

