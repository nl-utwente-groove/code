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
package nl.utwente.groove.gui.view;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JMenu;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.lts.ExploreResult;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.util.AIGenerated;

/**
 * What a {@link GraphViewController} asks of the tool in which its graph view is shown:
 * the grammar and options the view is shown under, the label filter it obeys,
 * and the actions and menu items the tool contributes to the view's menus.
 * <p>
 * A controller without a context is a graph view outside any tool, as shown
 * by a dialog or by the headless imager: it has no contributed actions and no filter.
 * The implementation that wires a graph view into the simulator is
 * {@code nl.utwente.groove.gui.display.SimulatorViewContext} and its subclasses.
 * @param <G> the type of graphs shown in the graph view
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Opus 5, 2026-09")
@NonNullByDefault
public interface GraphViewContext<G extends Graph> {
    /**
     * The grammar to which the displayed graph belongs;
     * {@code null} if the context has no grammar loaded.
     */
    @Nullable
    GrammarModel getGrammar();

    /** The object holding the display options of the context. */
    ViewOptions getOptions();

    /**
     * Indicates if the context offers actions on the displayed graph.
     * A context that only provides a grammar is not interactive.
     */
    boolean isInteractive();

    // ---------- canvas lifecycle ----------

    /**
     * Signals that a canvas has been attached to the controller, so that the
     * context can register its canvas listeners.
     */
    default void canvasAttached(GraphCanvas<G> canvas) {
        // empty by default
    }

    /**
     * Signals that the controller is discarding its listeners, so that the
     * context can unregister what it registered on attachment.
     */
    default void canvasDetached(GraphCanvas<G> canvas) {
        // empty by default
    }

    // ---------- label filtering ----------

    /**
     * Indicates if the context filters the cells of the graph view by label at all.
     * If it does not, {@link #isFiltered(ViewCell)} and {@link #isFiltered(Label)}
     * are invariably {@code false}.
     */
    boolean isFiltering();

    /** Indicates if a given cell is currently filtered out of the graph view. */
    boolean isFiltered(ViewCell<G> cell);

    /** Indicates if a given label is currently filtered out of the graph view. */
    boolean isFiltered(Label label);

    /** Enables or disables the label filter of the context, if there is one. */
    void setFilteringEnabled(boolean enabled);

    /**
     * The labelled cells of the label filter, from which the label sub-menus
     * of the show/hide menu are built; empty if the context does not filter.
     */
    default Collection<LabelledCells<G>> getFilterLabels() {
        return List.of();
    }

    /**
     * Indicates if a given cell is currently filtered out of the graph view by
     * a secondary filter: the rule level tree, for a view showing a rule.
     */
    default boolean isLevelFiltered(ViewCell<G> cell) {
        return false;
    }

    // ---------- actions and menu items of the context ----------

    /** The action exporting the graph shown on a given canvas. */
    Action getExportAction(GraphCanvas<G> canvas);

    /** The action opening the layout dialog of the context. */
    Action getLayoutDialogAction();

    /**
     * The items the context puts at the head of the popup menu of the graph view:
     * its actions on the displayed graph as a whole.
     * @param atPoint the point at which the menu is activated, in graph
     * coordinates; {@code null} if the menu is not activated at a point
     */
    default JMenu getPopupItems(@Nullable Point2D atPoint) {
        return new JMenu();
    }

    /**
     * The items the context puts at the head of the export menu of the graph view:
     * its actions saving the displayed graph into the grammar.
     */
    default JMenu getExportItems() {
        return new JMenu();
    }

    /**
     * The items the context offers for a given non-empty selection of cells.
     */
    default JMenu getSelectionItems(Collection<? extends ViewCell<G>> cells) {
        return new JMenu();
    }

    /** The exploration actions of the context, for a view showing an LTS. */
    default JMenu getExploreItems() {
        return new JMenu();
    }

    /** The traversal actions of the context, for a view showing an LTS. */
    default JMenu getGotoItems() {
        return new JMenu();
    }

    // ---------- LTS state of the context ----------

    /**
     * The result of the last exploration, for a view showing an LTS;
     * {@code null} if there is none.
     */
    default @Nullable ExploreResult getExploreResult() {
        return null;
    }

    /** Records a trace computed on the LTS shown in the graph view. */
    default void setTrace(Set<GraphTransition> trace) {
        // empty by default
    }
}
