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
package nl.utwente.groove.grammar.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.io.Groove;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * A rule from a {@code junit/rules} grammar, loaded for direct testing of the
 * rule-compilation data classes (gh #893): the rule model, a compiler for it,
 * and the level index tree of its normalised source graph. Lives in the
 * {@code grammar.model} package because the data classes are package-private.
 * @param model the loaded rule model
 * @param compiler a fresh compiler for the model, providing the compilation context
 * @param tree the level index tree of the model's normalised source graph
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
record LevelFixture(RuleModel model, RuleCompiler compiler, LevelIndexTree tree) {
    /** Loads a named rule of a named {@code junit/rules} grammar. The rule may have errors. */
    static LevelFixture load(String grammarName, String ruleName) {
        GrammarModel grammar;
        try {
            grammar = Groove.loadGrammar("junit/rules/" + grammarName);
        } catch (IOException exc) {
            fail(exc.getMessage());
            throw new IllegalStateException();
        }
        RuleModel model = grammar.getRuleModel(QualName.parse(ruleName));
        RuleCompiler compiler = new RuleCompiler(grammar, model.getSource(),
            model.getNormalSource(), model.getRole());
        LevelIndexTree tree = LevelIndexTree.from(normal(model), compiler.getQualName());
        return new LevelFixture(model, compiler, tree);
    }

    /** Loads a named rule of a named {@code junit/rules} grammar, asserting that it is error-free. */
    static LevelFixture loadValid(String grammarName, String ruleName) {
        LevelFixture result = load(grammarName, ruleName);
        assertFalse("Unexpected errors: " + result.model().getErrors(),
                    result.model().hasErrors());
        return result;
    }

    /** Returns the normalised source graph of a rule model. */
    private static AspectGraph normal(RuleModel model) {
        return model.getNormalSource();
    }

    /** Returns the normalised source graph of the rule. */
    AspectGraph normal() {
        return normal(model());
    }

    /** Builds the level distribution of the rule. */
    LevelDistribution distribution() throws Exception {
        return LevelDistribution.from(compiler(), normal(), tree());
    }

    /** Returns the node of the normalised source graph with a given name ({@code n0}, {@code x3}, ...). */
    AspectNode node(String name) {
        return normal()
            .nodeSet()
            .stream()
            .filter(n -> n.toString().equals(name))
            .findFirst()
            .orElseThrow(() -> new AssertionError("No node " + name + " in " + normal().nodeSet()));
    }

    /** Returns the sorted string forms of a collection of elements, for comparison with literals. */
    static List<String> names(Collection<?> elements) {
        return elements.stream().map(Object::toString).sorted().toList();
    }

    /** Returns the string forms of an ordered collection, in its own order. */
    static List<String> strings(Collection<?> elements) {
        return elements.stream().map(Object::toString).toList();
    }
}
