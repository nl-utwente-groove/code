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
package groove.transform;

import groove.grammar.host.HostNode;

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
     * <code>Edge</code> methods on a given target.
     * @param target the target to be processed
     */
    void applyDelta(DeltaTarget target);
}