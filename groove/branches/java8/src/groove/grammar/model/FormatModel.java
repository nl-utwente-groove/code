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

/**
 * Model for format configurations, which are just strings
 *
 * @author Harold Bruijntjes
 */
public class FormatModel extends TextBasedModel<String> {
    /**
     * Constructs a control view from a given format document.
     *
     * @param grammar
     *            the grammar model to which this format view belongs.
     * @param document
     *            the format document; non-null
     */
    public FormatModel(GrammarModel grammar, Text document) {
        super(grammar, ResourceKind.FORMAT, document);
    }

    @Override
    String compute() {
        return getProgram().getContent();
    }

    /** Returns the (XML string) configuration. */
    public String toConfig() {
        return compute();
    }
}
