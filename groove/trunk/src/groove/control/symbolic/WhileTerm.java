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
        for (OutEdge edge : super.getOutEdges()) {
            result.add(new OutEdge(edge.getCall(), edge.getTarget().seq(this)));
        }
        return result;
    }

    @Override
    protected Term computeSuccess() {
        Term result = null;
        if (super.hasSuccess()) {
            return super.getSuccess().seq(this);
        }
        return result;
    }

    @Override
    protected Term computeFailure() {
        return epsilon();
    }

    @Override
    protected int computeTransitDepth() {
        return 0;
    }

    @Override
    protected boolean computeFinal() {
        return false;
    }
}
