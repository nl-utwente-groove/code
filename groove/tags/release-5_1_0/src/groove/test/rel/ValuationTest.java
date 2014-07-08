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
package groove.test.rel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import groove.grammar.rule.LabelVar;
import groove.grammar.rule.Valuation;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeFactory;
import groove.grammar.type.TypeNode;
import groove.graph.EdgeRole;

import org.junit.Test;

/** Test of the class {@link Valuation}. */
public class ValuationTest {
    private static LabelVar xVar = new LabelVar("x", EdgeRole.BINARY);
    private static LabelVar yVar = new LabelVar("y", EdgeRole.BINARY);
    private static TypeFactory factory = TypeFactory.newInstance();
    private static TypeNode top = factory.getTopNode();
    private static TypeEdge aEdge = factory.createEdge(top, "a", top);
    private static TypeEdge bEdge = factory.createEdge(top, "b", top);

    /** Tests the {@link Valuation#getMerger(Valuation)} method. */
    @Test
    public void testMerger() {
        Valuation val1 = new Valuation();
        val1.put(xVar, aEdge);
        Valuation mergedVals = val1.getMerger(null);
        assertEquals(val1, mergedVals);
        Valuation val2 = new Valuation();
        val2.put(yVar, bEdge);
        mergedVals = val1.getMerger(val2);
        assertEquals(2, mergedVals.size());
        assertEquals(aEdge, mergedVals.get(xVar));
        assertEquals(bEdge, mergedVals.get(yVar));
        val2.clear();
        val2.put(xVar, aEdge);
        mergedVals = val1.getMerger(val2);
        assertEquals(val1, mergedVals);
        val2.clear();
        val2.put(xVar, bEdge);
        mergedVals = val1.getMerger(val2);
        assertNull(mergedVals);
    }
}
