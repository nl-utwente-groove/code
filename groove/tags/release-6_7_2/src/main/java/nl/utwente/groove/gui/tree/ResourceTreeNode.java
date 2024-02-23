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

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.gui.display.ResourceDisplay;
import nl.utwente.groove.io.HTMLConverter;

/**
 * A {@link ResourceTreeNode} is a {@link DefaultMutableTreeNode} that
 * corresponds to a resource.
 */
class ResourceTreeNode extends DisplayTreeNode {
    /** Default constructor. */
    ResourceTreeNode(ResourceDisplay display, QualName resourceName) {
        this(display, resourceName, false);
    }

    /** Constructor with an argument that controls the possibility of having children. */
    protected ResourceTreeNode(ResourceDisplay display, QualName resourceName,
                               boolean allowsChildren) {
        super(resourceName, allowsChildren);
        this.display = display;
    }

    /** Returns the resource name. */
    QualName getQualName() {
        return (QualName) getUserObject();
    }

    /** Returns the display with which this tree node is associated. */
    ResourceDisplay getDisplay() {
        return this.display;
    }

    private final ResourceDisplay display;

    /** Returns the icon to be used when rendering this tree node. */
    @Override
    Icon getIcon() {
        return getDisplay().getListIcon(getQualName());
    }

    /** Indicates if this tree node contains an error. */
    @Override
    boolean isError() {
        return getDisplay().hasError(getQualName());
    }

    /** Indicates if this tree node is enabled. */
    @Override
    Status getStatus() {
        return isActivated()
            ? Status.ACTIVE
            : Status.DISABLED;
    }

    /** Returns the text to be displayed on the tree node. */
    @Override
    String getText() {
        return getDisplay().getLabelText(getQualName());
    }

    @Override
    String getTip() {
        StringBuilder result = new StringBuilder();
        switch (getDisplay().getResourceKind()) {
        case HOST:
            result.append("Host graph ");
            break;
        case RULE:
            result.append("Rule ");
            break;
        case TYPE:
            result.append("Type graph ");
            break;
        default:
            // nothing to be done
        }
        result.append(HTMLConverter.STRONG_TAG.on(getQualName()));
        HTMLConverter.HTML_TAG.on(result);
        return result.toString();
    }

    /** Returns the tried state of the resource wrapped by this node. */
    boolean isActivated() {
        return getDisplay().getResource(getQualName()).isActive();
    }
}