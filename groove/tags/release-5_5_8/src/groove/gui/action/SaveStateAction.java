package groove.gui.action;

import java.io.File;
import java.io.IOException;

import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.ResourceKind;
import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.FileType;
import groove.io.graph.GxlIO;

/**
 * Action to save the currently selected state.
 * @author Arend Rensink
 * @version $Revision $
 */
public final class SaveStateAction extends SimulatorAction {
    /**
     * Creates an instance of the action for a given simulator.
     * @param simulator the editor whose content should be saved
     * @param saveAs flag indicating that the action attempts to save to
     * a file outside the grammar.
     */
    public SaveStateAction(Simulator simulator, boolean saveAs) {
        super(simulator, Options.getSaveStateActionName(saveAs),
            saveAs ? Icons.SAVE_AS_ICON : Icons.SAVE_ICON, null, ResourceKind.HOST);
        if (!saveAs) {
            putValue(ACCELERATOR_KEY, Options.SAVE_KEY);
        }
        this.saveAs = saveAs;
    }

    @Override
    public void execute() {
        AspectGraph graph = getStateDisplay().getStateGraph();
        if (this.saveAs) {
            doSaveAs(graph);
        } else {
            doSave(graph);
        }
    }

    /**
     * Stores the graph within the grammar.
     * @return {@code true} if the action succeeded
     */
    public boolean doSave(AspectGraph graph) {
        boolean result = false;
        QualName newName = askNewName(graph.getName(), true);
        if (newName != null) {
            try {
                getSimulatorModel().doAddGraph(getResourceKind(), graph.rename(newName), false);
                result = true;
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while saving state '%s'", graph.getName());
            }
        }
        return result;
    }

    /** Attempts to write the graph to an external file.
     * @return {@code true} if the graph was saved within the grammar
     */
    public boolean doSaveAs(AspectGraph graph) {
        boolean result = false;
        File selectedFile = askSaveResource(graph.getQualName());
        // now save, if so required
        if (selectedFile != null) {
            try {
                QualName nameInGrammar = getNameInGrammar(selectedFile);
                if (nameInGrammar == null) {
                    // save in external file
                    QualName newName = QualName.name(FileType.getPureName(selectedFile.getName()));
                    GxlIO.instance()
                        .saveGraph(graph.rename(newName)
                            .toPlainGraph(), selectedFile);
                } else {
                    // save within the grammar
                    result = doSave(graph.rename(nameInGrammar));
                }
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while writing state to '%s'", selectedFile);
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().hasState());
    }

    private final boolean saveAs;
}