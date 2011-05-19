package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.SaveDialog;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.GrooveFileChooser;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;

/**
 * Action to save the host graph externally, i.e., not as part of the grammar.
 */
public class SaveHostOrStateAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public SaveHostOrStateAction(Simulator simulator) {
        super(simulator, Options.SAVE_STATE_ACTION_NAME, Icons.SAVE_ICON);
        putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        AspectGraph graph = getModel().getHost().getAspectGraph();
        ExtensionFilter filter = FileType.STATE_FILTER;
        String name = graph.getName();
        GrooveFileChooser chooser = GrooveFileChooser.getFileChooser(filter);
        chooser.setSelectedFile(new File(name));
        File selectedFile = SaveDialog.show(chooser, getFrame(), null);
        // now save, if so required
        if (selectedFile != null) {
            try {
                result = marshalGraph(graph, selectedFile);
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while saving graph to '%s'", selectedFile));
            }
        }
        return result;
    }

    /**
     * Tests if the action should be enabled according to the current state
     * of the simulator, and also modifies the action name.
     */
    @Override
    public void refresh() {
        setEnabled(getModel().getState() != null
            || getModel().getHost() != null);
        if (getModel().getHost() == null) {
            putValue(NAME, Options.SAVE_STATE_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_STATE_ACTION_NAME);
        } else {
            putValue(NAME, Options.SAVE_GRAPH_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_GRAPH_ACTION_NAME);
        }
    }
}