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
import groove.abstraction.pattern.io.xml.TypeGraphJaxbGxlIO;
import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.match.Matcher;
import groove.abstraction.pattern.match.MatcherFactory;
import groove.abstraction.pattern.match.PreMatch;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.abstraction.pattern.trans.Materialisation;
import groove.abstraction.pattern.trans.PatternRule;
import groove.trans.HostGraph;
import groove.trans.Rule;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Eduardo Zambon
 */
public class TestMat {

    private static final String PATH =
        "/home/zambon/Work/workspace_groove/groove/junit/pattern/";

    private static final String GRAMMAR = PATH + "mat-test.gps/";

    private static final int TEST_NUM = 3;

    private static final String TYPE_GRAPH = GRAMMAR + "ptgraph-" + TEST_NUM
        + ".gxl";

    private static final String HOST = "host-" + TEST_NUM;

    private static final String RULE = "rule-" + TEST_NUM;

    /** Test method. */
    public static void main(String args[]) {
        TypeGraph pTGraph = null;
        HostGraph sHost = null;
        Rule sRule = null;
        try {
            GrammarModel view =
                GrammarModel.newInstance(new File(GRAMMAR), false);
            sHost = view.getHostModel(HOST).toResource();
            sRule = view.getRuleModel(RULE).toResource();

            pTGraph =
                TypeGraphJaxbGxlIO.getInstance().unmarshalTypeGraph(
                    new File(TYPE_GRAPH));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        PatternAbstraction.initialise();
        //PatternAbsParam.getInstance().setUseThreeValues(true);

        PatternGraph pHost = pTGraph.lift(sHost);
        PatternRule pRule = pTGraph.lift(sRule);

        /*PatternPreviewDialog.showPatternGraph(pTGraph);
        PatternPreviewDialog.showPatternGraph(pHost);
        PatternPreviewDialog.showPatternGraph(pRule.lhs());
        PatternPreviewDialog.showPatternGraph(pRule.rhs());*/

        PatternShape pShape = new PatternShape(pHost).normalise();
        pShape.setFixed();

        PatternPreviewDialog.showPatternGraph(pShape);

        Matcher matcher = MatcherFactory.instance().getMatcher(pRule, false);
        Match preMatch = matcher.findMatches(pShape, null).get(0).getMatch();
        Collection<Materialisation> mats =
            Materialisation.getMaterialisations(pShape, (PreMatch) preMatch);
        for (Materialisation mat : mats) {
            PatternPreviewDialog.showPatternGraph(mat.getShape());
            System.out.print(mat.getShape().nodeCount() + " ");
            System.out.println(mat.getShape().edgeCount());
        }
        /*for (Match preMatch : matcher.findMatches(pShape)) {
            Collection<Materialisation> mats =
                Materialisation.getMaterialisations(pShape, (PreMatch) preMatch);
            for (Materialisation mat : mats) {
                PatternPreviewDialog.showPatternGraph(mat.getShape());
                System.out.print(mat.getShape().nodeCount() + " ");
                System.out.println(mat.getShape().edgeCount());
            }
        }*/
    }
}

/*PatternShapeGxl gxl = new PatternShapeGxl(pTGraph);
File outFile = new File(GRAMMAR + "error.gxl");
PatternShape pShape = gxl.loadPatternShape(outFile);
System.out.println(pShape);
PatternPreviewDialog.showPatternGraph(pShape);*/

/*if (newState.getNumber() == 16) {
PatternShape pShape = newState.getShape();
PatternShapeGxl gxl = new PatternShapeGxl(pShape.getTypeGraph());
File outFile = new File("error.gxl");
gxl.saveShape(pShape, outFile);
}*/
