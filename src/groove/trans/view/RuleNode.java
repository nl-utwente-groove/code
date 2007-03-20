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
 * $Id: RuleNode.java,v 1.1.1.2 2007-03-20 10:42:57 kastenberg Exp $
 */
package groove.trans.view;

import groove.graph.DefaultNode;
import groove.graph.GraphFormatException;


/**
 * Subclass of nodes that records the nodes' role as
 * EMBARGO, ERASER, READER or CREATOR.
 * Only <tt>RuleNode</tt>s are allowed in <tt>RuleGraph</tt>s;
 * any other type of node will result in a <tt>RuleGraphFormatException</tt>
 * when trying to convert the rule graph into a graph or rule.
 */
public class RuleNode extends DefaultNode {
    /**
     * Constructs a new rule node with given role.
     * @param role the role within the rule of the node to be constructed
     * @throws GraphFormatException
     * @require <tt>isValidRole(role)</tt>
     * @see RuleGraph#isValidRole(int)
     */
    public RuleNode(int role) throws GraphFormatException {
        this.role = role == RuleGraph.NO_ROLE ? RuleGraph.DEFAULT_ROLE : role;
        // this.graph = graph;
        //HARMEN: is the exception still thrown?
    }

    /**
     * Constructs a new rule node with given role, and given node number.
     * @param nr the node number of the new node
     * @param role the role within the rule of the node to be constructed
     * @throws GraphFormatException
     * @require <tt>isValidRole(role)</tt>
     * @see RuleGraph#isValidRole(int)
     */
    public RuleNode(int nr, int role) throws GraphFormatException {
    	super(nr);
        this.role = role == RuleGraph.NO_ROLE ? RuleGraph.DEFAULT_ROLE : role;
        // this.graph = graph;
        //HARMEN: is the exception still thrown?
    }

    /**
     * Returns the role of this rule node.
     * @return the role of this rule node
     * @ensure <tt>isValidRole(result)</tt>
     */
    public int role() {
        return role;
    }

    /** 
     * The role of this rule node.
     * @invariant <tt>isValidRole(role)</tt> 
     */
    protected final int role;
}