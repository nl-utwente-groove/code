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
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.jgraph.AspectJModel;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;

/**
 * Regression test: the visuals of a cell must be computable while its content
 * model is not (yet) shown on a canvas. The state display builds the model of
 * the start state and copies the start graph's cell visuals before the model
 * is set on the graph component, so the look computation must not go through
 * the canvas for anything the model itself knows (type graph, resource model).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class DetachedCellVisualsTest {
    /** Grammar whose start graph is loaded. */
    private static final String GRAMMAR = "junit/samples/ferryman.gps";

    @Test
    void visualsOfDetachedModelCells() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        AspectGraph startGraph = grammar.getStartGraphModel().getSource();
        assert startGraph != null; // the fixture grammar has a start graph
        AspectJGraph jGraph = new AspectJGraph(null, DisplayKind.STATE, false);
        jGraph.getController().setGrammar(grammar);
        AspectJModel model = jGraph.newModel();
        model.loadGraph(startGraph);
        // the model is deliberately not set on the graph component
        int count = 0;
        for (var cell : model.getRoots()) {
            assertNotNull(cell.getVisuals().getLabel(), "no label for " + cell);
            count++;
        }
        assertTrue(count > 0, "start graph has no cells");
    }
}
