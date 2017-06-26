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
 * $Id$
 */
package groove.graph;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Interface of a graph (hyper-)edge, with two endpoints (i.e., nodes) and label.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Edge extends Element {
    /**
     * Returns the source node of this edge.
     */
    public Node source();

    /**
     * Returns the target node of this edge.
     */
    public Node target();

    /**
     * Returns the label of this edge. The label can never be <tt>null</tt>.
     * @return the label of this edge
     * @ensure <tt>result != null</tt>
     */
    public @NonNull Label label();

    /**
     * Returns the edge role of a given edge.
     * For most edge types, this is determined by (the edge role of) the label.
     */
    public EdgeRole getRole();

    /** Convenience method to tests if the source and target nodes of this edge coincide. */
    public boolean isLoop();
}