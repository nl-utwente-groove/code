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

import groove.gui.action.EnableAction.UniqueEnableAction;
import groove.io.HTMLConverter;
import groove.trans.ResourceKind;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

/**
 * Panel that holds the state display and host graph editors.
 * @author Arend Rensink
 * @version $Revision $
 */
final public class HostDisplay extends ResourceDisplay {

    // The displayed tree of host graphs.
    private final MyResourceTree tree;

    /**
     * Constructs a panel for a given simulator.
     */
    public HostDisplay(Simulator simulator) {
        super(simulator, ResourceKind.HOST);
        installListeners();
        this.tree = new MyResourceTree();
    }

    @Override
    protected void decorateLabelText(String name, StringBuilder text) {
        super.decorateLabelText(name, text);
        if (getSimulatorModel().getGrammar().isStartGraphComponent(name)) {
            HTMLConverter.STRONG_TAG.on(text);
            HTMLConverter.HTML_TAG.on(text);
        }
    }

    @Override
    protected void resetList() {
        this.tree.suspendListeners();
        super.resetList();
    }

    @Override
    public JComponent createList() {
        return this.tree;
    }

    @Override
    protected JToolBar createListToolBar(int separation) {
        JToolBar result = super.createListToolBar(separation);
        result.add(getUniqueEnableAction());
        return result;
    }

    @Override
    protected JPopupMenu createListPopupMenu(boolean overResource) {
        JPopupMenu result = super.createListPopupMenu(overResource);
        result.add(getUniqueEnableAction());
        return result;
    }

    /** Lazily creates the action for uniquely enabling a host graph. */
    protected final UniqueEnableAction getUniqueEnableAction() {
        if (this.uniqueEnableAction == null) {
            this.uniqueEnableAction = new UniqueEnableAction(getSimulator());
        }
        return this.uniqueEnableAction;
    }

    private class MyResourceTree extends ResourceTree {

        public MyResourceTree() {
            super(HostDisplay.this, ResourceKind.HOST);
        }

        @Override
        public String getDisplayText(String fullName, String shortName) {
            if (getSimulatorModel().getGrammar().isStartGraphComponent(fullName)) {
                StringBuilder text = new StringBuilder(shortName);
                HTMLConverter.STRONG_TAG.on(text);
                HTMLConverter.HTML_TAG.on(text);
                return text.toString();
            } else {
                return shortName;
            }
        }
    }

    /** Action for uniquely enabling a host graph. */
    private UniqueEnableAction uniqueEnableAction;
}
