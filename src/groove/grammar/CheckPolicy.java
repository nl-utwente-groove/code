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
package groove.grammar;

/**
 * Policy for dealing with run-time checks,
 * i.e. for typing errors and invariant and forbidden properties.
 */
public enum CheckPolicy {
    /** No checking occurs. */
    NONE("none"),
    /** Violations are errors. */
    ERROR("error"),
    /** Violations cause absence. */
    ABSENCE("absence"), ;

    private CheckPolicy(String name) {
        this.name = name;
    }

    /**
     * Returns the overruling policy of this and another.
     * @param other another policy; may be {@code null}.
     */
    public CheckPolicy max(CheckPolicy other) {
        if (other == null || compareTo(other) > 0) {
            return other;
        } else {
            return this;
        }
    }

    /** Returns the name of this policy. */
    public String getName() {
        return this.name;
    }

    private final String name;
}
