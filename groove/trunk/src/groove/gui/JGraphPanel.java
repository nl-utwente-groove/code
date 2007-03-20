// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: JGraphPanel.java,v 1.1.1.1 2007-03-20 10:05:29 kastenberg Exp $
 */
package groove.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 * A panel that combines a {@link groove.gui.jgraph.JGraph}and (optionally) a
 * {@link groove.gui.LabelList}.
 * 
 * @author Arend Rensink, updated by Carel van Leeuwen
 * @version $Revision: 1.1.1.1 $
 */
public class JGraphPanel<JG extends JGraph> extends JPanel {
    /**
     * The minimum width of the label pane. If the label list is empty, the
     * preferred width is set to the minimum width.
     */
    public final static int MINIMUM_LABEL_PANE_WIDTH = 75;

    /**
     * Constructs a view upon a given jgraph without a statusbar.
     * 
     * @param jGraph
     *            the jgraph on which this panel is a view
     * @ensure <tt>getJGraph() == jGraph</tt>
     */
    public JGraphPanel(JG jGraph) {
        this(jGraph, false);
    }

    /**
     * Constructs a view upon a given jgraph, possibly with a status bar.
     * 
     * @param jGraph
     *            the jgraph on which this panel is a view
     * @param withStatusBar
     *            <tt>true</tt> if a status bar should be added to the panel
     * @ensure <tt>getJGraph() == jGraph</tt>
     */
    public JGraphPanel(JG jGraph, boolean withStatusBar) {
        this(jGraph, withStatusBar, true);

    }

    /**
     * Constructs a view upon a given jgraph, possibly with a status bar or with
     * labelpanel.
     * 
     * @param jGraph
     *            the jgraph on which this panel is a view
     * @param withStatusBar
     *            <tt>true</tt> if a status bar should be added to the panel
     * @param withLabelPanel
     *            <tt>true</tt> if the labelPAnel should be added to the panel
     * @ensure <tt>getJGraph() == jGraph</tt>
     */
    public JGraphPanel(JG jGraph, boolean withStatusBar,
            boolean withLabelPanel) {
        this.jGraph = jGraph;
        if (withLabelPanel) {
            this.viewLabelListItem = this.createViewLabelListItem();
        } else {
            this.viewLabelListItem = null;
        }
        this.setLayout(new BorderLayout());
        if (withLabelPanel) {
            this.setPane(createSplitPane());
        } else {
            this.setPane(this.createSoloPane());
        }
        if (withStatusBar) {
            this.statusBar = new JLabel(" ");
            add(statusBar, BorderLayout.SOUTH);
        } else {
            statusBar = null;
        }
    }

    /**
     * Returns a menu item that allows to switch the label list view on and off.
     */
    public JMenuItem getViewLabelListItem() {
        return this.viewLabelListItem;
    }

    /**
     * Returns the underlying {@link JGraph}.
     */
    public JG getJGraph() {
        return jGraph;
    }
    
    /**
     * Returns the underlying {@link JModel}, or <code>null</code>
     * if the jgraph is currently disabled.
     */
    public JModel getJModel() {
    	if (isEnabled()) {
    		return jGraph.getModel();
    	} else {
    		return null;
    	}
    }

    /**
     * Returns the status bar of this panel, if any.
     */
    public JLabel getStatusBar() {
        return statusBar;
    }

    /**
     * Delegates the method to the content pane and to super.
     */
    public void setEnabled(boolean enabled) {
        jGraph.setEnabled(enabled);
        statusBar.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    /**
     * Creates and returns a new pane, on which only the jgraph is shown (and no
     * label list).
     */
    protected JComponent createSoloPane() {
        // set up the real editor pane
        JScrollPane result = new JScrollPane(jGraph);
        result.setPreferredSize(new Dimension(500, 400));
        return result;
    }

    /**
     * Creates and returns a new split pane, on which both the jgraph and label
     * list are shown.
     */
    protected JComponent createSplitPane() {
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new BorderLayout());
        labelPane.add(new JLabel(" " + Options.LABEL_PANE_TITLE + " "),
                BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(jGraph.getLabelList()) {
            public Dimension getMinimumSize() {
                return new Dimension(MINIMUM_LABEL_PANE_WIDTH, 0);
            }

            public Dimension getPreferredSize() {
                if (jGraph.getLabelList().getModel().getSize() == 0) {
                    return getMinimumSize();
                } else {
                    return super.getPreferredSize();
                }
            }
        };
        labelPane.add(scrollPane, BorderLayout.CENTER);
        // set up the split editor pane
        JSplitPane result = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createSoloPane(), labelPane);
        result.setOneTouchExpandable(true);
        result.setResizeWeight(1.0);
        return result;
    }

    /**
     * Creates a menu item that allows to add the label list to the panel.
     */
    protected JMenuItem createViewLabelListItem() {
        JCheckBoxMenuItem result = new JCheckBoxMenuItem("Label list", true);
        result.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    setPane(createSplitPane());
                    // splitEditorPane.revalidate();
                } else {
                    setPane(createSoloPane());
                    // realEditorPane.revalidate();
                }
                revalidate();
            }
        });
        return result;
    }

    /**
     * Adds a given component as center component to the panel. Removes the
     * previous pane, if any, and revalidates the panel. The current pane is
     * stored in {@link #currentPane}.
     */
    protected void setPane(JComponent editorPane) {
        if (currentPane != null) {
            remove(currentPane);
        }
        add(editorPane, BorderLayout.CENTER);
        currentPane = editorPane;
        revalidate();
    }

    /**
     * The {@link JGraph}on which this panel provides a view.
     */
    protected final JG jGraph;

    /**
     * Panel for showing status messages
     */
    protected final JLabel statusBar;

    /** The menu item to switch the label list on and off. */
    private final JMenuItem viewLabelListItem;

    /**
     * The editor pane most recently installed by {@link #setPane}.
     */
    private JComponent currentPane;
}
