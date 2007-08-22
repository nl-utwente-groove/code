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
 * $Id: VarFlag.java,v 1.5 2007-08-22 09:19:52 kastenberg Exp $
 */
package groove.rel;

import groove.graph.DefaultFlag;
import groove.graph.GenericNodeEdgeMap;
import groove.graph.NodeEdgeMap;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.UnaryEdge;

/**
 * Specialization of a default edge with a variable as label
 * (in the form of a {@link groove.rel.RegExpr.Wildcard}).
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
@Deprecated
public class VarFlag extends DefaultFlag {
    /**
     * Creates an edge with a given variable.
     * @param source source node of the new edge
     * @param var the variable name to wrap in the label of the new edge
     */
    public VarFlag(Node source, String var) {
        super(source, RegExpr.wildcard(var).toLabel());
        this.var = var;
    }

    /**
     * If the <code>label</code> contains a named wildcard, creates a 
     * new {@link VarFlag} with that name; otherwise, delegates to <code>super</code>.
     */
    @Override
    public UnaryEdge newEdge(Node source, Label label) {
        String var = RegExprLabel.getWildcardId(label);
        if (var == null) {
            return super.newEdge(source, label);
        } else {
            return new VarFlag(source, var);
        }
    }

    /**
     * If <code>elementMap</code> contains an image for the variable in this
     * edge (which must then be a {@link Label}, creates a {@link DefaultFlag}
     * with that label; otherwise delegates to <code>super</code>.
     */
    @Override
    protected UnaryEdge imageFor(NodeEdgeMap elementMap) {
        if (elementMap instanceof VarNodeEdgeMap) {
            Label varImage = ((VarNodeEdgeMap) elementMap).getVar(var);
            if (varImage != null) {
                Node sourceImage = elementMap.getNode(source);
                if (sourceImage == null) {
                    return null;
                } else {
                    return newEdge(sourceImage, varImage);
                }
            }
        } 
        return super.imageFor(elementMap);
    }

    /**
	 * Returns the variable name wrapped in the label of this edge.
	 */
    public final String getVar() {
        return var;
    }

    /** The variable stored in this flag. */
    private final String var;
}