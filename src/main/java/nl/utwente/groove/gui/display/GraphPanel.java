/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.gui.display;

import static nl.utwente.groove.gui.view.GraphViewMode.PAN_MODE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvasListener;
import nl.utwente.groove.gui.view.GraphViewMode;
import nl.utwente.groove.gui.view.GraphViewModel;

/**
 * A panel that wraps a {@link GraphCanvas} in a scroll pane, with a status bar
 * underneath. The panel enables and disables the canvas together with itself,
 * and follows the canvas' view mode and model changes through a canvas listener.
 * @author Arend Rensink, updated by Carel van Leeuwen
 * @version $Revision$
 */
@NonNullByDefault
public class GraphPanel<G extends Graph> extends JPanel {
    /**
     * Constructs a panel for a given canvas.
     * Call {@link #initialise()} to build the panel.
     */
    public GraphPanel(GraphCanvas<? extends G> canvas) {
        super(false);
        setFocusable(false);
        setFocusCycleRoot(true);
        this.canvas = canvas;
    }

    /**
     * Builds the panel: a main pane containing the canvas and a status bar.
     * Separated from the constructor so that subclasses can complete
     * their own construction first.
     */
    public void initialise() {
        setLayout(new BorderLayout());
        add(getScrollPane(), BorderLayout.CENTER);
        add(getStatusBar(), BorderLayout.SOUTH);
        installListeners();
        setEnabled(false);
    }

    private void installListeners() {
        installCanvasListener(getCanvas());
    }

    private <H extends Graph> void installCanvasListener(GraphCanvas<H> canvas) {
        canvas.addCanvasListener(new GraphCanvasListener<H>() {
            @Override
            public void modeChanged(GraphCanvas<H> canvas, GraphViewMode oldMode,
                                    GraphViewMode newMode) {
                getScrollPane().setWheelScrollingEnabled(newMode != PAN_MODE);
            }

            @Override
            public void viewModelChanged(GraphCanvas<H> canvas,
                                         @Nullable GraphViewModel<H> oldModel,
                                         @Nullable GraphViewModel<H> newModel) {
                setEnabled(newModel != null);
            }
        });
    }

    private JScrollPane getScrollPane() {
        JScrollPane result = this.scrollPane;
        if (result == null) {
            result = this.scrollPane = new JScrollPane(getCanvas().getComponent());
            result.getVerticalScrollBar().setUnitIncrement(10);
            result.setDoubleBuffered(false);
            result.setPreferredSize(new Dimension(500, 400));
        }
        return result;
    }

    private @Nullable JScrollPane scrollPane;

    /** Returns the status bar of this panel, creating it lazily. */
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

    private boolean hasStatusBar() {
        return this.statusBar != null;
    }

    private @Nullable JPanel statusBar;

    /** Returns the label on the status bar, creating it lazily. */
    public JLabel getStatusLabel() {
        JLabel result = this.statusLabel;
        if (result == null) {
            result = this.statusLabel = new JLabel();
            result.setBorder(null);
        }
        return result;
    }

    private @Nullable JLabel statusLabel;

    /** Returns the canvas shown on this panel. */
    public GraphCanvas<? extends G> getCanvas() {
        return this.canvas;
    }

    private final GraphCanvas<? extends G> canvas;

    /**
     * In addition to delegating the method to the canvas and to
     * <tt>super</tt>, sets the canvas background to <tt>null</tt> when disabled
     * and back to the enabled background when enabled.
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.canvas.setEnabled(enabled);
        getScrollPane().getHorizontalScrollBar().setEnabled(enabled);
        getScrollPane().getVerticalScrollBar().setEnabled(enabled);
        if (hasStatusBar()) {
            getStatusBar().setEnabled(enabled);
        }
        super.setEnabled(enabled);
        Color background = enabled
            ? getEnabledBackground()
            : null;
        getCanvas().setBackground(background);
    }

    /** Returns the background colour of the canvas when enabled. */
    protected Color getEnabledBackground() {
        return this.enabledBackground;
    }

    /** Sets the background colour of the canvas when enabled. */
    protected void setEnabledBackground(Color enabledBackground) {
        // only do something when it actually changes the background colour
        if (!Objects.equals(enabledBackground, this.enabledBackground)) {
            this.enabledBackground = enabledBackground;
            if (isEnabled()) {
                getCanvas().setBackground(enabledBackground);
            }
        }
    }

    private Color enabledBackground = Color.WHITE;

    /** Minimum width of the label pane. */
    public final static int MINIMUM_LABEL_PANE_WIDTH = 100;
}
