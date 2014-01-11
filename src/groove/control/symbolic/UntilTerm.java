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
 * Until-do term.
 * @author Arend Rensink
 * @version $Revision $
 */
public class UntilTerm extends Term {
    /**
     * Constructs an until-do term.
     */
    UntilTerm(Term arg0, Term arg1) {
        super(Op.UNTIL, arg0, arg1);
    }

    @Override
    protected List<OutEdge> computeOutEdges() {
        return arg0().getOutEdges();
    }

    @Override
    protected Term computeSuccess() {
        Term result = null;
        if (arg0().hasSuccess()) {
            result = arg0().getSuccess();
        }
        return result;
    }

    @Override
    protected Term computeFailure() {
        Term result = null;
        if (arg0().hasFailure()) {
            result = arg0().getFailure().ifElse(arg1().seq(this));
        } else if (!arg0().isFinal()) {
            result = arg1().seq(this);
        }
        return result;
    }

    @Override
    protected int computeTransitDepth() {
        return 0;
    }

    @Override
    protected boolean computeFinal() {
        return arg0().isFinal();
    }

    @Override
    public boolean hasClearFinal() {
        return arg0().hasClearFinal();
    }
}
