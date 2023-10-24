/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.grammar.rule;

import java.util.NoSuchElementException;

import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.rule.MethodName.Language;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Parser;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class MethodNameParser extends Parser.AParser<MethodName> {
    /** Private constructor for the singleton instance. */
    private MethodNameParser() {
        super("Method name, formatted according to [&lt;language&gt;COLON]&lt;qualName&gt;",
              MethodName.class);
    }

    @Override
    public MethodName parse(String input) throws FormatException {
        int colon = input.indexOf(':');
        Language language;
        String name;
        if (colon < 0) {
            language = Language.JAVA;
            name = input;
        } else {
            String langName = input.substring(0, colon);
            try {
                language = Language.fromName(langName);
            } catch (NoSuchElementException exc) {
                throw new FormatException("Unknown language '%s'", langName);
            }
            name = input.substring(colon + 1);
        }
        QualName qualName = QualName.parse(name);
        qualName.getErrors().throwException();
        return new MethodName(language, qualName);
    }

    @Override
    public <V extends MethodName> String unparse(V value) {
        return value.toString();
    }

    /** Returns the singleton instance of this parser. */
    public static MethodNameParser instance() {
        return INSTANCE;
    }

    private static final MethodNameParser INSTANCE = new MethodNameParser();
}
