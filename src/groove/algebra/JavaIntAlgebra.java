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
 * $Id: JavaIntAlgebra.java,v 1.3 2007-08-26 07:24:19 rensink Exp $
 */
package groove.algebra;


/**
 * Integer algebra based on the java type {@link Integer}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class JavaIntAlgebra implements IntSignature<Integer,Boolean>, NewAlgebra<Integer> {
    public Integer add(Integer arg0, Integer arg1) {
        return arg0 + arg1;
    }

    /**
     * Delegates to {@link Integer#parseInt(String)}.
     */
    public Integer getValue(String symbol) {
        try {
            return Integer.parseInt(symbol);
        } catch (NumberFormatException exc) {
            return null;
        }
    }
    
    /**
     * Delegates to {@link Integer#toString()}.
     */
    public String getSymbol(Integer value) {
        return value.toString();
    }

    /** Returns {@link #NAME}. */
    public String getName() {
        return NAME;
    }
    
    /** Name of the algebra. */
    public static final String NAME = "jint";
}
