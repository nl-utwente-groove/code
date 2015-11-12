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
package groove.control.instance;

import groove.control.Attempt;

/**
 * Attempt of a {@link Frame}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StepAttempt extends Attempt<Frame,Step> {
    /**
     * Creates a constraint attempt, initialised with a verdict alternate.
     * The verdict alternate serves both for success and for failure.
     */
    StepAttempt(Frame onVerdict) {
        setSuccess(onVerdict);
        setFailure(onVerdict);
        this.constraint = true;
    }

    /**
     * Creates an attempt, initialised with success and failure alternates.
     */
    StepAttempt(Frame onSuccess, Frame onFailure) {
        setSuccess(onSuccess);
        setFailure(onFailure);
        this.constraint = false;
    }

    /** Indicates if this is a constraint attempt. */
    public boolean isConstraint() {
        return this.constraint;
    }

    private final boolean constraint;
}
