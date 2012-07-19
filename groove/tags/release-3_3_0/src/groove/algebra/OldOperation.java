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
 * $Id: Operation.java,v 1.6 2007-07-21 20:07:43 rensink Exp $
 */
package groove.algebra;

import java.util.List;

/**
 * Interface specifying what methods each Operation needs to implement. AREND:
 * Shouldn't the types of the arguments be included somehow? HARMEN: I think it
 * should. Let's include this when refactoring the signature/algebra core.
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2007-07-21 20:07:43 $
 * @deprecated Superseded by the new algebra implementation
 */
@Deprecated
public interface OldOperation {
    /**
     * Apply this operation on the list of operands and return the result.
     * @param args the operands on which this operation operates
     * @return the resulting {@link groove.algebra.OldOperation} when applying this
     *         operation on its <tt>operands</tt>
     * @throws IllegalArgumentException if the operation cannot be performed,
     *         due to typing errors of the operands or zero division
     */
    public Object apply(List<Object> args) throws IllegalArgumentException;

    /**
     * @return the String representation of this operation
     */
    public String symbol();

    /**
     * @return the arity of this operation
     */
    public int arity();

    /**
     * @return the algebra to which this operation belongs
     */
    public OldAlgebra algebra();

    /**
     * Returns the algebra to which the result of the operation belongs. Note
     * that this may differ from the algebra in which the operation is defined!
     */
    public OldAlgebra getResultType();
}