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
 * $Id: NoLayouter.java,v 1.1.1.1 2007-03-20 10:05:31 kastenberg Exp $
 */
package groove.gui.layout;

import org.jgraph.graph.GraphConstants;

import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;

/**
 * An abstract class for layout actions.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public class NoLayouter implements Layouter {
    static public final String ACTION_NAME = "No layout";

    /**
     * Constructor to create a dummy, prototype layout action.
     * Proper layout actions are created using <tt>newInstance(MyJGraph)</tt>
     * @see #newInstance(JGraph)
     */
    public NoLayouter() {
        // constructor for prototype layouter
        jGraph = null;
    }

    /**
     * Constructor to create a no-layout action for a given j-graph.
     */
    protected NoLayouter(JGraph jGraph) {
        this.jGraph = jGraph;
    }

    /**
     * Returns a new no-layouter
     * @return a new no-layouter
     */
    public Layouter newInstance(JGraph jgraph) {
        return new NoLayouter(jgraph);
    }

    public String getName() {
        return ACTION_NAME;
    }
    
    public String getText() {
        return ACTION_NAME;
    }

    /**
     * This implementation does nothing except unfreezing the cells.
     */
    public void start(boolean complete) {
        JModel jModel = jGraph.getModel();
        for (int i = 0; i < jModel.getRootCount(); i++) {
            JCell jCell = (JCell) jModel.getRootAt(i);
            GraphConstants.setMoveable(jCell.getAttributes(), true);
        }
    }

    /**
     * The current implementation does nothing.
     */
    public void stop() {
        // does nothing
    }
    
    protected final JGraph jGraph;
}
