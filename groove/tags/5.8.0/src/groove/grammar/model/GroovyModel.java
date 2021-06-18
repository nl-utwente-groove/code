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
 * $Id$
 */
package groove.grammar.model;

import static groove.grammar.model.ResourceKind.GROOVY;

import groove.grammar.QualName;
import groove.grammar.groovy.Util;
import groove.util.parse.FormatException;

/**
 * Model for Groovy programs, which are just strings
 *
 * @author Staijen
 */
public class GroovyModel extends TextBasedModel<String> {
    /**
     * Constructs a control view from a given groovy program.
     *
     * @param grammar
     *            the grammar view to which this groovy view belongs. Must be
     *            non-{@code null} in order to compute the control automation
     * @param name
     *            the name of the groovy program
     * @param program
     *            the groovy program; non-null
     */
    public GroovyModel(GrammarModel grammar, QualName name, String program) {
        super(grammar, GROOVY, name, program);
    }

    @Override
    public boolean isEnabled() {
        return Util.isGroovyPresent();
    }

    @Override
    String compute() throws FormatException {
        return getProgram();
    }
}
