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
 * $Id: IsoChecker.java,v 1.3 2007-11-29 12:44:42 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;

/**
 * Interface for strategies that check isomorphism between graphs.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface IsoChecker {
    /**
     * Tests if two graphs are isomorphic. Implementations of this method are
     * allowed to be incomplete, in the sense that a <code>false</code> answer
     * does not guarantee non-isomorphism, but a <code>true</code> answer does
     * guarantee isomorphism. Although a complete algorithm is optimal, for the
     * purpose of collapsing states an "almost" complete but faster algorithm is
     * better than a complete, slow one.
     * @param dom First graph to be tested
     * @param cod Second graph to be tested
     * @return <code>true</code> only if <code>dom</code> and
     *         <code>cod</code> are isomorphic
     */
    public <N extends Node,L extends Label,E extends Edge> boolean areIsomorphic(
            Graph<N,L,E> dom, Graph<N,L,E> cod);

    /** 
     * Indicates if the checker is currently set to strong.
     * If the checker is strong, no false negatives will be returned.
     */
    public boolean isStrong();
}
