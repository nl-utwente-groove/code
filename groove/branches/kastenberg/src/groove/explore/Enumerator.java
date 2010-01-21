/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.explore;

/**
 * An enumerator class for objects of type Documented<A>.
 * Default implementation is the empty enumeration.
 * Subclasses must override the constructor to initialize the enumeration.
 * 
 * @author Maarten de Mol
 * @version $Revision $
 */
public class Enumerator<A> {
    private Object[] array;
    private int nrElements;

    /**
     * Default constructor. Initializes to empty enumeration.
     */
    public Enumerator() {
        this.nrElements = 0;
        this.array = new Object[100];
    }

    /**
     * Add a Documented<A> to the enumerator.
     * @param object - the object to be added
     */
    public void addObject(Documented<A> object) {
        this.nrElements++;
        this.array[this.nrElements - 1] = object;
    }

    /**
     * @return The element at the stored index.
     */
    @SuppressWarnings("unchecked")
    public Documented<A> getElementAt(int index) {
        return ((Documented<A>) this.array[index]);
    }

    /**
     * @return The number of stored Documented<A>'s.
     */
    public int getSize() {
        return this.nrElements;
    }

    /**
     * @return array with the names of the stored Documented<A>'s.
     */
    public String[] getAllNames() {
        String[] result = new String[this.nrElements];
        for (int i = 0; i < this.nrElements; i++) {
            result[i] = getElementAt(i).getName();
        }
        return result;
    }

    /**
     * Find a Documented<A> with the specified keyword.
     * @param keyword - the keyword
     * @return the object associated with the keyword (null if not found)
     */
    public Documented<A> findByKeyword(String keyword) {
        for (int i = 0; i < this.nrElements; i++) {
            if (getElementAt(i).getKeyword().equals(keyword)) {
                return getElementAt(i);
            }
        }
        return null;
    }

    /**
     * Find a Documented<A> with the specified name.
     * @param name - the name
     * @return the object associated with the name (null if not found)
     */
    public Documented<A> findByName(String name) {
        for (int i = 0; i < this.nrElements; i++) {
            if (getElementAt(i).getName().equals(name)) {
                return getElementAt(i);
            }
        }
        return null;
    }

    /**
     * Find a Documented<A> with the specified explanation.
     * @param explanation - the explanation
     * @return the object associated with the explanation (null if not found)
     */
    public Documented<A> findByExplanation(String explanation) {
        for (int i = 0; i < this.nrElements; i++) {
            if (getElementAt(i).getExplanation().equals(explanation)) {
                return getElementAt(i);
            }
        }
        return null;
    }
}
