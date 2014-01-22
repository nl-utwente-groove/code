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
package groove.control.template;

import groove.control.Position;

/**
 * Stage of a control location, corresponding to the part of the
 * attempt of that location that has been completed.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Stage implements Position<Stage> {
    /** Constructs a location stage with the given arguments. */
    public Stage(Location loc, int nr, boolean success) {
        this.loc = loc;
        this.nr = nr;
        this.success = success;
    }

    public Type getType() {
        return this.loc.getType();
    }

    public boolean isDead() {
        return this.loc.isDead();
    }

    public boolean isFinal() {
        return this.loc.isFinal();
    }

    public boolean isTrial() {
        return this.loc.isTrial();
    }

    public int getDepth() {
        return this.loc.getDepth();
    }

    public Switch getAttempt() {
        return this.loc.getAttempt().getStage(this.nr, this.success);
    }

    /** Returns the template location of which this is a stage. */
    public Location getLocation() {
        return this.loc;
    }

    private final Location loc;
    private final int nr;
    private final boolean success;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.nr;
        result = prime * result + this.loc.hashCode();
        result = prime * result + (this.success ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Stage)) {
            return false;
        }
        Stage other = (Stage) obj;
        if (this.nr != other.nr) {
            return false;
        }
        if (!this.loc.equals(other.loc)) {
            return false;
        }
        if (this.success != other.success) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "" + this.loc + "/" + this.nr + "/" + this.success;
    }
}
