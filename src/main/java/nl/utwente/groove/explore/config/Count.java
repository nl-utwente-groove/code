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

/**
 * Feature values for {@link ExploreKey#COUNT}: the number of results after
 * which exploration stops. Exploration always stops when the frontier is
 * empty, regardless of this setting.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum Count implements Setting.Kind {
    /** Continue until the entire state space has been explored. */
    ALL("all", "Exploration continues until the entire state space has been explored",
        Setting.ContentType.NULL),
    /** Stop as soon as a single result has been found. */
    FIRST("first", "Exploration stops as soon as a single result has been found",
        Setting.ContentType.NULL),
    /** Stop as soon as the given number of results (larger than 1) has been found. */
    COUNT("value", "Exploration stops as soon as the given number of results has been found",
        Setting.ContentType.INTEGER),;

    private Count(String name, String explanation, Setting.ContentType contentType) {
        this.name = name;
        this.explanation = explanation;
        this.contentType = contentType;
    }

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
    public Setting.ContentType contentType() {
        return this.contentType;
    }

    private final Setting.ContentType contentType;
}
