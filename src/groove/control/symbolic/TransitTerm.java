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
package groove.control.symbolic;

import java.util.List;

/**
 * Term with increased transient depth.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TransitTerm extends Term {
    /**
     * Creates a term with increased transient depth.
     */
    public TransitTerm(Term arg0) {
        super(Op.TRANSIT, arg0);
    }

    @Override
    protected List<TermAttempt> computeAttempts() {
        List<TermAttempt> result = null;
        if (isTrial()) {
            result = createAttempts();
            for (TermAttempt edge : arg0().getAttempts()) {
                result.add(edge.newAttempt(edge.target().transit()));
            }
        }
        return result;
    }

    @Override
    protected Term computeSuccess() {
        Term result = null;
        if (arg0().isTrial()) {
            result = arg0().onSuccess().transit();
        }
        return result;
    }

    @Override
    protected Term computeFailure() {
        Term result = null;
        if (arg0().isTrial()) {
            result = arg0().onFailure().transit();
        }
        return result;
    }

    @Override
    protected int computeDepth() {
        if (arg0().isFinal()) {
            return 0;
        } else {
            return arg0().getDepth() + 1;
        }
    }

    @Override
    protected Type computeType() {
        return arg0().getType();
    }
}
