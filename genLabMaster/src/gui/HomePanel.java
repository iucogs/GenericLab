package gui;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import core.ExperimentUtilities;
import core.GenLab;
import experiment.Experiment;
import net.miginfocom.swing.MigLayout;


public class HomePanel extends AbstractGenlabPanel{

	private JButton createExperimentJB, loadExperimentJB,
					loadScriptJB, loadJsonJB, loadServerJB,
					createScriptJB, builderJB,
					backToIntroJB;
	private JLabel optionsTextJL = new JLabel("Scroll over an option.");	
	
	public HomePanel(){
		this.setBackground(new Color(200,200,200));
		setupIntroButtons();
		doHomePanel();
	}

	@Override
	public boolean loadPanel() {
		doHomePanel();
		return true;
	}
	
	@Override
	public boolean leavePanel() {
		return true;
	}
	
	private void doHomePanel()
	{
		//GenLab.getInstance().experiment = new Experiment();
		if (GenLab.getInstance().getExperiment() != null)
			doExperimentView();
		else
			doIntroMenu();
	}
	
	private void doIntroMenu() {
		this.removeAll();
		MigLayout layout = new MigLayout("align center","push[][]push","push[][][]push");
		this.setLayout(layout);	
		JLabel introLabel = new JLabel("No experiment active.  Load or create one.");
		introLabel.setFont(introLabel.getFont().deriveFont(13f));
		//introLabel.setForeground(Color.red);
		this.add(introLabel,"cell 0 0,span 2,align center");
		this.add(createExperimentJB,"cell 0 1,w 200!, h 100!,align right");
		this.add(loadExperimentJB,"cell 1 1,w 200!, h 100!,align left,push");
		this.validate();
		this.repaint();
	}
	
	private void doLoadMenu() {
		this.removeAll();
		MigLayout lay = new MigLayout("align center","push[][]push","push[][][]push");
		this.setLayout(lay);
		
		///Setup layout
		this.add(backToIntroJB,"cell 0 1,w :180, h 65!,align right");

		this.add(loadScriptJB,	"cell 1 0,w :200:, h 75!");
		this.add(loadJsonJB,	"cell 1 1,w :200:, h 75!");
		this.add(loadServerJB,	"cell 1 2,w :200:, h 75!");
		
	//	this.add(backToIntroJB,"dock south, h 40!,align center");
		this.validate();
		this.repaint();
	}
	
	private void doCreateMenu()
	{
		this.removeAll();
		MigLayout lay = new MigLayout("align center","push[][]push","push[][]push");
		this.setLayout(lay);
		
		///Setup layout
		this.add(backToIntroJB,"cell 1 0,spany 2,w :180, h 75!,align right");
		this.add(builderJB,"cell 0 0,w :200:, h 75!");
		this.add(createScriptJB,"cell 0 1,w :200:, h 75!");
		
	//	this.add(backToIntroJB,"dock south, h 40!,align center");

		this.validate();
		this.repaint();
	}
		
