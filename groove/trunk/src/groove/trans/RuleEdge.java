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
 * $Id$
 */
package groove.trans;

import groove.graph.AbstractEdge;
import groove.graph.DefaultEdge;
import groove.graph.TypeEdge;
import groove.graph.TypeFactory;
import groove.graph.TypeLabel;
import groove.graph.algebra.ArgumentEdge;
import groove.graph.algebra.OperatorEdge;

/**
 * Supertype of all edges that may appear in a {@link RuleGraph}.
 * These are {@link DefaultEdge}s, {@link ArgumentEdge}s and
 * {@link OperatorEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RuleEdge extends AbstractEdge<RuleNode,RuleLabel> implements
        RuleElement {
    /**
     * Constructs a fresh rule edge
     */
    public RuleEdge(RuleNode source, RuleLabel label, RuleNode target) {
        super(source, label, target);
        assert source.equals(target) || label.isBinary() : String.format(
            "Can't create %s label %s between distinct nodes %s and %s",
            label.getRole().getDescription(false), label, source, target);
        TypeLabel typeLabel = label.getTypeLabel();
        if (typeLabel == null) {
            this.type = null;
        } else {
            this.type =
                TypeFactory.instance().createEdge(source.getType(), typeLabel,
                    target.getType());
        }
    }

    /**
     * Constructs a fresh rule edge
     */
    public RuleEdge(RuleNode source, TypeEdge type, RuleNode target) {
        super(source, new RuleLabel(type.label()), target);
        this.type = type;
    }

    /** Returns the (possibly {@code null}) edge type of this edge. */
    public TypeEdge getType() {
        return this.type;
    }

    /** The edge type of this rule edge. */
    private final TypeEdge type;
}
