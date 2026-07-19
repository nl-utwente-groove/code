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
package nl.utwente.groove.explore.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.ParsableKey;

/**
 * Keys of the exploration configuration: one per feature (dimension of
 * variation) of the exploration feature model. Each key has an associated
 * enumeration of feature values (its kind type) and a default kind.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum ExploreKey implements ParsableKey<Setting> {
    /** Selection of the next state to explore, from the frontier. */
    NEXT("next", "Selection of the next state to be explored", NextState.OLDEST),
    /** Selection of the successors to be generated for the explored state. */
    SUCCESSOR("successor", "Selection of the successor states to be generated", Successor.ALL),
    /** Size restriction on the exploration frontier. */
    FRONTIER("frontier", "Size restriction on the exploration frontier", Frontier.COMPLETE),
    /** Quality function guiding the selection of the next state. */
    HEURISTIC("heuristic", "Quality function guiding the selection of the next state",
        Heuristic.NONE),
    /** Cost of a single transition. */
    COST("cost", "Cost of a single transition", Cost.NONE),
    /** Condition determining when a result has been found. */
    GOAL("goal", "Condition determining when a result has been found", Goal.FINAL),
    /** Desired outcome of the goal condition. */
    OUTCOME("outcome", "Whether the goal condition is to be satisfied or violated",
        Outcome.SATISFY),
    /** Type of result the exploration yields. */
    RESULT("result", "Type of result the exploration yields", Result.STATE),
    /** Number of results after which exploration halts. */
    COUNT("count", "Number of results after which exploration halts", Count.ALL),
    /** Bound on the states to be explored. */
    BOUND("bound", "Bound on the states to be explored", Bound.NONE),
    /** Degree to which discovered states are stored. */
    PERSISTENCE("persistence", "Degree to which discovered states are stored in the GTS",
        Persistence.ALL),
    /** Condition under which a fresh state equals a known state. */
    COLLAPSE("collapse", "Condition under which a fresh state is considered equal to a known state",
        Collapse.GRAMMAR),
    /** Engine used to find rule matches. */
    MATCHER("matcher", "Engine used to find rule matches", Matcher.PLAN),
    /** Interpretation of data values. */
    ALGEBRA("algebra", "Interpretation of data values", Algebra.GRAMMAR),;

    private ExploreKey(String name, String explanation, Setting.Kind defaultKind) {
        this.name = name;
        this.keyPhrase = Strings.unCamel(name, false);
        this.explanation = explanation;
        this.defaultKind = defaultKind;
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

    /** Returns the default kind for this exploration key. */
    public Setting.Kind getDefaultKind() {
        return this.defaultKind;
    }

    private final Setting.Kind defaultKind;

    /** Returns the default setting for this exploration key. */
    public Setting getDefaultSetting() {
        return getDefaultKind().createSetting();
    }

    /** Returns the enumeration type of the kinds of this key. */
    public Class<? extends Setting.Kind> getKindType() {
        return getDefaultKind().getClass();
    }

    @Override
    public SettingParser parser() {
        var result = this.parser;
        if (result == null) {
            this.parser = result = new SettingParser(this);
        }
        return result;
    }

    private @Nullable SettingParser parser;

    /** Returns the mapping from names to kinds of this key. */
    public SettingKindMap getKindMap() {
        var result = this.kindMap;
        if (result == null) {
            result = new SettingKindMap(getKindType());
            switch (this) {
            case COUNT -> result.put("", Count.COUNT);
            case FRONTIER -> result.put("", Frontier.BEAM);
            default -> {
                // no additional mappings
            }
            }
            this.kindMap = result;
        }
        return result;
    }

    private @Nullable SettingKindMap kindMap;
}
