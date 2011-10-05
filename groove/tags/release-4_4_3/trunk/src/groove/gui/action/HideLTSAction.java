package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

/**
 * The 'default exploration' action (class).
 */
public class HideLTSAction extends SimulatorAction {
    /** Constructs a new action, for a given simulator. */
    public HideLTSAction(Simulator simulator, boolean animated) {
        super(simulator, Options.HIDE_LTS_NAME, Icons.HIDE_LTS_ICON);
    }

    @Override
    public void execute() {
        if (getLtsDisplay().getLtsModel() != null) {
            getLtsDisplay().getLtsModel().hideGTS();
            getLtsDisplay().getLtsJGraph().refreshAllCells();
        }
    }

    @Override
    public void refresh() {
        if (getLtsDisplay().getLtsModel() != null
            && !getLtsDisplay().getLtsModel().getGraph().getResultStates().isEmpty()) {
            setEnabled(true);
        }
    }
}