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

import groove.io.conceptual.Design;
import groove.io.conceptual.Field;
import groove.io.conceptual.Timer;
import groove.io.conceptual.lang.DesignExportBuilder;
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

/**
 * Bridge from design to ECore instance model.
 * @author Harold Bruijntjes
 * @version $Revision $
 */
public class DesignToEcore extends DesignExportBuilder<EcoreExport,java.lang.Object> {
    /** Constructs a design-to-ecore exporter for a given type-level exporter and design. */
    public DesignToEcore(GlossaryToEcore glossToEcore, Design design) {
        super(glossToEcore.getExport(), design);
        this.m_glossToEcore = glossToEcore;
    }

    /** The glossary-to-ecore bridge used for this design-to-ecore bridge. */
    private final GlossaryToEcore m_glossToEcore;
    /** Set of generated ECore objects, used to find those objects which are root. */
    private final Set<EObject> m_eObjects = new HashSet<EObject>();

    @Override
    public void build() throws PortException {
        int timer = Timer.start("IM to Ecore");
        super.build();

        /* Collects the root objects of the ECore instance model,
         * being the objects not contained in any other object.
         */
        List<EObject> rootObjects = new ArrayList<EObject>();
        for (EObject object : this.m_eObjects) {
            if (object.eContainer() == null) {
                rootObjects.add(object);
            }
        }
        if (rootObjects.size() == 0) {
            // This effectively means there is a containment cycle
            throw new PortException("Unable to find any root object");
        }
        Timer.stop(timer);

        timer = Timer.cont("Ecore save");
        Resource instanceResource =
            getExport().getInstanceResource(getDesign().getName() + "bababa");
        instanceResource.getContents().addAll(rootObjects);

        Timer.stop(timer);
    }

    @Override
    public void addObject(groove.io.conceptual.value.Object object) {
        if (!hasElement(object)) {
            Class cmClass = (Class) object.getType();
            EClass eClass = this.m_glossToEcore.getEClass(cmClass);

            EObject eObject = eClass.getEPackage().getEFactoryInstance().create(eClass);
            setElement(object, eObject);
            this.m_eObjects.add(eObject);

            for (Entry<Field,Value> fieldValue : object.getValue().entrySet()) {
                EStructuralFeature eFeature =
                    this.m_glossToEcore.getEStructuralFeature(fieldValue.getKey());
                // if unset value, dont set it in the Ecore model either
                if (fieldValue.getValue() == null
                    || fieldValue.getValue() == groove.io.conceptual.value.Object.NIL) {
                    continue;
                }
                if (eFeature.isMany()) {
                    // Expecting a container value, which will be iterated with all elements added to the (implicit) ELIST
                    ContainerValue cv = (ContainerValue) fieldValue.getValue();
                    @SuppressWarnings("unchecked")
                    EList<Object> objectList = (EList<Object>) eObject.eGet(eFeature);
                    for (Value subValue : cv.getValue()) {
                        Object eSubValue = getElement(subValue);
                        assert (eSubValue != null);
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
                        eValue =
                            getElement(((ContainerValue) fieldValue.getValue()).getValue().get(0));
                    } else {
                        eValue = getElement(fieldValue.getValue());
                    }

                    eObject.eSet(eFeature, eValue);
                }
            }
        }
    }

    @Override
    public void addRealValue(RealValue realval) {
        if (!hasElement(realval)) {
            EDataType realType = this.m_glossToEcore.getEDataType((DataType) realval.getType());

            Object eDoubleVal =
                realType.getEPackage()
                    .getEFactoryInstance()
                    .createFromString(realType, realval.toString());
            setElement(realval, eDoubleVal);
        }
    }

    @Override
    public void addStringValue(StringValue stringval) {
        if (hasElement(stringval)) {
            return;
        }

        EDataType eDataType = this.m_glossToEcore.getEDataType((DataType) stringval.getType());

        Object eStringVal =
            eDataType.getEPackage()
                .getEFactoryInstance()
                .createFromString(eDataType, stringval.toString());
        setElement(stringval, eStringVal);
    }

    @Override
    public void addIntValue(IntValue intval) {
        if (hasElement(intval)) {
            return;
        }

        EDataType eDataType = this.m_glossToEcore.getEDataType((DataType) intval.getType());

        Object eIntVal =
            eDataType.getEPackage()
                .getEFactoryInstance()
                .createFromString(eDataType, intval.toString());
        setElement(intval, eIntVal);
    }

    @Override
    public void addBoolValue(BoolValue val) {
        if (!hasElement(val)) {
            EDataType boolType = this.m_glossToEcore.getEDataType((DataType) val.getType());

            Object eBoolVal =
                boolType.getEPackage()
                    .getEFactoryInstance()
                    .createFromString(boolType, val.toString());
            setElement(val, eBoolVal);
        }
    }

    @Override
    public void addEnumValue(EnumValue val) {
        if (!hasElement(val)) {
            EEnum eEnum = (EEnum) this.m_glossToEcore.getEDataType((Enum) val.getType());

            Object eEnumVal =
                eEnum.getEPackage()
                    .getEFactoryInstance()
                    .createFromString(eEnum, val.getValue().toString());
            setElement(val, eEnumVal);
        }
    }

    @Override
    public void addContainerValue(ContainerValue containerVal, String base) {
        Container container = (Container) containerVal.getType();
        EClass containerClass = this.m_glossToEcore.getContainerClass(container);

        EObject containerObject =
            containerClass.getEPackage().getEFactoryInstance().create(containerClass);
        setElement(containerVal, containerObject);
        this.m_eObjects.add(containerObject);

        EStructuralFeature eFeature = containerClass.getEStructuralFeature("value");
        @SuppressWarnings("unchecked")
        EList<Object> objectList = (EList<Object>) containerObject.eGet(eFeature);
        for (Value val : containerVal.getValue()) {
            Object eSubValue = getElement(val);
            objectList.add(eSubValue);
        }
    }

    @Override
    public void addTupleValue(TupleValue val) {
        if (hasElement(val)) {
            return;
        }

        Tuple tuple = (Tuple) val.getType();
        EClass tupleClass = this.m_glossToEcore.getTupleClass(tuple);

        EObject tupleObject = tupleClass.getEPackage().getEFactoryInstance().create(tupleClass);
        setElement(val, tupleObject);
        this.m_eObjects.add(tupleObject);

        for (Entry<Integer,Value> entry : val.getValue().entrySet()) {
            String indexName = this.m_glossToEcore.getTupleElementName(tuple, entry.getKey());
            EStructuralFeature eFeature = tupleClass.getEStructuralFeature(indexName);
            Value tupValue = entry.getValue();
            if (eFeature.isMany()) {
                // Expecting a container value, which will be iterated with all elements added to the (implicit) ELIST
                ContainerValue cv = (ContainerValue) tupValue;
                @SuppressWarnings("unchecked")
                EList<Object> objectList = (EList<Object>) tupleObject.eGet(eFeature);
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
    public void addCustomDataValue(CustomDataValue val) {
        if (hasElement(val)) {
            return;
        }

        CustomDataType dataType = (CustomDataType) val.getType();
        EDataType eDataType = this.m_glossToEcore.getEDataType(dataType);

        Object eDataValue =
            eDataType.getEPackage()
                .getEFactoryInstance()
                .createFromString(eDataType, val.getValue());
        setElement(val, eDataValue);
    }
}
