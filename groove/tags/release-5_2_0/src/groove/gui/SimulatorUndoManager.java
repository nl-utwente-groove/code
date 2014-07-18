package groove.gui;

import groove.gui.SimulatorModel.Change;
import groove.gui.action.ActionStore;
import groove.gui.action.SimulatorAction;
import groove.io.store.SystemStore;

import java.util.Set;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/** Manager for undo actions to the graph grammar view. */
final public class SimulatorUndoManager extends UndoManager implements
        SimulatorListener {
    /** Creates an undo manager for the given simulator. */
    public SimulatorUndoManager(Simulator simulator) {
        this.actions = simulator.getActions();
        simulator.getModel().addListener(this);
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        super.undoableEditHappened(e);
        refreshActions();
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

    @Override
    public SystemStore.Edit editToBeUndone() {
        return (SystemStore.Edit) super.editToBeUndone();
    }

    @Override
    public SystemStore.Edit editToBeRedone() {
        return (SystemStore.Edit) super.editToBeRedone();
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GRAMMAR)
            && source.getGrammar() != oldModel.getGrammar()) {
            discardAllEdits();
            if (oldModel.getGrammar() != null) {
                oldModel.getStore().removeUndoableEditListener(this);
            }
            if (source.getGrammar() != null) {
                source.getStore().addUndoableEditListener(this);
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

    private final ActionStore actions;
}