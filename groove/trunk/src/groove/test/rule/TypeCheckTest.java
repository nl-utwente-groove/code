/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.test.rule;

import groove.trans.ResourceKind;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

/** Set of tests for rule application. */
public class TypeCheckTest {
    /** Location of the samples. */
    static public final String INPUT_DIR = "junit/types";

    /** Tests the rules in the creators grammar. */
    @Test
    public void testTypeSpecialisation() {
        setCorrect("mergeSharpTypes");
        setErroneous("createType", "deleteType", "mergeDistinctTypes",
            "mergeNonSharpTypes");
        test("type-specialisation");
    }

    /** Assigns the set of correct rules. */
    private void setCorrect(String... ruleNames) {
        this.correct = new HashSet<String>(Arrays.asList(ruleNames));
    }

    /** Assigns the set of erroneous rules. */
    private void setErroneous(String... ruleNames) {
        this.erroneous = new HashSet<String>(Arrays.asList(ruleNames));
    }

    /** Tests all rules in a named grammar (to be loaded from {@link #INPUT_DIR}). */
    private void test(String grammarName) {
        try {
            GrammarModel grammarView =
                Groove.loadGrammar(INPUT_DIR + "/" + grammarName);
            for (String ruleName : grammarView.getNames(ResourceKind.RULE)) {
                if (this.correct.contains(ruleName)) {
                    testCorrect(grammarView, ruleName);
                } else if (this.erroneous.contains(ruleName)) {
                    testErroneous(grammarView, ruleName);
                } else if (grammarView.getGraphResource(ResourceKind.RULE,
                    ruleName).isEnabled()) {
                    Assert.fail("Rule " + ruleName + " not declared");
                }
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    /** Tests that a given rule has no errors. */
    private void testCorrect(GrammarModel grammarView, String ruleName) {
        try {
            grammarView.getRuleModel(ruleName).toResource();
        } catch (NullPointerException e) {
            Assert.fail("Rule " + ruleName + " does not exist");
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
        }
    }

    /** Tests that a given rule has errors. */
    private void testErroneous(GrammarModel grammarView, String ruleName) {
        try {
            grammarView.getRuleModel(ruleName).toResource();
            Assert.fail("Rule " + ruleName + " has no errors");
        } catch (NullPointerException e) {
            Assert.fail("Rule " + ruleName + " does not exist");
        } catch (FormatException e) {
            // do nothing; this is the expected case
        }
    }

    private Set<String> correct;
    private Set<String> erroneous;
}
