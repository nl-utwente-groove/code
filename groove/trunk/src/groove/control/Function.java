/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.control;

import groove.control.template.Switch.Kind;
import groove.control.CtrlPar.Var;
import groove.util.Fixable;

import java.util.List;

/**
 * Control program function.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Function extends Procedure implements Fixable {
    /**
     * Constructs a function with a given name, priority and signature.
     */
    public Function(String fullName, int priority, List<Var> signature,
            String controlName, int startLine) {
        super(fullName, priority, signature, controlName, startLine);
    }

    public Kind getKind() {
        return Kind.FUNCTION;
    }
}
