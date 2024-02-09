package nl.utwente.groove.gui.action;

import javax.swing.Action;

import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorUndoManager;
import nl.utwente.groove.io.store.EditType;
import nl.utwente.groove.io.store.SystemStore;

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
    public void execute() {
        SystemStore.Edit edit = this.undoManager.editToBeRedone();
        this.undoManager.redo();
        getSimulatorModel().synchronize(edit.getType() != EditType.LAYOUT);
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

    private final SimulatorUndoManager undoManager;
}