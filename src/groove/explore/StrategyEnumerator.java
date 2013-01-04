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
package groove.explore;

import groove.explore.encode.Serialized;
import groove.explore.encode.Template;
import groove.explore.encode.TemplateList;
import groove.explore.strategy.Strategy;
import groove.trans.Grammar;
import groove.view.FormatException;

import java.util.EnumSet;

/**
 * <!=========================================================================>
 * StrategyEnumerator enumerates all strategies that are available in GROOVE.
 * With this enumeration, it is possible to create an editor for strategies
 * (inherited method createEditor, stored results as a Serialized) and to
 * parse a strategy from a Serialized (inherited method parse).
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public class StrategyEnumerator extends TemplateList<Strategy> {

    /**
     * Enumerates the available strategies one by one. A strategy is defined
     * by means of a Template<Strategy> instance.
     */
    private StrategyEnumerator(EnumSet<StrategyValue> enumSet) {
        super("exploration strategy", STRATEGY_TOOLTIP);
        for (StrategyValue value : enumSet) {
            Template<Strategy> template = value.getTemplate();
            addTemplate(template);
        }
    }

    /** Returns a fresh instance of this class. */
    public static StrategyEnumerator newInstance() {
        return new StrategyEnumerator(EnumSet.allOf(StrategyValue.class));
    }

    /** Returns a fresh instance of this class. */
    public static StrategyEnumerator newInstance(EnumSet<StrategyValue> enumSet) {
        return new StrategyEnumerator(enumSet);
    }

    /**
     * Create a {@link Strategy} out of a {@link Serialized}
     * by finding the template that starts
     * with the given keyword and then using its parse method.
     */
    public static Strategy parseStrategy(Grammar rules,
            Serialized source) throws FormatException {
        return INSTANCE.parse(rules, source);
    }

    /** The singleton instance of this class. */
    private final static StrategyEnumerator INSTANCE = newInstance();
    private static final String STRATEGY_TOOLTIP = "<HTML>"
        + "The exploration strategy determines at each state:<BR>"
        + "<B>1.</B> Which of the applicable transitions will be taken; "
        + "and<BR>"
        + "<B>2.</B> In which order the reached states will be explored."
        + "</HTML>";
}