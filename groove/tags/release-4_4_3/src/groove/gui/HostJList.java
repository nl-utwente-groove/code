/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.gui;

import groove.gui.action.ActionStore;
import groove.gui.jgraph.JAttr;

import java.awt.Component;
import java.awt.Point;
import java.util.Arrays;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

/**
 * @author Tom Staijen
 * @version $Revision $
 */
public class HostJList extends ResourceList {
    /**
     * Creates a new state list viewer.
     */
    protected HostJList(HostDisplay display) {
        super(display);
        this.listModel = new DefaultListModel();
        setModel(this.listModel);
        this.setCellRenderer(new MyCellRenderer());
    }

    /**
     * Creates a popup menu, consisting of "use as start graph" & "preview".
     */
    @Override
    protected JPopupMenu createPopupMenu(Point atPoint) {
        // add rest only if mouse is actually over a graph name
        int index = locationToIndex(atPoint);
        boolean overHost =
            index > 0 && getCellBounds(index, index).contains(atPoint);
        JPopupMenu result = getDisplay().createListPopupMenu(overHost);
        if (index == 0) {
            result.addSeparator();
            result.add(getActions().getBackAction());
            result.add(getActions().getForwardAction());
        }
        return result;
    }

    // -----------------------------------------
    // Methods from SimulationListener Interface
    // -----------------------------------------

    @Override
    protected ListSelectionModel createSelectionModel() {
        return new MySelectionModel();
    }

    @Override
    protected void refreshSelection(Set<String> selection) {
        if (selection.isEmpty()) {
            setSelectedIndex(0);
        } else {
            super.refreshSelection(selection);
        }
    }

    @Override
    protected void refreshList(Set<String> names) {
        Object[] hostNames = names.toArray();
        Arrays.sort(hostNames);
        // turn the selection into a set of names
        this.listModel.clear();
        // add the empty string, later to be replaced by the
        // current state indicator
        this.listModel.addElement("");
        for (int i = 0; i < hostNames.length; i++) {
            Object name = hostNames[i];
            this.listModel.addElement(name);
        }
    }

    /** Returns the simulator to which the state list belongs. */
    private ActionStore getActions() {
        return getDisplay().getActions();
    }

    /** The list model used in this class. */
    private final DefaultListModel listModel;

    /**
     * Cell renderer that distinguishes the name corresponding to the current
     * start graph and to the simulation state indicator.
     */
    private class MyCellRenderer extends ResourceCellRenderer {
        // This is the only method defined by ListCellRenderer.
        // We just reconfigure the JLabel each time we're called.
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) // the list and the cell have the focus
        {
            Component result =
                super.getListCellRendererComponent(list, value, index,
                    isSelected, false);
            // ensure some space to the left of the label
            cellHasFocus = isSelected && HostJList.this.isFocusOwner();
            // set tool tips and special formats
            if (index == 0) {
                // set the first item (the current state indicator) to special
                // format
                if (!isSelected) {
                    // distinguish the current start graph name
                    setBackground(JAttr.STATE_BACKGROUND);
                }
                setToolTipText("Currently selected state of the simulation");
                setIcon(Icons.STATE_MODE_ICON);
                if (getSimulatorModel().hasState()) {
                    setText("Simulation state: "
                        + getSimulatorModel().getState());
                } else {
                    setText(this.START_SIM_TEXT);
                }
            }
            return result;
        }

        private final String FULL_NAME =
            Options.START_SIMULATION_KEY.toString();
        private final String START_KEY_NAME =
            this.FULL_NAME.substring(this.FULL_NAME.indexOf(" ") + 1);
        private final String START_SIM_TEXT = "Press " + this.START_KEY_NAME
            + "to start simulation";
    }

    /**
     * Variation on the selection model that makes sure the first item
     * only gets selected if there is a GTS.
     */
    private class MySelectionModel extends DefaultListSelectionModel {
        @Override
        public void setSelectionInterval(int index0, int index1) {
            if (!getSimulatorModel().hasGts() && index0 == 0) {
                index0 = 1;
            }
            if (index0 <= index1) {
                super.setSelectionInterval(index0, index1);
            }
        }

        @Override
        public void setLeadSelectionIndex(int leadIndex) {
            if (getSimulatorModel().hasGts() || leadIndex != 0) {
                super.setLeadSelectionIndex(leadIndex);
            }
        }
    }
}
