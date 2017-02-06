/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.match.rete;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Represents a strategy for joining two matches inside a
 * subgraph checker node.
 *
 * @author Arash Jalali
 * @version $Revision $
 */
public interface JoinStrategy<LeftMatchType extends AbstractReteMatch,RightMatchType extends AbstractReteMatch> {
    /**
     * Should check if the two give matches can be joined.
     *
     * @param left The match that has arrived at a subgraph checker from its
     * left antecedent
     * @param right The match that has arrived at a subgraph checker from its
     * left antecedent
     * @return <code>true</code> if the two matches can be joined,
     * <code>false</code> otherwise.
     *
     */
    public boolean test(LeftMatchType left, RightMatchType right);

    /**
     * Should create a match that is the result of "join"-ing
     * the given left and right matches, whatever the semantics of the
     * join operation.
     *
     * @param left The match that has arrived at a subgraph checker from its
     * left antecedent
     * @param right The match that has arrived at a subgraph checker from its
     * left antecedent
     * @return The result of the join or <code>null</code> if the join fails for
     * any reason.
     */
    public AbstractReteMatch construct(@Nullable LeftMatchType left,
        @Nullable RightMatchType right);
}
