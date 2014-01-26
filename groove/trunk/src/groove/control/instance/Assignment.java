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
package groove.control.instance;

import groove.control.Binding;
import groove.control.Binding.Source;
import groove.control.CtrlVar;
import groove.control.Valuator;
import groove.grammar.host.HostNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.PUSH;

/**
 * Action to be taken as part of a {@link Step}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Assignment {
    /**
     * Creates an action with all necessary parameters.
     */
    private Assignment(Kind kind, Map<CtrlVar,Binding> bindings) {
        this(kind, bindings.values());
    }

    /**
     * Creates an action with all necessary parameters.
     */
    private Assignment(Kind kind, Collection<Binding> bindings) {
        this.kind = kind;
        this.bindings = new Binding[bindings.size()];
        bindings.toArray(this.bindings);
    }

    /** Returns the kind of action. */
    public Kind getKind() {
        return this.kind;
    }

    private final Kind kind;

    /** Returns the binding of a given variable index. */
    public Binding getBinding(int index) {
        return this.bindings[index];
    }

    /** Returns the assignment for this action. */
    public Binding[] getBindings() {
        return this.bindings;
    }

    private final Binding[] bindings;

    /** Returns the number of bindings in this assignment. */
    public int size() {
        return this.bindings.length;
    }

    /**
     * Applies this assignment to a given frame valuation.
     * {@link Source#ANCHOR}
     * and {@link Source#CREATOR} are ignored, meaning that the corresponding 
     * values are set to {@code null}.
     */
    public HostNode[] apply(Object[] val) {
        Binding[] bindings = getBindings();
        HostNode[] result = new HostNode[bindings.length];
        Object[] parentValues = Valuator.pop(val);
        for (int i = 0; i < bindings.length; i++) {
            Binding bind = bindings[i];
            int index = bind.getIndex();
            HostNode value;
            switch (bind.getSource()) {
            case ANCHOR:
            case CREATOR:
                value = null;
                break;
            case CALLER:
                assert parentValues != null : String.format(
                    "Can't apply %s: valuation %s does not have parent level", this,
                    Valuator.toString(val));
                value = Valuator.get(parentValues, index);
                break;
            case CONST:
                value = bind.getValue().getNode();
                break;
            case VAR:
                value = Valuator.get(val, index);
                break;
            default:
                assert false;
                value = null;
            }
            result[i] = value;
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.bindings);
        result = prime * result + this.kind.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Assignment)) {
            return false;
        }
        Assignment other = (Assignment) obj;
        if (!Arrays.equals(this.bindings, other.bindings)) {
            return false;
        }
        if (this.kind != other.kind) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.kind.name() + Arrays.toString(this.bindings);
    }

    /** Creates a new {@link PUSH} action with a given assignment. */
    public static Assignment push(List<Binding> bindings) {
        return new Assignment(Kind.PUSH, bindings);
    }

    /** Creates a new {@link Kind#POP} action with a given assignment. */
    public static Assignment pop(List<Binding> bindings) {
        return new Assignment(Kind.POP, bindings);
    }

    /** Creates a new {@link Kind#MODIFY} action with a given assignment. */
    public static Assignment call(List<Binding> bindings) {
        return new Assignment(Kind.MODIFY, bindings);
    }

    /** Kind of {@link Assignment}. */
    public static enum Kind {
        /** Create and initialise a frame instance. */
        PUSH,
        /** Pop a frame instance. */
        POP,
        /** Invoke a rule. */
        MODIFY, ;
    }
}
