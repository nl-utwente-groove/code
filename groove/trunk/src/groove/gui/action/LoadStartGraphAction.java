package groove.gui.action;

import groove.grammar.aspect.AspectGraph;
import groove.graph.GraphRole;
import groove.graph.plain.PlainGraph;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.ExtensionFilter;
import groove.io.xml.LayedOutXml;

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
                PlainGraph plainGraph =
                    LayedOutXml.getInstance().unmarshalGraph(file);
                plainGraph.setRole(GraphRole.HOST);
                plainGraph.setName(ExtensionFilter.getPureName(file));
                getSimulatorModel().doSetStartGraph(
                    AspectGraph.newInstance(plainGraph));
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