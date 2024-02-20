/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.tree;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/** Superclass for tree nodes in a display-related list. */
class DisplayTreeNode extends DefaultMutableTreeNode {
    /** Constructor for an empty node. */
    DisplayTreeNode() {
        // empty
    }

    /** Constructs a node. */
    DisplayTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    /** Returns the icon to be used when rendering this tree node. */
    Icon getIcon() {
        return null;
    }

    /** Indicates if this tree node contains an error. */
    boolean isError() {
        return false;
    }

    /** Indicates if this tree node represents part of a recipe. */
    boolean isInternal() {
        return false;
    }

    /** Returns the current status of this tree node. */
    Status getStatus() {
        return Status.ACTIVE;
    }

    /** Returns the text to be displayed on the tree node. */
    String getText() {
        return toString();
    }

    /** Returns the tooltip to be used when rendering this tree node. */
    String getTip() {
        return null;
    }

    /**
     * Insert child node using a sorting based on the name of the child node (toString)
     * Uses a natural ordering sort
     * @param child Child node to insert.
     */
    void insertSorted(MutableTreeNode child) {
        // binary search for the right position to insert
        int lower = 0;
        int upper = getChildCount();
        while (lower < upper) {
            int mid = (lower + upper) / 2;
            TreeNode midChild = getChildAt(mid);
            if (child.toString().compareTo(midChild.toString()) < 0) {
                upper = mid;
            } else {
                lower = mid + 1;
            }
        }
        insert(child, lower);
    }

    /** Diaplay status of a tree node. */
    static enum Status {
        /** Not considered for inclusion. */
        DISABLED,
        /** Enabled but not currently active. */
        STANDBY,
        /** Currently active. */
        ACTIVE;

        /** Indicates if this status is not {@link Status#DISABLED}. */
        public boolean isEnabled() {
            return this != DISABLED;
        }
    }
}
