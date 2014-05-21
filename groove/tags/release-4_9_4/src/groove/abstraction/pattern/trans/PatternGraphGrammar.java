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
package groove.abstraction.pattern.trans;

import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.control.CtrlAut;
import groove.control.instance.Automaton;
import groove.grammar.Grammar;
import groove.grammar.Rule;

import java.util.Map;
import java.util.TreeMap;

/**
 * Default model of a pattern graph grammar, consisting of a production
 * rule system and a default start graph.
 *
 * @author Eduardo Zambon
 */
public final class PatternGraphGrammar {

    /** Simple graph grammar used to construct this pattern graph grammar. */
    private final Grammar sGrammar;
    /** Pattern type graph. */
    private final TypeGraph typeGraph;
    /** The name of this grammar. */
    private final String name;
    /** A mapping from rule names to the available pattern graph rules. */
    private final Map<String,PatternRule> nameRuleMap = new TreeMap<String,PatternRule>();
    /** The start graph of this graph grammar. */
    private PatternGraph startGraph;

    /** Default constructor. */
    public PatternGraphGrammar(Grammar sGrammar, TypeGraph typeGraph) {
        assert sGrammar.isFixed();
        this.sGrammar = sGrammar;
        this.typeGraph = typeGraph;
        this.name = sGrammar.getName();
        liftStartGraph();
        liftRules();
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Rule system:\n");
        for (PatternRule pRule : this.nameRuleMap.values()) {
            res.append(pRule + "\n");
        }
        res.append("\nStart graph:\n");
        res.append(this.startGraph.toString());
        return res.toString();
    }

    private void liftStartGraph() {
        this.startGraph = this.typeGraph.lift(this.sGrammar.getStartGraph());
    }

    private void liftRules() {
        for (Rule sRule : this.sGrammar.getAllRules()) {
            PatternRule pRule = this.typeGraph.lift(sRule);
            this.nameRuleMap.put(sRule.getLastName(), pRule);
        }
    }

    /**
     * Returns the name of this rule system. May be <tt>null</tt> if the rule
     * system is anonymous.
     */
    public String getName() {
        return this.name;
    }

    /** Convenience method to return the rule with a given name, if any. */
    public PatternRule getRule(String name) {
        return this.nameRuleMap.get(name);
    }

    /** Basic getter. */
    public PatternGraph getStartGraph() {
        return this.startGraph;
    }

    /** Basic getter. */
    public CtrlAut getCtrlAut() {
        return this.sGrammar.getCtrlAut();
    }

    /** Basic getter. */
    public Automaton getControl() {
        return this.sGrammar.getControl();
    }

    /** Returns the simple grammar from which this pattern grammar was created.*/
    public Grammar getSimpleGrammar() {
        return this.sGrammar;
    }

}
