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
package groove.grammar.model;

import java.io.OutputStream;
import java.io.PrintWriter;

import groove.grammar.QualName;

/**
 * General interface for resource models constructed from a flat text.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class TextBasedModel<M> extends NamedResourceModel<M> {
    /**
     * Constructs a new text-based resource model, of a given kind.
     * @param grammar the grammar model to which this resource belongs
     * @param kind the kind of resource.
     * @param name the name of the resource
     * @param text the text of the resource
     */
    public TextBasedModel(GrammarModel grammar, ResourceKind kind, QualName name, String text) {
        super(grammar, kind, name);
        this.text = text;
    }

    /**
     * The source of a text-based resource is the program text.
     * @see #getProgram()
     */
    @Override
    public String getSource() {
        return getProgram();
    }

    /** Returns the text of the resource. */
    public String getProgram() {
        return this.text;
    }

    /** The text of the resource. */
    private final String text;

    /**
     * Saves the program to the given OutputStream.
     * @param out the output stream to write the control program to.
     */
    public static void store(String controlProgram, OutputStream out) {
        try (PrintWriter pw = new PrintWriter(out)) {
            pw.write(controlProgram);
        }
    }
}
