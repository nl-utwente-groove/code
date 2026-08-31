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
package nl.utwente.groove.test.verify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.explore.Exploration;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.lts.GraphTransition;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.verify.BuchiLocation;
import nl.utwente.groove.verify.ProductState;
import nl.utwente.groove.verify.ProductTransition;

/**
 * Tests the {@link ProductTransition} (and, in passing, {@link ProductState})
 * objects of the LTL product construction. In particular a regression test
 * for {@link ProductTransition#equals}, which formerly returned {@code false}
 * on identity — masked in practice by {@code HashMap}'s reference-equality
 * short-circuit.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class ProductTransitionTest {
    /** Location of the fixture grammar. */
    static private final String GRAMMAR = "junit/samples/mc.gps";

    /** Tests the getters and string rendering. */
    @Test
    public void testBasics() {
        GTS gts = explore();
        GraphTransition trans = firstTransition(gts);
        BuchiLocation loc0 = new BuchiLocation(0);
        BuchiLocation loc1 = new BuchiLocation(1);
        ProductState source = new ProductState(trans.source(), loc0);
        ProductState target = new ProductState(trans, loc1);
        assertSame(trans.source(), source.getGraphState());
        assertSame(loc0, source.getBuchiLocation());
        assertSame(trans.target(), target.getGraphState());
        ProductTransition product = new ProductTransition(source, trans, target);
        assertSame(source, product.source());
        assertSame(target, product.target());
        assertSame(trans, product.graphTransition());
        assertSame(trans.getAction(), product.rule());
        assertTrue(product.toString().contains("-->"));
    }

    /** Tests equality and hash codes; in particular, equality must be
     * reflexive (the former identity test returned false). */
    @Test
    public void testEquality() {
        GTS gts = explore();
        Iterator<? extends GraphTransition> transIter = gts.edgeSet().iterator();
        GraphTransition trans1 = transIter.next();
        GraphTransition trans2 = transIter.next();
        assertNotEquals(trans1, trans2);
        BuchiLocation loc = new BuchiLocation(0);
        ProductState source = new ProductState(trans1.source(), loc);
        ProductState target1 = new ProductState(trans1, loc);
        ProductState target2 = new ProductState(trans2, loc);
        ProductTransition product = new ProductTransition(source, trans1, target1);
        // reflexivity
        assertEquals(product, product);
        // equality is determined by source (by identity) and transition
        ProductTransition same = new ProductTransition(source, trans1, target1);
        assertEquals(product, same);
        assertEquals(product.hashCode(), same.hashCode());
        ProductTransition other = new ProductTransition(source, trans2, target2);
        assertNotEquals(product, other);
        assertFalse(product.equals(null));
        assertFalse(product.equals((Object) "no transition"));
    }

    /** Explores the fixture grammar and returns the resulting GTS. */
    private GTS explore() {
        try {
            GrammarModel model = SystemStore.newGrammar(new File(GRAMMAR));
            model.setLocalActiveNames(ResourceKind.HOST, QualName.name("start"));
            GTS gts = new GTS(model.toGrammar());
            Exploration exploration = Exploration.explore(gts);
            assertFalse(exploration.isInterrupted());
            return gts;
        } catch (IOException | FormatException exc) {
            fail(exc.toString());
            throw new IllegalStateException(); // unreachable
        }
    }

    /** Returns the first transition of a GTS. */
    private GraphTransition firstTransition(GTS gts) {
        return gts.edgeSet().iterator().next();
    }
}
