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
package groove.explore.config;

import groove.util.Pair;
import groove.verify.Formula;
import groove.verify.Logic;

/**
 * LTL or CTL formula.
 * @author Arend Rensink
 * @version $Revision $
 */
public class FormulaSetting extends Pair<CheckingKind,Formula> implements
    Setting<CheckingKind,Formula> {
    /**
     * Creates new content, from a given formula of a given logic.
     */
    public FormulaSetting(Logic logic, Formula formula) {
        super(CheckingKind.getKind(logic), formula);
    }

    @Override
    public CheckingKind getKind() {
        return one();
    }

    @Override
    public Formula getContent() {
        return two();
    }
}
