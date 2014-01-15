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
     * The argument must satisfy {@link #hasClearFinal()}.
     */
    public TransitTerm(Term arg0) {
        super(Op.TRANSIT, arg0);
        assert arg0.hasClearFinal();
    }

    @Override
    protected List<OutEdge> computeOutEdges() {
        return makeTransit(arg0().computeOutEdges());
    }

    @Override
    protected Term computeSuccess() {
        Term result = null;
        if (arg0().hasSuccess()) {
            result = arg0().getSuccess().transit();
        }
        return result;
    }

    @Override
    protected Term computeFailure() {
        Term result = null;
        if (arg0().hasFailure()) {
            result = arg0().getFailure().transit();
        }
        return result;
    }

    @Override
    protected int computeTransitDepth() {
        return arg0().getTransitDepth() + 1;
    }

    @Override
    protected boolean computeFinal() {
        return false;
    }

    @Override
    public boolean hasClearFinal() {
        // this term has a clear final location because that is demanded of the argument
        return true;
    }
}
