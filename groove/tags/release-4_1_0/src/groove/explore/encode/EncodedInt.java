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
package groove.explore.encode;

import groove.gui.Simulator;
import groove.lts.GTS;
import groove.view.FormatException;

/**
 * An <code>EncodedInt</code> describes an encoding of a number (in a given
 * range) by means of a String.
 * <p>
 * @see EncodedType
 * @author Maarten de Mol
 */
public class EncodedInt implements EncodedType<Integer,String> {

    /**
     * Local store of the allowed range of the number.
     */
    private int lowerBound;
    private int upperBound;

    /**
     * Default constructor. Creates local store only.
     * @param lowerBound - smallest number allowed (also the initial value)
     * @param upperBound - biggest number allowed (ignored if negative)  
     */
    public EncodedInt(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public EncodedTypeEditor<Integer,String> createEditor(Simulator simulator) {
        return new StringEditor<Integer>(Integer.toString(this.lowerBound), 4);
    }

    @Override
    public Integer parse(GTS gts, String source) throws FormatException {
        Integer value;

        try {
            value = Integer.parseInt(source, 10);
        } catch (NumberFormatException msg) {
            throw new FormatException("'" + source + "' is not a valid number.");
        }

        if (value < this.lowerBound) {
            throw new FormatException("Illegal input: '" + source
                + "' is smaller than the permitted lowerbound '"
                + Integer.toString(this.lowerBound) + "'.");
        }
        if (this.upperBound >= 0 && value > this.upperBound) {
            throw new FormatException("Illegal input: '" + source
                + "' is greater than the permitted upperbound '"
                + Integer.toString(this.upperBound) + "'.");
        }
        return value;
    }
}
