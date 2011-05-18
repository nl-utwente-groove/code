package groove.gui.action;

import static groove.graph.GraphRole.RULE;
import groove.graph.GraphRole;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.dialog.SaveDialog;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.LTSJModel;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.io.GrooveFileChooser;
import groove.io.xml.AspectGxl;
import groove.io.xml.DefaultGxl;
import groove.lts.GTS;
import groove.trans.RuleName;
import groove.view.CtrlView;
import groove.view.FormatException;
import groove.view.PrologView;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Action to save the state or LTS as a graph.
 */
public class SaveSimulatorAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public SaveSimulatorAction(Simulator simulator) {
        super(simulator, Options.SAVE_AS_ACTION_NAME, Icons.SAVE_ICON);
        putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
    }

    @Override
    public boolean execute() {
        boolean result = false;
        if (getSimulator().getPanel() == getSimulator().getControlPanel()) {
            result = actionForControl();
        } else {
            GraphJModel<?,?> jModel =
                getSimulator().getGraphPanel().getJModel();
            if (getSimulator().getGraphPanel() == getSimulator().getStatePanel()) {
                result =
                    actionForGraphs(((AspectJModel) jModel).getGraph(),
                        FileType.STATE_FILTER);
            } else if (getSimulator().getGraphPanel() == getSimulator().getRulePanel()) {
                result =
                    actionForGraphs(((AspectJModel) jModel).getGraph(),
                        FileType.RULE_FILTER);
            } else if (getSimulator().getGraphPanel() == getSimulator().getLtsPanel()) {
                result =
                    actionForGTS(((LTSJModel) jModel).getGraph(),
                        FileType.GXL_FILTER);
            } else if (getSimulator().getGraphPanel() == getSimulator().getTypePanel()) {
                result =
                    actionForGraphs(((AspectJModel) jModel).getGraph(),
                        FileType.TYPE_FILTER);
            }
        }
        return result;
    }

    boolean actionForGraphs(AspectGraph graph, ExtensionFilter filter) {
        boolean result = false;
        String name = graph.getName();
        GrooveFileChooser chooser = GrooveFileChooser.getFileChooser(filter);
        chooser.setSelectedFile(new File(name));
        File selectedFile = SaveDialog.show(chooser, getFrame(), null);
        // now save, if so required
        if (selectedFile != null) {
            try {
                result = marshalGraph(graph, selectedFile, filter);
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                        "Error while saving graph to '%s'", selectedFile));
            }
        }
        return result;
    }

    private boolean marshalGraph(AspectGraph graph, File selectedFile,
            ExtensionFilter filter) throws IOException {
        boolean result = false;
        // find out if this is within the grammar directory
        String name = null;
        Object location = getModel().getStore().getLocation();
        if (location instanceof File) {
            String locationPath = ((File) location).getCanonicalPath();
            String selectedPath =
                filter.stripExtension(selectedFile.getCanonicalPath());
            name = getName(locationPath, selectedPath, graph.getRole());
        }
        if (name == null) {
            name = filter.stripExtension(selectedFile.getName());
            graph = graph.rename(name);
            AspectGxl.getInstance().marshalGraph(graph, selectedFile);
        } else {
            // the graph will be put within the grammar itself
            graph = graph.rename(name);
            switch (graph.getRole()) {
            case HOST:
                result = getModel().doAddHost(graph);
                break;
            case RULE:
                result = getSimulator().getModel().doAddRule(graph);
                break;
            case TYPE:
                result = getSimulator().getModel().doAddType(graph);
                break;
            default:
                assert false;
            }
        }
        return result;
    }

    /** Constructs an aspect graph name from a  file within the grammar location,
     * or {@code null} if the file is not within the grammar location.
     * @throws IOException if the name is not well-formed
     */
    private String getName(String grammarPath, String selectedPath,
            GraphRole role) throws IOException {
        String name = null;
        if (selectedPath.startsWith(grammarPath)) {
            String diff = selectedPath.substring(grammarPath.length());
            File pathDiff = new File(diff);
            List<String> pathFragments = new ArrayList<String>();
            while (pathDiff.getName().length() > 0) {
                pathFragments.add(pathDiff.getName());
                pathDiff = pathDiff.getParentFile();
            }
            assert !pathFragments.isEmpty();
            int i = pathFragments.size() - 1;
            if (role == RULE) {
                RuleName ruleName = new RuleName(pathFragments.get(i));
                for (i--; i >= 0; i--) {
                    try {
                        ruleName = new RuleName(ruleName, pathFragments.get(i));
                    } catch (FormatException e) {
                        throw new IOException("Malformed rule name " + diff);
                    }
                }
                name = ruleName.toString();
            } else if (pathFragments.size() > 1) {
                throw new IOException(
                    "Can't save graph or type in a grammar subdirectory");
            } else {
                name = pathFragments.get(0);
            }
        }
        return name;
    }

    private boolean actionForGTS(GTS gts, ExtensionFilter filter) {
        GrooveFileChooser chooser = GrooveFileChooser.getFileChooser(filter);
        chooser.setSelectedFile(new File(Simulator.LTS_FILE_NAME));
        File selectedFile = SaveDialog.show(chooser, getFrame(), null);
        // now save, if so required
        if (selectedFile != null) {
            String name = filter.stripExtension(selectedFile.getName());
            gts.setName(name);
            try {
                DefaultGxl.getInstance().marshalAnyGraph(gts, selectedFile);
            } catch (IOException exc) {
                showErrorDialog(exc, String.format("Error while saving LTS to '%s'",
                        selectedFile));
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
        if (getSimulator().getGraphPanel() == getSimulator().getStatePanel()) {
            setEnabled(getSimulator().getStatePanel().getJModel() != null);
            if (getModel().getHost() == null) {
                putValue(NAME, Options.SAVE_STATE_ACTION_NAME);
                putValue(SHORT_DESCRIPTION, Options.SAVE_STATE_ACTION_NAME);
            } else {
                putValue(NAME, Options.SAVE_GRAPH_ACTION_NAME);
                putValue(SHORT_DESCRIPTION, Options.SAVE_GRAPH_ACTION_NAME);
            }
        } else if (getSimulator().getGraphPanel() == getSimulator().getRulePanel()) {
            setEnabled(getModel().getRule() != null);
            putValue(NAME, Options.SAVE_RULE_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_RULE_ACTION_NAME);
        } else if (getSimulator().getGraphPanel() == getSimulator().getLtsPanel()) {
            setEnabled(getModel().getGts() != null);
            putValue(NAME, Options.SAVE_LTS_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_LTS_ACTION_NAME);
        } else if (getSimulator().getGraphPanel() == getSimulator().getTypePanel()) {
            setEnabled(getModel().getType() != null);
            putValue(NAME, Options.SAVE_TYPE_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_TYPE_ACTION_NAME);
        } else if (getSimulator().getPanel() == getSimulator().getControlPanel()) {
            setEnabled(getModel().getControl() != null);
            putValue(NAME, Options.SAVE_CONTROL_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_CONTROL_ACTION_NAME);
        } else {
            setEnabled(false);
            putValue(NAME, Options.SAVE_AS_ACTION_NAME);
            putValue(SHORT_DESCRIPTION, Options.SAVE_AS_ACTION_NAME);
        }
    }
}