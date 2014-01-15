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
 * Sequential composition.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SeqTerm extends Term {
    /**
     * Constructs the sequential composition of two control terms.
     */
    SeqTerm(Term arg0, Term arg1) {
        super(Term.Op.SEQ, arg0, arg1);
        assert arg1.isTopLevel();
    }

    @Override
    protected List<OutEdge> computeOutEdges() {
        List<OutEdge> result = new ArrayList<OutEdge>();
        for (OutEdge out : arg0().getOutEdges()) {
            result.add(out.newEdge(out.getTarget().seq(arg1())));
        }
        if (arg0().isFinal()) {
            result.addAll(arg1().getOutEdges());
        }
        return result;
    }

    @Override
    protected Term computeSuccess() {
        Term result = null;
        if (arg0().isFinal()) {
            result = arg1().getSuccess();
        } else if (arg0().hasSuccess()) {
            result = arg0().getSuccess().seq(arg1());
        }
        return result;
    }

    @Override
    protected Term computeFailure() {
        Term result = null;
        if (arg0().isFinal()) {
            result = arg1().getFailure();
        } else if (arg0().hasFailure()) {
            result = arg0().getFailure().seq(arg1());
        }
        return result;
    }

    @Override
    protected int computeTransitDepth() {
        return arg0().getTransitDepth();
    }

    @Override
    protected boolean computeFinal() {
        return arg0().isFinal() && arg1().isFinal();
    }

    @Override
    public boolean hasClearFinal() {
        return (arg0().hasClearFinal() || !arg1().isFinal())
            && arg1().hasClearFinal();
    }
}
