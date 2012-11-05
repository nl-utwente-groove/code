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
package groove.gui;

import groove.util.Strings;

import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/** Tree node that adds children using a natural string sorter for their names. */
public class SortingTreeNode extends DefaultMutableTreeNode {
    /** Creates an empty node. */
    protected SortingTreeNode() {
        super();
    }

    /** Creates a new node with a given used object and ability to have children. */
    protected SortingTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
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

    private static class ChildComparator implements Comparator<TreeNode> {
        @Override
        public int compare(TreeNode o1, TreeNode o2) {
            if (o1 instanceof RecipeTreeNode) {
                if (!(o2 instanceof RecipeTreeNode)) {
                    return -1;
                }
            } else if (o2 instanceof RecipeTreeNode) {
                return 1;
            }
            return stringComparator.compare(o1.toString(), o2.toString());
        }

        private final static Comparator<String> stringComparator =
            Strings.getNaturalComparator();
    }
}
