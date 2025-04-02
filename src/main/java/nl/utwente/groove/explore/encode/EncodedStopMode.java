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
package nl.utwente.groove.explore.encode;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.utwente.groove.explore.strategy.StopMode;

/**
 * An {@link EncodedStopMode} gives a choice between exploration up to
 * a condition (meaning that a state where it holds --- a <i>hit state</i> --- is not explored)
 * or until it holds (meaning that a hit state is explored,
 * but its successors are not)
 * @author Arend Rensink
 */
public class EncodedStopMode extends EncodedFixedEnumeratedType<StopMode> {

    /**
     * Keyword for Depth-First search
     */
    public static final String UP_TO_KEY = "->";

    /**
    * Keyword for Breadth-First search
    */
    public static final String INCLUDE_KEY = "=>";

    private static final String UP_TO_TEXT = "Up to: Hit states are not explored";
    private static final String INCLUDE_TEXT = "Include: Hit states are the last to be explored";

    @Override
    public Map<String,String> fixedOptions() {
        Map<String,String> result = new LinkedHashMap<>();
        result.put(UP_TO_KEY, UP_TO_TEXT);
        result.put(INCLUDE_KEY, INCLUDE_TEXT);
        return result;
    }

    @Override
    public Map<String,StopMode> fixedValues() {
        Map<String,StopMode> result = new LinkedHashMap<>();
        result.put(UP_TO_KEY, StopMode.UP_TO);
        result.put(INCLUDE_KEY, StopMode.INCLUDE);
        return result;
    }
}
