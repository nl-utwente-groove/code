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

import groove.abstraction.pattern.explore.PatternGraphGenerator;
import groove.abstraction.pattern.lts.PGTS;

/**
 * @author Eduardo Zambon
 */
public class Test {

    /*private static final String PATH =
        "/home/zambon/Work/workspace_groove/groove/junit/pattern/";*/

    // private static final String GRAMMAR = PATH + "pattern-list.gps/";
    // private static final String GRAMMAR = PATH + "circ-list-4.gps/";
    // private static final String GRAMMAR = PATH + "trains.gps/";
    // private static final String GRAMMAR = PATH + "equiv.gps/";
    // private static final String GRAMMAR = PATH + "match-test.gps/";

    // private static final String TYPE_GRAPH = GRAMMAR + "ptgraph.gst";

    /** Test method. */
    public static void main(String args[]) {
        final String GRAMMAR = "junit/pattern/trains";
        final String START_GRAPH = "start";
        String typeGraph = "ptgraph.gst";
        String myargs[] = new String[] {GRAMMAR, START_GRAPH, typeGraph};
        PatternGraphGenerator generator = new PatternGraphGenerator(myargs);
        generator.processArguments();
        generator.explore();
        PGTS pgts = generator.getPGTS();
    }
}
