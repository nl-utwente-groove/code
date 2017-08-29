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

import groove.grammar.QualName;
import groove.util.parse.FormatException;

/**
 * Model for Groovy programs, which are just strings
 *
 * @author Harold Bruijntjes
 */
public class ConfigModel extends TextBasedModel<String> {
    /**
     * Constructs a control view from a given config document.
     *
     * @param grammar
     *            the grammar view to which this config view belongs. Must be
     *            non-{@code null} in order to compute the control automation
     * @param name
     *            the name of the config document
     * @param document
     *            the config document; non-null
     */
    public ConfigModel(GrammarModel grammar, QualName name, String document) {
        super(grammar, ResourceKind.GROOVY, name, document);
    }

    // Cannot be enabled
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    String compute() throws FormatException {
        return getProgram();
    }
}
