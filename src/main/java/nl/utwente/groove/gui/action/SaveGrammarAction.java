package nl.utwente.groove.gui.action;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.AIGenerated;

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
        JFileChooser chooser = prepareFileChooser();
        int approve = chooser.showSaveDialog(getFrame());
        // now save, if so required
        if (approve == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                save(selectedFile, true);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while saving grammar to " + selectedFile);
            }
        }
    }

    /**
     * Returns the grammar file chooser, with a file derived from the currently
     * loaded grammar set as proposed file.
     * Left to itself, the chooser only remembers the last grammar saved or
     * created through it, which a subsequent Load Grammar does not update.
     * The proposal takes its name from the grammar location and its directory
     * from the grammar origin; the two differ for a grammar unpacked from an
     * archive, whose location is a temporary directory. For a grammar loaded
     * from a URL there is no directory to propose, so the chooser stays where
     * it is.
     */
    @AIGenerated("Claude Opus 5, 2026-09")
    public JFileChooser prepareFileChooser() {
        JFileChooser result = getGrammarFileChooser(false);
        SystemStore store = getSimulatorModel().getStore();
        if (store != null) {
            File origin = store.getOriginFile();
            File dir = origin == null
                ? result.getCurrentDirectory()
                : origin.getAbsoluteFile().getParentFile();
            result.setSelectedFile(new File(dir, store.getLocation().getName()));
        }
        return result;
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
            SystemStore newStore = getSimulatorModel().getStore().save(grammarFile, clearDir);
            GrammarModel oldGrammar = getSimulatorModel().getGrammar();
            GrammarModel newGrammar = newStore.toGrammarModel();
            var startGraphModel = oldGrammar.getStartGraphModel();
            if (startGraphModel.isExternal()) {
                // remember external start graph, if grammar has one;
                // a start graph model composed from the stored host graphs
                // must not be pinned on the new grammar
                var startGraph = startGraphModel.getSource();
                assert startGraph != null; // an external start graph model has a source
                newGrammar.setStartGraph(startGraph);
            }
            getSimulatorModel().setGrammar(newStore);
            getSimulator().setTitle();
            result = true;
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGrammar() != null);
    }
}