/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.explore;

import groove.explore.config.TraverseKind;
import groove.util.Exceptions;

/**
 * A frontier that orders its elements according to a given {@link TraverseKind}.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class BasicFrontier extends ExploreFrontier {
    /** Constructs a fresh frontier for a given traversal kind. */
    BasicFrontier(TraverseKind traverse, int maxSize) {
        super(maxSize);
        this.traverse = traverse;
    }

    /**
     * Returns the traversal kind of this basic strategy.
     */
    public TraverseKind getTraverse() {
        return this.traverse;
    }

    private final TraverseKind traverse;

    /** Creates a basic frontier with a given traversal kind and no maximum size. */
    public static BasicFrontier createFrontier(TraverseKind traverse) {
        return createFrontier(traverse, 0);
    }

    /** Creates a basic frontier with a given traversal kind and maximum size. */
    public static BasicFrontier createFrontier(TraverseKind traverse, int maxSize) {
        assert maxSize >= 0;
        switch (traverse) {
        case NEWEST:
            return new NewestFrontier(maxSize);
        case OLDEST:
        case RANDOM:
        default:
            throw Exceptions.UNREACHABLE;
        }
    }
}
