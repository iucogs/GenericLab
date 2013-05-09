package gui;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import core.GenLab;

import net.miginfocom.swing.MigLayout;
import experiment.*;

public class ExperimentBuilderPanel extends JPanel{

	private ToolsBox toolsBox = new ToolsBox();
	private ViewBox viewBox = new ViewBox();
	private DetailsBox detailsBox = new DetailsBox();
	
	public ExperimentBuilderPanel()
	{
		doBuildLayout();
		setupListeners();
	}
	
	private void doBuildLayout() {
		this.removeAll();
//		MigLayout layout = new MigLayout("align center,fill","push[fill]push","[][][]push");
//		this.setLayout(layout);
//		toolsBox.layoutComponents(this);
//		this.add(toolsBox,"cell 0 0, span, grow");
//		this.add(viewBox,"cell 0 1");
//		this.add(detailsBox,"cell 0 2");
		MigLayout layout = new MigLayout("fill","","[grow 0][]");
		this.setLayout(layout);
//		this.add(new JButton("Load"),"align left, gap unrelated");
//		this.add(new JButton("Save"),"align right, wrap");
		this.add(toolsBox,"gap 0! 0!,shrinky 200,span,grow,wrap");
		this.add(viewBox,"align left, split 2");
		this.add(detailsBox,"align center,growx");
		
	}
	
	private void setupListeners() {
		// TODO Auto-generated method stub
		
	}
	
	private class ToolsBox extends JPanel
	{
		
		private JButton newBlockJB,newTrialJB,newDisplayJB;
		private JButton loadOtherJB,saveAsJB;
		private JButton deleteJB,copyJB,pasteJB,unselectJB;
		
		public ToolsBox()
		{
			setupComponents();
			doNoSelectionLayout();
			this.setBorder(BorderFactory.createLineBorder(Color.black));
		}
		
		public void setupComponents()
		{

			String[] buttonText = new String[]{"+Block","+Trial","+Display",
											"Load Existing Experiment","Save As...",
											"Delete","Copy","Paste","Unselect"};
			newBlockJB = new JButton("+Block");	
			newTrialJB = new JButton("+Trial");
			newDisplayJB = new JButton("+Display");
			loadOtherJB = new JButton("Load Existing Experiment");
			saveAsJB = new JButton("Save As...");
			deleteJB = new JButton("Delete");
			copyJB = new JButton("Copy");
			pasteJB = new JButton("Paste");
			unselectJB = new JButton("Unselect");
			JButton[] buttons = new JButton[]{newBlockJB,newTrialJB,newDisplayJB,
					loadOtherJB,saveAsJB,
					deleteJB,copyJB,pasteJB,unselectJB};
			for (JButton b : buttons)
			{
				b.setFont(b.getFont().deriveFont(11.0f));
			}
		}
		
		public void doNoSelectionLayout()
		{
			this.removeAll();
			MigLayout layout = new MigLayout("gap 0! 0!,align left,fillx","[]","[]");
			this.setLayout(layout);
			this.add(newBlockJB,"gaptop 0!,split 3");
			this.add(newTrialJB,"");
			this.add(newDisplayJB,"");
			this.add(loadOtherJB,"align right,split 2");
			this.add(saveAsJB,"align right");
		}
		public void doSelectionLayout()
		{
			this.removeAll();
			MigLayout layout = new MigLayout("align left,fillx","[]","[][]");
			this.setLayout(layout);
			this.add(newBlockJB,",split 3");
			this.add(newTrialJB,"");
			this.add(newDisplayJB,"");
			this.add(loadOtherJB,"align right,split 2");
			this.add(saveAsJB,"align right, wrap");
			this.add(deleteJB,"split 4,span");
			this.add(copyJB,"");
			this.add(pasteJB,"");
			this.add(unselectJB,"");
		}
		
		
		
		
	}
	private class ViewBox extends JPanel implements TreeSelectionListener
	{
		public JTree tree;
		
		public ViewBox()
		{
			this.setBorder(BorderFactory.createLineBorder(Color.black));
			

			
			doDefaultLayout();
		}
		
		public void doDefaultLayout()
		{
			this.removeAll();
			MigLayout layout = new MigLayout("align center","[]","[]");
			this.setLayout(layout);
			
			DefaultMutableTreeNode top = new DefaultMutableTreeNode("An Experiment");
			DefaultMutableTreeNode block1 = new DefaultMutableTreeNode("Block1");
			DefaultMutableTreeNode block2 = new DefaultMutableTreeNode("Block2");
			top.add(block1);
			top.add(block2);
			
			DefaultMutableTreeNode trial1 = new DefaultMutableTreeNode("trial1");
			DefaultMutableTreeNode trial2 = new DefaultMutableTreeNode("trial2");
			DefaultMutableTreeNode trial3 = new DefaultMutableTreeNode("trial3");
			DefaultMutableTreeNode trial4 = new DefaultMutableTreeNode("trial4");
			block1.add(trial1);
			block1.add(trial2);
			block2.add(trial3);
			block2.add(trial4);

	        tree = new JTree(top);
	        tree.getSelectionModel().setSelectionMode
	                (TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION); 
	        //Add Transfer Handler
	        tree.setDragEnabled(true);
	        tree.setDropMode(DropMode.ON_OR_INSERT);
	        tree.addTreeSelectionListener(this);
	        JScrollPane treeJSP = new JScrollPane(tree);
	        
			this.add(treeJSP,"align center,span,grow");

		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
		     DefaultMutableTreeNode node = (DefaultMutableTreeNode)
             tree.getLastSelectedPathComponent();
		     if (node == null) return;
		     
		     Object item = node.getUserObject();
		     detailsBox.showItem(item);
		}
	}
	private class DetailsBox extends JPanel
	{
		public DetailsBox()
		{
			this.setBorder(BorderFactory.createLineBorder(Color.black));
			
			showDefault();
		}
		private void showDefault()
		{
			this.removeAll();
			MigLayout layout = new MigLayout("align center","[]","[]");
			this.setLayout(layout);
			this.add(new JLabel("No Experiment Loaded. \n" +
								"Please create a new experiment or load an existing one."));
		}
		
		private void showExperiment(Experiment ex)
		{
			MigLayout layout = new MigLayout("align center","[]","[]");
			this.setLayout(layout);
			this.add(new JTextArea("An experiment!\nDir:" + ex.directory),"");
		}
		private void showBlock(Block b) {
			// TODO Auto-generated method stub
			
		}
		private void showTrial(Trial t) {
			// TODO Auto-generated method stub
			
		}
		private void showDisplay(Display d) {
			// TODO Auto-generated method stub
			
		}
		/**
		 * Show an item from the ViewBox.
		 * Helper method to break items out by class.
		 * @param item
		 */
		public void showItem(Object item) {
		     if (item instanceof Experiment)
		     {
		    	 Experiment ex = (Experiment)item;
		    	 showExperiment(ex);
		     }
		     else if (item instanceof Block)
		     {
		    	 Block b = (Block)item;
		    	 showBlock(b);
		     }
		     else if (item instanceof Trial)
		     {
		    	 Trial t = (Trial)item;
		    	 showTrial(t);
		     }
		     else if (item instanceof Display)
		     {
		    	 Display d = (Display)item;
		    	 showDisplay(d);
		     }
		     else
		     {
		    	 System.err.println("INVALID TYPE SELECTED IN TREE:" + item.getClass());
		     }
		}


	}
	
}
