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

import groove.grammar.UnitPar.ProcedurePar;
import groove.util.parse.FormatException;

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

    /** Tests if this signature has parameters of a given direction. */
    public boolean has(UnitPar.Direction dir) {
        return stream().anyMatch(p -> p.getDirection() == dir);
    }

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
            result.append(par.getDirection()
                .prefix(par.getType()
                    .toString()));
        }
        result.append(')');
        return result.toString();
    }

    /** Attempts to parse a given string as a comma-separated list of
     * procedure parameters.
     * @param input the input string to be parsed
     * @return a signature constructed from the parsed parameters
     * @throws FormatException if the input string cannot be parsed
     */
    static public Signature<ProcedurePar> parse(String input) throws FormatException {
        List<ProcedurePar> pars = new ArrayList<>();
        int comma;
        do {
            comma = input.indexOf(',');
            if (comma >= 0) {
                String next = input.substring(0, comma);
                pars.add(UnitPar.parse(next));
                input = input.substring(comma + 1);
            }
        } while (comma >= 0);
        pars.add(UnitPar.parse(input));
        return new Signature<>(pars);
    }
}
