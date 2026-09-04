/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.layout.Layouter;
import nl.utwente.groove.gui.menu.SetLayoutMenu;
import nl.utwente.groove.gui.view.GraphCanvas;

/**
 * Dialog to select and configure one of the backend-contributed layouters
 * of the currently displayed graph canvas.
 * @author Eduardo Zambon
 * @version $Revision$
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
    /** The prototype layouters offered in the combo box, in combo box order. */
    private final List<Layouter> protoLayouters = new ArrayList<>();
    private final JComboBox<String> layoutBox;
    private final JPanel panel;
    private GraphCanvas<?> canvas;

    private LayoutDialog(Simulator simulator) {
        super(simulator.getFrame());
        this.setAlwaysOnTop(true);
        this.setTitle("Configure Graph Layout");
        this.simulator = simulator;

        this.layoutBox = new JComboBox<>();
        this.layoutBox.addActionListener(this);
        this.layoutBox.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        this.panel = new JPanel();
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
        this.add(this.panel);
        this.addWindowFocusListener(this);
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        this.refreshCanvas();
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
        if (index >= 0 && index < this.protoLayouters.size()) {
            this.refreshPanel(this.protoLayouters.get(index));
        }
    }

    private void refreshPanel(Layouter item) {
        if (getCanvas() != null) {
            getLayoutMenu()
                .selectLayoutAction(item)
                .actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "layout"));
            replacePanel(getCanvas().getController().getLayouter().getSettingsPanel());
        }
    }

    private void replacePanel(JComponent panel) {
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
        return getCanvas() == null ? null : getCanvas().getController().getSetLayoutMenu();
    }

    /**
     * Retrieves the canvas of the currently displayed graph panel, if any,
     * and refills the combo box if that canvas offers a different layout palette.
     */
    private void refreshCanvas() {
        DisplayKind display = this.simulator.getModel().getDisplay();
        if (display.isGraphBased()) {
            this.canvas = this.simulator.getDisplaysPanel().getGraphPanel().getJGraph();
            List<Layouter> prototypes = this.canvas.getBackendLayouters();
            if (!prototypes.equals(this.protoLayouters)) {
                // refill the box without triggering the selection action
                this.layoutBox.removeActionListener(this);
                this.layoutBox.removeAllItems();
                this.protoLayouters.clear();
                for (Layouter prototype : prototypes) {
                    this.protoLayouters.add(prototype);
                    this.layoutBox.addItem(prototype.getName());
                }
                this.layoutBox.addActionListener(this);
            }
        }
    }

    private GraphCanvas<?> getCanvas() {
        return this.canvas;
    }

}
