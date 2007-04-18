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
 * $Id: VarBinaryEdge.java,v 1.4 2007-04-18 08:36:16 rensink Exp $
 */
package groove.rel;

import groove.graph.BinaryEdge;
import groove.graph.DefaultEdge;
import groove.graph.Label;
import groove.graph.Node;

/**
 * Specialization of a default edge with a variable as label
 * (in the form of a {@link groove.rel.RegExpr.Wildcard}).
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
@Deprecated
public class VarBinaryEdge extends DefaultEdge implements VarEdge {
    /**
     * Creates an edge wrapping a given variable.
     * The variable is turned into a {@link RegExpr.Wildcard}.
     * @param source source node of the new edge
     * @param var the variable name to wrap in the label of the new edge
     * @param target target node of the new edge
     */
    public VarBinaryEdge(Node source, String var, Node target) {
        super(source, RegExpr.wildcard(var).toLabel(), target);
        this.var = var;
    }

    /**
     * If the <code>label</code> contains a named wildcard, creates a 
     * new {@link VarBinaryEdge} with that name; otherwise, delegates to <code>super</code>.
     */
    @Override
    public BinaryEdge newEdge(Node source, Label label, Node target) {
        String var = RegExprLabel.getWildcardId(label);
        if (var == null) {
            return super.newEdge(source, label, target);
        } else {
            return new VarBinaryEdge(source, var, target);
        }
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