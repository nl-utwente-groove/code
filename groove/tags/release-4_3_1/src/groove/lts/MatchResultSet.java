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
package groove.lts;

import groove.util.TreeHashSet;

/** Set of {@link MatchResult}s, which only tests for code and equality of the events. */
public class MatchResultSet extends TreeHashSet<MatchResult> {

    @Override
    protected boolean areEqual(MatchResult newKey, MatchResult oldKey) {
        return newKey.getEvent().equals(oldKey.getEvent());
    }

    @Override
    protected int getCode(MatchResult key) {
        return key.getEvent().hashCode();
    }
}