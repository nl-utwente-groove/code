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
package groove.explore.encode;

import groove.explore.strategy.MinimaxStrategy;

import java.util.Map;
import java.util.TreeMap;

/**
 * Option specifying whether the {@link MinimaxStrategy} should start
 * with minimising or maximising.
 * @author Rick Hindriks
 * @version $Revision $
 */
public class EncodedMinMaxMode extends EncodedFixedEnumeratedType<Boolean> {
    /** Keyword for the value to start with maximising. */
    public static final String START_MAX = "MAX";
    /** Keyword for the value to start with minimising. */
    public static final String START_MIN = "MIN";

    @Override
    public Map<String,String> fixedOptions() {
        Map<String,String> result = new TreeMap<String,String>();
        result.put(START_MAX, "Maximize gains");
        result.put(START_MIN, "Minimize gains");
        return result;
    }

    @Override
    public Map<String,Boolean> fixedValues() {
        Map<String,Boolean> result = new TreeMap<String,Boolean>();
        result.put(START_MAX, true);
        result.put(START_MIN, false);
        return result;
    }

}
