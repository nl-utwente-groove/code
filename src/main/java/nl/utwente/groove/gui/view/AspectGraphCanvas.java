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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.util.AIGenerated;

/**
 * Canvas showing an aspect graph (a host graph, rule, type graph or state).
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
@AIGenerated("Claude Fable 5.1, 2026-09")
public interface AspectGraphCanvas extends GraphCanvas<AspectGraph> {
    @Override
    AspectGraphViewController getController();

    @Override
    @Nullable
    AspectGraphViewModel getViewModel();

    @Override
    AspectGraphViewModel newViewModel();

    @Override
    AspectGraphViewModel showGraph(AspectGraph graph);

    /** Returns the resource model of the graph currently shown. */
    GraphBasedModel<?> getResourceModel();

    /** Returns the type graph against which the graph currently shown is typed. */
    TypeGraph getTypeGraph();
}
