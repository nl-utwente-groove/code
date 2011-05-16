package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

/**
 * Action for saving a rule system.
 */
public class SaveGrammarAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public SaveGrammarAction(Simulator simulator) {
        super(simulator, Options.SAVE_GRAMMAR_ACTION_NAME, null);
        putValue(ACCELERATOR_KEY, Options.SAVE_GRAMMAR_AS_KEY);
    }

    @Override
    protected boolean doAction() {
        boolean result = false;
        int approve = getGrammarFileChooser(false).showSaveDialog(getFrame());
        // now save, if so required
        if (approve == JFileChooser.APPROVE_OPTION) {
            File selectedFile = getGrammarFileChooser(false).getSelectedFile();
            if (confirmOverwriteGrammar(selectedFile)) {
                try {
                    result |= getSimulator().doSaveGrammar(selectedFile, true);
                } catch (IOException exc) {
                    showErrorDialog("Error while saving grammar to "
                        + selectedFile, exc);
                }
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getModel().getGrammar() != null);
    }
}