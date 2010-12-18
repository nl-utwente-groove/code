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
 * $Id: DeltaApplier.java,v 1.4 2008-01-30 09:32:57 iovka Exp $
 */
package groove.graph;

import groove.trans.HostNode;

/**
 * Interface for an object that can process a {@link DeltaTarget}, by invoking
 * its {@link DeltaTarget#addNode(HostNode)} and
 * {@link DeltaTarget#removeNode(HostNode)} and the corresponding <code>Edge</code>
 * methods multiple times.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface DeltaApplier {
    /**
     * When invoked, will call {@link DeltaTarget#addNode(HostNode)} and
     * {@link DeltaTarget#removeNode(HostNode)} and the corresponding
     * <code>Edge</code> methods on a given target. The effect is the same as
     * calling {@link #applyDelta(DeltaTarget, int)} with {@link #ALL_ELEMENTS}
     * as second parameter.
     * @param target the target to be processed
     */
    void applyDelta(DeltaTarget target);

    /**
     * When invoked, will call {@link DeltaTarget#addNode(HostNode)} and
     * {@link DeltaTarget#removeNode(HostNode)} and the corresponding
     * <code>Edge</code> methods on a given target. A second parameter
     * controls whether nodes, edges or both should be added and removed.
     * @param target the target to be processed
     * @param mode if {@link #NODES_ONLY}, only nodes will be processed; if
     *        {@link #EDGES_ONLY}, only edges will be processed; if
     *        {@link #ALL_ELEMENTS}, both nodes and edges will be processed.
     */
    void applyDelta(DeltaTarget target, int mode);

    /**
     * Mode for {@link #applyDelta(DeltaTarget, int)} indicating that all
     * elements should be processed.
     */
    static public final int ALL_ELEMENTS = 0;
    /**
     * Mode for {@link #applyDelta(DeltaTarget, int)} indicating that only nodes
     * should be processed.
     */
    static public final int NODES_ONLY = 1;
    /**
     * Mode for {@link #applyDelta(DeltaTarget, int)} indicating that only edges
     * should be processed.
     */
    static public final int EDGES_ONLY = 2;
}