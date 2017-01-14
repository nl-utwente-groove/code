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
 * $Id: TraverseKind.java 5828 2017-01-10 18:04:04Z rensink $
 */
package groove.explore.config;

import groove.util.parse.Parser;
import groove.util.parse.StringParser;

/**
 * Condition for the bound on the frontier beyond which exploration will not go.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum BoundKind implements SettingKey {
    /** There is no bound. */
    NONE("none", "There is no bound"),
    /** The bound equals the path cost. */
    COST("cost", "The bound equals the path cost"),
    /** The bound equals the path cost. */
    SIZE("size", "The bound is given by graph size"),
    /** The bound equals the path cost. */
    COUNTS("counts", "The bound is determined by element counts"),;

    private BoundKind(String name, String explanation) {
        this.name = name;
        this.explanation = explanation;
    }

    /** Returns the name of this search order. */
    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    @Override
    public Parser<? extends Object> parser() {
        return StringParser.identity();
    }

    @Override
    public String getContentName() {
        return "Base and increment";
    }
}
