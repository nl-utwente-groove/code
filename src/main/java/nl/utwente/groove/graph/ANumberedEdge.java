/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */
package nl.utwente.groove.graph;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Abstract edge class for edges carrying an edge number.
 * The number is factory-scoped: within the creating factory, it uniquely
 * identifies the edge. The number also enters the equality test and hash
 * code, so that edges with the same content can coexist (parallel edges).
 * Whether such parallel edges actually occur is determined by the creating
 * factory, which in simple mode pools content-equal edges
 * (see {@link StoreFactory#isSimple()}).
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public abstract class ANumberedEdge<N extends Node,L extends Label> extends AEdge<N,L>
    implements NumberedEdge {
    /**
     * Creates a numbered edge with a given source and target node and label.
     */
    protected ANumberedEdge(N source, L label, N target, int number) {
        super(source, label, target);
        this.number = number;
    }

    @Override
    public int getNumber() {
        return this.number;
    }

    private final int number;

    /*
     * In addition to the content-based super implementation, takes the
     * edge number into account.
     */
    @Override
    protected int computeHashCode() {
        return 31 * super.computeHashCode() + getNumber();
    }

    /*
     * In addition to the content-based super implementation, tests for
     * equality of the edge number.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NumberedEdge other && other.getNumber() == getNumber())) {
            return false;
        }
        return super.equals(obj);
    }
}
