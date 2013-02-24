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
package groove.abstraction.pattern;

import groove.abstraction.pattern.gui.dialog.PatternPreviewDialog;
import groove.abstraction.pattern.lts.MatchResult;
import groove.abstraction.pattern.match.Matcher;
import groove.abstraction.pattern.match.MatcherFactory;
import groove.abstraction.pattern.match.PreMatch;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.abstraction.pattern.shape.TypeGraphFactory;
import groove.abstraction.pattern.trans.Materialisation;
import groove.abstraction.pattern.trans.PatternRule;
import groove.grammar.Rule;
import groove.grammar.host.HostGraph;
import groove.grammar.model.FormatException;
import groove.grammar.model.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Eduardo Zambon
 */
public class Test {

    private static final String PATH =
        "/home/zambon/Work/workspace_groove/groove/junit/pattern/";

    // private static final String GRAMMAR = PATH + "pattern-list.gps/";
    // private static final String GRAMMAR = PATH + "circ-list-4.gps/";
    // private static final String GRAMMAR = PATH + "trains.gps/";
    // private static final String GRAMMAR = PATH + "equiv.gps/";
    // private static final String GRAMMAR = PATH + "match-test.gps/";
    private static final String GRAMMAR = PATH + "mat-test.gps/";

    private static final int nr = 2;

    private static final String TYPE_GRAPH = GRAMMAR + "ptgraph-" + nr + ".gst";

    private static final String RULE = "rule-" + nr;

    private static final String HOST = "host-" + nr;

    /** Test method. */
    public static void main(String args[]) {
        TypeGraph pTGraph = null;
        Rule sRule = null;
        HostGraph sGraph = null;
        try {
            GrammarModel view =
                GrammarModel.newInstance(new File(GRAMMAR), false);
            sRule = view.getRuleModel(RULE).toResource();
            sGraph = view.getHostModel(HOST).toResource();
            pTGraph = TypeGraphFactory.unmarshalTypeGraph(new File(TYPE_GRAPH));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        PatternAbstraction.initialise();
        PatternAbsParam.getInstance().setNodeMultBound(1);
        PatternAbsParam.getInstance().setEdgeMultBound(1);

        PatternGraph pGraph = pTGraph.lift(sGraph);
        PatternShape pShape = new PatternShape(pGraph).normalise();
        pShape.setFixed();

        PatternPreviewDialog.showPatternGraph(pShape);

        PatternRule pRule = pTGraph.lift(sRule);
        Matcher matcher = MatcherFactory.instance().getMatcher(pRule, false);
        List<MatchResult> matches = matcher.findMatches(pShape, null);
        PreMatch preMatch = (PreMatch) matches.get(0).getMatch();

        for (PatternShape mat : Materialisation.getMaterialisations(pShape,
            preMatch)) {
            PatternPreviewDialog.showPatternGraph(mat);
        }
    }
}
