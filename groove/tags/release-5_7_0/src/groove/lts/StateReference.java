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
package groove.lts;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import groove.util.cache.CacheHolder;
import groove.util.cache.CacheReference;

/**
 * Cache reference for state caches, which carry the system record as static
 * information.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StateReference extends CacheReference<@Nullable StateCache> {
    /**
     * Copies the system record from the template.
     */
    protected StateReference(CacheHolder<@Nullable StateCache> holder, StateCache referent,
        StateReference template) {
        super(holder, referent, template);
        this.gts = template.gts;
    }

    /**
     * Creates a reference with an explicitly given (non-<code>null</code>)
     * system record.
     */
    protected StateReference(boolean strong, int incarnation, StateReference template, GTS gts) {
        super(strong, incarnation, template);
        this.gts = gts;
    }

    /** Returns the system record associated with this reference. */
    public GTS getGTS() {
        return this.gts;
    }

    @Override
    protected @NonNull CacheReference<@Nullable StateCache> createNullInstance(boolean strong,
        int incarnation) {
        return new StateReference(strong, incarnation, this, this.gts);
    }

    @Override
    public CacheReference<@Nullable StateCache> newReference(
        CacheHolder<@Nullable StateCache> holder, StateCache cache) {
        return new StateReference(holder, cache, this);
    }

    /** The system record associated with this reference. */
    private final GTS gts;

    /**
     * Factory method for an uninitialised strong reference, i.e., with referent
     * <code>null</code>. This is a convenience method for
     * {@link #newInstance(boolean)} with parameter <code>true</code>.
     */
    static public StateReference newInstance(GTS gts) {
        return newInstance(gts, true);
    }

    /**
     * Factory method for an uninitialised reference, i.e., with referent
     * <code>null</code>.
     * @param strong if <code>true</code> the reference instance is to be
     *        strong
     * @return a reference that is either strong or soft, depending on
     *         <code>strong</code>
     */
    static public StateReference newInstance(GTS gts, boolean strong) {
        return new StateReference(strong, 0, null, gts);
    }
}
