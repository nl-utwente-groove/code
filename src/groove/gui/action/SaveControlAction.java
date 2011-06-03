package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.SaveDialog;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.GrooveFileChooser;
import groove.view.ControlModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/** Action to save the currently edited control program. */
public class SaveControlAction extends SimulatorAction {
    /** 
     * Constructs a new action, for a given simulator.
     * @param saveAs if {@code true}, attempts to save the control program
     * as a separate file outside the grammar
     */
    public SaveControlAction(Simulator simulator, boolean saveAs) {
        super(simulator, Options.getSaveControlActionName(saveAs), saveAs
                ? Icons.SAVE_AS_ICON : Icons.SAVE_ICON);
        putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
        this.saveAs = saveAs;
        getControlDisplay().addRefreshable(this);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        if (getControlDisplay().isDirty()) {
            String name = getSimulatorModel().getControl().getName();
            if (this.saveAs) {
                result = doSaveAs(name);
            } else {
                result = doSave(name);
            }
        }
        return result;
    }

    /**
     * Saves the control program under a given name.
     */
    public boolean doSave(String name) {
        boolean result = false;
        try {
            String program = getControlDisplay().stopEditing();
            result = getSimulatorModel().doAddControl(name, program);
        } catch (IOException exc) {
            showErrorDialog(exc, "Error saving control program " + name);
        }
        return result;
    }

    private boolean doSaveAs(String name) {
        boolean result = false;
        getControlDisplay().cancelEditing(false);
        ExtensionFilter filter = FileType.CONTROL_FILTER;
        GrooveFileChooser chooser = GrooveFileChooser.getFileChooser(filter);
        chooser.setSelectedFile(new File(name));
        File selectedFile = SaveDialog.show(chooser, getFrame(), null);
        // now save, if so required
        if (selectedFile != null) {
            try {
                String nameInGrammar =
                    getNameInGrammar(
                        filter.stripExtension(selectedFile.getCanonicalPath()),
                        false);
                String program = getSimulatorModel().getControl().getProgram();
                if (nameInGrammar == null) {
                    // store as external file
                    ControlModel.store(program, new FileOutputStream(selectedFile));
                } else {
                    // store in grammar
                    getSimulatorModel().doAddControl(nameInGrammar, program);
                }
                result = true;
            } catch (IOException exc) {
                showErrorDialog(exc,
                    "Error while writing control program to %s", selectedFile);
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(this.saveAs ? getSimulatorModel().getControl() != null
                : getControlDisplay().isEditing());
    }

    private final boolean saveAs;
}