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

import java.util.function.Predicate;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Label;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public sealed interface StateProperty extends Predicate<GraphState>
    permits UserStateProperty, SystemStateProperty {
    /** Returns the name of this property.
     * This is guaranteed to start with #PREFIX followed by a non-empty identifier not starting with #PREFIX.
     */
    public String getName();

    /**
     * Returns a HTML-formatted, user-oriented description of this property.
     */
    public String getDescription();

    /** Returns a label describing this property.
     * The label is a flag (meaning it is set italic) consisting of the state property name,
     * which is, moreover, underlined.
     */
    default public Label getLabel() {
        return new StatePropertyLabel(this);
    }

    /** Indicates if this is a system property. */
    default public boolean isSystem() {
        return this instanceof SystemStateProperty;
    }

    /** Default prefix of all property names. */
    static public final String PREFIX = "$";

    /** Tests if a given name is a state property name
     * (meaning that it starts with {@link #PREFIX}).
     */
    static public boolean isStateProperty(String name) {
        return name.startsWith(PREFIX);
    }

    /** Tests if a given label denotes a state property
     * (meaning that its text starts with {@link #PREFIX}).
     */
    static public boolean isStateProperty(Label label) {
        return isStateProperty(label.text());
    }

    /** Tests if a given name is a state property name
     * (meaning that it is one of the names of {@link SystemStateProperty}).
     */
    static public boolean isSystemStateProperty(String name) {
        return SystemStateProperty.has(name);
    }

    /** Tests if a given label denotes a state property
     * (meaning that its text starts with {@link #PREFIX}).
     */
    static public boolean isSystemStateProperty(Label label) {
        return isSystemStateProperty(label.text());
    }
}
