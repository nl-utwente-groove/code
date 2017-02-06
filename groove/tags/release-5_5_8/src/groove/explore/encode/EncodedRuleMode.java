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
package groove.explore.encode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <!=========================================================================>
 * An EncodedRuleMode describes an encoding of a RuleMode (which is either
 * 'positive' or 'negative') by means of a String.
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public class EncodedRuleMode extends EncodedFixedEnumeratedType<Boolean> {

    /**
     * Keyword for a 'positive' rule mode, meaning that the corresponding
     * acceptor succeeds when the rule matches.
     */
    public static final String POSITIVE = "Positive";

    /**
    * Keyword for a 'negative' rule mode, meaning that the corresponding
    * acceptor succeeds when the rule does not match.
    */
    public static final String NEGATIVE = "Negative";

    private static final String POSITIVE_TEXT =
        "Positive: when the rule matches.";
    private static final String NEGATIVE_TEXT =
        "Negative: when the rule does not match.";

    @Override
    public Map<String,String> fixedOptions() {
        Map<String,String> result = new LinkedHashMap<>();
        result.put(POSITIVE, POSITIVE_TEXT);
        result.put(NEGATIVE, NEGATIVE_TEXT);
        return result;
    }

    @Override
    public Map<String,Boolean> fixedValues() {
        Map<String,Boolean> result = new LinkedHashMap<>();
        result.put(POSITIVE, true);
        result.put(NEGATIVE, false);
        return result;
    }
}
