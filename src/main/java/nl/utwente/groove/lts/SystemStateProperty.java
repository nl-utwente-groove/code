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
package nl.utwente.groove.lts;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.lts.Status.Flag;

/**
 * State property that depends on its position in the LTS.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public enum SystemStateProperty implements StateProperty {
    /** Start state. */
    START("start", "Initial state", null),
    /** Final state, i.e., fully explored and no outgoing transitions. */
    FINAL("final", "Final state: fully explored, no outgoing transitions", Flag.FINAL),
    /** Result state, i.e., part of the outcome of an analysis. */
    RESULT("result", "Result of the last exploration", Flag.RESULT),;

    SystemStateProperty(String name, String description, @Nullable Flag flag) {
        this.name = PREFIX + name;
        this.description = description;
        this.flag = flag;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public String getDescription() {
        return this.description;
    }

    private final String description;

    /** Status flag corresponding to this state property. */
    private final @Nullable Flag flag;

    @Override
    public boolean test(GraphState t) {
        var flag = this.flag;
        if (flag == null) {
            assert this == START;
            return t instanceof StartGraphState;
        } else {
            return t.hasFlag(flag);
        }
    }

    /** Returns the system state property for a given name, if any. */
    public static final SystemStateProperty get(String name) {
        return propertyMap.get(name);
    }

    /** Checks if a system state property with a given name exists. */
    public static final boolean has(String name) {
        return propertyMap.containsKey(name);
    }

    /** Mapping from system property names to system properties. */
    private static final Map<String,SystemStateProperty> propertyMap = new LinkedHashMap<>();

    static {
        for (var prop : values()) {
            propertyMap.put(prop.getName(), prop);
        }
    }
}
