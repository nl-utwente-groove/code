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
package groove.sts;

import groove.graph.algebra.VariableNode;
import groove.trans.HostGraph;
import groove.trans.RuleEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Symbolic Transition System for on-the-fly testing. This STS does not keep
 * track of location variables and therefore does not create updates for switch
 * relations.
 * @author Vincent de Bruijn
 * @version $Revision $
 */
public class OnTheFlySTS extends STS {

    /**
     * Constructor.
     */
    public OnTheFlySTS() {
        initialize();
    }

    @Override
    public Location hostGraphToStartLocation(HostGraph graph) {
        Location location = hostGraphToLocation(graph);
        setStartLocation(location);
        return location;
    }

    @Override
    public Set<LocationVariable> getLocationVariables() {
        return new HashSet<LocationVariable>();
    }

    @Override
    protected void createLocationVariables(RuleEvent event,
            HostGraph sourceGraph, Map<VariableNode,LocationVariable> lVarMap) {
        // No Location variables created in OnTheFlySTS.
    }

    @Override
    protected String createUpdate(RuleEvent event,
            Map<VariableNode,InteractionVariable> iVarMap,
            Map<VariableNode,LocationVariable> lVarMap) throws STSException {
        // No update created in OnTheFlySTS.
        return "";
    }

}
