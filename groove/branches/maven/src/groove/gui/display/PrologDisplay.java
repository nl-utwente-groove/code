/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.gui.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import gnu.prolog.io.TermWriter;
import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.PrologException;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.ResourceKind;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.lts.MatchResult;
import groove.prolog.GrooveEnvironment;
import groove.prolog.GrooveState;
import groove.prolog.PrologEngine;
import groove.prolog.QueryResult;
import groove.util.parse.FormatException;

/**
 * The Prolog editor tab for the improved simulator
 *
 * @author Michiel Hendriks
 */
@SuppressWarnings("resource")
public class PrologDisplay extends ResourceDisplay {
    private static final int MAX_HISTORY = 50;

    static final Preferences PREFS = Preferences.userNodeForPackage(PrologDisplay.class);

    /**
     * Construct a prolog panel
     */
    PrologDisplay(Simulator simulator) {
        super(simulator, ResourceKind.PROLOG);
        Environment.setDefaultOutputStream(getUserOutput());
    }

    @Override
    protected void buildDisplay() {
        JPanel queryPane = new JPanel(new BorderLayout());
        JLabel leading = new JLabel(" ?- ");
        leading.setFont(leading.getFont()
            .deriveFont(Font.BOLD));
        queryPane.add(leading, BorderLayout.WEST);
        queryPane.add(getQueryField(), BorderLayout.CENTER);
        JPanel buttonsPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonsPane.add(createExecuteButton());
        buttonsPane.add(getNextResultButton());
        buttonsPane.setBorder(null);
        queryPane.add(buttonsPane, BorderLayout.EAST);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(.4);
        splitPane.setBottomComponent(getTabPane());
        splitPane.setTopComponent(getResultsPanel());

        JPanel mainPane = new JPanel(new BorderLayout());
        mainPane.add(queryPane, BorderLayout.NORTH);
        mainPane.add(splitPane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(mainPane, BorderLayout.CENTER);
    }

    @Override
    protected JComponent createInfoPanel() {
        return new TitledPanel("Available predicates", getSyntaxHelp(), null, false);
    }

    @Override
    protected void buildInfoPanel() {
        getInfoPanel().setEnabled(isEnabled());
    }

    /**
     * Creates and returns the output stream in which the Prolog output should appear.
     */
    private OutputStream getUserOutput() {
        if (this.userOutput == null) {
            this.userOutput = new JTextAreaOutputStream(getResultsArea());
        }
        return this.userOutput;
    }

    /**
     * Constructs and returns the text component in the query field.
     */
    private JTextComponent getQueryEdit() {
        if (this.queryEdit == null) {
            getQueryField();
        }
        return this.queryEdit;
    }

    /**
     * Constructs and returns the query field.
     * Also initialises {@link #queryEdit}.
     */
    private JComboBox<String> getQueryField() {
        if (this.queryField == null) {
            this.queryField = new JComboBox<>(PREFS.get("queryHistory", "")
                .split("\\n"));
            this.queryField.setFont(EDIT_FONT);
            this.queryField.setEditable(true);
            this.queryField.setEnabled(true);
            this.queryField.setPrototypeDisplayValue("groove+prolog");
            this.queryEdit = (JTextComponent) this.queryField.getEditor()
                .getEditorComponent();
            this.queryEdit.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        executeQuery();
                        giveFocusToNextResultButton();
                    }
                }
            });
        }
        return this.queryField;
    }

    private JTabbedPane getSyntaxHelp() {
        if (this.syntaxHelp == null) {
            this.syntaxHelp = createSyntaxHelp();
        }
        return this.syntaxHelp;
    }

    /**
     * Creates the panel with documentation trees.
     */
    private JTabbedPane createSyntaxHelp() {
        JTabbedPane treePane = new JTabbedPane() {
            @Override
            public void setEnabled(boolean enabled) {
                getGrooveTree().setEnabled(enabled);
                getPrologTree().setEnabled(enabled);
                getUserTree().setEnabled(enabled);
            }
        };
        treePane.add("Groove", new JScrollPane(getGrooveTree()));
        treePane.add("Prolog", new JScrollPane(getPrologTree()));
        treePane.add("User", new JScrollPane(getUserTree()));
        return treePane;
    }

    private JTree getGrooveTree() {
        if (this.grooveTree == null) {
            this.grooveTree = createPredicateTree(true);
            loadSyntaxHelpTree(this.grooveTree, getEnvironment().getGrooveTags());
        }
        return this.grooveTree;
    }

    private JTree getPrologTree() {
        if (this.prologTree == null) {
            this.prologTree = createPredicateTree(false);
            loadSyntaxHelpTree(this.prologTree, getEnvironment().getPrologTags());
        }
        return this.prologTree;
    }

    private JTree getUserTree() {
        if (this.userTree == null) {
            this.userTree = createPredicateTree(false);
            loadSyntaxHelpTree(this.userTree, getEnvironment().getUserTags());
        }
        return this.userTree;
    }

    /** Loads a tree with a given set of tags. */
    private void loadSyntaxHelpTree(JTree tree, Set<CompoundTermTag> tags) {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel()
            .getRoot();
        rootNode.removeAllChildren();
        Map<AtomTerm,DefaultMutableTreeNode> nodes = new HashMap<>();
        for (CompoundTermTag tag : tags) {
            DefaultMutableTreeNode baseNode = nodes.get(tag.functor);
            if (baseNode == null) {
                baseNode = new DefaultMutableTreeNode(tag);
                rootNode.add(baseNode);
                nodes.put(tag.functor, baseNode);
            } else {
                if (baseNode.getChildCount() == 0) {
                    baseNode.add(new DefaultMutableTreeNode(baseNode.getUserObject()));
                    baseNode.setUserObject(tag.functor.value);
                }
                DefaultMutableTreeNode predNode = new DefaultMutableTreeNode(tag);
                baseNode.add(predNode);
            }
        }
        ((DefaultTreeModel) tree.getModel()).reload();
        tree.expandPath(new TreePath(rootNode.getPath()));
    }

    /** Creates and results the panel for the results area and status. */
    private JPanel getResultsPanel() {
        if (this.resultsPanel == null) {
            this.resultsPanel = new JPanel(new BorderLayout());
            this.resultsPanel.setBorder(null);
            this.resultsPanel.setPreferredSize(new Dimension(0, 200));
            this.resultsPanel.add(new JScrollPane(getResultsArea()));
            this.resultsPanel.add(this.resultsStatus, BorderLayout.SOUTH);
        }
        return this.resultsPanel;
    }

    private JTextArea getResultsArea() {
        if (this.results == null) {
            this.results = new JTextArea();
            this.results.setFont(EDIT_FONT);
            this.results.setText("");
            this.results.setEditable(false);
            this.results.setEnabled(true);
            this.results.setBackground(Color.WHITE);
        }
        return this.results;
    }

    /**
     * Creates the Execute button.
     */
    private JButton createExecuteButton() {
        return Options.createButton(getActions().getPrologFirstResultAction());
    }

    /**
     * Creates the next-result button.
     */
    private JButton getNextResultButton() {
        if (this.nextResultButton == null) {
            this.nextResultButton = Options.createButton(getActions().getPrologNextResultAction());
            this.nextResultButton.setFocusable(true);
        }
        return this.nextResultButton;
    }

    /** Convenience method to retrieve the next-result action. */
    private Action getNextResultAction() {
        return getActions().getPrologNextResultAction();
    }

    /**
     * Creates the predicate tree component.
     */
    private JTree createPredicateTree(final boolean toolTips) {
        final JTree result = new JTree(new DefaultMutableTreeNode()) {
            @Override
            public String getToolTipText(MouseEvent evt) {
                if (!toolTips || getRowForLocation(evt.getX(), evt.getY()) == -1) {
                    return null;
                }
                TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
                Object userObject =
                    ((DefaultMutableTreeNode) curPath.getLastPathComponent()).getUserObject();
                if (userObject instanceof CompoundTermTag) {
                    return getEngine().getEnvironment()
                        .getToolTipText((CompoundTermTag) userObject);
                } else {
                    return null;
                }
            }
        };
        result.setRootVisible(false);
        result.setShowsRootHandles(true);
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) result.getCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        ToolTipManager.sharedInstance()
            .registerComponent(result);
        result.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1) {
                    // when double clicked add the selected predicate (with
                    // template) to the current query
                    TreePath sel = result.getSelectionPath();
                    if (sel != null) {
                        Object o = sel.getLastPathComponent();
                        if (o instanceof DefaultMutableTreeNode) {
                            o = ((DefaultMutableTreeNode) o).getUserObject();
                            if (o instanceof CompoundTermTag) {
                                CompoundTermTag tag = (CompoundTermTag) o;
                                StringBuilder sb =
                                    new StringBuilder(getQueryField().getSelectedItem()
                                        .toString());
                                if (sb.length() > 0 && !sb.toString()
                                    .endsWith(",")) {
                                    sb.append(',');
                                }
                                sb.append(tag.functor.value);
                                if (tag.arity > 0) {
                                    sb.append('(');
                                    for (int i = 0; i < tag.arity; i++) {
                                        if (i > 0) {
                                            sb.append(',');
                                        }
                                        sb.append('_');
                                    }
                                    sb.append(')');
                                }
                                getQueryField().setSelectedItem(sb.toString());
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (e.getSource() == result) {
                    this.manager.setDismissDelay(Integer.MAX_VALUE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (e.getSource() == result) {
                    this.manager.setDismissDelay(this.standardDelay);
                }
            }

            private final ToolTipManager manager = ToolTipManager.sharedInstance();
            private final int standardDelay = this.manager.getDismissDelay();
        });
        return result;
    }

    @Override
    protected void updateGrammar(GrammarModel grammar, boolean fresh) {
        super.updateGrammar(grammar, fresh);
        this.environment = null;
        this.engine = null;
        loadSyntaxHelpTree(getUserTree(), getEnvironment().getUserTags());
    }

    /**
     * Execute the current query
     */
    public void executeQuery() {
        executeQuery(getQueryEdit().getText());
    }

    private GrooveEnvironment getEnvironment() {
        if (this.environment == null) {
            if (getSimulatorModel().getGrammar() == null) {
                this.environment = new GrooveEnvironment(null, getUserOutput());
            } else {
                this.environment = getSimulatorModel().getGrammar()
                    .getPrologEnvironment();
            }
        }
        return this.environment;
    }

    /**
     * Make sure the prolog environment is initialized and clean up previous
     * results.
     */
    private PrologEngine getEngine() {
        this.resultsStatus.setText(" ");
        if (this.engine == null) {
            try {
                this.engine = new PrologEngine(getEnvironment());
            } catch (FormatException e) {
                getResultsArea().append("\nError loading the prolog engine:\n");
                getResultsArea().append(e.getMessage());
            }
        }
        return this.engine;
    }

    /**
     * Execute the given prolog query
     */
    public void executeQuery(String queryString) {
        if (getGrammar() == null) {
            getResultsArea().setText("Please first load a grammar and select a start graph.");
            return;
        }

        if (getGrammar().getStartGraphModel() == null) {
            getResultsArea().setText("Please first select a start graph.");
            return;
        }

        if (getEngine() == null) {
            getResultsArea().setText("Failed to initialize prolog.");
            return;
        }

        if (queryString == null) {
            return;
        }
        queryString = queryString.trim();
        if (queryString.length() == 0) {
            return;
        }
        if (queryString.endsWith(".")) {
            queryString = queryString.substring(0, queryString.length() - 1);
        }

        try {
            addQueryHistory(queryString);
            getResultsArea().setText("?- " + queryString + "\n");

            MatchResult match = getSimulatorModel().getMatch();
            getEngine().setGrooveState(
                new GrooveState(getGrammar().toGrammar(), getSimulatorModel().getGTS(),
                    getSimulatorModel().getState(), match == null ? null : match.getEvent()));

            this.solutionCount = 0;
            processResults(getEngine().newQuery(queryString));
        } catch (Exception e) {
            handlePrologException(e);
        }
    }

    /**
     * Add the query to the history
     */
    private void addQueryHistory(String queryString) {
        JComboBox<String> query = getQueryField();
        query.removeItem(queryString);
        query.insertItemAt(queryString, 0);
        query.setSelectedIndex(0);
        while (query.getItemCount() > MAX_HISTORY) {
            query.removeItemAt(MAX_HISTORY);
        }

        // store the history
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < query.getItemCount(); i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append(query.getItemAt(i));
        }
        PREFS.put("queryHistory", sb.toString());
    }

    /**
     * Handler for the exceptions thrown by the prolog environment
     */
    private void handlePrologException(Throwable e) {
        try {
            getUserOutput().flush();
        } catch (IOException e1) {
            // ignore
        }
        if (e.getCause() instanceof PrologException) {
            PrologException pe = (PrologException) e.getCause();
            if (pe.getCause() == null) {
                getResultsArea().append(e.getCause()
                    .getMessage());
                return;
            } else {
                e = pe;
            }
        }
        // StringWriter sw = new StringWriter();
        // e.printStackTrace(new PrintWriter(sw));
        // this.results.append(sw.toString());
        getResultsArea().append(e.getMessage());
    }

    /**
     * Get the next set of results. Only works after a successful
     * {@link #executeQuery(String)}
     */
    public void nextResults() {
        if (getEngine() != null) {
            getResultsArea().append("\n");
            try {
                processResults(getEngine().next());
            } catch (PrologException e) {
                handlePrologException(e);
            }
        }
    }

    /** Indicates if the last query has outstanding results,
     * i.e., if a call to #nextResults would be successful.
     */
    public boolean hasNextResult() {
        return getEngine() != null && getEngine().hasNext();
    }

    /** Puts the focus on the next result button. */
    public void giveFocusToNextResultButton() {
        assert this.nextResultButton != null;
        this.nextResultButton.requestFocusInWindow();
    }

    /**
     * Pretty print the results of the query in the output panel
     */
    private void processResults(QueryResult queryResult) {
        try {
            getUserOutput().flush();
        } catch (IOException e) {
            // ignore
        }
        JTextArea results = getResultsArea();
        if (!results.getText()
            .endsWith("\n")) {
            results.append("\n");
        }
        if (queryResult == null) {
            results.append("No\n");
            getNextResultAction().setEnabled(false);
        } else {
            switch (queryResult.getReturnValue()) {
            case SUCCESS:
            case SUCCESS_LAST:
                this.solutionCount++;
                for (Entry<String,Object> entry : queryResult.getVariables()
                    .entrySet()) {
                    results.append(entry.getKey());
                    results.append(" = ");
                    if (entry.getValue() instanceof Term) {
                        results.append(TermWriter.toString((Term) entry.getValue()));
                    } else {
                        results.append("" + entry.getValue());
                    }
                    results.append("\n");
                }
                results.append("Yes\n");
                getNextResultAction().setEnabled(true);
                break;
            case FAIL:
                results.append("No\n");
                getNextResultAction().setEnabled(false);
                break;
            case HALT:
                results.append("Interpreter was halted\n");
                break;
            default:
                results.append(String.format("Unexpected return value: %s",
                    getEngine().lastReturnValue()
                        .toString()));
            }
            this.resultsStatus.setText(String.format("%d solution(s); Executed in %fms",
                this.solutionCount,
                queryResult.getExecutionTime() / 1000000.0));
        }
    }

    /** Convenience method to retrieve the grammar view from the simulator. */
    private GrammarModel getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /** The environment, initialised from the grammar view. */
    private GrooveEnvironment environment;
    /**
     * The current instance of the prolog interpreter. Will be recreated every
     * time "reconsult" action is performed.
     */
    private PrologEngine engine;
    private JComboBox<String> queryField;
    private JTextComponent queryEdit;
    /** Panel for the results area and status bar. */
    private JPanel resultsPanel;
    /** Text area for the results. */
    private JTextArea results;
    /** Status bar for the results area. */
    private final JLabel resultsStatus = new JLabel(" ");
    private OutputStream userOutput;

    private JTabbedPane syntaxHelp;
    /**
     * The tree of built-in Prolog predicates
     */
    private JTree prologTree;
    /**
     * The tree of Groove predicates
     */
    private JTree grooveTree;
    /**
     * The tree of user-defined predicates
     */
    private JTree userTree;
    /**
     * The button associated with the next solution action.
     */
    private JButton nextResultButton;
    /**
     * Counter used to show the number of found solutions (so far)
     */
    private int solutionCount;
    final static Font EDIT_FONT = new Font("Monospaced", Font.PLAIN, 12);

    /**
     * Class used to redirect the standard output stream used by prolog to the
     * output panel
     *
     * @author Michiel Hendriks
     */
    static class JTextAreaOutputStream extends OutputStream {
        JTextArea dest;

        static final int BUFFER_SIZE = 512;
        int[] buffer = new int[BUFFER_SIZE];
        int pos = 0;

        JTextAreaOutputStream(JTextArea toArea) {
            this.dest = toArea;
        }

        @Override
        public void write(int arg0) throws IOException {
            this.buffer[this.pos++] = arg0;
            if (this.pos >= this.buffer.length) {
                flush();
            }
        }

        @Override
        public void flush() throws IOException {
            if (this.pos == 0) {
                return;
            }
            this.dest.append(new String(this.buffer, 0, this.pos));
            this.buffer = new int[BUFFER_SIZE];
            this.pos = 0;
        }
    }
}
