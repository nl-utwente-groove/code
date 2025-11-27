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
package nl.utwente.groove.gui.tree;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.lts.StateProperty;
import nl.utwente.groove.util.line.Line;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
public class LTSEntry implements LabelEntry {
    /** Constructs an initially selected fresh label entry from a given label. */
    protected LTSEntry(Label label) {
        this.content = label;
        this.line = label.toLine();
        this.role = label.getRole();
        this.selected = true;
        if (this.role == EdgeRole.BINARY) {
            this.type = Type.TRANSITION_LABEL;
        } else if (StateProperty.isProperty(label.text())) {
            this.type = Type.STATE_PROPERTY;
        } else {
            this.type = Type.GRAPH_CONDITION;
        }
    }

    @Override
    public @NonNull Label getContent() {
        return this.content;
    }

    private final Label content;

    @Override
    public Line getLine() {
        return this.line;
    }

    /** The label wrapped in this entry. */
    private final Line line;

    @Override
    public boolean isForNode() {
        return this.role == EdgeRole.NODE_TYPE;
    }

    private final EdgeRole role;

    /** Returns the type of entry. */
    Type getType() {
        return this.type;
    }

    private final Type type;

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public boolean setSelected(boolean selected) {
        boolean result = this.selected != selected;
        this.selected = selected;
        return result;
    }

    /** Flag indicating if this entry is currently selected. */
    private boolean selected;

    @Override
    public boolean matches(Label label) {
        return label.getRole() == this.role && label.text().equals(toString());
    }

    @Override
    public int hashCode() {
        return getContent().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof LTSEntry other)) {
            return false;
        }
        return getContent().equals(other.getContent());
    }

    @Override
    public String toString() {
        return this.content.text();
    }

    @Override
    public int compareTo(LabelEntry o) {
        var other = (LTSEntry) o;
        var result = this.role.compareTo(other.role);
        if (result == 0) {
            var thisText = toString();
            var otherText = other.toString();
            // state properties come before other properties
            boolean thisProperty = StateProperty.isProperty(thisText);
            boolean otherProperty = StateProperty.isProperty(otherText);
            if (thisProperty != otherProperty) {
                result = thisProperty
                    ? -1
                    : +1;
            } else {
                result = thisText.compareTo(otherText);
            }
        }

        return result;
    }

    /** Entry type. */
    enum Type {
        /** User-defined state property (see {@link StateProperty}) */
        STATE_PROPERTY("State properties"),
        /** Graph condition */
        GRAPH_CONDITION("Graph conditions"),
        /** Binary transition */
        TRANSITION_LABEL("Transition labels");

        Type(String name) {
            this.name = name;
        }

        /** The name of this entry type. */
        public String getName() {
            return this.name;
        }

        private final String name;
    }
}
