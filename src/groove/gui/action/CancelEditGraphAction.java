package groove.gui.action;

import groove.gui.EditorPanel;
import groove.gui.Icons;
import groove.gui.Options;
import groove.view.aspect.AspectGraph;

import javax.swing.JOptionPane;

/**
 * Action to cancel the editing of a graph.
 * If the editor is dirty, gives the chance to save it.
 * @author Arend Rensink
 * @version $Revision $
 */
public final class CancelEditGraphAction extends SimulatorAction {
    /** Creates an instance of the action for a given editor panel. */
    public CancelEditGraphAction(EditorPanel editor) {
        super(editor.getSimulator(), Options.CANCEL_EDIT_ACTION_NAME,
            Icons.CANCEL_ICON);
        putValue(ACCELERATOR_KEY, Options.CLOSE_KEY);
        this.editor = editor;
    }

    /**
     * If the editor is dirty, asks if it should be saved, and does so if
     * the answer is yes.
     * Disposes the editor if not cancelled, by calling {@link EditorPanel#dispose()}.
     * @return {@code true} if the editor was indeed disposed
     */
    @Override
    public boolean execute() {
        boolean result = true;
        if (this.editor.isDirty()) {
            AspectGraph graph = this.editor.getGraph();
            int confirm =
                JOptionPane.showConfirmDialog(this.editor, String.format(
                    "%s '%s' has been modified. Save changes?",
                    graph.getRole().toString(true), graph.getName()), null,
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.editor.getSaveAction().doSave(graph);
            }
            result = (confirm != JOptionPane.CANCEL_OPTION);
        }
        if (result) {
            this.editor.dispose();
        }
        return result;
    }

    private final EditorPanel editor;
}