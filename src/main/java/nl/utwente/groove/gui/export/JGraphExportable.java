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
package nl.utwente.groove.gui.export;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.ResourceModel;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.jgraph.AspectJModel;
import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Exporter.ExportKind;

/**
 * Exportable wrapping a rendered {@link JGraph}, next to the graph and
 * (optional) resource model that the JGraph displays.
 * This is the only kind of {@link Exportable} accepted by the exporters of
 * kind {@link ExportKind#JGRAPH}.
 * @author Harold Bruijntjes
 * @version $Revision$
 */
@NonNullByDefault
public class JGraphExportable extends Exportable {
    private JGraphExportable(Set<ExportKind> exportKinds, QualName qualName, JGraph<?> jGraph,
                             @Nullable Graph graph, @Nullable ResourceModel<?> resourceModel) {
        super(exportKinds, qualName, graph, resourceModel);
        this.jGraph = jGraph;
    }

    /** Returns the {@link JGraph} contained in this exportable object. */
    public JGraph<?> jGraph() {
        return this.jGraph;
    }

    private final JGraph<?> jGraph;

    /** Constructs an exportable for a given {@link JGraph}. */
    static public <G extends Graph> JGraphExportable instance(JGraph<G> jGraph) {
        var jModel = jGraph.getModel();
        var graph = jModel == null
            ? null
            : jModel.getGraph();
        assert graph != null;
        var resourceModel = jModel instanceof AspectJModel am
            ? am.getResourceModel()
            : null;
        var kinds = EnumSet.of(ExportKind.JGRAPH, ExportKind.GRAPH);
        if (resourceModel != null) {
            kinds.add(ExportKind.RESOURCE);
        }
        var name = QualName.parse(jGraph.getName());
        return new JGraphExportable(kinds, name, jGraph, graph, resourceModel);
    }
}
