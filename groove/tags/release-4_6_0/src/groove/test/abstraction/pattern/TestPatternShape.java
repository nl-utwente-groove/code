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
import groove.abstraction.pattern.io.xml.TypeGraphJaxbGxlIO;
import groove.abstraction.pattern.shape.PatternShape;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.trans.HostGraph;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestPatternShape {

    @Test
    public void testNormalisation0() {
        final String GRAMMAR = "junit/pattern/pattern-list.gps/";
        final String TYPE = "ptgraph.gxl";

        TypeGraph typeGraph = loadTypeGraph(GRAMMAR + TYPE);
        HostGraph sGraph = loadSimpleGraph(GRAMMAR, "start-5");
        PatternShape pShape = new PatternShape(typeGraph.lift(sGraph));
        PatternShape canShape = pShape.normalise();

        assertEquals(10, canShape.nodeCount());
        assertEquals(12, canShape.edgeCount());
    }

    @Test
    public void testNormalisation1() {
        final String GRAMMAR = "junit/pattern/equiv.gps/";
        final String TYPE = "ptgraph.gxl";

        TypeGraph typeGraph = loadTypeGraph(GRAMMAR + TYPE);
        HostGraph sGraph = loadSimpleGraph(GRAMMAR, "start");
        PatternShape pShape = new PatternShape(typeGraph.lift(sGraph));
        PatternShape canShape = pShape.normalise();

        assertEquals(9, canShape.nodeCount());
        assertEquals(10, canShape.edgeCount());
    }

    private TypeGraph loadTypeGraph(String typeGraphFileName) {
        File typeGraphFile = new File(typeGraphFileName);
        TypeGraph typeGraph = null;
        try {
            typeGraph =
                TypeGraphJaxbGxlIO.getInstance().unmarshalTypeGraph(
                    typeGraphFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return typeGraph;
    }

    private HostGraph loadSimpleGraph(String grammarName, String hostGraphName) {
        File grammarFile = new File(grammarName);
        HostGraph sGraph = null;
        try {
            GrammarModel view = GrammarModel.newInstance(grammarFile, false);
            sGraph = view.getHostModel(hostGraphName).toResource();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return sGraph;
    }

}
