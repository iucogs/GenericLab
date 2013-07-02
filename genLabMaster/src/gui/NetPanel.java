package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import com.sun.jna.platform.win32.BaseTSD.SSIZE_T;

import core.GenLab;


import net.miginfocom.swing.MigLayout;

public class NetPanel extends JPanel {

	JTable table;
	JScrollPane jsp;
	GenLab genLab = GenLab.getInstance();
	
	public NetPanel(){
		
		//Table Setup
		this.setBackground(new Color(200,200,200));
		MigLayout lay = new MigLayout();
		this.setLayout(lay);
		this.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		this.add(new JLabel("Choose an experiment from the server."),"align left,wrap");
		//The Table will be added when the panel gains focus
	}
	
	public void setupTable(){
		String[] colsNames = {"Experiment","Author"};
//		Object[][] data = {{"EX1","Jay"},{"Ex2","Dave"},{"Ex2","Dave"},{"Ex2","Dave"},{"Ex2","Dave"},
//				{"Ex2","Dave"},{"Ex2","Dave"},{"Ex2","Dave"},{"Ex2","Dave"}};
		table = new JTable(new MyTableModel(populateDataFromRemoteFile(),colsNames));
		table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

		//Scroll Pane Setup
		jsp = new JScrollPane(table);
		jsp.setPreferredSize(new java.awt.Dimension(400,100));
		table.setFillsViewportHeight(true);
		
		//Sorting Setup
		table.setAutoCreateRowSorter(true);
		
		this.add(jsp,"");
		this.add(new JLabel("Setup Fired"));
		this.repaint();

	}
	
	
	public Object[][] populateDataFromRemoteFile(){
		try {
			//	System.out.println("Codebase is:" + genLab.getDocumentBase());
			URL url = new URL(genLab.getCodeBase().toString() + "/ExperimentList.txt");;
			File list = new File(url.toURI());
			
			Scanner s = new Scanner(list);
			ArrayList<Object[]> items = new ArrayList<Object[]>();
			while(s.hasNextLine()){
				String line = s.nextLine();
				items.add(line.split("~"));
				System.out.println("Line:" + line);
				for (String str : line.split("~")){
					System.out.println("Here's a part:" + str);
				}
				//System.out.println("parts:"+line.split("~")[0] + "~"+line.split("~")[1]);
			}
			Object[][] toReturn = new Object[items.size()][2];
			for(int i=0;i<items.size();i++){
				toReturn[i][0] = items.get(i)[0];
				toReturn[i][1] = items.get(i)[1];
			}
			return toReturn;
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	 class MyTableModel extends DefaultTableModel {
		 
		   public MyTableModel(Object[][] tableData, Object[] colNames) {
			   super(tableData, colNames);
			}
	 
	        public boolean isCellEditable(int row, int col) {
	        	return false;
	        }
	    }


	
}
