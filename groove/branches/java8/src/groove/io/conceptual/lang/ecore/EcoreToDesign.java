package groove.io.conceptual.lang.ecore;

import groove.io.FileType;
import groove.io.conceptual.Design;
import groove.io.conceptual.Field;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.lang.DesignImporter;
import groove.io.conceptual.lang.ImportException;
import groove.io.conceptual.lang.InvalidTypeException;
import groove.io.conceptual.lang.Message;
import groove.io.conceptual.lang.Message.MessageType;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Type;
import groove.io.conceptual.value.ContainerValue;
import groove.io.conceptual.value.Object;
import groove.io.conceptual.value.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/** Importer for Ecore instance models. */
public class EcoreToDesign extends DesignImporter {
    /**
     * Creates an ECore design importer.
     * @param ecoreToGlos {@link EcoreToGlossary} to use for the corresponding glossary. The glossary should not contain errors.
     * @param filename Name of the design file to load
     * @throws ImportException When the file could not be properly loaded, or the glossary is invalid
     */
    public EcoreToDesign(EcoreToGlossary ecoreToGlos, String filename) throws ImportException {
        this.m_ecoreToGlos = ecoreToGlos;
        // "ecore" is the hardcoded string for an ecore type model
        this.m_glossary = this.m_ecoreToGlos.getGlossary("ecore");
        if (this.m_glossary == null) {
            throw new ImportException("Cannot load type model from given EcoreToType");
        }
        this.m_filename = filename;
    }

    // References to the Ecore type model information, retrieved from EcoreToType
    private final EcoreToGlossary m_ecoreToGlos;
    private final Glossary m_glossary;
    /** Name of the input file. */
    private final String m_filename;

    @Override
    public EcoreToDesign build() throws ImportException {
        ResourceSet rs = this.m_ecoreToGlos.getResourceSet();

        // Load the XMI model containing Ecore instance model
        File file = new File(this.m_filename);
        Resource resource;
        try (FileInputStream in = new FileInputStream(file)) {
            resource = rs.createResource(URI.createURI(this.m_filename));
            int timer = Timer.cont("Load Ecore");
            resource.load(in, null);
            Timer.stop(timer);
        } catch (FileNotFoundException e) {
            throw new ImportException("Cannot find file " + this.m_ecoreToGlos, e);
        } catch (IOException e) {
            throw new ImportException(e);
        }
        int timer = Timer.start("Ecore to IM");
        addDesign(buildDesign(resource, FileType.getPureName(file)));
        Timer.stop(timer);
        return this;
    }

    private Design buildDesign(Resource resource, String modelName) {
        // Create the Model based on TypeModel of ecoreType
        Design m = new Design(this.m_glossary, modelName);
        // Iterate over all objects in the Ecore instance and create Model Object where applicable
        Iterator<EObject> it = resource.getAllContents();
        while (it.hasNext()) {
            EObject obj = it.next();
            // This check is probably unnecessary
            if (obj.eClass().eClass().getName().equals("EClass")) {
                //count++;
                visitObject(m, obj);
            }
        }
        addDesign(m);
        return m;
    }

    /**
     * Visit an EObject, try to find the associated class in the type model and create Object in instance model
     * @param m Instance model to create objct for
     * @param eObject EOBject to translate
     * @return The translated Object, or null on error
     */
    private Object visitObject(Design m, EObject eObject) {
        if (this.m_objects.containsKey(eObject)) {
            return this.m_objects.get(eObject);
        }

        Name objName = getObjectName(eObject);
        Id clsId = EcoreUtil.idFromClassifier(eObject.eClass());
        Class cmClass = this.m_glossary.getClass(clsId);
        if (cmClass == null) {
            addMessage(new Message("Cannot find class " + clsId + " in type model",
                MessageType.ERROR));
            return null;
        }

        Object cmObject = new Object(cmClass, objName);
        m.addObject(cmObject);
        this.m_objects.put(eObject, cmObject);

        // Run through structural features in metamodel, and map the values (if any)
        for (EStructuralFeature feature : eObject.eClass().getEAllStructuralFeatures()) {
            if (feature.eClass().getName().equals("EReference")) {
                visitReference(m, cmObject, (EReference) feature, eObject.eGet(feature));
            } else if (feature.eClass().getName().equals("EAttribute")) {
                visitAttribute(m, cmObject, (EAttribute) feature, eObject.eGet(feature));
            }
        }

        return cmObject;
    }

    // Map to keep track of objects that have been generated
    private Map<EObject,Object> m_objects = new HashMap<EObject,Object>();

