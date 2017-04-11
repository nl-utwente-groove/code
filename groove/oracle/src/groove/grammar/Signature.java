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
package groove.grammar;

import static groove.grammar.aspect.AspectKind.PARAM_ASK;
import static groove.grammar.aspect.AspectKind.PARAM_BI;
import static groove.grammar.aspect.AspectKind.PARAM_IN;
import static groove.grammar.aspect.AspectKind.PARAM_OUT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import groove.control.CtrlPar;
import groove.control.CtrlPar.Var;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.grammar.Signature.UnitPar;
import groove.grammar.aspect.AspectKind;
import groove.grammar.rule.RuleNode;
import groove.grammar.rule.VariableNode;

/**
 * Class wrapping the signature of a rule, i.e., the list of parameters.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Signature<P extends UnitPar> implements Iterable<P> {
    /**
     * Creates an empty signature.
     */
    public Signature() {
        this.pars = new ArrayList<>();
    }

    /**
     * Creates a signature from a list of variables.
     */
    public Signature(List<P> pars) {
        this.pars = new ArrayList<>(pars);
    }

    /** Returns the list of all parameters. */
    public List<P> getPars() {
        return this.pars;
    }

    /** Returns the parameter at a given index. */
    public P getPar(int i) {
        return this.pars.get(i);
    }

    private final List<P> pars;

    @Override
    public Iterator<P> iterator() {
        return this.pars.iterator();
    }

    /** Returns a stream over the variables in this signature. */
    public Stream<P> stream() {
        return this.pars.stream();
    }

    /** Returns the number of parameters in the signature. */
    public int size() {
        return this.pars.size();
    }

    /** Indicates that this is an empty signature. */
    public boolean isEmpty() {
        return this.pars.isEmpty();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.pars.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Signature)) {
            return false;
        }
        Signature<?> other = (Signature<?>) obj;
        if (!this.pars.equals(other.pars)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('(');
        for (UnitPar par : this) {
            if (result.length() > 1) {
                result.append(',');
            }
            if (par.isOutOnly()) {
                result.append(OUT_PREFIX);
                result.append(' ');
            } else if (par.isAsk()) {
                result.append(BI_PREFIX);
                result.append(' ');
            } else if (par.isBidirectional()) {
                result.append(BI_PREFIX);
                result.append(' ');
            }
            result.append(par.getType());
        }
        result.append(')');
        return result.toString();
    }

    /** Prefix used to indicate output-only parameters. */
    public static final String OUT_PREFIX = "out";
    /** Prefix used to indicate user-provided parameters. */
    public static final String ASK_PREFIX = "ask";
    /** Prefix used to indicate bidirectional parameters. */
    public static final String BI_PREFIX = "inout";

    /**
     * Convenience method to construct a parameter with a given name, type and direction.
     * @param scope defining scope of the variable; possibly {@code null}
     */
    public static ProcedurePar par(QualName scope, String name, CtrlType type, boolean isOut) {
        return new ProcedurePar(new CtrlVar(scope, name, type), isOut);
    }

    /** Class encoding a formal unit parameter. */
    public static abstract class UnitPar {
        /**
         * Returns the control type of this parameter.
         */
        public abstract CtrlType getType();

        /**
         * Indicates whether this parameter is input-only.
         * A parameter is either input-only, output-only, user-provided, or bidirectional.
         */
        public abstract boolean isInOnly();

        /**
         * Indicates whether this parameter is output-only.
         * A parameter is either input-only, output-only, user-provided, or bidirectional.
         */
        public abstract boolean isOutOnly();

        /** Indicates that the value of this variable should be user-provided
         * (i.e., obtained through an oracle).
         * A parameter is either input-only, output-only, user-provided, or bidirectional.
         */
        public boolean isAsk() {
            return false;
        }

        /**
         * Indicates whether this parameter is bidirectional.
         * A parameter is either input-only, output-only, user-provided, or bidirectional.
         */
        public boolean isBidirectional() {
            return false;
        }

        /**
         * Tests whether this variable parameter,
         * when used as a formal parameter, is compatible with a given
         * control argument.
         * Compatibility refers to direction and type
         * @param arg the control argument to test against; non-{@code null}
         * @return if <code>true</code>, this variable is compatible with {@code arg}
         */
        public boolean compatibleWith(CtrlPar arg) {
            CtrlType argType = arg.getType();
            if (argType != null && !getType().equals(argType)) {
                return false;
            }
            if (isInOnly()) {
                return arg.isInOnly();
            }
            if (isOutOnly() || isAsk()) {
                return !arg.isInOnly();
            }
            assert isBidirectional();
            return true;
        }
    }

    /** Class encoding a formal action parameter. */
    public static class ProcedurePar extends UnitPar {
        /**
         * Constructs a new formal action parameter.
         * @param var the control variable declared by this parameter
         * @param isOut flag indicating whether this is an output parameter
         */
        public ProcedurePar(CtrlVar var, boolean isOut) {
            this.var = var;
            this.isOut = isOut;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof CtrlVar) {
                return this.var.equals(obj);
            }
            if (!(obj instanceof Var)) {
                return false;
            }
            Var other = (Var) obj;
            return isOutOnly() == other.isOutOnly() && isInOnly() == other.isInOnly()
                && getVar().equals(other.getVar());
        }

        @Override
        public CtrlType getType() {
            return getVar().getType();
        }

        /** Returns the control variable declared by this procedure parameter. */
        public CtrlVar getVar() {
            return this.var;
        }

        @Override
        public int hashCode() {
            int result = isInOnly() ? 0 : isOutOnly() ? 1 : 2;
            result = result * 31 + getVar().hashCode();
            return result;
        }

        /**
         * Indicates whether this parameter is input-only.
         * A parameter is either input-only, output-only, or bidirectional.
         */
        @Override
        public boolean isInOnly() {
            return !this.isOut;
        }

        /**
         * Indicates whether this parameter is output-only.
         * A parameter is either input-only, output-only, or bidirectional.
         */
        @Override
        public boolean isOutOnly() {
            return this.isOut;
        }

        @Override
        public String toString() {
            String result = isOutOnly() ? "out " : "";
            result += getVar().toString();
            return result;
        }

        /** The control variable declared by this procedure parameter. */
        private final CtrlVar var;
        /** Flag indicating if this is an input or output parameter. */
        private final boolean isOut;

    }

    /** Class encoding a formal action parameter. */
    public static class RulePar extends UnitPar {
        /**
         * Constructs a new formal action parameter.
         * @param kind the kind of parameter; determines the directionality
         * @param node the associated rule node
         */
        public RulePar(AspectKind kind, RuleNode node, boolean creator) {
            assert kind.isParam();
            this.kind = kind;
            this.ruleNode = node;
            this.creator = creator;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof RulePar)) {
                return false;
            }
            RulePar other = (RulePar) obj;
            return getKind().equals(other.getKind()) && getNode().equals(other.getNode());
        }

        /**
         * Returns the control type of this parameter.
         */
        @Override
        public CtrlType getType() {
            if (this.ruleNode instanceof VariableNode) {
                return CtrlType.getType(((VariableNode) this.ruleNode).getSort());
            } else {
                return CtrlType.NODE;
            }
        }

        @Override
        public int hashCode() {
            int result = getKind().hashCode();
            result = result * 31 + getNode().hashCode();
            return result;
        }

        /** Returns the directionality of this parameter.
         * @return one of {@link #PARAM_IN}, {@link #PARAM_OUT} or {@link #PARAM_BI}
         */
        public AspectKind getKind() {
            return this.kind;
        }

        private final AspectKind kind;

        @Override
        public boolean isInOnly() {
            return getKind() == PARAM_IN;
        }

        @Override
        public boolean isOutOnly() {
            return isCreator() && getKind() == PARAM_BI || getKind() == PARAM_OUT;
        }

        @Override
        public boolean isAsk() {
            return getKind() == PARAM_ASK;
        }

        @Override
        public boolean isBidirectional() {
            return !isCreator() && getKind() == PARAM_BI;
        }

        @Override
        public String toString() {
            String result = isOutOnly() ? "!" : isInOnly() ? "?" : "";
            result += getNode().getId()
                .toString();
            return result;
        }

        /** Returns the (possibly {@code null} rule node in this parameter. */
        public RuleNode getNode() {
            return this.ruleNode;
        }

        /** Indicates if this is a rule parameter corresponding to a creator node. */
        public final boolean isCreator() {
            return this.creator;
        }

        /** Associated node if this is a rule parameter. */
        private final RuleNode ruleNode;
        /** Flag indicating if this is a rule parameter referring to a creator node. */
        private final boolean creator;
    }
}
