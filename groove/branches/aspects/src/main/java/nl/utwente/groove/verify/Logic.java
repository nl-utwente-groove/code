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
package nl.utwente.groove.verify;

import static nl.utwente.groove.verify.LogicOp.ALWAYS;
import static nl.utwente.groove.verify.LogicOp.AND;
import static nl.utwente.groove.verify.LogicOp.EQUIV;
import static nl.utwente.groove.verify.LogicOp.EVENTUALLY;
import static nl.utwente.groove.verify.LogicOp.EXISTS;
import static nl.utwente.groove.verify.LogicOp.FALSE;
import static nl.utwente.groove.verify.LogicOp.FOLLOWS;
import static nl.utwente.groove.verify.LogicOp.FORALL;
import static nl.utwente.groove.verify.LogicOp.IMPLIES;
import static nl.utwente.groove.verify.LogicOp.LPAR;
import static nl.utwente.groove.verify.LogicOp.NEXT;
import static nl.utwente.groove.verify.LogicOp.NOT;
import static nl.utwente.groove.verify.LogicOp.OR;
import static nl.utwente.groove.verify.LogicOp.PROP;
import static nl.utwente.groove.verify.LogicOp.RELEASE;
import static nl.utwente.groove.verify.LogicOp.S_RELEASE;
import static nl.utwente.groove.verify.LogicOp.TRUE;
import static nl.utwente.groove.verify.LogicOp.UNTIL;
import static nl.utwente.groove.verify.LogicOp.W_UNTIL;

import java.util.EnumSet;
import java.util.Set;

/**
 * Enumerator for LTL versus CTL logic.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum Logic {
    /** Linear Temporal Logic. */
    LTL,
    /** Computation Tree Logic. */
    CTL, ;

    /** Returns the temporal operators associated with this logic. */
    public Set<LogicOp> getOps() {
        switch (this) {
        case LTL:
            return EnumSet.of(PROP, TRUE, FALSE, NOT, OR, AND, IMPLIES, FOLLOWS, EQUIV, NEXT,
                UNTIL, W_UNTIL, RELEASE, S_RELEASE, ALWAYS, EVENTUALLY, LPAR);
        case CTL:
            return EnumSet.of(PROP, TRUE, FALSE, NOT, OR, AND, IMPLIES, FOLLOWS, EQUIV, NEXT,
                UNTIL, ALWAYS, EVENTUALLY, FORALL, EXISTS, LPAR);
        default:
            assert false;
            return null;
        }
    }
}
