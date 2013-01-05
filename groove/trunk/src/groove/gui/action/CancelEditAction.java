package groove.gui.action;

import groove.grammar.model.ResourceKind;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.display.ResourceTab;

/**
 * Action to cancel editing the currently displayed control program.
 */
public class CancelEditAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public CancelEditAction(Simulator simulator, ResourceKind resource) {
        super(simulator, Options.CANCEL_EDIT_ACTION_NAME, Icons.CANCEL_ICON,
            null, resource);
        putValue(ACCELERATOR_KEY, Options.CLOSE_KEY);
    }

    @Override
    public void execute() {
        ResourceTab editorTab = getDisplay().getSelectedEditor();
        if (editorTab != null) {
            editorTab.saveEditor(true, true);
        }
    }

    @Override
    public void refresh() {
        setEnabled(true);
    }
}
