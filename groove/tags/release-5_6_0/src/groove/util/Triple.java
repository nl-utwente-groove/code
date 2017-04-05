/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package groove.util;

/**
 * Implements a generic triple of values.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Triple<T,U,V> implements Fixable {
    /** Constructs a triple with given first, second and third fields. */
    public Triple(final T one, final U two, final V three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    /**
     * Returns the first value of the triple.
     */
    public T one() {
        return this.one;
    }

    /**
     * Returns the second value of the triple.
     */
    public U two() {
        return this.two;
    }

    /**
     * Returns the third value of the triple.
     */
    public V three() {
        return this.three;
    }

    /** Changes the first value of the triple. */
    public T setOne(T one) {
        assert !isFixed() : "Can't set a value after the triple is fixed.";
        T result = this.one;
        this.one = one;
        return result;
    }

    /** Changes the second value of the triple. */
    public U setTwo(U two) {
        assert !isFixed() : "Can't set a value after the triple is fixed.";
        U result = this.two;
        this.two = two;
        return result;
    }

    /** Changes the second value of the triple. */
    public V setThree(V three) {
        assert !isFixed() : "Can't set a value after the triple is fixed.";
        V result = this.three;
        this.three = three;
        return result;
    }

    @Override
    public boolean setFixed() {
        boolean result = !isFixed();
        if (result) {
            // the pair is fixed by computing the hash code.
            hashCode();
        }
        return result;
    }

    @Override
    public boolean isFixed() {
        // the pair is fixed as soon as the hash code is computed.
        return this.hashCode != 0;
    }

    /**
     * Tests for the equality of the {@link #one()}, {@link #two()} and {@link #three()}
     * fields.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof Triple<?,?,?> && equalsOne((Triple<?,?,?>) obj)
            && equalsTwo((Triple<?,?,?>) obj) && equalsThree((Triple<?,?,?>) obj);
    }

    /** Tests if the {@link #one()} field of this triple equals that of another. */
    protected boolean equalsOne(Triple<?,?,?> other) {
        if (this.one == null) {
            return other.one == null;
        } else {
            return this.one.equals(other.one);
        }
    }

    /** Tests if the {@link #two()} field of this triple equals that of another. */
    protected boolean equalsTwo(Triple<?,?,?> other) {
        if (this.two == null) {
            return other.two == null;
        } else {
            return this.two.equals(other.two);
        }
    }

    /** Tests if the {@link #three()} field of this triple equals that of another. */
    protected boolean equalsThree(Triple<?,?,?> other) {
        if (this.three == null) {
            return other.three == null;
        } else {
            return this.three.equals(other.three);
        }
    }

    /**
     * This implementation uses the hash codes of the {@link #one()},
     * {@link #two()} and {@link #three()} fields.
     */
    @Override
    final public int hashCode() {
        if (this.hashCode == 0) {
            int prime = 31;
            int result = this.one == null ? 0 : this.one.hashCode();
            result = result * prime + (this.two == null ? 0 : this.two.hashCode());
            result = result * prime + (this.three == null ? 0 : this.three.hashCode());
            this.hashCode = result;
            if (this.hashCode == 0) {
                this.hashCode = 1;
            }
        }
        return this.hashCode;
    }

    @Override
    public String toString() {
        return String.format("%s<%s,%s,%s>",
            getClass().getSimpleName(),
            this.one,
            this.two,
            this.three);
    }

    /**
     * Factory method for generically creating typed triples.
     * @param <TT> type capture of the first parameter
     * @param <UU> type capture of the second parameter
     * @param <VV> type capture of the third parameter
     * @param one first element of the new triple
     * @param two second element of the new triple
     * @param three third element of the new triple
     * @return a new typed triple with the given values
     */
    public static <TT,UU,VV> Triple<TT,UU,VV> newTriple(TT one, UU two, VV three) {
        return new Triple<>(one, two, three);
    }

    /** The precomputed hash code. The triple is fixed iff this value is not 0. */
    private int hashCode;
    /** The first value of the triple. */
    private T one;
    /** The second value of the triple. */
    private U two;
    /** The third value of the triple. */
    private V three;
}