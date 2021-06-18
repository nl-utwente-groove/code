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
package groove.grammar.rule;

import java.util.stream.Stream;

import groove.grammar.QualName;
import groove.grammar.rule.MethodName.Language;
import groove.util.parse.FormatException;
import groove.util.parse.Parser;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class MethodNameParser implements Parser<MethodName> {
    /** Private constructor for the singleton instance. */
    private MethodNameParser() {
        // empty
    }

    @Override
    public String getDescription() {
        return "Format: [&lt;language&gt;COLON]&lt;qualName&gt;";
    }

    @Override
    public MethodName parse(String input) throws FormatException {
        MethodName result;
        if (input == null || input.isEmpty()) {
            result = getDefaultValue();
        } else {
            int colon = input.indexOf(':');
            Language language;
            String name;
            if (colon < 0) {
                language = Language.JAVA;
                name = input;
            } else {
                String langName = input.substring(0, colon);
                language = Stream.of(Language.values())
                    .filter(s -> s.getName()
                        .equals(langName))
                    .findAny()
                    .orElseThrow(() -> new FormatException("Unknown language '%s'", langName));
                name = input.substring(colon + 1);
            }
            QualName qualName = QualName.parse(name);
            qualName.getErrors()
                .throwException();
            result = new MethodName(language, qualName);
        }
        return result;
    }

    @Override
    public String toParsableString(Object value) {
        return isDefault(value) ? getDefaultString() : ((MethodName) value).toString();
    }

    @Override
    public MethodName getDefaultValue() {
        return null;
    }

    @Override
    public Class<? extends MethodName> getValueType() {
        return MethodName.class;
    }

    /** Returns the singleton instance of this parser. */
    public static MethodNameParser instance() {
        return INSTANCE;
    }

    private static final MethodNameParser INSTANCE = new MethodNameParser();
}
