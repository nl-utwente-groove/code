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
package nl.utwente.groove.io.external;

import java.util.EnumSet;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.model.NamedResourceModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.ResourceModel;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.jgraph.JGraph;

/**
 * Wrapper class for resources to be exported.
 * Can wrap either {@link Graph}s, {@link JGraph}s or {@link ResourceModel}s.
 * @author Harold Bruijntjes
 * @version $Revision$
 */
public record Exportable(EnumSet<Kind> kinds, @NonNull QualName qualName, @Nullable JGraph<?> jGraph,
    @Nullable Graph graph, @Nullable ResourceModel<?> model) {

    /** Indicates if this exportable contains an object of a given kind. */
    public boolean hasKind(Kind kind) {
        return this.kinds.contains(kind);
    }

    /** Returns the kind of the grammar resource wrapped by this exportable, if any. */
    public @Nullable ResourceKind getResourceKind() {
        ResourceKind result = null;
        var model = model();
        var graph = graph();
        if (model != null) {
            result = model.getKind();
        } else if (graph != null) {
            result = ResourceKind.toResource(graph.getRole());
        }
        return result;
    }

    /** Constructs an exportable for a given {@link Graph}. */
    static public Exportable instance(Graph graph) {
        return new Exportable(EnumSet.of(Kind.GRAPH), QualName.parse(graph.getName()), null, graph,
            null);
    }

    /** Constructs an exportable for a given {@link JGraph}. */
    static public Exportable instance(JGraph<?> jGraph) {
        var kinds = EnumSet.of(Kind.GRAPH, Kind.JGRAPH);
        var graph = jGraph.getModel().getGraph();
        var model = jGraph instanceof AspectJGraph ag
            ? ag.getModel().getResourceModel()
            : null;
        if (model != null) {
            kinds.add(Kind.RESOURCE);
        }
        var name = QualName.parse(graph.getName());
        return new Exportable(kinds, name, jGraph, graph, model);
    }

    /** Constructs an exportable for a given {@link ResourceModel}. */
    static public Exportable instance(NamedResourceModel<?> model) {
        var kinds = EnumSet.of(Kind.RESOURCE);
        Graph graph = null;
        if (model.getKind().isGraphBased()) {
            kinds.add(Kind.GRAPH);
            graph = ((GraphBasedModel<?>) model).getSource();
        }
        var name = model.getQualName();
        return new Exportable(kinds, name, null, graph, model);
    }

    /** Constructs an exportable for a given {@link ResourceModel} that is displayed in a {@link JGraph}. */
    static public Exportable instance(JGraph<?> jGraph, NamedResourceModel<?> model) {
        var kinds = EnumSet.of(Kind.GRAPH, Kind.JGRAPH, Kind.RESOURCE);
        var graph = jGraph.getModel().getGraph();
        return new Exportable(kinds, model.getQualName(), jGraph, graph, model);
    }

    /** Kinds of objects that can be exported. */
    public static enum Kind {
        /** Instances of {@link Graph}. */
        GRAPH,
        /** Instances of {@link JGraph}. */
        JGRAPH,
        /** Instances of {@link ResourceModel}. */
        RESOURCE;
    }
}