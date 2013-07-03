package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.GenLab;

import net.miginfocom.swing.MigLayout;

/**
 * This panel holds all other tier 1 generic lab panels:
 * 	HomePanel, the creator and configuration panels, and the run panel.
 * This panel is responsible for switching between them.
 * Replaces the tabbed pane found in earlier versions.  
 * @author Jay
 */
public class HolderPanel extends JPanel {
	
	private AbstractGenlabPanel currentPanel;
	
	private JPanel topBar;
	private JButton backToMainMenuJB;
	private JLabel topBarExperimentLabel;
	
	public HolderPanel()
	{
		backToMainMenuJB = new JButton("Back to Main Menu");
		backToMainMenuJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				GenLab.getInstance().holderP.showPanel(GenLab.getInstance().homeP);
			}
		});
		backToMainMenuJB.setOpaque(false);
		topBarExperimentLabel = new JLabel();
		topBarExperimentLabel.setFont(topBarExperimentLabel.getFont().deriveFont((13f)));
		updateLabel();
		topBar = new JPanel();
		topBar.setLayout(new MigLayout("","push[]push","push[]push"));
		topBar.add(backToMainMenuJB,"align left,dock west");
		topBar.add(topBarExperimentLabel,"id exLabel,pos (50%-((1/2)*exLabel.w)) (50%-((1/2)*exLabel.h))");
		topBar.setBackground(new Color(200,220,200));
	}
	


	public void showPanel(AbstractGenlabPanel panelToShow)
	{
		updateLabel();
		//---See if we're good to leave the old panel and go to the new one
		boolean okToLeave = currentPanel == null || currentPanel.leavePanel();
		if (!okToLeave)
		{
			return;
		}
		boolean okToLoad = panelToShow.loadPanel();
		if (!okToLoad)
		{
			return;
		}
		//---Update vars and prep layout
		currentPanel = panelToShow;
		this.removeAll();
		MigLayout layout = new MigLayout("align center,fill","[]","[]");
		this.setLayout(layout);	
		//---Show top bar if we're not on the Home Panel
		if (!(panelToShow instanceof HomePanel))
		{
			this.add(topBar,"growx,h " + backToMainMenuJB.getPreferredSize().height + "!,dock north");
		}
		//---Show the current panel
		JPanel currentPanelFrame = new JPanel();
		currentPanelFrame.setLayout(new GridLayout());	
		currentPanelFrame.add(panelToShow);
		this.add(currentPanelFrame,"growx,growy");
		
		this.validate();
		this.repaint();
	}
	
	public void updateLabel() {
		if (GenLab.getInstance().getExperiment() == null)
		{
			topBarExperimentLabel.setForeground(Color.red);
			topBarExperimentLabel.setText("No Experiment active. Load or create one to use.");
		}
		else
		{
			topBarExperimentLabel.setForeground(Color.green.darker().darker());
			topBarExperimentLabel.setText("Current Experiment: " + GenLab.getInstance().getExperiment().name);
		}
	}
}


