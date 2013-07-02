package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
	
	
	public void showPanel(JPanel panelToShow)
	{
		this.removeAll();
		MigLayout layout = new MigLayout("align center,fill","[]","[]");
		this.setLayout(layout);	
		
		//Show top bar if we're not on the Home Panel
		if (!(panelToShow instanceof HomePanel))
		{
			JButton backToMainMenuJB = new JButton("Back to Main Menu");
			backToMainMenuJB.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					GenLab.getInstance().holderP.showPanel(GenLab.getInstance().homeP);
				}
			});
			JPanel topMenuBar = new JPanel();
			topMenuBar.setLayout(new MigLayout("align left, gap 0, gap 0"));
			topMenuBar.add(backToMainMenuJB,"north,west");
			topMenuBar.setBackground(new Color(200,220,200));
			this.add(topMenuBar,"growx,h " + backToMainMenuJB.getPreferredSize().height + "!,dock north");
		}
		
		//Show the current panel
		JPanel currentPanelFrame = new JPanel();
		currentPanelFrame.setLayout(new GridLayout());	
		currentPanelFrame.add(panelToShow);
		this.add(currentPanelFrame,"growx,growy");
		
		this.validate();
		this.repaint();
	}
	
}


