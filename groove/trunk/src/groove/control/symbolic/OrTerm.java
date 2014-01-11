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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class OrTerm extends Term {
    /**
     * Constructs the choice of two control terms.
     */
    OrTerm(Term arg0, Term arg1) {
        super(Term.Op.OR, arg0, arg1);
        assert arg0.isTopLevel();
        assert arg1.isTopLevel();
    }

    @Override
    protected List<OutEdge> computeOutEdges() {
        List<OutEdge> result = new ArrayList<OutEdge>();
        if (arg0().hasSuccess() || !arg1().hasSuccess()) {
            result.addAll(arg0().getOutEdges());
        }
        if (!arg0().hasSuccess()) {
            result.addAll(arg1().getOutEdges());
        }
        return result;
    }

    @Override
    protected Term computeSuccess() {
        Term result = null;
        if (arg0().hasSuccess()) {
            result = arg0().getSuccess().or(arg1());
        } else if (arg1().hasSuccess()) {
            result = arg0().or(arg1().getSuccess());
        }
        return result;
    }

    @Override
    protected Term computeFailure() {
        Term result = null;
        if (arg0().hasFailure()) {
            result = arg0().getFailure().or(arg1());
        } else if (arg1().hasFailure()) {
            result = arg0().or(arg1().getFailure());
        }
        return result;
    }

    @Override
    protected int computeTransitDepth() {
        return 0;
    }

    @Override
    protected boolean computeFinal() {
        return arg0().isFinal() || arg1().isFinal();
    }

    @Override
    public boolean hasClearFinal() {
        boolean result = !(isFinal() && hasOutEdges());
        if (result) {
            result = arg0().hasClearFinal() && arg1().hasClearFinal();
        }
        return result;
    }
}
