/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.transform;

import nl.utwente.groove.util.collect.Likeness;
import nl.utwente.groove.util.collect.TreeHashSet;

/**
 * Dedicated set of rule events.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RuleEventSet extends TreeHashSet<AbstractRuleEvent<?,?>> {
    @Override
    protected Likeness areEqual(AbstractRuleEvent<?,?> newKey, AbstractRuleEvent<?,?> oldKey) {
        return newKey.equalsEvent(oldKey)
            ? Likeness.EQUAL
            : Likeness.DISTINCT;
    }

    @Override
    protected int getCode(AbstractRuleEvent<?,?> key) {
        return key.eventHashCode();
    }
}
