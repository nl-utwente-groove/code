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
 * ({@code mult=k:}): it is only allowed in host graphs (its use in rules is
 * deferred until a real use case comes by), the value must be a positive
 * constant, the edge must be binary, and any use requires the parallelEdges
 * grammar property to be SPO or DPO. Also tests that a host graph edge with
 * a multiplicity compiles into that many parallel host edges.
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

    /** Returns the model of a named host graph in a named test grammar. */
    private ResourceModel<?> getHostModel(String grammarName, String hostName) {
        return getGrammar(grammarName).getResource(ResourceKind.HOST, QualName.parse(hostName));
    }

    /** Asserts that a resource model has an error containing a given text. */
    private void assertError(ResourceModel<?> model, String text) {
        assertTrue(model.hasErrors());
        assertTrue("Unexpected errors: " + model.getErrors(),
                   model.getErrors().toString().contains(text));
    }

    /** A multiplicity is not allowed on rule edges: its use in rules is
     * deferred until a real use case comes by. */
    @Test
    public void testRuleMult() {
        assertError(getGrammar("multErrors").getRuleModel(QualName.parse("ruleMult")),
                    "not allowed");
    }

    /** A multiplicity must be a single number, not a range. */
    @Test
    public void testHostRange() {
        assertError(getHostModel("multErrors", "hostRange"), "single");
    }

    /** A multiplicity must be positive. */
    @Test
    public void testHostZero() {
        assertError(getHostModel("multErrors", "hostZero"), "Multiplicity 0");
    }

    /** A multiplicity is only allowed on binary edges. */
    @Test
    public void testHostFlag() {
        assertError(getHostModel("multErrors", "hostFlag"), "binary");
    }

    /** A host graph multiplicity above 1 requires the parallelEdges property. */
    @Test
    public void testNoParallelHost() {
        assertError(getHostModel("multNoParallel", "hostMult"), "parallelEdges grammar property");
    }

    /** A host graph edge with multiplicity 2 compiles into 2 parallel edges. */
    @Test
    public void testHostMult() throws FormatException {
        var hostModel = getGrammar("multErrors").getHostModel(QualName.parse("hostMult"));
        assertFalse("Unexpected errors: " + hostModel.getErrors(), hostModel.hasErrors());
        assertEquals(2, hostModel.toResource().edgeSet().size());
    }
}
