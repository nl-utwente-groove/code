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
package groove.explore.config;

import java.util.function.Function;

import groove.lts.ActionLabel;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransition;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class CostSetting extends DefaultSetting<CostKind,Function<ActionLabel,Integer>> {
    /** Constructs the setting for {@link CostKind#NONE}. */
    private CostSetting(CostKind kind) {
        super(kind, kind == CostKind.NONE ? (lab -> 0) : (lab -> 1));
    }

    /**
     * @param function cost function returning an integer for every action label.
     */
    public CostSetting(Function<ActionLabel,Integer> function) {
        super(CostKind.RULE, function);
    }

    /** Computes the cost of reaching a given graph state,
     * based on the action labels in the trace from start state to that graph state.
     * @param state the state whose cost is to be computed.
     */
    public Integer compute(GraphState state) {
        int result = 0;
        while (state instanceof GraphNextState) {
            GraphTransition trans = ((GraphNextState) state);
            result += getContent().apply(trans.label());
            state = trans.source();
        }
        return result;
    }

    /** The singleton setting for {@link CostKind#NONE}. */
    static public final CostSetting NONE_SETTING = new CostSetting(CostKind.NONE);
    /** The singleton setting for {@link CostKind#UNIFORM}. */
    static public final CostSetting UNIFORM_SETTING = new CostSetting(CostKind.UNIFORM);
}
