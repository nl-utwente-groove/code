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
 * $Id: AttributedSPORule.java,v 1.5 2007-04-18 08:36:10 rensink Exp $
 */
package groove.trans;

import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.util.FormatException;

/**
 * Extension of the normal SPO-rule in which we take special care about attribute
 * nodes.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.5 $ $Date: 2007-04-18 08:36:10 $
 * @deprecated all funtionality now in {@link SPORule}.
 */
@Deprecated
public class AttributedSPORule extends SPORule {

//    public Graph getGraphFactory() {
//    	// we should create the current graph factory to create new graphs
//    	// and add the specific nodes and edges used for attributed graphs
//    	// in that
//        return new DefaultAttributedGraph();
//    }

//    /**
//     * Constructor creating an rule (probably) using attributes based on the given
//     * morphism with the given name.
//     * 
//     * @param morphism the morphism on which this rule is based
//     * @param name the name of this rule
//     */
//    public AttributedSPORule(Morphism morphism, NameLabel name, RuleFactory ruleFactory) {
//    	super(morphism, name, ruleFactory);
////    	this.dom = lhs();
//    	this.cod = lhs();
//    }

    /**
     * Constructor.
     * @param morphism the rule morphism from which to create the rule
     * @param name the name of this rule
     * @param priority the priority of this rule
     * @param properties
     */
    public AttributedSPORule(Morphism morphism, NameLabel name, int priority, SystemProperties properties) throws FormatException {
    	super(morphism, name, priority, properties);
    }

    /**
     * This method checks whether the given graph contains the given element.
     * This method first checks whether the given element is a node representing
     * an algebra constant. In that case, it always returns <tt>true</tt>, since
     * algebra constant are theoretically always present in the graph.
     * @see SPORule#containsElement(Graph, Element)
     * @param graph the graph in which to look for the element
     * @param element the element to look for
     * @return <tt>true</tt> if the graph contains the element or if the element
     * is a node representing an algebra constant, <tt>false</tt> otherwise
     */
    @Override
    protected boolean containsElement(Graph graph, Element element) {
    	// value nodes and product nodes are implicitely part of the graph
    	if (element instanceof ValueNode || element instanceof ProductNode)
    		return true;
    	if (element instanceof ProductEdge)
    		return true;
   		return super.containsElement(graph, element);
    }
//
//	@Override
//	protected ConditionSearchPlanFactory createSearchPlanFactory() {
//		return new AttributeScheduleFactory();
//	}
}
