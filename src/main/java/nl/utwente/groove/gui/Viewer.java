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
package nl.utwente.groove.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.kohsuke.args4j.Argument;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.dialog.GraphPreviewDialog;
import nl.utwente.groove.gui.dialog.GraphPreviewDialog.GraphPreviewPanel;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.graph.GraphIO;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.cli.ExistingFileHandler;
import nl.utwente.groove.util.cli.GrooveCmdLineTool;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Command-line tool to load and view a graph.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Viewer extends GrooveCmdLineTool<Object> {
    /**
     * Constructs a viewer, with a given list of command-line arguments.
     */
    public Viewer(String... args) {
        super("Viewer", args);
        // force the LAF to be set
        Options.initLookAndFeel();
    }

    @Override
    protected Object run() throws Exception {
        show(this.inFile, false);
        return null;
    }

    /** Shows a given file as a graph in an optionally modal dialog. */
    public void show(File file, boolean modal) throws IOException, FormatException {
        GraphIO<?> io = null;
        var type = FileType.getType(file);
        if (type != null && type.hasGraphIO() && type.getGraphIO().canLoad()) {
            io = type.getGraphIO();
        }
        GrammarModel grammar = getGrammar();
        final Graph graph;
        if (io == null) {
            graph = Groove.loadGraph(this.inFile);
        } else {
            graph = io.loadGraph(file);
        }
        show(graph, grammar, modal);
    }

    /** Shows a given graph in an optionally modal dialog. */
    public void show(Graph graph, boolean modal) {
        show(graph, null, modal);
    }

    /** Shows a given graph in an optionally modal dialog. */
    private void show(final Graph graph, GrammarModel grammar, boolean modal) {
        GraphPreviewPanel panel = GraphPreviewDialog.createPanel(grammar, graph);
        panel.add(new NodeIdsButton(panel), BorderLayout.NORTH);
        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = optionPane.createDialog(graph.getName());
        dialog.setModal(modal);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    /**
     * Tries to find the enclosing grammar of the graph file
     */
    private GrammarModel getGrammar() throws IOException {
        File dir = this.inFile.getCanonicalFile().getParentFile();
        while (dir != null && !FileType.GRAMMAR.hasExtension(dir)) {
            dir = dir.getParentFile();
        }
        GrammarModel grammar = null;
        if (dir != null) {
            grammar = Groove.loadGrammar(dir.getPath());
        }
        return grammar;
    }

    /** The location of the file to be viewer. */
    @Argument(metaVar = "input",
        usage = "Graph file to be viewed. Its extension is used to guess its format and type",
        required = true, handler = ExistingFileHandler.class)
    private File inFile;

    /**
     * Invokes the viewer for a given list of command-line arguments.
     */
    public static void main(String[] args) {
        GrooveCmdLineTool.tryExecute(Viewer.class, args);
    }

    /** Constructs and starts the viewer. */
    static public Object execute(String[] args) throws Exception {
        return new Viewer(args).start();
    }

    /**
     * Tries to show a given graph in a modeless dialog.
     * Convenience method for {@code showGraph(graph,false)}.
     */
    static public void showGraph(Graph graph) {
        showGraph(graph, false);
    }

    /**
     * Tries to show a given file as a graph in an optionally modal dialog,
     * by invoking {@link #show(File,boolean)} on a fresh {@link Viewer} instance.
     */
    static public void showGraph(File file, boolean modal) throws IOException, FormatException {
        new Viewer().show(file, modal);
    }

    /**
     * Tries to show a given graph in an optionally modal dialog,
     * by invoking {@link #show(Graph,boolean)} on a fresh {@link Viewer} instance.
     */
    static public void showGraph(Graph graph, boolean modal) {
        new Viewer().show(graph, modal);
    }

    private class NodeIdsButton extends JButton {
        NodeIdsButton(GraphPreviewPanel panel) {
            this.nodeIdsItem = panel.getOptions().getItem(Options.SHOW_INTERNAL_NODE_IDS_OPTION);
            setText();
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    NodeIdsButton.this.nodeIdsItem
                        .setSelected(!NodeIdsButton.this.nodeIdsItem.isSelected());
                    setText();
                }
            });
        }

        void setText() {
            if (this.nodeIdsItem.isSelected()) {
                setText("Hide node identities");
            } else {
                setText("Show node identities");
            }
        }

        private final JMenuItem nodeIdsItem;
    }
}
