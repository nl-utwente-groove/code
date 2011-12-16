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
package groove.abstraction.neigh.explore.util;

import groove.abstraction.neigh.Abstraction;
import groove.abstraction.neigh.Parameters;
import groove.abstraction.neigh.io.xml.ShapeGxl;
import groove.abstraction.neigh.match.PreMatch;
import groove.abstraction.neigh.shape.Shape;
import groove.abstraction.neigh.shape.iso.ShapeIsoChecker;
import groove.abstraction.neigh.trans.Materialisation;
import groove.trans.GraphGrammar;
import groove.trans.Proof;
import groove.trans.Rule;
import groove.trans.RuleEvent;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Class for storing and running traces in the abstract state space exploration.
 * 
 * @author Eduardo Zambon
 */
public final class Trace {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Full path to the grammar. */
    private final String grammarName;
    /** The loaded grammar. */
    private final GraphGrammar grammar;
    /** File names of the states in the trace. */
    private final String stateFiles[];
    /** Rule names of the applications in the trace. */
    private final String ruleNames[];
    /** Loaded shapes from the files. */
    private final Shape states[];
    /** Rule objects. */
    private final Rule rules[];
    /** Current state being executed. */
    private int currState;
    /** Current rule being executed. s*/
    private int currRule;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Basic constructor. */
    public Trace(String grammarName, GraphGrammar grammar, String stateFiles[],
            String ruleNames[]) {
        assert ruleNames.length == stateFiles.length - 1;
        this.grammarName = grammarName;
        this.grammar = grammar;
        this.stateFiles = stateFiles;
        this.ruleNames = ruleNames;
        this.states = loadStates();
        this.rules = loadRules();
        this.currRule = 0;
        this.currState = 0;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Loads and returns the states in the given names array. */
    private Shape[] loadStates() {
        ShapeGxl marshaller = ShapeGxl.getInstance(this.grammar.getTypeGraph());
        Shape result[] = new Shape[this.stateFiles.length];
        int i = 0;
        for (String stateFile : this.stateFiles) {
            File file = new File(this.grammarName + "/" + stateFile + ".gxl");
            result[i] = marshaller.loadShape(file);
            i++;
        }
        return result;
    }

    /** Loads and returns the rules in the given names array. */
    private Rule[] loadRules() {
        Rule result[] = new Rule[this.ruleNames.length];
        int i = 0;
        for (String ruleName : this.ruleNames) {
            result[i] = this.grammar.getRule(ruleName);
            i++;
        }
        return result;
    }

    /** Returns the current source shape in the trace. */
    public Shape getSourceShape() {
        return this.states[this.currState];
    }

    /** Returns true if the given shape is current source shape in the trace. */
    public boolean isSourceShape(Shape shape) {
        return ShapeIsoChecker.areExactlyEqual(shape, getSourceShape());
    }

    /** Returns the current target shape in the trace. */
    public Shape getTargetShape() {
        return this.states[this.currState + 1];
    }

    /** Returns true if the given shape is current target shape in the trace. */
    public boolean isTargetShape(Shape shape) {
        return ShapeIsoChecker.areExactlyEqual(shape, getTargetShape());
    }

    /** Returns the current rule in the trace. */
    public Rule getCurrentRule() {
        return this.currRule < this.rules.length ? this.rules[this.currRule]
                : null;
    }

    /** Returns true if the given rule is current rule in the trace. */
    public boolean isCurrentRule(RuleEvent event) {
        return event.getRule().equals(getCurrentRule());
    }

    /** Increase the counters to next state in the trace. */
    public void advanceCurrentState() {
        this.currState++;
        this.currRule++;
    }

    /** Prints the current transition to stdout. */
    public void printTransition() {
        System.out.println(this.stateFiles[this.currState] + "--"
            + this.rules[this.currRule].getName() + "-->"
            + this.stateFiles[this.currState + 1]);
    }

    /** Executes the trace. */
    public void run() {
        while (this.currRule < this.rules.length) {
            Shape srcShape = getSourceShape();
            Shape tgtShape = getTargetShape();
            Rule rule = getCurrentRule();

            boolean foundTransition = false;
            Set<Proof> preMatches = PreMatch.getPreMatches(srcShape, rule);
            matchLoop: for (Proof preMatch : preMatches) {
                Set<Materialisation> mats =
                    Materialisation.getMaterialisations(srcShape, preMatch);
                for (Materialisation mat : mats) {
                    Shape transformedShape = mat.applyMatch(null).one();
                    Shape normalisedShape = transformedShape.normalise();
                    if (ShapeIsoChecker.areExactlyEqual(normalisedShape,
                        tgtShape)) {
                        foundTransition = true;
                        break matchLoop;
                    }
                }
            }

            if (foundTransition) {
                printTransition();
            } else {
                assert false;
            }

            advanceCurrentState();
        }
    }

    /** Initialises the abstraction engine. */
    private static void init() {
        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Abstraction.initialise();
    }

    /** Loads and returns the grammar. */
    private static GraphGrammar loadGrammar() {
        File grammarFile = new File(GRAMMAR_NAME);
        GrammarModel view;
        GraphGrammar grammar = null;
        try {
            view = GrammarModel.newInstance(grammarFile, false);
            grammar = view.toGrammar();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return grammar;
    }

    // ------------------------------------------------------------------------
    // Test methods
    // ------------------------------------------------------------------------

    private static final String GRAMMAR_NAME =
        "junit/abstraction/euler-counting.gps";

    private static final String[] STATE_FILES = {"dfs0", "dfs1", "dfs2",
        "dfs3", "dfs4", "dfs5", "dfs6", "dfs7", "dfs8", "dfs9"};

    private static final String[] RULE_NAMES = {"toNewArea0", "toOldArea11",
        "toOldArea00", "toNewArea1", "toOldArea11", "toOldArea00",
        "toOldArea10", "toOldArea10", "toOldArea10"};

    /** Test method. */
    public static void main(String[] args) {
        init();
        GraphGrammar grammar = loadGrammar();
        Trace trace = new Trace(GRAMMAR_NAME, grammar, STATE_FILES, RULE_NAMES);
        trace.run();
    }

}
