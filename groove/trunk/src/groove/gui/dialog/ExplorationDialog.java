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
import groove.explore.Documented;
import groove.explore.Enumerator;
import groove.explore.StrategyEnumerator;
import groove.explore.result.Acceptor;
import groove.explore.strategy.Strategy;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.layout.SpringUtilities;

import java.awt.CardLayout;
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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Dialog that allows the user to compose an exploration out of a strategy, an
 * acceptor and a result. The strategy is selected from one of the options in a
 * StrategyEnumeration. The acceptor is selected from one of the options in a
 * StrategyEnumeration.
 * 
 * @author Maarten de Mol
 * @version $Revision $
 */
public class ExplorationDialog extends JDialog implements ActionListener {
    // Button Texts
    private final String EXPLORE_COMMAND = "Run";
    private final String CANCEL_COMMAND = "Cancel";

    // Tool Tip Texts
    private final String STRATEGY_TOOLTIP =
        "<HTML>" + "The exploration strategy determines at each state:<BR>"
            + "<B>1.</B> Which of the applicable transitions will be taken; "
            + "and<BR>"
            + "<B>2.</B> In which order the reached states will be explored."
            + "</HTML>";
    private final String ACCEPTOR_TOOLTIP =
        "<HTML>"
            + "An acceptor is a predicate that is applied each time the LTS is "
            + "updated<I>*</I>.<BR>"
            + "Information about each acceptor success is added to the result "
            + "set of the exploration.<BR>"
            + "This result set can be used to interrupt exploration.<BR>"
            + "<I>(*)<I>The LTS is updated when a transition is applied, or "
            + "when a new state is reached." + "</HTML>";
    private final String RESULT_TOOLTIP =
        "<HTML>"
            + "Exploration can be interrupted between atomic steps of the "
            + "strategy.<BR> "
            + "The size of the atomic steps depends on the chosen "
            + "strategy.<BR> "
            + "The interruption condition is determined by the indicated "
            + "number of times that the acceptor succeeds." + "</HTML>";
    private final String EXPLORE_TOOLTIP =
        "<HTML>" + "Run the customized exploration, and set it as the default."
            + "</HTML>";

    // Colors.
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

    // The four main panels (strategy / acceptor / result / buttons).
    private DocumentedSelection<Strategy> strategySelector;
    private DocumentedSelection<Acceptor> acceptorSelector;
    private ResultSelection resultSelector;
    private JPanel buttonPanel;

    // Reference to the simulator.
    private Simulator simulator;

    // Memory of the old dismiss delay for tool tips.
    private int oldDismissDelay;

