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
package groove.graph;

/**
 * Edges used in type graphs.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TypeEdge extends AbstractEdge<TypeNode,TypeLabel,TypeNode> {
    /**
     * Constructs a new type edge.
     */
    public TypeEdge(TypeNode source, TypeLabel label, TypeNode target) {
        super(source, label, target);
        assert source.equals(target) || label.isBinary() : String.format(
            "Can't create %s label %s between distinct nodes %s and %s",
            label.getKind().getName(false), label, source, target);
    }

    /** Indicates if this edge type is abstract. */
    public final boolean isAbstract() {
        return this.abstractType;
    }

    /** Sets this edge type to abstract. */
    public final void setAbstract() {
        this.abstractType = true;
    }

    /** Flag indicating if this edge type is abstract. */
    private boolean abstractType;
}
