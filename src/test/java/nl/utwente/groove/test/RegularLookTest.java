/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Font;
import java.io.IOException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.look.Look;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectViewEdge;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Checks which rule edges get the regular-expression look, and with it an italic
 * font: those with a real regular expression, but not a plain negation of an atom.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class RegularLookTest {
    @Test
    void plainNegationIsNotRegular() throws IOException {
        // the eat rule of ferryman has a !moored edge and no other non-atom labels
        int edges = 0;
        for (var edge : ruleEdges("junit/samples/ferryman.gps", "eat")) {
            assertTrue(!edge.getLooks().contains(Look.REGULAR), "regular look on " + edge);
            assertEquals(Font.PLAIN, edge.getVisuals().getFont(), "font of " + edge);
            edges++;
        }
        assertTrue(edges > 0, "no edges");
    }

    @Test
    void regularExpressionIsRegular() throws IOException {
        // the append rule of regexpr has next* edges among plain ones
        int regular = 0;
        int plain = 0;
        for (var edge : ruleEdges("junit/samples/regexpr.gps", "append")) {
            if (edge.getLooks().contains(Look.REGULAR)) {
                // the embargo look, which comes later, resets the font to plain
                if (!edge.getLooks().contains(Look.EMBARGO)) {
                    assertEquals(Font.ITALIC, edge.getVisuals().getFont(), "font of " + edge);
                    regular++;
                }
            } else {
                assertEquals(Font.PLAIN, edge.getVisuals().getFont(), "font of " + edge);
                plain++;
            }
        }
        assertTrue(regular > 0, "no regular expression edges");
        assertTrue(plain > 0, "no plain edges");
    }

    /** Returns the edge cells of a named rule of a grammar, in a detached view model. */
    private static Iterable<AspectViewEdge> ruleEdges(String grammarPath, String rule)
        throws IOException {
        GrammarModel grammar = Groove.loadGrammar(grammarPath);
        var graph = grammar.getRuleModel(QualName.name(rule)).getSource();
        var controller = new AspectGraphViewController(null, DisplayKind.RULE, false);
        controller.setGrammar(grammar);
        var model = controller.getCanvas().newViewModel();
        model.loadGraph(graph);
        var result = new java.util.ArrayList<AspectViewEdge>();
        for (var cell : model.getCells()) {
            if (cell instanceof AspectViewEdge edge) {
                result.add(edge);
            }
        }
        return result;
    }
}
