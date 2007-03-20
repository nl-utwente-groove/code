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
 * $Id: RuleEdge.java,v 1.1.1.1 2007-03-20 10:05:21 kastenberg Exp $
 */
package groove.trans.view;


import groove.graph.AbstractEdge;
import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.NodeEdgeMap;
import groove.graph.GraphFormatException;
import groove.graph.Label;
import groove.graph.Node;
import groove.rel.RegExprLabel;


/**
 * Subclass of edges that records the edges' role as
 * EMBARGO, ERASER, READER or CREATOR.
 * Only {@link RuleEdge}s are allowed in a {@link RuleGraph};
 * any other type of edge will result in a {@link GraphFormatException}
 * when trying to convert the rule graph into a graph or rule.
 * This implementation supports both unary and binary edges.
 */
public class RuleEdge extends AbstractEdge implements Edge {
    /**
     * Constructs a rule flag for a given nodes, with given label and role.
     * The nodes must be compatible with the role.
     * @param source the source node for the new edge
     * @param label the label for the new edge
     * @param role role for this rule edge
     * @require <tt>source, target instanceof RuleNode</tt>, 
     * <tt>isValidRole(role) || role == NO_ROLE</tt>
     * @see RuleGraph#isValidRole(int)
     */
    public RuleEdge(RuleNode source, Label label, int role) throws GraphFormatException {
        this(source, label, null, role);
    }

    /**
     * Constructs a rule edge between two nodes, with given label and role. The nodes must be
     * compatible with the role.
     * If the target node is <tt>null</tt>, it means the edge is unary.
     * @param source the source node for the new edge
     * @param label the label for the new edge
     * @param target the target node for the new edge; may be <tt>null</tt> for a unary edge
     * @param role role for this rule edge
     * @require <tt>source, target instanceof RuleNode</tt>,
     *          <tt>isValidRole(role) || role == NO_ROLE</tt>
     * @see RuleGraph#isValidRole(int)
     */
    public RuleEdge(RuleNode source, Label label, RuleNode target, int role)
            throws GraphFormatException {
        // start of constructor body
        super(source, label);
        this.target = target;
//        this.label = label;
        int sourceRole = source.role();
        int targetRole = target == null ? RuleGraph.NO_ROLE: target.role();
        if (role == RuleGraph.NO_ROLE) {
            if (sourceRole == targetRole || target == null)
                role = targetRole;
            else if (sourceRole == RuleGraph.READER || RuleGraph.inLHS(sourceRole)
                    && targetRole == RuleGraph.EMBARGO)
                role = targetRole;
            else if (targetRole == RuleGraph.READER || RuleGraph.inLHS(targetRole) && sourceRole == RuleGraph.EMBARGO)
                role = sourceRole;
            else
                throw new GraphFormatException(
                    "Rule edge '" + label + "' between nodes with incompatible roles");
        }
        switch (role) {
            case RuleGraph.EMBARGO :
                if (sourceRole == RuleGraph.CREATOR || targetRole == RuleGraph.CREATOR)
                    throw new GraphFormatException("Embargo edge '" + label + "' may not be between creator nodes");
                else if (
                    label.equals(RuleGraph.MERGE_LABEL) && source != target && !(RuleGraph.inLHS(sourceRole) && RuleGraph.inLHS(targetRole)))
                    throw new GraphFormatException("Merge embargo must be between LHS nodes");
                break;
            case RuleGraph.ERASER :
                if (!(RuleGraph.inLHS(sourceRole) && RuleGraph.inLHS(targetRole))) {
                    throw new GraphFormatException("Eraser edge '" + label + "' must be between LHS nodes");
                }
                break;
            case RuleGraph.READER :
                if (!(sourceRole == RuleGraph.READER) && (targetRole == RuleGraph.READER))
                    throw new GraphFormatException("Reader edge '" + label + "' must be between reader nodes");
                break;
            case RuleGraph.CREATOR :
                if (!(RuleGraph.inRHS(sourceRole) && RuleGraph.inRHS(targetRole)))
                    throw new GraphFormatException("Creator edge '" + label + "' must be between RHS nodes");
            default :
                break;
        }
        if (label.equals(RuleGraph.MERGE_LABEL)) {
            if (role != RuleGraph.CREATOR && role != RuleGraph.EMBARGO) {
                throw new GraphFormatException("Merge labels only allowed on empargo and creator edges");
            }                    
        } else if (label instanceof RegExprLabel && RegExprLabel.getWildcardId(label) == null) {
            if (role != RuleGraph.READER && role != RuleGraph.EMBARGO) {
                throw new GraphFormatException("Regular expression \""+label+"\" only allowed on reader and empargo edges");
            }
        }
        this.role = role;
    }

