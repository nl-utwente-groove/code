package groove.gui.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.TextBasedModel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.display.GraphEditorTab;
import groove.gui.display.ResourceTab;
import groove.gui.display.TextTab;
import groove.io.FileType;
import groove.io.graph.GxlIO;

/**
 * Action to save the resource in an editor panel.
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
        super(simulator, saveAs ? Options.SAVE_AS_ACTION_NAME : Options.SAVE_ACTION_NAME,
            saveAs ? Icons.SAVE_AS_ICON : Icons.SAVE_ICON, null, resource);
        if (!saveAs) {
            putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
        }
        this.saveAs = saveAs;
        this.saveStateAction =
            saveAs ? getActions().getSaveStateAsAction() : getActions().getSaveStateAction();
    }

    @Override
    public void execute() {
        if (isForState()) {
            this.saveStateAction.execute();
        } else {
            boolean saved = false;
            ResourceKind resourceKind = getResourceKind();
            QualName name = getSimulatorModel().getSelected(resourceKind);
            ResourceTab editor = getDisplay().getEditor(name);
            if (resourceKind.isGraphBased()) {
                AspectGraph graph;
                boolean minor;
                if (editor == null) {
                    graph = getGrammarStore().getGraphs(resourceKind)
                        .get(name);
                    minor = true;
                } else {
                    graph = ((GraphEditorTab) editor).getGraph();
                    minor = ((GraphEditorTab) editor).isDirtMinor();
                }
                saved = this.saveAs ? doSaveGraphAs(graph) : doSaveGraph(graph, minor);
            } else {
                assert resourceKind.isTextBased();
                String text;
                if (editor == null) {
                    text = getGrammarStore().getTexts(resourceKind)
                        .get(name);
                } else {
                    text = ((TextTab) editor).getProgram();
                }
                saved = this.saveAs ? doSaveTextAs(name, text) : doSaveText(name, text);
            }
            if (editor != null && saved) {
                editor.setClean();
            }
        }
    }

    /**
     * Stores the graph within the grammar.
     * @param graph the graph to be saved
     * @param minor if {@code true}, this is a minor change
     * @return {@code true} if the action succeeded
     */
    public boolean doSaveGraph(AspectGraph graph, boolean minor) {
        boolean result = false;
        ResourceKind resource = ResourceKind.toResource(graph.getRole());
        try {
            getSimulatorModel().doAddGraph(resource, graph, minor);
            result = true;
        } catch (IOException exc) {
            showErrorDialog(exc,
                "Error while saving %s '%s'",
                getResourceKind().getDescription(),
                graph.getName());
        }
        return result;
    }

    /** Attempts to write the graph to an external file.
     * @return {@code true} if the graph was saved within the grammar
     */
    public boolean doSaveGraphAs(AspectGraph graph) {
        boolean result = false;
        File selectedFile = askSaveResource(graph.getQualName());
        // now save, if so required
        if (selectedFile != null) {
            try {
                QualName nameInGrammar = getNameInGrammar(selectedFile);
                if (nameInGrammar == null) {
                    FileType fileType = getResourceKind().getFileType();
                    // save in external file
                    QualName newName =
                        QualName.name(fileType.stripExtension(selectedFile.getName()));
                    GxlIO.instance()
                        .saveGraph(graph.rename(newName)
                            .toPlainGraph(), selectedFile);
                } else {
                    // save within the grammar
                    result = doSaveGraph(graph.rename(nameInGrammar), false);
                }
            } catch (IOException exc) {
                showErrorDialog(exc,
                    "Error while writing %s to '%s'",
                    getResourceKind().getDescription(),
                    selectedFile);
            }
        }
        return result;
    }

    /**
     * Saves the text under a given name in the grammar.
     * @return {@code true} if the action succeeded
     */
    public boolean doSaveText(QualName name, String text) {
        boolean result = false;
        if (isForPrologProgram() && endsInGarbage(text)) {
            showErrorDialog(null,
                "The last non-empty characted of a Prolog program must be a dot: '.'");
        } else {
            try {
                getSimulatorModel().doAddText(getResourceKind(), name, text);
                result = true;
            } catch (IOException exc) {
                showErrorDialog(exc,
                    "Error saving %s '%s'",
                    getResourceKind().getDescription(),
                    name);
            }
        }
        return result;
    }

    /**
     * Saves the text under a given name as a file outside the grammar.
     * @return {@code true} if the text was saved within the grammar
     */
    public boolean doSaveTextAs(QualName name, String text) {
        boolean result = false;
        File selectedFile = askSaveResource(name);
        // now save, if so required
        if (selectedFile != null) {
            try {
                QualName nameInGrammar = getNameInGrammar(selectedFile);
                if (nameInGrammar == null) {
                    // store as external file
                    try (FileOutputStream out = new FileOutputStream(selectedFile)) {
                        TextBasedModel.store(text, out);
                    }
                } else {
                    // store in grammar
                    result = doSaveText(nameInGrammar, text);
                }
            } catch (IOException exc) {
                showErrorDialog(exc,
                    "Error while writing %s to '%s'",
                    getResourceKind().getDescription(),
                    selectedFile);
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        boolean enabled = false;
        ResourceKind resource = getResourceKind();
        if (isForState()) {
            enabled = getSimulatorModel().hasState();
        } else {
            QualName name = getSimulatorModel().getSelected(resource);
            if (name != null) {
                ResourceTab editor = getDisplay().getEditor(name);
                enabled = this.saveAs || editor != null && editor.isDirty();
            }
        }
        setEnabled(enabled);
        String name = isForState() ? Options.getSaveStateActionName(this.saveAs)
            : Options.getSaveActionName(resource, this.saveAs);
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
    }

    private boolean isForState() {
        return getDisplaysPanel().getSelectedDisplay() == getLtsDisplay()
            && getLtsDisplay().isActive();
    }

    private boolean isForPrologProgram() {
        return getDisplaysPanel().getSelectedDisplay() == getPrologDisplay()
            && getPrologDisplay().isActive();
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