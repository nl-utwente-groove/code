package groove.io.conceptual.lang.ecore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import groove.grammar.QualName;
import groove.io.conceptual.Field;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.lang.ImportException;
import groove.io.conceptual.lang.InvalidTypeException;
import groove.io.conceptual.lang.Message;
import groove.io.conceptual.lang.Message.MessageType;
import groove.io.conceptual.lang.TypeImporter;
import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.IdentityProperty;
import groove.io.conceptual.property.KeysetProperty;
import groove.io.conceptual.property.OppositeProperty;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Container.Kind;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.DataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Type;
import groove.io.conceptual.value.EnumValue;
import groove.io.conceptual.value.Value;

@SuppressWarnings("javadoc")
public class EcoreToType extends TypeImporter {
    // Resource containing Ecore type model
    private Resource r = null;
    private ResourceSet rs = null;

    // Name of TypeModel, simply "ecore"
    private QualName m_typeName = QualName.name("ecore");

    // Map to keep track of Java class names for custom data types. Used when importing instance models
    // Strictly speaking this should be linked to a single TypeModel, but since only one Ecore type model exists per instance of this class, this should be fine.
    private Map<String,Id> m_customDatatypeInstances = new HashMap<>();

    /**
     * Handler for Ecore type models to be converted to the conceptual model
     * @param typeModel URI for the Ecore model to be loaded
     */
    public EcoreToType(String typeModel) throws ImportException {
        // Create new ResourceSet and register an XMI model loader (for all filetypes)
        this.rs = new ResourceSetImpl();
        this.rs.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put("*", new XMIResourceFactoryImpl());

        // Load the XMI model containing Ecore type model
        try (FileInputStream in = new FileInputStream(typeModel)) {
            this.r = this.rs.createResource(URI.createURI(typeModel));
            int timer = Timer.start("Load Ecore");
            this.r.load(in, null);
            org.eclipse.emf.ecore.util.EcoreUtil.resolveAll(this.rs);
            Timer.stop(timer);
        } catch (FileNotFoundException e) {
            throw new ImportException("Cannot find file " + typeModel, e);
        } catch (IOException e) {
            throw new ImportException(e);
        }

        /*
        Iterator<EObject> it = this.r.getAllContents();
        EObject topObject = null;
        if (!it.hasNext() || !(topObject = it.next()).eClass().getName().equals("EPackage")) {
            throw new ImportException("Ecore type model has no root package");
        }
        
        m_typeName = ((EPackage) topObject).getName();
        */

        int timer = Timer.start("Ecore to TM");
        buildTypeModel();
        Timer.stop(timer);
    }

    /**
     * Get the resource set associated with this type model (used for loading instance models)
     * @return ResourceSet containing the Ecore model and package reference within it (use for loading instance models)
     */
    public ResourceSet getResourceSet() {
        return this.rs;
    }

    @Override
    public TypeModel getTypeModel(QualName modelName) {
        return this.m_typeModels.get(modelName);
    }

    private void buildTypeModel() {
        TypeModel tm = new TypeModel(this.m_typeName);
        Iterator<EObject> it = this.r.getAllContents();
        // It can happen that the same entry is visit multiple times when browsing the tree of dependent elements
        // The TypeModel ought to keep track of all elements and return the proper reference if this happens
        while (it.hasNext()) {
            EObject obj = it.next();
            if (obj.eClass()
                .getName()
                .equals("EClass")) {
                visitClass(tm, (EClass) obj);
            } else if (obj.eClass()
                .getName()
                .equals("EEnum")) {
                visitEnum(tm, (EEnum) obj);
            } else if (obj.eClass()
                .getName()
                .equals("EPackage")) {
                visitPackage((EPackage) obj);
            } else if (obj.eClass()
                .getName()
                .equals("EDataType")) {
                visitDataType(tm, (EDataType) obj);
            }
        }

        //System.out.println("Ecore elements: " + count);

        tm.resolve();

        this.m_typeModels.put(this.m_typeName, tm);
    }