    /**
     * Constructs a rule edge between two nodes given as an array, with given label and role.
     * The nodes must be compatible with the role.
     * @param ends the array containint source and target node for the new edge
     * @param label the label for the new edge
     * @param role role for this rule edge
     * @require <tt>source, target instanceof RuleNode</tt>, 
     * <tt>isValidRole(role) || role == NO_ROLE</tt>
     * @see RuleGraph#isValidRole(int)
     */
    public RuleEdge(Node[] ends, Label label, int role) throws GraphFormatException {
        this((RuleNode) ends[SOURCE_INDEX], label, (RuleNode) ends[TARGET_INDEX], role);
    }
    
    public Node opposite() {
        if (target == null) {
            return source;  
        } else {
            return target;
        }
    }

    /**
     * Creates a rule edge with the label and role of this one, but
     * different source and target nodes.
     * The target node may be <tt>null</tt>, in which case we create a unary node.
     * @require <tt>source instanceof RuleNode && target instanceof RuleNode</tt>
     */
    public Edge newEdge(Node source, Node target) {
        try {
            return new RuleEdge((RuleNode) source, label(), (RuleNode) target, role);
        } catch (GraphFormatException exc) {
            assert false;
            return null;
        }
    }

    /**
     * Returns the label text with the label role previx appended.
     */
    public String textWithRole() {
        return RuleGraph.ROLE_PREFIX[role]+label();
    }
    
    protected int computeHashCode() {
        return super.computeHashCode() << role;
    }

    /**
     * String description contains role prefix in label.
     */
    public String toString() {
        if (target != null) {
            return source + " --" + textWithRole() + "--> " + target;
        } else {
            return source + " --" + textWithRole() + "-|"; 
        }
    }

    /**
     * Returns the role of this rule edge
     * @return the role of this rule edge
     * @ensure <tt>isValidRole(result)</tt>
     * @see RuleGraph#isValidRole(int)
     */
    public int role() {
        return role;
    }

    /** Returns an array of source and target. */
    public Node[] ends() {
        if (target == null) {
            return new Node[] { source };
        } else {
            return new Node[] {source, target};
        }
    }
    
    public int endCount() {
        return target == null ? 1 : 2;
    }
    
    public Node end(int index) {
        switch (index) {
        case 0:
            return source;
        case 1:
            if (target != null) {
                return target;
            }
        default:
            throw new IllegalArgumentException("End index " + index
                    + " does not exist for rule edge " + this);
        }
    }

    /** 
     * This implementation only attempts to create a {@link RuleEdge} if the
     * images of the end points in <code>elementMap</code> are {@link RuleNode}s;
     * otherwise it returns a {@link groove.graph.DefaultEdge}. 
     */
    public Edge imageFor(NodeEdgeMap elementMap) {
        Node newSource = elementMap.getNode(source);
        Node newTarget = elementMap.getNode(target);
        if (newSource instanceof RuleNode && (newTarget instanceof RuleNode || target == null)) {
            return newEdge(newSource, newTarget);
        } else if (newSource != null && newTarget != null) {
            return createEdgeImage(newSource, newTarget);
        } else {
            return null;
        }
    }

    /**
     * Callback method to create an ordinary binary edge, which will serve
     * as the image of this rule edge under some mapping that does not go to rule nodes.
     * @see #imageFor(NodeEdgeMap) 
     */
    protected Edge createEdgeImage(Node source, Node target) {
        return DefaultEdge.createEdge(source, label(), target);
    }
    
    /** The target node of the rule edge. */
    private final RuleNode target;
    /** 
     * The role of this rule edge.
     * @invariant <tt>isValidRole(role)</tt> 
     */
    protected final int role;
}