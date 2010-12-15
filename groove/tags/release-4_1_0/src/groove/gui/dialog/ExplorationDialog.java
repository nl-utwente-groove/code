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
package groove.gui.dialog;

import groove.explore.AcceptorEnumerator;
import groove.explore.Exploration;
import groove.explore.StrategyEnumerator;
import groove.explore.encode.EncodedTypeEditor;
import groove.explore.encode.Serialized;
import groove.explore.encode.TemplateList.TemplateListListener;
import groove.explore.result.Acceptor;
import groove.explore.strategy.Strategy;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.layout.SpringUtilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.ToolTipManager;

/**
 * <!=========================================================================>
 * Dialog that allows the user to compose an exploration out of a strategy, an
 * acceptor and a result. The dialog combines the editors from
 * StrategyEnumerator and AcceptorEnumerator, and adds an editor for Result.
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public class ExplorationDialog extends JDialog implements ActionListener,
        TemplateListListener {

    private static final String EXPLORE_COMMAND = "Run";
    private static final String CANCEL_COMMAND = "Cancel";

    private static final String RESULT_TOOLTIP = "<HTML>"
        + "Exploration can be interrupted between atomic steps of the "
        + "strategy.<BR> "
        + "The size of the atomic steps depends on the chosen "
        + "strategy.<BR> "
        + "The interruption condition is determined by the indicated "
        + "number of times that the acceptor succeeds." + "</HTML>";
    private static final String EXPLORE_TOOLTIP = "<HTML>"
        + "Run the customized exploration, and set it as the default."
        + "</HTML>";

    /**
     * Color to be used for headers on the dialog.
     */
    public static final String HEADER_COLOR = "green";
    /**
     * Color to be used for text in the info panel.
     */
    public static final String INFO_COLOR = "#005050";
    /**
     * Color to be used for the background of the info panel.
     */
    public static final Color INFO_BG_COLOR = new Color(230, 230, 255);
    /**
     * Color to be used for the background boxes on the info panel.
     */
    public static final Color INFO_BOX_BG_COLOR = new Color(210, 210, 255);

    private final EncodedTypeEditor<Strategy,Serialized> strategyEditor;
    private final EncodedTypeEditor<Acceptor,Serialized> acceptorEditor;
    private ResultPanel resultPanel;
    private JButton exploreButton;

    private final Simulator simulator;
    private final int oldDismissDelay;

    /**
     * Create the dialog.
     * @param strategyMask - mask for determining the strategies to be displayed
     * @param acceptorMask - mask for determining the acceptors to be displayed
     * @param simulator - reference to the simulator
     * @param owner - reference to the parent GUI component
     */
    public ExplorationDialog(int strategyMask, int acceptorMask,
            Simulator simulator, JFrame owner) {

        // Open a modal dialog, which cannot be resized or closed.
        super(owner, Options.EXPLORATION_DIALOG_ACTION_NAME, true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // Override DismissDelay of the ToolTipManager.
        // Old value will be reset when the dialog is closed.
        this.oldDismissDelay =
            ToolTipManager.sharedInstance().getDismissDelay();
        ToolTipManager.sharedInstance().setDismissDelay(1000000000);

        // Remember the simulator.
        this.simulator = simulator;

        // Make sure that closeDialog is called whenever the dialog is closed.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                closeDialog();
            }
        });

        // Create the content panel.
        JPanel dialogContent = new JPanel(new SpringLayout());
        dialogContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        dialogContent.registerKeyboardAction(createEscapeListener(), escape,
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialogContent.registerKeyboardAction(createEnterListener(), enter,
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Create the explore button (reference is needed when setting the
        // initial value of the (strategy/acceptor) editors.
        this.exploreButton = new JButton(EXPLORE_COMMAND);

        // Create the strategy editor.
        StrategyEnumerator strategyEnumerator = new StrategyEnumerator();
        strategyEnumerator.setMask(strategyMask);
        strategyEnumerator.addListener(this);
        this.strategyEditor = strategyEnumerator.createEditor(simulator);
        Serialized defaultStrategy =
            this.simulator.getDefaultExploration().getStrategy();

        // Create the acceptor editor.
        AcceptorEnumerator acceptorEnumerator = new AcceptorEnumerator();
        acceptorEnumerator.setMask(acceptorMask);
        acceptorEnumerator.addListener(this);
        this.acceptorEditor = acceptorEnumerator.createEditor(simulator);
        Serialized defaultAcceptor =
            this.simulator.getDefaultExploration().getAcceptor();

        // Initialize the editors with the stored default.
        this.strategyEditor.setCurrentValue(defaultStrategy);
        this.acceptorEditor.setCurrentValue(defaultAcceptor);

        // Create the different components and add them to the content panel.
        JPanel selectors = new JPanel(new SpringLayout());
        selectors.add(this.strategyEditor);
        selectors.add(this.acceptorEditor);
        SpringUtilities.makeCompactGrid(selectors, 1, 2, 0, 0, 15, 0);
        dialogContent.add(selectors);
        dialogContent.add(new JLabel(" "));
        dialogContent.add(createResultPanel());
        dialogContent.add(new JLabel(" "));
        dialogContent.add(createButtonPanel(this.exploreButton));
        SpringUtilities.makeCompactGrid(dialogContent, 5, 1, 0, 0, 0, 0);

        // Add the dialogContent to the dialog.
        add(dialogContent);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    /*
     * The close dialog action. Disposes dialog and resets DismissDelay of the
     * ToolTipManager.
     */
    private void closeDialog() {
        this.dispose();
        ToolTipManager.sharedInstance().setDismissDelay(this.oldDismissDelay);
    }

    /*
     * The run action. Gets the current selection (strategy, acceptor and
     * result), constructs an exploration out of its, and then runs it.
     * NOTE: simulator.doRunExploration will remember the exploration as the
     * default for the next explore.
     */
    private void doExploration() {
        Serialized strategy = this.strategyEditor.getCurrentValue();
        Serialized acceptor = this.acceptorEditor.getCurrentValue();
        int nrResults = this.resultPanel.getSelectedValue();
        /*
        if (strategy == null || acceptor == null) {
            return;
        }
        */
        Exploration exploration =
            new Exploration(strategy, acceptor, nrResults);
        closeDialog();
        this.simulator.doRunExploration(exploration);
    }

    /*
     * Action that responds to Escape. Ensures that the dialog is closed.
     */
    private ActionListener createEscapeListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                closeDialog();
            }
        };
    }

    /*
     * Action that responds to Enter. Runs the exploration.
     */
    private ActionListener createEnterListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                doExploration();
            }
        };
    }

    /*
     * The action listener of the dialog. Responds to button presses only.
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(EXPLORE_COMMAND)) {
            doExploration();
            return;
        }

        if (event.getActionCommand().equals(CANCEL_COMMAND)) {
            closeDialog();
            return;
        }
    }

    /*
     * Create the result panel.
     */
    private ResultPanel createResultPanel() {
        this.resultPanel =
            new ResultPanel(RESULT_TOOLTIP,
                this.simulator.getDefaultExploration().getNrResults());
        return this.resultPanel;
    }

    /*
     * Create the button panel.
     */
    private JPanel createButtonPanel(JButton exploreButton) {
        JPanel buttonPanel = new JPanel();
        exploreButton.addActionListener(this);
        exploreButton.setToolTipText(EXPLORE_TOOLTIP);
        buttonPanel.add(exploreButton);
        if (this.simulator.getGrammarView() == null
            || this.simulator.getGrammarView().getStartGraphView() == null) {
            exploreButton.setEnabled(false);
        }
        JButton cancelButton = new JButton(CANCEL_COMMAND);
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    /*
     * <!--------------------------------------------------------------------->
     * A ResultPanel is a panel in which the size of the Result set of the
     * exploration can be selected.
     * <!--------------------------------------------------------------------->
     */
    private static class ResultPanel extends JPanel implements ActionListener {

        JRadioButton[] checkboxes;
        JTextField customNumber;

        /*
         * Create the ResultPanel (constructor).
         */
        public ResultPanel(String tooltip, int initialValue) {
            super(new SpringLayout());

            this.checkboxes = new JRadioButton[3];
            this.checkboxes[0] = new JRadioButton("Infinite (don't interrupt)");
            this.checkboxes[1] =
                new JRadioButton("1 (interrupt as soon as acceptor succeeds)");
            this.checkboxes[2] = new JRadioButton("Custom: ");
            for (int i = 0; i < 3; i++) {
                this.checkboxes[i].addActionListener(this);
            }

            String initialCustomValue = "2";
            if (initialValue == 0) {
                this.checkboxes[0].setSelected(true);
            } else if (initialValue == 1) {
                this.checkboxes[1].setSelected(true);
            } else {
                this.checkboxes[2].setSelected(true);
                initialCustomValue = Integer.toString(initialValue);
            }

            this.customNumber = new JTextField(initialCustomValue, 3);
            this.customNumber.addKeyListener(new OnlyListenToNumbers());
            this.customNumber.setEnabled(initialValue >= 2);

            JLabel leadingLabel =
                new JLabel("<HTML><FONT color="
                    + ExplorationDialog.HEADER_COLOR
                    + "><B>Interrupt exploration when the following number "
                    + "of accepted results have been found: </HTML>");
            leadingLabel.setToolTipText(tooltip);
            this.add(leadingLabel);
            ButtonGroup options = new ButtonGroup();
            JPanel optionsLine = new JPanel(new SpringLayout());
            for (int i = 0; i < 3; i++) {
                optionsLine.add(this.checkboxes[i]);
                if (i < 2) {
                    optionsLine.add(Box.createRigidArea(new Dimension(25, 0)));
                }
                options.add(this.checkboxes[i]);
            }
            optionsLine.add(this.customNumber);
            optionsLine.add(Box.createRigidArea(new Dimension(50, 0)));
            SpringUtilities.makeCompactGrid(optionsLine, 1, 7, 0, 0, 0, 0);
            this.add(optionsLine);

            SpringUtilities.makeCompactGrid(this, 2, 1, 0, 0, 0, 0);
        }

        /*
         * Get the nrResults value that is currently selected.
         */
        public Integer getSelectedValue() {
            if (this.checkboxes[0].isSelected()) {
                return 0;
            }
            if (this.checkboxes[1].isSelected()) {
                return 1;
            }
            if (this.checkboxes[2].isSelected()) {
                return Integer.parseInt(this.customNumber.getText());
            }
            return null;
        }

        /*
         * The actionListener of the ResultPanel. Updates the enabledness of
         * the checkboxes.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == this.checkboxes[0]) {
                this.customNumber.setEnabled(false);
            }

            if (e.getSource() == this.checkboxes[1]) {
                this.customNumber.setEnabled(false);
            }

            if (e.getSource() == this.checkboxes[2]) {
                this.customNumber.setEnabled(true);
            }
        }

        /*
         * KeyAdapter that throws away all non-digit keystrokes.
         */
        private static class OnlyListenToNumbers extends KeyAdapter {
            @Override
            public void keyTyped(KeyEvent evt) {
                char ch = evt.getKeyChar();

                if (!Character.isDigit(ch)) {
                    evt.consume();
                }
            }
        }
    }

    /**
     * Respond to a change of either the selected strategy (keyword) or the
     * selected acceptor (keyword).
     */
    @Override
    public void selectionChanged() {
        Serialized strategy = this.strategyEditor.getCurrentValue();
        Serialized acceptor = this.acceptorEditor.getCurrentValue();
        this.exploreButton.setEnabled(strategy != null && acceptor != null);
    }
}
