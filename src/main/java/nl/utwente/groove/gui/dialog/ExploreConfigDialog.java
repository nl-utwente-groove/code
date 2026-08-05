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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.config.Bound;
import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreConfigChecker;
import nl.utwente.groove.explore.config.ExploreConfigSchema;
import nl.utwente.groove.explore.config.ExploreKey;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.explore.config.Frontier;
import nl.utwente.groove.explore.config.Goal;
import nl.utwente.groove.explore.config.Setting;
import nl.utwente.groove.grammar.GrammarKey;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.SettingsSchemas;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.SimulatorModel;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.util.HTMLConverter;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Dialog that allows the user to edit the grammar's saved exploration settings
 * by choosing a value for every key of the exploration feature model (see
 * {@link ExploreKey}). The dialog always edits <i>saved</i> settings: a
 * dropdown selects which of the grammar's exploration settings resources is
 * the grammar's exploration, the widgets are loaded from those settings, and
 * the run buttons always run the saved exploration. Keys whose composed value
 * deviates from the saved settings are marked bold: those are unsaved edits,
 * which have to be saved (or reverted) before an exploration can be run.
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
        // narrow the key and kind columns to their widest values, aligned
        // across all rows; the content editors get the remaining width.
        // The key labels are measured in bold, the widest form they can take
        // (see KeyRow.refreshLabel), so that bolding never shifts the layout
        int keyWidth = 0;
        int kindWidth = 0;
        for (var row : this.rows.values()) {
            var font = row.label.getFont();
            row.label.setFont(font.deriveFont(Font.BOLD));
            keyWidth = Math.max(keyWidth, row.label.getPreferredSize().width);
            row.label.setFont(font);
            kindWidth = Math.max(kindWidth, row.kindBox.getPreferredSize().width);
        }
        for (var row : this.rows.values()) {
            row.setColumnWidths(keyWidth, kindWidth);
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
                               ExploreKey.SHAPE, ExploreKey.COUNT));
        content
            .add(createSection("Engine", ExploreKey.COLLAPSE, ExploreKey.ALGEBRA,
                               ExploreKey.PERSISTENCE));
        content.add(createResourcePanel());
        content.add(createErrorPanel());
        content.add(createButtonPanel());

        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        content
            .registerKeyboardAction(e -> closeDialog(), escape, JComponent.WHEN_IN_FOCUSED_WINDOW);
        content
            .registerKeyboardAction(e -> doDefaultExploration(), enter,
                                    JComponent.WHEN_IN_FOCUSED_WINDOW);

        // a legacy exploration strategy that cannot be expressed in the
        // feature model cannot be loaded into the widgets; announce that
        var properties = getGrammar().getProperties();
        if (properties.getExplorationName() == null
            && properties.containsKey(GrammarKey.EXPLORATION)
            && !(properties.getLegacyExploreType() instanceof ConfiguredExploreType)) {
            this.legacyNotice = "The legacy exploration strategy cannot be expressed"
                + " in the feature model; showing the default configuration";
        }
        loadConfig(getGrammar().getDefaultExploreConfig());
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

    /**
     * Creates the (borderless) panel with the selector for the exploration
     * settings the grammar uses, as well as the status of the configuration.
     */
    private JPanel createResourcePanel() {
        JPanel result = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel(NAME_LABEL_TEXT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        this.nameBox = new JComboBox<>();
        this.nameBox.setToolTipText(NAME_TOOLTIP);
        this.nameBox.addActionListener(e -> selectName());
        JPanel namePanel = new JPanel(new BorderLayout(5, 0));
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(this.nameBox, BorderLayout.CENTER);
        this.statusLabel = new JLabel(" ");
        this.statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        // anchor both to the top, so surplus vertical space stays below them
        JPanel inner = new JPanel(new BorderLayout(0, 2));
        inner.add(namePanel, BorderLayout.NORTH);
        inner.add(this.statusLabel, BorderLayout.SOUTH);
        result.add(inner, BorderLayout.NORTH);
        return result;
    }

    /**
     * Reacts to a user selection in the settings selector: makes the selected
     * settings (if any) the grammar's exploration, and loads them into the
     * widgets. Unsaved edits are discarded, after confirmation. Since changing
     * the exploration reference does not reset the GTS, switching to other
     * settings preserves the explored state space, so that exploration can be
     * continued under the newly selected settings.
     */
    private void selectName() {
        if (this.refreshing) {
            return;
        }
        var errors = new FormatErrorSet();
        ExploreConfig config = storeConfig(errors);
        var savedConfig = getGrammar().getDefaultExploreConfig();
        if (!errors.isEmpty() || !config.unparse().equals(savedConfig.unparse())) {
            int answer = JOptionPane
                .showConfirmDialog(this, ASK_DISCARD_TEXT, ASK_DISCARD_TITLE,
                                   JOptionPane.OK_CANCEL_OPTION);
            if (answer != JOptionPane.OK_OPTION) {
                refreshNameSelection(getGrammar().getProperties().getExplorationName());
                return;
            }
        }
        int index = this.nameBox.getSelectedIndex();
        QualName name = index < 0
            ? null
            : this.nameBoxNames.get(index);
        try {
            getSimulatorModel().doSetExplorationName(name);
        } catch (IOException exc) {
            // do nothing
        }
        resetTo(getGrammar().getDefaultExploreConfig());
    }

    /** Sets the selector to the item for a given settings name, without triggering a selection. */
    private void refreshNameSelection(QualName name) {
        boolean wasRefreshing = this.refreshing;
        this.refreshing = true;
        try {
            int index = name == null
                ? 0
                : this.nameBoxNames.indexOf(name);
            this.nameBox.setSelectedIndex(Math.max(0, index));
        } finally {
            this.refreshing = wasRefreshing;
        }
    }

    /** Loads a given configuration into the widgets, replacing the composed one. */
    private void resetTo(ExploreConfig config) {
        loadConfig(config);
        refresh();
    }

    /**
     * Creates the (borderless) area listing the problems of the composed
     * configuration. The area is invisible while there are no problems; its
     * text is top-aligned and shown in full, with the dialog growing as
     * needed (see {@link #refreshStatus}).
     */
    private JPanel createErrorPanel() {
        JPanel result = new JPanel(new BorderLayout()) {
            // the error text wraps to whatever width the rest of the dialog
            // establishes, so it must never influence the dialog width
            @Override
            public Dimension getPreferredSize() {
                var size = super.getPreferredSize();
                size.width = 0;
                return size;
            }
        };
        this.errorLabel = new JLabel();
        this.errorLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        result.add(this.errorLabel, BorderLayout.NORTH);
        return result;
    }

    /**
     * Creates the button panel: two rows, the first for managing the saved
     * settings and the second for running the exploration and closing the
     * dialog. A single row would make the dialog too wide.
     */
    private JPanel createButtonPanel() {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        JPanel settingsRow = new JPanel();
        this.saveButton = new JButton(SAVE_COMMAND);
        this.saveButton.addActionListener(e -> saveConfig(false));
        settingsRow.add(this.saveButton);
        this.saveAsButton = new JButton(SAVE_AS_COMMAND);
        this.saveAsButton.addActionListener(e -> saveConfig(true));
        settingsRow.add(this.saveAsButton);
        this.revertButton = new JButton(REVERT_COMMAND);
        this.revertButton.setToolTipText(REVERT_TOOLTIP);
        this.revertButton.addActionListener(e -> resetTo(getGrammar().getDefaultExploreConfig()));
        settingsRow.add(this.revertButton);
        result.add(settingsRow);
        JPanel runRow = new JPanel();
        this.startButton = new JButton(START_COMMAND);
        this.startButton.addActionListener(e -> startExploration());
        runRow.add(this.startButton);
        this.exploreButton = new JButton(EXPLORE_COMMAND);
        this.exploreButton.addActionListener(e -> doExploration());
        runRow.add(this.exploreButton);
        JButton cancelButton = new JButton(CANCEL_COMMAND);
        cancelButton.addActionListener(e -> closeDialog());
        runRow.add(cancelButton);
        result.add(runRow);
        return result;
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
     * resource line, status text and buttons.
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
            // recompute the configuration and status; errors in the value of
            // a particular key are marked at the key's row, and collected
            // (with the key phrase prefixed) for the central error area
            var errors = new FormatErrorSet();
            var config = new ExploreConfig();
            for (var key : ExploreKey.values()) {
                var rowErrors = new FormatErrorSet();
                try {
                    Setting setting = getRow(key).getSetting();
                    if (setting != null) {
                        config.put(key, setting);
                        rowErrors.addAll(ExploreConfigChecker.check(getGrammar(), key, setting));
                    }
                } catch (FormatException exc) {
                    rowErrors.addAll(exc.getErrors());
                }
                getRow(key).setErrors(rowErrors);
                for (var error : rowErrors) {
                    errors.add("Error in value for '%s': %s", key.getKeyPhrase(), error.toString());
                }
            }
            ExploreType exploreType = null;
            if (errors.isEmpty()) {
                try {
                    exploreType = ExploreTypeConverter.toExploreType(config);
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
            refreshStatus(config, exploreType, errors);
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
                errors.add("Error in value for '%s': %s", key.getKeyPhrase(), exc.getMessage());
            }
        }
        return result;
    }

    /** Refreshes the error area, the status label and the enabling of the buttons. */
    private void refreshStatus(ExploreConfig config, ExploreType exploreType,
                               FormatErrorSet errors) {
        boolean runnable = exploreType != null;
        // like the old dialog, running additionally requires an error-free grammar
        // that is compatible with the exploration
        boolean explorable = runnable;
        String problem = null;
        // reason for disabled run buttons, shown in their tooltips only:
        // the details of general grammar errors are of no interest here
        String disabledReason = null;
        GrammarModel grammar = getGrammar();
        if (explorable && grammar.hasErrors()) {
            explorable = false;
            disabledReason = "The grammar has errors";
        }
        if (explorable) {
            assert exploreType != null;
            try {
                exploreType.test(grammar.toGrammar());
            } catch (FormatException exc) {
                explorable = false;
                problem = exc.getMessage();
            }
        }
        // compose the problems text, for the error area and button tooltips
        var problems = new StringBuilder();
        for (var error : errors) {
            if (!problems.isEmpty()) {
                problems.append("<br>");
            }
            problems.append(HTMLConverter.toHtml(new StringBuilder(error.toString())));
        }
        if (problem != null) {
            if (!problems.isEmpty()) {
                problems.append("<br>");
            }
            problems.append(HTMLConverter.toHtml(new StringBuilder(problem)));
        }
        // an unparseable stored exploration value cannot be loaded into the
        // dialog widgets, so its error has to be reported here explicitly
        for (var key : new GrammarKey[] {GrammarKey.EXPLORE_CONFIG, GrammarKey.EXPLORATION}) {
            if (!grammar.getProperties().containsKey(key)) {
                continue;
            }
            try {
                grammar.getProperties().parseProperty(key);
            } catch (FormatException exc) {
                if (!problems.isEmpty()) {
                    problems.append("<br>");
                }
                problems
                    .append(HTMLConverter
                        .toHtml(new StringBuilder("The stored '" + key.getName()
                            + "' property does not parse: " + exc.getMessage())));
            }
        }
        String problemsHtml = problems.isEmpty()
            ? null
            : problems.toString();
        this.errorLabel
            .setText(problemsHtml == null
                ? null
                : "<html><body style='width:" + getErrorWrapWidth()
                    + "px'><font color='red'>" + problemsHtml + "</font></body></html>");
        QualName exploreName = grammar.getProperties().getExplorationName();
        refreshNameBox(exploreName);
        // the status label only carries informational messages
        String status = " ";
        if (this.legacyNotice != null) {
            status
                = "<html><font color='" + INFO_COLOR + "'>" + this.legacyNotice + "</font></html>";
            this.legacyNotice = null;
        }
        this.statusLabel.setText(status);
        // mark the keys whose composed value deviates from the saved settings:
        // those are the unsaved edits, which have to be saved before a run
        var savedConfig = grammar.getDefaultExploreConfig();
        boolean savedDiffers = !config.unparse().equals(savedConfig.unparse());
        for (var key : ExploreKey.values()) {
            var savedSetting = savedConfig.get(key);
            String deviationHtml = null;
            if (!config.get(key).equals(savedSetting)) {
                String text = key.parser().unparse(savedSetting);
                if (text.isEmpty()) {
                    text = savedSetting.kind().getName();
                }
                deviationHtml
                    = "Saved settings use: <b>" + HTMLConverter.toHtml(new StringBuilder(text))
                        + "</b>";
            }
            getRow(key).setDeviating(deviationHtml);
        }
        this.revertButton.setEnabled(savedDiffers || !errors.isEmpty());
        // there is nothing to save if the composition equals the saved setting,
        // unless there is no resource yet to save it in
        this.saveButton.setEnabled(runnable && (savedDiffers || exploreName == null));
        this.saveButton.setToolTipText(SAVE_TOOLTIP);
        this.saveAsButton.setEnabled(runnable);
        this.saveAsButton.setToolTipText(SAVE_AS_TOOLTIP);
        // on a fresh state space there is no difference between restarting
        // and continuing: the start button reads "Start" and Continue is off
        boolean fresh = isFreshGTS();
        this.startButton.setText(fresh
            ? START_FRESH_COMMAND
            : START_COMMAND);
        // only the saved exploration settings can be run: unsaved edits block
        // the run buttons, rather than being run without leaving a trace
        this.startButton.setEnabled(explorable && !savedDiffers);
        // continuing cannot change the per-GTS features (collapse, algebra,
        // persistence) recorded in the explored state space
        String continueProblem = null;
        var gts = getSimulatorModel().getGTS();
        if (!fresh && gts != null && exploreType instanceof ConfiguredExploreType configured) {
            var gtsErrors = configured.checkGTS(gts);
            if (!gtsErrors.isEmpty()) {
                continueProblem = gtsErrors.iterator().next().toString();
            }
        }
        this.exploreButton
            .setEnabled(explorable && !fresh && continueProblem == null && !savedDiffers);
        String tipHtml = problemsHtml;
        if (disabledReason != null) {
            tipHtml = (tipHtml == null
                ? ""
                : tipHtml + "<br>") + HTMLConverter.toHtml(new StringBuilder(disabledReason));
        }
        String startTipBase = fresh
            ? START_FRESH_TOOLTIP
            : START_TOOLTIP;
        String exploreTip = tipHtml != null
            ? "<html>" + EXPLORE_TOOLTIP + "<br><font color='red'>" + tipHtml + "</font></html>"
            : fresh
                ? "<html>" + EXPLORE_TOOLTIP
                    + "<br><i>Nothing to continue: the state space is still unexplored</i></html>"
                : EXPLORE_TOOLTIP;
        if (continueProblem != null) {
            exploreTip = "<html>" + EXPLORE_TOOLTIP + "<br><font color='red'>"
                + HTMLConverter.toHtml(new StringBuilder(continueProblem)) + "</font></html>";
        }
        String startTip = tipHtml == null
            ? startTipBase
            : "<html>" + startTipBase + "<br><font color='red'>" + tipHtml + "</font></html>";
        // if the only thing standing in the way of a run is that the
        // composition is not saved, the tooltip leads with that reason
        if (savedDiffers && tipHtml == null) {
            startTip = "<html><font color='red'>" + UNSAVED_REASON + "</font><br>" + startTipBase
                + "</html>";
        }
        if (savedDiffers && tipHtml == null && continueProblem == null && !fresh) {
            exploreTip = "<html><font color='red'>" + UNSAVED_REASON + "</font><br>"
                + EXPLORE_TOOLTIP + "</html>";
        }
        this.startButton.setToolTipText(startTip);
        this.exploreButton.setToolTipText(exploreTip);
        // grow the dialog height if the error area no longer fits; only the
        // height, as pack() would also snap the width back to its preferred
        // value, causing a spurious horizontal resize
        if (isVisible()) {
            int prefHeight = getPreferredSize().height;
            if (prefHeight > getHeight()) {
                setSize(getWidth(), prefHeight);
            }
        }
    }

    /**
     * Refreshes the settings selector: the {@link #NONE_ITEM} sentinel followed
     * by the names of the grammar's exploration settings resources, with the
     * currently referenced name selected. A reference to a non-existent
     * resource is appended as a (marked) item of its own, so that it can be
     * shown as the selection.
     * @param exploreName the currently referenced settings name, or {@code null}
     */
    private void refreshNameBox(QualName exploreName) {
        var names = SettingsSchemas.getResourceNames(getGrammar(), ExploreConfigSchema.INSTANCE);
        this.nameBoxNames.clear();
        // the sentinel stands for the absence of a reference
        this.nameBoxNames.add(null);
        this.nameBoxNames.addAll(names);
        var items = new ArrayList<String>();
        items.add(NONE_ITEM);
        for (var name : names) {
            items.add(name.toString());
        }
        if (exploreName != null && !names.contains(exploreName)) {
            this.nameBoxNames.add(exploreName);
            items.add(exploreName + MISSING_SUFFIX);
        }
        // only rebuild the item list on an actual change: this method runs on
        // every keystroke in the dialog, and a rebuild closes an open popup
        boolean sameItems = items.size() == this.nameBox.getItemCount();
        for (int i = 0; sameItems && i < items.size(); i++) {
            sameItems = items.get(i).equals(this.nameBox.getItemAt(i));
        }
        if (!sameItems) {
            this.nameBox.removeAllItems();
            for (var item : items) {
                this.nameBox.addItem(item);
            }
        }
        refreshNameSelection(exploreName);
    }

    /**
     * Computes the wrap width for the error text: the available width of the
     * error area, so that the text never widens the dialog. Before the first
     * layout (during construction), this falls back to the preferred width of
     * the widest other dialog component.
     */
    private int getErrorWrapWidth() {
        var parent = this.errorLabel.getParent();
        int result = parent.getWidth();
        if (result == 0) {
            for (var sibling : parent.getParent().getComponents()) {
                if (sibling != parent) {
                    result = Math.max(result, sibling.getPreferredSize().width);
                }
            }
        }
        return Math.max(100, result - 30);
    }

    /**
     * Indicates if the current LTS consists of just the unexplored start
     * state (or there is none at all).
     */
    private boolean isFreshGTS() {
        var gts = getSimulatorModel().getGTS();
        return gts == null || gts.nodeCount() == 1 && !gts.startState().isClosed();
    }

    /**
     * Runs the exploration appropriate for the state of the LTS: a fresh
     * (re)start if nothing has been explored yet, otherwise a continuation.
     * Bound to the Enter key.
     */
    private void doDefaultExploration() {
        // the key is bound on the dialog as a whole, so it also arrives when
        // the corresponding button is disabled; then it should do nothing
        if (isFreshGTS()) {
            if (this.startButton.isEnabled()) {
                startExploration();
            }
        } else if (this.exploreButton.isEnabled()) {
            doExploration();
        }
    }

    /** Restarts the GTS and runs the saved exploration. */
    private void startExploration() {
        getSimulatorModel().resetGTS();
        doExploration();
    }

    /** Runs the grammar's saved exploration on the current state space. */
    private void doExploration() {
        // the run buttons are only enabled if the composition equals the saved
        // exploration, so the standard explore action runs what is composed;
        // going through that action gets the result emphasis for free
        closeDialog();
        this.simulator.getActions().getExploreAction().execute();
    }

    /**
     * Saves the composed configuration as named exploration settings (stored
     * in a SETTINGS resource), and makes those settings the grammar's
     * exploration.
     * @param askName if {@code true}, the target name is asked from the user;
     * otherwise it is asked only if the grammar has no exploration reference
     * yet
     */
    private void saveConfig(boolean askName) {
        var errors = new FormatErrorSet();
        ExploreConfig config = storeConfig(errors);
        if (config == null || !errors.isEmpty()) {
            return;
        }
        QualName target = getGrammar().getProperties().getExplorationName();
        if (target == null || askName) {
            target = askConfigName(target);
            if (target == null) {
                return;
            }
        }
        try {
            getSimulatorModel().doSaveExploreConfig(target, config);
        } catch (IOException exc) {
            // do nothing
        }
        // the grammar has changed, so the status may have as well
        refresh();
    }

    /**
     * Asks the user for the name to save the exploration settings under.
     * @param current the currently referenced settings name, if any; used as
     * suggestion
     * @return the chosen name, or {@code null} if the dialog was cancelled
     */
    private QualName askConfigName(QualName current) {
        var existingNames = getGrammar().getNames(ResourceKind.SETTINGS);
        // the name is not required to be fresh: the suggestion may well be the
        // current name, and saving in place must stay possible
        FreshNameDialog<QualName> nameDialog = new FreshNameDialog<>(existingNames == null
            ? Collections.emptySet()
            : existingNames, current == null
                ? DEFAULT_CONFIG_NAME
                : current.toString(), false) {
            @Override
            protected QualName createName(String name) throws FormatException {
                return QualName.parse(name).testValid();
            }
        };
        return nameDialog.showDialog(this.simulator.getFrame(), ASK_NAME_TITLE)
            ? nameDialog.getName()
            : null;
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
    /** Selector for the grammar's exploration settings. */
    private JComboBox<String> nameBox;
    /**
     * Settings names corresponding to the items of {@link #nameBox}, so that a
     * selection can be resolved by index rather than by parsing the item text.
     * The first entry is {@code null}, for the {@link #NONE_ITEM} sentinel.
     */
    private final List<QualName> nameBoxNames = new ArrayList<>();
    private JLabel statusLabel;
    private JLabel errorLabel;
    private JButton saveButton;
    private JButton saveAsButton;
    private JButton startButton;
    private JButton exploreButton;
    private JButton revertButton;
    private boolean refreshing;
    private String legacyNotice;
    private final int oldDismissDelay;

    private static final String SAVE_COMMAND = "Save";
    private static final String SAVE_AS_COMMAND = "Save As...";
    private static final String START_COMMAND = "Restart";
    private static final String EXPLORE_COMMAND = "Continue";
    private static final String CANCEL_COMMAND = "Cancel";
    private static final String REVERT_COMMAND = "Revert";
    /** Text of the label in front of the settings selector. */
    private static final String NAME_LABEL_TEXT = "Exploration settings:";
    /** Selector item standing for the absence of an exploration reference. */
    private static final String NONE_ITEM = "(none)";
    /** Suffix marking a reference to a non-existent settings resource. */
    private static final String MISSING_SUFFIX = " (missing)";
    private static final String NAME_TOOLTIP
        = "The exploration settings the grammar uses; selecting other settings"
            + " preserves the explored state space";
    /** Title of the dialog asking to confirm discarding unsaved changes. */
    private static final String ASK_DISCARD_TITLE = "Unsaved changes";
    private static final String ASK_DISCARD_TEXT = "Discard the unsaved changes?";
    private static final String SAVE_TOOLTIP
        = "Save the composed exploration settings under the name given by the 'exploration'"
            + " system property, asking for a name if that property is unset";
    private static final String SAVE_AS_TOOLTIP
        = "Save the composed exploration settings under a name of your choice"
            + " and make that the grammar's exploration";
    /** Title of the dialog asking for the name to save the exploration settings under. */
    private static final String ASK_NAME_TITLE = "Select exploration settings name";
    /** Name suggested for a first exploration settings resource; settings
     * names are free, so this is a suggestion and nothing more. */
    private static final String DEFAULT_CONFIG_NAME = "exploration";
    private static final String REVERT_TOOLTIP
        = "Discard the unsaved changes and reload the saved exploration settings";
    private static final String START_FRESH_COMMAND = "Start";
    private static final String START_TOOLTIP
        = "Discard the current state space and explore afresh with the saved exploration settings";
    private static final String START_FRESH_TOOLTIP
        = "Explore the state space with the saved exploration settings";
    private static final String EXPLORE_TOOLTIP
        = "Continue exploring the current state space with the saved exploration settings";
    /** Reason shown when the run buttons are blocked by unsaved changes only. */
    private static final String UNSAVED_REASON
        = "The composed settings differ from the saved exploration settings; save them first";
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
            this.defaultKindName = key.getDefaultKind().getName();
            this.kindBox = new JComboBox<>();
            for (var kind : key.getKindType().getEnumConstants()) {
                this.kindBox.addItem(kind.getName());
            }
            this.kindBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public java.awt.Component getListCellRendererComponent(JList<?> list, Object value,
                                                                       int index,
                                                                       boolean isSelected,
                                                                       boolean cellHasFocus) {
                    var result = super.getListCellRendererComponent(list, value, index, isSelected,
                                                                    cellHasFocus);
                    if (value != null) {
                        var text = new StringBuilder(value.toString());
                        String hint = getKindHint(value.toString());
                        if (hint != null) {
                            text.append(" (").append(hint).append(")");
                        }
                        if (KeyRow.this.defaultKindName.equals(value)) {
                            text.append("*");
                        }
                        setText(text.toString());
                    }
                    return result;
                }
            });
            this.kindToolTip = createKindToolTip(key);
            this.kindBox.setToolTipText("<html>" + this.kindToolTip + "</html>");
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
            this.textBorder = this.textField.getBorder();
            this.namesBorder = this.namesBox.getBorder();
            this.label = new JLabel(key.getKeyPhrase());
            this.labelColor = this.label.getForeground();
            this.label.setToolTipText("<html>" + this.kindToolTip + "</html>");
            this.panel = new JPanel(new BorderLayout(5, 0));
            this.panel.add(this.label, BorderLayout.WEST);
            JPanel valuePanel = new JPanel(new BorderLayout(5, 0));
            valuePanel.add(this.kindBox, BorderLayout.WEST);
            valuePanel.add(this.contentCards, BorderLayout.CENTER);
            this.panel.add(valuePanel, BorderLayout.CENTER);
        }

        /** Returns the display hint of the kind with a given name, if any. */
        private String getKindHint(String kindName) {
            for (var kind : this.key.getKindType().getEnumConstants()) {
                if (kind.getName().equals(kindName)) {
                    return kind.getHint();
                }
            }
            return null;
        }

        /** Sets the widths of the key and kind columns (shared by all rows). */
        void setColumnWidths(int keyWidth, int kindWidth) {
            this.label
                .setPreferredSize(new Dimension(keyWidth,
                    this.label.getPreferredSize().height));
            this.kindBox
                .setPreferredSize(new Dimension(kindWidth,
                    this.kindBox.getPreferredSize().height));
        }

        /**
         * Creates an HTML fragment stating the meaning of the key and listing
         * its kinds, with the key default marked. (The syntax of a kind's
         * content is documented on the content editor instead.)
         */
        private String createKindToolTip(ExploreKey key) {
            var result = new StringBuilder("<b>");
            result.append(key.getKeyPhrase());
            result.append("</b>: ");
            result.append(key.getExplanation());
            result.append(". Possible values:");
            for (var kind : key.getKindType().getEnumConstants()) {
                result.append("<br>- <i>");
                result.append(kind.getName());
                result.append("</i>");
                if (kind.getName().equals(this.defaultKindName)) {
                    result.append(" (default)");
                }
                result.append(": ");
                result.append(kind.getExplanation());
            }
            return result.toString();
        }

        /**
         * Returns a (plain-text or HTML-fragment) description of the content
         * expected for a given kind, or {@code null} if the kind has no content.
         */
        private String createContentToolTip(Setting.Kind kind) {
            if (kind == Goal.CONDITION) {
                return "a propositional condition over rule names:"
                    + " <i>rule</i>; !P; P&amp;&amp;Q; P||Q; P-&gt;Q; (P)";
            }
            if (kind == Goal.LTL) {
                return "an LTL formula over rule names";
            }
            if (kind == Goal.CTL) {
                return "a CTL formula over rule names";
            }
            if (kind == Goal.FIRES) {
                return "an action (rule or recipe) name";
            }
            if (kind == Goal.GRAPH) {
                return "a host graph name";
            }
            if (kind == Bound.EDGES) {
                return "a comma-separated list of <i>label</i>&gt;<i>bound</i> pairs,"
                    + " e.g. a&gt;2,b&gt;3";
            }
            if (kind == Bound.UPTO || kind == Bound.INCLUDE) {
                return "a rule name, optionally negated by a '!' prefix";
            }
            return switch (kind.contentType()) {
            case INTEGER -> this.key == ExploreKey.FRONTIER
                ? "the maximal frontier size (at least 2)"
                : "the number of results (at least 2)";
            case LIMIT -> "a maximum, optionally followed by +<i>inc</i>"
                + " for iterative deepening, e.g. 200 or 200+50";
            default -> null;
            };
        }

        /** Returns the row panel. */
        JComponent getPanel() {
            return this.panel;
        }

        /** Returns the currently selected kind. */
        Setting.Kind getKind() {
            var name = (String) this.kindBox.getSelectedItem();
            return this.key
                .getKindMap()
                .get(name == null
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
            this.contentMap.put(setting.kind(), setting.kind().parser().unparse(setting));
            // force the content editors to be reloaded from the content map
            this.shownKind = null;
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

        /**
         * Marks or unmarks the row as erroneous: the key phrase turns red,
         * the content editor gets a red border, and the row tooltips lead
         * with the error text.
         */
        void setErrors(FormatErrorSet errors) {
            String errorHtml = null;
            if (!errors.isEmpty()) {
                var text = new StringBuilder();
                for (var error : errors) {
                    if (!text.isEmpty()) {
                        text.append("<br>");
                    }
                    text.append(HTMLConverter.toHtml(new StringBuilder(error.toString())));
                }
                errorHtml = text.toString();
            }
            if (Objects.equals(errorHtml, this.errorHtml)) {
                // nothing changed, in particular not the widget state below
                return;
            }
            this.errorHtml = errorHtml;
            boolean hasErrors = errorHtml != null;
            this.textField
                .setBorder(hasErrors
                    ? BorderFactory.createCompoundBorder(ERROR_BORDER, this.textBorder)
                    : this.textBorder);
            this.namesBox
                .setBorder(hasErrors
                    ? BorderFactory.createCompoundBorder(ERROR_BORDER, this.namesBorder)
                    : this.namesBorder);
            refreshLabel();
            refreshContentToolTip();
        }

        /**
         * Marks or unmarks the row as deviating from the exploration in
         * force when the dialog was opened: the key phrase turns bold, and
         * the label tooltip shows the value the row deviates from.
         * @param deviationHtml HTML fragment describing the current value,
         * or {@code null} if the row does not deviate
         */
        void setDeviating(String deviationHtml) {
            if (Objects.equals(deviationHtml, this.deviationHtml)) {
                return;
            }
            this.deviationHtml = deviationHtml;
            refreshLabel();
        }

        /**
         * Refreshes colour, font and tooltip of the key label from the
         * error and deviation states.
         */
        private void refreshLabel() {
            boolean hasErrors = this.errorHtml != null;
            this.label
                .setForeground(hasErrors
                    ? Values.ERROR_NORMAL_FOREGROUND
                    : this.labelColor);
            this.label
                .setFont(this.label
                    .getFont()
                    .deriveFont(this.deviationHtml == null
                        ? Font.PLAIN
                        : Font.BOLD));
            var tip = new StringBuilder("<html>");
            if (hasErrors) {
                tip.append("<font color='red'>" + this.errorHtml + "</font><hr>");
            }
            tip.append(this.kindToolTip);
            if (this.deviationHtml != null) {
                tip.append("<hr>" + this.deviationHtml);
            }
            tip.append("</html>");
            this.label.setToolTipText(tip.toString());
        }

        /**
         * Shows the content card appropriate for the selected kind. Each kind
         * has its own content: on a kind switch, the previous kind's content
         * is remembered and the new kind's content restored, so (say) a
         * formula does not linger in the field when an LTL goal is selected.
         */
        void refreshContentCard() {
            var kind = getKind();
            if (kind != this.shownKind) {
                if (this.shownKind != null) {
                    this.contentMap.put(this.shownKind, getContentText(this.shownKind));
                }
                setContentText(kind, kind == null
                    ? ""
                    : this.contentMap.getOrDefault(kind, ""));
                this.shownKind = kind;
            }
            String card = kind == null
                ? CARD_NONE
                : getCard(kind);
            if (CARD_NAMES.equals(card)) {
                refreshNames(kind);
            }
            refreshContentToolTip();
            ((CardLayout) this.contentCards.getLayout()).show(this.contentCards, card);
        }

        /**
         * Refreshes the tooltip of the content editors: the syntax of the
         * selected kind's content, led by the current errors of this row
         * (if any).
         */
        private void refreshContentToolTip() {
            var kind = getKind();
            String contentTip = kind == null
                ? null
                : createContentToolTip(kind);
            if (contentTip != null) {
                contentTip = Character.toUpperCase(contentTip.charAt(0)) + contentTip.substring(1);
            }
            String html;
            if (this.errorHtml != null) {
                html = "<html><font color='red'>" + this.errorHtml + "</font>" + (contentTip == null
                    ? ""
                    : "<hr>" + contentTip) + "</html>";
            } else {
                html = contentTip == null
                    ? null
                    : "<html>" + contentTip + "</html>";
            }
            this.textField.setToolTipText(html);
            this.namesBox.setToolTipText(html);
        }

        /** Sets the content editors to a given text, without triggering a refresh. */
        private void setContentText(Setting.Kind kind, String text) {
            boolean wasRefreshing = ExploreConfigDialog.this.refreshing;
            ExploreConfigDialog.this.refreshing = true;
            try {
                this.textField.setText(text);
                if (kind != null) {
                    refreshNames(kind);
                }
                this.namesBox.setSelectedItem(text);
            } finally {
                ExploreConfigDialog.this.refreshing = wasRefreshing;
            }
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
            // a condition is usually a bare rule name, so the editable name
            // combo doubles as its editor, leaving room for compound formulas
            if (kind == Goal.CONDITION || kind == Goal.FIRES || kind == Bound.UPTO
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
        private final String defaultKindName;
        private final JLabel label;
        /** The normal (non-error) colour of the key phrase label. */
        private final Color labelColor;
        /** HTML fragment documenting the key and its kinds. */
        private final String kindToolTip;
        private final JComboBox<String> kindBox;
        private final JTextField textField;
        private final JComboBox<String> namesBox;
        /** The normal (non-error) borders of the content editors. */
        private final Border textBorder;
        private final Border namesBorder;
        private final JPanel contentCards;
        private final JPanel panel;
        /** Content last entered for each kind of this key. */
        private final Map<Setting.Kind,String> contentMap = new HashMap<>();
        /** The kind whose content is currently shown in the editors. */
        private Setting.Kind shownKind;
        /** HTML fragment with the current errors of this row, or {@code null}. */
        private String errorHtml;
        /** HTML description of the current value this row deviates from, if any. */
        private String deviationHtml;
    }

    /** Border marking an erroneous content editor. */
    private static final Border ERROR_BORDER
        = BorderFactory.createLineBorder(Values.ERROR_NORMAL_FOREGROUND);
}
