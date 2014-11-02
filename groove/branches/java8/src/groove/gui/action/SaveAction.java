package groove.gui.action;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.Resource;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.Text;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.display.ResourceTab;
import groove.io.FileType;
import groove.io.graph.GxlIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        super(simulator, saveAs ? Options.SAVE_AS_ACTION_NAME : Options.SAVE_ACTION_NAME, saveAs
            ? Icons.SAVE_AS_ICON : Icons.SAVE_ICON, null, resource);
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
            String name = getSimulatorModel().getSelected(resourceKind);
            ResourceTab editor = getDisplay().getEditor(name);
            Resource resource;
            boolean minor;
            if (editor == null) {
                resource = getGrammarStore().get(resourceKind).get(name);
                minor = true;
            } else {
                resource = editor.getResource();
                minor = editor.isDirtMinor();
            }
            saved = this.saveAs ? doSaveAs(resource) : doSave(resource, minor);
            if (editor != null && saved) {
                editor.setClean();
            }
        }
    }

    /**
     * Stores the resource within the grammar.
     * @param resource the resource to be saved
     * @param minor if {@code true}, this is a minor change
     * @return {@code true} if the action succeeded
     */
    public boolean doSave(Resource resource, boolean minor) {
        boolean result = false;
        if (isForPrologProgram() && endsInGarbage(((Text) resource).getContent())) {
            showErrorDialog(null,
                "The last non-empty character of a Prolog program must be a dot: '.'");
        } else {
            try {
                getSimulatorModel().doAdd(resource, minor);
                result = true;
            } catch (IOException exc) {
                showErrorDialog(exc,
                    "Error while saving %s '%s'",
                    getResourceKind().getDescription(),
                    resource.getName());
            }
        }
        return result;
    }

    /** Attempts to write the graph to an external file.
     * @return {@code true} if the graph was saved within the grammar
     */
    public boolean doSaveAs(Resource resource) {
        boolean result = false;
        Path selectedFile = askSaveResource(resource.getName());
        // now save, if so required
        if (selectedFile != null) {
            try {
                String nameInGrammar = getNameInGrammar(selectedFile);
                if (nameInGrammar == null) {
                    writeFile(selectedFile, resource);
                } else {
                    // save within the grammar
                    result = doSave(resource.rename(nameInGrammar), false);
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

    private void writeFile(Path file, Resource resource) throws IOException {
        if (resource.getKind().isGraphBased()) {
            AspectGraph graph = (AspectGraph) resource;
            FileType fileType = getResourceKind().getFileType();
            // save in external file
            String newName = fileType.stripExtension(file.getFileName().toString());
            GxlIO.instance().saveGraph(graph.rename(newName).toPlainGraph(), file);
        } else {
            Text text = (Text) resource;
            Files.write(file, text.getLines());
        }
    }

    @Override
    public void refresh() {
        boolean enabled = false;
        ResourceKind resource = getResourceKind();
        if (isForState()) {
            enabled = getSimulatorModel().hasState();
        } else {
            String name = getSimulatorModel().getSelected(resource);
            if (name != null) {
                ResourceTab editor = getDisplay().getEditor(name);
                enabled = this.saveAs || editor != null && editor.isDirty();
            }
        }
        setEnabled(enabled);
        String name =
            isForState() ? Options.getSaveStateActionName(this.saveAs)
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