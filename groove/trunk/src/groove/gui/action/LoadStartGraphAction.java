package groove.gui.action;

import groove.graph.GraphRole;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.ExtensionFilter;
import groove.io.graph.AttrGraph;
import groove.io.graph.GxlIO;

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
    public void execute() {
        // stateFileChooser.setSelectedFile(currentStartStateFile);
        int approve = getStateFileChooser().showOpenDialog(getFrame());
        // now load, if so required
        if (approve == JFileChooser.APPROVE_OPTION) {
            File file = getStateFileChooser().getSelectedFile();
            try {
                AttrGraph xmlGraph =
                    GxlIO.getInstance().loadGraph(file);
                xmlGraph.setRole(GraphRole.HOST);
                xmlGraph.setName(ExtensionFilter.getPureName(file));
                getSimulatorModel().doSetStartGraph(xmlGraph.toAspectGraph());
            } catch (IOException exc) {
                showErrorDialog(exc,
                    "Could not load start graph from " + file.getName());
            }
        }
    }

    /**
     * Sets the enabling status of this action, depending on whether a
     * grammar is currently loaded.
     */
    @Override
    public void refresh() {
        setEnabled(getGrammarModel() != null);
    }
}