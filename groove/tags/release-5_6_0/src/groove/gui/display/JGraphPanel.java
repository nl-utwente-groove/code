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
package groove.gui.display;

import static groove.gui.jgraph.JGraphMode.PAN_MODE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import groove.graph.Graph;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;

/**
 * A panel that combines a {@link groove.gui.jgraph.JGraph}and (optionally) a
 * {@link groove.gui.tree.LabelTree}.
 *
 * @author Arend Rensink, updated by Carel van Leeuwen
 * @version $Revision$
 */
public class JGraphPanel<G extends Graph> extends JPanel {
    /**
     * Constructs a view upon a given jgraph, possibly with a status bar.
     *
     * @param jGraph the jgraph on which this panel is a view
     * @ensure <tt>getJGraph() == jGraph</tt>
     */
    public JGraphPanel(JGraph<? extends G> jGraph) {
        super(false);
        setFocusable(false);
        setFocusCycleRoot(true);
        // right now we always want label panels; keep this option
        this.jGraph = jGraph;
    }

    /**
     * Initialises the GUI.
     * Should be called immediately after the constructor.
     */
    public void initialise() {
        // a JGraphPanel consists of an optional tool bar,
        // a main pane containing the graph, label tree and (possibly)
        // error panel, and an optional status bar.
        setLayout(new BorderLayout());
        add(getScrollPane(), BorderLayout.CENTER);
        add(getStatusBar(), BorderLayout.SOUTH);
        installListeners();
        setEnabled(false);
    }

    /** Callback method that adds the required listeners to this panel. */
    private void installListeners() {
        getJGraph().addJGraphModeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getScrollPane().setWheelScrollingEnabled(evt.getNewValue() != PAN_MODE);
            }
        });
        getJGraph().addPropertyChangeListener(org.jgraph.JGraph.GRAPH_MODEL_PROPERTY,
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    JModel<?> jModel = (JModel<?>) evt.getNewValue();
                    setEnabled(jModel != null);
                }
            });
    }

    /**
     * Lazily creates and returns the scroll pane within which the {@link JGraph}
     * is displayed.
     */
    private JScrollPane getScrollPane() {
        JScrollPane result = this.scrollPane;
        if (result == null) {
            result = this.scrollPane = new JScrollPane(getJGraph());
            result.getVerticalScrollBar()
                .setUnitIncrement(10);
            result.setDoubleBuffered(false);
            result.setPreferredSize(new Dimension(500, 400));
        }
        return result;
    }

    /**
     * The scroll pane in which the JGraph is displayed.
     */
    private JScrollPane scrollPane;

    /** Lazily creates and returns the status bar component of the panel. */
    public JPanel getStatusBar() {
        JPanel result = this.statusBar;
        if (result == null) {
            result = this.statusBar = new JPanel();
            result.setBorder(null);
            result.setLayout(new BorderLayout());
            result.add(getStatusLabel(), BorderLayout.CENTER);
        }
        return result;
    }

    /** Tests if the status bar has been initialised. */
    private boolean hasStatusBar() {
        return this.statusBar != null;
    }

    /**
     * Panel for showing status messages
     */
    private JPanel statusBar;

    /** Lazily creates and returns the status bar component of the panel. */
    public JLabel getStatusLabel() {
        JLabel result = this.statusLabel;
        if (result == null) {
            result = this.statusLabel = new JLabel();
            result.setBorder(null);
        }
        return result;
    }

    /**
     * Panel for showing status messages
     */
    private JLabel statusLabel;

    /**
     * Returns the underlying {@link JGraph}.
     */
    public JGraph<? extends G> getJGraph() {
        return this.jGraph;
    }

    /**
     * The {@link JGraph}on which this panel provides a view.
     */
    private final JGraph<? extends G> jGraph;

    /**
     * Delegates the method to the content pane and to super.
     * Also sets the background appropriately.
     * @see #getEnabledBackground()
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.jGraph.setEnabled(enabled);
        getScrollPane().getHorizontalScrollBar()
            .setEnabled(enabled);
        getScrollPane().getVerticalScrollBar()
            .setEnabled(enabled);
        if (hasStatusBar()) {
            getStatusBar().setEnabled(enabled);
        }
        super.setEnabled(enabled);
        Color background = enabled ? getEnabledBackground() : null;
        getJGraph().setBackground(background);
    }

    /** Callback method to return the background colour in case the panel is enabled.
     * This is {@link Color#WHITE} by default, but may be changed by a call to
     * #setEnabledBackgound.
     * The background colour for an enabled panel; non-{@code null}
     */
    protected Color getEnabledBackground() {
        return this.enabledBackground;
    }

    /** Sets the background colour for an enabled panel. */
    protected void setEnabledBackground(Color enabledBackground) {
        // only do something when it actually changes the background colour
        if (enabledBackground == null ? this.enabledBackground != null
            : !enabledBackground.equals(this.enabledBackground)) {
            this.enabledBackground = enabledBackground;
            if (isEnabled()) {
                getJGraph().setBackground(enabledBackground);
            }
        }
    }

    /** The background colour in case the panel is enabled. */
    private Color enabledBackground = Color.WHITE;
    /**
     * The minimum width of the label pane. If the label list is empty, the
     * preferred width is set to the minimum width.
     */
    public final static int MINIMUM_LABEL_PANE_WIDTH = 100;
}
