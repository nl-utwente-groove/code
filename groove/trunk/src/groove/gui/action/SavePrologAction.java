package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.PrologDisplay;
import groove.gui.Simulator;
import groove.gui.PrologDisplay.PrologEditor;

import java.io.IOException;

/** Action that saves all Prolog files. */
public class SavePrologAction extends SimulatorAction {
    /** Constructs an instance of this action for a given simulator. */
    public SavePrologAction(Simulator simulator) {
        super(simulator, Options.SAVE_ACTION_NAME, Icons.SAVE_ICON);
    }

    @Override
    public void refresh() {
        PrologEditor editor = getEditor();
        setEnabled(editor != null && editor.isDirty());
    }

    @Override
    public boolean execute() {
        if (doSave(getEditor().getName(), getEditor().getProgram())) {
            getEditor().discardEdits();
        }
        return false;
    }

    /** Saves a prolog program with a given name and content. */
    public boolean doSave(String name, String program) {
        boolean result = false;
        try {
            getModel().doAddProlog(name, program);
            result = true;
        } catch (IOException e) {
            showErrorDialog(e, "Error saving Prolog program %s", name);
        }
        return result;
    }

    /** Returns the editor of the currently selected prolog program, if any. */
    private PrologEditor getEditor() {
        PrologEditor result = null;
        if (getModel().hasProlog()) {
            result =
                getPrologDisplay().getEditor(
                    getModel().getProlog().getName());
        }
        return result;
    }
}