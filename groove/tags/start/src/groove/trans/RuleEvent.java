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
 * $Id: RuleEvent.java,v 1.1.1.2 2007-03-20 10:42:56 kastenberg Exp $
 */
package groove.trans;


import groove.graph.Graph;
import groove.graph.Label;
import groove.rel.VarNodeEdgeMap;

/**
 * Interface to encode the information on a graph transition.
 * Together with the source and target state, the event uniquely defines the transition.
 * Typically, the event stores the anchor images of the particular rule application in the host graph.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:56 $
 */
public interface RuleEvent extends Comparable<RuleEvent> {
    /**
     * Returns the rule for which this is an application.
     */
    public Rule getRule();
    
    /**
     * Returns a label that uniquely identifies this event.
     */
    public Label getLabel();

	/**
     * Returns the mapping from the anchors in the rule's LHS to the source graph.
     */
    public VarNodeEdgeMap getAnchorMap();
    
    /**
     * Returns a string representation of the anchor image.
     */
    public String getAnchorImageString();
    
    /**
     * Returns a provisional mapping from the rule's RHS to the target graph,
     * minus the creator nodes.
     * The mapping is only guaranteed to provide images
     * for the endpoints and variables of the creator edges.
     */
    public VarNodeEdgeMap getCoanchorMap();

    /**
	 * Indicates if a matching of the rule exists based on the mapping in this event.
	 */
	public boolean hasMatching(Graph source);

    /**
	 * Raturns a matching of the rule based on the mapping in this event, if it exists.
	 * Returns <code>null</code> otherwise.
	 */
	public Matching getMatching(Graph source);

    /**
     * Factory method to create a rule application on a given source graph.
     */
    public RuleApplication createApplication(Graph source);
//
//    /**
//     * @param ruleFactory the <code>ruleFactory</code> to be set
//     */
//    public void setRuleFactory(RuleFactory ruleFactory);
//
//    /**
//     * Returns the {@link groove.trans.RuleFactory} the is needed to instantiate classes for performing transformations.
//     * @return the current <code>ruleFactory</code>
//     */
//    public RuleFactory getRuleFactory();
}