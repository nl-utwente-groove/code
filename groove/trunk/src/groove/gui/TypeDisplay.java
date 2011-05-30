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

import groove.gui.DisplaysPanel.DisplayKind;
import groove.gui.SimulatorModel.Change;
import groove.gui.jgraph.AspectJGraph;
import groove.io.HTMLConverter;
import groove.view.TypeView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

/**
 * Panel that holds the type display and type graph editors.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TypeDisplay extends TabbedDisplay implements SimulatorListener {
    /**
     * Constructs a panel for a given simulator.
     */
    public TypeDisplay(Simulator simulator) {
        super(simulator);
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.TYPE);
        installListeners();
    }

    @Override
    public JGraphPanel<AspectJGraph> getMainPanel() {
        return getTypePanel();
    }

    @Override
    public DisplayKind getKind() {
        return DisplayKind.TYPE;
    }

    @Override
    public String getName() {
        TypeView type = getSimulatorModel().getType();
        return type == null ? null : type.getName();
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(Change.GRAMMAR)) {
            clearJModelMap();
        }
        if (changes.contains(Change.GRAMMAR) || changes.contains(Change.TYPE)) {
            TypeView type = source.getType();
            setSelectedTab(type == null ? null : type.getName());
            getEnableButton().setSelected(type != null && type.isEnabled());
        }
        activateListeners();
    }

    @Override
    protected TypeView getView(String name) {
        return getSimulatorModel().getGrammar().getTypeView(name);
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
    private JToolBar createListToolBar() {
        JToolBar result = getSimulator().createToolBar();
        result.add(getActions().getNewTypeAction());
        result.add(getActions().getEditTypeAction());
        result.addSeparator();
        result.add(getActions().getCopyTypeAction());
        result.add(getActions().getDeleteTypeAction());
        result.add(getActions().getRenameTypeAction());
        result.addSeparator();
        result.add(getEnableButton());
        return result;
    }

    /** The type enable button. */
    private JToggleButton getEnableButton() {
        if (this.enableButton == null) {
            this.enableButton =
                new JToggleButton(getActions().getEnableTypeAction());
            this.enableButton.setText(null);
            this.enableButton.setMargin(new Insets(3, 1, 3, 1));
            this.enableButton.setFocusable(false);
        }
        return this.enableButton;
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
    protected void decorateLabelText(String name, StringBuilder text) {
        super.decorateLabelText(name, text);
        if (getView(name).isEnabled()) {
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
    /** The type enable button. */
    private JToggleButton enableButton;
}
