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
package nl.utwente.groove.gui.export;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.model.ResourceModel;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.view.AspectGraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Exporter.ExportKind;
import nl.utwente.groove.util.QualName;

/**
 * Exportable wrapping a graph rendered on a {@link GraphCanvas}, next to the graph and
 * (optional) resource model that the canvas displays.
 * This is the only kind of {@link Exportable} accepted by the exporters of
 * kind {@link ExportKind#CANVAS}.
 * @author Harold Bruijntjes
 * @version $Revision$
 */
@NonNullByDefault
public class CanvasExportable extends Exportable {
    private CanvasExportable(Set<ExportKind> exportKinds, QualName qualName,
                             GraphCanvas<?> canvas, @Nullable Graph graph,
                             @Nullable ResourceModel<?> resourceModel) {
        super(exportKinds, qualName, graph, resourceModel);
        this.canvas = canvas;
    }

    /** Returns the canvas contained in this exportable object. */
    public GraphCanvas<?> canvas() {
        return this.canvas;
    }

    private final GraphCanvas<?> canvas;

    /** Constructs an exportable for a given canvas, which must show a graph. */
    static public <G extends Graph> CanvasExportable instance(GraphCanvas<G> canvas) {
        var graph = canvas.getGraph();
        assert graph != null; // only canvases showing a graph are exported
        var resourceModel = canvas instanceof AspectGraphCanvas ac
            ? ac.getResourceModel()
            : null;
        var kinds = EnumSet.of(ExportKind.CANVAS, ExportKind.GRAPH);
        if (resourceModel != null) {
            kinds.add(ExportKind.RESOURCE);
        }
        var name = QualName.parse(graph.getName());
        return new CanvasExportable(kinds, name, canvas, graph, resourceModel);
    }
}
