package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.SaveDialog;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.GrooveFileChooser;
import groove.io.xml.DefaultGxl;
import groove.lts.GTS;
import groove.view.CtrlView;
import groove.view.PrologView;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Action to save simulator components in external files, i.e., not as
 * part of the grammar.
 */
public class SaveAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public SaveAction(Simulator simulator) {
        super(simulator, Options.SAVE_AS_ACTION_NAME, Icons.SAVE_ICON);
        putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        switch (getPanel().getSelectedTab()) {
        case CONTROL:
            result = actionForControl();
            break;
        case GRAPH:
            result = getActions().getSaveHostOrStateAction().execute();
            break;
        case LTS:
            result = actionForGTS(getModel().getGts(), FileType.GXL_FILTER);
            break;
        case EDITOR:
        case PROLOG:
            break;
        case RULE:
            result =
                actionForGraphs(getModel().getRule().getAspectGraph(),
                    FileType.RULE_FILTER);
            break;
        case TYPE:
            result =
                actionForGraphs(getModel().getType().getAspectGraph(),
                    FileType.TYPE_FILTER);
            break;
        default:
            assert false;
        }
        return result;
    }

    boolean actionForGraphs(AspectGraph graph, ExtensionFilter filter) {
        return getActions().getSaveHostOrStateAction().execute();
    }

    private boolean actionForGTS(GTS gts, ExtensionFilter filter) {
        File selectedFile = askSaveGraph(gts);
        // now save, if so required
        if (selectedFile != null) {
            String name = filter.stripExtension(selectedFile.getName());
            gts.setName(name);
            try {
                DefaultGxl.getInstance().marshalAnyGraph(gts, selectedFile);
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while saving LTS to '%s'", selectedFile));
            }
        }
        return false;
    }

    private boolean actionForControl() {
        boolean result = false;
        ExtensionFilter filter = FileType.CONTROL_FILTER;
        GrooveFileChooser chooser = GrooveFileChooser.getFileChooser(filter);
        CtrlView controlView = getModel().getControl();
        String program = controlView.getProgram();
        String controlName = controlView.getName();
        chooser.setSelectedFile(new File(controlName));
        File selectedFile = SaveDialog.show(chooser, getFrame(), null);
        // now save, if so required
        if (selectedFile != null) {
            controlName = filter.stripExtension(selectedFile.getName());
            try {
                result = doSaveControl(program, selectedFile);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while saving to " + selectedFile);
            }
        }
        return result;
    }

    /**
     * Attempts to save a control program to a file. Failure to do so will be
     * reported in an error dialog. The return value indicates if the attempt
     * was successful.
     * @param controlProgram string containing a (parsable) control program
     *        (non-null)
     * @param file target file; will be overwritten if already existing
     *        (non-null)
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the save action failed
     */
    private boolean doSaveControl(String controlProgram, File file)
        throws IOException {
        CtrlView.store(controlProgram, new FileOutputStream(file));
        return false;
    }

    /**
     * Attempts to save a prolog program to a file. Failure to do so will be
     * reported in an error dialog. The return value indicates if the attempt
     * was successful.
     * @param prolog string containing a (parsable) control program
     *        (non-null)
     * @param file target file; will be overwritten if already existing
     *        (non-null)
     * @return {@code true} if the GTS was invalidated as a result of the action
     * @throws IOException if the save action failed
     */
    private boolean doSaveProlog(String prolog, File file) throws IOException {
        PrologView.store(prolog, new FileOutputStream(file));
        return false;
    }

    /**
     * Tests if the action should be enabled according to the current state
     * of the simulator, and also modifies the action name.
     * 
     */
    @Override
    public void refresh() {
        switch (getPanel().getSelectedTab()) {
        case CONTROL:
            setEnabled(getModel().getControl() != null);
            putValue(NAME, Options.SAVE_CONTROL_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_CONTROL_ACTION_NAME);
            break;
        case GRAPH:
            setEnabled(getModel().getHost() != null
                || getModel().getState() != null);
            if (getModel().getHost() == null) {
                putValue(NAME, Options.SAVE_STATE_ACTION_NAME);
                putValue(SHORT_DESCRIPTION, Options.SAVE_STATE_ACTION_NAME);
            } else {
                putValue(NAME, Options.SAVE_GRAPH_ACTION_NAME);
                putValue(SHORT_DESCRIPTION, Options.SAVE_GRAPH_ACTION_NAME);
            }
            break;
        case LTS:
            setEnabled(getModel().getGts() != null);
            putValue(NAME, Options.SAVE_LTS_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_LTS_ACTION_NAME);
            break;
        case RULE:
            setEnabled(getModel().getRule() != null);
            putValue(NAME, Options.SAVE_RULE_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_RULE_ACTION_NAME);
            break;
        case TYPE:
            setEnabled(getModel().getType() != null);
            putValue(NAME, Options.SAVE_TYPE_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_TYPE_ACTION_NAME);
            break;
        case EDITOR:
        case PROLOG:
            setEnabled(false);
            putValue(NAME, Options.SAVE_AS_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_AS_ACTION_NAME);
            break;
        default:
            assert false;
        }
    }
}