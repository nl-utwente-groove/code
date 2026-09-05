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

import java.util.Iterator;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.type.LabelPattern;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.util.AIGenerated;

/**
 * Vertex cell of an aspect graph view.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
public interface AspectViewVertex extends AspectViewCell, ViewVertex<@NonNull AspectGraph> {
    @Override
    AspectNode getNode();

    /**
     * Fixes the node of this vertex after the graph was rebuilt from the cells,
     * and takes over any errors the node then reports.
     */
    void setNodeFixed();

    @Override
    Set<AspectEdge> getEdges();

    @Override
    Iterator<? extends AspectViewEdge> getContext();

    /**
     * Returns the self-edges of the node that are not shown as node labels,
     * i.e., that are shown as separate edge cells.
     */
    Set<AspectEdge> getExtraSelfEdges();

    /** Indicates if this vertex is in fact a nodified edge. */
    boolean isNodeEdge();

    /**
     * Returns the edge label pattern, if this vertex is a nodified edge;
     * {@code null} otherwise.
     */
    @Nullable
    LabelPattern getEdgeLabelPattern();

    /**
     * Returns the type of the node, or {@code null} if the graph has typing errors.
     */
    @Nullable
    TypeNode getNodeType();
}
