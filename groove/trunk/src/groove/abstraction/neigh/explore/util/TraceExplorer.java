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
import groove.graph.TypeGraph;
import groove.trans.GraphGrammar;
import groove.trans.Proof;
import groove.trans.Rule;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Executes a trace using abstraction.
 * 
 * @author Eduardo Zambon
 */
public final class TraceExplorer {

    private static final String grammarName =
        "junit/abstraction/euler-counting.gps";

    private static final String[] stateFiles = {"dfs0", "dfs1", "dfs2", "dfs3",
        "dfs4", "dfs5", "dfs6", "dfs7", "dfs8", "dfs9"};
    private static final String[] ruleNames = {"toNewArea0", "toOldArea11",
        "toOldArea00", "toNewArea1", "toOldArea11", "toOldArea00",
        "toOldArea10", "toOldArea10", "toOldArea10"};

    private static void init() {
        Parameters.setNodeMultBound(1);
        Parameters.setEdgeMultBound(1);
        Abstraction.initialise();
    }

    private static GraphGrammar loadGrammar() {
        File grammarFile = new File(grammarName);
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

    private static Shape[] loadStates(TypeGraph typeGraph) {
        ShapeGxl marshaller = ShapeGxl.getInstance(typeGraph);
        Shape result[] = new Shape[stateFiles.length];
        int i = 0;
        for (String stateFile : stateFiles) {
            File file = new File(grammarName + "/" + stateFile + ".gxl");
            result[i] = marshaller.loadShape(file);
            i++;
        }
        return result;
    }

    private static Rule[] loadRules(GraphGrammar grammar) {
        Rule result[] = new Rule[ruleNames.length];
        int i = 0;
        for (String ruleName : ruleNames) {
            result[i] = grammar.getRule(ruleName);
            i++;
        }
        return result;
    }

    /** Test method. */
    public static void main(String[] args) {
        init();
        GraphGrammar grammar = loadGrammar();
        Shape states[] = loadStates(grammar.getTypeGraph());
        Rule rules[] = loadRules(grammar);

        assert rules.length == states.length - 1;

        int currState = 0;
        int currRule = 0;

        while (currRule < rules.length) {
            Shape srcShape = states[currState];
            Shape tgtShape = states[currState + 1];
            Rule rule = rules[currRule];

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
                System.out.println(stateFiles[currState] + "--"
                    + rule.getName() + "-->" + stateFiles[currState + 1]);
            } else {
                assert false;
            }

            currState++;
            currRule++;
        }

        assert currState == states.length - 1;
    }

}
