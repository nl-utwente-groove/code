/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.gui.tree;

import groove.util.Strings;
import groove.util.collect.Comparator;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/** Superclass for tree nodes in a display-related list. */
public class DisplayTreeNode extends DefaultMutableTreeNode {
    /** Constructor for an empty node. */
    DisplayTreeNode() {
        // empty
    }

    /** Constructs a node. */
    protected DisplayTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    /** Returns the icon to be used when rendering this tree node. */
    public Icon getIcon() {
        return null;
    }

    /** Indicates if this tree node contains an error. */
    public boolean isError() {
        return false;
    }

    /** Indicates if this tree node represents part of a recipe. */
    public boolean isInternal() {
        return false;
    }

    /** Indicates if this tree node is enabled. */
    public boolean isEnabled() {
        return true;
    }

    /** Returns the text to be displayed on the tree node. */
    public String getText() {
        return toString();
    }

    /** Returns the tooltip to be used when rendering this tree node. */
    public String getTip() {
        return null;
    }

    /**
     * Insert child node using a sorting based on the name of the child node (toString)
     * Uses a natural ordering sort
     * @param child Child node to insert.
     */
    public void insertSorted(MutableTreeNode child) {
        Comparator<TreeNode> comparator = this.comparator;
        // binary search for the right position to insert
        int lower = 0;
        int upper = getChildCount();
        while (lower < upper) {
            int mid = (lower + upper) / 2;
            TreeNode midChild = getChildAt(mid);
            if (comparator.compare(child, midChild) < 0) {
                upper = mid;
            } else {
                lower = mid + 1;
            }
        }
        insert(child, lower);
    }

    private final Comparator<TreeNode> comparator = new ChildComparator();

    private static class ChildComparator extends Comparator<TreeNode> {
        @Override
        public int compare(TreeNode o1, TreeNode o2) {
            int result = compare(o1 instanceof RecipeTreeNode, o2 instanceof RecipeTreeNode);
            if (result != 0) {
                return result;
            }
            result = compare(o1 instanceof ActionTreeNode, o2 instanceof ActionTreeNode);
            if (result != 0) {
                return result;
            }
            if (o1 instanceof ActionTreeNode) {
                result =
                    compare(((ActionTreeNode) o2).isProperty(), ((ActionTreeNode) o1).isProperty());
                if (result != 0) {
                    return result;
                }
            }
            return stringComparator.compare(o1.toString(), o2.toString());
        }

        private final static java.util.Comparator<String> stringComparator =
            Strings.getNaturalComparator();
    }
}
