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
package groove.test.graph;

import static groove.io.FileType.STATE;
import groove.graph.iso.IsoChecker;
import groove.graph.plain.PlainGraph;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/** Tests the isomorphism checker. */
public class IsoTest {
    /** Location of the samples. */
    static private final String INPUT_DIR = "junit/graphs/iso";
    static private final IsoChecker checker = IsoChecker.getInstance(true);

    private Map<String,List<PlainGraph>> graphMap;

    /** Setup method loading all comparable graphs. */
    @Before
    public void setUp() {
        this.graphMap = new HashMap<>();
        for (File stateFile : new File(INPUT_DIR).listFiles(STATE.getFilter())) {
            if (stateFile.isDirectory()) {
                continue;
            }
            String name = STATE.stripExtension(stateFile.getName());
            try {
                addGraph(name, Groove.loadGraph(stateFile));
            } catch (IOException e) {
                System.err.printf("Error loading %s: %s%n", stateFile,
                    e.getMessage());
            }
        }
    }

    /** Adds a loaded graph to the graph map. */
    private void addGraph(String name, PlainGraph graph) {
        // get the number suffix
        int hyphenPos = name.lastIndexOf('-');
        if (hyphenPos < 0) {
            System.err.printf("Graph name %s should end on suffix '-suffix'%n",
                name);
        } else {
            String actualName = name.substring(0, hyphenPos);
            List<PlainGraph> record = this.graphMap.get(actualName);
            if (record == null) {
                this.graphMap.put(actualName, record =
                    new LinkedList<>());
            }
            record.add(graph);
        }
    }

    /** Tests Euler's walk. */
    @Test
    public void testBridges() {
        test("bridges", true);
    }

    /** Tests all rules in a named grammar (to be loaded from {@link #INPUT_DIR}). */
    private void test(String graphName, boolean iso) {
        List<PlainGraph> graphs = this.graphMap.get(graphName);
        Assert.assertNotNull(graphs);
        for (int i = 0; i < graphs.size(); i++) {
            for (int j = i + 1; j < graphs.size(); j++) {
                Assert.assertEquals(iso,
                    checker.areIsomorphic(graphs.get(i), graphs.get(j)));
            }
        }
    }
}
