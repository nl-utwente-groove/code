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

import groove.util.Groove;
import groove.util.Parser;
import groove.util.PropertyKey;

/**
 * Key type of the exploration configuration.
 * @author Arend Rensink
 */
public enum ExploreKey implements PropertyKey<Setting<?,?>> {
    /** The basic search strategy. */
    STRATEGY("strategy", "Basic exploration strategy", StrategyKey.DEPTH_FIRST),
    /** The acceptor for results. */
    RANDOM("random", "Pick random successor of explored state?", null),
    /** The acceptor for results. */
    ACCEPTOR("accept", "Acceptor for result values", null),
    /** The matching strategy. */
    MATCHER("match", "Match strategy", null),
    /** The algebra for data values. */
    ALGEBRA("algebra", "Algebra for data values", null),
    /** Collapsing of isomorphic states. */
    ISO("iso", "Collapse isomorphic states?", null),
    /** Conditions for where to stop exploring. */
    BOUNDARY("bound", "Acceptor for result values", null),
    /** Number of results after which to stop exploring. */
    COUNT("count", "Number of results before halting; 0 means unbounded", ResultCountKey.COUNT), ;

    private ExploreKey(String name, String explanation, SettingKey defaultKey) {
        this.name = name;
        this.keyPhrase = Groove.unCamel(name, false);
        this.explanation = explanation;
        this.defaultValue = defaultKey.createSetting(defaultKey.getDefaultValue());
        this.parser = new SettingParser(defaultKey);
    }

    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public String getKeyPhrase() {
        return this.keyPhrase;
    }

    private final String keyPhrase;

    @Override
    public boolean isSystem() {
        return false;
    }

    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    @Override
    public Parser<? extends Setting<?,?>> parser() {
        return this.parser;
    }

    private final Parser<? extends Setting<?,?>> parser;

    @Override
    public Setting<?,?> getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public boolean isValue(Object value) {
        return parser().isValue(value);
    }

    private final Setting<?,?> defaultValue;
}
