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
package nl.utwente.groove.explore.config;

import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.ParsableKey;

/**
 * Key type of the exploration configuration.
 * The key determines the kind of setting.
 * @author Arend Rensink
 */
public enum ExploreKey implements ParsableKey<Setting> {
    /** The search traversal strategy. */
    TRAVERSE("traverse", "State space traversal strategy", Traversal.Kind.DEPTH_FIRST, true),
    /** Model checking settings. */
    CHECKING("checking", "Model checking mode", ModelChecking.Kind.NONE, true),
    /** The acceptor for results. */
    RANDOM("random", "Pick random successor of explored state?", Flag.Kind.FALSE, true),
    /** The acceptor for results. */
    ACCEPTOR("accept", "Criterion for results", Acceptor.Key.FINAL, true),
    /** The matching strategy. */
    MATCHER("match", "Match strategy", Matcher.Key.PLAN, true),
    /** The algebra for data values. */
    ALGEBRA("algebra", "Algebra for data values", Algebra.Kind.DEFAULT, true),
    /** Collapsing of isomorphic states. */
    ISO("iso", "Collapse isomorphic states?", Flag.Kind.TRUE, true),
    /** Conditions for where to stop exploring. */
    //BOUNDARY("bound", "Boundary conditions for exploration", null, false),
    /** Number of results after which to stop exploring. */
    COUNT("count", "Result count before exploration halts", Count.Key.ALL, true),;

    private ExploreKey(String name, String explanation, Setting.Key defaultKind, boolean singular) {
        this.name = name;
        this.keyPhrase = Strings.unCamel(name, false);
        this.explanation = explanation;
        assert defaultKind.hasDefault();
        this.defaultKind = defaultKind;
        this.singular = singular;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    /** Short description for user consumption. */
    public String getKeyPhrase() {
        return this.keyPhrase;
    }

    private final String keyPhrase;

    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    @Override
    public SettingParser parser() {
        if (this.parser == null) {
            this.parser = new SettingParser(this);
        }
        return this.parser;
    }

    private SettingParser parser;

    /** Returns the default setting kind for this exploration key. */
    public Setting.Key getDefaultKind() {
        return this.defaultKind;
    }

    private final Setting.Key defaultKind;

    /** Returns the type of the setting key for this explore key. */
    @SuppressWarnings("unchecked")
    public Class<? extends Setting.Key> getKindType() {
        return getDefaultKind().getClass();
    }

    /**
     * Indicates if this key has a singular value.
     */
    public boolean isSingular() {
        return this.singular;
    }

    private final boolean singular;

    /** Returns a mapping from strings to corresponding values of this key's setting kind. */
    public SettingKeyMap getKindMap() {
        if (this.kindMap == null) {
            this.kindMap = new SettingKeyMap(getKindType());
            switch (this) {
            case COUNT:
                this.kindMap.put("", Count.Key.COUNT);
                break;
            case ISO:
            case RANDOM:
                this.kindMap.put("yes", Flag.Kind.TRUE);
                this.kindMap.put("no", Flag.Kind.FALSE);
                break;
            default:
                // no mappings
            }
        }
        return this.kindMap;
    }

    private SettingKeyMap kindMap;
}