	private void setupIntroButtons() {
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
				doLoadMenu();
			}
		});
		//'''Create Buttons
		createScriptJB = new JButton("Use old script creator");
		createScriptJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				GenLab.getInstance().switchToPanel(GenLab.getInstance().scriptCreatorP);
			}
		});
		builderJB = new JButton("Use new script creator");
		builderJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				GenLab.getInstance().switchToPanel(GenLab.getInstance().builderP);
			}
		});
		//'''Load Buttons
		loadScriptJB = new JButton("Load from Script file");
		loadScriptJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				GenLab.getInstance().switchToPanel(GenLab.getInstance().scriptSetupP);
			}
		});
		loadJsonJB = new JButton("Load from JSON file");
		loadJsonJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Experiment newEx = ExperimentUtilities.loadJsonExperiment();
				if (newEx != null)
				{
					GenLab.getInstance().setExperiment(newEx);
					GenLab.getInstance().homeP.doHomePanel();
				}
			}
		});
		loadServerJB = new JButton("Load from the server");
		loadServerJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(GenLab.getInstance(),
											  "Sorry, this feature isn't availiable!",
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

	public void doExperimentView()
	{
		this.removeAll();
		MigLayout layout = new MigLayout("align center","push[align center]push","push[][][][]push");
		this.setLayout(layout);	
		JButton runButton = new JButton("Run");
		runButton.setIcon(new ImageIcon(this.getClass().getResource("/icons/forward_arrow_20.png")));		
		runButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				GenLab.getInstance().switchToPanel(GenLab.getInstance().runP);
			}
		});
		JTextArea experimentTA = new JTextArea();
		experimentTA.setText("Experiment:"+GenLab.getInstance().getExperiment().name + "\n\n");
		experimentTA.setEditable(false);
		experimentTA.setBackground(new Color(120,220,120));
		experimentTA.setBorder(BorderFactory.createEtchedBorder());
		optionsTextJL.setFont(optionsTextJL.getFont().deriveFont(13f));

		this.add(runButton,"align center,w 80!, h 30!,wrap"); 
		this.add(experimentTA,"align center,wrap,gapbottom unrelated");
		this.add(optionsTextJL,"gapbottom unrelated,wrap");
		
		JButton unloadJB = new JButton();
		unloadJB.setName("Unload this experiment to load or create a new one.");
		unloadJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/delete_20.png")));
		unloadJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				GenLab.getInstance().setExperiment(null);
				GenLab.getInstance().homeP.loadPanel();
			}
		});
		JButton editJB = new JButton();
		editJB.setName("Edit this experiment.");
		editJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/edit_20.png")));
		editJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				GenLab.getInstance().switchToPanel(GenLab.getInstance().builderP);
			}
		});
		JButton duplicateJB = new JButton();
		duplicateJB.setName("Create an offline, JSON copy of this experiment.");
		duplicateJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/save_as_20.png")));		
		duplicateJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){				
				boolean success = GenLab.getInstance().saveExperimentToJson();
				if (success)
					optionsTextJL.setText("Saved.");
				else
					optionsTextJL.setText("Failed to save a copy.");
			}
		});		
		JButton saveToNetJB = new JButton();
		saveToNetJB.setName("Publish this experiment to the internet.");
		saveToNetJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/up_20.png")));		
		saveToNetJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JOptionPane.showMessageDialog(GenLab.getInstance(),
						  "Not yet implemented.",
						  "", JOptionPane.ERROR_MESSAGE);
			}
		});	
		JButton viewResultsJB = new JButton();
		viewResultsJB.setName("View results of this experiment. Must be from server.");
		viewResultsJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/find_20.png")));
		viewResultsJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JOptionPane.showMessageDialog(GenLab.getInstance(),
						  "Not yet implemented.",
						  "", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		this.add(unloadJB,"w 50!, h 50!,split 5");
		this.add(editJB,"w 50!, h 50!");
		this.add(duplicateJB,"w 50!, h 50!");
		this.add(saveToNetJB,"w 50!, h 50!");
		this.add(viewResultsJB,"w 50!, h 50!");
		
		this.validate();
		this.repaint();
		
		MouseListener highlightListener = new MouseListener(){
			public void mouseClicked(MouseEvent arg0) {};
			public void mouseEntered(MouseEvent e) {
				JButton jb = (JButton)e.getSource();
				optionsTextJL.setText(jb.getName());
			}
			public void mouseExited(MouseEvent arg0) {
				optionsTextJL.setText("Scroll over an option.");
			};
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
		};
		unloadJB.addMouseListener(highlightListener);
		editJB.addMouseListener(highlightListener);
		duplicateJB.addMouseListener(highlightListener);
		saveToNetJB.addMouseListener(highlightListener);
		viewResultsJB.addMouseListener(highlightListener);

	}
	
}
