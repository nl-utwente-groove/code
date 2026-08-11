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
package nl.utwente.groove.test.grammar;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.model.HostModel;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Regression test for resource models created outside the context of a
 * grammar. These used to share a single static change tracker
 * ({@code ChangeCount.DUMMY_TRACKER}), whose one stale answer was consumed
 * by the first grammar-less model in the JVM; every later model then never
 * built its resource, and {@code toResource()} failed on an assertion.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GrammarlessModelTest {
    /** Every grammar-less model must build its resource, not just the first
     * one created in the JVM. */
    @Test
    public void modelsBuildIndependently() throws FormatException {
        AspectGraph graph = AspectGraph.emptyGraph("g", GraphRole.HOST, true);
        assertNotNull(new HostModel(null, graph).toHost());
        assertNotNull(new HostModel(null, graph).toHost());
        assertNotNull(new HostModel(null, graph).toHost());
    }

    /** Querying the errors first must not stop the resource from being built. */
    @Test
    public void errorsThenResource() throws FormatException {
        AspectGraph graph = AspectGraph.emptyGraph("g", GraphRole.HOST, true);
        HostModel model = new HostModel(null, graph);
        assertTrue(model.getErrors().isEmpty());
        assertNotNull(model.toHost());
    }
}
