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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.model.ResourceModel;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the static checks on the parallel-edge multiplicity aspect
 * ({@code mult=k:}): the value must be a positive constant, the edge must be
 * binary, the combination with a NAC role ({@code not:} or {@code cnew:}) is
 * disallowed entirely (counting NACs are not supported), rule labels must
 * have edge images, and any multiplicity above 1 requires the parallelEdges
 * grammar property. Also tests that a host graph edge with a multiplicity
 * compiles into that many parallel host edges.
 * @author Arend Rensink
 * @version $Revision$
 */
@SuppressWarnings("javadoc")
public class MultAspectTest {
    /** Returns a named test grammar. */
    private GrammarModel getGrammar(String grammarName) {
        try {
            return Groove.loadGrammar("junit/rules/" + grammarName);
        } catch (IOException exc) {
            Assert.fail(exc.getMessage());
            throw new IllegalStateException();
        }
    }

    /** Returns the model of a named rule in a named test grammar. */
    private ResourceModel<?> getRuleModel(String grammarName, String ruleName) {
        return getGrammar(grammarName).getRuleModel(QualName.parse(ruleName));
    }

    /** Asserts that a resource model has an error containing a given text. */
    private void assertError(ResourceModel<?> model, String text) {
        assertTrue(model.hasErrors());
        assertTrue("Unexpected errors: " + model.getErrors(),
                   model.getErrors().toString().contains(text));
    }

    /** An embargo edge may not carry a multiplicity. */
    @Test
    public void testEmbargoMult() {
        assertError(getRuleModel("multErrors", "embargoMult"), "counting NACs");
    }

    /** The NAC prohibition is total: even multiplicity 1 is not allowed. */
    @Test
    public void testEmbargoMultOne() {
        assertError(getRuleModel("multErrors", "embargoMultOne"), "counting NACs");
    }

    /** An adder edge may not carry a multiplicity, since its implicit NAC
     * would be a counting NAC. */
    @Test
    public void testAdderMult() {
        assertError(getRuleModel("multErrors", "adderMult"), "counting NAC");
    }

    /** A multiplicity must be a single number, not a range. */
    @Test
    public void testRangeMult() {
        assertError(getRuleModel("multErrors", "rangeMult"), "single");
    }

    /** A multiplicity must be positive. */
    @Test
    public void testZeroMult() {
        assertError(getRuleModel("multErrors", "zeroMult"), "Multiplicity 0");
    }

    /** A multiplicity is only allowed on labels with edge images. */
    @Test
    public void testPathMult() {
        assertError(getRuleModel("multErrors", "pathMult"), "regular expression label");
    }

    /** A multiplicity is only allowed on binary edges. */
    @Test
    public void testFlagMult() {
        assertError(getRuleModel("multErrors", "flagMult"), "binary");
    }

    /** A rule multiplicity above 1 requires the parallelEdges property. */
    @Test
    public void testNoParallelRule() {
        assertError(getRuleModel("multNoParallel", "creatorMult"),
                    "parallelEdges grammar property");
    }

    /** A host graph multiplicity above 1 requires the parallelEdges property. */
    @Test
    public void testNoParallelHost() {
        var hostModel
            = getGrammar("multNoParallel").getResource(ResourceKind.HOST, QualName.parse("hostMult"));
        assertError(hostModel, "parallelEdges grammar property");
    }

    /** A host graph edge with multiplicity 2 compiles into 2 parallel edges. */
    @Test
    public void testHostMult() throws FormatException {
        var hostModel = getGrammar("multErrors").getHostModel(QualName.parse("hostMult"));
        assertFalse("Unexpected errors: " + hostModel.getErrors(), hostModel.hasErrors());
        assertEquals(2, hostModel.toResource().edgeSet().size());
    }
}
