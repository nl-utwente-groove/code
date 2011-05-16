package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

/**
 * Action for loading a new rule system.
 */
public class LoadGrammarAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public LoadGrammarAction(Simulator simulator) {
        super(simulator, Options.LOAD_GRAMMAR_ACTION_NAME, null);
        putValue(ACCELERATOR_KEY, Options.OPEN_KEY);
        simulator.addAccelerator(this);
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        JFileChooser fileChooser = getGrammarFileChooser(true);
        int approve = fileChooser.showOpenDialog(getFrame());
        // now load, if so required
        if (approve == JFileChooser.APPROVE_OPTION && confirmAbandon()) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile == null) {
                showErrorDialog("No file selected", null);
            } else {
                try {
                    result = getSimulator().doLoadGrammar(selectedFile, null);
                } catch (IOException exc) {
                    showErrorDialog(exc.getMessage(), exc);
                }
            }
        }
        return result;
    }
}