    /**
     * Create the dialog.
     * @param simulator - reference to the simulator
     * @param owner - reference to the parent GUI component
     */
    public ExplorationDialog(Simulator simulator, JFrame owner) {
        // Open a modal dialog, which cannot be resized or closed.
        super(owner, Options.EXPLORATION_DIALOG_ACTION_NAME, true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // Override DismissDelay of the ToolTipManager.
        // Old value will be reset when the dialog is closed.
        this.oldDismissDelay =
            ToolTipManager.sharedInstance().getDismissDelay();
        ToolTipManager.sharedInstance().setDismissDelay(1000000000);

        // Make sure that closeDialog is called whenever the dialog is closed.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                closeDialog();
            }
        });

        // Remember simulator.
        this.simulator = simulator;

        // Create the content panel.
        JPanel dialogContent = new JPanel(new SpringLayout());
        dialogContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));

        // Make sure that the dialog listens to Escape and Enter.
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        dialogContent.registerKeyboardAction(this.escapeListener, escape,
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialogContent.registerKeyboardAction(this.enterListener, enter,
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Create the panel that holds the strategy and acceptor selectors.
        JPanel selectors = new JPanel(new SpringLayout());
        this.strategySelector =
            new DocumentedSelection<Strategy>("exploration strategy",
                this.STRATEGY_TOOLTIP, new StrategyEnumerator(),
                this.simulator.getDefaultExploration().getStrategyKeyword(),
                this.simulator);
        selectors.add(this.strategySelector);
        this.acceptorSelector =
            new DocumentedSelection<Acceptor>("acceptor",
                this.ACCEPTOR_TOOLTIP, new AcceptorEnumerator(),
                this.simulator.getDefaultExploration().getAcceptorKeyword(),
                this.simulator);
        selectors.add(this.acceptorSelector);
        SpringUtilities.makeCompactGrid(selectors, 1, 2, 0, 0, 15, 0);

        // Create the panel that holds the result selector.
        this.resultSelector =
            new ResultSelection(this.RESULT_TOOLTIP,
                this.simulator.getDefaultExploration().getResult().getBound());

        // Add all panels to the dialogContrent.
        dialogContent.add(selectors);
        dialogContent.add(new JLabel(" "));
        dialogContent.add(this.resultSelector);
        dialogContent.add(new JLabel(" "));
        createButtonPanel();
        dialogContent.add(this.buttonPanel);
        SpringUtilities.makeCompactGrid(dialogContent, 5, 1, 0, 0, 0, 0);

        // Add the dialogContent to the dialog.
        add(dialogContent);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    // The close dialog action. Disposes dialog and resets DismissDelay of the
    // ToolTipManager.
    private void closeDialog() {
        this.dispose();
        ToolTipManager.sharedInstance().setDismissDelay(this.oldDismissDelay);
    }

    // The run action. Gets the current selection (strategy, acceptor and
    // result), constructs an exploration out of its, and then runs it.
    // NOTE: simulator.doRunExploration will remember the exploration as the
    // default for the next explore.
    private void doExploration() {
        /*
        Strategy strategy =
            this.strategySelector.getSelectedValue().getObjectForUI(
                this.simulator, this);
        if (strategy == null) {
            return;
        }
        Acceptor acceptor =
            this.acceptorSelector.getSelectedValue().getObjectForUI(
                this.simulator, this);
        if (acceptor == null) {
            return;
        }
        Integer nrResults = this.resultSelector.getSelectedValue();
        if (nrResults == null) {
            return;
        }

        Exploration exploration =
            new Exploration(strategy,
                this.strategySelector.getSelectedValue().getKeyword(),
                acceptor,
                this.acceptorSelector.getSelectedValue().getKeyword(),
                new Result(nrResults));
        */
        closeDialog();
        ToolTipManager.sharedInstance().setDismissDelay(this.oldDismissDelay);
        // this.simulator.doRunExploration(exploration);
    }

    // The action listener of the dialog. Responds to button presses only.
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(this.EXPLORE_COMMAND)) {
            doExploration();
            return;
        }

        if (event.getActionCommand().equals(this.CANCEL_COMMAND)) {
            closeDialog();
            return;
        }
    }

    // Action that responds to Escape. Ensures that the dialog is closed.
    ActionListener escapeListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            closeDialog();
        }
    };

    // Action that responds to Enter. Ensures that the currently selected
    // exploration is executed. 
    ActionListener enterListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            doExploration();
        }
    };

    /**
     * A generic wrapper class that allows elements from an arbitrary
     * DocumentedSelection to be selected by the user.
     */
    private class DocumentedSelection<A> extends JPanel implements
            ListSelectionListener {
        private Enumerator<A> enumerator;
        private Documented<A> currentlySelected;
        private JPanel infoPanel;

        DocumentedSelection(String objectType, String tooltip,
                Enumerator<A> enumerator, String initialValue,
                Simulator simulator) {
            super(new SpringLayout());
            this.enumerator = enumerator;

            initializeInfoPanel(initialValue, simulator);
            this.add(headerText(objectType, tooltip));
            this.add(itemList(initialValue));
            this.add(this.infoPanel);
            SpringUtilities.makeCompactGrid(this, 3, 1, 0, 0, 0, 3);
        }

        private JLabel headerText(String objectType, String tooltip) {
            JLabel headerText =
                new JLabel("<HTML><B><FONT color="
                    + ExplorationDialog.HEADER_COLOR + ">Select " + objectType
                    + ":");
            headerText.setToolTipText(tooltip);
            return headerText;
        }

        private JScrollPane itemList(String initialValue) {
            JList list = new JList(this.enumerator.getAllNames());
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            for (int i = 0; i < this.enumerator.getSize(); i++) {
                if (this.enumerator.getElementAt(i).getKeyword() == initialValue) {
                    list.setSelectedIndex(i);
                    this.currentlySelected = this.enumerator.getElementAt(i);
                }
            }
            list.addListSelectionListener(this);

            JScrollPane listScroller = new JScrollPane(list);
            listScroller.setPreferredSize(new Dimension(300, 200));
            return listScroller;
        }

        // TO DO: Initialize additional arguments properly.
        private void initializeInfoPanel(String initialValue,
                Simulator simulator) {
            this.infoPanel = new JPanel(new CardLayout());
            this.infoPanel.setPreferredSize(new Dimension(300, 200));
            this.infoPanel.setBorder(BorderFactory.createLineBorder(new Color(
                150, 150, 255)));

            for (int i = 0; i < this.enumerator.getSize(); i++) {
                JPanel argumentPanel =
                    this.enumerator.getElementAt(i).getArgumentPanel(simulator);

                JPanel elementPanel = new JPanel(new SpringLayout());
                elementPanel.setBackground(ExplorationDialog.INFO_BG_COLOR);
                elementPanel.add(new JLabel("<HTML><B><U><FONT color="
                    + ExplorationDialog.INFO_COLOR + ">"
                    + this.enumerator.getElementAt(i).getName()
                    + ":</FONT></U></B></HTML>"));
                elementPanel.add(new JLabel("<HTML><FONT color="
                    + ExplorationDialog.INFO_COLOR + ">"
                    + this.enumerator.getElementAt(i).getExplanation()
                    + "</FONT></B></HTML>"));
                elementPanel.add(new JLabel(" "));
                elementPanel.add(new JLabel("<HTML><FONT color="
                    + ExplorationDialog.INFO_COLOR + ">"
                    + "Keyword for command line: <B>"
                    + this.enumerator.getElementAt(i).getKeyword()
                    + "</B></FONT>.</HTML>"));
                elementPanel.add(new JLabel("<HTML><FONT color="
                    + ExplorationDialog.INFO_COLOR + ">"
                    + "Additional arguments required: <B>"
                    + ((argumentPanel == null) ? "No" : "Yes")
                    + "</B>.</FONT></HTML>"));
                elementPanel.add((argumentPanel == null) ? (new JLabel(""))
                        : argumentPanel);

                SpringUtilities.makeCompactGrid(elementPanel, 6, 1, 2, 2, 0, 0);
                this.infoPanel.add(elementPanel,
                    this.enumerator.getElementAt(i).getKeyword());
            }
        }

        private void updateInfoPanel() {

            CardLayout cards = (CardLayout) (this.infoPanel.getLayout());
            cards.show(this.infoPanel, this.currentlySelected.getKeyword());
        }

        public Documented<A> getSelectedValue() {
            return this.currentlySelected;
        }

        public void valueChanged(ListSelectionEvent e) {
            Documented<A> newSelected =
                this.enumerator.findByName((String) ((JList) e.getSource()).getSelectedValue());
            if (newSelected != null) {
                this.currentlySelected = newSelected;
                updateInfoPanel();
            }
        }
    }

    /**
     * The panel where the results can be selected.
     */
    private class ResultSelection extends JPanel implements ActionListener {
        JRadioButton[] checkboxes;
        JTextField customNumber;

        ResultSelection(String tooltip, Integer initialValue) {
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
            this.customNumber.setEnabled(false);

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

        private class OnlyListenToNumbers extends KeyAdapter {
            @Override
            public void keyTyped(KeyEvent evt) {
                char ch = evt.getKeyChar();

                if (!Character.isDigit(ch)) {
                    evt.consume();
                }
            }
        }
    }

    // Create the button panel.
    private void createButtonPanel() {
        this.buttonPanel = new JPanel();

        JButton exploreButton = new JButton(this.EXPLORE_COMMAND);
        exploreButton.addActionListener(this);
        exploreButton.setToolTipText(this.EXPLORE_TOOLTIP);
        this.buttonPanel.add(exploreButton);
        if (this.simulator.getGrammarView() == null
            || this.simulator.getGrammarView().getStartGraphView() == null) {
            exploreButton.setEnabled(false);
        }

        JButton cancelButton = new JButton(this.CANCEL_COMMAND);
        cancelButton.addActionListener(this);
        this.buttonPanel.add(cancelButton);
    }
}
