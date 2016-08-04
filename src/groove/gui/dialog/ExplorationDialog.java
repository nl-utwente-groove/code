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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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
import javax.swing.WindowConstants;

import groove.explore.AcceptorEnumerator;
import groove.explore.AcceptorValue;
import groove.explore.ExploreType;
import groove.explore.StrategyEnumerator;
import groove.explore.StrategyValue;
import groove.explore.encode.EncodedTypeEditor;
import groove.explore.encode.Serialized;
import groove.explore.encode.TemplateListener;
import groove.explore.result.Acceptor;
import groove.explore.strategy.Strategy;
import groove.grammar.model.GrammarModel;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.SimulatorModel;
import groove.gui.layout.SpringUtilities;
import groove.io.HTMLConverter;
import groove.util.parse.FormatException;

/**
 * <!=========================================================================>
 * Dialog that allows the user to compose an exploration out of a strategy, an
 * acceptor and a result. The dialog combines the editors from
 * StrategyEnumerator and AcceptorEnumerator, and adds an editor for Result.
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public class ExplorationDialog extends JDialog implements TemplateListener {

    private static final String DEFAULT_COMMAND = "Set Default";
    private static final String START_COMMAND = "Start";
    private static final String EXPLORE_COMMAND = "Run";
    private static final String CANCEL_COMMAND = "Cancel";

    private static final String RESULT_TOOLTIP =
        "<HTML>" + "Exploration can be interrupted between atomic steps of the " + "strategy.<BR> "
            + "The size of the atomic steps depends on the chosen " + "strategy.<BR> "
            + "The interruption condition is determined by the indicated "
            + "number of times that the acceptor succeeds." + "</HTML>";
    private static final String START_TOOLTIP = "Restart with the customized exploration";
    private static final String DEFAULT_TOOLTIP =
        "Set the currently selected exploration as the default for this grammar";
    private static final String EXPLORE_TOOLTIP =
        "Run the customized exploration on the currently explored state space";

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

    /**
     * Create the dialog.
     * @param simulator - reference to the simulator
     * @param owner - reference to the parent GUI component
     */
    public ExplorationDialog(Simulator simulator, JFrame owner) {

        // Open a modal dialog, which cannot be resized or closed.
        super(owner, Options.EXPLORATION_DIALOG_ACTION_NAME, true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // Override DismissDelay of the ToolTipManager.
        // Old value will be reset when the dialog is closed.
        this.oldDismissDelay = ToolTipManager.sharedInstance()
            .getDismissDelay();
        ToolTipManager.sharedInstance()
            .setDismissDelay(1000000000);

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
        dialogContent.registerKeyboardAction(createCloseListener(),
            escape,
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialogContent.registerKeyboardAction(createExploreListener(),
            enter,
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Create the strategy editor.
        StrategyEnumerator strategyEnumerator =
            StrategyEnumerator.instance(StrategyValue.DIALOG_STRATEGIES);
        this.strategyEditor = strategyEnumerator.createEditor(getGrammar());
        Serialized defaultStrategy = getSimulatorModel().getExploreType()
            .getStrategy();

        // Create the acceptor editor.
        EnumSet<AcceptorValue> acceptorMask = EnumSet.allOf(AcceptorValue.class);
        acceptorMask.remove(AcceptorValue.CYCLE);
        AcceptorEnumerator acceptorEnumerator = AcceptorEnumerator.instance(acceptorMask);
        this.acceptorEditor = acceptorEnumerator.createEditor(getGrammar());
        Serialized defaultAcceptor = getSimulatorModel().getExploreType()
            .getAcceptor();

        // Initialize the editors with the stored default.
        this.strategyEditor.setCurrentValue(defaultStrategy);
        this.acceptorEditor.setCurrentValue(defaultAcceptor);

        this.strategyEditor.addTemplateListener(this);
        this.acceptorEditor.addTemplateListener(this);

        // Create the different components and add them to the content panel.
        JPanel selectors = new JPanel(new SpringLayout());
        selectors.add(this.strategyEditor);
        selectors.add(this.acceptorEditor);
        SpringUtilities.makeCompactGrid(selectors, 1, 2, 0, 0, 15, 0);
        dialogContent.add(selectors);
        dialogContent.add(new JLabel(" "));
        dialogContent.add(createResultPanel());
        dialogContent.add(new JLabel(" "));
        dialogContent.add(createButtonPanel());
        SpringUtilities.makeCompactGrid(dialogContent, 5, 1, 0, 0, 0, 0);

        // Add the dialogContent to the dialog.
        add(dialogContent);
        pack();
        setLocationRelativeTo(owner);
        this.buttons = createButtons();
        refreshButtons();
        setVisible(true);
    }

    /**
     * The close dialog action. Disposes dialog and resets DismissDelay of the
     * ToolTipManager.
     */
    private void closeDialog() {
        this.dispose();
        ToolTipManager.sharedInstance()
            .setDismissDelay(this.oldDismissDelay);
    }

    /**
     * The start action. Gets the current selection (strategy, acceptor and
     * result), constructs an exploration out of it, and then starts a
     * new exploration for it.
     */
    private void startExploration() {
        getSimulatorModel().resetGTS();
        doExploration();
    }

    /**
     * The run action. Gets the current selection (strategy, acceptor and
     * result), constructs an exploration out of its, and then runs it.
     */
    private void doExploration() {
        try {
            getSimulatorModel().setExploreType(createExploreType());
            closeDialog();
            this.simulator.getActions()
                .getExploreAction()
                .execute();
        } catch (FormatException exc) {
            showError(exc);
        }
    }

    /**
     * Displays an error dialog for a {@link FormatException} that was caused
     * by parsing an (invalid) exploration.
     */
    private void showError(FormatException exc) {
        new ErrorDialog(this.simulator.getFrame(),
            "<HTML><B>Invalid exploration.</B><BR> " + exc.getMessage(), exc).setVisible(true);
    }

    /** Returns an exploration created on the basis of the current settings in this dialog.
     * @return the selected exploration strategy, or {@code null} if no
     * coherent strategy is currently selected
     */
    private ExploreType createExploreType() {
        ExploreType result = null;
        Serialized strategy = this.strategyEditor.getCurrentValue();
        Serialized acceptor = this.acceptorEditor.getCurrentValue();
        if (strategy != null && acceptor != null) {
            int nrResults = this.resultPanel.getSelectedValue();
            result = new ExploreType(strategy, acceptor, nrResults);
        }
        return result;
    }

    /** Sets the currently selected exploration as the default for the
     * grammar.
     */
    private void setDefaultExploreType() {
        try {
            ExploreType exploreType = createExploreType();
            exploreType.test(getGrammar().toGrammar());
            getSimulatorModel().doSetDefaultExploreType(exploreType);
            this.strategyEditor.refresh();
            this.acceptorEditor.refresh();
        } catch (FormatException exc) {
            showError(exc);
        } catch (IOException exc) {
            // do nothing
        }
    }

    /**
     * Action that responds to Escape. Ensures that the dialog is closed.
     */
    private ActionListener createCloseListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                closeDialog();
            }
        };
    }

    /**
     * Action that responds to Enter. Runs the exploration.
     */
    private ActionListener createExploreListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                doExploration();
            }
        };
    }

    /**
     * Creates the result panel.
     */
    private ResultPanel createResultPanel() {
        this.resultPanel = new ResultPanel(RESULT_TOOLTIP, getSimulatorModel().getExploreType()
            .getBound());
        return this.resultPanel;
    }

    /**
     * Creates the button panel.
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(getDefaultButton());
        buttonPanel.add(getStartButton());
        buttonPanel.add(getExploreButton());
        buttonPanel.add(getCancelButton());
        return buttonPanel;
    }

    /**
     * Responds to a change of either the selected strategy (keyword) or the
     * selected acceptor (keyword).
     */
    @Override
    public void templateEdited() {
        refreshButtons();
    }

    private void refreshButtons() {
        ExploreType exploreType = createExploreType();
        for (RefreshButton button : this.buttons) {
            button.refresh(exploreType);
        }
    }

    private List<RefreshButton> createButtons() {
        List<RefreshButton> result = new ArrayList<>();
        result.add(getDefaultButton());
        result.add(getStartButton());
        result.add(getExploreButton());
        result.add(getCancelButton());
        return result;
    }

    /** Initialises and returns the start button. */
    private RefreshButton getDefaultButton() {
        if (this.defaultButton == null) {
            // Create the explore button (reference is needed when setting the
            // initial value of the (strategy/acceptor) editors.
            this.defaultButton = new RefreshButton(DEFAULT_COMMAND) {
                @Override
                public void execute() {
                    setDefaultExploreType();
                }

                @Override
                public void refresh(ExploreType exploreType) {
                    setEnabled(DEFAULT_TOOLTIP, exploreType);
                }
            };
            this.defaultButton.setToolTipText(DEFAULT_TOOLTIP);
        }
        return this.defaultButton;
    }

    /** Initialises and returns the start button. */
    private RefreshButton getStartButton() {
        if (this.startButton == null) {
            // Create the explore button (reference is needed when setting the
            // initial value of the (strategy/acceptor) editors.
            this.startButton = new RefreshButton(START_COMMAND) {
                @Override
                public void execute() {
                    startExploration();
                }

                @Override
                public void refresh(ExploreType exploreType) {
                    setEnabled(START_TOOLTIP, exploreType);
                }
            };
        }
        return this.startButton;
    }

    /** Initialises and returns the explore button. */
    private RefreshButton getExploreButton() {
        if (this.exploreButton == null) {
            // Create the explore button (reference is needed when setting the
            // initial value of the (strategy/acceptor) editors.
            this.exploreButton = new RefreshButton(EXPLORE_COMMAND) {
                @Override
                public void execute() {
                    doExploration();
                }

                @Override
                public void refresh(ExploreType exploreType) {
                    setEnabled(EXPLORE_TOOLTIP, exploreType);
                }
            };
        }
        return this.exploreButton;
    }

    /** Initialises and returns the cancel button. */
    private RefreshButton getCancelButton() {
        if (this.cancelButton == null) {
            // Create the explore button (reference is needed when setting the
            // initial value of the (strategy/acceptor) editors.
            this.cancelButton = new RefreshButton(CANCEL_COMMAND) {
                @Override
                public void execute() {
                    closeDialog();
                }

                @Override
                public void refresh(ExploreType exploreType) {
                    // do nothing
                }
            };
        }
        return this.cancelButton;
    }

    /** Convenience method to retrieve the simulator model. */
    private SimulatorModel getSimulatorModel() {
        return this.simulator.getModel();
    }

    /** Convenience method to retrieve the grammar model. */
    private GrammarModel getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    private final EncodedTypeEditor<Strategy,Serialized> strategyEditor;
    private final EncodedTypeEditor<Acceptor,Serialized> acceptorEditor;
    private ResultPanel resultPanel;
    private RefreshButton defaultButton;
    private RefreshButton startButton;
    private RefreshButton exploreButton;
    private RefreshButton cancelButton;
    private final List<RefreshButton> buttons;
    private final Simulator simulator;
    private final int oldDismissDelay;

    /** Refreshable button class. */
    private abstract class RefreshButton extends JButton {
        /** Constructs a refreshable button with a given button text. */
        public RefreshButton(String text) {
            super(text);
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    execute();
                }
            });
        }

        /** Callback action invoked on button click. */
        public abstract void execute();

        /** Callback action allowing the button to refresh its status. */
        public abstract void refresh(ExploreType exploreType);

        /** Tests if the current grammar is compatible with a given exploration;
         * if so, enables the button, if not, disables it and adds the error text
         * to the tooltip.
         * @param toolTipText bare tooltip text (without error)
         * @param exploreType the exploration strategy
         */
        protected void setEnabled(String toolTipText, ExploreType exploreType) {
            GrammarModel grammar = getGrammar();
            boolean enabled = exploreType != null && grammar != null && !grammar.hasErrors();
            StringBuilder toolTip = new StringBuilder(toolTipText);
            if (enabled) {
                assert exploreType != null && grammar != null;
                try {
                    exploreType.test(grammar.toGrammar());
                } catch (FormatException exc) {
                    enabled = false;
                    toolTip.append(HTMLConverter.HTML_LINEBREAK);
                    toolTip.append(HTMLConverter.EMBARGO_TAG
                        .on(HTMLConverter.toHtml(new StringBuilder(exc.getMessage()))));
                }
            }
            setEnabled(enabled);
            setToolTipText(HTMLConverter.HTML_TAG.on(toolTip)
                .toString());
        }
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
            this.checkboxes[1] = new JRadioButton("1 (interrupt as soon as acceptor succeeds)");
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

            JLabel leadingLabel = new JLabel("<HTML><FONT color=" + ExplorationDialog.HEADER_COLOR
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
}
