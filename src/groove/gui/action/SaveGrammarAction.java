package groove.gui.action;

import static groove.grammar.model.ResourceKind.HOST;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.HostModel;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.store.SystemStore;

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
    public void execute() {
        int approve = getGrammarFileChooser(false).showSaveDialog(getFrame());
        // now save, if so required
        if (approve == JFileChooser.APPROVE_OPTION) {
            File selectedFile = getGrammarFileChooser(false).getSelectedFile();
            try {
                save(selectedFile, true);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while saving grammar to "
                    + selectedFile);
            }
        }
    }

    /**
     * Saves the current grammar to a given file.
     * @param grammarFile the grammar file to be used
     * @throws IOException if the save action failed
     * @return {@code true} if the GTS was invalidated as a result of the action
     */
    public boolean save(File grammarFile, boolean clearDir) throws IOException {
        boolean result = false;
        if (getDisplaysPanel().saveAllEditors(false)) {
            SystemStore newStore =
                getSimulatorModel().getStore().save(grammarFile, clearDir);
            GrammarModel oldGrammar = getSimulatorModel().getGrammar();
            GrammarModel newGrammar = newStore.toGrammarModel();
            if (oldGrammar.getActiveNames(HOST).isEmpty()) {
                // remember external start graph, if grammar has one
                HostModel startGraph = oldGrammar.getStartGraphModel();
                if (startGraph != null) {
                    newGrammar.setStartGraph(startGraph.getSource());
                }
            }
            getSimulatorModel().setGrammar(newGrammar);
            getSimulator().setTitle();
            getGrammarFileChooser().setSelectedFile(grammarFile);
            result = true;
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGrammar() != null);
    }
}