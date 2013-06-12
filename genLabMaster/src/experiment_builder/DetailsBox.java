package experiment_builder;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import core.GenLab;

import net.miginfocom.swing.MigLayout;
import experiment.Block;
import experiment.Display;
import experiment.Display.DisplayType;
import experiment.Display.PositionType;
import experiment.Experiment;
import experiment.Trial;

public class DetailsBox extends JPanel
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
		this.add(new JLabel("Welcome to experiment builder, the new way to make experiments."));
		this.repaint();
	}
	
	private static DocumentListener resizeDocumentListener = new DocumentListener(){
		public void changedUpdate(DocumentEvent arg0) {
			refreshBuilderP();
		}
		public void insertUpdate(DocumentEvent arg0) {
			refreshBuilderP();
		}
		public void removeUpdate(DocumentEvent arg0) {
			refreshBuilderP();
		}
		private void refreshBuilderP(){
			GenLab.getInstance().builderP.validate();
			GenLab.getInstance().builderP.repaint();
		}
	};
	
	
	private static class ExperimentModel //C
	{
		static JFormattedTextField name,promptString,
							trialTypes,usableKeys; //comma de-liniated for now		
		static JCheckBox randomizeBlockOrder,giveFeedback,
							includeAllNumbers,includeAllLetters;
		static JLabel nameL,promptStringL,trialTypesL,usableKeysL,
						randomizeBlockOrderL,giveFeedbackL,includeAllNumbersL,includeAllLettersL;
		static Experiment ex;
				
		private static void setupExperimentView(Experiment exToRegister)
		{
			ex = exToRegister;
			setupLabels();
			///Text Fields
			name = new JFormattedTextField(ex.name);
			name.addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent arg0) {
					String val = (String)name.getValue();
					if (val != null && val != "")
						ex.name = val;
				}});
			name.getDocument().addDocumentListener(resizeDocumentListener);
			promptString = new JFormattedTextField(ex.promptString);
			promptString.addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent arg0) {
					String val = (String)promptString.getValue();
					if (val != null)
						ex.promptString = val;
				}});
			promptString.getDocument().addDocumentListener(new DocumentListener()
			{
				public void changedUpdate(DocumentEvent arg0) {update();}
				public void insertUpdate(DocumentEvent arg0) {update();}
				public void removeUpdate(DocumentEvent arg0) {update();}
				private void update()
				{
					GenLab.getInstance().builderP.validate();
					GenLab.getInstance().builderP.repaint();

				}});
			trialTypes = new JFormattedTextField(listToCommaDeliniated(ex.trialTypes));
			trialTypes.addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent arg0) {
					String val = (String)trialTypes.getValue();
					if (val != null)
						ex.trialTypes =  commaDeliniatedToList(val);
				}});
			trialTypes.getDocument().addDocumentListener(resizeDocumentListener);
			usableKeys = new JFormattedTextField(listToCommaDeliniated(ex.usableKeys));
			usableKeys.addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent arg0) {
					String val = (String)usableKeys.getValue();
					if (val != null)
						ex.usableKeys =  commaDeliniatedToList(val);					
				}});
			usableKeys.getDocument().addDocumentListener(resizeDocumentListener);
			///Check Boxes
			randomizeBlockOrder = new JCheckBox();
			if (ex.randomizeBlockOrder == null)
				ex.randomizeBlockOrder = false;
			randomizeBlockOrder.setSelected(ex.randomizeBlockOrder);
			randomizeBlockOrder.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					ex.randomizeBlockOrder = randomizeBlockOrder.isSelected();
				}});
			giveFeedback = new JCheckBox();
			giveFeedback.setSelected(ex.giveFeedback);
			giveFeedback.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					ex.giveFeedback = giveFeedback.isSelected();
				}});
			includeAllLetters = new JCheckBox();
			includeAllLetters.setSelected(ex.includeAllLetters);
			includeAllLetters.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					ex.includeAllLetters = includeAllLetters.isSelected();
				}});
			includeAllNumbers = new JCheckBox();
			includeAllNumbers.setSelected(ex.includeAllNumbers);
			includeAllNumbers.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					ex.includeAllNumbers = includeAllNumbers.isSelected();
				}});
		}
		
		private static void setupLabels() {
			nameL = new JLabel("Experiment Name:");
			nameL.setToolTipText("The title of this experiment; be descriptive and consice.");
			promptStringL = new JLabel("Prompt:");
			promptStringL.setToolTipText("This prompt is shown below the experiment window" +
											" throughout the experiment.");
			trialTypesL = new JLabel("Trial Types:");
			trialTypesL.setToolTipText("Enter category names for trials, to be used to" + 
											" catagorize results.  Comma separated.");
			usableKeysL = new JLabel("Usable Keys:");
			usableKeysL.setToolTipText("Enter the keys subjects may press during the experiment." + 
											" Keys listed will register as correct or incorrect responses "+
											" wheras keys not listed will be ignored.  Comma separated");
			randomizeBlockOrderL = new JLabel("Randomize Block Order:");
			randomizeBlockOrderL.setToolTipText("Randomize the order of all reptitions of all blocks.");
			giveFeedbackL = new JLabel("Give Feedback:");
			giveFeedbackL.setToolTipText("Show 'Correct' or 'Incorrect' after each response.");
			includeAllNumbersL = new JLabel("Use All Numbers:");
			includeAllNumbersL.setToolTipText("Include all numbers as usable keys.");
			includeAllLettersL = new JLabel("Use All Letters:");
			includeAllLettersL.setToolTipText("Include all letters as usable keys.");
		}
		
		private static String listToCommaDeliniated(List<String> stringList)
		{
			if (stringList == null)
				return "";
			String output = "";
			Iterator<String> iter = stringList.iterator();
			while(iter.hasNext()){
				output += iter.next();
				if (iter.hasNext())
					output += ",";
			}
			return output;
		}
		private static List<String> commaDeliniatedToList(String commaString)
		{
			LinkedList<String> output = new LinkedList<String>();
			Collections.addAll(output,commaString.split(","));
			return output;
		}
	}
	
	private static class BlockModel
	{
		static JFormattedTextField name;
		static JSpinner reps, delayBetweenTrials;
		static JCheckBox randomizeTrialOrder,leaveDisplaysOn;
		static JButton font;
		static JLabel nameL,
					repsL,delayBetweenTrialsL,
					randomizeTrialOrderL,leaveDisplaysOnL,
					fontL;
		static Block b;
		
		static void setupBlockView(Block blockToRegister)
		{
			b = blockToRegister;
			setupLabels();
			name = new JFormattedTextField(b.name);
			name.addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent arg0) {
					String val = (String)name.getValue();
					if (val != null && val != "")
						b.name = val;
				}});
			name.getDocument().addDocumentListener(resizeDocumentListener);
			reps = new JSpinner();
			reps.setModel(new SpinnerNumberModel(1,1,Integer.MAX_VALUE,1));
			reps.setValue(b.reps);
			reps.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					b.reps = (Integer)reps.getValue();
				}
			});
			delayBetweenTrials = new JSpinner();
			delayBetweenTrials.setModel(new SpinnerNumberModel(0,0,Integer.MAX_VALUE,100));
			delayBetweenTrials.setValue(b.delayBetweenTrials);
			delayBetweenTrials.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					b.delayBetweenTrials = (Integer)delayBetweenTrials.getValue();
				}
			});
			randomizeTrialOrder = new JCheckBox();
			randomizeTrialOrder.setSelected(b.randomizeTrialOrder);
			randomizeTrialOrder.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					b.randomizeTrialOrder = randomizeTrialOrder.isSelected();
				}
			});
			leaveDisplaysOn = new JCheckBox();
			leaveDisplaysOn.setSelected(b.leaveDisplaysOn);
			leaveDisplaysOn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					b.leaveDisplaysOn = leaveDisplaysOn.isSelected();
				}
			});
			if (b.font == null)
				b.font = new JLabel().getFont();
			font = new JButton(b.font.getFontName());
			font.setFont(b.font);
			font.addActionListener(new AbstractAction(){
				public void actionPerformed(ActionEvent arg0) {
					JFontChooser fontChooser = new JFontChooser();
					int result = fontChooser.showDialog(GenLab.getInstance());
					if (result == JFontChooser.OK_OPTION)
					{
						b.font = fontChooser.getFont();
						font.setFont(b.font);
						font.setText(b.font.toString());
					}
				}
			});

		}

		private static void setupLabels() {
			nameL = new JLabel("Block Name:");
			nameL.setToolTipText("The name of this block.  Ex: 'Training Block'.");
			repsL = new JLabel("Number of Reptitions:");
			repsL.setToolTipText("The number of times to run all the trials in this block.");
			delayBetweenTrialsL = new JLabel("Delay Between Trials(ms):");
			delayBetweenTrialsL.setToolTipText("Time in milliseconds to wait before displaying the"+
													" next trial after a response is registered.");
			randomizeTrialOrderL = new JLabel("Randomize Trial Order:");
			randomizeTrialOrderL.setToolTipText("Randomize the order of trials in this block.");
			leaveDisplaysOnL = new JLabel("Leave Displays On:");
			leaveDisplaysOnL.setToolTipText("Within each trial in this block, leave each display painted"+
												" until a response is received. Can be overriden on a per-trial basis.");
			fontL = new JLabel("Font:");
			fontL.setToolTipText("The default font of text for trials in this Block."+
									" Can be overriden on a per-trial basis.");
		}
		
	}
	
	private static class TrialModel
	{
		static JFormattedTextField name, trialType, correctKey; //TODO: Change trialType to a combo box.
		static JCheckBox randomizeDisplayOrder;
		static JLabel nameL,trialTypeL,correctKeyL,
						randomizeDisplayOrderL;
		static Trial t;
		
		static void setupTrialView(Trial trialToSetup)
		{
			t = trialToSetup;
			setupLabels();
			name = new JFormattedTextField(t.name);
			name.addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent arg0) {
					String val = (String)name.getValue();
					if (val != null)
						t.name = val;
				}});
			name.getDocument().addDocumentListener(resizeDocumentListener);
			trialType = new JFormattedTextField(t.trialType);
			trialType.addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent arg0) {
					String val = (String)trialType.getValue();
					if (val != null && val != "")
						t.trialType = val;
				}});
			trialType.getDocument().addDocumentListener(resizeDocumentListener);
			correctKey = new JFormattedTextField(t.correctKey);
			correctKey.addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent arg0) {
					String val = (String)correctKey.getValue();
					if (val != null && val != "")
						t.correctKey = val;
				}});
			correctKey.getDocument().addDocumentListener(resizeDocumentListener);
			randomizeDisplayOrder = new JCheckBox();
			randomizeDisplayOrder.setSelected(t.randomizeDisplayOrder);
			randomizeDisplayOrder.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					t.randomizeDisplayOrder = randomizeDisplayOrder.isSelected();
				}
			});
			
		}

		private static void setupLabels() {
			nameL = new JLabel("Trial Name:");
			nameL.setToolTipText("Name of this trial (internal use only).");
			trialTypeL = new JLabel("Trial Type:");
			trialTypeL.setToolTipText("The category of this trial. Must match one of this Experiment's trial types.");
			correctKeyL = new JLabel("Correct Key:");
			correctKeyL.setToolTipText("The correct response key.");
			randomizeDisplayOrderL = new JLabel("Randomize Display Order:");
			randomizeDisplayOrderL.setToolTipText("Randomize the order of this trial's displays.");
		}
	}

	private static class DisplayModel{
		static JComboBox displayType,positionType;
		static JFormattedTextField textOrPath;
		static JSpinner positionX,positionY,randomOffsetX,randomOffsetY,
							durationSecs,persistTime;
		static JLabel displayTypeL,positionTypeL,
						textOrPathL,
						positionXL,positionYL,randomOffsetXL,randomOffsetYL,
						durationSecsL,persistTimeL;
		
		static Display d;
		
		static void setupDisplayView(Display displayToSetup){
			d = displayToSetup;
			displayType = new JComboBox(Display.DisplayType.values());
			if (d.getDisplayType() != null)
				displayType.setSelectedItem(d.getDisplayType());
			displayType.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					DisplayType dt = (DisplayType)displayType.getSelectedItem();
					d.setDisplayType(dt);
					boolean hasPosition = !dt.equals(DisplayType.SOUND);
					positionType.setEnabled(hasPosition);
					positionX.setEnabled(hasPosition);
					positionY.setEnabled(hasPosition);
				}
			});
			positionType = new JComboBox(Display.PositionType.values());
			if (d.getPositionType() != null)
				positionType.setSelectedItem(d.getPositionType());
			positionType.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					PositionType pt = (PositionType)positionType.getSelectedItem();
					d.setPositionType(pt);
					boolean exactLocation = pt.equals(PositionType.EXACT);
					positionX.setEnabled(exactLocation);
					positionY.setEnabled(exactLocation);
				}
			});
			textOrPath = new JFormattedTextField(d.getTextOrPath());
			textOrPath.addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent arg0) {
					String val = (String)textOrPath.getText();
					System.out.println("hi");

					if (val != null && val != "")
					{
						d.setTextOrPath(val);
						System.out.println("wAMAMBAN: val:" + val);
					}
				}});
			textOrPath.getDocument().addDocumentListener(resizeDocumentListener);
			
			positionX = new JSpinner();
			positionX.setModel(new SpinnerNumberModel(0,0,Integer.MAX_VALUE,10));
			if (d.getPosition() != null)
			positionX.setValue(d.getPosition().x);
			positionX.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					d.setPosition(new Point((Integer)positionX.getValue(),d.getPosition().y));
				}
			});
			positionY = new JSpinner();
			positionY.setModel(new SpinnerNumberModel(0,0,Integer.MAX_VALUE,10));
			if (d.getPosition() != null)
				positionY.setValue(d.getPosition().y);
			positionY.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					d.setPosition(new Point(d.getPosition().x,(Integer)positionY.getValue()));
				}
			});
			randomOffsetX = new JSpinner();
			randomOffsetX.setModel(new SpinnerNumberModel(0,0,Integer.MAX_VALUE,10));
			if (d.getRandomOffset() != null)
				randomOffsetX.setValue(d.getRandomOffset().width);
			randomOffsetX.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					d.setRandomOffset((Integer)randomOffsetX.getValue(),d.getRandomOffset().height);
				}
			});
			randomOffsetY = new JSpinner();
			randomOffsetY.setModel(new SpinnerNumberModel(0,0,Integer.MAX_VALUE,10));
			if (d.getRandomOffset() != null)
				randomOffsetY.setValue(d.getRandomOffset().height);
			randomOffsetY.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					d.setRandomOffset(d.getRandomOffset().width,(Integer)randomOffsetY.getValue());
				}
			});
			durationSecs = new JSpinner();
			durationSecs.setModel(new SpinnerNumberModel(1000,1,Integer.MAX_VALUE,100));
			durationSecs.setValue(d.getDurationSecs() * 1000);
			durationSecs.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					d.setDurationSecs(((Double)durationSecs.getValue()) / 1000);
				}
			});
			persistTime = new JSpinner();
			persistTime.setModel(new SpinnerNumberModel(0,0,Integer.MAX_VALUE,100));
			persistTime.setValue(d.getPersistTime() * 1000);
			persistTime.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					d.setPersistTime((Double)persistTime.getValue() / 1000);
				}
			});
			
			setupLabels();
		}
		private static void setupLabels(){
			displayTypeL = new JLabel("Display Type:");
			displayTypeL.setToolTipText("");
			positionTypeL = new JLabel("Positioning:");
			positionTypeL.setToolTipText("");
			textOrPathL = new JLabel("Text or Path");
			textOrPathL.setToolTipText("");
			positionXL = new JLabel("Position X:");
			positionXL.setToolTipText("");
			positionYL = new JLabel("Y:");
			positionYL.setToolTipText("");
			randomOffsetXL = new JLabel("Randomness X:");
			randomOffsetXL.setToolTipText("");
			randomOffsetYL = new JLabel("Y:");
			randomOffsetYL.setToolTipText("");
			durationSecsL = new JLabel("Duration(ms):");
			durationSecsL.setToolTipText("");
			persistTimeL = new JLabel("Persist Time(ms):");
			persistTimeL.setToolTipText("");
		}
	}
	
	private void showExperimentView(Experiment ex)
	{
		ExperimentModel.setupExperimentView(ex);
		MigLayout layout = new MigLayout("align center","[align right][align left,grow]","[]");
		this.setLayout(layout);
		this.add(ExperimentModel.nameL,"");
		this.add(ExperimentModel.name,"wrap");
		this.add(ExperimentModel.promptStringL,"");
		this.add(ExperimentModel.promptString,"wrap");
		this.add(ExperimentModel.trialTypesL,"");
		this.add(ExperimentModel.trialTypes,"wrap");
		this.add(ExperimentModel.usableKeysL,"");
		this.add(ExperimentModel.usableKeys,"wrap");
		this.add(ExperimentModel.randomizeBlockOrderL, "");
		this.add(ExperimentModel.randomizeBlockOrder, "wrap");
		this.add(ExperimentModel.giveFeedbackL, "");
		this.add(ExperimentModel.giveFeedback, "wrap");
		this.add(ExperimentModel.includeAllLettersL, "");
		this.add(ExperimentModel.includeAllLetters, "wrap");
		this.add(ExperimentModel.includeAllNumbersL, "");
		this.add(ExperimentModel.includeAllNumbers, "wrap");
		
	}
	
	private void showBlockView(Block b) {
		BlockModel.setupBlockView(b);
		MigLayout layout = new MigLayout("align center","[align right][align left,grow]","[]");
		this.setLayout(layout);
		this.add(BlockModel.nameL,"");
		this.add(BlockModel.name,"wrap");
		this.add(BlockModel.repsL,"");	
		this.add(BlockModel.reps,"wrap");	
		this.add(BlockModel.randomizeTrialOrderL,"");
		this.add(BlockModel.randomizeTrialOrder,"wrap");
		this.add(BlockModel.delayBetweenTrialsL,"");
		this.add(BlockModel.delayBetweenTrials,"wrap");
		this.add(BlockModel.leaveDisplaysOnL,"");
		this.add(BlockModel.leaveDisplaysOn,"wrap");
		this.add(BlockModel.fontL,"");
		this.add(BlockModel.font,"wrap");
	}
	
	private void showTrial(Trial t) {
		TrialModel.setupTrialView(t);
		MigLayout layout = new MigLayout("align center","[align right][align left,grow]","[]");
		this.setLayout(layout);
		this.add(TrialModel.nameL,"");
		this.add(TrialModel.name,"wrap");
		this.add(TrialModel.trialTypeL,"");
		this.add(TrialModel.trialType,"wrap");
		this.add(TrialModel.correctKeyL,"");
		this.add(TrialModel.correctKey,"wrap");
		this.add(TrialModel.randomizeDisplayOrderL,"");
		this.add(TrialModel.randomizeDisplayOrder,"wrap");
	}
	
	private void showDisplay(Display d) {
		DisplayModel.setupDisplayView(d);
		MigLayout layout = new MigLayout("align center","[align right][align left,grow]","[]");
		this.setLayout(layout);
		this.add(DisplayModel.displayTypeL,"");
		this.add(DisplayModel.displayType,"wrap");
		this.add(DisplayModel.textOrPathL,"");
		this.add(DisplayModel.textOrPath,"wrap");
		this.add(DisplayModel.durationSecsL,"");
		this.add(DisplayModel.durationSecs,"wrap");
		this.add(DisplayModel.persistTimeL,"");
		this.add(DisplayModel.persistTime,"wrap");
		this.add(DisplayModel.positionTypeL,"");
		this.add(DisplayModel.positionType,"wrap");
		this.add(DisplayModel.positionXL,"");
		this.add(DisplayModel.positionX,"wrap");
		this.add(DisplayModel.positionYL,"");
		this.add(DisplayModel.positionY,"wrap");
		this.add(DisplayModel.randomOffsetXL,"");
		this.add(DisplayModel.randomOffsetX,"wrap");
		this.add(DisplayModel.randomOffsetYL,"");
		this.add(DisplayModel.randomOffsetY,"wrap");
	}
	/**
	 * Show an item from the ViewBox.
	 * Helper method to break items out by class.
	 * @param item
	 */
	public void showItem(Object item) {
		this.removeAll();
		if (item instanceof Experiment) {
			Experiment ex = (Experiment) item;
			showExperimentView(ex);
		} else if (item instanceof Block) {
			Block b = (Block) item;
			showBlockView(b);
		} else if (item instanceof Trial) {
			Trial t = (Trial) item;
			showTrial(t);
		} else if (item instanceof Display) {
			Display d = (Display) item;
			showDisplay(d);
		} else {
			System.err.println("INVALID TYPE SELECTED IN TREE:"
					+ item.getClass());
			showDefault();
		}
		GenLab.getInstance().builderP.validate();
		GenLab.getInstance().builderP.repaint();
	}
}