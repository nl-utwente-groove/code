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
 * $Id: DefaultConstant.java,v 1.5 2007-08-28 22:02:00 rensink Exp $
 */
package groove.algebra;

import java.util.List;

/**
 * Class implementing the {@link groove.algebra.Constant}- interface being a
 * subclass of class {@link groove.algebra.DefaultOperation}.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2007-08-28 22:02:00 $
 */
abstract public class DefaultConstant extends DefaultOperation implements
        Constant {
    /** Constructs a constant with a given algebra and string description. */
    public DefaultConstant(Algebra algebra, String symbol) {
        super(algebra, symbol, 0);
    }

    public Object apply(List<Object> args) throws IllegalArgumentException {
        if (args == null || args.size() > 0) {
            throw new IllegalArgumentException(String.format(
                "Constants cannot be applied to arguments %s", args));
        }
        return getValue();
    }
}
