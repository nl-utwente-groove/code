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
 * Kleene-starred term.
 * @author Arend Rensink
 * @version $Revision $
 */
public class StarTerm extends Term {
    /**
     * Constructs a Kleene-starred term.
     */
    public StarTerm(Term arg0) {
        super(Op.STAR, arg0);
        assert arg0.isTopLevel();
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
            result = arg0().getSuccess().seq(this).or(epsilon());
        }
        return result;
    }

    @Override
    protected Term computeFailure() {
        Term result = null;
        if (arg0().hasFailure()) {
            result = arg0().getFailure().seq(this).or(epsilon());
        }
        return result;
    }

    @Override
    protected int computeTransitDepth() {
        return 0;
    }

    @Override
    protected boolean computeFinal() {
        return true;
    }

    @Override
    public boolean hasClearFinal() {
        return !hasOutEdges();
    }
}
