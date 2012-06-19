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
 * $Id: GraphJCell.java,v 1.3 2007-09-30 21:45:10 rensink Exp $
 */
package groove.gui.jgraph;

import groove.graph.Edge;
import groove.graph.Label;
import groove.view.LabelParser;

/**
 * Extension of {@link JCell} that recognises that cells have underlying edges.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
interface GraphJCell extends JCell {
    /** 
     * Callback method from {@link #getLines()} to obtain the (html-formatted)
     * text to be displayed for a given edge.
     */
    StringBuilder getLine(Edge edge);
    
    /** 
     * Returns the label of the edge as to be displayed in the label list.
     * Callback method from {@link #getListLabels()}.
     */
    String getListLabel(Edge edge);

    /** 
     * Retrieves an edge label. 
     * Callback method from {@link #getLine(Edge)}, {@link #getPlainLabel(Edge)} and {@link #getListLabel(Edge)}.
     */
    Label getLabel(Edge edge);
    
    /** 
     * Returns the label of the edge as to be displayed in an edit view.
     * Callback method from {@link #getPlainLabels()}.
     */
    String getPlainLabel(Edge edge);
    
    /** 
     * Returns a label parser for this jnode.
     * Callback method from {@link #getPlainLabel(Edge)}.
     */
    LabelParser getLabelParser();
    
    /**
     * Specialises the return type.
     */
    public EdgeContent getUserObject();
}