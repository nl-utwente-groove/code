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

import gnu.prolog.database.Module;
import gnu.prolog.io.TermWriter;
import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.PrologException;
import groove.gui.DisplaysPanel.DisplayKind;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.SimulatorAction;
import groove.prolog.GrooveState;
import groove.prolog.PrologEngine;
import groove.prolog.QueryResult;
import groove.prolog.exception.GroovePrologException;
import groove.prolog.exception.GroovePrologLoadingException;
import groove.view.PrologView;
import groove.view.StoredGrammarView;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
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
        this.sim = simulator;
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
        consultUserCode();
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
        this.prologRootNode = new DefaultMutableTreeNode("Prolog", true);
        this.prologTree = createPredicateTree(this.prologRootNode, false);
        this.grooveRootNode = new DefaultMutableTreeNode("Prolog", true);
        this.grooveTree = createPredicateTree(this.grooveRootNode, true);
        this.userRootNode = new DefaultMutableTreeNode("Prolog", true);
        this.userTree = createPredicateTree(this.userRootNode, false);
        treePane.add("Groove", new JScrollPane(this.grooveTree));
        treePane.add("Prolog", new JScrollPane(this.prologTree));
        treePane.add("User", new JScrollPane(this.userTree));
        return treePane;
    }

    /**
     * Creates a tool bar for the display.
     */
    private JToolBar createToolBar() {
        JToolBar result = Options.createToolBar();
        result.add(new JLabel("User code:"));
        result.addSeparator();
        result.add(createNewButton());
        result.add(createEditButton());
        result.add(createSaveButton());
        result.add(getCloseButton());
        result.addSeparator();
        result.add(createConsultButton());

        this.userCodeConsulted = new JLabel("");
        this.userCodeConsulted.setFont(this.userCodeConsulted.getFont().deriveFont(
            Font.BOLD));
        result.addSeparator();
        result.add(this.userCodeConsulted);
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
     * Creates the consult button.
     */
    private JButton createConsultButton() {
        JButton result = new JButton("Consult");
        result.setToolTipText("Reconsult the prolog code. This will cancel the current active query.");
        result.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (!confirmDirty()) {
                    return;
                }
                consultUserCode();
            }
        });
        return result;
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
    private JTree createPredicateTree(TreeNode rootNode, final boolean toolTips) {
        final JTree result = new JTree(rootNode) {
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
                    return PrologDisplay.this.engine.getToolTipText((CompoundTermTag) userObject);
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
            getPrologList().fillToolBar(toolBar);
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

    }

    /**
     * Create a new prolog editor tab
     * 
     * @param delayLoading
     *            if false then do not consule the file, used in case of new
     *            files
     */
    private void createEditor(File file, boolean delayLoading) {
        if (file != null && this.prologFileMap.containsKey(file)) {
            PrologEditor proFile = this.prologFileMap.get(file);
            getEditorPane().setSelectedComponent(proFile);
            return;
        }

        final PrologEditor proFile = new PrologEditor(file);
        String title = "* untitled.pro";
        if (file != null) {
            title = file.getName();
            this.prologFileMap.put(file, proFile);
        }
        this.prologFiles.add(proFile);
        getEditorPane().addTab(title, proFile);
        getEditorPane().setSelectedComponent(proFile);

        if (file != null) {
            try {
                FileReader fis = new FileReader(file);
                proFile.getEditor().read(fis, null);
                if (delayLoading) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            consultUserCode();
                        }
                    });
                }
            } catch (IOException eex) {
                eex.printStackTrace();
            }
        }

        proFile.getEditor().getDocument().addDocumentListener(
            new DocumentListener() {

                /**
                 * Update the tab title
                 */
                protected void updateTab() {
                    if (proFile.isDirty()) {
                        return;
                    }
                    proFile.setDirty(true);
                    String title = "untitled.pro";
                    if (proFile.getFile() != null) {
                        title = proFile.getFile().getName();
                    }
                    int index = getEditorPane().indexOfComponent(proFile);
                    getEditorPane().setTitleAt(index, "* " + title);
                }

                public void changedUpdate(DocumentEvent arg0) {
                    PrologDisplay.this.userCodeConsulted.setText("Modified");
                    updateTab();
                }

                public void insertUpdate(DocumentEvent arg0) {
                    PrologDisplay.this.userCodeConsulted.setText("Modified");
                    updateTab();
                }

                public void removeUpdate(DocumentEvent arg0) {
                    PrologDisplay.this.userCodeConsulted.setText("Modified");
                    updateTab();
                }
            });
    }

    /**
     * Execute the current query
     */
    private void executeQuery() {
        executeQuery(getQueryEdit().getText());
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
                updatePredicateTree(this.engine);
                this.engine.init();
            } catch (GroovePrologLoadingException e) {
                getResultsArea().append("\nError loading the prolog engine:\n");
                getResultsArea().append(e.getMessage());
            }
        }
        return this.engine;
    }

    /**
     * Update the predicate trees with all known predicates
     */
    private void updatePredicateTree(PrologEngine engine) {
        this.prologRootNode.removeAllChildren();
        this.grooveRootNode.removeAllChildren();
        this.userRootNode.removeAllChildren();
        Map<AtomTerm,DefaultMutableTreeNode> nodes =
            new HashMap<AtomTerm,DefaultMutableTreeNode>();
        SortedSet<CompoundTermTag> tags =
            new TreeSet<CompoundTermTag>(new Comparator<CompoundTermTag>() {
                public int compare(CompoundTermTag o1, CompoundTermTag o2) {
                    int rc = o1.functor.value.compareTo(o2.functor.value);
                    if (rc == 0) {
                        rc = o1.arity - o2.arity;
                    }
                    return rc;
                }
            });
        Module module = engine.getEnvironment().getModule();
        Set<CompoundTermTag> prologTags = engine.getPrologTags();
        Set<CompoundTermTag> grooveTags = engine.getGrooveTags();
        tags.addAll(module.getPredicateTags());
        for (CompoundTermTag tag : tags) {
            DefaultMutableTreeNode baseNode = nodes.get(tag.functor);
            if (baseNode == null) {
                baseNode = new DefaultMutableTreeNode(tag);
                if (prologTags.contains(tag)) {
                    this.prologRootNode.add(baseNode);
                } else if (grooveTags.contains(tag)) {
                    this.grooveRootNode.add(baseNode);
                } else {
                    this.userRootNode.add(baseNode);
                }
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
        ((DefaultTreeModel) this.prologTree.getModel()).reload();
        this.prologTree.expandPath(new TreePath(this.prologRootNode.getPath()));
        ((DefaultTreeModel) this.grooveTree.getModel()).reload();
        this.grooveTree.expandPath(new TreePath(this.grooveRootNode.getPath()));
        ((DefaultTreeModel) this.userTree.getModel()).reload();
        this.userTree.expandPath(new TreePath(this.userRootNode.getPath()));
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

    /**
     * (re)consult the user code. This doesn't execute any queries
     */
    private void consultUserCode() {
        // reset the prolog base
        getEngine().reset();
        getNextResultButton().setVisible(false);
        getResultsArea().setText("");
        try {
            Environment env = getEngine().getEnvironment();
            for (PrologEditor pf : this.prologFiles) {
                if (pf.file == null) {
                    continue;
                }
                CompoundTerm term =
                    new CompoundTerm(
                        AtomTerm.get("file"),
                        new Term[] {AtomTerm.get(pf.getFile().getAbsolutePath())});
                env.ensureLoaded(term);
                if (!env.getLoadingErrors().isEmpty()) {
                    throw new GroovePrologLoadingException(
                        env.getLoadingErrors());
                }
            }
            this.userCodeConsulted.setText("User code consulted");
            this.statusBar.setText("User code accepted");
        } catch (GroovePrologLoadingException e) {
            this.userCodeConsulted.setText("Error");
            getResultsArea().append("\nError loading the prolog engine:\n");
            getResultsArea().append(e.getMessage());
            this.engine = null;
        }
    }

    /**
     * Prompt the user when there are dirty editors
     */
    private boolean confirmDirty() {
        boolean dirty = false;
        for (PrologEditor pf : this.prologFiles) {
            if (pf.isDirty()) {
                dirty = true;
                break;
            }
        }
        if (dirty) {
            int overwrite =
                JOptionPane.showConfirmDialog(
                    this.sim.getFrame(),
                    "You have got unsaved changes. Are you sure you want to consult the files on disk?",
                    "Ignore changes?", JOptionPane.YES_NO_OPTION);
            if (overwrite == JOptionPane.NO_OPTION) {
                return false;
            }
        }
        return true;
    }

    /** Convenience method to retrieve the simulator. */
    private Simulator getSimulator() {
        return this.sim;
    }

    /** Convenience method to retrieve the simulator state. */
    private SimulatorModel getSimulatorModel() {
        return this.sim.getModel();
    }

    /** Convenience method to retrieve the grammar view from the simulator. */
    private StoredGrammarView getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /**
     * Counter used to show the number of found solutions (so far)
     */
    private int solutionCount;
    /**
     * The Simulator UI
     */
    private Simulator sim;
    /**
     * The current instance of the prolog interpreter. Will be recreated every
     * time "reconsult" action is performed.
     */
    private PrologEngine engine;
    private JComboBox queryField;
    private JTextComponent queryEdit;
    private JTextArea results;
    private JButton nextResultBtn;
    private JLabel userCodeConsulted;
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

    /**
     * Root node for built-in predicates tree
     */
    private DefaultMutableTreeNode prologRootNode;
    /**
     * Root node for groove predicates tree
     */
    private DefaultMutableTreeNode grooveRootNode;
    /**
     * Root node for user-defined predicates tree
     */
    private DefaultMutableTreeNode userRootNode;
    private JTabbedPane editorPane;
    private Map<File,PrologEditor> prologFileMap =
        new HashMap<File,PrologEditor>();
    private Set<PrologEditor> prologFiles = new HashSet<PrologEditor>();

    /** panel on which the prolog list (and toolbar) are displayed. */
    private JPanel prologListPanel;

    /** Production system prolog program list. */
    private PrologJList prologJList;

    /**
     * Creates the New button.
     */
    private JButton createNewButton() {
        return Options.createButton(getNewPrologAction());
    }

    /** Constructs and returns a new prolog creation action for a given simulator. */
    public NewPrologAction getNewPrologAction() {
        if (this.newPrologAction == null) {
            this.newPrologAction = new NewPrologAction(getSimulator());
        }
        return this.newPrologAction;
    }

    private NewPrologAction newPrologAction;

    private final static Font EDIT_FONT =
        new Font("Monospaced", Font.PLAIN, 12);

    /** Action that creates a new Prolog file. */
    public static class NewPrologAction extends SimulatorAction {
        /** Constructs an instance of this action for a given simulator. */
        public NewPrologAction(Simulator simulator) {
            super(simulator, Options.NEW_PROLOG_ACTION_NAME,
                Icons.NEW_PROLOG_ICON);
        }

        @Override
        public boolean execute() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    getPrologPanel().createEditor(null, false);
                }
            });
            return false;
        }
    }

    /**
     * Creates the load button.
     */
    private JButton createEditButton() {
        return Options.createButton(getEditPrologAction());
    }

    /** Constructs and returns a new prolog creation action for a given simulator. */
    public EditPrologAction getEditPrologAction() {
        if (this.editPrologAction == null) {
            this.editPrologAction = new EditPrologAction(getSimulator());
        }
        return this.editPrologAction;
    }

    private EditPrologAction editPrologAction;

    /** Action that creates a new Prolog file. */
    public static class EditPrologAction extends SimulatorAction {
        /** Constructs an instance of this action for a given simulator. */
        public EditPrologAction(Simulator simulator) {
            super(simulator, Options.EDIT_PROLOG_ACTION_NAME,
                Icons.EDIT_PROLOG_ICON);
        }

        @Override
        public boolean execute() {
            getPrologFileChooser().setMultiSelectionEnabled(true);
            int result =
                getPrologFileChooser().showOpenDialog(getPrologPanel());
            // now load, if so required
            if (result == JFileChooser.APPROVE_OPTION) {
                final File[] files = getPrologFileChooser().getSelectedFiles();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (File fl : files) {
                            getPrologPanel().createEditor(fl, true);
                        }
                        getPrologPanel().consultUserCode();
                    }
                });
            }
            return false;
        }
    }

    /**
     * Creates the save button.
     */
    private JButton createSaveButton() {
        return Options.createButton(getSavePrologAction());
    }

    /** Constructs and returns a new prolog creation action for a given simulator. */
    public SavePrologAction getSavePrologAction() {
        if (this.savePrologAction == null) {
            this.savePrologAction = new SavePrologAction(getSimulator());
        }
        return this.savePrologAction;
    }

    private SavePrologAction savePrologAction;

    /** Action that saves all Prolog files. */
    public static class SavePrologAction extends SimulatorAction {
        /** Constructs an instance of this action for a given simulator. */
        public SavePrologAction(Simulator simulator) {
            super(simulator, Options.SAVE_ACTION_NAME, Icons.SAVE_ICON);
        }

        @Override
        public boolean execute() {
            Component comp =
                getPrologPanel().getEditorPane().getSelectedComponent();
            if (comp == null) {
                return false;
            }
            PrologEditor proFile = null;
            for (PrologEditor pf : getPrologPanel().prologFiles) {
                if (pf == comp) {
                    proFile = pf;
                }
            }
            if (proFile == null) {
                return false;
            }

            // select a filename
            if (proFile.getFile() == null) {
                JFileChooser fc = getPrologFileChooser();
                fc.setMultiSelectionEnabled(false);
                fc.setSelectedFile(null);
                do {
                    int result = fc.showSaveDialog(getPrologPanel());
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File fl = getPrologFileChooser().getSelectedFile();
                        if (fl.exists()) {
                            int overwrite =
                                JOptionPane.showConfirmDialog(
                                    getPrologPanel(),
                                    "Overwrite existing file \""
                                        + fl.getName()
                                        + "\"?"
                                        + (getPrologPanel().prologFileMap.containsKey(fl)
                                                ? "\nThis will also discard the current editor for this file."
                                                : ""));
                            if (overwrite == JOptionPane.NO_OPTION) {
                                continue;
                            } else if (overwrite == JOptionPane.CANCEL_OPTION) {
                                break;
                            }
                        }

                        if (getPrologPanel().prologFileMap.containsKey(fl)) {
                            PrologEditor other =
                                getPrologPanel().prologFileMap.get(fl);
                            getPrologPanel().getEditorPane().remove(other);
                            getPrologPanel().prologFileMap.remove(fl);
                            getPrologPanel().prologFiles.remove(other);
                        }
                        proFile.setFile(fl);
                        getPrologPanel().prologFileMap.put(fl, proFile);
                        break;
                    }
                } while (true);
            }

            try {
                proFile.getEditor().write(new FileWriter(proFile.getFile()));
                proFile.setDirty(false);
                int index =
                    getPrologPanel().getEditorPane().indexOfComponent(proFile);
                if (index > -1) {
                    getPrologPanel().getEditorPane().setTitleAt(index,
                        proFile.getFile().getName());
                }
            } catch (IOException eex) {
                eex.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Creates the close button.
     */
    private JButton getCloseButton() {
        return Options.createButton(getDeletePrologAction());
    }

    /** Constructs and returns a prolog delete action for this display. */
    public DeletePrologAction getDeletePrologAction() {
        if (this.deletePrologAction == null) {
            this.deletePrologAction = new DeletePrologAction(getSimulator());
        }
        return this.deletePrologAction;
    }

    private DeletePrologAction deletePrologAction;

    /** Action that deletes a Prolog program. */
    public static class DeletePrologAction extends SimulatorAction {
        /** Constructs an instance of this action for a given simulator. */
        public DeletePrologAction(Simulator simulator) {
            super(simulator, Options.DELETE_ACTION_NAME, Icons.DELETE_ICON);
        }

        @Override
        public boolean execute() {
            Component comp =
                getPrologPanel().getEditorPane().getSelectedComponent();
            if (comp == null) {
                return false;
            }
            PrologEditor proFile = null;
            for (PrologEditor pf : getPrologPanel().prologFiles) {
                if (pf == comp) {
                    proFile = pf;
                }
            }
            if (proFile == null) {
                return false;
            }
            if (proFile.isDirty()) {
                int overwrite =
                    JOptionPane.showConfirmDialog(
                        getPrologPanel(),
                        "You have got unsaved changes. Are you sure you want to close this file and discard the changes?",
                        "Discard changes?", JOptionPane.YES_NO_OPTION);
                if (overwrite == JOptionPane.NO_OPTION) {
                    return false;
                }
            }

            getPrologPanel().getEditorPane().remove(proFile);
            getPrologPanel().prologFileMap.remove(proFile.getFile());
            getPrologPanel().prologFiles.remove(proFile);
            getPrologPanel().consultUserCode();
            return false;
        }
    }

    /**
     * Data structure to keep track of the open/loaded prolog files
     * 
     * @author Michiel Hendriks
     */
    public static class PrologEditor extends JPanel {
        /** Constructs a prolog editor from a given file. */
        public PrologEditor(File file) {
            this.file = file;
            this.dirty = file == null;
            this.editor = new RSyntaxTextArea(25, 100);
            this.editor.setFont(EDIT_FONT);
            this.editor.setText("");
            this.editor.setEditable(true);
            this.editor.setEnabled(true);
            this.editor.setTabSize(4);
            setBorder(null);
            setLayout(new BorderLayout());
            add(new RTextScrollPane(this.editor, true));
        }

        /** Indicates if the editor is currently dirty. */
        public final boolean isDirty() {
            return this.dirty;
        }

        /** Changes the dirty status of the editor. */
        public final void setDirty(boolean dirty) {
            this.dirty = dirty;
        }

        /** Returns the file from which the editor was loaded. */
        public final File getFile() {
            return this.file;
        }

        /** Changes the file to which the editor should be saved. */
        public final void setFile(File file) {
            this.file = file;
        }

        /** Returns the editor panel. */
        public final RSyntaxTextArea getEditor() {
            return this.editor;
        }

        /**
         * Editor contents has changed
         */
        private boolean dirty;
        /**
         * The origin file, can be null when it is a new file
         */
        private File file;
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

        /*
         * (non-Javadoc)
         * @see java.io.OutputStream#write(int)
         */
        @Override
        public void write(int arg0) throws IOException {
            this.buffer[this.pos++] = arg0;
            if (this.pos >= this.buffer.length) {
                flush();
            }
        }

        /*
         * (non-Javadoc)
         * @see java.io.OutputStream#flush()
         */
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
