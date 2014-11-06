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
import groove.io.conceptual.Design;
import groove.io.conceptual.ExportBuilder;
import groove.io.conceptual.Glossary;
import groove.io.external.PortException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Abstract superclass for all design exporters.
 * @param <X> type of the export object
 * @param <Elem> type of the target format elements
 */
public abstract class DesignExportBuilder<X,Elem> implements Messenger, ExportBuilder<X> {
    protected DesignExportBuilder(X export, Design design) {
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
    private Map<Acceptor,Elem> m_elements = new HashMap<Acceptor,Elem>();

    @Override
    public void build() throws PortException {
        for (groove.io.conceptual.value.Object obj : getDesign().getObjects()) {
            getElement(obj);
        }
    }

    protected void setElement(Acceptor acceptor, Elem element) {
        assert !(this.m_elements.containsKey(acceptor));
        this.m_elements.put(acceptor, element);
    }

    protected boolean hasElement(Acceptor acceptor) {
        return this.m_elements.containsKey(acceptor);
    }

    protected Elem getElement(Acceptor acceptor) {
        return getElement(acceptor, null);
    }

    protected Elem getElement(Acceptor acceptor, String param) {
        if (!this.m_elements.containsKey(acceptor)) {
            acceptor.doBuild(this, param);
        }

        if (!this.m_elements.containsKey(acceptor)) {
            throw new IllegalArgumentException("Cannot create export element for "
                + acceptor.toString());
        }

        return this.m_elements.get(acceptor);
    }
}
