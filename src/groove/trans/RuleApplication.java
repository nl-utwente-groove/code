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
 * $Id: RuleApplication.java,v 1.1.1.1 2007-03-20 10:05:20 kastenberg Exp $
 */
package groove.trans;


import groove.graph.DeltaApplier;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.DeltaTarget;
import groove.rel.VarNodeEdgeMap;

/**
 * Interface to wrap the computation involved in applying a production rule.
 * This is used in two different phases: when constructing a
 * derivation, and to reconstruct the matching and the target graph after they
 * have been minimized, if the cached representation has been discarded.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public interface RuleApplication extends Derivation, DeltaApplier {
    /**
     * Returns the rule for which this is an application.
     */
    public Rule getRule();
    
    /**
     * Returns the event underlying this application.
     */
    public RuleEvent getEvent();
    
    /**
     * Returns the source graph to which the rule is applied.
     */
    public Graph getSource();
    
    /**
     * Returns a target graph created as a result of the application.
     * The target is typically created lazily.
     */
    public Graph getTarget();
    
    /**
     * Indicates if the target of this application has already been computed.
     */
    public boolean isTargetSet();
    
    /**
     * Returns the mapping from the rule's LHS to the source graph.
     */
    public VarNodeEdgeMap getAnchorMap();
    
    /**
     * Returns the mapping from the rule's coanchor to the target graph.
     * The mapping is only guaranteed to provide images for the creator nodes and
     * for the endpoints and variables of the creator edges.
     */
    public VarNodeEdgeMap getCoanchorMap();
    
    /**
     * Sets the image of the rule's coanchor in the target graph.
     * @see #getCoanchorImage()
     */
    public void setCoanchorImage(Element[] image);
    
    /**
     * Returns the image of the rule's coanchor in the target graph.
     * @see Rule#anchor()
     */
	public Element[] getCoanchorImage();

    /**
     * Applies the rule to a given source graph, using a previously computed footprint
     * and a target object.
     * This method is used to reconstruct a rule application from its footprint.
     * The source should coincide with that for which the footprint was originally created 
     * @param target the target object on which the modifications are to be performed
     */
    public void applyDelta(DeltaTarget target);
//
//    /**
//     * Returns the {@link groove.trans.RuleFactory} the is needed to instantiate classes for performing transformations.
//     * @return the current <code>ruleFactory</code>
//     */
//    public RuleFactory getRuleFactory();
}