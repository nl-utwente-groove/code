/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package nl.utwente.groove.util.parse;

import java.util.Collection;
import java.util.SortedSet;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.GraphProperties.Key;

/** Interface for entries of a selectable list, such as the GUI's error and
 * search-result panels. */
public interface SelectableListEntry {
    /** Returns the resource kind for which this entry occurs. */
    public @Nullable ResourceKind getResourceKind();

    /** Returns the resource name for which this entry occurs. */
    public @NonNull SortedSet<QualName> getResourceNames();

    /** Returns the list of elements in which the entry occurs. May be empty. */
    public @NonNull Collection<Element> getElements();

    /** Returns the property key in which the entry occurs. May be {@code null}. */
    default public @Nullable Key getPropertyKey() {
        return null;
    }
}
