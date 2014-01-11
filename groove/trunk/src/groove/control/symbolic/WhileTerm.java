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
 * While-do term.
 * @author Arend Rensink
 * @version $Revision $
 */
public class WhileTerm extends Term {
    /**
     * Constructor for subclassing.
     */
    public WhileTerm(Op op, Term arg0) {
        super(op, arg0);
    }

    /**
     * Constructs a while-do term.
     */
    public WhileTerm(Term arg0) {
        this(Op.WHILE, arg0);
    }

    @Override
    protected List<OutEdge> computeOutEdges() {
        List<OutEdge> result = new ArrayList<OutEdge>();
        for (OutEdge edge : arg0().getOutEdges()) {
            result.add(edge.newEdge(edge.getTarget().seq(this)));
        }
        return result;
    }

    @Override
    protected Term computeSuccess() {
        Term result = null;
        if (arg0().hasSuccess()) {
            result = arg0().getSuccess().seq(this);
        } else if (!arg0().isFinal()) {
            result = delta();
        }
        return result;
    }

    @Override
    protected Term computeFailure() {
        Term result = null;
        if (arg0().hasFailure()) {
            result = arg0().getFailure().seq(this).ifNoElse();
        } else if (!arg0().isFinal()) {
            result = epsilon();
        }
        return result;
    }

    @Override
    protected int computeTransitDepth() {
        return 0;
    }

    @Override
    protected boolean computeFinal() {
        return false;
    }

    @Override
    public boolean hasClearFinal() {
        // By definition, while only terminates if
        // it cannot do any transitions
        return true;
    }
}
