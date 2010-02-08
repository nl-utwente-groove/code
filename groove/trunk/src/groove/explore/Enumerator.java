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

import groove.explore.Serialized.Materialize;
import groove.explore.Serialized.SerializedArgument;

/**
 * An enumerator class for serialized objects.
 * Each object is also accompanied by a descriptive name, and an explanation.  
 * Default implementation is the empty enumeration.
 * Subclasses must override the constructor to initialize the enumeration.
 * 
 * @author Maarten de Mol
 * @version $Revision $
 */
public class Enumerator<A> {
    // Array holding the serialized objects.
    private Object[] objects;
    // Array holding the names of the serialized objects.
    private String[] names;
    // Array holding the explanations of the serialized objects/
    private String[] explanations;
    // Counter for the number of stored elements.
    private int nrElements;

    /**
     * Default constructor. Initializes to empty enumeration.
     */
    public Enumerator() {
        this.nrElements = 0;
        this.objects = new Object[100];
        this.names = new String[100];
        this.explanations = new String[100];
    }

    /**
     * Add an element to the enumerator.
     * @param object - the serialized object to be added
     * @param name - the descriptive name of the object
     * @param explanation - the explanation of the object
     */
    public void addElement(Serialized<A> object, String name, String explanation) {
        this.nrElements++;
        this.objects[this.nrElements - 1] = object;
        this.names[this.nrElements - 1] = name;
        this.explanations[this.nrElements - 1] = explanation;
    }

    /**
     * Add a fixed element (a serialized with no arguments) to the enumerator. 
     */
    public void addElement(String identifier, String name, String explanation,
            A object) {
        addElement(new Serialized<A>(identifier, object), name, explanation);
    }

    /**
     * Add a dynamic element (a serialized with 1 argument) to the enumerator. 
     */
    public void addElement(String identifier, String name, String explanation,
            SerializedArgument arg1, Materialize<A> materialize) {
        addElement(new Serialized<A>(identifier, arg1, materialize), name,
            explanation);
    }

    /**
     * Add a dynamic element (a serialized with 2 arguments) to the enumerator. 
     */
    public void addElement(String identifier, String name, String explanation,
            SerializedArgument arg1, SerializedArgument arg2,
            Materialize<A> materialize) {
        addElement(new Serialized<A>(identifier, arg1, arg2, materialize),
            name, explanation);
    }

    /**
     * @return The number of stored elements.
     */
    public int getSize() {
        return this.nrElements;
    }

    /**
     * @param index - the index of the element
     * @return the serialized object at the indicated index
     */
    @SuppressWarnings("unchecked")
    public Serialized<A> getObjectAt(int index) {
        if (index < 0 || index >= this.nrElements) {
            return null;
        } else {
            return (Serialized<A>) this.objects[index];
        }
    }

    /**
     * @param index - the index of the element
     * @return the keyword at the indicated index
     */
    @SuppressWarnings("unchecked")
    public String getKeywordAt(int index) {
        if (index < 0 || index >= this.nrElements) {
            return null;
        } else {
            return ((Serialized<A>) this.objects[index]).getObjectOnly();
        }
    }

    /**
     * @param index - the index of the element
     * @return the descriptive name at the indicated index
     */
    public String getNameAt(int index) {
        if (index < 0 || index >= this.nrElements) {
            return null;
        } else {
            return this.names[index];
        }
    }

    /**
     * @return pointer to array of names
     */
    public String[] getNameArray() {
        return this.names;
    }

    /**
     * @param index - the index of the element
     * @return the explanation at the indicated index
     */
    public String getExplanationAt(int index) {
        if (index < 0 || index >= this.nrElements) {
            return null;
        } else {
            return this.explanations[index];
        }
    }

    /**
     * Replaces an object with the indicated one.
     * Uses 'getObjectOnly' to find the object. If the object does not
     * exist, no replacement (or addition) takes place.
     */
    @SuppressWarnings("unchecked")
    public void replaceObject(Serialized<A> newObject) {
        for (int i = 0; i < this.nrElements; i++) {
            Serialized<A> stored = (Serialized<A>) this.objects[i];
            if (stored.getObjectOnly().equals(newObject.getObjectOnly())) {
                this.objects[i] = newObject;
            }
        }
    }
}
