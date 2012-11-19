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
import groove.abstraction.pattern.io.xml.TypeGraphGxl;
import groove.abstraction.pattern.shape.TypeGraph;

import java.io.File;
import java.io.IOException;

/**
 * @author Eduardo Zambon
 */
public class Test {

    private static final String PATH =
        "/home/zambon/Work/workspace_groove/groove/junit/pattern/";

    private static final String GRAMMAR = PATH + "pattern-list.gps/";

    private static final String TYPE_GRAPH = GRAMMAR + "ptgraph.gst";

    /** Test method. */
    public static void main(String args[]) {
        TypeGraph pTGraph = null;
        try {
            pTGraph =
                TypeGraphGxl.getInstance().unmarshalTypeGraph(
                    new File(TYPE_GRAPH));
        } catch (IOException e) {
            e.printStackTrace();
        }

        PatternPreviewDialog.showPatternGraph(pTGraph);

    }
}
