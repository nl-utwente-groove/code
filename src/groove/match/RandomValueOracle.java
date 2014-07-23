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
package groove.match;

import groove.algebra.BoolSignature;
import groove.algebra.Constant;
import groove.algebra.Sort;
import groove.grammar.Condition;
import groove.grammar.rule.VariableNode;

import java.util.Collections;

/** Oracle returning a single random value for the appropriate type. */
public class RandomValueOracle implements ValueOracle {
    /** Constructor for the singleton instance. */
    private RandomValueOracle() {
        // empty
    }

    @Override
    public Iterable<Constant> getValues(Condition condition, VariableNode var) {
        Sort sig = var.getSignature();
        Constant result;
        switch (sig) {
        case BOOL:
            result =
                Math.random() < 0.5 ? BoolSignature.TRUE
                        : BoolSignature.FALSE;
            break;
        case INT:
            result = Constant.instance((int) Math.random() * 100);
            break;
        case REAL:
            result = Constant.instance(Math.random() * 100);
            break;
        case STRING:
            StringBuffer text = new StringBuffer();
            int length = (int) (Math.random() * 10);
            for (int i = 0; i < length; i++) {
                text.append((char) ('0' + Math.random() * 36));
            }
            result = Constant.instance(text.toString());
            break;
        default:
            result = null;
            assert false;
        }
        return Collections.singleton(result);
    }

    /** Returns the singleton instance of this class. */
    public final static RandomValueOracle instance() {
        return instance;
    }

    private static final RandomValueOracle instance = new RandomValueOracle();
}
