/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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

import groove.util.Groove;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** 
 * The main panel of the simulator.
 * Offers functionality for detaching and reattaching components in 
 * separate windows. 
 */
public class SimulatorPanel extends JTabbedPane {
    /** Constructs a fresh instance, for a given simulator. */
    public SimulatorPanel(final Simulator simulator) {
        add(simulator.getStatePanel(), Icons.GRAPH_FRAME_ICON,
            Icons.GRAPH_FILE_ICON, "Current graph state");
        add(simulator.getRulePanel(), Icons.RULE_FRAME_ICON,
            Icons.RULE_FILE_ICON, "Selected rule");
        add(simulator.getLtsPanel(), Icons.LTS_FRAME_ICON, null,
            "Labelled transition system");
        add(simulator.getControlPanel(), Icons.CONTROL_FRAME_ICON,
            Icons.CONTROL_FILE_ICON, "Control specification");
        add(simulator.getTypePanel(), Icons.TYPE_FRAME_ICON,
            Icons.TYPE_FILE_ICON, "Type graph");
        if (Groove.INCLUDE_PROLOG) {
            add(simulator.getPrologPanel(), Icons.PROLOG_FRAME_ICON, null,
                "Prolog");
        }

        // add the change listener only now, as otherwise the add actions
        // above will trigger it
        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                simulator.refreshActions();
            }
        });
        // adds a mouse listener that offers a popup menu with a detach action
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = indexAtLocation(e.getX(), e.getY());
                if (index >= 0 && e.getButton() == MouseEvent.BUTTON3) {
                    Component panel = getComponentAt(index);
                    createDetachMenu(panel).show(SimulatorPanel.this, e.getX(),
                        e.getY());
                }
            }
        });
        setVisible(true);
    }

    private void add(Component component, ImageIcon tabIcon,
            ImageIcon frameIcon, String title) {
        this.seqNrMap.put(component, this.tabList.size());
        this.tabList.add(component);
        this.tabIconMap.put(component, tabIcon);
        this.frameIconMap.put(component, frameIcon);
        this.titleMap.put(component, title);
        addTab(null, tabIcon, component, title);
    }

    /** Reattaches a component at its proper place. */
    public void attach(Component component) {
        if (component instanceof EditorPanel) {
            add((EditorPanel) component);
        } else {
            int mySeqNr = this.seqNrMap.get(component);
            int index;
            for (index = 0; index < getTabCount(); index++) {
                Integer otherSeqNr = this.seqNrMap.get(getComponentAt(index));
                if (otherSeqNr == null || otherSeqNr > mySeqNr) {
                    // insert here
                    break;
                }
            }
            insertTab(null, this.tabIconMap.get(component), component,
                this.titleMap.get(component), index);
        }
    }

    /** Detaches a component (presumably shown as a tab) into its own window. */
    public void detach(Component component) {
        revertSelection();
        new JGraphWindow(component);
    }

    /** Adds a tab for a given editor panel. */
    public void add(EditorPanel panel) {
        Icon icon = null;
        switch (panel.getGraph().getRole()) {
        case HOST:
            icon = Icons.GRAPH_MODE_ICON;
            break;
        case RULE:
            icon = Icons.RULE_MODE_ICON;
            break;
        case TYPE:
            icon = Icons.TYPE_MODE_ICON;
        }
        addTab("", panel);
        int index = indexOfComponent(panel);
        Component tabComponent =
            new ButtonTabComponent(panel, icon, panel.getTitle());
        setTabComponentAt(index, tabComponent);
        setSelectedIndex(index);
    }

    /** 
     * Returns the tab component of a given editor panel, if it 
     * is currently attached to the simulator panel.
     * @return the tab component, of {@code null} if the editor panel
     * is not attached
     */
    public ButtonTabComponent getTabComponentOf(EditorPanel panel) {
        int index = indexOfComponent(panel);
        return index >= 0 ? (ButtonTabComponent) getTabComponentAt(index)
                : null;
    }

    /** Returns the parent frame of an editor panel, if the editor is not
     * displayed in a tab. */
    public JFrame getFrameOf(EditorPanel panel) {
        if (indexOfComponent(panel) < 0) {
            Container window = panel.getParent();
            while (!(window instanceof JGraphWindow)) {
                window = window.getParent();
            }
            return (JFrame) window;
        } else {
            return null;
        }
    }

    /** Returns a list of all editor panels currently displayed. */
    public List<EditorPanel> getEditors() {
        List<EditorPanel> result = new ArrayList<EditorPanel>();
        for (int i = 0; i < getTabCount(); i++) {
            if (getComponentAt(i) instanceof EditorPanel) {
                result.add((EditorPanel) getComponentAt(i));
            }
        }
        return result;
    }

    /** Creates a popup menu with a detach action for a given component. */
    private JPopupMenu createDetachMenu(final Component component) {
        assert indexOfComponent(component) >= 0;
        JPopupMenu result = new JPopupMenu();
        result.add(new AbstractAction("Detach") {
            @Override
            public void actionPerformed(ActionEvent e) {
                detach(component);
            }
        });
        return result;
    }

    @Override
    public void setSelectedIndex(int index) {
        if (getSelectedIndex() != index) {
            this.lastSelected = getSelectedComponent();
            super.setSelectedIndex(index);
        }
        getSelectedComponent().requestFocusInWindow();
    }

    /** Resets the selected tab to the one before the last call to {@link #setSelectedIndex(int)}. */
    public void revertSelection() {
        setSelectedComponent(this.lastSelected);
    }

    /** List of components that can appear on this panel. */
    private final List<Component> tabList = new ArrayList<Component>();
    private final Map<Component,ImageIcon> tabIconMap =
        new HashMap<Component,ImageIcon>();
    private final Map<Component,Integer> seqNrMap =
        new HashMap<Component,Integer>();
    private final Map<Component,ImageIcon> frameIconMap =
        new HashMap<Component,ImageIcon>();
    private final Map<Component,String> titleMap =
        new HashMap<Component,String>();
    private Component lastSelected;

    /**
     * Independent window wrapping a JGraphPanel.
     * @author Arend Rensink
     * @version $Revision $
     */
    private class JGraphWindow extends JFrame {
        /** Constructs an instance for a given simulator and panel. */
        public JGraphWindow(final Component panel) {
            super(SimulatorPanel.this.titleMap.get(panel));
            getContentPane().add(panel);
            setAlwaysOnTop(true);
            ImageIcon icon = SimulatorPanel.this.frameIconMap.get(panel);
            if (icon != null) {
                setIconImage(icon.getImage());
            }
            if (panel instanceof EditorPanel) {
                setTitle(((EditorPanel) panel).getName());
            }
            pack();
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    attach(panel);
                    super.windowClosing(e);
                }
            });
            setVisible(true);
        }
    }
}
