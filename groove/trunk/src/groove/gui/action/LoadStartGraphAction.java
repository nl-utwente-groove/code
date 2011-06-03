package groove.gui.action;

import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.xml.AspectGxl;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

/**
 * Action for loading and setting a new initial state.
 */
public class LoadStartGraphAction extends SimulatorAction {
    /** Constructs an instance of the action, for a given simulator. */
    public LoadStartGraphAction(Simulator simulator) {
        super(simulator, Options.LOAD_START_STATE_ACTION_NAME, null);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        // stateFileChooser.setSelectedFile(currentStartStateFile);
        int approve = getStateFileChooser().showOpenDialog(getFrame());
        // now load, if so required
        if (approve == JFileChooser.APPROVE_OPTION && confirmAbandon()) {
            File file = getStateFileChooser().getSelectedFile();
            try {
                AspectGraph startGraph =
                    AspectGxl.getInstance().unmarshalGraph(file);
                result = getSimulator().getModel().doSetStartGraph(startGraph);
            } catch (IOException exc) {
                showErrorDialog(
                    exc, "Could not load start graph from " + file.getName());
            }
        }
        return result;
    }

    /**
     * Sets the enabling status of this action, depending on whether a
     * grammar is currently loaded.
     */
    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getGrammar() != null);
    }
}