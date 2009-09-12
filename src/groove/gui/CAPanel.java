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
 * $Id: CAPanel.java,v 1.18 2008-03-18 12:18:19 fladder Exp $
 */
package groove.gui;

import groove.control.ControlAutomaton;
import groove.control.ControlView;
import groove.control.parse.GCLTokenMaker;
import groove.gui.jgraph.ControlJGraph;
import groove.gui.jgraph.ControlJModel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.RuleMatch;
import groove.trans.RuleName;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.view.DefaultGrammarView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * The Simulator panel that shows the control program, with a buttton that shows
 * the corresponding control automaton.
 * 
 * @author Tom Staijen
 * @version $0.9$
 */
public class CAPanel extends JPanel implements SimulationListener {

    /**
     * @param simulator The Simulator the panel is added to.
     */
    public CAPanel(Simulator simulator) {
        super();
        this.simulator = simulator;

        // create the layout for this JPanel
        this.setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar("Control");

        this.nameLabel = new JLabel("Name: ");
        toolBar.add(this.nameLabel);

        this.nameField = new JTextField();
        this.nameField.setEnabled(false);
        this.nameField.setEditable(false);
        this.nameField.setBorder(BorderFactory.createLoweredBevelBorder());
        this.nameField.setMaximumSize(new Dimension(150, 24));
        toolBar.add(this.nameField);
        toolBar.add(new JLabel(" "));

        this.editButton = new JButton("Edit");
        toolBar.add(this.editButton);
        this.editButton.setEnabled(false);
        this.editButton.addActionListener(new EditButtonListener());

        this.doneButton = new JButton("Save");
        toolBar.add(this.doneButton);
        this.doneButton.addActionListener(new DoneButtonListener());
        this.doneButton.setEnabled(false);

        this.cancelButton = new JButton("Cancel");
        toolBar.add(this.cancelButton);
        this.cancelButton.addActionListener(new CancelButtonListener());
        this.cancelButton.setEnabled(false);

        this.viewButton = new JButton(Groove.GRAPH_MODE_ICON);
        int preferredHeight =
            (int) this.cancelButton.getPreferredSize().getHeight();
        this.viewButton.setMaximumSize(new Dimension(
            (int) this.viewButton.getPreferredSize().getWidth(),
            preferredHeight));
        toolBar.add(this.viewButton);
        this.viewButton.addActionListener(new ViewButtonListener());
        this.viewButton.setEnabled(false);

        this.toggleButton = new JButton("<Toggle>");
        toolBar.add(this.toggleButton);
        this.toggleButton.setEnabled(false);
        this.toggleButton.addActionListener(new ToggleButtonListener());

        RSyntaxDocument document = new RSyntaxDocument("gcl");
        document.setSyntaxStyle(new GCLTokenMaker());

        this.textPanel = new RSyntaxTextArea(document);

        RTextScrollPane scroller =
            new RTextScrollPane(500, 400, this.textPanel, true);

        this.textPanel.setText("");
        this.textPanel.setEditable(false);
        this.textPanel.setEnabled(false);
        this.textPanel.setBackground(DISABLED_COLOUR);

        this.add(toolBar, BorderLayout.NORTH);
        this.add(scroller, BorderLayout.CENTER);

        simulator.addSimulationListener(this);
    }

    /**
     * We do nothing when a transition is applied
     */
    public void applyTransitionUpdate(GraphTransition transition) {
        // // do nothing
    }

    public void setGrammarUpdate(DefaultGrammarView grammar) {
        ControlView controlView = grammar.getControlView();
        boolean controlLoaded = controlView != null;
        String controlName =
            controlLoaded ? controlView.getName()
                    : grammar.getProperties().getControlName();
        boolean controlEnabled =
            grammar.getProperties().isUseControl() && controlName != null;
        this.nameField.setText(" "
            + (controlName == null ? Groove.DEFAULT_CONTROL_NAME : controlName));
        this.nameField.setEditable(controlEnabled);
        this.toggleButton.setText(controlEnabled ? "Disable" : "Enable");
        this.toggleButton.setEnabled(true);
        this.editButton.setEnabled(grammar.getProperties().isUseControl());
        this.viewButton.setEnabled(controlEnabled);

        setText();
        this.textPanel.setEnabled(controlEnabled);
        this.textPanel.setBackground(controlEnabled ? ENABLED_COLOUR
                : DISABLED_COLOUR);
    }

    /**
     * Displays the control program on the text panel. Displays a special
     * message if no control is loaded.
     */
    private void setText() {
        ControlView cv = getSimulator().getGrammarView().getControlView();
        if (cv != null) {
            // in any case display the program
            this.textPanel.setText(cv.getProgram());
        } else {
            this.textPanel.setText("No control program set.");
        }
    }

    public void setRuleUpdate(RuleName name) {
        // nothing happens
    }

    public void setStateUpdate(GraphState state) {
        // nothing happens
    }

    public void setMatchUpdate(RuleMatch match) {
        // nothing happens
    }

    public void setTransitionUpdate(GraphTransition transition) {
        // nothing happens
    }

    public void startSimulationUpdate(GTS gts) {
        // nothing happens
    }

