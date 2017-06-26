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
package groove.io.conceptual.lang.ecore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import groove.io.conceptual.Field;
import groove.io.conceptual.InstanceModel;
import groove.io.conceptual.Timer;
import groove.io.conceptual.Visitor;
import groove.io.conceptual.lang.InstanceExporter;
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
import groove.io.conceptual.value.Value;
import groove.io.external.PortException;

@SuppressWarnings("javadoc")
public class InstanceToEcore extends InstanceExporter<java.lang.Object>implements Visitor {
    private TypeToEcore m_typeToEcore;

    private EcoreResource m_ecoreResource;

    // Set of generated Ecore objects. Used to find those objects which are root
    private Set<EObject> m_eObjects = new HashSet<>();

    public InstanceToEcore(TypeToEcore typeToEcore) {
        this.m_ecoreResource = (EcoreResource) typeToEcore.getResource();
        this.m_typeToEcore = typeToEcore;
    }

    @Override
    public void addInstanceModel(InstanceModel instanceModel) throws PortException {
        int timer = Timer.start("IM to Ecore");
        this.m_eObjects.clear();

        visitInstanceModel(instanceModel);

        List<EObject> rootObjects = new ArrayList<>();
        for (EObject object : this.m_eObjects) {
            if (object.eContainer() != null) {
                continue;
            }
            rootObjects.add(object);
        }

        if (rootObjects.size() == 0) {
            // This effectively means there is a containment cycle
            throw new PortException("Unable to find any root object");
        }

        Timer.stop(timer);
        timer = Timer.cont("Ecore save");

        Resource typeResource =
            this.m_ecoreResource.getInstanceResource(instanceModel.getQualName());
        EList<EObject> contents = typeResource.getContents();

        //contents.add(rootObject);
        contents.addAll(rootObjects);

        Timer.stop(timer);
    }

    @Override
    public void visit(groove.io.conceptual.value.Object object, String param) {
        if (hasElement(object)) {
            return;
        }

        Class cmClass = (Class) object.getType();
        EClass eClass = this.m_typeToEcore.getEClass(cmClass);

        EObject eObject = eClass.getEPackage()
            .getEFactoryInstance()
            .create(eClass);
        setElement(object, eObject);
        this.m_eObjects.add(eObject);

        for (Entry<Field,Value> fieldValue : object.getValue()
            .entrySet()) {
            EStructuralFeature eFeature =
                this.m_typeToEcore.getEStructuralFeature(fieldValue.getKey());
            // if unset value, dont set it in the Ecore model either
            if (fieldValue.getValue() == null
                || fieldValue.getValue() == groove.io.conceptual.value.Object.NIL) {
                continue;
            }
            if (eFeature.isMany()) {
                // Expecting a container value, which will be iterated with all elements added to the (implicit) ELIST
                ContainerValue cv = (ContainerValue) fieldValue.getValue();
                @SuppressWarnings("unchecked") EList<Object> objectList =
                    (EList<Object>) eObject.eGet(eFeature);
                for (Value subValue : cv.getValue()) {
                    Object eSubValue = getElement(subValue);
                    assert(eSubValue != null);
                    // It is very well possible that this evaluated to true, due to recursion and opposite edges
                    if (!objectList.contains(eSubValue)) {
                        objectList.add(eSubValue);
                    }
                }
            } else {
                // Just insert the value directly
                Object eValue = null;
                // ContainerValue possible for 0..1 attribs
                if (fieldValue.getValue() instanceof ContainerValue) {
                    eValue = getElement(((ContainerValue) fieldValue.getValue()).getValue()
                        .get(0));
                } else {
                    eValue = getElement(fieldValue.getValue());
                }

                eObject.eSet(eFeature, eValue);
            }
        }
    }

    @Override
    public void visit(RealValue realval, String param) {
        if (hasElement(realval)) {
            return;
        }

        EDataType eDataType = this.m_typeToEcore.getEDataType((DataType) realval.getType());

        Object eDoubleVal = eDataType.getEPackage()
            .getEFactoryInstance()
            .createFromString(eDataType, realval.toString());
        setElement(realval, eDoubleVal);
    }

