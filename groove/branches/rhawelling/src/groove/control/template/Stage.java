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
import groove.control.instance.CallStack;

/**
 * Stage of a control location, corresponding to the part of the
 * attempt of that location that has been completed.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Stage implements Position<Stage> {
    /** Constructs a location stage with the given arguments. 
     * @param caller switch from which the stage is invoked
     */
    public Stage(Location loc, Switch caller, int nr, boolean success) {
        this.loc = loc;
        this.caller = caller;
        this.nr = nr;
        this.success = success;
    }

    @Override
    public Type getType() {
        return this.loc.getType();
    }

    @Override
    public boolean isDead() {
        return this.loc.isDead();
    }

    @Override
    public boolean isFinal() {
        return this.loc.isFinal();
    }

    @Override
    public boolean isTrial() {
        return this.loc.isTrial();
    }

    @Override
    public int getDepth() {
        int result = this.loc.getDepth();
        for (Switch caller : getCallStack()) {
            result += caller.source().getDepth();
        }
        return result;
    }

    @Override
    public Switch getAttempt() {
        return this.loc.getAttempt().getStage(this.caller, this.nr, this.success);
    }

    /** Returns the template location of which this is a stage. */
    public Location getLocation() {
        return this.loc;
    }

    private final Location loc;

    /** Indicates if this stage was created from a procedure call switch.
     * @see #getCaller()
     */
    public boolean hasCaller() {
        return getCaller() == null;
    }

    /** Returns the (possibly {@code null}) procedure call switch from which
     * this stage was created. */
    public Switch getCaller() {
        return this.caller;
    }

    private final Switch caller;

    /** Returns the call stack of this stage.
     */
    public CallStack getCallStack() {
        if (this.callStack == null) {
            this.callStack = new CallStack(getCaller());
        }
        return this.callStack;
    }

    /** List of callers, from bottom to top. */
    private CallStack callStack;

    private final int nr;
    private final boolean success;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.nr;
        result = prime * result + this.loc.hashCode();
        result = prime * result + (this.caller == null ? 0 : this.caller.hashCode());
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
        if (this.caller == null) {
            if (other.caller != null) {
                return false;
            }
        } else if (!this.caller.equals(other.caller)) {
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
