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
 * $Id: TraverseKind.java 5479 2014-07-19 12:20:13Z rensink $
 */
package groove.explore.config;

import groove.util.parse.NullParser;
import groove.util.parse.Parser;
import groove.verify.Formula;
import groove.verify.FormulaParser;
import groove.verify.Logic;

/**
 * Kind of traversal strategies.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum CheckingKind implements SettingKey {
    /** No model checking. */
    NONE("None", "No model checking", null),
    /** LTL model checking. */
    LTL_CHECK("LTL", "Linear Temporal Logic checking", Logic.LTL),
    /** CTL model checking. */
    CTL_CHECK("CTL", "Computation Tree Logic checking", Logic.CTL),;

    private CheckingKind(String name, String explanation, Logic logic) {
        this.name = name;
        this.explanation = explanation;
        this.logic = logic;
        if (logic == null) {
            this.parser = NullParser.instance(Formula.class);
        } else {
            this.parser = FormulaParser.instance(logic);
        }
    }

    /** Returns the name of this search order. */
    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public String getContentName() {
        return getName() + " formula";
    }

    /** Returns the logic corresponding to this checking kind;
     * may be {@code null}.
     */
    public Logic getLogic() {
        return this.logic;
    }

    private final Logic logic;

    @Override
    public FormulaSetting getDefaultSetting() {
        return createSetting(getDefaultValue());
    }

    @Override
    public FormulaSetting createSettting() throws IllegalArgumentException {
        switch (this) {
        case NONE:
            return null;
        case LTL_CHECK:
        case CTL_CHECK:
            throw new IllegalArgumentException();
        default:
            assert false;
            return null;
        }
    }

    @Override
    public FormulaSetting createSetting(Object content) throws IllegalArgumentException {
        return new FormulaSetting(getLogic(), (Formula) content);
    }

    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    @Override
    public Parser<Formula> parser() {
        return this.parser;
    }

    private final Parser<Formula> parser;

    /** Returns the model checking kind corresponding to a given logic.
     * @return {@link #LTL_CHECK} for {@link Logic#LTL}, {@link #CTL_CHECK} for {@link Logic#CTL}
     * and {@link #NONE} for {@code null}.
     */
    public static final CheckingKind getKind(Logic logic) {
        CheckingKind result = NONE;
        if (logic != null) {
            switch (logic) {
            case CTL:
                result = CTL_CHECK;
                break;
            case LTL:
                result = LTL_CHECK;
                break;
            default:
                assert false;
            }
        }
        return result;
    }
}
