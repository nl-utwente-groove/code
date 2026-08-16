// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

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
package nl.utwente.groove.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceModel;
import nl.utwente.groove.io.Groove;

/**
 * Tests the static check on composite regular expressions versus erasers
 * (the ignoreRegExp grammar property): in a grammar with parallel edges,
 * a rule is erroneous if a composite regular expression edge may match a
 * path through an edge that the rule erases, since such path witnesses are
 * untracked and thereby escape the identification condition.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RegExprErasureCheckTest {
    /** Returns the model of a named rule in a named test grammar. */
    private ResourceModel<?> getRuleModel(String grammarName, String ruleName) {
        try {
            GrammarModel grammar = Groove.loadGrammar("junit/rules/" + grammarName);
            return grammar.getRuleModel(QualName.parse(ruleName));
        } catch (IOException exc) {
            Assert.fail(exc.getMessage());
            throw new IllegalStateException();
        }
    }

    /** Asserts that a rule model has an error reported by the erasure check. */
    private void assertErasureError(ResourceModel<?> ruleModel) {
        assertTrue(ruleModel.hasErrors());
        assertTrue("Unexpected errors: " + ruleModel.getErrors(),
                   ruleModel.getErrors().toString().contains("may match a path through"));
    }

    /** A sequence a.b may run through the erased b-edge. */
    @Test
    public void testSeqThroughEraser() {
        assertErasureError(getRuleModel("regExprErasure", "seqThroughEraser"));
    }

    /** An eraser whose edge type the expression cannot traverse is harmless. */
    @Test
    public void testSeqBesideEraser() {
        assertFalse(getRuleModel("regExprErasure", "seqBesideEraser").hasErrors());
    }

    /** Wildcard traversability is positional: between two A-nodes, the
     * wildcards can only traverse a-edges, so the erased c-edge (which a
     * label-based approximation would flag) does not conflict. */
    @Test
    public void testWildcardPositional() {
        assertFalse(getRuleModel("regExprErasure", "wildcardPositional").hasErrors());
    }

    /** An eraser node does not contribute erased edge types: DPO semantics
     * implies the dangling-edge condition, so node deletion can never erase
     * unmatched edges — in particular not the b-edges that the sequence a.b
     * runs through. */
    @Test
    public void testNodeEraserIncident() {
        assertFalse(getRuleModel("regExprErasure", "nodeEraserIncident").hasErrors());
    }

    /** The ignoreRegExp property accepts the overlapping rule. */
    @Test
    public void testIgnoreRegExp() {
        assertFalse(getRuleModel("regExprErasureIgnored", "seqThroughEraser").hasErrors());
    }
}
