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

import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.TextLabel;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.line.Line.Style;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
public class StatePropertyLabel extends TextLabel {
    /** Constructs a label from a given state property. */
    public StatePropertyLabel(StateProperty prop) {
        super(prop.getName(), EdgeRole.FLAG);
        this.prop = prop;
    }

    /** Returns the description of the state property wrapped in this label. */
    public String getDescription() {
        return this.prop.getDescription();
    }

    /** The state property wrapped in this label. */
    public StateProperty prop() {
        return this.prop;
    }

    private final StateProperty prop;

    @Override
    public Line toLine() {
        return super.toLine().style(Style.UNDERLINE);
    }
}
