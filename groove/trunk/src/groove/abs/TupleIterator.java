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
 * $Id: TupleIterator.java,v 1.1 2007-11-28 15:35:07 iovka Exp $
 */
package groove.abs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * An iterator over a cartesian product. Sets defining the cartesian product
 * have values from Val and are indexed by a finite index set I \subseteq Idx.
 * The cartesian product is defined as a mapping m : I --> Set<Val>, where I is
 * a finite set of elements of Idx, and for each i in I, m(i) is a finite set.
 * Consider as example the mapping m defined by (i1 -> {a1, a2}, i2 -> {b1}, i3 ->
 * {c1, c2, c3}). Then this iterator should iterate over the maps (i1 -> a1, i2 ->
 * b1, i3 -> c1), (i1 -> a2, i2 -> b1, i3 -> c1), (i1 -> a1, i2 -> b1, i3 ->
 * c2), (i1 -> a2, i2 -> b1, i3 -> c2), (i1 -> a1, i2 -> b1, i3 -> c3), (i1 ->
 * a2, i2 -> b1, i3 -> c3) in some order. Maps returned by the next() can share
 * elements. Thus, these elements should not be further alised. The maps
 * returned should not be modified.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class TupleIterator<Idx,Val> implements Iterator<Map<Idx,Val>> {

    /**
     * Used to order the index set, necessary for iteration over a Cartesian
     * product.
     */
    private final ArrayList<Idx> iset;

    /**
     * The inner iterators. iterators.get(i) iterates over the set
     * m(iset.get(i)).
     */
    private ArrayList<Iterator<Val>> iterators;

    /**
     * The value to be returned by next(). Whenever consumed,
     * <code>consumed</code> is set tu null.
     * @see #consumed
     */
    private final Map<Idx,Val> nextVal;

    /**
     * Set to true when nextVal has been consumed by a call of next().
     * @invariant !hasNext implies cosumed
     */
    private boolean consumed;

    /**
     * Indicates whether the iterator has more elements. Set first by the
     * constructor, then by the computeNext() method. When !hasNext, calls of
     * next() throw NoSuchElementException
     * @see #computeNext()
     */
    private boolean hasNext;

    /** The mapping defining the Cartesian product. Given at construction time. */
    private final Mapping<Idx,Val> m;

    /**
     * Construct a tuple iterator for a given mapping.
     * @param m the mapping defining the cartesian product.
     */
    public TupleIterator(Mapping<Idx,Val> m) {
        this.iset = new ArrayList<Idx>(m.size());
        this.iterators = new ArrayList<Iterator<Val>>(m.size());
        this.nextVal = new HashMap<Idx,Val>(m.size());
        this.m = m;
        int k = 0;
        for (Idx i : m.keySet()) {
            this.iset.add(k, i);
            Iterator<Val> it = m.itFor(i);
            this.iterators.add(it);
            try {
                this.nextVal.put(i, it.next()); // NoSuchElementException
                                                // possible
            } catch (NoSuchElementException e) { // the inner iterator it is
                                                    // empty, thus this iterator
                                                    // is also empty
                this.hasNext = false;
                this.consumed = true;
                this.nextVal.clear();
                this.iterators = null;
                return;
            }
            k++;
        }
        this.hasNext = true;
        this.consumed = false;
    }

    public boolean hasNext() {
        computeNext();
        return this.hasNext;
    }

    public Map<Idx,Val> next() {
        computeNext();
        if (!this.hasNext) {
            throw new NoSuchElementException();
        }
        this.consumed = true;
        return new HashMap<Idx,Val>(this.nextVal);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Goes to the next value, if it exists. Sets the correct values for nextVal
     * and hasNext. Successive calls of computeNext() do not have effect if no
     * call of next() is
     */
    private void computeNext() {
        if (!this.hasNext) {
            return;
        } // no need of computing any more
        if (!this.consumed) {
            return;
        } // no need to compute because next() was not called since the last
            // computation
        // find the first iterator that can be incremented
        int k = 0;
        while (k < this.iset.size()) { // && (! iterators.get(k).hasNext())) {
            if (this.iterators.get(k).hasNext()) {
                break;
            }
            // for all iterators that do not have a next element, reinitialise
            // them
            this.iterators.set(k, this.m.itFor(this.iset.get(k)));
            this.nextVal.put(this.iset.get(k), this.iterators.get(k).next());
            k++;
        }
        // k is the index of the first iterator that can be incremented
        try {
            this.nextVal.put(this.iset.get(k), this.iterators.get(k).next());
            this.consumed = false;
        } catch (IndexOutOfBoundsException e) { // no more iterators to be
                                                // incremented
            this.hasNext = false;
            this.consumed = true;
            this.nextVal.clear();
            this.iterators = null;
            return;
        }
    }

    /** Represents the underlying mapping. */
    public interface Mapping<I,V> {
        /** itFor(d) is an iterator over the set m(d). */
        Iterator<V> itFor(I i);

        /** The size of the index set. */
        int size();

        /** The domain of this mapping. */
        Collection<I> keySet();
    }

    /** Testing method */
    public static void main(String args[]) {

        class MappingImpl implements Mapping<Integer,String> {

            ArrayList<String> s1, s2, s3;

            MappingImpl() {
                this.s1 = new ArrayList<String>(2);
                this.s1.add("a1");
                this.s1.add("a2");
                this.s2 = new ArrayList<String>(1);
                this.s2.add("b1");
                this.s3 = new ArrayList<String>(3);
                this.s3.add("c1");
                this.s3.add("c2");
                this.s3.add("c3");
            }

            public Iterator<String> itFor(Integer i) {
                switch (i) {
                case 1:
                    return this.s1.iterator();
                case 2:
                    return this.s2.iterator();
                case 3:
                    return this.s3.iterator();
                default:
                    throw new NoSuchElementException();
                }
            }

            public Collection<Integer> keySet() {
                ArrayList<Integer> result = new ArrayList<Integer>(3);
                result.add(1);
                result.add(2);
                result.add(3);
                return result;
            }

            public int size() {
                return 3;
            }

            @Override
            public String toString() {
                return "(a1, a2) X (b1) X (c1, c2, c3)";
            }

        }

        class MappingImplEmptySet implements Mapping<Integer,String> {

            ArrayList<String> s1, s2;

            MappingImplEmptySet() {
                this.s1 = new ArrayList<String>(2);
                this.s1.add("a1");
                this.s1.add("a2");
                this.s2 = new ArrayList<String>(1);
            }

            public Iterator<String> itFor(Integer i) {
                switch (i) {
                case 1:
                    return this.s1.iterator();
                case 2:
                    return this.s2.iterator();
                default:
                    throw new NoSuchElementException();
                }
            }

            public Collection<Integer> keySet() {
                ArrayList<Integer> result = new ArrayList<Integer>(3);
                result.add(1);
                result.add(2);
                result.add(3);
                return result;
            }

            public int size() {
                return 2;
            }
        }

        class MappingImplEmptyMap implements Mapping<Integer,String> {

            public Iterator<String> itFor(Integer i) {
                throw new NoSuchElementException();
            }

            public Collection<Integer> keySet() {
                return new ArrayList<Integer>();
            }

            public int size() {
                return 0;
            }
        }

        MappingImpl cartesian = new MappingImpl();
        System.out.println("Cartesian product for :");
        System.out.println(cartesian);
        TupleIterator<Integer,String> tit =
            new TupleIterator<Integer,String>(cartesian);
        while (tit.hasNext()) {
            System.out.println(tit.next());
        }
        System.out.println("*************************");
        tit = new TupleIterator<Integer,String>(new MappingImplEmptySet());
        while (tit.hasNext()) {
            System.out.println(tit.next());
        }
        System.out.println("*************************");
        tit = new TupleIterator<Integer,String>(new MappingImplEmptyMap());
        while (tit.hasNext()) {
            System.out.println(tit.next());
        }

        System.out.println("FINISHED");
    }

}