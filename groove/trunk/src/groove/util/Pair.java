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
 * $Id: Pair.java,v 1.4 2008-01-30 09:32:13 iovka Exp $
 */
package groove.util;

/**
 * Implements a generic pair of values.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Pair<T,U> {
    /** Constructs a pair with given first and second fields. */
    public Pair(final T first, final U second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first value of the pair.
     */
    public T first() {
        return this.first;
    }

    /**
     * Returns the second value of the pair.
     */
    public U second() {
        return this.second;
    }

    /**
     * Tests for the equality of the {@link #first()} and {@link #second()}
     * fields.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Pair<?,?> && equalsFirst((Pair<?,?>) obj)
            && equalsSecond((Pair<?,?>) obj);
    }

    /** Tests if the {@link #first()} field of this pair equals that of another. */
    protected boolean equalsFirst(Pair<?,?> other) {
        if (this.first == null) {
            return other.first == null;
        } else {
            return this.first.equals(other.first);
        }
    }

    /** Tests if the {@link #second()} field of this pair equals that of another. */
    protected boolean equalsSecond(Pair<?,?> other) {
        if (this.second == null) {
            return other.second == null;
        } else {
            return this.second.equals(other.second);
        }
    }

    /**
     * This implementation uses the hash codes of the {@link #first()} and
     * {@link #second()} fields.
     */
    @Override
    public int hashCode() {
        int firstHash = this.first == null ? 0 : this.first.hashCode();
        int secondHash = this.second == null ? 0 : this.second.hashCode();
        return firstHash ^ (secondHash << 1);
    }

    @Override
    public String toString() {
        return String.format("<%s,%s>", this.first, this.second);
    }

    /**
     * Factory method for generically creating typed pairs.
     * @param <TT> type capture of the first parameter
     * @param <UU> type capture of the second parameter
     * @param first first element of the new pair
     * @param second second element of the new pair
     * @return a new typed pair for with the given values
     */
    public static <TT,UU> Pair<TT,UU> createPair(TT first, UU second) {
        return new Pair<TT,UU>(first, second);
    }

    /** The first value of the pair. */
    private final T first;
    /** The second value of the pair. */
    private final U second;
}