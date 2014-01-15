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
package groove.control;

import groove.graph.NodeSetEdgeSetGraph;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class Instance extends NodeSetEdgeSetGraph<Frame,Step> {
    /**
     * Instantiates a given template.
     */
    public Instance(Template template) {
        super(template.getName());
    }

    /** Returns the next available frame number. */
    int getNextFrameNr() {
        return nodeCount();
    }
}
