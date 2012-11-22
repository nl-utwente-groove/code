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
package groove.gui.look;

import groove.gui.jgraph.GraphJCell;

import java.util.Set;

/**
 * Creates a refresher for the edge source or target shape.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EdgeEndShapeValue implements VisualValue<EdgeEnd> {
    /** Creates an label refresher for either the source or the target shape. */
    public EdgeEndShapeValue(boolean source) {
        this.source = source;
    }

    @Override
    public EdgeEnd get(GraphJCell cell) {
        EdgeEnd result;
        Set<Look> looks = cell.getLooks();
        // if arrows are shown on labels, do not show them on edges
        if (cell.getJGraph().isShowArrowsOnLabels()
            || looks.contains(Look.NO_ARROW)) {
            result = EdgeEnd.NONE;
        } else {
            // first see what the looks have to say
            VisualMap looksMap = Look.getVisualsFor(cell.getLooks());
            if (cell.getLooks().contains(Look.BIDIRECTIONAL) || !this.source) {
                // use the target end
                result = looksMap.getEdgeTargetShape();
            } else {
                // use the source end
                result = looksMap.getEdgeSourceShape();
            }
        }
        return result;
    }

    private final boolean source;
}
