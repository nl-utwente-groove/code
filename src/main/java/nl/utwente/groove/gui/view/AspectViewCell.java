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
package nl.utwente.groove.gui.view;

import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.Aspect;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeComparator;
import nl.utwente.groove.util.parse.Severity;

/**
 * Instantiation of a {@link ViewCell} carrying {@link EditableLabels},
 * the editable string representation of the node/edge labels.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface AspectViewCell extends ViewCell<@NonNull AspectGraph> {
    @Override
    public Iterator<? extends AspectViewCell> getContext();

    /** Returns the aspect kind of the element wrapped in this cell. */
    Aspect.Map getAspects();

    /**
     * Returns the editable labels of this cell: the texts of its node labels and
     * wrapped edges, as shown and changed by the in-place editor.
     */
    EditableLabels getEditableLabels();

    /**
     * Sets the editable labels of this cell to a copy of the given labels.
     * The cell's own elements are not affected until {@link #applyEditableLabels}.
     */
    void setEditableLabels(EditableLabels labels);

    /**
     * Returns the errors in this cell.
     */
    AspectViewCellErrors getErrors();

    @Override
    default boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    @Override
    default @Nullable Severity getErrorSeverity() {
        return getErrors().getSeverity();
    }

    /**
     * Recomputes the editable labels from the cell's wrapped
     * nodes and edges.
     */
    void refreshEditableLabels();

    /**
     * Resets the cell's nodes and edges from the editable labels.
     */
    void applyEditableLabels(AspectGraph graph);

    /** Separator between level name and edge label. */
    static final char LEVEL_NAME_SEPARATOR = '@';

    /** Comparator ordering remark edges strictly before all other edges,
     * without ordering among the remark or non-remark edges themselves.
     */
    static final Comparator<Edge> REMARK_FIRST_COMPARATOR = (e1, e2) -> {
        boolean r1 = e1 instanceof AspectEdge ae1 && ae1.has(AspectKind.REMARK);
        boolean r2 = e2 instanceof AspectEdge ae2 && ae2.has(AspectKind.REMARK);
        return Boolean.compare(r2, r1);
    };

    /** Comparator for the edges wrapped in an aspect ViewCell:
     * remark edges are ordered strictly first, all other edges are ordered
     * as by {@link EdgeComparator}.
     */
    static final Comparator<Edge> EDGE_COMPARATOR
        = REMARK_FIRST_COMPARATOR.thenComparing(EdgeComparator.instance());
}
