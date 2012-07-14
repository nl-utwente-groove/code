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

import static groove.graph.GraphRole.HOST;
import groove.algebra.AlgebraFamily;
import groove.graph.ElementMap;
import groove.graph.Graph;
import groove.graph.TypeGraph;
import groove.view.FormatException;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;

/**
 * Graph type used for graphs under transformation.
 * Host graphs consist of {@link HostNode}s and {@link HostEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface HostGraph extends Graph<HostNode,HostEdge>, DeltaTarget {
    @Override
    HostGraph newGraph(String name);

    @Override
    HostGraph clone();

    /** Clones this host graph, while optionally changing the algebras. */
    HostGraph clone(AlgebraFamily family);

    @Override
    HostFactory getFactory();

    /** Returns the algebra family of this host graph. */
    AlgebraFamily getFamily();

    /** Returns the type graph for this host graph, if any. */
    public TypeGraph getTypeGraph();

    /** 
     * Returns a copy of this graph, typed against a given type graph.
     * @throws FormatException if there are typing errors in the graph 
     */
    public HostGraph retype(TypeGraph typeGraph) throws FormatException;

    /** Converts this host graph to an equivalent aspect graph representation. */
    HostToAspectMap toAspectMap();

    /** 
     * Mapping from the elements of a host graph to those of a corresponding
     * aspect graph. For convenience, the aspect graph is bundled in with the map.  
     */
    public class HostToAspectMap extends
            ElementMap<HostNode,HostEdge,AspectNode,AspectEdge> {
        /**
         * Creates a new, empty map.
         */
        public HostToAspectMap(AspectGraph aspectGraph) {
            super(new AspectGraph.AspectFactory(HOST) {
                @Override
                public AspectLabel createLabel(String text) {
                    return AspectParser.getInstance().parse(text, HOST);
                }
            });
            this.aspectGraph = aspectGraph;
        }

        /** Returns the target aspect graph of this mapping. */
        public AspectGraph getAspectGraph() {
            return this.aspectGraph;
        }

        private final AspectGraph aspectGraph;
    }
}
