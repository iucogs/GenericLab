package experiment_builder;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import core.GenLab;

public class ToolsBox extends JPanel
{
	
	private JButton newBlockJB,newTrialJB,newDisplayJB;
	private JButton openJB,saveAsJB,saveJB,quickRunJB,launchJB;
	private JButton deleteJB,copyJB,pasteJB;
	
	public ToolsBox(boolean hasBeenSaved)
	{
		setupComponents();
		doDefaultLayout(hasBeenSaved);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}
	
	public void setupComponents()
	{
		newBlockJB = new JButton("+Block");	
		newBlockJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/new_block_32.png")));
		newTrialJB = new JButton("+Trial");
		newTrialJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/new_trial_32.png")));
		newDisplayJB = new JButton("+Display");
		newDisplayJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/new_display_32.png")));
		openJB = new JButton("Open");
		openJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/find_20.png")));		
		saveAsJB = new JButton("Save...");
		saveAsJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/save_as_20.png")));		
		saveJB = new JButton("Save");
		saveJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/edit_20.png")));	
		launchJB = new JButton("Use Experiment");
		launchJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/up_20.png")));		
		quickRunJB = new JButton("Quick Run");
		quickRunJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/forward_arrow_20.png")));		
		deleteJB = new JButton("Delete");
		deleteJB.setIcon(new ImageIcon(this.getClass().getResource("/icons/delete_20.png")));
		copyJB = new JButton("Copy");
		copyJB.setEnabled(false);//TODO: implement CnP- use single object serialization via Jackson in ExperimentUtilities.
		pasteJB = new JButton("Paste");
		pasteJB.setEnabled(false);//TODO: implement CnP- use single object serialization via Jackson in ExperimentUtilities.
		JButton[] buttons = new JButton[]{newBlockJB,newTrialJB,newDisplayJB,
				openJB,saveJB,saveAsJB,launchJB,quickRunJB,
				deleteJB,copyJB,pasteJB};
		for (JButton b : buttons)
		{
			b.setFont(b.getFont().deriveFont(11.0f));
		}
		//Listeners
		newBlockJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				GenLab.getInstance().builderP.newItem(new experiment.Block());
			}
		});
		newTrialJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				GenLab.getInstance().builderP.newItem(new experiment.Trial());
			}
		});
		newDisplayJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				GenLab.getInstance().builderP.newItem(new experiment.Display());
			}
		});
		deleteJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				GenLab.getInstance().builderP.removeItem();
			}
		});	
		copyJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JOptionPane.showMessageDialog(GenLab.getInstance(),
						  "Not yet implemented.",
						  "", JOptionPane.ERROR_MESSAGE);
			}
		});	
		pasteJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JOptionPane.showMessageDialog(GenLab.getInstance(),
						  "Not yet implemented.",
						  "", JOptionPane.ERROR_MESSAGE);
			}
		});	
		openJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				boolean success = GenLab.getInstance().setupExperimentFromJson();
				if (success)
				{
					GenLab.getInstance().builderP.doBuildLayout();
				}
			}
		});
		saveAsJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){				
				boolean success = GenLab.getInstance().saveExperimentToJson();
				if (success)
				{
					GenLab.getInstance().builderP.setHasBeenSaved(true);
					//saveJB.setVisible(true);
				}
			}
		});			
		saveJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JOptionPane.showMessageDialog(GenLab.getInstance(),
						  "Not yet implemented.",
						  "", JOptionPane.ERROR_MESSAGE);
			}
		});	
		quickRunJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//TODO: Update to do soft save if builderP.hasBeenSaved.
				boolean success = GenLab.getInstance().saveExperimentToJson();
				if (!success) return;
				success = GenLab.getInstance().setupExperimentFromJson();
				if (!success) return;
				GenLab.getInstance().switchToPanel(GenLab.getInstance().runP);
			}
		});	
		launchJB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JOptionPane.showMessageDialog(GenLab.getInstance(),
						  "Not yet implemented.",
						  "", JOptionPane.ERROR_MESSAGE);
			}
		});	
	}

	public void doDefaultLayout(boolean hasBeenSaved)
	{
		this.removeAll();
		MigLayout layout = new MigLayout("align left,fillx","[]","[][]");
		this.setLayout(layout);
		this.add(newBlockJB,",split 3");
		this.add(newTrialJB,"");
		this.add(newDisplayJB,"");
		this.add(saveJB,"align right, split 3");
		this.add(saveAsJB,"align right");
		this.add(openJB,"align right,wrap");
		this.add(deleteJB,"split 3");
		this.add(copyJB,"");
		this.add(pasteJB,"");
		this.add(quickRunJB,"align right,split 2");
		this.add(launchJB,"align right");
		//saveJB.setVisible(hasBeenSaved);
	}
}