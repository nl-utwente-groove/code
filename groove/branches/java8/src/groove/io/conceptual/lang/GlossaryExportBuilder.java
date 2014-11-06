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

import groove.io.conceptual.Acceptor;
import groove.io.conceptual.ExportBuilder;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.property.Property;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.Enum;
import groove.io.external.PortException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

/** Abstract superclass for all glossary exporters.
 */
public abstract class GlossaryExportBuilder<X,E> implements Messenger, ExportBuilder<X> {
    protected GlossaryExportBuilder(X export, Glossary glos) {
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
            getElement(cmClass);
        }
        for (Enum cmEnum : getGlossary().getEnums()) {
            getElement(cmEnum);
        }
        for (CustomDataType cmData : getGlossary().getDatatypes()) {
            getElement(cmData);
        }
        for (Property prop : getGlossary().getProperties()) {
            prop.doBuild(this, null);
        }
    }

    protected Map<Acceptor,E> m_elements = new HashMap<Acceptor,E>();

    protected void setElement(Acceptor acceptor, E element) {
        assert !(this.m_elements.containsKey(acceptor));
        this.m_elements.put(acceptor, element);
    }

    protected boolean hasElement(Acceptor acceptor) {
        return this.m_elements.containsKey(acceptor);
    }

    protected @Nullable E getElement(Acceptor acceptor) {
        return getElement(acceptor, null, false);
    }

    protected E getElement(Acceptor acceptor, String param) {
        return getElement(acceptor, param, false);
    }

    // If allowNull and element is not being set, returns null
    protected E getElement(Acceptor acceptor, String param, boolean allowNull) {
        if (!this.m_elements.containsKey(acceptor)) {
            acceptor.doBuild(this, param);
        }

        if (!this.m_elements.containsKey(acceptor)) {
            if (allowNull) {
                return null;
            }
            throw new IllegalArgumentException("Cannot get element for acceptor "
                + acceptor.toString());
        }

        return this.m_elements.get(acceptor);
    }
}
