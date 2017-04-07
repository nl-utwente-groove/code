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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import groove.control.CtrlPar.Var;
import groove.grammar.rule.RuleNode;

/**
 * Class wrapping the signature of a rule, i.e., the list of parameters.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Signature implements Iterable<Var> {
    /**
     * Creates an empty signature.
     */
    public Signature() {
        this.pars = new ArrayList<>();
    }

    /**
     * Creates a signature from a list of variables.
     */
    public Signature(List<Var> pars) {
        this.pars = new ArrayList<>(pars);
    }

    /** Returns the list of all parameters. */
    public List<Var> getPars() {
        return this.pars;
    }

    /** Returns the parameter at a given index. */
    public Var getPar(int i) {
        return this.pars.get(i);
    }

    /** Returns the rule node corresponding to the parameter at a given index. */
    public RuleNode getNode(int i) {
        return getPar(i).getRuleNode();
    }

    private final List<Var> pars;

    @Override
    public Iterator<Var> iterator() {
        return this.pars.iterator();
    }

    /** Returns a stream over the variables in this signature. */
    public Stream<Var> stream() {
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
        Signature other = (Signature) obj;
        if (!this.pars.equals(other.pars)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.pars.toString();
    }
}
