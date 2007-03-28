// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: AttributeMatching.java,v 1.2 2007-03-28 15:12:27 rensink Exp $
 */

package groove.trans;

import groove.graph.AttributeSimulation;
import groove.graph.Graph;
import groove.graph.Simulation;
import groove.rel.VarNodeEdgeMap;

/**
 * Creates an morphism between attributed graphs from a given morphism.
 * @author Harmen Kastenberg
 * @version $Revision 1.0$
 * 
 * Class description.
 * @deprecated No longer needed now construction is done using factories
 */
public class AttributeMatching extends DefaultMatching {

    /**
     * @param condition
     * @param graph
     */
    public AttributeMatching(DefaultGraphCondition condition, Graph graph, RuleFactory ruleFactory) {
        super(condition, graph, ruleFactory);
    }

	/* (non-Javadoc)
     * @see groove.graph.DefaultMorphism#createSimulation()
     */
//    protected AttributeSimulation createSimulation() {
//        return new AttributeSimulation(this);
//    }

    /* (non-Javadoc)
     * @see groove.graph.DefaultMorphism#createMorphism(groove.graph.Simulation)
     */
    @Override
    protected AttributeMatching createMorphism(final Simulation sim) {
        final AttributeSimulation attrSim = (AttributeSimulation) sim;
        AttributeMatching result = new AttributeMatching((DefaultGraphCondition) attrSim.getCondition(), attrSim.cod(), getRuleFactory()) {
            @Override
            protected VarNodeEdgeMap createElementMap() {
                return attrSim.getSingularMap();
            }
        };
        result.setFixed();
        return result;
    }
}