    @Override
    public void visit(StringValue stringval, String param) {
        if (hasElement(stringval)) {
            return;
        }

        EDataType eDataType = this.m_typeToEcore.getEDataType((DataType) stringval.getType());

        Object eStringVal = eDataType.getEPackage()
            .getEFactoryInstance()
            .createFromString(eDataType, stringval.toString());
        setElement(stringval, eStringVal);
    }

    @Override
    public void visit(IntValue intval, String param) {
        if (hasElement(intval)) {
            return;
        }

        EDataType eDataType = this.m_typeToEcore.getEDataType((DataType) intval.getType());

        Object eIntVal = eDataType.getEPackage()
            .getEFactoryInstance()
            .createFromString(eDataType, intval.toString());
        setElement(intval, eIntVal);
    }

    @Override
    public void visit(BoolValue boolval, String param) {
        if (hasElement(boolval)) {
            return;
        }

        EDataType eDataType = this.m_typeToEcore.getEDataType((DataType) boolval.getType());

        Object eBoolVal = eDataType.getEPackage()
            .getEFactoryInstance()
            .createFromString(eDataType, boolval.toString());
        setElement(boolval, eBoolVal);
    }

    @Override
    public void visit(EnumValue enumval, String param) {
        if (hasElement(enumval)) {
            return;
        }

        EEnum eEnum = (EEnum) this.m_typeToEcore.getEDataType((Enum) enumval.getType());

        Object eEnumVal = eEnum.getEPackage()
            .getEFactoryInstance()
            .createFromString(eEnum, enumval.getValue()
                .toString());
        setElement(enumval, eEnumVal);

        return;
    }

    @Override
    public void visit(ContainerValue containerval, String param) {
        Container container = (Container) containerval.getType();
        EClass containerClass = this.m_typeToEcore.getContainerClass(container);

        EObject containerObject = containerClass.getEPackage()
            .getEFactoryInstance()
            .create(containerClass);
        setElement(containerval, containerObject);
        this.m_eObjects.add(containerObject);

        EStructuralFeature eFeature = containerClass.getEStructuralFeature("value");
        @SuppressWarnings("unchecked") EList<Object> objectList =
            (EList<Object>) containerObject.eGet(eFeature);
        for (Value val : containerval.getValue()) {
            Object eSubValue = getElement(val);
            objectList.add(eSubValue);
        }
    }

    @Override
    public void visit(TupleValue tupleval, String param) {
        if (hasElement(tupleval)) {
            return;
        }

        Tuple tuple = (Tuple) tupleval.getType();
        EClass tupleClass = this.m_typeToEcore.getTupleClass(tuple);

        EObject tupleObject = tupleClass.getEPackage()
            .getEFactoryInstance()
            .create(tupleClass);
        setElement(tupleval, tupleObject);
        this.m_eObjects.add(tupleObject);

        for (Entry<Integer,Value> entry : tupleval.getValue()
            .entrySet()) {
            String indexName = this.m_typeToEcore.getTupleElementName(tuple, entry.getKey());
            EStructuralFeature eFeature = tupleClass.getEStructuralFeature(indexName);
            Value tupValue = entry.getValue();
            if (eFeature.isMany()) {
                // Expecting a container value, which will be iterated with all elements added to the (implicit) ELIST
                ContainerValue cv = (ContainerValue) tupValue;
                @SuppressWarnings("unchecked") EList<Object> objectList =
                    (EList<Object>) tupleObject.eGet(eFeature);
                for (Value subValue : cv.getValue()) {
                    Object eSubValue = getElement(subValue);
                    objectList.add(eSubValue);
                }
            } else {
                // Just insert the value directly
                Object eValue = getElement(tupValue);
                tupleObject.eSet(eFeature, eValue);
            }
        }
    }

    @Override
    public void visit(CustomDataValue dataval, String param) {
        if (hasElement(dataval)) {
            return;
        }

        CustomDataType dataType = (CustomDataType) dataval.getType();
        EDataType eDataType = this.m_typeToEcore.getEDataType(dataType);

        Object eDataValue = eDataType.getEPackage()
            .getEFactoryInstance()
            .createFromString(eDataType, dataval.getValue());
        setElement(dataval, eDataValue);
    }
}
