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

package groove.io.conceptual;

/**
 * Class to represent a Triple of elements, used to compare instances of 
 * EReferences since they are not objects themselves. A Triple of
 * (source EClass instance, EReference, target EClass instance) is used to
 * represent and compare them instead.
 * 
 * Taken implementation for Tuple of 2 elements from a website with anonymous
 * author, and changed to implementation for a Triple of elements.
 * 
 * @author Anonymous
 *
 */
public class Triple<L,M,R> {

    private final L left;
    private final M middle;
    private final R right;

    /**
     * @return right element
     */
    public R getRight() {
        return this.right;
    }

    /**
     * @return middle element
     */
    public M getMiddle() {
        return this.middle;
    }

    /**
     * @return left element
     */
    public L getLeft() {
        return this.left;
    }

    /**
     * Constructor method for a new Triple
     */
    public Triple(final L left, final M middle, final R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    /**
     * @return new Triple with left, middle and right elements
     */
    public static <A,B,C> Triple<A,B,C> create(A left, B middle, C right) {
        return new Triple<>(left, middle, right);
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Triple)) {
            return false;
        }

        final Triple<?,?,?> other = (Triple<?,?,?>) o;
        return equal(getLeft(), other.getLeft())
            && equal(getRight(), other.getRight())
            && equal(getMiddle(), other.getMiddle());
    }

    /**
     * @return true if Triples contain the same elements
     */
    public static final boolean equal(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    @Override
    public int hashCode() {
        int hLeft = getLeft() == null ? 0 : getLeft().hashCode();
        int hRight = getRight() == null ? 0 : getRight().hashCode();
        int hMiddle = getMiddle() == null ? 0 : getMiddle().hashCode();

        return hLeft + (57 * hRight + (37 * hMiddle));
    }
}
