package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.tree.RuleTree;
import groove.gui.tree.TypeTree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

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
        TreeNode root = (TreeNode) this.tree.getModel().getRoot();
        List<DefaultMutableTreeNode> collapsableNodes = new ArrayList<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            TreeNode child = root.getChildAt(i);
            if (isDirectoryNode(child)) {
                for (int j = 0; j < child.getChildCount(); j++) {
                    collapsableNodes.add((DefaultMutableTreeNode) child.getChildAt(j));
                }
            } else {
                collapsableNodes.add((DefaultMutableTreeNode) child);
            }
        }
        for (DefaultMutableTreeNode node : collapsableNodes) {
            TreePath path = new TreePath(node.getPath());
            if (!this.tree.isCollapsed(path)) {
                this.tree.collapsePath(path);
            }
        }
    }

    /** Tests if a given tree node is a directory node, meaning that it
     * should not be collapsed by this action.
     */
    private boolean isDirectoryNode(TreeNode node) {
        return node instanceof RuleTree.DirectoryTreeNode
            || node instanceof TypeTree.TypeGraphTreeNode;
    }

    @Override
    public void refresh() {
        setEnabled(this.tree.isEnabled());
    }

    private JTree tree;
}