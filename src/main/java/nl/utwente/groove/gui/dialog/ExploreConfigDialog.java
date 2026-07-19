/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.Bound;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreKey;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.explore.config.Frontier;
import nl.utwente.groove.explore.config.Goal;
import nl.utwente.groove.explore.config.Setting;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Dialog that allows the user to compose an exploration by choosing a value
 * for every key of the exploration feature model (see {@link ExploreKey}).
 * The composed {@link ExploreConfig} is realised through
 * {@link ExploreTypeConverter}; feature combinations that are inconsistent or
 * not (yet) realisable disable the exploration buttons, with the errors shown
 * in the dialog and in the button tooltips.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ExploreConfigDialog extends JDialog {
    /**
     * Creates and shows the (modal) dialog.
     * @param simulator reference to the simulator
     * @param owner parent GUI component
     */
    public ExploreConfigDialog(Simulator simulator, JFrame owner) {
        super(owner, Options.EXPLORATION_DIALOG_ACTION_NAME, true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.simulator = simulator;
        this.oldDismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
        ToolTipManager.sharedInstance().setDismissDelay(1000000000);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                closeDialog();
            }
        });
        this.ruleNames = getResourceNames(ResourceKind.RULE);
        this.hostNames = getResourceNames(ResourceKind.HOST);
        this.rows = new EnumMap<>(ExploreKey.class);
        for (var key : ExploreKey.values()) {
            this.rows.put(key, new KeyRow(key));
        }

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content
            .add(createSection("Search order", ExploreKey.NEXT, ExploreKey.SUCCESSOR,
                               ExploreKey.FRONTIER, ExploreKey.HEURISTIC, ExploreKey.COST,
                               ExploreKey.BOUND));
        content
            .add(createSection("Goal and results", ExploreKey.GOAL, ExploreKey.OUTCOME,
                               ExploreKey.RESULT, ExploreKey.COUNT));
        content
            .add(createSection("Engine", ExploreKey.MATCHER, ExploreKey.COLLAPSE,
                               ExploreKey.ALGEBRA, ExploreKey.PERSISTENCE));
        content.add(createPreviewPanel());
        content.add(createButtonPanel());

        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        content
            .registerKeyboardAction(e -> closeDialog(), escape, JComponent.WHEN_IN_FOCUSED_WINDOW);
        content
            .registerKeyboardAction(e -> doExploration(), enter, JComponent.WHEN_IN_FOCUSED_WINDOW);

        loadConfig(createInitialConfig());
        refresh();

        add(content);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    /** Creates a titled section panel with one row per given key. */
    private JPanel createSection(String title, ExploreKey... keys) {
        JPanel result = new JPanel(new java.awt.GridLayout(0, 1, 0, 2));
        result.setBorder(BorderFactory.createTitledBorder(title));
        for (var key : keys) {
            result.add(getRow(key).getPanel());
        }
        return result;
    }

    /** Creates the panel showing the textual form and status of the configuration. */
    private JPanel createPreviewPanel() {
        JPanel result = new JPanel(new BorderLayout(0, 2));
        result.setBorder(BorderFactory.createTitledBorder("Configuration"));
        this.previewField = new JTextField();
        this.previewField.setEditable(false);
        result.add(this.previewField, BorderLayout.NORTH);
        this.statusLabel = new JLabel(" ");
        result.add(this.statusLabel, BorderLayout.SOUTH);
        return result;
    }

    /** Creates the button panel. */
    private JPanel createButtonPanel() {
        JPanel result = new JPanel();
        this.defaultButton = new JButton(DEFAULT_COMMAND);
        this.defaultButton.addActionListener(e -> setDefaultExploreType());
        result.add(this.defaultButton);
        this.startButton = new JButton(START_COMMAND);
        this.startButton.addActionListener(e -> startExploration());
        result.add(this.startButton);
        this.exploreButton = new JButton(EXPLORE_COMMAND);
        this.exploreButton.addActionListener(e -> doExploration());
        result.add(this.exploreButton);
        JButton cancelButton = new JButton(CANCEL_COMMAND);
        cancelButton.addActionListener(e -> closeDialog());
        result.add(cancelButton);
        return result;
    }

    /**
     * Computes the initial configuration: the simulator's current exploration
     * type if it is expressible, otherwise the grammar's default exploration
     * type, otherwise the default configuration (with a notice).
     */
    private ExploreConfig createInitialConfig() {
        try {
            return ExploreTypeConverter.toConfig(getSimulatorModel().getExploreType());
        } catch (FormatException exc) {
            // fall through to the grammar default
        }
        try {
            return ExploreTypeConverter
                .toConfig(getGrammar().getProperties().getExploreType());
        } catch (FormatException exc) {
            this.legacyNotice = "The current exploration strategy cannot be expressed"
                + " in the feature model; showing the default configuration";
            return new ExploreConfig();
        }
    }

    /** Loads a configuration into the dialog widgets. */
    private void loadConfig(ExploreConfig config) {
        this.refreshing = true;
        try {
            for (var key : ExploreKey.values()) {
                getRow(key).loadSetting(config.get(key));
            }
        } finally {
            this.refreshing = false;
        }
    }

    /**
     * Recomputes the configuration from the widgets, applies the dependency
     * rules of the feature model to the widget states, and refreshes the
     * preview, status text and buttons.
     */
    private void refresh() {
        if (this.refreshing) {
            return;
        }
        this.refreshing = true;
        try {
            // dependency rules: an irrelevant key is reset and disabled
            KeyRow next = getRow(ExploreKey.NEXT);
            if (getRow(ExploreKey.FRONTIER).getKind() == Frontier.SINGLE) {
                next.reset();
                next.setEnabled(false);
            } else {
                next.setEnabled(true);
            }
            KeyRow outcome = getRow(ExploreKey.OUTCOME);
            var goal = getRow(ExploreKey.GOAL).getKind();
            if (goal == Goal.NONE || goal == Goal.ANY || goal == Goal.FINAL) {
                outcome.reset();
                outcome.setEnabled(false);
            } else {
                outcome.setEnabled(true);
            }
            for (var row : this.rows.values()) {
                row.refreshContentCard();
            }
            // recompute the configuration and status
            var errors = new FormatErrorSet();
            ExploreConfig config = storeConfig(errors);
            this.previewField
                .setText(config == null || config.unparse().isEmpty()
                    ? "(default configuration)"
                    : config.unparse());
            ExploreType exploreType = null;
            if (config != null && errors.isEmpty()) {
                try {
                    exploreType = ExploreTypeConverter.toExploreType(config);
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
            refreshStatus(exploreType, errors);
        } finally {
            this.refreshing = false;
        }
    }

    /** Reads the configuration from the widgets, collecting content parse errors. */
    private ExploreConfig storeConfig(FormatErrorSet errors) {
        var result = new ExploreConfig();
        for (var key : ExploreKey.values()) {
            try {
                Setting setting = getRow(key).getSetting();
                if (setting != null) {
                    result.put(key, setting);
                }
            } catch (FormatException exc) {
                errors
                    .add("Error in value for '%s': %s", key.getKeyPhrase(),
                         exc.getMessage());
            }
        }
        return result;
    }

    /** Refreshes the status label and the enabling of the buttons. */
    private void refreshStatus(ExploreType exploreType, FormatErrorSet errors) {
        String status;
        boolean runnable = exploreType != null;
        if (!errors.isEmpty()) {
            var text = new StringBuilder("<html><font color='red'>");
            for (var error : errors) {
                text.append(HTMLConverter.toHtml(new StringBuilder(error.toString())));
                text.append("<br>");
            }
            text.append("</font></html>");
            status = text.toString();
        } else if (this.legacyNotice != null) {
            status = "<html><font color='" + INFO_COLOR + "'>" + this.legacyNotice
                + "</font></html>";
            this.legacyNotice = null;
        } else {
            assert exploreType != null;
            status = "<html><font color='" + INFO_COLOR + "'>Runs as: "
                + exploreType.getIdentifier() + "</font></html>";
        }
        this.statusLabel.setText(status);
        // like the old dialog, running additionally requires an error-free grammar
        // that is compatible with the exploration
        boolean explorable = runnable;
        String problem = null;
        GrammarModel grammar = getGrammar();
        if (explorable && grammar.hasErrors()) {
            explorable = false;
            problem = "The grammar has errors";
        }
        if (explorable) {
            try {
                exploreType.test(grammar.toGrammar());
            } catch (FormatException exc) {
                explorable = false;
                problem = exc.getMessage();
            }
        }
        this.defaultButton.setEnabled(runnable);
        this.defaultButton.setToolTipText(DEFAULT_TOOLTIP);
        this.startButton.setEnabled(explorable);
        this.exploreButton.setEnabled(explorable);
        String problemHtml = problem == null
            ? null
            : HTMLConverter.toHtml(new StringBuilder(problem)).toString();
        String exploreTip = problemHtml == null
            ? EXPLORE_TOOLTIP
            : "<html>" + EXPLORE_TOOLTIP + "<br><font color='red'>" + problemHtml
                + "</font></html>";
        String startTip = problemHtml == null
            ? START_TOOLTIP
            : "<html>" + START_TOOLTIP + "<br><font color='red'>" + problemHtml
                + "</font></html>";
        this.startButton.setToolTipText(startTip);
        this.exploreButton.setToolTipText(exploreTip);
    }

    /** Returns the currently composed exploration type, or {@code null} if invalid. */
    private ExploreType createExploreType() {
        var errors = new FormatErrorSet();
        ExploreConfig config = storeConfig(errors);
        if (config == null || !errors.isEmpty()) {
            return null;
        }
        try {
            return ExploreTypeConverter.toExploreType(config);
        } catch (FormatException exc) {
            return null;
        }
    }

    /** Restarts the GTS and runs the composed exploration. */
    private void startExploration() {
        getSimulatorModel().resetGTS();
        doExploration();
    }

    /** Runs the composed exploration on the current state space. */
    private void doExploration() {
        ExploreType exploreType = createExploreType();
        if (exploreType == null) {
            return;
        }
        try {
            getSimulatorModel().setExploreType(exploreType);
            closeDialog();
            this.simulator.getActions().getExploreAction().execute();
        } catch (FormatException exc) {
            new ErrorDialog(this.simulator.getFrame(),
                "<HTML><B>Invalid exploration.</B><BR> " + exc.getMessage(), exc)
                    .setVisible(true);
        }
    }

    /** Stores the composed exploration as the grammar default. */
    private void setDefaultExploreType() {
        ExploreType exploreType = createExploreType();
        if (exploreType == null) {
            return;
        }
        try {
            getSimulatorModel().doSetDefaultExploreType(exploreType);
        } catch (IOException exc) {
            // do nothing
        }
    }

    /** Disposes the dialog and resets the tooltip dismiss delay. */
    private void closeDialog() {
        dispose();
        ToolTipManager.sharedInstance().setDismissDelay(this.oldDismissDelay);
    }

    /** Returns the sorted names of the grammar's resources of a given kind. */
    private List<String> getResourceNames(ResourceKind kind) {
        List<String> result = new ArrayList<>();
        var names = getGrammar().getNames(kind);
        if (names != null) {
            for (var name : new TreeSet<>(names)) {
                result.add(name.toString());
            }
        }
        return result;
    }

    /** Returns the dialog row for a given key. */
    private KeyRow getRow(ExploreKey key) {
        return this.rows.get(key);
    }

    /** Convenience method to retrieve the simulator model. */
    private SimulatorModel getSimulatorModel() {
        return this.simulator.getModel();
    }

    /** Convenience method to retrieve the grammar model. */
    private GrammarModel getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    private final Simulator simulator;
    private final Map<ExploreKey,KeyRow> rows;
    private final List<String> ruleNames;
    private final List<String> hostNames;
    private JTextField previewField;
    private JLabel statusLabel;
    private JButton defaultButton;
    private JButton startButton;
    private JButton exploreButton;
    private boolean refreshing;
    private String legacyNotice;
    private final int oldDismissDelay;

    private static final String DEFAULT_COMMAND = "Set Default";
    private static final String START_COMMAND = "Start";
    private static final String EXPLORE_COMMAND = "Run";
    private static final String CANCEL_COMMAND = "Cancel";
    private static final String DEFAULT_TOOLTIP
        = "Set the currently composed exploration as the default for this grammar";
    private static final String START_TOOLTIP = "Restart with the composed exploration";
    private static final String EXPLORE_TOOLTIP
        = "Run the composed exploration on the currently explored state space";
    /** Colour of informational status text. */
    private static final String INFO_COLOR = "#005050";

    /** Names of the content editor cards. */
    private static final String CARD_NONE = "none";
    private static final String CARD_TEXT = "text";
    private static final String CARD_NAMES = "names";

    /**
     * A dialog row for a single exploration key: a label, a combo box with
     * the key's kinds, and a content editor whose form depends on the
     * selected kind (absent, free text, or a choice among grammar resource
     * names).
     */
    private class KeyRow {
        KeyRow(ExploreKey key) {
            this.key = key;
            this.kindBox = new JComboBox<>();
            for (var kind : key.getKindType().getEnumConstants()) {
                this.kindBox.addItem(kind.getName());
            }
            this.kindBox.setToolTipText(createKindToolTip(key));
            this.kindBox.addActionListener(e -> refresh());
            this.textField = new JTextField(12);
            this.textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    refresh();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    refresh();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    refresh();
                }
            });
            this.namesBox = new JComboBox<>();
            this.namesBox.setEditable(true);
            this.namesBox.addActionListener(e -> refresh());
            this.contentCards = new JPanel(new CardLayout());
            this.contentCards.add(new JLabel(), CARD_NONE);
            this.contentCards.add(this.textField, CARD_TEXT);
            this.contentCards.add(this.namesBox, CARD_NAMES);
            this.panel = new JPanel(new java.awt.GridLayout(1, 3, 5, 0));
            JLabel label = new JLabel(key.getKeyPhrase());
            label.setToolTipText(key.getExplanation());
            this.panel.add(label);
            this.panel.add(this.kindBox);
            this.panel.add(this.contentCards);
        }

        /** Creates an HTML tooltip listing the kinds of the key. */
        private String createKindToolTip(ExploreKey key) {
            var result = new StringBuilder("<html>");
            result.append(key.getExplanation());
            result.append(":");
            for (var kind : key.getKindType().getEnumConstants()) {
                result.append("<br>- <i>");
                result.append(kind.getName());
                result.append("</i>: ");
                result.append(kind.getExplanation());
            }
            result.append("</html>");
            return result.toString();
        }

        /** Returns the row panel. */
        JComponent getPanel() {
            return this.panel;
        }

        /** Returns the currently selected kind. */
        Setting.Kind getKind() {
            var name = (String) this.kindBox.getSelectedItem();
            return this.key.getKindMap().get(name == null
                ? ""
                : name);
        }

        /**
         * Returns the setting composed from the selected kind and the content
         * editor, or {@code null} if no kind is selected.
         * @throws FormatException if the content does not parse for the kind
         */
        Setting getSetting() throws FormatException {
            var kind = getKind();
            if (kind == null) {
                return null;
            }
            return kind.parser().parse(getContentText(kind));
        }

        /** Returns the text of the currently visible content editor. */
        private String getContentText(Setting.Kind kind) {
            return switch (getCard(kind)) {
            case CARD_TEXT -> this.textField.getText().trim();
            case CARD_NAMES -> {
                Object item = this.namesBox.isEditable()
                    ? this.namesBox.getEditor().getItem()
                    : this.namesBox.getSelectedItem();
                yield item == null
                    ? ""
                    : item.toString().trim();
            }
            default -> "";
            };
        }

        /** Loads a setting into the row widgets. */
        void loadSetting(Setting setting) {
            this.kindBox.setSelectedItem(setting.kind().getName());
            String content = setting.kind().parser().unparse(setting);
            this.textField.setText(content);
            refreshNames(setting.kind());
            this.namesBox.setSelectedItem(content);
            refreshContentCard();
        }

        /** Resets the row to the key's default setting. */
        void reset() {
            loadSetting(this.key.getDefaultSetting());
        }

        /** Enables or disables the entire row. */
        void setEnabled(boolean enabled) {
            this.kindBox.setEnabled(enabled);
            this.textField.setEnabled(enabled);
            this.namesBox.setEnabled(enabled);
        }

        /** Shows the content card appropriate for the selected kind. */
        void refreshContentCard() {
            var kind = getKind();
            String card = kind == null
                ? CARD_NONE
                : getCard(kind);
            if (CARD_NAMES.equals(card)) {
                refreshNames(kind);
            }
            ((CardLayout) this.contentCards.getLayout()).show(this.contentCards, card);
        }

        /** Determines the content card for a given kind. */
        private String getCard(Setting.Kind kind) {
            if (kind.contentType() == Setting.ContentType.NULL) {
                return CARD_NONE;
            }
            return getNames(kind) == null
                ? CARD_TEXT
                : CARD_NAMES;
        }

        /**
         * Returns the resource names offered for a given kind, or {@code null}
         * if the kind's content is not name-based.
         */
        private List<String> getNames(Setting.Kind kind) {
            if (kind == Goal.RULE || kind == Goal.APPLIED || kind == Bound.UPTO
                || kind == Bound.INCLUDE) {
                return ExploreConfigDialog.this.ruleNames;
            }
            if (kind == Goal.GRAPH) {
                return ExploreConfigDialog.this.hostNames;
            }
            return null;
        }

        /** Refills the names combo for a given kind, keeping the current text. */
        private void refreshNames(Setting.Kind kind) {
            List<String> names = getNames(kind);
            if (names == null) {
                return;
            }
            Object current = this.namesBox.getEditor().getItem();
            boolean wasRefreshing = ExploreConfigDialog.this.refreshing;
            ExploreConfigDialog.this.refreshing = true;
            try {
                this.namesBox.removeAllItems();
                for (var name : names) {
                    this.namesBox.addItem(name);
                }
                this.namesBox.setSelectedItem(current);
            } finally {
                ExploreConfigDialog.this.refreshing = wasRefreshing;
            }
        }

        private final ExploreKey key;
        private final JComboBox<String> kindBox;
        private final JTextField textField;
        private final JComboBox<String> namesBox;
        private final JPanel contentCards;
        private final JPanel panel;
    }
}
