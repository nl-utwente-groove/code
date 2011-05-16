package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

import javax.swing.Action;
import javax.swing.undo.UndoManager;

/**
 * Action for redoing the last edit to the grammar.
 */
public class RedoSimulatorAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public RedoSimulatorAction(Simulator simulator) {
        super(simulator, Options.REDO_ACTION_NAME, Icons.REDO_ICON);
        putValue(SHORT_DESCRIPTION, Options.REDO_ACTION_NAME);
        putValue(ACCELERATOR_KEY, Options.REDO_KEY);
        setEnabled(false);
        this.undoManager = simulator.getUndoManager();
    }

    @Override
    protected boolean doAction() {
        this.undoManager.redo();
        getModel().refreshGrammar(true);
        return true;
    }

    @Override
    public void refresh() {
        if (this.undoManager.canRedo()) {
            setEnabled(true);
            putValue(Action.NAME, this.undoManager.getRedoPresentationName());
        } else {
            setEnabled(false);
            putValue(Action.NAME, Options.REDO_ACTION_NAME);
        }
    }

    private final UndoManager undoManager;
}