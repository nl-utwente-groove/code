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

import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.util.AIGenerated;

/**
 * Edge cell of an aspect graph view.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
public interface AspectViewEdge extends AspectViewCell, ViewEdge<@NonNull AspectGraph> {
    @Override
    AspectEdge getEdge();

    @Override
    Set<AspectEdge> getEdges();

    @Override
    AspectNode getSourceNode();

    @Override
    AspectNode getTargetNode();

    @Override
    @Nullable
    AspectViewVertex getSourceVertex();

    @Override
    @Nullable
    AspectViewVertex getTargetVertex();

    /** Indicates if the target node of this edge is a nodified edge. */
    boolean isNodeEdgeIn();

    /** Indicates if the source node of this edge is a nodified edge. */
    boolean isNodeEdgeOut();

    /**
     * Indicates if this edge is shown as a label of its source vertex
     * rather than as a separate edge.
     */
    boolean isSourceLabel();
}
