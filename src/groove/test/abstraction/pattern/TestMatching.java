/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.test.abstraction.pattern;

import static org.junit.Assert.assertEquals;
import groove.abstraction.pattern.PatternAbstraction;
import groove.abstraction.pattern.io.xml.TypeGraphGxl;
import groove.abstraction.pattern.lts.MatchResult;
import groove.abstraction.pattern.match.Matcher;
import groove.abstraction.pattern.match.MatcherFactory;
import groove.abstraction.pattern.shape.PatternGraph;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.abstraction.pattern.trans.PatternRule;
import groove.grammar.Rule;
import groove.grammar.host.HostGraph;
import groove.grammar.model.FormatException;
import groove.grammar.model.GrammarModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestMatching {

    static private final String GRAMMAR = "junit/pattern/match-test.gps/";
    static private final String TYPE = "ptgraph.gst";
    static private GrammarModel view;
    static private TypeGraph typeGraph;

    @BeforeClass
    public static void setUp() {
        PatternAbstraction.initialise();
        File grammarFile = new File(GRAMMAR);
        File typeGraphFile = new File(GRAMMAR + TYPE);
        try {
            view = GrammarModel.newInstance(grammarFile, false);
            typeGraph =
                TypeGraphGxl.getInstance().unmarshalTypeGraph(typeGraphFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPreMatch0() {
        PatternShape pShape = getNormalisedShape("start-5");
        PatternRule pRule = getPatternRule("del");
        Matcher matcher = MatcherFactory.instance().getMatcher(pRule, false);
        List<MatchResult> matches = matcher.findMatches(pShape, null);
        assertEquals(1, matches.size());
    }

    private PatternShape getNormalisedShape(String name) {
        HostGraph sGraph = getSimpleGraph(name);
        PatternGraph pGraph = typeGraph.lift(sGraph);
        return new PatternShape(pGraph).normalise();
    }

    private PatternRule getPatternRule(String name) {
        return typeGraph.lift(getSimpleRule(name));
    }

    private HostGraph getSimpleGraph(String name) {
        HostGraph result = null;
        try {
            result = view.getHostModel(name).toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Rule getSimpleRule(String name) {
        Rule result = null;
        try {
            result = view.getRuleModel(name).toResource();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return result;
    }

}
