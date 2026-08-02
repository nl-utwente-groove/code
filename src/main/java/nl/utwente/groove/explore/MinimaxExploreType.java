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
package nl.utwente.groove.explore;

import java.util.ArrayList;
import java.util.List;

import nl.utwente.groove.explore.config.parse.EnabledRuleParser;
import nl.utwente.groove.explore.config.parse.LegacySyntaxParser;
import nl.utwente.groove.explore.encode.Serialized;
import nl.utwente.groove.explore.strategy.MinimaxStrategy;
import nl.utwente.groove.explore.strategy.Strategy;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Exploration type for minimax strategy generation: generates a strategy
 * for a two-player game. The rule names are resolved against the grammar
 * when the strategy is instantiated.
 * @author Arend Rensink
 * @version $Revision$
 */
public class MinimaxExploreType extends DirectExploreType {
    /**
     * Constructs a minimax exploration type.
     * @param heuristicParam index of the rule parameter holding the heuristic value
     * @param maxDepth maximum search depth; {@code 0} means unbounded
     * @param ruleNames names of the rules enabled in the game
     * @param startMax whether the maximising player starts (display-only,
     * as in the legacy template)
     * @param minmaxRule name of the rule alternating the turns
     * @param minmaxParam index of the turn-holding parameter of the minimax rule
     * @param acceptor the acceptor specification
     * @param count number of results after which exploration halts;
     * {@code 0} means unbounded
     */
    public MinimaxExploreType(int heuristicParam, int maxDepth, List<String> ruleNames,
                              String startMax, String minmaxRule, int minmaxParam,
                              LegacySyntaxParser.AcceptorSpec acceptor, int count) {
        super(createStrategyDescriptor(heuristicParam, maxDepth, ruleNames, startMax, minmaxRule,
                                       minmaxParam),
              acceptor, count);
        this.heuristicParam = heuristicParam;
        this.maxDepth = maxDepth;
        this.ruleNames = ruleNames;
        this.minmaxRule = minmaxRule;
        this.minmaxParam = minmaxParam;
    }

    private final int heuristicParam;
    private final int maxDepth;
    private final List<String> ruleNames;
    private final String minmaxRule;
    private final int minmaxParam;

    /** Computes the legacy display descriptor for the minimax arguments. */
    private static Serialized createStrategyDescriptor(int heuristicParam, int maxDepth,
                                                       List<String> ruleNames, String startMax,
                                                       String minmaxRule, int minmaxParam) {
        Serialized result = new Serialized("minimax");
        result.setArgument("heuristic-parameter-index", Integer.toString(heuristicParam));
        result.setArgument("maximum-search-depth", Integer.toString(maxDepth));
        result.setArgument("enabled-rule-names", String.join(";", ruleNames));
        result.setArgument("start-max", startMax);
        result.setArgument("minmax-rule", minmaxRule);
        result.setArgument("minmax-rule-parameter-index", Integer.toString(minmaxParam));
        return result;
    }

    @Override
    public Strategy getParsedStrategy(Grammar grammar) throws FormatException {
        List<Rule> rules = new ArrayList<>();
        for (String name : this.ruleNames) {
            rules.add(EnabledRuleParser.parse(grammar, name));
        }
        Rule evalRule = EnabledRuleParser.parse(grammar, this.minmaxRule);
        return new MinimaxStrategy(this.heuristicParam, this.maxDepth, rules, evalRule,
            this.minmaxParam);
    }
}
