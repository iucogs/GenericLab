package experiment_builder;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.miginfocom.swing.MigLayout;
import core.GenLab;
import experiment.Block;
import experiment.Display;
import experiment.Experiment;
import experiment.Trial;

public class ViewBox extends JPanel implements TreeSelectionListener
{
	public JTree tree;
	
	public ViewBox()
	{
		this.setBorder(BorderFactory.createLineBorder(Color.black));
//		doDefaultLayout();
	}
	
	public void doDefaultLayout()
	{
		this.removeAll();
		MigLayout layout = new MigLayout("align center","[]","[]");
		this.setLayout(layout);
		
        tree = new JTree(new DefaultTreeModel(initializeTreeNodes()));
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION); 
		tree.setExpandsSelectedPaths(true);
        tree.setCellRenderer(new CustomTreeCellRenderer());
        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        tree.setTransferHandler(new TransferHandler(){ //TODO: Finish quasi drag-n-drop
	        public boolean canImport(TransferHandler.TransferSupport support) {
	        	    if (!support.isDataFlavorSupported(DataFlavor.stringFlavor) ||
	        	            !support.isDrop()) {
	        	        return false;
	        	    }
	        	    JTree.DropLocation dropLocation =
	        	            (JTree.DropLocation)support.getDropLocation();
	        	    return dropLocation.getPath() != null;
	        }
	        public boolean importData(TransferSupport supp) {
	            if (!canImport(supp)) {
	                return false;
	            }

	            // Fetch the Transferable and its data
	            Transferable t = supp.getTransferable();
	            try {
					String data = (String) t.getTransferData(DataFlavor.stringFlavor);
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	            // Fetch the drop location
	            DropLocation loc = supp.getDropLocation();

	            // Insert the data at this location
	            //insertAt(loc, data);

	            return true;
	        }
	    });
        
        tree.addTreeSelectionListener(this);
        JScrollPane treeJSP = new JScrollPane(tree);
        
		this.add(treeJSP,"align center,span,grow");
        tree.setSelectionPath(new TreePath(((DefaultMutableTreeNode)tree.getModel().getRoot()).getPath()));

	}
	
	public void removeItem()
	{
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		Object nodeData = selectedNode.getUserObject();
		if (nodeData instanceof Experiment)
		{
			JOptionPane.showMessageDialog(GenLab.getInstance(),
					  "Cannot delete the entire experiment.  Load or create a new one instead.",
					  "", JOptionPane.ERROR_MESSAGE);
		}
		else if (nodeData instanceof Block)
		{
			Block b = (Block)nodeData;
			int response = JOptionPane.showConfirmDialog(GenLab.getInstance(), "Are you sure you want to delete this" +
												" block and all its trials?\n This cannot be undone.",
												"Delete " + b + "?", JOptionPane.WARNING_MESSAGE);
			if (response == JOptionPane.YES_OPTION)
			{
				Experiment experiment = (Experiment)((DefaultMutableTreeNode)selectedNode.getParent()).getUserObject();
				experiment.blocks.remove(nodeData);
				model.removeNodeFromParent(selectedNode);
			}
		}
		else if (nodeData instanceof Trial)
		{
			Block block = (Block)((DefaultMutableTreeNode)selectedNode.getParent()).getUserObject();
			block.trials.remove(nodeData);
			model.removeNodeFromParent(selectedNode);
		}
		else if (nodeData instanceof Display)
		{
			Trial trial = (Trial)((DefaultMutableTreeNode)selectedNode.getParent()).getUserObject();
			trial.displays.remove(nodeData);
			model.removeNodeFromParent(selectedNode);
		}
	}
	
	public void newItem(Object item)
	{
		if (item instanceof Block)
		{
			insertBlock((Block)item);
			return;
		}
		else if (item instanceof Trial)
		{
			insertTrial((Trial)item);
			return;
		}
		else if (item instanceof Display)
		{
			insertDisplay((Display)item);
		}
		else
		{
			System.err.println("Error! ViewBox's New Item Object invalid type. new item:" + item);
			return;
		}
	}

