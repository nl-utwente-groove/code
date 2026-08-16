package nl.utwente.groove.gui;

import java.util.Set;
import java.util.function.Consumer;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import nl.utwente.groove.gui.SimulatorModel.Change;
import nl.utwente.groove.gui.action.ActionStore;
import nl.utwente.groove.gui.action.SimulatorAction;
import nl.utwente.groove.io.store.SystemStore;

/** Manager for undo actions to the graph grammar view. */
final public class SimulatorUndoManager extends UndoManager implements
        SimulatorListener {
    /** Creates an undo manager for the given simulator. */
    public SimulatorUndoManager(Simulator simulator) {
        this.actions = simulator.getActions();
        simulator.getModel().addListener(this);
    }

    /** Adds a posted store edit to the undo history. */
    private void editPosted(SystemStore.Edit edit) {
        if (edit instanceof UndoableEdit undoable) {
            addEdit(undoable);
            refreshActions();
        }
    }

    @Override
    public synchronized void discardAllEdits() {
        super.discardAllEdits();
        refreshActions();
    }

    @Override
    public synchronized void redo() throws CannotRedoException {
        super.redo();
        refreshActions();
    }

    @Override
    public synchronized void undo() throws CannotUndoException {
        super.undo();
        refreshActions();
    }

    /** Returns the store edit that {@link #undo()} would undo, if any. */
    public SystemStore.Edit getEditToBeUndone() {
        return (SystemStore.Edit) editToBeUndone();
    }

    /** Returns the store edit that {@link #redo()} would redo, if any. */
    public SystemStore.Edit getEditToBeRedone() {
        return (SystemStore.Edit) editToBeRedone();
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GRAMMAR)
            && source.getGrammar() != oldModel.getGrammar()) {
            discardAllEdits();
            if (oldModel.getGrammar() != null) {
                oldModel.getStore().removeEditListener(this.editListener);
            }
            if (source.getGrammar() != null) {
                source.getStore().addEditListener(this.editListener);
            }
        }
    }

    private void refreshActions() {
        getUndoAction().refresh();
        getRedoAction().refresh();
    }

    private SimulatorAction getRedoAction() {
        return this.actions.getRedoAction();
    }

    private SimulatorAction getUndoAction() {
        return this.actions.getUndoAction();
    }

    /** The listener registered on the store; kept in a field so that
     * registration and deregistration use the identical object. */
    private final Consumer<SystemStore.Edit> editListener = this::editPosted;

    private final ActionStore actions;
}
