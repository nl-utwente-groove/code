package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

import javax.swing.SwingUtilities;

/** Action to start editing the currently selected state. */
public class EditStateAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public EditStateAction(Simulator simulator) {
        super(simulator, Options.EDIT_STATE_ACTION_NAME, Icons.EDIT_STATE_ICON,
            null, ResourceKind.HOST);
        putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
    }

    @Override
    public void execute() {
        AspectGraph graph = getLtsDisplay().getStateTab().getGraph();
        final String newGraphName = askNewName(graph.getName(), true);
        if (newGraphName != null) {
            final AspectGraph newGraph = graph.rename(newGraphName);
            try {
                getSimulatorModel().doAddGraph(getResourceKind(), newGraph);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getDisplaysPanel().getHostDisplay().startEditResource(
                            newGraphName);
                    }
                });
            } catch (IOException e) {
                showErrorDialog(e, "Can't edit state '%s'", graph.getName());
            }
        }
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().hasState());
    }
}