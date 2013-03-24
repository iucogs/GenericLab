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
				switchToTab(1);
			}
		});
		loadExperiment.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				switchToTab(3);
			}
		});
		
		this.setBackground(Color.CYAN);
		MigLayout lay = new MigLayout();
		this.setLayout(lay);
		this.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		this.add(new JLabel("Please choose an option."),"span 2,push,align center,wrap");
		this.add(createExperiment,"push,w 200!, h 100!,align right");
		this.add(loadExperiment,"w 200!, h 100!,align left,push");
	}
	
	private void switchToTab(int i) {
		tabbedPane.setSelectedIndex(i);
	}
	
}