    private Class visitClass(TypeModel mm, EClass eClass) {
        if (eClass.eIsProxy()) {
            return mm.getClass(Id.getId(Id.ROOT, Name.getName("Proxy")), true);
        }
        Id clsID = EcoreUtil.idFromClassifier(eClass);
        if (mm.hasClass(clsID)) {
            return mm.getClass(clsID);
        }

        Class cmClass = mm.getClass(clsID, true);
        if (eClass.isAbstract() || eClass.isInterface()) {
            AbstractProperty p = new AbstractProperty(cmClass);
            mm.addProperty(p);
        }

        for (EAttribute eAttribute : eClass.getEAttributes()) {
            visitAttribute(mm, cmClass, eAttribute);
        }

        for (EReference eReference : eClass.getEReferences()) {
            visitReference(mm, cmClass, eReference);
        }

        for (EClass eSuperClass : eClass.getESuperTypes()) {
            Class superClass = visitClass(mm, eSuperClass);
            cmClass.addSuperClass(superClass);
        }

        // Handle class iD
        if (eClass.getEIDAttribute() != null) {
            if (eClass.getEIDAttribute()
                .getEContainingClass() == eClass) {
                Name attrName = Name.getName(eClass.getEIDAttribute()
                    .getName());
                IdentityProperty p = new IdentityProperty(cmClass, attrName);
                mm.addProperty(p);
            }
        }

        // ID and Keyset handled by reference and attribute visitors
        return cmClass;
    }

    private Type visitDataType(TypeModel mm, EDataType eDataType) {
        // Enums have special visitor
        if (eDataType instanceof EEnum) {
            return visitEnum(mm, (EEnum) eDataType);
        }
        Id dataID = EcoreUtil.idFromClassifier(eDataType);
        // Check if custom
        if (mm.hasDatatype(dataID)) {
            return mm.getDatatype(dataID);
        }

        // Check if built-in
        if (EcoreUtil.g_knownTypes.containsKey(eDataType.getName())) {
            return EcoreUtil.g_knownTypes.get(eDataType.getName());
        }

        // Not in tables, create custom datatype
        CustomDataType cmDatatype = mm.getDatatype(dataID, true);
        this.m_customDatatypeInstances.put(eDataType.getInstanceClassName(), dataID);

        return cmDatatype;
    }

    private Enum visitEnum(TypeModel mm, EEnum eEnum) {
        Id enumID = EcoreUtil.idFromClassifier(eEnum);

        if (mm.hasEnum(enumID)) {
            return mm.getEnum(enumID);
        }

        Enum cmEnum = mm.getEnum(enumID, true);

        for (EEnumLiteral eEnumLiteral : eEnum.getELiterals()) {
            Name litName = Name.getName(eEnumLiteral.getName());
            cmEnum.addLiteral(litName);
        }

        return cmEnum;
    }

    private void visitAttribute(TypeModel mm, Class cmClass, EAttribute eAttribute) {
        Name attrName = Name.getName(eAttribute.getName());

        // Get the correct type
        Type attribType = null;
        if (eAttribute.getEType() instanceof EEnum) {
            attribType = visitEnum(mm, (EEnum) eAttribute.getEType());
        } else if (eAttribute.getEType() instanceof EDataType) {
            attribType = visitDataType(mm, (EDataType) eAttribute.getEType());
            if (attribType == null) {
                addMessage(new Message("Unsupported EDataType " + eAttribute.getEType()
                    .getName(), MessageType.ERROR));
            }
        } else {
            // Cannot handle other types as attribute
            addMessage(new Message("Invalid type as attribute " + eAttribute, MessageType.ERROR));
        }
        if (attribType == null) {
            // error message already generated
            return;
        }
        // Handle container type
        if (eAttribute.getUpperBound() > 1 || eAttribute.getUpperBound() == -1) {
            Kind type = eAttribute.isUnique() ? (eAttribute.isOrdered() ? Kind.ORD : Kind.SET) : // Unique
                (eAttribute.isOrdered() ? Kind.SEQ : Kind.BAG); // Non-unique
            attribType = new Container(type, attribType);
        }

        if (eAttribute.getDefaultValue() != null) {

            Object value = eAttribute.getDefaultValue();
            // Ignore default value defined by type
            if (value != eAttribute.getEType()
                .getDefaultValue()) {
                try {
                    Value defaultVal = objectToDataType(mm, attribType, value);
                    if (!attribType.acceptValue(defaultVal)) {
                        addMessage(
                            new Message("Incorrect value type of default value " + defaultVal,
                                MessageType.ERROR));
                    } else {
                        mm.addProperty(new DefaultValueProperty(cmClass, attrName, defaultVal));
                    }
                } catch (InvalidTypeException e) {
                    addMessage(new Message(e.getMessage(), MessageType.ERROR));
                }
            }
        }

        // Add the attribute
        cmClass.addField(new Field(Name.getName(eAttribute.getName()), attribType,
            eAttribute.getLowerBound(), eAttribute.getUpperBound()));
    }

