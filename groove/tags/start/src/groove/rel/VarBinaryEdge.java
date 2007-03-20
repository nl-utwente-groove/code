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
 * $Id: VarBinaryEdge.java,v 1.1.1.2 2007-03-20 10:42:54 kastenberg Exp $
 */
package groove.rel;

import groove.graph.BinaryEdge;
import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.NodeEdgeMap;
import groove.graph.Label;
import groove.graph.Node;

/**
 * Specialization of a default edge with a variable as label
 * (in the form of a {@link groove.rel.RegExpr.Wildcard}).
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class VarBinaryEdge extends DefaultEdge implements VarEdge {
    /**
     * Creates an edge wrapping a given variable.
     * The variable is turned into a {@link RegExpr.Wildcard}.
     * @param source source node of the new edge
     * @param var the variable name to wrap in the label of the new edge
     * @param target target node of the new edge
     */
    public VarBinaryEdge(Node source, String var, Node target) {
        super(source, new RegExprLabel(RegExpr.wildcard(var)), target);
        this.var = var;
    }

    /**
     * If the <code>label</code> contains a named wildcard, creates a 
     * new {@link VarBinaryEdge} with that name; otherwise, delegates to <code>super</code>.
     */
    public BinaryEdge newEdge(Node source, Label label, Node target) {
        String var = RegExprLabel.getWildcardId(label);
        if (var == null) {
            return super.newEdge(source, label, target);
        } else {
            return new VarBinaryEdge(source, var, target);
        }
    }

    /**
     * Creates a new {@link VarBinaryEdge} with the same name as this one.
     */
    public BinaryEdge newEdge(Node source, Node target) {
        return new VarBinaryEdge(source, var, target);
    }

    /**
     * If <code>elementMap</code> is a {@link VarNodeEdgeMap} that
     * contains an image for the variable in this
     * edge (which must then be a {@link Label}),
     * this implementation invokes {@link #newEdge(Node, Label, Node)} with
     * that image (and the images of source and target node);
     * otherwise delegates to <code>super</code>.
     */
    public Edge imageFor(NodeEdgeMap elementMap) {
        if (elementMap instanceof VarNodeEdgeMap) {
            Label varImage = ((VarNodeEdgeMap) elementMap).getVar(var);
            if (varImage != null) {
                Node sourceImage = elementMap.getNode(source);
                Node targetImage = elementMap.getNode(target);
                if (sourceImage == null || targetImage == null) {
                    return null;
                } else {
                    return newEdge(sourceImage, varImage, targetImage);
                }
            }
        }
        return super.imageFor(elementMap);
    }

    /**
     * Returns the variable name wrapped in the label of this edge.
     */
    public final String var() {
        return var;
    }

    /** The variable stored in this edge. */
    private final String var;
}