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

import groove.io.conceptual.ExportBuilder;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.property.Property;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.Enum;
import groove.io.external.PortException;

import java.util.ArrayList;
import java.util.List;

/** Abstract superclass for all glossary exporters.
 */
public abstract class GlossaryExportBuilder<X,E> extends ExportBuilder<X,E> implements Messenger {
    /** Constructs a bridge from a given glossary to a given export. */
    protected GlossaryExportBuilder(Glossary glos, X export) {
        this.export = export;
        this.glos = glos;
    }

    /** Returns the glossary that is to be exported. */
    public Glossary getGlossary() {
        return this.glos;
    }

    private final Glossary glos;

    @Override
    public X getExport() {
        return this.export;
    }

    private final X export;

    @Override
    public List<Message> getMessages() {
        return this.m_messages;
    }

    private List<Message> m_messages = new ArrayList<Message>();

    @Override
    public void build() throws PortException {
        for (Class cmClass : getGlossary().getClasses()) {
            add(cmClass);
        }
        for (Enum cmEnum : getGlossary().getEnums()) {
            add(cmEnum);
        }
        for (CustomDataType cmData : getGlossary().getDatatypes()) {
            add(cmData);
        }
        for (Property prop : getGlossary().getProperties()) {
            add(prop, null);
        }
    }
}
