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

import groove.grammar.model.ResourceModel;
import groove.gui.display.ResourceDisplay;
import groove.io.HTMLConverter;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A {@link ResourceTreeNode} is a {@link DefaultMutableTreeNode} that
 * corresponds to a resource.
 */
public class ResourceTreeNode extends DisplayTreeNode {
    /** Default constructor. */
    public ResourceTreeNode(ResourceDisplay display, String resourceName) {
        this(display, resourceName, false);
    }

    /** Constructor with an argument that controls the possibility of having children. */
    protected ResourceTreeNode(ResourceDisplay display, String resourceName, boolean allowsChildren) {
        super(resourceName, allowsChildren);
        assert display.getResource(getName()) != null : String.format("No such %s %s",
            display.getResourceKind(),
            resourceName);
        this.display = display;
    }

    /** Returns the resource name. */
    public String getName() {
        return (String) getUserObject();
    }

    /** Returns the icon to be used when rendering this tree node. */
    @Override
    public Icon getIcon() {
        return getDisplay().getListIcon(getName());
    }

    /** Indicates if this tree node contains an error. */
    @Override
    public boolean isError() {
        return getDisplay().hasError(getName());
    }

    /** Indicates if this tree node is enabled. */
    @Override
    public boolean isEnabled() {
        return getDisplay().getResource(getName()).isEnabled();
    }

    /** Returns the text to be displayed on the tree node. */
    @Override
    public String getText() {
        return getDisplay().getLabelText(getName());
    }

    @Override
    public String getTip() {
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
        }
        result.append(HTMLConverter.STRONG_TAG.on(getName()));
        HTMLConverter.HTML_TAG.on(result);
        return result.toString();
    }

    /** Returns the resource model. */
    protected ResourceModel<?> getResource() {
        return getDisplay().getResource(getName());
    }

    /** Returns the display with which this tree node is associated. */
    protected ResourceDisplay getDisplay() {
        return this.display;
    }

    private final ResourceDisplay display;
}