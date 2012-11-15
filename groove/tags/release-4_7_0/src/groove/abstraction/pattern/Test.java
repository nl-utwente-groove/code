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
import groove.abstraction.pattern.io.xml.PatternShapeGxl;
import groove.abstraction.pattern.io.xml.TypeGraphJaxbGxlIO;
import groove.abstraction.pattern.lts.MatchResult;
import groove.abstraction.pattern.match.Matcher;
import groove.abstraction.pattern.match.MatcherFactory;
import groove.abstraction.pattern.match.PreMatch;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.abstraction.pattern.trans.Materialisation;
import groove.abstraction.pattern.trans.PatternRule;
import groove.trans.Rule;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author Eduardo Zambon
 */
public class Test {

    private static final String PATH =
        "/home/zambon/Work/workspace_groove/groove/junit/pattern/";

    private static final String GRAMMAR = PATH + "euler-0.gps/";

    private static final String TYPE_GRAPH = GRAMMAR + "ptgraph-0.gxl";

    private static final String RULE = "toOldArea00";

    /** Test method. */
    public static void main(String args[]) {
        TypeGraph pTGraph = null;
        Rule sRule = null;
        try {
            GrammarModel view =
                GrammarModel.newInstance(new File(GRAMMAR), false);
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
        PatternAbsParam.getInstance().setNodeMultBound(1);
        PatternAbsParam.getInstance().setEdgeMultBound(1);

        PatternShapeGxl gxl = new PatternShapeGxl(pTGraph);
        File outFile = new File(GRAMMAR + "error.gxl");
        PatternShape pShape = gxl.loadPatternShape(outFile);
        pShape.setFixed();

        PatternPreviewDialog.showPatternGraph(pShape);

        PatternRule pRule = pTGraph.lift(sRule);
        Matcher matcher = MatcherFactory.instance().getMatcher(pRule, false);
        List<MatchResult> matches = matcher.findMatches(pShape, null);
        PreMatch preMatch = (PreMatch) matches.get(0).getMatch();

        Collection<Materialisation> mats =
            Materialisation.getMaterialisations(pShape, preMatch);
        for (Materialisation mat : mats) {
            PatternPreviewDialog.showPatternGraph(mat.getShape());
            System.out.print(mat.getShape().nodeCount() + " ");
            System.out.println(mat.getShape().edgeCount());
        }
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
