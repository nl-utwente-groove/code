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
 * $Id: LongCell.java,v 1.3 2008-01-30 09:32:16 iovka Exp $
 */
package groove.util;

/**
 * 
 * @author Arend Rensink
 * @version $Revision$
 */
public class LongCell {
    /**
     * Returns the current value of this cell.
     */
    public long getValue() {
        return value;
    }

    /**
     * Takes the exclusive-or of the value of this cell with a given number.
     * Both stores and resturns the resulting value.
     * @param i the number to be combined with the value of this cell
     * @return the changed value
     */
    public long xor(long i) {
        return value ^= i;
    }

    /**
     * Adds an increment to the value of this cell.
     * Both stores and resturns the resulting value.
     * @param i the increment to be added to the value of this cell
     * @return the incremented value
     */
    public long add(long i) {
        return value += i;
    }

    /**
     * Multiplies the value of this cell fwith a given factor.
     * Both stores and resturns the resulting value.
     * @param i the factor to be multiplied with the value of this cell
     * @return the multiplied value
     */
    public long mult(int i) {
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
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof LongCell && ((LongCell) obj).value == value);
    }

    @Override
    public int hashCode() {
        return (int) value;
    }
    
    private long value;
}
