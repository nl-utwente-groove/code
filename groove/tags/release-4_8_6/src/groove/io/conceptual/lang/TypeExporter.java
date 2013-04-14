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
import groove.io.conceptual.Field;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.IdentityProperty;
import groove.io.conceptual.property.KeysetProperty;
import groove.io.conceptual.property.OppositeProperty;
import groove.io.conceptual.property.Property;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.DataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.value.BoolValue;
import groove.io.conceptual.value.ContainerValue;
import groove.io.conceptual.value.CustomDataValue;
import groove.io.conceptual.value.EnumValue;
import groove.io.conceptual.value.IntValue;
import groove.io.conceptual.value.RealValue;
import groove.io.conceptual.value.StringValue;
import groove.io.conceptual.value.TupleValue;
import groove.io.external.PortException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TypeExporter<E> implements Messenger, Visitor {
    public abstract void addTypeModel(TypeModel typeModel) throws PortException;

    private List<Message> m_messages = new ArrayList<Message>();
    protected Map<Acceptor,E> m_elements = new HashMap<Acceptor,E>();

    protected void addMessage(Message m) {
        this.m_messages.add(m);
    }

    public List<Message> getMessages() {
        return this.m_messages;
    }

    public void clearMessages() {
        this.m_messages.clear();
    }

    public abstract ExportableResource getResource();

    protected void setElement(Acceptor acceptor, E element) {
        assert !(this.m_elements.containsKey(acceptor));
        this.m_elements.put(acceptor, element);
    }

    protected boolean hasElement(Acceptor acceptor) {
        return this.m_elements.containsKey(acceptor);
    }

    protected E getElement(Acceptor acceptor) {
        return getElement(acceptor, null, false);
    }

    protected E getElement(Acceptor acceptor, java.lang.Object param) {
        return getElement(acceptor, param, false);
    }

    // If allowNull and element is not being set, returns null
    protected E getElement(Acceptor acceptor, java.lang.Object param,
            boolean allowNull) {
        if (!this.m_elements.containsKey(acceptor)) {
            acceptor.doVisit(this, param);
        }

        if (!this.m_elements.containsKey(acceptor)) {
            if (allowNull) {
                return null;
            }
            throw new IllegalArgumentException(
                "Cannot get element for acceptor " + acceptor.toString());
        }

        return this.m_elements.get(acceptor);
    }

    protected void visitTypeModel(TypeModel typeModel, Config cfg) {
        TypeModel prevType = cfg.getTypeModel();
        cfg.setTypeModel(typeModel);

        visitTypeModel(typeModel);

        cfg.setTypeModel(prevType);
    }

    protected void visitTypeModel(TypeModel typeModel) {
        for (Class cmClass : typeModel.getClasses()) {
            getElement(cmClass);
        }
        for (Enum cmEnum : typeModel.getEnums()) {
            getElement(cmEnum);
        }
        for (CustomDataType cmData : typeModel.getDatatypes()) {
            getElement(cmData);
        }

        for (Property prop : typeModel.getProperties()) {
            prop.doVisit(this, null);
        }
    }

    @Override
    public void visit(DataType t, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(Class cmClass, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(Field field, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(Container container, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(Enum cmEnum, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(Tuple tuple, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(groove.io.conceptual.value.Object object, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(RealValue realval, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(StringValue stringval, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(IntValue intval, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(BoolValue boolval, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(EnumValue enumval, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(ContainerValue containerval, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(TupleValue tupleval, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(CustomDataValue dataval, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(AbstractProperty abstractProperty, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(ContainmentProperty containmentProperty, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(IdentityProperty identityProperty, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(KeysetProperty keysetProperty, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(OppositeProperty oppositeProperty, Object param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(DefaultValueProperty defaultValueProperty, Object param) {
        throw new UnsupportedOperationException();
    }
}
