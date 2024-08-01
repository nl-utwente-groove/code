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
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.model.NamedResourceModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.ResourceModel;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.jgraph.AspectJModel;
import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.io.external.Exporter.ExportKind;

/**
 * Wrapper class for resources to be exported.
 * Can wrap either {@link Graph}s, {@link JGraph}s or {@link ResourceModel}s (or a combination).
 * @param exportKinds the kind of exportable objects that this {@link Exportable} contains
 * The export kind determines which of the fields of this {@link Exportable} is non-{@code null}:
 * <ul>
 * <li> {@link ExportKind#JGRAPH} is an element if and only if {@link #jGraph()} is non-{@code null}
 * (note that this may but does not have to imply that {@link ExportKind#RESOURCE} is also contained)
 * <li> {@link ExportKind#RESOURCE} is an element if and only if {@link #resourceModel()} is non-{@code null}
 * (note that then {@link ExportKind#GRAPH} also holds if and only if the resource is graph-based)
 * <li> {@link ExportKind#GRAPH} is an element if and only if {@link #graph()} is non-{@code null}
 * </ul>
 * @param qualName the name of the exportable object
 * @param jGraph the optional {@link JGraph} contained in this exportable object
 * @param graph the optional {@link Graph} contained in this exportable object
 * @param resourceModel the optional {@link ResourceModel} contained in this exportable object
 * @author Harold Bruijntjes
 * @version $Revision$
 */
public record Exportable(Set<ExportKind> exportKinds, @NonNull QualName qualName,
    @Nullable JGraph<?> jGraph, @Nullable Graph graph, @Nullable ResourceModel<?> resourceModel) {

    /** Indicates if this exportable contains an object of a given export kind.
     * The export kind determines which of the fields of this {@link Exportable} is non-{@code null}:
     * <ul>
     * <li> {@code hasExportKind(JGRAPH)} holds if and only if {@link #jGraph()} is non-{@code null}
     * (note that this may but does not have to imply that {@code hasExportKind(RESOURCE)} also holds)
     * <li> {@code hasExportKind(RESOURCE)} holds if and only if {@link #resourceModel()} is non-{@code null}
     * (note that then {@code hasExportKind(GRAPH)} also holds if and only if the resource is graph-based)
     * <li> {@code hasExportKind(GRAPH)} holds if and only if {@link #graph()} is non-{@code null}
     * </ul>
     */
    public boolean hasExportKind(Exporter.ExportKind kind) {
        return exportKinds().contains(kind);
    }

    /** Returns the kind of the grammar resource wrapped by this exportable, if any.
     * Returns a non-{@code null} value if and only if {@code hasExportKind(RESOURCE)} holds,
     * in which case also {@link #resourceModel()} is non-{@code null}.
     */
    public @Nullable ResourceKind getResourceKind() {
        var model = resourceModel();
        return model == null
            ? null
            : model.getKind();
    }

    /** Constructs an exportable for a given {@link Graph}. */
    static public Exportable graph(Graph graph) {
        var kinds = EnumSet.of(ExportKind.GRAPH);
        return new Exportable(kinds, QualName.parse(graph.getName()), null, graph, null);
    }

    /** Constructs an exportable for a given {@link JGraph}. */
    static public <G extends @NonNull Graph> Exportable jGraph(JGraph<G> jGraph) {
        var jModel = jGraph.getModel();
        var graph = jModel == null
            ? null
            : jModel.getGraph();
        var resourceModel = jModel instanceof AspectJModel am
            ? am.getResourceModel()
            : null;
        var kinds = EnumSet.of(ExportKind.JGRAPH, ExportKind.GRAPH);
        if (resourceModel != null) {
            kinds.add(ExportKind.RESOURCE);
        }
        var name = QualName.parse(graph.getName());
        return new Exportable(kinds, name, jGraph, graph, resourceModel);
    }

    /** Constructs an exportable for a given {@link ResourceModel}. */
    static public Exportable resource(NamedResourceModel<?> resourceModel) {
        var name = resourceModel.getQualName();
        var kinds = EnumSet.of(ExportKind.RESOURCE);
        Graph graph = null;
        if (resourceModel instanceof GraphBasedModel<?> graphModel) {
            graph = graphModel.getSource();
            kinds.add(ExportKind.GRAPH);
        }
        return new Exportable(kinds, name, null, graph, resourceModel);
    }
}