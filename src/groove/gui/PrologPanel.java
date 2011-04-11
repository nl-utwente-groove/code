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
import groove.explore.result.PrologCondition;
import groove.io.FileType;
import groove.io.GrooveFileChooser;
import groove.prolog.GrooveState;
import groove.prolog.PrologQuery;
import groove.prolog.QueryResult;
import groove.prolog.exception.GroovePrologException;
import groove.prolog.exception.GroovePrologLoadingException;
import groove.trans.RuleEvent;
import groove.util.Groove;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
// TODO make this nicer
public class PrologPanel extends JPanel {
    private static final long serialVersionUID = 1728208313657610091L;
    private static final int MAX_HISTORY = 50;

    static final Preferences PREFS =
        Preferences.userNodeForPackage(PrologPanel.class);

    /**
     * Data structure to keep track of the open/loaded prolog files
     * 
     * @author Michiel Hendriks
     */
    public class PrologFile {
        /**
         * Editor contents has changed
         */
        boolean dirty;
        /**
         * The origin file, can be null when it is a new file
         */
        File file;
        /**
         * The associated editor
         */
        RSyntaxTextArea editor;
        /**
         * The panel the editor is in. This is needed in order to remove the
         * editor from the tab when a file is closed
         */
        RTextScrollPane pane;
    }

    /**
     * Counter used to show the number of found solutions (so far)
     */
    protected int solutionCount;
    /**
     * The Simulator UI
     */
    protected Simulator sim;
    /**
     * The current instance of the prolog interpreter. Will be recreated every
     * time "reconsult" action is performed.
     */
    protected PrologQuery prolog;

    /**
     * If true the user code should also be consulted. Otherwise only the
     * standard prolog files are consulted.
     */
    protected boolean doConsultUserCode = false;
    /**
     * Used for the "graphstate accepting" exploration method
     */
    protected PrologCondition prologCondition;

    // UI components
    /**
     * TODO
     */
    protected JComboBox query;

    /**
     * TODO
     */
    protected JTextComponent queryEdit;

    /**
     * TODO
     */
    protected JTextArea results;

    /**
     * TODO
     */
    protected JButton nextResultBtn;

    /**
     * TODO
     */
    protected JButton consultBtn;

    /**
     * TODO
     */
    protected JLabel userCodeConsulted;

    /**
     * TODO
     */
    protected JLabel statusBar;

    /**
     * TODO
     */
    protected OutputStream userOutput;

    /**
     * TODO
     */
    protected JFileChooser prologFileChooser;

    /**
     * TODO
     */
    protected JTree predicateTree;

    /**
     * TODO
     */
    protected DefaultMutableTreeNode predRootNode;

    /**
     * TODO
     */
    protected JTabbedPane prologEditors;

    /**
     * TODO
     */
    protected Map<File,PrologFile> prologFileMap =
        new HashMap<File,PrologFile>();

    /**
     * TODO
     */
    protected Set<PrologFile> prologFiles = new HashSet<PrologFile>();

