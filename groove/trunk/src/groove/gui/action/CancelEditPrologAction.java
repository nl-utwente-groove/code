package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.PrologEditorPanel;
import groove.gui.Simulator;

/**
 * Action to cancel editing the currently displayed control program.
 */
public class CancelEditPrologAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public CancelEditPrologAction(Simulator simulator) {
        super(simulator, Options.CANCEL_EDIT_ACTION_NAME, Icons.CANCEL_ICON);
    }

    @Override
    public boolean execute() {
        PrologEditorPanel editor = getEditor();
        if (editor != null) {
            editor.cancelEditing(true);
        }
        return false;
    }

    @Override
    public void refresh() {
        setEnabled(true);
    }

    /** Returns the editor of the currently selected prolog program, if any. */
    private PrologEditorPanel getEditor() {
        return getPrologDisplay().getSelectedEditor();
    }
}