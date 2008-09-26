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
package groove.explore.util;

import groove.trans.Rule;
import groove.trans.RuleEvent;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Extension of a map from rules to event collections
 * that additionally keeps track of the combined size of the event collections.
 * The total size is only accurate if the event collections are not modified after addition.
 * @author Arend Rensink
 * @version $Revision $
 */
class MatchMap extends LinkedHashMap<Rule, Collection<RuleEvent>> {
    @Override
    public Collection<RuleEvent> put(Rule key, Collection<RuleEvent> value) {
        totalSize += value.size();
        Collection<RuleEvent> result = super.put(key, value);
        if (result != null) {
            totalSize -= result.size();
        }
        return result;
    }

    @Override
    public Collection<RuleEvent> remove(Object key) {
        Collection<RuleEvent> result = super.remove(key);
        if (result != null) {
            totalSize -= result.size();
        }
        return result;
    }
    
    /** Returns the combined size of the event collections in the map. */
    public int totalSize() {
        return totalSize;
    }
    
    /** The combined size of the event sets. */
    private int totalSize;
}