    /**
     * Visit an EReference and get the associated value. Value will be assigned to the objects corresponding field
     * @param m Instance model to work in
     * @param cmObject object to assign value to
     * @param eReference reference to get value from
     * @param value actual value of the reference to translate
     * @return The Value if translated, null on error
     */
    @SuppressWarnings("unchecked")
    private Value visitReference(Design m, Object cmObject, EReference eReference,
        java.lang.Object value) {
        // Happens if no value is assigned.
        if (value == null) {
            return null;
        }

        //Reference may be defined in supertype, so acquire field through that
        Id classId = EcoreUtil.idFromClassifier(eReference.getEContainingClass());
        Class refClass = this.m_glossary.getClass(classId);
        if (refClass == null) {
            addMessage(new Message("Cannot find class of reference " + eReference,
                MessageType.ERROR));
            return null;
        }
        Field f = refClass.getField(Name.getName(eReference.getName()));

        // When there are multiple values of a feature, an EcoreEList contains them
        if (eReference.isMany()) {
            ContainerValue cv = new ContainerValue((Container) f.getType());
            for (EObject target : (EList<EObject>) value) {
                Object refObj = visitObject(m, target);
                cv.addValue(refObj);
            }
            cmObject.setFieldValue(f, cv);
            return cv;
        } else {
            EObject target = (EObject) value;
            Object refObj = visitObject(m, target);
            if (refObj == null) {
                return null;
            }
            cmObject.setFieldValue(f, refObj);
            return refObj;
        }
    }

    /**
     * Visit an EAttribute and get the associated value. Value will be assigned to the objects corresponding field
     * @param m Instance model to work in
     * @param cmObject object to assign value to
     * @param eAttribute attribute to get value from
     * @param value actual value of the attribute to translate
     * @return The Value if translated, null on error
     */
    @SuppressWarnings("unchecked")
    private Value visitAttribute(Design m, Object cmObject, EAttribute eAttribute,
        java.lang.Object value) {
        // Happens if no value is assigned.
        if (value == null) {
            return null;
        }

        //Attribute may be defined in supertype, so acquire field through that
        Id classId = EcoreUtil.idFromClassifier(eAttribute.getEContainingClass());
        Class attrClass = this.m_glossary.getClass(classId);
        if (attrClass == null) {
            addMessage(new Message("Cannot find class of attribute " + eAttribute,
                MessageType.ERROR));
            return null;
        }
        Field f = attrClass.getField(Name.getName(eAttribute.getName()));

        if (f.getType() instanceof Container) {
            Container containerType = (Container) f.getType();
            ContainerValue cv = new ContainerValue(containerType);
            cmObject.setFieldValue(f, cv);

            Type subType = containerType.getType();
            // When there are multiple values of a feature, an EcoreEList contains them
            if (eAttribute.isMany()) {
                for (java.lang.Object target : (EList<Object>) value) {
                    try {
                        Value cmVal =
                            this.m_ecoreToGlos.objectToDataType(this.m_glossary, subType, target);
                        cv.addValue(cmVal);
                    } catch (InvalidTypeException e) {
                        addMessage(new Message(e.getMessage(), MessageType.ERROR));
                        continue;
                    }
                }
            } else {
                try {
                    Value cmVal =
                        this.m_ecoreToGlos.objectToDataType(this.m_glossary, subType, value);
                    cv.addValue(cmVal);
                } catch (InvalidTypeException e) {
                    addMessage(new Message(e.getMessage(), MessageType.ERROR));
                    return null;
                }
            }
            return cv;
        } else {
            try {
                Value cmVal =
                    this.m_ecoreToGlos.objectToDataType(this.m_glossary, f.getType(), value);
                cmObject.setFieldValue(f, cmVal);
                return cmVal;
            } catch (InvalidTypeException e) {
                addMessage(new Message(e.getMessage(), MessageType.ERROR));
                return null;
            }
        }
    }

    // Generates a name for the given EObject. This name is not guaranteed to be unique.
    // Currently based on the hashCode of the object.
    private Name getObjectName(EObject eObject) {
        String fragment = "";
        EObject current = eObject;
        while (current.eContainer() != null && current.eContainer() instanceof InternalEObject) {
            fragment =
                "/"
                    + ((InternalEObject) current.eContainer()).eURIFragmentSegment(current.eContainingFeature(),
                        current) + fragment;
            current = current.eContainer();
        }
        fragment = "/" + fragment;
        return Name.getName(fragment);
    }
}