	private void insertDisplay(Display d)
	{
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(d);
		Object selectedObject = selected.getUserObject();
		if (selectedObject instanceof Trial)//Add as last child
		{
			DefaultMutableTreeNode parent = selected;
			Trial trial = (Trial)selected.getUserObject();
			model.insertNodeInto(newNode, parent, parent.getChildCount());
			trial.displays.add(d);
		}
		else if (selectedObject instanceof Display)//add as sibling of Display
		{
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode)selected.getParent();
			Trial trial = (Trial)parent.getUserObject();
			int index = parent.getIndex(selected);
			trial.displays.add(index,d); //our data structure
			model.insertNodeInto(newNode, parent, index+1);
		}
		else if (selectedObject instanceof Block)
		{
			Block block = (Block)selected.getUserObject();
			//Empty?  Insert new Trial if so
			if (block.trials == null || block.trials.size() == 0)
			{
				insertTrial(new Trial());
				insertDisplay(d); //Will attach to the newly selected new trial
				return;
			}
			else
			{
				//Any expanded? use first if so
				for (int i = 0; i < selected.getChildCount(); i++)
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)selected.getChildAt(i);
					TreePath expandedTrialPath = new TreePath(node.getPath());
					if (tree.isExpanded(expandedTrialPath))
					{
						tree.setSelectionPath(expandedTrialPath);
						insertDisplay(d);
						return;
					}
				}
				//None expanded, so use last
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)selected.getLastChild();
				TreePath lastTrialPath = new TreePath(node.getPath());
				tree.setSelectionPath(lastTrialPath);
				insertDisplay(d);
				return;
			}
		}
		else if (selectedObject instanceof Experiment)//Find most suitable Block
		{
			Experiment e = (Experiment)selected.getUserObject();
			//Empty?  Insert new Block if so
			if (e.blocks == null || e.blocks.size() == 0)
			{
				insertBlock(new Block());
				insertDisplay(d); //Will attach to the newly selected new block
				return;
			}
			else
			{
				//Any expanded? use first if so
				for (int i = 0; i < selected.getChildCount(); i++)
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)selected.getChildAt(i);
					TreePath expandedBlockPath = new TreePath(node.getPath());
					if (tree.isExpanded(expandedBlockPath))
					{
						tree.setSelectionPath(expandedBlockPath);
						insertDisplay(d);
						return;
					}
				}
				//None expanded, so use last
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)selected.getLastChild();
				TreePath lastBlockPath = new TreePath(node.getPath());
				tree.setSelectionPath(lastBlockPath);
				insertDisplay(d);
				return;
			}
		}
		else
		{
			System.err.println("Error!! Selected Object invalid type. Obj:" + selectedObject);
			return;
		}
		tree.setSelectionPath(new TreePath(newNode.getPath()));
	}
	
	private void insertTrial(Trial t)
	{
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(t);
		Object selectedObject = selected.getUserObject();
		if (selectedObject instanceof Block)//Add as last child
		{
			DefaultMutableTreeNode parent = selected;
			Block block = (Block)selected.getUserObject();
			model.insertNodeInto(newNode, parent, parent.getChildCount());
			block.trials.add(t);
		}
		else if (selectedObject instanceof Trial || 
				 	selectedObject instanceof Display)//add as sibling of Trial (or parent trial)
		{
			if (selectedObject instanceof Display)
			{
				selected = (DefaultMutableTreeNode) selected.getParent();
			}
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode)selected.getParent();
			Block block = (Block)parent.getUserObject();
			int index = parent.getIndex(selected);
			block.trials.add(index,t); //our data structure
			model.insertNodeInto(newNode, parent, index+1);
		}
		else if (selectedObject instanceof Experiment)//Find most suitable Block
		{
			Experiment e = (Experiment)selected.getUserObject();
			//Empty?  Insert new Block if so
			if (e.blocks == null || e.blocks.size() == 0)
			{
				insertBlock(new Block());
				insertTrial(t); //Will attach to the newly selected new block
				return;
			}
			else
			{
				//Any expanded? use first if so
				for (int i = 0; i < selected.getChildCount(); i++)
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)selected.getChildAt(i);
					TreePath expandedBlockPath = new TreePath(node.getPath());
					if (tree.isExpanded(expandedBlockPath))
					{
						tree.setSelectionPath(expandedBlockPath);
						insertTrial(t);
						return;
					}
				}
				//None expanded, so use last
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)selected.getLastChild();
				TreePath lastBlockPath = new TreePath(node.getPath());
				tree.setSelectionPath(lastBlockPath);
				insertTrial(t);
				return;
			}
		}
		else
		{
			System.err.println("Error!! Selected Object invalid type. Obj:" + selectedObject);
			return;
		}
		tree.setSelectionPath(new TreePath(newNode.getPath()));
	}
	
	private void insertBlock(Block b)
	{
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(b);
		Object selectedObject = selected.getUserObject();
		if (selected.getUserObject() instanceof Experiment)//Add as last child
		{
			DefaultMutableTreeNode parent = selected;
			Experiment ex = (Experiment)selected.getUserObject();
			model.insertNodeInto(newNode, parent, parent.getChildCount());
			ex.blocks.add(b);
		}
		else if (selectedObject instanceof Block ||
					selectedObject instanceof Trial ||
					selectedObject instanceof Display)//Insert sister block after current block
		{
			while(!(selected.getUserObject() instanceof Block))
			{
				selected = (DefaultMutableTreeNode) selected.getParent();
				if (selected == null)
				{
					System.err.println("Invalid insertion point."); //TODO: improve this error msg? its no biggie
					return;	
				}
			}
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode)selected.getParent();
			Experiment ex = (Experiment)parent.getUserObject();
			int index = parent.getIndex(selected);
			ex.blocks.add(index,b); //our data structure
			model.insertNodeInto(newNode, parent, index+1);
		}
		else
		{
			System.err.println("Error!! Selected Object invalid type. Obj:" + selectedObject);
			return;
		}
		tree.setSelectionPath(new TreePath(newNode.getPath()));
	}
	
	
	/**
	 * Generates a Node tree representing the loaded Experiment
	 * for rendering in the ViewBox.
	 * @return
	 */
	private DefaultMutableTreeNode initializeTreeNodes()
	{
		Experiment ex = GenLab.getInstance().builderP.builderExperiment;

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(ex);
		for (Block b : ex.blocks)
		{
			DefaultMutableTreeNode blockNode = new DefaultMutableTreeNode(b);
			for (Trial t : b.trials)
			{
				DefaultMutableTreeNode trialNode = new DefaultMutableTreeNode(t);
				for (Display d : t.displays)
				{
					DefaultMutableTreeNode displayNode = new DefaultMutableTreeNode(d);
					trialNode.add(displayNode);
				}
				blockNode.add(trialNode);
			}
			root.add(blockNode);
		}
		return root;
	}
	
	private class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

		  public CustomTreeCellRenderer() {
		  }

		@Override
		public Component getTreeCellRendererComponent(JTree tree,
				Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			if (value != null)
			{
				try {
					Object userObj = ((DefaultMutableTreeNode)value).getUserObject();
					ImageIcon icon = new ImageIcon(ImageIO.read(getClass().getResource(
							"/icons/" + userObj.getClass().getSimpleName().toLowerCase() + "_20.png")));
					setOpenIcon(icon);
					setClosedIcon(icon);
					setLeafIcon(icon);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(leaf)
			{
				this.backgroundSelectionColor = this.backgroundNonSelectionColor;
				this.textSelectionColor = this.textNonSelectionColor;
			}

			return super.getTreeCellRendererComponent(tree, value, leaf,
					expanded, leaf, row, hasFocus);
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
	     DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
	     if (node == null) return;
	     Object item = node.getUserObject();
	     GenLab.getInstance().builderP.showDetails(item);
	}
}
