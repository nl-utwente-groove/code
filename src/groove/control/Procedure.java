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
package groove.control;

import groove.control.CtrlPar.Var;
import groove.grammar.QualName;
import groove.util.Fixable;
import groove.util.Groove;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Control-defined callable unit.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class Procedure implements Callable, Fixable {
    /**
     * Constructor for subclassing.
     * @param fullName name of the unit
     * @param priority priority of the unit
     * @param signature signature of the unit
     * @param controlName control program in which the unit has
     * been declared
     * @param startLine first line in the control program at
     * which the unit declaration starts
     */
    protected Procedure(String fullName, int priority, List<Var> signature,
            String controlName, int startLine) {
        this.fullName = fullName;
        this.priority = priority;
        this.signature = signature;
        this.controlName = controlName;
        this.startLine = startLine;
    }

    public String getLastName() {
        return QualName.getLastName(getFullName());
    }

    public String getFullName() {
        return this.fullName;
    }

    private final String fullName;

    public int getPriority() {
        return this.priority;
    }

    private final int priority;

    public List<Var> getSignature() {
        return this.signature;
    }

    private final List<Var> signature;

    /** Returns the full name of the control program in which this procedure is declared. */
    public String getControlName() {
        return this.controlName;
    }

    private final String controlName;

    /** Returns the start line of this procedure's declaration within the control program. */
    public int getStartLine() {
        return this.startLine;
    }

    private final int startLine;

    /** Sets the body of the procedure. 
     * Should only be invoked once, before the procedure is fixed.
     * The call fixes the function.
     */
    public void setTemplate(Template body) {
        assert body != null && body.getName().equals(getFullName());
        assert body == null && !isFixed();
        this.template = body;
        setFixed();
    }

    /** Returns the body of this procedure. */
    public Template getTemplate() {
        assert isFixed();
        return this.template;
    }

    private Template template;

    /** Sets the control automaton of the procedure. */
    public void setBody(CtrlAut body) {
        assert this.body == null : String.format(
            "%s body of %s already set to %s", getKind().getName(true),
            getFullName(), body);
        this.body = body;
    }

    /** Returns the control automaton of this procedure, if set. */
    public CtrlAut getBody() {
        return this.body;
    }

    private CtrlAut body;

    /**
     * Returns a mapping from input variables occurring in the signature of this procedure
     * to their corresponding indices in the signature.
     */
    public Map<CtrlVar,Integer> getParIxMap() {
        assert isFixed();
        if (this.parIxMap == null) {
            this.parIxMap = computeParIxMap();
        }
        return this.parIxMap;
    }

    private Map<CtrlVar,Integer> computeParIxMap() {
        Map<CtrlVar,Integer> result = new LinkedHashMap<CtrlVar,Integer>();
        for (int i = 0; i < getSignature().size(); i++) {
            CtrlPar.Var par = getSignature().get(i);
            if (par.isInOnly()) {
                result.put(par.getVar(), i);
            }
        }
        return result;
    }

    private Map<CtrlVar,Integer> parIxMap;

    public boolean setFixed() {
        boolean result = this.fixed;
        this.fixed = true;
        return result;
    }

    public boolean isFixed() {
        return this.fixed;
    }

    public void testFixed(boolean fixed) {
        if (fixed != isFixed()) {
            throw new IllegalStateException(String.format(
                "The unit is %sfixed", fixed ? "" : "not "));
        }
    }

    private boolean fixed;

    @Override
    public String toString() {
        return getKind().getName(true) + " " + getFullName()
            + Groove.toString(getSignature().toArray(), "(", ")", ", ");
    }

    @Override
    public int hashCode() {
        return getFullName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Procedure)) {
            return false;
        }
        Procedure other = (Procedure) obj;
        return getFullName().equals(other.getFullName());
    }
}
