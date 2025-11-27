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

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.graph.Label;
import nl.utwente.groove.util.line.Line;

/** Type of the keys in a label filter. */
@NonNullByDefault
public interface LabelEntry extends Comparable<LabelEntry> {
    /** Retrieves the label content of this entry. */
    public Label getContent();

    /** Retrieves the line of the entry. */
    public Line getLine();

    /** Indicates if this entry is currently selected. */
    public boolean isSelected();

    /** Sets the selection status to a given value.
     * The return value indicates if the selection status was changed.
     */
    public boolean setSelected(boolean selected);

    /** Indicates if this entry is passive, i.e., it
     * does not enforce itse selection status on its parent or children.
     */
    public default boolean isPassive() {
        return false;
    }

    /** Signals that this is a filter entry for nodes. */
    public boolean isForNode();

    /** Indicates if a given label corresponds to the one wrapped in this {@link LabelEntry}. */
    public boolean matches(Label label);
}