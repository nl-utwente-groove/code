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
package groove.abstraction.neigh.explore;

import groove.abstraction.neigh.lts.AGTS;
import groove.grammar.Grammar;
import groove.grammar.Rule;
import groove.grammar.host.ValueNode;
import groove.grammar.rule.OperatorNode;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.VariableNode;
import groove.graph.Node;
import groove.lts.GTS;
import groove.transform.Transformer;
import groove.util.parse.FormatException;

import java.io.File;
import java.io.IOException;

/**
 * Transformer that generates an {@link AGTS} rather than a {@link GTS}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ShapeTransformer extends Transformer {
    /**
     * Constructs a shape transformer based on the grammar found at a given
     * location.
     * A second parameter determines whether only a reachability check is performed.
     */
    public ShapeTransformer(File grammarLocation, boolean reachability)
        throws IOException {
        super(grammarLocation);
        this.reachability = reachability;
    }

    @Override
    protected GTS createGTS(Grammar grammar) throws FormatException {
        checkGrammarForAbstraction(grammar);
        return new AGTS(grammar, isReachability());
    }

    @Override
    public AGTS getGTS() {
        return (AGTS) super.getGTS();
    }

    private boolean isReachability() {
        return this.reachability;
    }

    /** Tests whether a given graph grammar is suitable for shape generation. */
    private void checkGrammarForAbstraction(Grammar grammar)
        throws FormatException {
        if (!grammar.getProperties().isInjective()) {
            throw new FormatException(
                "Grammar %s is not injective! Abstraction can only work with injective rules...",
                grammar.getName());
        }
        for (Node node : grammar.getStartGraph().nodeSet()) {
            if (node instanceof ValueNode) {
                throw new FormatException(
                    "Grammar start graph has attributes! Abstraction cannot handle attributes...",
                    grammar.getName());
            }
        }
        for (Rule rule : grammar.getAllRules()) {
            for (RuleNode node : rule.lhs().nodeSet()) {
                if (node instanceof OperatorNode
                    || node instanceof VariableNode) {
                    throw new FormatException(
                        "Grammar rule %s operates on attributes! Abstraction cannot handle attributes...",
                        rule.getFullName());
                }
            }
            for (RuleEdge edge : rule.lhs().edgeSet()) {
                if (!edge.label().isAtom()) {
                    throw new FormatException(
                        "Grammar rule %s has regular expression %s that the abstraction cannot handle!",
                        rule.getFullName(), edge.label());
                }
            }
        }
    }

    private final boolean reachability;
}