    /**
     * TODO
     */
    public PrologPanel(Simulator simulator) {
        super();
        Font editFont = new Font("Monospaced", Font.PLAIN, 12);

        this.sim = simulator;
        setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // TODO: Fix this
        /*
        final JPopupMenu explorePopup = new JPopupMenu();
        // explorePopup.add(new JMenuItem(createExploreGraphStateAction()));
        // explorePopup.add(new JMenuItem(createExploreRuleEventsAction()));
        
        JButton exploreBtn = new JButton("Explore");
        exploreBtn.setToolTipText("Explore the LTL for each state which has a result with the given query.");
        exploreBtn.addMouseListener(new MouseAdapter() {
            void postToolbarMenu(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Component c = (Component) e.getSource();
                    explorePopup.show(c, 0, c.getHeight());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                postToolbarMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                postToolbarMenu(e);
            }
        });
        exploreBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Component c = (Component) e.getSource();
                explorePopup.show(c, 0, c.getHeight());
            }
        });
        

        toolBar.addSeparator();
        toolBar.add(exploreBtn);
        */

        this.query = new JComboBox(PREFS.get("queryHistory", "").split("\\n"));
        this.query.setFont(editFont);
        this.query.setEditable(true);
        this.query.setEnabled(true);
        this.query.setPrototypeDisplayValue("groove+prolog");
        this.queryEdit =
            (JTextComponent) this.query.getEditor().getEditorComponent();
        this.queryEdit.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    executeQuery();
                }
            }
        });

        JButton execQuery = new JButton("Execute");
        execQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                executeQuery();
            }
        });

        JPanel queryPane = new JPanel(new BorderLayout());
        queryPane.add(toolBar, BorderLayout.NORTH);
        queryPane.add(this.query, BorderLayout.CENTER);
        queryPane.add(execQuery, BorderLayout.EAST);

        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(new JLabel("User code:"));
        toolBar.addSeparator();

        JButton newButton =
            new JButton(new ImageIcon(Groove.getResource("new.gif")));
        newButton.setToolTipText("Create a new prolog file");
        newButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        createEditor(null);
                    }
                });
            }
        });
        toolBar.add(newButton);

        JButton loadButton =
            new JButton(new ImageIcon(Groove.getResource("open.gif")));
        loadButton.setToolTipText("Open a prolog file");
        loadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                getPrologFileChooser().setMultiSelectionEnabled(true);
                int result =
                    getPrologFileChooser().showOpenDialog(
                        PrologPanel.this.sim.getFrame());
                // now load, if so required
                if (result == JFileChooser.APPROVE_OPTION) {
                    final File[] files =
                        getPrologFileChooser().getSelectedFiles();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            for (File fl : files) {
                                createEditor(fl, true);
                            }
                            consultUserCode();
                        }
                    });
                }
            }
        });
        toolBar.add(loadButton);

        JButton saveButton =
            new JButton(new ImageIcon(Groove.getResource("save.gif")));
        saveButton.setToolTipText("Save the current prolog file");
        saveButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Component comp =
                    PrologPanel.this.prologEditors.getSelectedComponent();
                if (comp == null) {
                    return;
                }
                PrologFile proFile = null;
                for (PrologFile pf : PrologPanel.this.prologFiles) {
                    if (pf.pane == comp) {
                        proFile = pf;
                    }
                }
                if (proFile == null) {
                    return;
                }

                // select a filename
                if (proFile.file == null) {
                    JFileChooser fc = getPrologFileChooser();
                    fc.setMultiSelectionEnabled(false);
                    fc.setSelectedFile(null);
                    do {
                        int result =
                            fc.showSaveDialog(PrologPanel.this.sim.getFrame());
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File fl = getPrologFileChooser().getSelectedFile();
                            if (fl.exists()) {
                                int overwrite =
                                    JOptionPane.showConfirmDialog(
                                        PrologPanel.this.sim.getFrame(),
                                        "Overwrite existing file \""
                                            + fl.getName()
                                            + "\"?"
                                            + (PrologPanel.this.prologFileMap.containsKey(fl)
                                                    ? "\nThis will also discard the current editor for this file."
                                                    : ""));
                                if (overwrite == JOptionPane.NO_OPTION) {
                                    continue;
                                } else if (overwrite == JOptionPane.CANCEL_OPTION) {
                                    return;
                                }
                            }

                            if (PrologPanel.this.prologFileMap.containsKey(fl)) {
                                PrologFile other =
                                    PrologPanel.this.prologFileMap.get(fl);
                                PrologPanel.this.prologEditors.remove(other.pane);
                                PrologPanel.this.prologFileMap.remove(fl);
                                PrologPanel.this.prologFiles.remove(other);
                            }
                            proFile.file = fl;
                            PrologPanel.this.prologFileMap.put(fl, proFile);
                            break;
                        }
                    } while (true);
                }

                try {
                    proFile.editor.write(new FileWriter(proFile.file));
                    proFile.dirty = false;
                    int index =
                        PrologPanel.this.prologEditors.indexOfComponent(proFile.pane);
                    if (index > -1) {
                        PrologPanel.this.prologEditors.setTitleAt(index,
                            proFile.file.getName());
                    }
                } catch (IOException eex) {
                    /**
                     * TODO: Handle this exception
                     */
                    eex.printStackTrace();
                }
                return;
            }
        });
        toolBar.add(saveButton);

        JButton closeButton =
            new JButton(new ImageIcon(Groove.getResource("delete.gif")));
        closeButton.setToolTipText("Close the current prolog file");
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Component comp =
                    PrologPanel.this.prologEditors.getSelectedComponent();
                if (comp == null) {
                    return;
                }
                PrologFile proFile = null;
                for (PrologFile pf : PrologPanel.this.prologFiles) {
                    if (pf.pane == comp) {
                        proFile = pf;
                    }
                }
                if (proFile == null) {
                    return;
                }
                if (proFile.dirty) {
                    int overwrite =
                        JOptionPane.showConfirmDialog(
                            PrologPanel.this.sim.getFrame(),
                            "You have got unsaved changes. Are you sure you want to close this file and discard the changes?",
                            "Discard changes?", JOptionPane.YES_NO_OPTION);
                    if (overwrite == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

                PrologPanel.this.prologEditors.remove(proFile.pane);
                PrologPanel.this.prologFileMap.remove(proFile.file);
                PrologPanel.this.prologFiles.remove(proFile);
                consultUserCode();
            }
        });
        toolBar.add(closeButton);

        toolBar.addSeparator();

        this.consultBtn = new JButton("Consult");
        this.consultBtn.setToolTipText("Reconsult the prolog code. This will cancel the current active query.");
        this.consultBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (!confirmDirty()) {
                    return;
                }
                consultUserCode();
            }
        });
        toolBar.add(this.consultBtn);

        this.userCodeConsulted = new JLabel("");
        this.userCodeConsulted.setFont(this.userCodeConsulted.getFont().deriveFont(
            Font.BOLD));
        toolBar.addSeparator();
        toolBar.add(this.userCodeConsulted);

        this.prologEditors =
            new JTabbedPane(SwingConstants.BOTTOM,
                JTabbedPane.SCROLL_TAB_LAYOUT);

        JPanel editorPane = new JPanel(new BorderLayout());
        editorPane.add(toolBar, BorderLayout.NORTH);
        editorPane.add(this.prologEditors, BorderLayout.CENTER);

        this.results = new JTextArea();
        this.results.setFont(editFont);
        this.results.setText("");
        this.results.setEditable(false);
        this.results.setEnabled(true);
        this.results.setBackground(null);
        this.userOutput = new JTextAreaOutputStream(this.results);
        Environment.setDefaultOutputStream(this.userOutput);

        this.nextResultBtn = new JButton("More?");
        this.nextResultBtn.setFont(this.nextResultBtn.getFont().deriveFont(
            Font.BOLD));
        this.nextResultBtn.setVisible(false);
        this.nextResultBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                nextResults();
            }
        });

        JPanel resultsPane = new JPanel(new BorderLayout());
        resultsPane.add(new JScrollPane(this.results), BorderLayout.CENTER);
        resultsPane.add(this.nextResultBtn, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setBorder(null);
        splitPane.setOneTouchExpandable(true);
        splitPane.setTopComponent(editorPane);
        splitPane.setBottomComponent(resultsPane);
        splitPane.setDividerLocation(0);

        JPanel mainPane = new JPanel(new BorderLayout());
        mainPane.add(queryPane, BorderLayout.NORTH);
        mainPane.add(splitPane, BorderLayout.CENTER);

        this.predRootNode = new DefaultMutableTreeNode("Predicates", true);
        this.predRootNode.add(new DefaultMutableTreeNode(
            "Press 'consult' to load the predicates"));
        this.predicateTree = new JTree(this.predRootNode);
        this.predicateTree.setRootVisible(false);
        this.predicateTree.setShowsRootHandles(true);
        this.predicateTree.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1
                    && e.getButton() == MouseEvent.BUTTON1) {
                    // when double clicked add the selected predicate (with
                    // template) to the current query
                    TreePath sel =
                        PrologPanel.this.predicateTree.getSelectionPath();
                    if (sel != null) {
                        Object o = sel.getLastPathComponent();
                        if (o instanceof DefaultMutableTreeNode) {
                            o = ((DefaultMutableTreeNode) o).getUserObject();
                            if (o instanceof CompoundTermTag) {
                                CompoundTermTag tag = (CompoundTermTag) o;
                                StringBuilder sb =
                                    new StringBuilder(
                                        PrologPanel.this.query.getSelectedItem().toString());
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
                                PrologPanel.this.query.setSelectedItem(sb.toString());
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                /**
                 * TODO
                 */
            }

            @Override
            public void mouseExited(MouseEvent e) {
                /**
                 * TODO
                 */
            }

            @Override
            public void mousePressed(MouseEvent e) {
                /**
                 * TODO
                 */
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                /**
                 * TODO
                 */
            }
        });
        DefaultTreeCellRenderer renderer =
            (DefaultTreeCellRenderer) this.predicateTree.getCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);

        JSplitPane sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        sp2.setResizeWeight(0.3);
        sp2.setBorder(null);
        sp2.setOneTouchExpandable(true);
        sp2.setRightComponent(new JScrollPane(this.predicateTree));
        sp2.setLeftComponent(mainPane);
        sp2.setDividerLocation(Integer.MAX_VALUE);

        add(sp2, BorderLayout.CENTER);

        this.statusBar = new JLabel(" ");
        add(this.statusBar, BorderLayout.SOUTH);
    }

    /**
     * Create a prolog editor tab for the given file
     * 
     * @param file TODO
     */
    protected void createEditor(File file) {
        createEditor(file, false);
    }

    /**
     * Create a new prolog editor tab
     * 
     * @param file TODO
     * @param delayLoading
     *            if false then do not consule the file, used in case of new
     *            files
     */
    protected void createEditor(File file, boolean delayLoading) {
        if (file != null && this.prologFileMap.containsKey(file)) {
            PrologFile proFile = this.prologFileMap.get(file);
            this.prologEditors.setSelectedComponent(proFile.pane);
            return;
        }

        final PrologFile proFile = new PrologFile();
        proFile.file = file;
        proFile.dirty = file == null;
        proFile.editor = new RSyntaxTextArea();
        Font editFont = new Font("Monospaced", Font.PLAIN, 12);
        proFile.editor.setFont(editFont);
        proFile.editor.setText("");
        proFile.editor.setEditable(true);
        proFile.editor.setEnabled(true);
        proFile.editor.setTabSize(4);
        String title = "* untitled.pro";
        if (file != null) {
            title = file.getName();
            this.prologFileMap.put(file, proFile);
        }
        proFile.pane = new RTextScrollPane(300, 300, proFile.editor, true);
        this.prologFiles.add(proFile);
        this.prologEditors.addTab(title, proFile.pane);
        this.prologEditors.setSelectedComponent(proFile.pane);

        if (file != null) {
            try {
                FileReader fis = new FileReader(file);
                proFile.editor.read(fis, null);
                if (delayLoading) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            consultUserCode();
                        }
                    });
                }
            } catch (IOException eex) {
                /**
                 * TODO: Handle this exception
                 */
                eex.printStackTrace();
            }
        }

        proFile.editor.getDocument().addDocumentListener(
            new DocumentListener() {

                /**
                 * Update the tab title
                 */
                protected void updateTab() {
                    if (proFile.dirty) {
                        return;
                    }
                    proFile.dirty = true;
                    String title = "untitled.pro";
                    if (proFile.file != null) {
                        title = proFile.file.getName();
                    }
                    int index =
                        PrologPanel.this.prologEditors.indexOfComponent(proFile.pane);
                    PrologPanel.this.prologEditors.setTitleAt(index, "* "
                        + title);
                }

                public void changedUpdate(DocumentEvent arg0) {
                    PrologPanel.this.userCodeConsulted.setText("Modified");
                    updateTab();
                }

                public void insertUpdate(DocumentEvent arg0) {
                    PrologPanel.this.userCodeConsulted.setText("Modified");
                    updateTab();
                }

                public void removeUpdate(DocumentEvent arg0) {
                    PrologPanel.this.userCodeConsulted.setText("Modified");
                    updateTab();
                }
            });
    }

    /**
     * Create the Action for exploring based on filtering of rule events
     * 
     * @return
     */
    /* TODO: Fix this code for the new version
    protected Action createExploreRuleEventsAction() {
        final ExploreStatePrologStrategy strat =
            new ExploreStatePrologStrategy();
        Scenario scen =
            ScenarioFactory.getScenario(strat, new Acceptor(),
                "Explore by selecting rule events.",
                "Select Rule Events Exploration");
        final Action innerAct = this.sim.createLaunchScenarioAction(scen);
        Action act = new Action() {
            public void addPropertyChangeListener(
                    PropertyChangeListener listener) {
                innerAct.addPropertyChangeListener(listener);
            }

            public Object getValue(String key) {
                return innerAct.getValue(key);
            }

            public boolean isEnabled() {
                return innerAct.isEnabled();
            }

            public void putValue(String key, Object value) {
                innerAct.putValue(key, value);
            }

            public void removePropertyChangeListener(
                    PropertyChangeListener listener) {
                innerAct.removePropertyChangeListener(listener);
            }

            public void setEnabled(boolean b) {
                innerAct.setEnabled(b);
            }

            public void actionPerformed(ActionEvent e) {
                if (!confirmDirty()) {
                    return;
                }
                if (PrologPanel.this.queryEdit.getText().length() == 0) {
                    return;
                }
                if (PrologPanel.this.sim.getCurrentState() == null) {
                    return;
                }
                PrologPanel.this.results.setText("");
                strat.setPrologQuery(null,
                    PrologPanel.this.queryEdit.getText(), getUserPrologCode());
                addQueryHistory(PrologPanel.this.queryEdit.getText());
                innerAct.actionPerformed(e);
            }
        };
        return act;
    }
    */

    /**
     * Produces prolog code to load the currently open files
     * 
     * @return prolog code
     */
    /* TODO: Can this be removed?
    private String getUserPrologCode() {
        StringBuilder sb = new StringBuilder();
        for (PrologFile pfile : this.prologFiles) {
            if (pfile.file != null) {
                sb.append(":-ensure_loaded(file('");
                sb.append(pfile.file.toString().replaceAll("\\\\", "\\\\\\\\"));
                sb.append("')).\n");
            }
        }
        return sb.toString();
    }
    */

    /**
     * Create the Action for simulating using the graph state acceptor method
     * 
     * @return
     */
    /* TODO: Fix this code for the new version
    protected Action createExploreGraphStateAction() {
        ConditionalBFSStrategy strat = new ConditionalBFSStrategy();
        this.prologCondition = new PrologCondition();
        strat.setExploreCondition(this.prologCondition);
        Scenario scen =
            ScenarioFactory.getScenario(strat, new Acceptor(),
                "Explore by accepting graph states.",
                "Accept Graph State Exploration");
        final Action innerAct = this.sim.createLaunchScenarioAction(scen);
        Action act = new Action() {
            public void addPropertyChangeListener(
                    PropertyChangeListener listener) {
                innerAct.addPropertyChangeListener(listener);
            }

            public Object getValue(String key) {
                return innerAct.getValue(key);
            }

            public boolean isEnabled() {
                return innerAct.isEnabled();
            }

            public void putValue(String key, Object value) {
                innerAct.putValue(key, value);
            }

            public void removePropertyChangeListener(
                    PropertyChangeListener listener) {
                innerAct.removePropertyChangeListener(listener);
            }

            public void setEnabled(boolean b) {
                innerAct.setEnabled(b);
            }

            public void actionPerformed(ActionEvent e) {
                if (!confirmDirty()) {
                    return;
                }
                if (PrologPanel.this.queryEdit.getText().length() == 0) {
                    return;
                }
                if (PrologPanel.this.sim.getCurrentState() == null) {
                    return;
                }
                PrologPanel.this.results.setText("");
                PrologPanel.this.prologCondition.setCondition(PrologPanel.this.queryEdit.getText());
                PrologPanel.this.prologCondition.setUsercode(getUserPrologCode());
                addQueryHistory(PrologPanel.this.queryEdit.getText());
                innerAct.actionPerformed(e);
            }
        };
        return act;
    }
    */

    /**
     * Return a file chooser for prolog files
     */
    protected JFileChooser getPrologFileChooser() {
        if (this.prologFileChooser == null) {
            this.prologFileChooser =
                GrooveFileChooser.getFileChooser(FileType.PROLOG_FILTER);
        }
        return this.prologFileChooser;
    }

    /**
     * Execute the current query
     */
    protected void executeQuery() {
        executeQuery(this.queryEdit.getText());
    }

    /**
     * Make sure the prolog environment is initialized and clean up previous
     * results.
     * 
     * @return TODO
     */
    protected boolean ensureProlog() {
        this.statusBar.setText(" ");
        if (this.prolog == null) {
            this.prolog = new PrologQuery();
            if (this.doConsultUserCode) {
                try {
                    Environment env = this.prolog.getEnvironment();
                    for (PrologFile pf : this.prologFiles) {
                        if (pf.file == null) {
                            continue;
                        }
                        CompoundTerm term =
                            new CompoundTerm(
                                AtomTerm.get("file"),
                                new Term[] {AtomTerm.get(pf.file.getAbsolutePath())});
                        env.ensureLoaded(term);
                        if (!env.getLoadingErrors().isEmpty()) {
                            throw new GroovePrologLoadingException(
                                env.getLoadingErrors());
                        }
                    }
                } catch (GroovePrologLoadingException e) {
                    this.userCodeConsulted.setText("Error");
                    this.results.append("\nError loading the prolog engine:\n");
                    this.results.append(e.getMessage());
                    this.prolog = null;
                    this.doConsultUserCode = false;
                    return false;
                }
                this.userCodeConsulted.setText("User code consulted");
                this.statusBar.setText("User code accepted");
            }

            try {
                updatePredicateTree(this.prolog.getEnvironment().getModule());
                this.prolog.init();
            } catch (GroovePrologLoadingException e) {
                this.results.append("\nError loading the prolog engine:\n");
                this.results.append(e.getMessage());
                this.prolog = null;
                return false;
            }
        }
        return true;
    }

    /**
     * Update the tree with all known predicates
     * 
     * @param module TODO
     */
    protected void updatePredicateTree(Module module) {
        this.predRootNode.removeAllChildren();
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
        tags.addAll(module.getPredicateTags());
        for (CompoundTermTag tag : tags) {
            DefaultMutableTreeNode baseNode = nodes.get(tag.functor);
            if (baseNode == null) {
                baseNode = new DefaultMutableTreeNode(tag);
                this.predRootNode.add(baseNode);
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
        ((DefaultTreeModel) this.predicateTree.getModel()).reload();
        this.predicateTree.expandPath(new TreePath(this.predRootNode.getPath()));
    }

    /**
     * Execute the gven prolog query
     * 
     * @param queryString TODO
     */
    public void executeQuery(String queryString) {
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

        addQueryHistory(queryString);
        this.results.setText("?- " + queryString + "\n");

        if (!ensureProlog()) {
            return;
        }

        // TODO: Should this be a set?
        Set<RuleEvent> ruleEvents = new HashSet<RuleEvent>();
        ruleEvents.add(this.sim.getCurrentEvent());

        this.prolog.setGrooveState(new GrooveState(this.sim.getGrammarView(),
            this.sim.getGTS(), this.sim.getCurrentState(), ruleEvents));

        try {
            this.solutionCount = 0;
            processResults(this.prolog.newQuery(queryString));
        } catch (GroovePrologException e) {
            handlePrologException(e);
        }
    }

    /**
     * Add the query to the history
     * 
     * @param queryString TODO
     */
    protected void addQueryHistory(String queryString) {
        this.query.removeItem(queryString);
        this.query.insertItemAt(queryString, 0);
        this.query.setSelectedIndex(0);
        while (this.query.getItemCount() > MAX_HISTORY) {
            this.query.removeItemAt(MAX_HISTORY);
        }

        // store the history
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.query.getItemCount(); i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append(this.query.getItemAt(i));
        }
        PREFS.put("queryHistory", sb.toString());
    }

    /**
     * Handler for the exceptions thrown by the prolog environment
     * 
     * @param e TODO
     */
    protected void handlePrologException(Throwable e) {
        try {
            this.userOutput.flush();
        } catch (IOException e1) {
            /**
             * TODO: Handle this exception
             */
            e1.printStackTrace();
        }
        if (e.getCause() instanceof PrologException) {
            PrologException pe = (PrologException) e.getCause();
            if (pe.getCause() == null) {
                this.results.append(e.getCause().getMessage());
                return;
            } else {
                e = pe;
            }
        }
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        this.results.append(sw.toString());
    }

    /**
     * Get the next set of results. Only works after a successful
     * {@link #executeQuery(String)}
     */
    public void nextResults() {
        if (this.prolog == null || !this.prolog.hasNext()) {
            return;
        }
        this.results.append("\n");
        try {
            processResults(this.prolog.next());
        } catch (GroovePrologException e) {
            handlePrologException(e);
        }
    }

    /**
     * Pretty print the results of the query in the output panel
     * 
     * TODO
     */
    protected void processResults(QueryResult queryResult) {
        try {
            this.userOutput.flush();
        } catch (IOException e) {
            /**
             * TODO: Handle this exception
             */
            e.printStackTrace();
        }
        if (queryResult == null) {
            return;
        }
        if (!this.results.getText().endsWith("\n")) {
            this.results.append("\n");
        }
        switch (queryResult.getReturnValue()) {
        case SUCCESS:
        case SUCCESS_LAST:
            ++this.solutionCount;
            for (Entry<String,Object> entry : queryResult.getVariables().entrySet()) {
                this.results.append(entry.getKey());
                this.results.append(" = ");
                if (entry.getValue() instanceof Term) {
                    this.results.append(TermWriter.toString((Term) entry.getValue()));
                } else {
                    this.results.append("" + entry.getValue());
                }
                this.results.append("\n");
            }
            this.results.append("Yes\n");
            break;
        case FAIL:
            this.results.append("No\n");
            break;
        case HALT:
            this.results.append("Interpreter was halted\n");
            break;
        default:
            this.results.append(String.format("Unexpected return value: %s",
                this.prolog.lastReturnValue().toString()));
        }
        this.nextResultBtn.setVisible(this.prolog.hasNext());
        if (this.nextResultBtn.isVisible()) {
            this.nextResultBtn.grabFocus();
        }
        this.statusBar.setText(String.format(
            "%d solution(s); Executed in %fms", this.solutionCount,
            queryResult.getExecutionTime() / 1000000.0));
    }

    /**
     * (re)consult the user code. This doesn't execute any queries
     */
    protected void consultUserCode() {
        this.prolog = null;
        this.doConsultUserCode = true;
        this.nextResultBtn.setVisible(false);
        this.results.setText("");
        ensureProlog();
    }

    /**
     * Prompt the user when there are dirty editors
     */
    protected boolean confirmDirty() {
        boolean dirty = false;
        for (PrologFile pf : this.prologFiles) {
            if (pf.dirty) {
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
