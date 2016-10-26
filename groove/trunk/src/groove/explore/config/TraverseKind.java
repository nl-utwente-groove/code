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

import groove.util.parse.Parser;

/**
 * Kind of traversal strategies.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum TraverseKind implements SettingKey, Setting<TraverseKind,Null> {
    /** Depth-first search. */
    DEPTH_FIRST("DFS", "Depth-first search"),
    /** Breadth-first search. */
    BREADTH_FIRST("BFS", "Breadth-first search"),
    /** Linear search. */
    LINEAR("Linear", "Linear search: never backtracks"),
    /** Best-first search, driven by some heuristic. */
    BEST_FIRST("Heuristic", "Heuristic search according to a given function"),
    /** LTL model checking, driven by some property to be checked. */
    //LTL("LTL", "LTL model checking of a given formula"),
    ;

    private TraverseKind(String name, String explanation) {
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
    public String getContentName() {
        return null;
    }

    @Override
    public TraverseKind getDefaultSetting() {
        return createSetting(getDefaultValue());
    }

    @Override
    public TraverseKind createSettting() {
        return this;
    }

    @Override
    public TraverseKind createSetting(Object content) throws IllegalArgumentException {
        if (content != null) {
            throw new IllegalArgumentException();
        }
        return this;
    }

    @Override
    public TraverseKind getKind() {
        return this;
    }

    @Override
    public Null getContent() {
        return null;
    }

    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    @Override
    public Parser<Null> parser() {
        return Null.PARSER;
    }
}
