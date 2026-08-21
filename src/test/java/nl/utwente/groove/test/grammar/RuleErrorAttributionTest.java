// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2026 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package nl.utwente.groove.test.grammar;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.model.RuleModel;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatError;

/**
 * Tests the error-attribution contract of the rule compiler (gh #893):
 * whichever compilation phase reports an error, the error reaches the rule
 * model carrying at least one context element of the model's <em>source</em>
 * aspect graph, so that the GUI can highlight it. One deliberately broken
 * rule per phase (level distribution, pattern building, typing, condition
 * assembly, signature extraction); the typing case is exercised both on a
 * plain rule and on a normalised ({@code let}) rule, since the latter needs
 * the final normal-to-source hop of the pullback.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class RuleErrorAttributionTest {
    /** Returns the model of a named rule in a named {@code junit/rules} grammar. */
    private RuleModel getRuleModel(String grammarName, String ruleName) {
        try {
            return Groove
                .loadGrammar("junit/rules/" + grammarName)
                .getRuleModel(QualName.parse(ruleName));
        } catch (IOException exc) {
            fail(exc.getMessage());
            throw new IllegalStateException();
        }
    }

    /**
     * Asserts that the model has an error containing a given text, and
     * returns the source-graph context elements of the first such error,
     * which must be non-empty.
     */
    private List<Element> assertSourceError(RuleModel model, String text) {
        assertTrue("Expected errors in " + model.getQualName(), model.hasErrors());
        AspectGraph source = model.getSource();
        for (FormatError error : model.getErrors()) {
            if (error.toString().contains(text)) {
                List<Element> result = error
                    .getElements()
                    .stream()
                    .filter(e -> e instanceof Node n
                        ? source.containsNode(n)
                        : source.containsEdge((Edge) e))
                    .toList();
                assertTrue("Error '" + error + "' carries no source element, only "
                    + error.getElements(), !result.isEmpty());
                return result;
            }
        }
        fail("No error containing '" + text + "' among " + model.getErrors());
        throw new IllegalStateException();
    }

    /** Normalisation, before the compiler proper: an undefined quantifier
     * level name on a {@code test} edge. */
    @Test
    public void testNormalisationError() {
        assertSourceError(getRuleModel("quantLevelErrors", "undefLevel"),
                          "Undefined nesting level");
    }

    /** Level distribution: an undefined quantifier level name on an ordinary edge. */
    @Test
    public void testDistributionError() {
        assertSourceError(getRuleModel("quantLevelErrors", "undefEdgeLevel"),
                          "Undefined nesting level");
    }

    /** Pattern building: a creator edge with a label variable bound nowhere. */
    @Test
    public void testBuilderVariableError() {
        assertSourceError(getRuleModel("compileErrors", "unassignedVar"),
                          "Unassigned label variable");
    }

    /** Pattern building: a parameter node below the top level. */
    @Test
    public void testBuilderParameterLevelError() {
        assertSourceError(getRuleModel("compileErrors", "parOnForall"),
                          "only allowed on top existential level");
    }

    /** Typing: an edge label absent from the type graph, on a plain rule. */
    @Test
    public void testTyperError() {
        assertSourceError(getRuleModel("compileErrors", "unknownEdge"), "y");
    }

    /**
     * Typing: a {@code let} assigning a field that the type graph does not
     * declare. The error arises on an edge of the normalised graph; it must
     * be traced back to the {@code let} edge of the source graph.
     */
    @Test
    public void testTyperLetError() {
        List<Element> elements
            = assertSourceError(getRuleModel("compileErrors", "letUnknown"), "y");
        assertTrue("Error not attributed to the let edge: " + elements,
                   elements
                       .stream()
                       .anyMatch(e -> e instanceof AspectEdge edge
                           && edge.has(AspectKind.LET)));
    }

    /** Condition assembly: a regular-expression path through an erased edge. */
    @Test
    public void testAssemblerError() {
        assertSourceError(getRuleModel("regExprErasure", "seqThroughEraser"),
                          "may match a path through");
    }

    /** Signature extraction: a parameter number used twice. (A parameter in
     * a NAC is already rejected as an aspect conflict and never reaches the
     * extractor.) */
    @Test
    public void testSignatureError() {
        assertSourceError(getRuleModel("compileErrors", "parDuplicate"), "more than once");
    }

    /** Signature extraction: a gap in the parameter numbering. The error
     * has no node of its own to point at, so it carries the existing
     * parameter nodes instead. */
    @Test
    public void testSignatureGapError() {
        assertSourceError(getRuleModel("compileErrors", "parGap"), "missing");
    }
}
