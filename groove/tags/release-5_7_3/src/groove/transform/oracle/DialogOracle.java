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
package groove.transform.oracle;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import groove.algebra.Constant;
import groove.algebra.Sort;
import groove.grammar.GrammarProperties;
import groove.grammar.UnitPar.RulePar;
import groove.grammar.host.HostGraph;
import groove.transform.RuleEvent;
import groove.util.parse.FormatException;

/**
 * Value oracle that asks the user for a value.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class DialogOracle implements ValueOracleFactory, ValueOracle {
    /** Private constructor for the singleton instance. */
    private DialogOracle() {
        // empty
    }

    @Override
    public DialogOracle instance(GrammarProperties properties) {
        return instance();
    }

    @Override
    public Constant getValue(HostGraph host, RuleEvent event, RulePar par) throws FormatException {
        String ruleName = event.getRule()
            .getQualName()
            .toString();
        Sort sort = par.getType()
            .getSort();
        Constant result = null;
        boolean answered = false;
        do {
            String value = JOptionPane.showInputDialog(this.parent,
                String.format("Enter a value of type %s for parameter %s of rule %s",
                    sort,
                    par.getName(),
                    ruleName));
            if (value == null) {
                int answer = JOptionPane.showConfirmDialog(this.parent,
                    "Cancelling means the exploration will be interrupted.\nIs that what you want?",
                    "Confirm cancel",
                    JOptionPane.YES_NO_OPTION);
                answered = answer == JOptionPane.YES_OPTION;
            } else {
                try {
                    result = sort.createConstant(value);
                    answered = true;
                } catch (FormatException exc) {
                    JOptionPane.showMessageDialog(this.parent,
                        String.format("Invalid %s value: %s", sort.getName(), exc.getMessage()));
                }
            }
        } while (!answered);
        if (result == null) {
            throw new FormatException("User input in value dialog cancelled");
        }
        return result;
    }

    /** Sets the parent component of the dialog, to enable correct placement. */
    public void setParent(Component parent) {
        this.parent = parent;
    }

    private @Nullable Component parent;

    @Override
    public ValueOracleKind getKind() {
        return ValueOracleKind.DIALOG;
    }

    /** Returns the singleton instance of this class. */
    public final static DialogOracle instance() {
        DialogOracle result = INSTANCE;
        if (result == null) {
            result = INSTANCE = new DialogOracle();
        }
        return result;
    }

    private static @Nullable DialogOracle INSTANCE;
}
