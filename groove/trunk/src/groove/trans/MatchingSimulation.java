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
 * $Id: MatchingSimulation.java,v 1.3 2007-04-01 12:49:54 rensink Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Node;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.rel.RegExprSimulation;
import groove.trans.match.MatchingMatcher;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Regular expression simulation that also applies merge and edge embargoes.
 * The embargoes must be provided through (abstract) factory methods,
 * {@link #computeInjectionMap()} and {@link #computeEmbargoMap()}.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 * @deprecated use {@link MatchingMatcher} instead
 */
@Deprecated
public class MatchingSimulation extends RegExprSimulation {
    public MatchingSimulation(Matching mapping, RuleFactory ruleFactory) {
        super(mapping);
    	this.ruleFactory = ruleFactory;
    }

    /**
     * Returns the rule factory of this simulation.
     */
    protected RuleFactory getRuleFactory() {
    	return ruleFactory;
    }

    /**
     * Returns the graph condition of which this is a matching simulation.
     */
    public GraphCondition getCondition() {
        return ((Matching) morph).getCondition();
    }
    
    /**
     * Invokes {@link #applyInjections(Node,Node)} and {@link #applyEmbargoes(Node)}
     * if <tt>changed</tt> has become singular, and then the <tt>super</tt> method.
     * Note that we override {@link #notifyNodeChange(groove.graph.Simulation.ImageSet, Edge)} rather
     * than {@link #notifySingular(groove.graph.Simulation.ImageSet)} because the latter may be invoked
     * <i>before</i> the images are put into the map.
     */
    @Override
    protected void notifyNodeChange(ImageSet<Node> changed, Edge trigger) {
        if (changed.isSingular()) {
            applyInjections(changed.getKey(), changed.getSingular());
            applyEmbargoes(changed.getKey());
        }
        super.notifyNodeChange(changed, trigger);
    }

    /**
     * Applies the stored injections on the basis of an image set that
     * has just been turned to singular.
     * Callback method from {@link #notifyNodeChange(groove.graph.Simulation.ImageSet, Edge)}.
     */
    protected void applyInjections(Node trigger, Node image) {
        reporter.start(APPLY_INJECTIONS);
        try {
            if (getInjectionMap() != null) {
                Set<Node> injection = getInjectionMap().get(trigger);
                if (injection != null) {
                	for (Node injectionElem: injection) {
                        ImageSet<Node> injectionImages = getFreshImageSet(injectionElem);
                        if (injectionImages != null && injectionImages.remove(image)) {
                            // there's a choice here: notify always, never, or on singularity?
                            if (injectionImages.isSingular()) {
                                notifyNodeChange(injectionImages, null);
                            }
                        }
                    }
                }
            }
        } finally {
            reporter.stop();
        }
    }
    
    /**
     * Applies the stored embargoes on the basis of an image set that has just been turned to
     * singular. 
     * Callback method from {@link #notifyNodeChange(groove.graph.Simulation.ImageSet, Edge)}.
     */
    protected void applyEmbargoes(Node trigger) {
        reporter.start(APPLY_EMBARGOES);
        try {
			if (getEmbargoMap() != null) {
				Collection<Edge> embargoEdgeSet = getEmbargoMap().get(trigger);
				if (embargoEdgeSet != null) {
					for (Edge embargoEdge : embargoEdgeSet) {
						applyEmbargo(trigger, embargoEdge);
					}
				}
			}
		} finally {
			reporter.stop();
		}
    }
    
    /**
	 * Attempts to apply a single embargo edge, triggered by the singularity of
	 * one of its endpoints. This implementation first tests if all the
	 * endpoints are singular before proceedings.
	 */
    protected void applyEmbargo(Node trigger, Edge key) {
        int arity = key.endCount();
        // flags if the ends of the embargo key are all present;
        // if not, we will not yet attempt to apply the embargo
        boolean endsSingular = true;
        RegExpr keyExpr = RegExprLabel.getRegExpr(key.label());
        if (keyExpr != null) {
        	Iterator<String> varIter = keyExpr.allVarSet().iterator();
        	while (endsSingular && varIter.hasNext()) {
        		endsSingular = getValuation().containsKey(varIter.next());
        	}
        }
        for (int i = 0; endsSingular && i < arity; i++) {
            Node keyEnd = key.end(i);
            if (keyEnd != trigger) {
                ImageSet<Node> endImages = getNode(keyEnd);
                endsSingular = endImages != null && endImages.isSingular();
            }
        }
        if (endsSingular && getEdgeMatches(key).hasNext()) {
            throw emptyImageSet;
        }
    }

    /**
     * Getter for the injection map.
     * This is a map from domain nodes to sets of nodes that may not be 
     * mapped injectively.
     * @return te injection map for this simulation.
     */
    protected Map<Node,Set<Node>> getInjectionMap() {
    	if (injectionMap == null) {
    		injectionMap = computeInjectionMap();
    	}
    	return injectionMap;
    }

    /**
     * Getter for the embargo map.
     * This is a map from domain nodes to sets of incident edges that 
     * are forbidden in the image.
     * @return te embargo map for this simulation.
     */
    protected Map<Node,Collection<Edge>> getEmbargoMap() {
    	if (embargoMap == null) {
    		embargoMap = computeEmbargoMap();
    	}
    	return embargoMap;
    }
    
    /**
     * Callback method to create the injection map.
     * The injection map is a partial map from domain nodes to sets of domain nodes
     * that should be mapped injectively with respect to the key.
     * This implementation takes the map from the matching's graph condition
     * (assumed to be a {@link DefaultGraphCondition}).
     * @see #initSimulation()
     */
    protected Map<Node, Set<Node>> computeInjectionMap() {
        return ((DefaultGraphCondition) getCondition()).getInjectionMap();
    }
    
    /**
     * Callback method to create the embargo map.
     * The embargo map is a partial map from domain nodes to sets of incident
     * embargo edges.
     * This implementation takes the map from the matching's graph condition
     * (assumed to be a {@link DefaultGraphCondition}).
     * @see #initSimulation()
     */
    protected Map<Node, Collection<Edge>> computeEmbargoMap() {
        return ((DefaultGraphCondition) getCondition()).getNegationMap();        
    }
    
//    /**
//     * Callback factory method to create the matching order.
//     * The matching order is used to generate the key iterator.
//     * This implementation takes the matching order from the matching's graph condition
//     * (assumed to be a {@link DefaultGraphCondition}).
//     * @see #initSimulation()
//     */
//    @Override
//    protected List<Element> computeMatchingSchedule() {
//        return new IndegreeScheduleFactory();        
//    }
//    
    /**
     * Mapping from domain nodes to sets of domain nodes that should be matched
     * injectively with respect to it.
     */
    private Map<Node,Set<Node>> injectionMap;
    /**
     * Mapping from domain nodes to sets of incident embargo edges.
     */
    private Map<Node,Collection<Edge>> embargoMap;
    /**
     * The underlying rule factory for this simulation.
     */
    private RuleFactory ruleFactory;

    /** Handle for profiling {@link #applyEmbargoes(Node)} */
    static final int APPLY_EMBARGOES = reporter.newMethod("applyEmbargoes(ImageSet)");
    /** Handle for profiling {@link #applyInjections(Node, Node)} */
    static final int APPLY_INJECTIONS = reporter.newMethod("applyInjections(ImageSet)");
}