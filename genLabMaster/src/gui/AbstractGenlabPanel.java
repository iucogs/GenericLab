package gui;
import javax.swing.JPanel;
/**
 * Standardizes behavior of the panels that represent each page (formerly each tab) in GenLab.
 * Creates standard methods for preparing a page to be displayed and for doing work while leaving the page.
 * @author Jay
 *
 */
public abstract class AbstractGenlabPanel extends JPanel {

	/**
	 * To be called before showing a panel.
	 * @return
	 */
	public abstract boolean loadPanel();
	
	/**
	 * To be called before leaving a panel.  If returns false,
	 * the panel should not be left (the user may have unfinished business).
	 * @return
	 */
	public abstract boolean leavePanel();
}
