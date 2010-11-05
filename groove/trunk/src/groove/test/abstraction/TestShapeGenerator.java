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
package groove.test.abstraction;

import groove.abstraction.Parameters;
import groove.abstraction.ShapeGenerator;
import junit.framework.TestCase;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestShapeGenerator extends TestCase {

    public void testShapeGenerator0() {
        final String GRAMMAR = "junit/samples/abs-single-link-list.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setEdgeMultBound(1);
        Parameters.setAbsRadius(1);
        // EDUARDO: FIX THIS RESULTS!!!
        Parameters.setNodeMultBound(1);
        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, false);
        assertTrue(generator.getStateCount() == 6
            && generator.getTransitionCount() == 12);

        Parameters.setNodeMultBound(2);
        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, false);
        assertTrue(generator.getStateCount() == 7
            && generator.getTransitionCount() == 14);

        Parameters.setNodeMultBound(3);
        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, false);
        assertTrue(generator.getStateCount() == 8
            && generator.getTransitionCount() == 16);

    }

}
