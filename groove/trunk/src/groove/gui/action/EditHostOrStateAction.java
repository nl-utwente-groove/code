package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.trans.ResourceKind;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

import javax.swing.SwingUtilities;

/**
 * Action for editing the current host graph or state.
 */
public class EditHostOrStateAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public EditHostOrStateAction(Simulator simulator) {
        super(simulator, Options.EDIT_STATE_ACTION_NAME, Icons.EDIT_GRAPH_ICON);
        putValue(ACCELERATOR_KEY, Options.EDIT_KEY);
    }

    /**
     * Checks if the enabling condition is satisfied, and if so, calls
     * {@link #setEnabled(boolean)}.
     */
    @Override
    public void refresh() {
        boolean enabled = getSimulatorModel().hasHost() || getSimulatorModel().getState() != null;
        if (enabled != isEnabled()) {
            setEnabled(enabled);
        }
        putValue(NAME, getSimulatorModel().hasHost() ? Options.EDIT_GRAPH_ACTION_NAME
                : Options.EDIT_STATE_ACTION_NAME);
    }

    /**
     * Invokes the editor on the current state. Handles the execution of an
     * <code>EditHostOrStateAction</code>, if the current panel is the state
     * panel.
     */
    @Override
    public boolean execute() {
        if (getSimulatorModel().hasHost()) {
            getStateDisplay().doEdit(getSimulatorModel().getHost().getSource());
        } else {
            AspectGraph graph =
                getStateDisplay().getStatePanel().getJModel().getGraph();
            // find out if we're editing a host graph or a state
            String newGraphName =
                askNewName(ResourceKind.HOST, "Select graph name",
                    graph.getName(), true);
            if (newGraphName != null) {
                final AspectGraph newGraph = graph.rename(newGraphName);
                try {
                    getSimulatorModel().doAddHost(newGraph);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            getMainPanel().getStateDisplay().doEdit(newGraph);
                        }
                    });
                } catch (IOException e) {
                    showErrorDialog(e, "Can't edit state '%s'", graph.getName());
                }
            }
        }
        return false;
    }
}