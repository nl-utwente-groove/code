/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.test.type;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.NamedResourceModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.parse.FormatException;

/** Set of tests for graph typing. */
public class TypeCheckTest {
    /** Location of the samples. */
    static public final String INPUT_DIR = "junit/types";
    private static final String ERR_PREFIX = "ERR-";
    private static final String OK_PREFIX = "OK-";

    /** Tests attribute typing. */
    @Test
    public void testAttributes() {
        test("attributes");
    }

    /** Tests containment typing. */
    @Test
    public void testContainment() {
        test("containment");
    }

    /** Tests expression typing. */
    @Test
    public void testExpressions() {
        test("expressions");
    }

    /** Tests multiplicity typing. */
    @Test
    public void testMult() {
        test("mult");
    }

    /** Tests node identity constraints. */
    @Test
    public void testNodeIds() {
        test("nodeids");
    }

    /** Tests node identity constraints. */
    @Test
    public void testQuantification() {
        test("quantification");
    }

    /** Tests regular expression typing. */
    @Test
    public void testRegExpr() {
        test("regexpr");
    }

    /** Tests for regression. */
    @Test
    public void testRegression() {
        test("regression");
    }

    /** Tests abstract node and edge types and edge shadowing. */
    @Test
    public void testShadow() {
        test("shadow");
    }

    /** Tests sort derivation for ambiguous expressions. */
    @Test
    public void testSorts() {
        test("sorts");
    }

    /** Tests type specialisation. */
    @Test
    public void testTypeSpecialisation() {
        test("type-specialisation");
    }

    /** Tests wildcard expression typing. */
    @Test
    public void testWildcards() {
        test("wildcards");
    }

    /** Tests the implicit type graph. */
    @Test
    public void testImplicit() {
        test("implicit");
    }

    /** Tests all rules in a named grammar (to be loaded from {@link #INPUT_DIR}). */
    private void test(String grammarName) {
        try {
            GrammarModel grammarView = Groove.loadGrammar(INPUT_DIR + "/" + grammarName);
            for (ResourceKind kind : EnumSet
                .of(ResourceKind.RULE, ResourceKind.HOST, ResourceKind.TYPE)) {
                for (Map.Entry<QualName,? extends NamedResourceModel<?>> entry : grammarView
                    .getResourceMap(kind)
                    .entrySet()) {
                    String name = entry.getKey().last();
                    NamedResourceModel<?> model = entry.getValue();
                    if (name.startsWith(OK_PREFIX)) {
                        testCorrect(model);
                    } else if (name.startsWith(ERR_PREFIX)) {
                        testErroneous(model);
                    }
                }
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    /** Tests that a given rule has no errors. */
    private void testCorrect(NamedResourceModel<?> model) {
        String kindName = model.getKind().getName();
        QualName modelName = model.getQualName();
        try {
            model.toResource();
        } catch (NullPointerException e) {
            Assert.fail(kindName + " " + modelName + " does not exist");
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
        }
    }

    /** Tests that a given rule has errors. */
    private void testErroneous(NamedResourceModel<?> model) {
        String kindName = model.getKind().getName();
        QualName modelName = model.getQualName();
        try {
            model.toResource();
            Assert.fail(kindName + " " + modelName + " has no errors");
        } catch (NullPointerException e) {
            Assert.fail(kindName + " " + modelName + " does not exist");
        } catch (FormatException e) {
            // do nothing; this is the expected case
        }
    }
}
