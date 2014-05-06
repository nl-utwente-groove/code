/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: DefaultEdge.java,v 1.15 2008-02-12 15:15:31 fladder Exp $
 */
package groove.graph.multi;

import groove.graph.AEdge;

/**
 * Default implementation of a multi-graph edge, with an identity
 * (represented by a number) in addition to source and target nodes and label.
 * @author Arend Rensink
 * @version $Revision: 4813 $ $Date: 2008-02-12 15:15:31 $
 */
public class MultiEdge extends AEdge<MultiNode,MultiLabel> {
    /**
     * Constructs a new edge on the basis of a given source, label and target.
     * @param source source node of the new edge
     * @param label label of the new edge
     * @param target target node of the new edge
     * @require <tt>source != null && target != null</tt>
     * @ensure <tt>source()==source</tt>, <tt>label()==label</tt>,
     *         <tt>target()==target </tt>
     */
    MultiEdge(MultiNode source, MultiLabel label, MultiNode target, int nr) {
        super(source, label, target);
        this.nr = nr;
    }

    @Override
    protected int computeHashCode() {
        return 31 * super.computeHashCode() + getNumber();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MultiEdge)) {
            return false;
        }
        return ((MultiEdge) obj).getNumber() == getNumber();
    }

    /** 
     * Returns the number of this edge.
     * The number uniquely identifies the edge.
     */
    public int getNumber() {
        return this.nr;
    }

    /** The (unique) number of this edge. */
    private final int nr;
}
