/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import groove.gui.Simulator;
import groove.gui.display.DisplayKind;
import groove.gui.jgraph.JGraph;
import groove.gui.layout.LayoutKind;
import groove.gui.layout.LayouterItem;
import groove.gui.menu.SetLayoutMenu;

/**
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class LayoutDialog extends JDialog implements ActionListener, WindowFocusListener {

    private static LayoutDialog INSTANCE;

    /** Returns the singleton instance of this dialog. */
    public static LayoutDialog getInstance(Simulator simulator) {
        if (INSTANCE == null) {
            INSTANCE = new LayoutDialog(simulator);
        }
        return INSTANCE;
    }

    private final Simulator simulator;
    private final LayouterItem protoLayouterItems[];
    private final JComboBox<String> layoutBox;
    private final JPanel panel;
    private JGraph<?> jGraph;

    private LayoutDialog(Simulator simulator) {
        super(simulator.getFrame());
        this.setAlwaysOnTop(true);
        this.setTitle("Configure Graph Layout");
        this.simulator = simulator;
        this.protoLayouterItems = new LayouterItem[LayoutKind.values().length];

        this.layoutBox = new JComboBox<>();
        int i = 0;
        for (LayoutKind kind : LayoutKind.values()) {
            this.protoLayouterItems[i] = LayoutKind.getLayouterItemProto(kind);
            this.layoutBox.addItem(this.protoLayouterItems[i].getName());
            i++;
        }
        this.layoutBox.addActionListener(this);
        this.layoutBox.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        this.panel = new JPanel();
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
        this.add(this.panel);
        this.addWindowFocusListener(this);
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        this.refreshJGraph();
        this.refreshPanel(this.layoutBox.getSelectedIndex());
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        // Empty by design.
    }

    /** Makes the dialog visible. */
    public void showDialog() {
        this.setLocationRelativeTo(this.simulator.getFrame());
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.layoutBox.equals(e.getSource())) {
            this.refreshPanel(this.layoutBox.getSelectedIndex());
        }
    }

    private void refreshPanel(int index) {
        this.refreshPanel(this.protoLayouterItems[index]);
    }

    private void refreshPanel(LayouterItem item) {
        if (getJGraph() != null) {
            getLayoutMenu().selectLayoutAction(item).actionPerformed(null);
            LayouterItem layouterItem = (LayouterItem) getJGraph().getLayouter();
            replacePanel(layouterItem.getPanel());
        }
    }

    private void replacePanel(JPanel panel) {
        this.panel.removeAll();
        this.panel.add(this.layoutBox);
        this.panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        if (panel != null) {
            this.panel.add(panel);
        }
        this.panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.pack();
    }

    private SetLayoutMenu getLayoutMenu() {
        return getJGraph() == null ? null : getJGraph().getSetLayoutMenu();
    }

    private void refreshJGraph() {
        DisplayKind display = this.simulator.getModel().getDisplay();
        if (display.isGraphBased()) {
            this.jGraph = this.simulator.getDisplaysPanel().getGraphPanel().getJGraph();
        }
    }

    private JGraph<?> getJGraph() {
        return this.jGraph;
    }

}
