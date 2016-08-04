package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.display.GraphEditorTab;

import java.util.HashSet;
import java.util.Set;

/**
 * Action to preview the current type graph.
 */
public class SnapToGridAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public SnapToGridAction(Simulator simulator) {
        super(simulator, Options.SNAP_TO_GRID_NAME, Icons.GRID_ICON);
    }

    @Override
    public void execute() {
        this.snapToGrid = !this.snapToGrid;
        for (GraphEditorTab observer : this.observers) {
            observer.setSnapToGrid();
        }
    }

    /** 
     * Adds a graph editor tab that should be informed of a change in snap status.
     * The action will invoke {@link GraphEditorTab#setSnapToGrid()}
     * upon execution of the action.
     */
    public void addSnapListener(GraphEditorTab editorTab) {
        this.observers.add(editorTab);
    }

    /** Removes a listener. */
    public void removeSnapListener(GraphEditorTab editorTab) {
        this.observers.remove(editorTab);
    }

    /** Returns the current snap status of the action. */
    public boolean getSnap() {
        return this.snapToGrid;
    }

    private final Set<GraphEditorTab> observers =
        new HashSet<>();
    private boolean snapToGrid;
}