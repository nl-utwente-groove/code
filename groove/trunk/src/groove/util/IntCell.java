// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: IntCell.java,v 1.1.1.2 2007-03-20 10:42:59 kastenberg Exp $
 */
package groove.util;

/**
 * 
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class IntCell implements Cloneable {
    /**
     * Returns the current value of this cell.
     */
    public int getValue() {
        return value;
    }

    /**
     * Increments the value in this cell and returns the incremented value.
     */
    public int inc() {
        value++;
        return value;
    }

    /**
     * Decrements the value in this cell and returns the decremented value.
     */
    public int dec() {
        value--;
        return value;
    }

    /**
     * Takes the exclusive-or of the value of this cell with a given number.
     * Both stores and resturns the resulting value.
     * @param i the number to be combined with the value of this cell
     * @return the changed value
     */
    public int xor(int i) {
        return value ^= i;
    }

    /**
     * Adds an increment to the value of this cell.
     * Both stores and resturns the resulting value.
     * @param i the increment to be added to the value of this cell
     * @return the incremented value
     */
    public int add(int i) {
        return value += i;
    }

    /**
     * Multiplies the value of this cell fwith a given factor.
     * Both stores and resturns the resulting value.
     * @param i the factor to be multiplied with the value of this cell
     * @return the multiplied value
     */
    public int mult(int i) {
        return value *= i;
    }
    
    /**
     * Chences the value of this cell.
     * @param i the new value
     */
    public void setValue(int i) {
        value = i;
    }

    // --------------------------- Object overrides -----------------------
    
    public boolean equals(Object obj) {
        return (obj instanceof IntCell && ((IntCell) obj).value == value);
    }

    public int hashCode() {
        return value;
    }
    
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exc) {
            return null;
        }
    }

    public String toString() {
        return ""+value;
    }
    
    private int value;
}
