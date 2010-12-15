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

import static org.junit.Assert.assertEquals;
import groove.abstraction.Parameters;
import groove.abstraction.ShapeGenerator;

import org.junit.Test;

/**
 * Extra test for the shape generator. It should not be included in the
 * 'AllAbstractionTests' suite because it takes too long to run...
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class ExtraShapeGeneratorTest {

    @Test
    public void testShapeGenerator() {
        final String GRAMMAR = "junit/samples/abs-circ-buf-1.gps";
        final String START_GRAPH = "start";
        ShapeGenerator generator;

        Parameters.setEdgeMultBound(1);
        Parameters.setAbsRadius(1);
        Parameters.setNodeMultBound(1);

        generator = new ShapeGenerator();
        generator.generate(GRAMMAR, START_GRAPH, false);
        assertEquals(37, generator.getStateCount());
        assertEquals(98, generator.getTransitionCount());
    }

}
