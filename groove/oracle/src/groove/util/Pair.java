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
 * Implements a generic pair of values.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Pair<T,U> implements Fixable {
    /** Constructs a pair with given first and second fields. */
    public Pair(final T one, final U two) {
        this.one = one;
        this.two = two;
    }

    /**
     * Returns the first value of the pair.
     */
    public T one() {
        return this.one;
    }

    /**
     * Returns the second value of the pair.
     */
    public U two() {
        return this.two;
    }

    /** Changes the first value of the pair. */
    public T setOne(T one) {
        assert !isFixed() : "Can't set a value after the pair is fixed.";
        T result = this.one;
        this.one = one;
        return result;
    }

    /** Changes the second value of the pair. */
    public U setTwo(U two) {
        assert !isFixed() : "Can't set a value after the pair is fixed.";
        U result = this.two;
        this.two = two;
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
     * Tests for the equality of the {@link #one()} and {@link #two()}
     * fields.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof Pair<?,?> && equalsOne((Pair<?,?>) obj) && equalsTwo((Pair<?,?>) obj);
    }

    /** Tests if the {@link #one()} field of this pair equals that of another. */
    protected boolean equalsOne(Pair<?,?> other) {
        if (this.one == null) {
            return other.one == null;
        } else {
            return this.one.equals(other.one);
        }
    }

    /** Tests if the {@link #two()} field of this pair equals that of another. */
    protected boolean equalsTwo(Pair<?,?> other) {
        if (this.two == null) {
            return other.two == null;
        } else {
            return this.two.equals(other.two);
        }
    }

    /**
     * This implementation uses the hash codes of the {@link #one()} and
     * {@link #two()} fields.
     */
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int firstHash = this.one == null ? 0 : this.one.hashCode();
            int secondHash = this.two == null ? 0 : this.two.hashCode();
            this.hashCode = firstHash ^ (secondHash << 1);
            if (this.hashCode == 0) {
                this.hashCode = 1;
            }
        }
        return this.hashCode;
    }

    @Override
    public String toString() {
        return String.format("<%s,%s>", this.one, this.two);
    }

    /**
     * Factory method for generically creating typed pairs.
     * @param <TT> type capture of the first parameter
     * @param <UU> type capture of the second parameter
     * @param one first element of the new pair
     * @param two second element of the new pair
     * @return a new typed pair for with the given values
     */
    public static <TT,UU> Pair<TT,UU> newPair(TT one, UU two) {
        return new Pair<>(one, two);
    }

    /** The precomputed hash code. The pair is fixed iff this value is not 0. */
    private int hashCode;
    /** The first value of the pair. */
    private T one;
    /** The second value of the pair. */
    private U two;
}