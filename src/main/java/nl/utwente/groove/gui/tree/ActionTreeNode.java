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

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import nl.utwente.groove.grammar.QualName;

/**
 * Tree node wrapping an action.
 * @author Arend Rensink
 * @version $Revision$
 */
interface ActionTreeNode extends MutableTreeNode {
    /** Returns the action name. */
    public QualName getQualName();

    /** Indicates if the wrapped action is a property. */
    public boolean isProperty();

    /** Returns the current status of this tree node. */
    public DisplayTreeNode.Status getStatus();

    /** Sets the tried state of the action wrapped by this node. */
    public void setTried(boolean tried);

    public TreeNode[] getPath();
}
