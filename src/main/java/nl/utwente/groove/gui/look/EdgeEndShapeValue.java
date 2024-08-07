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
package nl.utwente.groove.gui.look;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.jgraph.AspectJEdge;
import nl.utwente.groove.gui.jgraph.JCell;
import nl.utwente.groove.gui.jgraph.JGraph;

/**
 * Creates a refresher for the edge source or target shape.
 * @author Arend Rensink
 * @version $Revision$
 */
public class EdgeEndShapeValue implements VisualValue<EdgeEnd> {
    /** Creates an label refresher for either the source or the target shape. */
    public EdgeEndShapeValue(boolean source) {
        this.source = source;
    }

    @Override
    public <G extends @NonNull Graph> EdgeEnd get(JGraph<G> jGraph, JCell<G> cell) {
        // first see what the looks have to say
        VisualMap looksMap = Look.getVisualsFor(cell.getLooks());
        EdgeEnd result = this.source
            ? looksMap.getEdgeSourceShape()
            : looksMap.getEdgeTargetShape();
        Set<Look> looks = cell.getLooks();
        if (looks.contains(Look.NO_ARROW)) {
            result = EdgeEnd.NONE;
        } else if (jGraph.isShowArrowsOnLabels()) {
            // only show some arrows
            boolean show = false;
            if (cell instanceof AspectJEdge jEdge) {
                show |= jEdge.getAspects().has(AspectKind.SUBTYPE);
                show |= this.source && jEdge.getAspects().has(AspectKind.COMPOSITE);
                show |= jEdge.isNodeEdgeOut();
            }
            if (!show) {
                result = EdgeEnd.NONE;
            }
        } else if (looks.contains(Look.BIDIRECTIONAL)) {
            // use the target end
            result = looksMap.getEdgeTargetShape();
        }
        return result;
    }

    private final boolean source;
}
