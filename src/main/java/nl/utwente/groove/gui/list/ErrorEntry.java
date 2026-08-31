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
package nl.utwente.groove.gui.list;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.ResourceProperties;
import nl.utwente.groove.grammar.model.ErrorLocation;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatError;

/**
 * Selectable list entry wrapping a {@link FormatError}, deriving the
 * selection information from the error's context (see {@link ErrorLocation}).
 * @author Arend Rensink
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class ErrorEntry implements SelectableListEntry {
    /** Constructs an entry wrapping a given error. */
    public ErrorEntry(FormatError error) {
        this.error = error;
        this.location = ErrorLocation.of(error);
    }

    /** Returns the error wrapped in this entry. */
    public FormatError getError() {
        return this.error;
    }

    /** The wrapped error. */
    private final FormatError error;

    /** The location information derived from the error context. */
    private final ErrorLocation location;

    @Override
    public @Nullable ResourceKind getResourceKind() {
        return this.location.kind();
    }

    @Override
    public SortedSet<QualName> getResourceNames() {
        return this.location.names();
    }

    @Override
    public Collection<Element> getElements() {
        return this.location.elements();
    }

    @Override
    public ResourceProperties.@Nullable Key getPropertyKey() {
        return this.location.key();
    }

    /** Delegates to the wrapped error, whose message is what the list displays. */
    @Override
    public String toString() {
        return this.error.toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof ErrorEntry other && this.error.equals(other.error);
    }

    @Override
    public int hashCode() {
        return this.error.hashCode();
    }

    /** Wraps a collection of errors into a list of entries. */
    public static List<ErrorEntry> wrap(Collection<FormatError> errors) {
        return errors.stream().map(ErrorEntry::new).toList();
    }
}