    /** Returns the simulator to which the control panel belongs. */
    private Simulator getSimulator() {
        return this.simulator;
    }

    /** Simulator to which the control panel belongs. */
    private final Simulator simulator;
    /** Name label of the control program. */
    private final JLabel nameLabel;
    /** Name field of the control program. */
    private final JTextField nameField;
    /** Panel showing the control program. */
    private final RSyntaxTextArea textPanel;
    /** Button to start editing the control program. */
    private final JButton editButton;
    /** Button to stop editing the control program. */
    private final JButton doneButton;
    /** Button to cancel editing the control program. */
    private final JButton cancelButton;
    /** Button to get an automaton view of the program. */
    private final JButton viewButton;
    /** Button to enable or disable the control program. */
    private final JButton toggleButton;

    private class DoneButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String program = CAPanel.this.textPanel.getText();
            // if (program == null || program.length() == 0) {
            // return;
            // }
            if (getSimulator().handleSaveControl(program) != null) {
                getSimulator().doRefreshGrammar();
                // CAPanel.this.textPanel.setEditable(false);
                // CAPanel.this.textPanel.setEnabled(false);
                // CAPanel.this.editButton.setEnabled(true);
                // CAPanel.this.doneButton.setEnabled(false);
                // CAPanel.this.cancelButton.setEnabled(false);
            }
        }
    }

    private class CancelButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            setText();
            CAPanel.this.textPanel.setEditable(false);
            CAPanel.this.textPanel.setEnabled(false);
            CAPanel.this.editButton.setEnabled(true);
            CAPanel.this.doneButton.setEnabled(false);
            CAPanel.this.cancelButton.setEnabled(false);
        }
    }

    private class ToggleButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            SystemProperties oldProperties =
                getSimulator().getGrammarView().getProperties();
            SystemProperties newProperties = oldProperties.clone();
            newProperties.setProperty(SystemProperties.CONTROL_KEY,
                new Boolean(!oldProperties.isUseControl()).toString());
            getSimulator().doSaveProperties(newProperties);
            // if control just got enabled but no control program exists,
            // we create an empty control program
            if (newProperties.isUseControl()
                && getSimulator().getGrammarView().getClass() == null
                && getSimulator().handleSaveControl("") != null) {
                getSimulator().doRefreshGrammar();
            } else {
                // since we may be in the middle of an edit action,
                // let's assume editing was cancelled.
                setText();
                CAPanel.this.textPanel.setEditable(false);
                CAPanel.this.textPanel.setEnabled(false);
                CAPanel.this.editButton.setEnabled(true);
                CAPanel.this.doneButton.setEnabled(false);
                CAPanel.this.cancelButton.setEnabled(false);
            }
        }
    }

    private class EditButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Clear the text panel of the special message text
            if (getSimulator().getGrammarView().getControlView() == null) {
                CAPanel.this.textPanel.setText("");
            }
            CAPanel.this.textPanel.setEditable(true);
            CAPanel.this.textPanel.setEnabled(true);
            CAPanel.this.editButton.setEnabled(false);
            CAPanel.this.cancelButton.setEnabled(true);
            CAPanel.this.doneButton.setEnabled(true);
        }
    }

    /**
     * Creates a dialog showing the control automaton
     * @author Tom Staijen
     * @version $Revision $
     */
    private class ViewButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            assert getSimulator().getGrammarView().getErrors().size() == 0 : "View Button should be disabled if grammar has errors.";

            ControlAutomaton caut =
                getSimulator().getGrammarView().getControlView().getAutomaton();
            ControlJGraph cjg =
                new ControlJGraph(new ControlJModel(caut,
                    getSimulator().getOptions()));

            AutomatonPanel autPanel =
                new AutomatonPanel(CAPanel.this.simulator, cjg);

            JDialog jf =
                new JDialog(getSimulator().getFrame(), "Control Automaton");
            jf.add(autPanel);
            jf.setSize(600, 700);
            Point p = getSimulator().getFrame().getLocation();
            jf.setLocation(new Point(p.x + 50, p.y + 50));
            jf.setVisible(true);

            cjg.getLayouter().start(true);

        }
    }

    private class AutomatonPanel extends JGraphPanel<ControlJGraph> {
        /**
         * The constructor of this panel creates a panel with the Control
         * Automaton of the current grammar.
         * @param simulator
         */
        public AutomatonPanel(Simulator simulator, ControlJGraph graph) {
            super(graph, true, simulator.getOptions());
            this.getJGraph().setConnectable(false);
            this.getJGraph().setDisconnectable(false);
            this.getJGraph().setEnabled(true);
            getJGraph().setToolTipEnabled(true);
        }

        @Override
        public ControlJModel getJModel() {
            return (ControlJModel) super.getJModel();
        }
    }

    private static JTextField enabledField = new JTextField();
    private static JTextField disabledField = new JTextField();
    static {
        enabledField.setEditable(true);
        disabledField.setEditable(false);
    }
    /** The background colour of an enabled component. */
    private static Color ENABLED_COLOUR = enabledField.getBackground();
    /** The background colour of a disabled component. */
    private static Color DISABLED_COLOUR = disabledField.getBackground();
}
