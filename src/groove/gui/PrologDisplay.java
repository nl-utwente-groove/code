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
package groove.gui;

import gnu.prolog.io.TermWriter;
import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.PrologException;
import groove.gui.DisplaysPanel.DisplayKind;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.ActionStore;
import groove.prolog.GrooveEnvironment;
import groove.prolog.GrooveState;
import groove.prolog.PrologEngine;
import groove.prolog.QueryResult;
import groove.prolog.exception.GroovePrologException;
import groove.prolog.exception.GroovePrologLoadingException;
import groove.view.PrologView;
import groove.view.StoredGrammarView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * The Prolog editor tab for the improved simulator
 * 
 * @author Michiel Hendriks
 */
public class PrologDisplay extends JPanel implements Display, SimulatorListener {
    private static final long serialVersionUID = 1728208313657610091L;
    private static final int MAX_HISTORY = 50;

    static final Preferences PREFS =
        Preferences.userNodeForPackage(PrologDisplay.class);

    /**
     * Construct a prolog panel
     */
    public PrologDisplay(Simulator simulator) {
        this.simulator = simulator;
        setLayout(new BorderLayout());

        JPanel queryPane = new JPanel(new BorderLayout());
        queryPane.add(getQueryField(), BorderLayout.CENTER);
        queryPane.add(createExecuteButton(), BorderLayout.EAST);

        JPanel editorPane = new JPanel(new BorderLayout());
        editorPane.add(createToolBar(), BorderLayout.NORTH);
        editorPane.add(getEditorPane(), BorderLayout.CENTER);

        Environment.setDefaultOutputStream(getUserOutput());

        JPanel resultsPane = new JPanel(new BorderLayout());
        resultsPane.add(new JScrollPane(getResultsArea()), BorderLayout.CENTER);
        resultsPane.add(getNextResultButton(), BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setBorder(null);
        splitPane.setOneTouchExpandable(true);
        splitPane.setTopComponent(editorPane);
        splitPane.setBottomComponent(resultsPane);

        JPanel mainPane = new JPanel(new BorderLayout());
        mainPane.add(queryPane, BorderLayout.NORTH);
        mainPane.add(splitPane, BorderLayout.CENTER);

        JSplitPane sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        sp2.setResizeWeight(0.9);
        sp2.setBorder(null);
        sp2.setOneTouchExpandable(true);
        sp2.setRightComponent(createSyntaxHelp());
        sp2.setLeftComponent(mainPane);
        add(sp2, BorderLayout.CENTER);

        add(this.statusBar, BorderLayout.SOUTH);
        simulator.getModel().addListener(this, Change.GRAMMAR, Change.PROLOG);
        this.listening = true;
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
     * Creates and returns the tabbed pane on which the editors are placed.
     */
    private JTabbedPane getEditorPane() {
        if (this.editorPane == null) {
            this.editorPane =
                new JTabbedPane(SwingConstants.BOTTOM,
                    JTabbedPane.SCROLL_TAB_LAYOUT);
            this.editorPane.setMinimumSize(new Dimension(0, 200));
            this.editorPane.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (PrologDisplay.this.listening) {
                        PrologDisplay.this.listening = false;
                        getSimulatorModel().setProlog(
                            ((PrologEditor) PrologDisplay.this.editorPane.getSelectedComponent()).getName());
                        PrologDisplay.this.listening = true;
                    }

                }
            });
        }
        return this.editorPane;
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
    private JComboBox getQueryField() {
        if (this.queryField == null) {
            this.queryField =
                new JComboBox(PREFS.get("queryHistory", "").split("\\n"));
            this.queryField.setFont(EDIT_FONT);
            this.queryField.setEditable(true);
            this.queryField.setEnabled(true);
            this.queryField.setPrototypeDisplayValue("groove+prolog");
            this.queryEdit =
                (JTextComponent) this.queryField.getEditor().getEditorComponent();
            this.queryEdit.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        executeQuery();
                    }
                }
            });
        }
        return this.queryField;
    }

    /**
     * Creates the panel with documentation trees.
     */
    private JTabbedPane createSyntaxHelp() {
        JTabbedPane treePane = new JTabbedPane();
        // create the groove predicate tree
        this.grooveTree = createPredicateTree(true);
        treePane.add("Groove", new JScrollPane(this.grooveTree));
        loadSyntaxHelpTree(this.grooveTree, getEnvironment().getGrooveTags());
        // create the prolog predicate tree
        this.prologTree = createPredicateTree(false);
        treePane.add("Prolog", new JScrollPane(this.prologTree));
        loadSyntaxHelpTree(this.prologTree, getEnvironment().getPrologTags());
        // create the user predicate tree
        this.userTree = createPredicateTree(false);
        treePane.add("User", new JScrollPane(this.userTree));
        loadSyntaxHelpTree(this.userTree, getEnvironment().getUserTags());
        return treePane;
    }

    /** Loads a tree with a given set of tags. */
    private void loadSyntaxHelpTree(JTree tree, Set<CompoundTermTag> tags) {
        DefaultMutableTreeNode rootNode =
            (DefaultMutableTreeNode) tree.getModel().getRoot();
        rootNode.removeAllChildren();
        Map<AtomTerm,DefaultMutableTreeNode> nodes =
            new HashMap<AtomTerm,DefaultMutableTreeNode>();
        for (CompoundTermTag tag : tags) {
            DefaultMutableTreeNode baseNode = nodes.get(tag.functor);
            if (baseNode == null) {
                baseNode = new DefaultMutableTreeNode(tag);
                rootNode.add(baseNode);
                nodes.put(tag.functor, baseNode);
            } else {
                if (baseNode.getChildCount() == 0) {
                    baseNode.add(new DefaultMutableTreeNode(
                        baseNode.getUserObject()));
                    baseNode.setUserObject(tag.functor.value);
                }
                DefaultMutableTreeNode predNode =
                    new DefaultMutableTreeNode(tag);
                baseNode.add(predNode);
            }
        }
        ((DefaultTreeModel) tree.getModel()).reload();
        tree.expandPath(new TreePath(rootNode.getPath()));
    }

    /**
     * Creates a tool bar for the display.
     */
    private JToolBar createToolBar() {
        JToolBar result = Options.createToolBar();
        result.add(new JLabel("User code:"));
        result.addSeparator();
        result.add(createSaveButton());
        return result;
    }

    private JTextArea getResultsArea() {
        if (this.results == null) {
            this.results = new JTextArea();
            this.results.setFont(EDIT_FONT);
            this.results.setText("");
            this.results.setEditable(false);
            this.results.setEnabled(true);
            this.results.setBackground(null);
        }
        return this.results;
    }

    @Override
    public DisplayKind getKind() {
        return DisplayKind.PROLOG;
    }

    @Override
    public JComponent getPanel() {
        return this;
    }

    @Override
    public String getName() {
        PrologView prolog = getSimulatorModel().getProlog();
        return prolog == null ? null : prolog.getName();
    }

    /**
     * Creates the Execute button.
     */
    private JButton createExecuteButton() {
        JButton execQuery = new JButton("Execute");
        execQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                executeQuery();
            }
        });
        return execQuery;
    }

    /**
     * Creates the next-result button.
     */
    private JButton getNextResultButton() {
        if (this.nextResultBtn == null) {
            JButton result = new JButton("More?");
            result.setFont(result.getFont().deriveFont(Font.BOLD));
            result.setVisible(false);
            result.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    nextResults();
                }
            });
            this.nextResultBtn = result;
        }
        return this.nextResultBtn;
    }

    /**
     * Creates the predicate tree component.
     */
    private JTree createPredicateTree(final boolean toolTips) {
        final JTree result = new JTree(new DefaultMutableTreeNode()) {
            @Override
            public String getToolTipText(MouseEvent evt) {
                if (!toolTips
                    || getRowForLocation(evt.getX(), evt.getY()) == -1) {
                    return null;
                }
                TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
                Object userObject =
                    ((DefaultMutableTreeNode) curPath.getLastPathComponent()).getUserObject();
                if (userObject instanceof CompoundTermTag) {
                    return getEngine().getEnvironment().getToolTipText(
                        (CompoundTermTag) userObject);
                } else {
                    return null;
                }
            }
        };
        result.setRootVisible(false);
        result.setShowsRootHandles(true);
        DefaultTreeCellRenderer renderer =
            (DefaultTreeCellRenderer) result.getCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        ToolTipManager.sharedInstance().registerComponent(result);
        result.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1
                    && e.getButton() == MouseEvent.BUTTON1) {
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
                                    new StringBuilder(
                                        getQueryField().getSelectedItem().toString());
                                if (sb.length() > 0
                                    && !sb.toString().endsWith(",")) {
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

            private final ToolTipManager manager =
                ToolTipManager.sharedInstance();
            private final int standardDelay = this.manager.getDismissDelay();
        });
        return result;
    }

    /**
     * Creates and returns the panel with the control programs list.
     */
    public JPanel getListPanel() {
        if (this.prologListPanel == null) {
            JToolBar toolBar = Options.createToolBar();
            toolBar.add(getActions().getNewPrologAction());
            toolBar.add(getActions().getEditPrologAction());
            toolBar.addSeparator();
            toolBar.add(getActions().getCopyPrologAction());
            toolBar.add(getActions().getDeletePrologAction());
            toolBar.add(getActions().getRenamePrologAction());

            JScrollPane prologPane = new JScrollPane(getPrologList()) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension superSize = super.getPreferredSize();
                    return new Dimension((int) superSize.getWidth(),
                        Simulator.START_LIST_MINIMUM_HEIGHT);
                }
            };

            this.prologListPanel = new JPanel(new BorderLayout(), false);
            this.prologListPanel.add(toolBar, BorderLayout.NORTH);
            this.prologListPanel.add(prologPane, BorderLayout.CENTER);
            // make sure tool tips get displayed
            ToolTipManager.sharedInstance().registerComponent(
                this.prologListPanel);
        }
        return this.prologListPanel;
    }

    /** Returns the list of control programs. */
    public PrologJList getPrologList() {
        if (this.prologJList == null) {
            this.prologJList = new PrologJList(getSimulator());
        }
        return this.prologJList;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (this.listening) {
            this.listening = false;
            if (changes.contains(Change.GRAMMAR)) {
                StoredGrammarView grammar = source.getGrammar();
                for (PrologEditor editor : this.editorMap.values()) {
                    if (grammar == null
                        || !grammar.getPrologNames().contains(editor.getName())) {
                        editor.dispose();
                    }
                }
                if (grammar != null) {
                    this.environment = null;
                    this.engine = null;
                }
                loadSyntaxHelpTree(this.userTree,
                    getEnvironment().getUserTags());
            }
            if (changes.contains(Change.PROLOG)) {
                PrologEditor editor =
                    this.editorMap.get(source.getProlog().getName());
                if (editor != null) {
                    getEditorPane().setSelectedComponent(editor);
                }
            }
            this.listening = true;
        }
    }

    /**
     * Create a new prolog editor tab
     */
    public void createEditor(String title) {
        if (this.editorMap.containsKey(title)) {
            PrologEditor editor = this.editorMap.get(title);
            getEditorPane().setSelectedComponent(editor);
            return;
        }

        String program =
            getSimulatorModel().getGrammar().getPrologView(title).getProgram();
        final PrologEditor editor = new PrologEditor(this, title, program);
        this.editorMap.put(title, editor);
        this.editors.add(editor);
        getEditorPane().addTab(title, editor);
        getEditorPane().setSelectedComponent(editor);
    }

    /** Returns the editor for a control program with a given name, if any. */
    public PrologEditor getEditor(String title) {
        return this.editorMap.get(title);
    }

    /**
     * Cancels the current editing action, if any.
     * @param confirm indicates if the user should be asked for confirmation
     * @return if editing was indeed stopped
     */
    public boolean cancelEditing(String name, boolean confirm) {
        boolean result = true;
        PrologEditor editor = this.editorMap.get(name);
        if (editor != null && editor.isDirty()) {
            if (!confirm || editor.confirmAbandon()) {
                editor.dispose();
            } else {
                result = false;
            }
        }
        return result;
    }

    /**
     * Execute the current query
     */
    private void executeQuery() {
        executeQuery(getQueryEdit().getText());
    }

    private GrooveEnvironment getEnvironment() {
        if (this.environment == null) {
            if (getSimulatorModel().getGrammar() == null) {
                this.environment = new GrooveEnvironment(null, getUserOutput());
            } else {
                this.environment =
                    getSimulatorModel().getGrammar().getPrologEnvironment(
                        getUserOutput());
            }
        }
        return this.environment;
    }

    /**
     * Make sure the prolog environment is initialized and clean up previous
     * results.
     */
    private PrologEngine getEngine() {
        this.statusBar.setText(" ");
        if (this.engine == null) {
            this.engine = PrologEngine.instance();
            try {
                this.engine.setEnvironment(getEnvironment());
                this.engine.init();
            } catch (GroovePrologLoadingException e) {
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
            getResultsArea().setText(
                "Please first load a grammar and select a start graph.");
            return;
        }

        if (getGrammar().getStartGraphView() == null) {
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

            getEngine().setGrooveState(
                new GrooveState(getGrammar().toGrammar(),
                    getSimulatorModel().getGts(),
                    getSimulatorModel().getState(),
                    getSimulatorModel().getMatch()));

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
        JComboBox query = getQueryField();
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
            e1.printStackTrace();
        }
        if (e.getCause() instanceof PrologException) {
            PrologException pe = (PrologException) e.getCause();
            if (pe.getCause() == null) {
                getResultsArea().append(e.getCause().getMessage());
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
    private void nextResults() {
        if (getEngine() == null || !getEngine().hasNext()) {
            return;
        }
        getResultsArea().append("\n");
        try {
            processResults(getEngine().next());
        } catch (GroovePrologException e) {
            handlePrologException(e);
        }
    }

    /**
     * Pretty print the results of the query in the output panel
     */
    private void processResults(QueryResult queryResult) {
        try {
            getUserOutput().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (queryResult == null) {
            return;
        }
        JTextArea results = getResultsArea();
        if (!results.getText().endsWith("\n")) {
            results.append("\n");
        }
        switch (queryResult.getReturnValue()) {
        case SUCCESS:
        case SUCCESS_LAST:
            ++this.solutionCount;
            for (Entry<String,Object> entry : queryResult.getVariables().entrySet()) {
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
            break;
        case FAIL:
            results.append("No\n");
            break;
        case HALT:
            results.append("Interpreter was halted\n");
            break;
        default:
            results.append(String.format("Unexpected return value: %s",
                getEngine().lastReturnValue().toString()));
        }
        getNextResultButton().setVisible(getEngine().hasNext());
        if (getNextResultButton().isVisible()) {
            getNextResultButton().grabFocus();
        }
        this.statusBar.setText(String.format(
            "%d solution(s); Executed in %fms", this.solutionCount,
            queryResult.getExecutionTime() / 1000000.0));
    }

    /** Convenience method to retrieve the simulator. */
    private Simulator getSimulator() {
        return this.simulator;
    }

    /** Convenience method to retrieve the simulator state. */
    private SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Convenience method to retrieve the action store. */
    private ActionStore getActions() {
        return getSimulator().getActions();
    }

    /** Convenience method to retrieve the grammar view from the simulator. */
    private StoredGrammarView getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /**
     * The Simulator UI
     */
    private final Simulator simulator;
    /** The environment, initialised from the grammar view. */
    private GrooveEnvironment environment;
    /**
     * The current instance of the prolog interpreter. Will be recreated every
     * time "reconsult" action is performed.
     */
    private PrologEngine engine;
    private JComboBox queryField;
    private JTextComponent queryEdit;
    private JTextArea results;
    private JButton nextResultBtn;
    private final JLabel statusBar = new JLabel(" ");
    private OutputStream userOutput;
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

    private JTabbedPane editorPane;
    private Map<String,PrologEditor> editorMap =
        new HashMap<String,PrologEditor>();
    private Set<PrologEditor> editors = new HashSet<PrologEditor>();

    /** panel on which the prolog list (and toolbar) are displayed. */
    private JPanel prologListPanel;

    private boolean listening;
    /** Production system prolog program list. */
    private PrologJList prologJList;

    /**
     * Counter used to show the number of found solutions (so far)
     */
    private int solutionCount;
    private final static Font EDIT_FONT =
        new Font("Monospaced", Font.PLAIN, 12);

    /**
     * Creates the save button.
     */
    private JButton createSaveButton() {
        return Options.createButton(getActions().getSavePrologAction());
    }

    /**
     * Data structure to keep track of the open/loaded prolog files
     * 
     * @author Michiel Hendriks
     */
    public static class PrologEditor extends JPanel {
        /** 
         * Constructs a prolog editor with a given name.
         * @param display the display on which this editor is placed.
         */
        public PrologEditor(final PrologDisplay display, String name,
                String program) {
            this.display = display;
            this.name = name;
            this.editor = new RSyntaxTextArea(25, 100);
            this.editor.setFont(EDIT_FONT);
            this.editor.setText(program);
            this.editor.setEditable(true);
            this.editor.setEnabled(true);
            this.editor.setTabSize(4);
            this.editor.discardAllEdits();
            setBorder(null);
            setLayout(new BorderLayout());
            add(new RTextScrollPane(this.editor, true));
            this.editor.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent arg0) {
                        updateTab();
                    }

                    public void insertUpdate(DocumentEvent arg0) {
                        updateTab();
                    }

                    public void removeUpdate(DocumentEvent arg0) {
                        updateTab();
                    }
                });
        }

        /** Indicates if the editor is currently dirty. */
        public final boolean isDirty() {
            return this.editor.canUndo();
        }

        /** Returns the file from which the editor was loaded. */
        @Override
        public final String getName() {
            return this.name;
        }

        /** Returns the current program. */
        public final String getProgram() {
            return this.editor.getText();
        }

        /** Returns the editor panel. */
        public final RSyntaxTextArea getEditor() {
            return this.editor;
        }

        /**
         * Creates and shows a confirmation dialog for abandoning the currently
         * edited control program.
         */
        public boolean confirmAbandon() {
            boolean result = true;
            if (isDirty()) {
                int answer =
                    JOptionPane.showConfirmDialog(this,
                        String.format("Save changes in '%s'?", getName()),
                        null, JOptionPane.YES_NO_CANCEL_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    this.display.getActions().getSavePrologAction().doSave(
                        getName(), getProgram());
                } else {
                    result = answer == JOptionPane.NO_OPTION;
                }
            }
            return result;
        }

        /** Removes this editor from the editor pane. */
        public void dispose() {
            this.display.getEditorPane().remove(this);
        }

        /** Discards the edit history. */
        public void discardEdits() {
            this.editor.discardAllEdits();
            updateTab();
        }

        /**
         * Update the tab title
         */
        protected void updateTab() {
            JTabbedPane editorPane = this.display.getEditorPane();
            int index = editorPane.indexOfComponent(PrologEditor.this);
            editorPane.setTitleAt(index, (isDirty() ? "* " : "") + getName());
            this.display.getActions().getSavePrologAction().refresh();
        }

        /**
         * The display on which the editor is placed.
         */
        private final PrologDisplay display;
        /**
         * The name of the editor.
         */
        private final String name;
        /**
         * The associated editor
         */
        private final RSyntaxTextArea editor;
    }

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
