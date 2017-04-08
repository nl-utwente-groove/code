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

import java.awt.Component;
import java.util.Collections;
import java.util.Optional;

import javax.swing.JOptionPane;

import groove.algebra.Constant;
import groove.algebra.Sort;
import groove.grammar.Condition;
import groove.grammar.rule.VariableNode;
import groove.util.parse.FormatException;

/**
 * Value oracle that asks the user for a value
 * @author Arend Rensink
 * @version $Revision $
 */
public class DialogValueOracle implements ValueOracle {
    @Override
    public Iterable<Constant> getValues(Condition condition, VariableNode var) {
        Optional<Constant> value = getValue(condition.getName(), var.getSort());
        return value.map(v -> Collections.singleton(v))
            .orElse(Collections.emptySet());
    }

    private Optional<Constant> getValue(String ruleName, Sort type) {
        Optional<Constant> result = Optional.empty();
        boolean answered = false;
        do {
            String value = JOptionPane.showInputDialog(this.parent,
                String.format("Enter a %s value for rule %s", type.getName(), ruleName));
            if (value == null) {
                int answer = JOptionPane.showConfirmDialog(this.parent,
                    "Cancelling means the rule is marked as not applicable.\nIs that what you want?",
                    "Confirm cancel",
                    JOptionPane.YES_NO_OPTION);
                answered = answer == JOptionPane.YES_OPTION;
            } else {
                try {
                    result = Optional.of(type.createConstant(value));
                    answered = true;
                } catch (FormatException exc) {
                    JOptionPane.showMessageDialog(this.parent,
                        String.format("Invalid %s value: %s", type.getName(), exc.getMessage()));
                }
            }
        } while (!answered);
        return result;
    }

    /** Sets the parent component of the dialog, to enable correct placement. */
    public void setParent(Component parent) {
        this.parent = parent;
    }

    private Component parent;

    @Override
    public Kind getKind() {
        return Kind.DIALOG;
    }

    /** Returns the singleton instance of this class. */
    public final static DialogValueOracle instance() {
        if (INSTANCE == null) {
            INSTANCE = new DialogValueOracle();
        }
        return INSTANCE;
    }

    private static DialogValueOracle INSTANCE;
}
