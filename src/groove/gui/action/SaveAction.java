package groove.gui.action;

import groove.gui.GraphEditorTab;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.ResourceTab;
import groove.gui.Simulator;
import groove.gui.TextTab;
import groove.io.ExtensionFilter;
import groove.io.xml.AspectGxl;
import groove.trans.ResourceKind;
import groove.view.TextBasedModel;
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
        if (!saveAs) {
            putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
        }
        this.saveAs = saveAs;
        this.saveStateAction =
            saveAs ? getActions().getSaveStateAsAction()
                    : getActions().getSaveStateAction();
    }

    @Override
    public void execute() {
        if (isForState()) {
            this.saveStateAction.execute();
        } else {
            boolean saved = false;
            ResourceKind resourceKind = getResourceKind();
            ResourceTab editor = getEditor();
            String name =
                editor == null ? getSimulatorModel().getSelected(resourceKind)
                        : editor.getName();
            if (resourceKind.isGraphBased()) {
                AspectGraph graph;
                if (editor == null) {
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
                    text = ((TextTab) editor).getProgram();
                }
                saved =
                    this.saveAs ? doSaveTextAs(name, text) : doSaveText(name,
                        text);
            }
            if (editor != null && saved) {
                editor.setClean();
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
        try {
            getSimulatorModel().doAddGraph(resource, graph);
            result = true;
        } catch (IOException exc) {
            showErrorDialog(exc, "Error while saving %s '%s'",
                getResourceKind().getDescription(), graph.getName());
        }
        return result;
    }

    /** Attempts to write the graph to an external file. 
     * @return {@code true} if the graph was saved within the grammar
     */
    public boolean doSaveGraphAs(AspectGraph graph) {
        boolean result = false;
        File selectedFile = askSaveResource(graph.getName());
        // now save, if so required
        if (selectedFile != null) {
            try {
                String nameInGrammar = getNameInGrammar(selectedFile);
                if (nameInGrammar == null) {
                    ExtensionFilter filter = getResourceKind().getFilter();
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
                showErrorDialog(exc, "Error while writing %s to '%s'",
                    getResourceKind().getDescription(), selectedFile);
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
        if (isForPrologProgram() && endsInGarbage(text)) {
            showErrorDialog(null,
                "The last non-empty characted of a Prolog program must be a dot: '.'");
        } else {
            try {
                getSimulatorModel().doAddText(getResourceKind(), name, text);
                result = true;
            } catch (IOException exc) {
                showErrorDialog(exc, "Error saving %s '%s'",
                    getResourceKind().getDescription(), name);
            }
        }
        return result;
    }

    /**
     * Saves the text under a given name as a file outside the grammar.
     * @return {@code true} if the text was saved within the grammar
     */
    public boolean doSaveTextAs(String name, String text) {
        boolean result = false;
        File selectedFile = askSaveResource(name);
        // now save, if so required
        if (selectedFile != null) {
            try {
                String nameInGrammar = getNameInGrammar(selectedFile);
                if (nameInGrammar == null) {
                    // store as external file
                    TextBasedModel.store(text, new FileOutputStream(
                        selectedFile));
                } else {
                    // store in grammar
                    result = doSaveText(nameInGrammar, text);
                }
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while writing %s to '%s'",
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

    /** Returns the currently selected editor tab on the appropriate display, if any. */
    private ResourceTab getEditor() {
        return getDisplay().getSelectedEditor();
    }

    private boolean isForState() {
        return getDisplaysPanel().getSelectedDisplay() == getLtsDisplay();
    }

    private boolean isForPrologProgram() {
        return getDisplaysPanel().getSelectedDisplay() == getPrologDisplay();
    }

    private boolean endsInGarbage(String text) {
        int lastDotIndex = text.lastIndexOf(".");
        boolean result = false;
        for (int i = lastDotIndex + 1; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!(c == ' ' || c == '\n' || c == '\r')) {
                result = true;
                break;
            }
        }
        return result;
    }

    private final boolean saveAs;
    private final SaveStateAction saveStateAction;
}