package groove.gui.action;

import groove.gui.EditorPanel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.view.aspect.AspectGraph;

import java.io.IOException;

import javax.swing.Action;

/**
 * Action to save the graph (host graph, rule graph of type graph) in
 * an editor panel.
 * @author Arend Rensink
 * @version $Revision $
 */
public final class SaveGraphAction extends SimulatorAction {
    /** Creates an instance of the action for a given editor panel. */
    public SaveGraphAction(EditorPanel editor) {
        super(editor.getSimulator(), Options.SAVE_ACTION_NAME, Icons.SAVE_ICON);
        putValue(Action.ACCELERATOR_KEY, Options.SAVE_KEY);
        this.editor = editor;
    }

    /**
     * Saves the editor if it is dirty.
     * @return {@code true} if the simulation was invalidated as a 
     * consequence of this save action
     */
    public boolean doSave() {
        boolean result = false;
        if (this.editor.isDirty()) {
            AspectGraph graph = this.editor.getGraph();
            try {
                switch (graph.getRole()) {
                case HOST:
                    result = getModel().doAddHost(graph);
                    break;
                case RULE:
                    result = getModel().doAddRule(graph);
                    break;
                case TYPE:
                    result = getModel().doAddType(graph);
                    break;
                }
                this.editor.setDirty(false);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error while saving edited graph '%s'",
                    graph.getName());
            }
        }
        return result;
    }

    @Override
    public boolean execute() {
        boolean result = false;
        if (this.editor.isDirty()
            && confirmBehaviourOption(Options.STOP_SIMULATION_OPTION)) {
            result = doSave();
        }
        return result;
    }

    private final EditorPanel editor;
}