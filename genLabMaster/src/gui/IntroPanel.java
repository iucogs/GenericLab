package gui;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import core.GenLab;
import net.miginfocom.swing.MigLayout;


public class IntroPanel extends JPanel {

	private JButton createExperimentJB, loadExperimentJB,
					loadScriptJB, loadJsonJB, loadServerJB,
					createScriptJB, createFancyJB,
					backToIntroJB;
	
	public IntroPanel(){
		setupButtons();
		this.setBackground(new Color(200,200,200));
		doIntroMenu();
	}

	public void doIntroMenu() {
		this.removeAll();
		MigLayout layout = new MigLayout("align center","push[][]push","push[][][]push");
		this.setLayout(layout);	
		this.add(new JLabel("Choose an option..."),"cell 0 0,span 2,align center");
		this.add(createExperimentJB,"cell 0 1,w 200!, h 100!,align right");
		this.add(loadExperimentJB,"cell 1 1,w 200!, h 100!,align left,push");
		this.repaint();
	}
	
	private void doVertLoadMenu() {
		this.removeAll();
		MigLayout lay = new MigLayout("align center","push[][]push","push[][][]push");
		this.setLayout(lay);
		
		///Setup layout
		this.add(backToIntroJB,"cell 0 1,w :180, h 65!,align right");

		this.add(loadScriptJB,	"cell 1 0,w :200:, h 75!");
		this.add(loadJsonJB,	"cell 1 1,w :200:, h 75!");
		this.add(loadServerJB,	"cell 1 2,w :200:, h 75!");
		
	//	this.add(backToIntroJB,"dock south, h 40!,align center");

		this.repaint();
	}
	
	private void doCreateMenu()
	{
		this.removeAll();
		MigLayout lay = new MigLayout("align center","push[][]push","push[][]push");
		this.setLayout(lay);
		
		///Setup layout
		this.add(backToIntroJB,"cell 1 0,spany 2,w :180, h 75!,align right");
		this.add(createFancyJB,"cell 0 0,w :200:, h 75!");
		this.add(createScriptJB,"cell 0 1,w :200:, h 75!");
		
	//	this.add(backToIntroJB,"dock south, h 40!,align center");

		this.repaint();
	}
	
	private void doHorzLoadMenu()
	{
		this.removeAll();
		MigLayout lay = new MigLayout("align center","push[][][]push","push[]20[][]push");
		this.setLayout(lay);
		
		///Setup layout
		this.add(new JLabel("Choose an option..."),"cell 1 0, wrap,align center");
		this.add(loadScriptJB,"cell 0 1,w :200:, h 75!");
		this.add(loadJsonJB,"cell 1 1,w :200:, h 75!");
		this.add(loadServerJB,"cell 2 1,w :200:, h 75!,wrap");
		this.add(backToIntroJB,"cell 0 2, span 3, h 40!,align center");

		this.repaint();
	}
	

	
	/**
	 * Switches to a given tab.
	 * TODO: Rewrite this using the Panels instead of indexes to avoid errors.
	 * @param i
	 */
	private void switchToTab(int i) {
		GenLab.getInstance().tabbedPane.setSelectedIndex(i);
	}
	
	private void setupButtons() {
		//'''Top Level Buttons
		createExperimentJB = new JButton("Create a new\n Experiment");
		createExperimentJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/edit_20.png")));
		createExperimentJB.setVerticalTextPosition(SwingConstants.BOTTOM);
		createExperimentJB.setHorizontalTextPosition(SwingConstants.CENTER);
		createExperimentJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				doCreateMenu();
			}
		});
		loadExperimentJB = new JButton("Load an Existing\n Experiment");
		loadExperimentJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/find_20.png")));
		loadExperimentJB.setVerticalTextPosition(SwingConstants.BOTTOM);
		loadExperimentJB.setHorizontalTextPosition(SwingConstants.CENTER);
		loadExperimentJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				doVertLoadMenu();
			}
		});
		//'''Create Buttons
		createScriptJB = new JButton("Use old script creator");
		createScriptJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				switchToTab(3);
			}
		});
		createFancyJB = new JButton("Use new script creator");
		createFancyJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				switchToTab(1);
			}
		});
		//'''Load Buttons
		loadScriptJB = new JButton("Load from Script file");
		loadScriptJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				switchToTab(5);
			}
		});
		loadJsonJB = new JButton("Load from JSON file");
		loadJsonJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				GenLab.getInstance().setupExperimentFromJson();
				switchToTab(6);
			}
		});
		loadServerJB = new JButton("Load from the server");
		loadServerJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(GenLab.getInstance(),
											  "Not yet implemented.",
											  "", 
											  JOptionPane.ERROR_MESSAGE);
			}
		});
		//'''Back Button
		backToIntroJB = new JButton("Nevermind, take me back.");
		backToIntroJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				doIntroMenu();
			}
		});
	}

}
