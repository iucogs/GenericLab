package gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import core.GenLab;

import net.miginfocom.swing.MigLayout;
import experiment.*;
import experiment_builder.DetailsBox;
import experiment_builder.ToolsBox;
import experiment_builder.ViewBox;

public class ExperimentBuilderPanel extends AbstractGenlabPanel{

	private boolean hasBeenSaved = false;

	public Experiment builderExperiment = null;
	
	public ToolsBox toolsBox = new ToolsBox(hasBeenSaved);
	public ViewBox viewBox = new ViewBox();
	public DetailsBox detailsBox = new DetailsBox();
	
	@Override
	public boolean loadPanel() {
		setupPanel();
		return true;
	}

	@Override
	public boolean leavePanel() {
		// TODO ADD DIALOG: If it hasn't been saved, are you sure you want to leave?
		return true;
	}
	
	/**
	 * Sets up the panel for another use.  Resets the hasBeenSaved variable.
	 */
	public void setupPanel() {
		if (GenLab.getInstance().experiment == null)
			builderExperiment = new Experiment();
		Experiment ex = GenLab.getInstance().experiment;
		hasBeenSaved = false;
		this.removeAll();
		MigLayout layout = new MigLayout("fill","","[grow 0][]");
		this.setLayout(layout);
		toolsBox = new ToolsBox(hasBeenSaved);
		viewBox = new ViewBox();
		detailsBox = new DetailsBox();
		this.add(toolsBox,"gap 0! 0!,shrinky 200,span,grow,wrap");
		this.add(viewBox,"align left, split 2");
		this.add(detailsBox,"align right");
		viewBox.doDefaultLayout();
		this.validate();
		this.repaint();
	}
	
	public void removeItem()
	{
		viewBox.removeItem();
	}
	
	public void newItem(Object clazz)
	{
		viewBox.newItem(clazz);
	}
	
	public void showDetails(Object item)
	{
		detailsBox.showItem(item);
	}

	public boolean hasBeenSaved()
	{
		return hasBeenSaved;
	}
	
	public void setHasBeenSaved(boolean b) {
		hasBeenSaved = true;
	}	
}
	
