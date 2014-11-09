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
package groove.io.conceptual.lang;

import groove.io.conceptual.Design;
import groove.io.conceptual.ExportBuilder;
import groove.io.conceptual.Glossary;
import groove.io.external.PortException;

import java.util.ArrayList;
import java.util.List;

/** Abstract superclass for all design exporters.
 * @param <X> type of the export object
 * @param <E> type of the target format elements
 */
public abstract class DesignExportBuilder<X,E> extends ExportBuilder<X,E> implements Messenger {
    /** Constructs an bridge from a given design to a given export. */
    protected DesignExportBuilder(Design design, X export) {
        this.export = export;
        this.design = design;
    }

    @Override
    public X getExport() {
        return this.export;
    }

    private final X export;

    /** Returns the design being exported. */
    public Design getDesign() {
        return this.design;
    }

    /** Convenience method to returns the glossary of the exported design. */
    protected Glossary getGlossary() {
        return this.design.getGlossary();
    }

    private final Design design;

    @Override
    public List<Message> getMessages() {
        return this.m_messages;
    }

    private List<Message> m_messages = new ArrayList<Message>();

    @Override
    public void build() throws PortException {
        for (groove.io.conceptual.value.Object obj : getDesign().getObjects()) {
            add(obj);
        }
    }
}
