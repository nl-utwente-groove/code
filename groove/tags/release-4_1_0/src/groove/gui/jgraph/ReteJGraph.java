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
package groove.gui.jgraph;

/**
 * @author Arash Jalali
 * @version $Revision $
 */
public class ReteJGraph extends JGraph {

    /**
     * A graph representing the looks of the static structure of a RETE network.
     * @param model the associated model that represents the structure of the graph.
     * @param hasFilters indicates if this JGraph is to use label filtering.
     */
    public ReteJGraph(ReteJModel model, boolean hasFilters) {
        super(model, hasFilters);
    }

    //TODO ARASH: we can add a new tab to the groove GUI to display the constructed RETE 
    //network of a given grammar more appropriately.

}
