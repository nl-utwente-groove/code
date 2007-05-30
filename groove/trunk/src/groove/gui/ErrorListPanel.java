package groove.gui;

import groove.util.Groove;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * Panel showing a list of error messages.
 * The panel hides itself when the error list is empty.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class ErrorListPanel extends JPanel {
	/**
	 * Constructs a new panel. 
	 */
	public ErrorListPanel() {
		super(new BorderLayout());
		add(new JLabel("<html><b>Format errors in graph</b></html>"), BorderLayout.NORTH);
		JScrollPane scrollPane = new JScrollPane(getErrorArea());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(0, 70));
		add(scrollPane);
		setVisible(false);
	}

	/** 
	 * Shows or appends a list of error messages in the error window. 
	 * @param errors the list of error messages to be shown
	 */
	public void setErrors(List<String> errors) {
		if (errors.isEmpty()) {
			if (isVisible()) {
				setVisible(false);
				getErrorArea().setText("");
			}
		} else {
			StringBuffer text = new StringBuffer();
			text.append(Groove.toString(errors.toArray(), "", "", "\n"));
			getErrorArea().setText(text.toString());
			getErrorArea().setSelectionStart(0);
			if (! isVisible()) {
				setVisible(true);
			}
		}
	}

	/** Lazily creates and returns the error panel. */
	private JTextArea getErrorArea() {
		if (errorArea == null) {
			errorArea = new JTextArea();
			errorArea.setEditable(false);
			errorArea.setBackground(SystemColor.text);
			errorArea.setForeground(SystemColor.RED);
			//    			errorArea.setPreferredSize(new Dimension(0, 70));
		}
		return errorArea;
	}

	/** The text area containing the error messages. */
	private JTextArea errorArea;
}