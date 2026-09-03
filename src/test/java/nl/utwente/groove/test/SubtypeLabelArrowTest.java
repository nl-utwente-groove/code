/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
package nl.utwente.groove.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Point2D;
import java.io.IOException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.TypeModel;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.display.DisplayKind;
import nl.utwente.groove.gui.jgraph.AspectJEdge;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.jgraph.AspectJModel;
import nl.utwente.groove.gui.look.EdgeEnd;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.line.StringFormat;

/**
 * Regression test for gh #878: with the "Show arrows on labels" option
 * enabled, a subtype edge in a type graph must not show a direction arrow
 * on its (empty) label, since it keeps the subtype arrowhead on the edge
 * itself. Labelled edges do get the arrow, as a check that the option is
 * in effect.
 * <p>
 * The type graph is rendered into a headless {@link AspectJGraph}, the
 * way the {@code Imager} does it; only the computed visuals are inspected.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class SubtypeLabelArrowTest {
    /** Grammar containing the fixture type graph. */
    private static final String GRAMMAR = "junit/types/shadow.gps";
    /** Type graph with both subtype edges and labelled edges. */
    private static final String TYPE_GRAPH = "OK-01-common-supertype";
    /** Start and end points of the (horizontal, left-to-right) edge path
     * used to orient the label arrows. */
    private static final Point2D START = new Point2D.Double(0, 0);
    private static final Point2D END = new Point2D.Double(100, 0);

    /** Value of the arrows-on-labels option before the test. */
    private boolean oldArrowsOnLabels;

    @BeforeEach
    void enableArrowsOnLabels() {
        Options options = Options.instance();
        this.oldArrowsOnLabels = options.isSelected(Options.SHOW_ARROWS_ON_LABELS_OPTION);
        options.setSelected(Options.SHOW_ARROWS_ON_LABELS_OPTION, true);
    }

    @AfterEach
    void restoreArrowsOnLabels() {
        Options.instance().setSelected(Options.SHOW_ARROWS_ON_LABELS_OPTION, this.oldArrowsOnLabels);
    }

    /**
     * Every subtype edge has an empty label without direction arrow, while
     * keeping the subtype arrowhead at its target end; every other edge shows
     * its label text with an arrow attached.
     */
    @Test
    void subtypeEdgesHaveNoLabelArrow() throws IOException {
        AspectJModel model = loadTypeGraph();
        boolean seenSubtype = false;
        boolean seenLabelled = false;
        for (var cell : model.getRoots()) {
            if (cell instanceof AspectJEdge jEdge) {
                var visuals = jEdge.getVisuals();
                String text = visuals.getLabel().toString(StringFormat.instance(), START, END).toString();
                if (jEdge.getAspects().has(AspectKind.SUBTYPE)) {
                    assertEquals("", text, "subtype edge label should be empty");
                    assertEquals(EdgeEnd.SUBTYPE, visuals.getEdgeTargetShape());
                    seenSubtype = true;
                } else {
                    String label = jEdge.getEdge().getInnerText();
                    assertTrue(text.startsWith(label), "label text lost: " + text);
                    assertNotEquals(label, text, "no arrow on label " + label);
                    seenLabelled = true;
                }
            }
        }
        assertTrue(seenSubtype, "fixture has no subtype edge");
        assertTrue(seenLabelled, "fixture has no labelled edge");
    }

    /** Loads the fixture type graph into a headless type-graph JGraph model. */
    private AspectJModel loadTypeGraph() throws IOException {
        GrammarModel grammar = Groove.loadGrammar(GRAMMAR);
        TypeModel typeModel = grammar.getTypeModel(QualName.parse(TYPE_GRAPH));
        AspectJGraph jGraph = new AspectJGraph(null, DisplayKind.TYPE, false);
        jGraph.getController().setGrammar(grammar);
        AspectJModel result = jGraph.newModel();
        result.loadGraph(typeModel.getSource());
        jGraph.setModel(result);
        return result;
    }
}
