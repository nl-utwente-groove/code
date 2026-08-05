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
package nl.utwente.groove.test.control;

import org.junit.Test;

/**
 * Tests the interplay of rule priorities and explicit control calls (gh #756):
 * explicitly calling a prioritised action is an error only if the grammar
 * actually has more than one distinct action priority.
 * @author Arend Rensink
 * @version $Revision$
 */
public class PriorityCallTest extends CtrlTester {
    /** In a grammar with distinct priorities (0, 5, 10), explicit calls of
     * prioritised actions are not allowed. */
    @Test
    public void testMixedPriorities() {
        initGrammar("emptypriorules");
        buildWrong("c2;");
        buildWrong("m3;");
        // also within a procedure body
        buildWrong("recipe r() { m2; } r;");
        // an explicitly called recipe with positive priority is equally wrong
        buildWrong("recipe r() priority 3 { m1; } r;");
        // priority-0 actions may be called explicitly
        buildFragment("m1;");
        // group calls respect priorities and are always allowed
        buildFragment("any;");
    }

    /** In a grammar where all transformers have the same positive priority,
     * the priorities are vacuous, so explicit calls are allowed. */
    @Test
    public void testUniformPriorities() {
        initGrammar("samepriorules");
        buildFragment("a; b;");
        buildFragment("any;");
        // a recipe at the same priority keeps the priorities vacuous
        buildFragment("recipe r() priority 5 { a; } r;");
        // a recipe at a different priority makes them effective again
        buildWrong("recipe r() priority 3 { a; } r;");
    }
}
