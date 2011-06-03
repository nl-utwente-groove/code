package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

import javax.swing.Action;
import javax.swing.undo.UndoManager;

/**
 * Action for undoing an edit to the grammar.
 */
public class UndoSimulatorAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public UndoSimulatorAction(Simulator simulator) {
        super(simulator, Options.UNDO_ACTION_NAME, Icons.UNDO_ICON);
        putValue(SHORT_DESCRIPTION, Options.UNDO_ACTION_NAME);
        putValue(ACCELERATOR_KEY, Options.UNDO_KEY);
        setEnabled(false);
        this.undoManager = simulator.getUndoManager();
    }

    @Override
    public boolean execute() {
        this.undoManager.undo();
        getSimulatorModel().synchronize();
        return true;
    }

    @Override
    public void refresh() {
        if (this.undoManager.canUndo()) {
            setEnabled(true);
            putValue(Action.NAME, this.undoManager.getUndoPresentationName());
        } else {
            setEnabled(false);
            putValue(Action.NAME, Options.UNDO_ACTION_NAME);
        }
    }

    private final UndoManager undoManager;
}