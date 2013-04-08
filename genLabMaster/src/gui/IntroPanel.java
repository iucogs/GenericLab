package gui;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;


public class IntroPanel extends JPanel {

	JButton createExperiment,loadExperiment;
	private JTabbedPane tabbedPane;
	
	public IntroPanel(JTabbedPane pane){
		
		this.tabbedPane = pane;
		
		createExperiment = new JButton("Create a new\n Experiment");
		loadExperiment = new JButton("Load an Existing\n Experiment");
		
		createExperiment.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				showCreateButtons();
			}
		});
		loadExperiment.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				showLoadButtons();
			}
		});
		
		this.setBackground(new Color(200,200,200));
		MigLayout lay = new MigLayout();
		this.setLayout(lay);
		this.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		//this.add(new JLabel("Please choose an option."),"span 2,align center,wrap");
		this.add(createExperiment,"push,w 200!, h 100!,align right");
		this.add(loadExperiment,"w 200!, h 100!,align left,push");
	}
	
	private void switchToTab(int i) {
		tabbedPane.setSelectedIndex(i);
	}
	
	private void showLoadButtons()
	{
		this.remove(createExperiment);
		this.remove(loadExperiment);
		
		///Create server,JSON,and script load buttons/listeners
		JButton loadScriptJB = new JButton("Load from Script file");
		loadScriptJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//TODO
			}
		});
		JButton loadJsonJB = new JButton("Load from JSON file");
		loadJsonJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//TODO
			}
		});
		JButton loadServerJB = new JButton("Load from the server");
		loadServerJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//TODO
			}
		});
		///Add them to the layout
		this.add(new JLabel("Choose an option..."),"wrap");
		this.add(loadScriptJB,"w 200!, h 100!");
		this.add(loadJsonJB,"w 200!, h 100!");
		this.add(loadServerJB,"w 200!, h 100!");
		this.repaint();
	}
	
	private void showCreateButtons()
	{
		//switchToTab(3);
	}
	
}
