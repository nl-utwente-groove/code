/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.io.external;

import java.util.EnumSet;

import groove.grammar.QualName;
import groove.grammar.model.GraphBasedModel;
import groove.grammar.model.NamedResourceModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.ResourceModel;
import groove.graph.GGraph;
import groove.graph.Graph;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.JGraph;
import groove.io.external.Porter.Kind;

/**
 * Wrapper class for resources to be exported.
 * Can wrap either {@link Graph}s, {@link JGraph}s or {@link ResourceModel}s.
 * @author Harold Bruijntjes
 * @version $Revision $
 */
public class Exportable {
    private final EnumSet<Porter.Kind> porterKinds;
    private final QualName name;
    private final Graph graph;
    private final JGraph<?> jGraph;
    private final ResourceModel<?> model;

    /** Constructs an exportable for a given {@link Graph}. */
    public Exportable(Graph graph) {
        this.porterKinds = EnumSet.of(Kind.GRAPH);
        this.name = QualName.parse(graph.getName());
        this.jGraph = null;
        this.graph = graph;
        this.model = null;
    }

    /** Constructs an exportable for a given {@link JGraph}. */
    public Exportable(JGraph<?> jGraph) {
        this.porterKinds = EnumSet.of(Kind.GRAPH, Kind.JGRAPH);
        this.jGraph = jGraph;
        this.graph = jGraph.getModel()
            .getGraph();
        this.model = jGraph instanceof AspectJGraph ? ((AspectJGraph) jGraph).getModel()
            .getResourceModel() : null;
        if (this.model != null) {
            this.porterKinds.add(Kind.RESOURCE);
        }
        this.name = QualName.parse(this.graph.getName());
    }

    /** Constructs an exportable for a given {@link ResourceModel}. */
    public Exportable(NamedResourceModel<?> model) {
        this.porterKinds = EnumSet.of(Kind.RESOURCE);
        if (model.getKind()
            .isGraphBased()) {
            this.porterKinds.add(Kind.GRAPH);
            this.graph = ((GraphBasedModel<?>) model).getSource();
        } else {
            this.graph = null;
        }

        this.name = model.getQualName();
        this.model = model;
        this.jGraph = null;
    }

    /** Constructs an exportable for a given {@link ResourceModel} that is displayed in a {@link JGraph}. */
    public Exportable(JGraph<?> jGraph, NamedResourceModel<?> model) {
        this.porterKinds = EnumSet.of(Kind.GRAPH, Kind.JGRAPH, Kind.RESOURCE);
        this.name = model.getQualName();
        this.jGraph = jGraph;
        this.graph = jGraph.getModel()
            .getGraph();
        this.model = model;
    }

    /** Indicates if this exportable contains an object of a given porter kind. */
    public boolean containsKind(Kind kind) {
        return this.porterKinds.contains(kind);
    }

    /** Returns the name of the object wrapped by this exportable. */
    public QualName getQualName() {
        return this.name;
    }

    /** Returns the {@link GGraph} wrapped by this exportable, if any. */
    public Graph getGraph() {
        return this.graph;
    }

    /** Returns the {@link JGraph} wrapped by this exportable, if any. */
    public JGraph<?> getJGraph() {
        return this.jGraph;
    }

    /** Returns the resource kind of the model wrapped by this exportable, if any. */
    public ResourceKind getKind() {
        ResourceKind result = null;
        if (getModel() != null) {
            result = getModel().getKind();
        } else if (getGraph() != null) {
            result = ResourceKind.toResource(getGraph().getRole());
        }
        return result;
    }

    /** Returns the {@link ResourceModel} wrapped by this exportable, if any. */
    public ResourceModel<?> getModel() {
        return this.model;
    }
}