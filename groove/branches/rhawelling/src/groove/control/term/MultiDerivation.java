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
package groove.control.term;

import groove.control.MultiAttempt;

/**
 * List of term derivations in combination with the success and failure alternates.
 * This constitutes a term attempt.
 * @author Arend Rensink
 * @version $Revision $
 */
public class MultiDerivation extends MultiAttempt<Term,Derivation> {
    @Override
    public Term onSuccess() {
        assert this.onSuccess != null;
        return this.onSuccess;
    }

    /** Sets the success alternate of this attempt. */
    public void setSuccess(Term onSuccess) {
        assert this.onSuccess == null;
        assert onSuccess != null;
        this.onSuccess = onSuccess;
    }

    private Term onSuccess;

    @Override
    public Term onFailure() {
        assert this.onFailure != null;
        return this.onFailure;
    }

    /** Sets the failure alternate of this attempt. */
    public void setFailure(Term onFailure) {
        assert this.onFailure == null;
        assert onFailure != null;
        this.onFailure = onFailure;
    }

    private Term onFailure;
}
