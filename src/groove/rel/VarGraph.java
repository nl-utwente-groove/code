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
 * $Id: VarGraph.java,v 1.6 2008-01-30 09:32:26 iovka Exp $
 */
package groove.rel;

import java.util.Set;

import groove.graph.Edge;
import groove.graph.Graph;

/**
 * Graph type with additional support for <i>variables</i>,
 * which are mapped to graph elements by the corresponding morphisms. 
 * @author Arend Rensink
 * @version $Revision$
 * @deprecated use ordinary {@link Graph}s and the utilities in {@link VarSupport}.
 */
@Deprecated
public interface VarGraph extends Graph {//, VarSetSupport {
    /**
     * Returns the set of variable-binding edges occurring in this graph.
     */
    Set<Edge> varEdgeSet();
}
