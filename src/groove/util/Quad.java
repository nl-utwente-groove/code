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
 * Implements a generic quadruple of values.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Quad<T,U,V,W> implements Fixable {
    /** Constructs a quad with given first, second, third and fourth fields. */
    public Quad(final T one, final U two, final V three, final W four) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    /**
     * Returns the first value of the quad.
     */
    public T one() {
        return this.one;
    }

    /**
     * Returns the second value of the quad.
     */
    public U two() {
        return this.two;
    }

    /**
     * Returns the third value of the quad.
     */
    public V three() {
        return this.three;
    }

    /**
     * Returns the fourth value of the quad.
     */
    public W four() {
        return this.four;
    }

    /** Changes the first value of the quad. */
    public T setOne(T one) {
        assert !isFixed() : "Can't set a value after the quad is fixed.";
        T result = this.one;
        this.one = one;
        return result;
    }

    /** Changes the second value of the quad. */
    public U setTwo(U two) {
        assert !isFixed() : "Can't set a value after the quad is fixed.";
        U result = this.two;
        this.two = two;
        return result;
    }

    /** Changes the third value of the quad. */
    public V setThree(V three) {
        assert !isFixed() : "Can't set a value after the quad is fixed.";
        V result = this.three;
        this.three = three;
        return result;
    }

    /** Changes the fourth value of the quad. */
    public W setFour(W four) {
        assert !isFixed() : "Can't set a value after the quad is fixed.";
        W result = this.four;
        this.four = four;
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
        return obj instanceof Quad<?,?,?,?> && equalsOne((Quad<?,?,?,?>) obj)
            && equalsTwo((Quad<?,?,?,?>) obj) && equalsThree((Quad<?,?,?,?>) obj)
            && equalsFour((Quad<?,?,?,?>) obj);
    }

    /** Tests if the {@link #one()} field of this quad equals that of another. */
    protected boolean equalsOne(Quad<?,?,?,?> other) {
        if (this.one == null) {
            return other.one == null;
        } else {
            return this.one.equals(other.one);
        }
    }

    /** Tests if the {@link #two()} field of this quad equals that of another. */
    protected boolean equalsTwo(Quad<?,?,?,?> other) {
        if (this.two == null) {
            return other.two == null;
        } else {
            return this.two.equals(other.two);
        }
    }

    /** Tests if the {@link #three()} field of this quad equals that of another. */
    protected boolean equalsThree(Quad<?,?,?,?> other) {
        if (this.three == null) {
            return other.three == null;
        } else {
            return this.three.equals(other.three);
        }
    }

    /** Tests if the {@link #four()} field of this quad equals that of another. */
    protected boolean equalsFour(Quad<?,?,?,?> other) {
        if (this.four == null) {
            return other.four == null;
        } else {
            return this.four.equals(other.four);
        }
    }

    /**
     * This implementation uses the hash codes of the {@link #one()},
     * {@link #two()}, {@link #three()} and {@link #four()} fields.
     */
    @Override
    final public int hashCode() {
        if (this.hashCode == 0) {
            int prime = 31;
            int result = this.one == null ? 0 : this.one.hashCode();
            result = result * prime + (this.two == null ? 0 : this.two.hashCode());
            result = result * prime + (this.three == null ? 0 : this.three.hashCode());
            result = result * prime + (this.four == null ? 0 : this.four.hashCode());
            this.hashCode = result;
            if (this.hashCode == 0) {
                this.hashCode = 1;
            }
        }
        return this.hashCode;
    }

    @Override
    public String toString() {
        return String.format("<%s,%s,%s,%s>", this.one, this.two, this.three, this.four);
    }

    /**
     * Factory method for generically creating typed quads.
     * @param <TT> type capture of the first parameter
     * @param <UU> type capture of the second parameter
     * @param <VV> type capture of the third parameter
     * @param <WW> type capture of the fourth parameter
     * @param one first element of the new quad
     * @param two second element of the new quad
     * @param three third element of the new quad
     * @param four fourth element of the new quad
     * @return a new typed quad with the given values
     */
    public static <TT,UU,VV,WW> Quad<TT,UU,VV,WW> newQuad(TT one, UU two, VV three, WW four) {
        return new Quad<>(one, two, three, four);
    }

    /** The precomputed hash code. The quad is fixed iff this value is not 0. */
    private int hashCode;
    /** The first value of the quad. */
    private T one;
    /** The second value of the quad. */
    private U two;
    /** The third value of the quad. */
    private V three;
    /** The fourth value of the quad. */
    private W four;
}