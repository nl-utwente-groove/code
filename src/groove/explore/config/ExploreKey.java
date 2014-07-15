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
import groove.util.PropertyKey;

/**
 * Key type of the exploration configuration.
 * @author Arend Rensink
 */
public enum ExploreKey implements PropertyKey<SettingList> {
    /** The basic search strategy. */
    STRATEGY("strategy", "Basic exploration strategy", StrategyKind.DEPTH_FIRST, true),
    /** The acceptor for results. */
    RANDOM("random", "Pick random successor of explored state?", BooleanKey.FALSE, true),
    /** The acceptor for results. */
    ACCEPTOR("accept", "Acceptor for result values", AcceptorKind.FINAL, true),
    /** The matching strategy. */
    MATCHER("match", "Match strategy", MatchKind.PLAN, true),
    /** The algebra for data values. */
    ALGEBRA("algebra", "Algebra for data values", AlgebraKind.DEFAULT, true),
    /** Collapsing of isomorphic states. */
    ISO("iso", "Collapse isomorphic states?", BooleanKey.TRUE, true),
    /** Conditions for where to stop exploring. */
    //BOUNDARY("bound", "Boundary conditions for exploration", null, false),
    /** Number of results after which to stop exploring. */
    COUNT("count", "Number of results before halting; 0 means unbounded", CountKind.ALL, true), ;

    private ExploreKey(String name, String explanation, SettingKey defaultKind, boolean singular) {
        this.name = name;
        this.keyPhrase = Groove.unCamel(name, false);
        this.explanation = explanation;
        this.defaultKind = defaultKind;
        this.singular = singular;
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
    public SettingListParser parser() {
        if (this.parser == null) {
            this.parser = new SettingListParser(this);
        }
        return this.parser;
    }

    private SettingListParser parser;

    @Override
    public SettingList getDefaultValue() {
        if (this.defaultValue == null) {
            this.defaultValue = parser().getDefaultValue();
        }
        return this.defaultValue;
    }

    private SettingList defaultValue;

    /** Returns the default setting kind for this exploration key. */
    public SettingKey getDefaultKind() {
        return this.defaultKind;
    }

    private final SettingKey defaultKind;

    /** Returns the type of the setting key for this explore key. */
    public Class<? extends SettingKey> getKindType() {
        return getDefaultKind().getClass();
    }

    @Override
    public boolean isValue(Object value) {
        return parser().isValue(value);
    }

    /**
     * Indicates if this key has a singular value.
     */
    public boolean isSingular() {
        return this.singular;
    }

    private final boolean singular;
}