    private void visitReference(TypeModel mm, Class cmClass, EReference eReference) {
        Name refName = Name.getName(eReference.getName());

        // Get the correct type
        Type fieldType = null;
        if (eReference.getEType() instanceof EClass) {
            fieldType = visitClass(mm, (EClass) eReference.getEType());
        } else {
            // Cannot handle  other types as references
            addMessage(new Message("Invalid type as reference " + eReference, MessageType.ERROR));
        }

        // Handle keyset
        if (eReference.getEKeys()
            .size() > 0) {
            List<Name> keyNames = new ArrayList<>();
            for (EAttribute attr : eReference.getEKeys()) {
                keyNames.add(Name.getName(attr.getName()));
            }

            KeysetProperty p = new KeysetProperty(cmClass, refName, (Class) fieldType,
                keyNames.toArray(new Name[keyNames.size()]));
            mm.addProperty(p);
        }

        // Handle opposite
        if (eReference.getEOpposite() != null) {
            EReference eOpposite = eReference.getEOpposite();
            Name oppositeName = Name.getName(eOpposite.getName());
            Class oppositeClass = visitClass(mm, eOpposite.getEContainingClass());

            OppositeProperty p =
                new OppositeProperty(cmClass, refName, oppositeClass, oppositeName);
            mm.addProperty(p);
        }

        // Handle containment
        if (eReference.isContainment()) {
            ContainmentProperty p = new ContainmentProperty(cmClass, refName);
            mm.addProperty(p);
        }

        if (eReference.getDefaultValue() != null) {
            addMessage(new Message("eReference default value not supported"));
        }

        // Handle container type
        if (eReference.getUpperBound() > 1 || eReference.getUpperBound() == -1) {
            Kind type = eReference.isUnique() ? (eReference.isOrdered() ? Kind.ORD : Kind.SET) : // Unique
                (eReference.isOrdered() ? Kind.SEQ : Kind.BAG); // Non-unique
            fieldType = new Container(type, fieldType);
        }

        // Add the reference to the class
        cmClass.addField(
            new Field(refName, fieldType, eReference.getLowerBound(), eReference.getUpperBound()));
    }

    // Inserts the package in the resourceset, so it can be used to load instance models
    private void visitPackage(EPackage ePackage) {
        //required for namespace lookups when importing instance model
        String nsURI = ePackage.getNsURI();
        if (nsURI == null) {
            nsURI = ePackage.getName();
            ePackage.setNsURI(nsURI);
        }

        // register all packages we find in the ResourceSet
        this.rs.getPackageRegistry()
            .put(ePackage.getNsURI(), ePackage);
    }

    /**
     * Converts an Object from an ecore model (type or instance) to a Value if its either a value for an EEnum or EDataType.
     * @param type The type of the value to convert to
     * @param ecoreValue The value to translate
     * @return The translated Value, or null on error
     * @throws InvalidTypeException When the conversion fails due to a type mismatch, or invalid value
     */
    public Value objectToDataType(TypeModel tm, Type type, Object ecoreValue)
        throws InvalidTypeException {
        if (!(type instanceof DataType)) {
            throw new InvalidTypeException(
                "Cannot convert Ecore object to non-datatype in type model");
        }
        Value value = null;
        if (ecoreValue instanceof EEnumLiteral) {
            if (type instanceof Enum) {
                value =
                    new EnumValue((Enum) type, Name.getName(((EEnumLiteral) ecoreValue).getName()));
            } else {
                throw new InvalidTypeException("Type error in ecore type model");
            }
        } else if (ecoreValue instanceof EObject) {
            // Cannot create Object without instance model
            throw new InvalidTypeException("Unsupported value type: Object");
        } else {
            // Most likely a Java object. Try to map it to Ecore, either from the know data types or the custom data types
            String className = ecoreValue.getClass()
                .getCanonicalName();
            if (type instanceof CustomDataType) {
                if (this.m_customDatatypeInstances.containsKey(className)) {
                    Id dataId = this.m_customDatatypeInstances.get(className);
                    CustomDataType dt = tm.getDatatype(dataId);
                    if (dt == null || !(dt.equals(type))) {
                        throw new InvalidTypeException("Unknown type class: " + className);
                    } else {
                        value = dt.valueFromString(ecoreValue.toString());
                    }
                } else {
                    throw new InvalidTypeException("Unknown custom type: " + type);
                }
            } else {
                if (EcoreUtil.g_knownInstanceTypes.containsKey(className)) {
                    String typeString = EcoreUtil.g_knownInstanceTypes.get(className);
                    Type knownType = EcoreUtil.g_knownTypes.get(typeString);
                    if (type.equals(knownType)) {
                        value = (((DataType) type).valueFromString(ecoreValue.toString()));
                    } else {
                        throw new InvalidTypeException("Type error in ecore type model");
                    }
                } else {
                    throw new InvalidTypeException("Unknown datatype requested: " + type);
                }
            }
        }
        return value;
    }
}
