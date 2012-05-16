package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;

import javax.swing.JTree;

/**
 * Action for collapsing a JTree. 
 */
public class CollapseAllAction extends SimulatorAction {
    /** Constructs an instance of the action. */
    public CollapseAllAction(Simulator simulator, JTree tree) {
        super(simulator, Options.COLLAPSE_ALL, Icons.COLLAPSE_ALL_ICON);
        putValue(SHORT_DESCRIPTION, Options.COLLAPSE_ALL);
        this.tree = tree;
    }

    @Override
    public void execute() {
        for (int i = 0; i < this.tree.getRowCount(); i++) {
            if (!this.tree.isCollapsed(i)) {
                this.tree.collapseRow(i);
            }
        }
    }

    @Override
    public void refresh() {
        setEnabled(this.tree.isEnabled());
    }

    private JTree tree;
}