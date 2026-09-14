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
package nl.utwente.groove.gui.view;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.gui.view.cell.PlainEdgeCell;
import nl.utwente.groove.gui.view.cell.PlainVertexCell;
import nl.utwente.groove.util.AIGenerated;

/**
 * View model of a plain graph of no particular role.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class PlainGraphViewModel extends GraphViewModel<Graph> {
    /** Creates a new model for a given controller and cell store. */
    public PlainGraphViewModel(GraphViewController<Graph> controller, CellStore<Graph> store) {
        super(controller, store);
    }

    @Override
    protected PlainVertexCell createVertexCell(Node node) {
        return new PlainVertexCell(this);
    }

    @Override
    protected PlainEdgeCell createEdgeCell() {
        return new PlainEdgeCell(this);
    }
}
