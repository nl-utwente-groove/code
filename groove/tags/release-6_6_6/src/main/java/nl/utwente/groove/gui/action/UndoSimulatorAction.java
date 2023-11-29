package nl.utwente.groove.gui.action;

import javax.swing.Action;

import nl.utwente.groove.gui.Icons;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorUndoManager;
import nl.utwente.groove.io.store.EditType;
import nl.utwente.groove.io.store.SystemStore;

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
    public void execute() {
        SystemStore.Edit edit = this.undoManager.editToBeUndone();
        this.undoManager.undo();
        getSimulatorModel().synchronize(edit.getType() != EditType.LAYOUT);
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

    private final SimulatorUndoManager undoManager;
}