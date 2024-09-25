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
package nl.utwente.groove.control.template;

import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.control.Attempt;

/**
 * Vector of switches in combination with the success and failure alternates.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class SwitchAttempt extends Attempt<Location,NestedSwitch> implements Relocatable {
    /** Constructs a switch attempt for a given source location.
     * @param propertyCount the number of property switches (which are always the first in the list)
     */
    public SwitchAttempt(Location source, Location onSuccess, Location onFailure, int switchCount,
                         int propertyCount, Stream<NestedSwitch> switches) {
        super(switchCount);
        this.source = source;
        this.propertyCount = propertyCount;
        setSuccess(onSuccess);
        setFailure(onFailure);
        assert source.getTemplate().equals(onSuccess.getTemplate());
        assert source.getTemplate().filter(t -> t.getLocations().contains(source)).isPresent();
        switches.forEach(s -> add(s));
    }

    /** Returns the source location of this switch attempt. */
    public Location source() {
        return this.source;
    }

    private final Location source;

    /** Indicates if this switch only tests for properties. */
    public boolean isPropertiesOnly() {
        return getPropertyCount() == size();
    }

    /** Indicates if this switch has a positive property count. */
    public boolean hasProperties() {
        return getPropertyCount() > 0;
    }

    /** Returns the number of switches in this attempt that are property invocations.
     * The property switches are always the first in the list.
     */
    public int getPropertyCount() {
        return this.propertyCount;
    }

    /** The number of switches in this attempt that are property invocations. */
    private final int propertyCount;

    @Override
    public SwitchAttempt relocate(Relocation map) {
        Location newSource = map.get(source());
        Location newSuccess = map.get(onSuccess());
        Location newFailure = map.get(onFailure());
        return new SwitchAttempt(newSource, newSuccess, newFailure, size(), getPropertyCount(),
            stream().map(s -> s.relocate(map)));
    }
}
