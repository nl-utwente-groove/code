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

import groove.lts.GraphState;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class BoundSetting extends DefaultSetting<BoundKind,Function<GraphState,Integer>> {
    /** Constructs the setting for {@link BoundKind#NONE}. */
    private BoundSetting() {
        super(BoundKind.NONE, s -> 0);
        this.bound = 0;
        this.increment = 0;
    }

    /** Returns the initial bound. */
    public int getBound() {
        return this.bound;
    }

    private final int bound;

    /** Returns the initial optional increment. */
    public int getIncrement() {
        return this.increment;
    }

    private final int increment;

    /** The singleton setting for {@link BoundKind#NONE}. */
    static public final BoundSetting NONE_SETTING = new BoundSetting();
}
