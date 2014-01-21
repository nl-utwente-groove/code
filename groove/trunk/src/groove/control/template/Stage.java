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
 * Slot within a control location, corresponding to the part of the
 * attempt of that location that has been completed.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Stage implements Position<Stage> {
    /** Constructs a location slot with the given arguments. */
    public Stage(TemplatePosition pos, int nr, boolean success) {
        assert nr < pos.getAttempt().size();
        this.pos = pos;
        this.nr = nr;
        this.success = success;
        this.last = nr == pos.getAttempt().size() - 1;
    }

    public Type getType() {
        return this.pos.getType();
    }

    public boolean isDead() {
        return this.pos.isDead();
    }

    public boolean isFinal() {
        return this.pos.isFinal();
    }

    public boolean isTrial() {
        return this.pos.isTrial();
    }

    public int getDepth() {
        return this.pos.getDepth();
    }

    public StageSwitch getAttempt() {
        return this.pos.getAttempt().getStage(this.nr);
    }

    public Stage onFailure() {
        assert getPosition() instanceof Location;
        Stage result;
        // go to the next position only if this is the last slot
        if (this.last) {
            TemplatePosition resultPos =
                this.success ? this.pos.onSuccess() : this.pos.onFailure();
            result = resultPos.getFirstStage();
        } else {
            result =
                ((Location) getPosition()).getStage(this.nr + 1, this.success);
        }
        return result;
    }

    public Stage onSuccess() {
        assert getPosition() instanceof Location;
        Stage result;
        // go to the next position only if this is the last slot
        if (this.last) {
            result = this.pos.onSuccess().getFirstStage();
        } else {
            result = ((Location) getPosition()).getStage(this.nr + 1, true);
        }
        return result;
    }

    /** Returns the template position of which this is a stage. */
    public TemplatePosition getPosition() {
        return this.pos;
    }

    private final TemplatePosition pos;
    private final int nr;
    private final boolean success;
    private final boolean last;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.nr;
        result = prime * result + this.pos.hashCode();
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
        if (!this.pos.equals(other.pos)) {
            return false;
        }
        if (this.success != other.success) {
            return false;
        }
        return true;
    }
}
