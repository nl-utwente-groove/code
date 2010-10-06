/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.control;

/**
 * Control variables, consisting of a name and a type.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlVar {
    /** Constructs a control variable with a given (non-{@code null}) name and type. */
    public CtrlVar(String name, CtrlType type) {
        super();
        this.name = name;
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.name.hashCode();
        result = prime * result + this.type.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CtrlVar other = (CtrlVar) obj;
        if (!this.name.equals(other.name)) {
            return false;
        }
        if (!this.type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /** Returns the name of this control variable. */
    public String getName() {
        return this.name;
    }

    /** Returns the type of this control variable. */
    public CtrlType getType() {
        return this.type;
    }

    /** The name of this control variable. */
    private final String name;
    /** The type of this control variable. */
    private final CtrlType type;
}
