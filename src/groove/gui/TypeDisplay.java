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

import groove.gui.SimulatorModel.Change;
import groove.io.HTMLConverter;
import groove.trans.ResourceKind;
import groove.view.TypeModel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

/**
 * Panel that holds the type display and type graph editors.
 * @author Arend Rensink
 * @version $Revision $
 */
final public class TypeDisplay extends GraphDisplay implements
        SimulatorListener {
    /**
     * Constructs a panel for a given simulator.
     */
    public TypeDisplay(Simulator simulator) {
        super(simulator, ResourceKind.TYPE);
        installListeners();
    }

    @Override
    protected void installListeners() {
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.TYPE);
        super.installListeners();
    }

    @Override
    public TypePanel getMainTab() {
        return getTypePanel();
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (!suspendListening()) {
            return;
        }
        if (changes.contains(Change.GRAMMAR)) {
            getMainTab().updateGrammar(source.getGrammar());
        }
        if (changes.contains(Change.GRAMMAR) || changes.contains(Change.TYPE)) {
            TypeModel type = source.getType();
            setSelected(type == null ? null : type.getName());
            getEnableButton().setSelected(type != null && type.isEnabled());
        }
        activateListening();
    }

    @Override
    protected TypeModel getResource(String name) {
        return getSimulatorModel().getGrammar().getTypeModel(name);
    }

    /**
     * Creates and returns the panel with the type graphs list.
     */
    public JPanel getListPanel() {
        if (this.listPanel == null) {

            JScrollPane typesPane = new JScrollPane(getList()) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension superSize = super.getPreferredSize();
                    return new Dimension((int) superSize.getWidth(),
                        Simulator.START_LIST_MINIMUM_HEIGHT);
                }
            };

            this.listPanel = new JPanel(new BorderLayout(), false);
            this.listPanel.add(createListToolBar(), BorderLayout.NORTH);
            this.listPanel.add(typesPane, BorderLayout.CENTER);
            // make sure tool tips get displayed
            ToolTipManager.sharedInstance().registerComponent(this.listPanel);
        }
        return this.listPanel;
    }

    /** Creates a tool bar for the types list. */
    @Override
    protected JToolBar createListToolBar() {
        JToolBar result = super.createListToolBar();
        return result;
    }

    /** Returns the list of states and host graphs. */
    public TypeJList getList() {
        if (this.typeJList == null) {
            this.typeJList = new TypeJList(this);
        }
        return this.typeJList;
    }

    /** Returns the default type panel displayed on this pane. */
    public final TypePanel getTypePanel() {
        if (this.typePanel == null) {
            this.typePanel = new TypePanel(getSimulator());
        }
        return this.typePanel;
    }

    @Override
    protected void selectionChanged() {
        getSimulatorModel().setType(getSelectedName());
    }

    @Override
    protected void decorateLabelText(String name, StringBuilder text) {
        super.decorateLabelText(name, text);
        if (getResource(name).isEnabled()) {
            HTMLConverter.STRONG_TAG.on(text);
            HTMLConverter.HTML_TAG.on(text);
        } else {
            text.insert(0, "(");
            text.append(")");
        }
    }

    private TypePanel typePanel;
    /** panel on which the type list list (and toolbar) are displayed. */
    private JPanel listPanel;

    /** Production system type list */
    private TypeJList typeJList;
}
