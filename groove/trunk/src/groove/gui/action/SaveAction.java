package groove.gui.action;

import static groove.graph.GraphRole.RULE;
import groove.gui.EditorTab;
import groove.gui.GraphEditorTab;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.TextEditorTab;
import groove.gui.dialog.SaveDialog;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.io.xml.AspectGxl;
import groove.trans.ResourceKind;
import groove.view.ControlModel;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Action to save the resource in
 * an editor panel.
 * @author Arend Rensink
 * @version $Revision $
 */
public final class SaveAction extends SimulatorAction {
    /** 
     * Creates an instance of the action for a given simulator.
     * @param simulator the editor whose content should be saved
     * @param saveAs flag indicating that the action attempts to save to
     * a file outside the grammar.
     */
    public SaveAction(Simulator simulator, ResourceKind resource, boolean saveAs) {
        super(simulator, saveAs ? Options.SAVE_AS_ACTION_NAME
                : Options.SAVE_ACTION_NAME, saveAs ? Icons.SAVE_AS_ICON
                : Icons.SAVE_ICON, null, resource);
        simulator.addAccelerator(this);
        this.saveAs = saveAs;
    }

    @Override
    public void execute() {
        boolean saved = false;
        if (confirmBehaviourOption(Options.STOP_SIMULATION_OPTION)) {
            ResourceKind resourceKind = getResourceKind();
            EditorTab editor = getEditor();
            String name =
                editor == null ? getSimulatorModel().getSelected(resourceKind)
                        : editor.getName();
            if (resourceKind.isGraphBased()) {
                AspectGraph graph;
                if (isForState()) {
                    graph = getStateDisplay().getStateTab().getGraph();
                } else if (editor == null) {
                    graph = getGrammarStore().getGraphs(resourceKind).get(name);
                } else {
                    graph = ((GraphEditorTab) editor).getGraph();
                }
                saved = this.saveAs ? doSaveGraphAs(graph) : doSaveGraph(graph);
            } else {
                assert resourceKind.isTextBased();
                String text;
                if (editor == null) {
                    text = getGrammarStore().getTexts(resourceKind).get(name);
                } else {
                    text = ((TextEditorTab) editor).getProgram();
                }
                saved =
                    this.saveAs ? doSaveTextAs(name, text) : doSaveText(name,
                        text);
            }
            if (saved) {
                getEditor().setClean();
            }
        }
    }

    /**
     * Stores the graph within the grammar.
     * @return {@code true} if the action succeeded
     */
    public boolean doSaveGraph(AspectGraph graph) {
        boolean result = false;
        ResourceKind resource = ResourceKind.toResource(graph.getRole());
        if (isForState()) {
            // we're saving a state
            String newName = askNewName(resource, graph.getName(), true);
            graph = newName == null ? null : graph.rename(newName);
        }
        try {
            if (graph != null) {
                getSimulatorModel().doAddGraph(resource, graph);
                result = true;
            }
        } catch (IOException exc) {
            showErrorDialog(exc, "Error while saving edited graph '%s'",
                graph.getName());
        }
        return result;
    }

    /** Attempts to write the graph to an external file. 
     * @return {@code true} if the graph was saved within the grammar
     */
    public boolean doSaveGraphAs(AspectGraph graph) {
        boolean result = false;
        File selectedFile = askSaveGraph(graph);
        // now save, if so required
        if (selectedFile != null) {
            try {
                ExtensionFilter filter = getResourceKind().getFilter();
                // find out if this is within the grammar directory
                String selectedPath =
                    filter.stripExtension(selectedFile.getCanonicalPath());
                String nameInGrammar =
                    getNameInGrammar(selectedPath, graph.getRole() == RULE);
                if (nameInGrammar == null) {
                    // save in external file
                    String newName =
                        filter.stripExtension(selectedFile.getName());
                    AspectGxl.getInstance().marshalGraph(graph.rename(newName),
                        selectedFile);
                } else {
                    // save within the grammar
                    result = doSaveGraph(graph.rename(nameInGrammar));
                }
            } catch (IOException exc) {
                showErrorDialog(exc, String.format(
                    "Error while writing graph to '%s'", selectedFile));
            }
        }
        return result;
    }

    /**
     * Saves the text under a given name in the grammar.
     * @return {@code true} if the action succeeded
     */
    public boolean doSaveText(String name, String text) {
        boolean result = false;
        try {
            getSimulatorModel().doAddText(getResourceKind(), name, text);
            result = true;
        } catch (IOException exc) {
            showErrorDialog(exc, "Error saving %s %s",
                getResourceKind().getDescription(), name);
        }
        return result;
    }

    /**
     * Saves the text under a given name as a file outside the grammar.
     * @return {@code true} if the text was saved within the grammar
     */
    public boolean doSaveTextAs(String name, String text) {
        boolean result = false;
        File selectedFile = askSave(name);
        // now save, if so required
        if (selectedFile != null) {
            ExtensionFilter filter = getResourceKind().getFilter();
            try {
                String nameInGrammar =
                    getNameInGrammar(
                        filter.stripExtension(selectedFile.getCanonicalPath()),
                        false);
                if (nameInGrammar == null) {
                    // store as external file
                    ControlModel.store(text, new FileOutputStream(selectedFile));
                } else {
                    // store in grammar
                    result = doSaveText(nameInGrammar, text);
                }
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while writing %s to %s",
                    getResourceKind().getDescription(), selectedFile);
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        boolean enabled;
        ResourceKind resource = getResourceKind();
        if (getEditor() == null) {
            if (isForState()) {
                enabled = getSimulatorModel().hasState();
            } else {
                enabled = getSimulatorModel().isSelected(resource);
            }
        } else {
            enabled = getEditor().isDirty();
        }
        setEnabled(enabled);
        String name =
            isForState() ? Options.getSaveStateActionName(this.saveAs)
                    : Options.getSaveActionName(resource, this.saveAs);
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
    }

    /**
     * Invokes a file chooser of the right type to save the resource kind
     * of this action,
     * and returns the chosen (possibly {@code null}) file.
     */
    private File askSave(String name) {
        ExtensionFilter filter = getResourceKind().getFilter();
        GrooveFileChooser chooser = GrooveFileChooser.getFileChooser(filter);
        chooser.setSelectedFile(new File(name));
        return SaveDialog.show(chooser, getFrame(), null);
    }

    /** Returns the currently selected editor tab on the appropriate display, if any. */
    private EditorTab getEditor() {
        return getDisplay().getSelectedEditor();
    }

    private boolean isForState() {
        return getResourceKind() == ResourceKind.HOST
            && !getSimulatorModel().hasHost();
    }

    private final boolean saveAs;
}