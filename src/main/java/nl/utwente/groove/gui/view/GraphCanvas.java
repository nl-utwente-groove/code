/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
 */
package nl.utwente.groove.gui.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JComponent;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.look.VisualKey.Nature;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.util.AIGenerated;

/**
 * Backend-neutral view of the component that renders a graph: everything the controller
 * and the rest of the GUI need from the rendering library, and nothing that is specific
 * to one library. The design record is {@code claude/view-facade.md}.
 * <p>
 * The canvas renders from the {@link VisualMap}s of its {@link ViewCell}s. Values of
 * {@link Nature#CONTROLLED} keys are only ever changed through {@link #edit};
 * values of refreshable keys that depend on rendering (node and text sizes) are supplied
 * by the canvas under the staleness protocol of {@link ViewCell#setStale}.
 * @param <G> the type of graph displayed
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public interface GraphCanvas<G extends Graph> {
    // ---------- structure ----------

    /** Returns the controller of this canvas. */
    GraphViewController<G> getController();

    /** Returns the content model currently shown, if any. */
    @Nullable
    GraphViewModel<G> getViewModel();

    /** Returns the content model currently shown; fails if there is none. */
    default GraphViewModel<G> getNonNullViewModel() {
        var result = getViewModel();
        assert result != null;
        return result;
    }

    /** Returns the graph currently shown, if any. */
    default @Nullable G getGraph() {
        var model = getViewModel();
        return model == null
            ? null
            : model.getGraph();
    }

    /** Returns the role of the graphs shown on this canvas. */
    GraphRole getGraphRole();

    /** Tests if the graphs shown on this canvas have a given role. */
    default boolean hasGraphRole(GraphRole role) {
        return getGraphRole() == role;
    }

    /** Returns the display options. */
    Options getOptions();

    // ---------- content ----------

    /**
     * Shows a given graph, in a fresh content model.
     * @return the content model created for the graph
     */
    GraphViewModel<G> showGraph(G graph);

    /**
     * Indicates that the canvas is in the process of refreshing its content model;
     * while this is the case, change events should be ignored.
     */
    boolean isModelRefreshing();

    /** Returns all cells currently shown; empty if there is no content model. */
    Collection<? extends ViewCell<G>> getCells();

    /** Returns the topmost visible cell at a given position in screen coordinates. */
    @Nullable
    ViewCell<G> getCellAt(double x, double y);

    // ---------- selection ----------

    /** Returns the currently selected cells. */
    List<ViewCell<G>> getSelection();

    /** Returns the first selected cell, if any. */
    default @Nullable ViewCell<G> getSelectedCell() {
        var selection = getSelection();
        return selection.isEmpty()
            ? null
            : selection.get(0);
    }

    /** Tests if no cell is selected. */
    boolean isSelectionEmpty();

    /** Sets the selection to a given set of cells. */
    void select(Collection<? extends ViewCell<G>> cells);

    /** Sets the selection to the cells representing a given set of graph elements. */
    void selectElements(Collection<? extends Element> elements);

    /** Clears the selection. */
    void clearSelection();

    // ---------- refreshing ----------

    /**
     * Refreshes the visibility and rendering of a given set of cells, after their visuals
     * have changed.
     * @param unselectGrayedOut if {@code true}, grayed-out cells are removed from the selection
     */
    void refresh(Collection<? extends ViewCell<G>> cells, boolean unselectGrayedOut);

    /** Refreshes the visibility and rendering of all cells. */
    void refreshAll(boolean unselectGrayedOut);

    /** Changes the grayed-out status of a given set of cells. */
    void setGrayedOut(Set<ViewCell<G>> cells, boolean grayedOut);

    /** Schedules a repaint of the canvas. */
    void repaint();

    // ---------- editing ----------

    /**
     * Applies a set of visual changes to cells, as one undoable edit.
     * Only values of {@link Nature#CONTROLLED} keys may be changed this way.
     */
    void edit(Map<? extends ViewCell<G>,VisualMap> changes);

    /** Removes the bend points of all edges. */
    void clearAllEdgePoints();

    /** Indicates if the content may be edited interactively. */
    boolean isEditable();

    /** Enables or disables interactive editing. */
    void setEditable(boolean editable);

    /** Starts in-place editing of the label of a given cell. */
    void startEditing(ViewCell<G> cell);

    /**
     * Indicates if the canvas currently has an active in-place editor.
     * This is always {@code false} for non-editable canvases.
     */
    boolean hasActiveEditor();

    /** Switches the layout grid on or off. */
    void setGridEnabled(boolean enabled);

    // ---------- mode ----------

    /** Returns the current interaction mode. */
    GraphViewMode getMode();

    /**
     * Sets the interaction mode.
     * @return {@code true} if the mode changed
     */
    boolean setMode(GraphViewMode mode);

    /** Returns the interaction mode that the canvas starts in. */
    GraphViewMode getDefaultMode();

    // ---------- layout ----------

    /** Signals that an automatic layout is in progress (or has ended). */
    void setLayouting(boolean layouting);

    /** Indicates if an automatic layout is in progress. */
    boolean isLayouting();

    /**
     * Returns the text-fitted preferred size of a given vertex, according to the
     * canvas' own font metrics.
     */
    Dimension2D getPreferredSize(ViewVertex<G> vertex);

    // ---------- viewport ----------

    /** Returns the bounds of the displayed graph, in graph coordinates; {@code null} if empty. */
    @Nullable
    Rectangle2D getGraphBounds();

    /** Returns the current zoom factor. */
    double getScale();

    /** Sets the zoom factor. */
    void setScale(double scale);

    /** Zooms in or out by a number of steps (negative for zooming out). */
    void changeScale(int steps);

    /** Zooms and scrolls so that a given area (in graph coordinates) fills the viewport. */
    void zoomTo(Rectangle2D bounds);

    /** Scrolls so that a given area (in graph coordinates) becomes visible. */
    void scrollTo(Rectangle2D bounds);

    /** Scrolls so that a given cell becomes visible. */
    void scrollTo(ViewCell<G> cell);

    /** Scrolls so that the cell of a given graph element becomes visible, if there is one. */
    void scrollTo(Element element);

    /** Scrolls to the next selected cell that is not yet in view. */
    void scrollToNextSelected();

    // ---------- Swing ----------

    /** Returns the Swing component that renders the canvas. */
    JComponent getComponent();

    /** Indicates if the canvas is enabled. */
    boolean isEnabled();

    /** Enables or disables the canvas. */
    void setEnabled(boolean enabled);

    /** Sets the background colour; {@code null} for the default. */
    void setBackground(@Nullable Color background);

    /** Sets the overlay drawn over the canvas. */
    void setOverlay(Overlay overlay);

    /** Returns the overlay drawn over the canvas. */
    Overlay getOverlay();

    /** Decorations a canvas may draw over its content, to signal the state of that content. */
    enum Overlay {
        /** No decoration. */
        NONE,
        /** A hatch pattern, signalling that the content is absent or stale. */
        HATCHED;
    }

    /** Registers the accelerator key of an action on the canvas. */
    void addAccelerator(Action action);

    /** Switches tool tips on or off. */
    void setToolTipEnabled(boolean enabled);

    /** Indicates if tool tips are on. */
    boolean getToolTipEnabled();

    /**
     * Returns a listener that refreshes the canvas when a given display option changes,
     * or {@code null} if the option does not affect this canvas.
     */
    @Nullable
    OptionRefreshListener getRefreshListener(String option);

    /** Unregisters the canvas from all external sources of events; call before discarding. */
    void removeListeners();

    // ---------- listeners ----------

    /** Adds a listener for canvas events. */
    void addCanvasListener(GraphCanvasListener<G> listener);

    /** Removes a listener for canvas events. */
    void removeCanvasListener(GraphCanvasListener<G> listener);

    // ---------- export ----------

    /** Renders the displayed graph to an image; {@code null} if there is nothing to render. */
    @Nullable
    BufferedImage toImage();

    /**
     * Paints the whole displayed graph at scale 1 to a given graphics object,
     * without selection or grid decorations, translated so that the graph bounds
     * start at the origin.
     */
    void paintGraph(Graphics2D graphics);
}
