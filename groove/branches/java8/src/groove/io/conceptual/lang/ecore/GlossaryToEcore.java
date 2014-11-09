package groove.io.conceptual.lang.ecore;

import groove.io.conceptual.Field;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.lang.GlossaryExportBuilder;
import groove.io.conceptual.lang.Message;
import groove.io.conceptual.lang.Message.MessageType;
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
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.type.Type;
import groove.io.external.PortException;
import groove.util.Exceptions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;

/** Bridge from glossary to ECore export. */
public class GlossaryToEcore extends GlossaryExportBuilder<EcoreExport,EObject> {
    /** Constructs a new bridge for a given glossary and export object. */
    public GlossaryToEcore(Glossary glos, EcoreExport export) {
        super(glos, export);
    }

    @Override
    public void build() throws PortException {
        int timer = Timer.start("TM to Ecore");
        super.build();
        Timer.stop(timer);

        timer = Timer.start("Ecore save");
        Resource typeResource = getExport().getTypeResource(getGlossary().getName());
        EList<EObject> contents = typeResource.getContents();
        for (EPackage pkg : this.m_rootPackages) {
            contents.add(pkg);
        }
        Timer.stop(timer);
    }

    /** Generates a name for a given tuple type. */
    public String getTupleName(Tuple tuple) {
        return getGlossary().getTupleName(tuple);
    }

    /** Generates a name for a tuple type at a given index. */
    public String getTupleElementName(Tuple tuple, int index) {
        return "_" + index;
    }

    /** Convenience method to add a type concept and cast the result to an {@link EClass}. */
    public EClass addClassType(Type type) {
        return (EClass) add(type);
    }

    /** Convenience method to add a data type concept and cast the result to an {@link EDataType}. */
    public EDataType getEDataType(DataType dataType) {
        return (EDataType) add(dataType);
    }

    /** Convenience method to add a field and cast the result to an {@link EStructuralFeature}. */
    public EStructuralFeature addEStructuralFeature(Field field) {
        return (EStructuralFeature) add(field);
    }

    /** Generates an {@link EPackage} from a given ID. */
    private EPackage packageFromId(Id id) {
        EPackage result = this.m_packages.get(id);
        if (result == null) {
            if (id == Id.ROOT) {
                // This is actually an error in the metamodel
                result = g_EcoreFactory.createEPackage();
                result.setName("ROOT");
                addMessage(new Message(
                    "A package (ROOT) was generated for the root namespace, please check your identifiers",
                    MessageType.WARNING));
                this.m_rootPackages.add(result);
            } else {
                // No package yet. If not toplevel Id, recursively get that package
                result = g_EcoreFactory.createEPackage();
                result.setName(id.getName().toString());
                result.setNsPrefix(id.getName().toString());
                result.setNsURI(id.getName().toString());
                if (id.getNamespace() != Id.ROOT) {
                    EPackage topLevel = packageFromId(id.getNamespace());
                    topLevel.getESubpackages().add(result);
                } else {
                    this.m_rootPackages.add(result);
                }
            }
            this.m_packages.put(id, result);
        }
        return result;
    }

    private final Map<Id,EPackage> m_packages = new HashMap<Id,EPackage>();

    // To keep track of generated packages
    private final Set<EPackage> m_rootPackages = new HashSet<EPackage>();

    /** Generates a fresh container name. */
    private String createContainerName() {
        this.nrContainer++;
        return "ContainerClass_" + this.nrContainer;
    }

    private int nrContainer = 0;

    @Override
    protected EObject addDataType(DataType t) {
        EDataType result;
        switch (t.getKind()) {
        case STRING_TYPE:
            put(t, result = g_EcorePackage.getEString());
            break;
        case BOOL_TYPE:
            put(t, result = g_EcorePackage.getEBoolean());
            break;
        case INT_TYPE:
            put(t, result = g_EcorePackage.getEInt());
            break;
        case REAL_TYPE:
            put(t, result = g_EcorePackage.getEFloat());
            break;
        case CUSTOM_TYPE:
            result = g_EcoreFactory.createEDataType();
            put(t, result);

            CustomDataType cmDataType = (CustomDataType) t;
            result.setName(cmDataType.getId().getName().toString());
            // Forcing to the string class, as it always has a string representation.
            // This is a limitation of the conceptual model (doesn't store other type information)
            result.setInstanceClass(String.class);
            EPackage typePackage = packageFromId(cmDataType.getId().getNamespace());
            typePackage.getEClassifiers().add(result);
            break;
        default:
            throw Exceptions.illegalArg("Parameter kind %s should be data type", t.getKind());
        }
        return result;
    }

    @Override
    protected EObject addClass(Class cmClass) {
        EObject result;
        if (cmClass.isProper()) {
            EClass eClass = g_EcoreFactory.createEClass();
            put(cmClass, eClass);

            eClass.setName(cmClass.getId().getName().toString());

            EPackage classPackage = packageFromId(cmClass.getId().getNamespace());
            classPackage.getEClassifiers().add(eClass);

            // Map supertypes to ecore supertypes
            for (Class superClass : cmClass.getSuperClasses()) {
                EObject eObj = add(superClass);
                eClass.getESuperTypes().add((EClass) eObj);
            }

            for (Field field : cmClass.getFields()) {
                EStructuralFeature eStructFeat = (EStructuralFeature) add(field);
                eClass.getEStructuralFeatures().add(eStructFeat);
            }
            result = eClass;
        } else {
            result = add(cmClass.getProperClass());
            put(cmClass, result);
        }
        return result;
    }

    @Override
    protected EObject addField(Field field) {
        // Map fields, either as attribute or reference
        Type fieldType = field.getType();
        // Ecore defaults, only changed if an container
        boolean ordered = true;
        boolean unique = true;
        if (fieldType instanceof Container) {
            Container cmContainer = (Container) fieldType;
            fieldType = cmContainer.getType();
            ordered = cmContainer.getContainerType().isOrdered();
            unique = cmContainer.getContainerType().isUnique();
        }

        EStructuralFeature result = null;
        // Tuples/Containers are represented by classes, so would be a reference
        if (fieldType instanceof Class || fieldType instanceof Tuple
            || fieldType instanceof Container) {
            // Create EReference
            result = g_EcoreFactory.createEReference();
        } else {
            // Create EAttribute
            result = g_EcoreFactory.createEAttribute();
        }
        put(field, result);

        result.setName(field.getName().toString());
        result.setOrdered(ordered);
        result.setUnique(unique);

        EObject eType = add(fieldType);
        result.setEType((EClassifier) eType);

        if (field.getUpperBound() == -1) {
            result.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
        } else {
            result.setUpperBound(field.getUpperBound());
        }
        if (field.getType() instanceof Class && !((Class) field.getType()).isProper()) {
            result.setLowerBound(0);
        } else {
            result.setLowerBound(field.getLowerBound());
        }
        return result;
    }

    @Override
    protected EObject addContainer(Container container, String base) {
        // Create class for container
        EClass result = g_EcoreFactory.createEClass();
        put(container, result);

        result.setName(createContainerName());

        // Find matching field and package
        EPackage containerPackage = null;
        Container topContainer = container;
        while (topContainer.getParent() != null) {
            topContainer = topContainer.getParent();
        }
        if (topContainer.getField() != null) {
            Field f = topContainer.getField();
            Class c = f.getDefiningClass();
            EClass eClass = (EClass) add(c);
            containerPackage = eClass.getEPackage();
        } else {
            containerPackage = packageFromId(Id.ROOT);
        }
        containerPackage.getEClassifiers().add(result);

        // Create value reference
        EStructuralFeature eContainerFeature = null;
        if (container.getType() instanceof Class || container.getType() instanceof Tuple
            || container.getType() instanceof Container) {
            // Create EReference
            eContainerFeature = g_EcoreFactory.createEReference();
        } else {
            // Create EAttribute
            eContainerFeature = g_EcoreFactory.createEAttribute();
        }

        eContainerFeature.setName("value");
        if (container.getType() instanceof Container) {
            Container subType = (Container) container.getType();
            eContainerFeature.setOrdered(subType.getContainerType() == Kind.ORD
                || subType.getContainerType() == Kind.SEQ);
            eContainerFeature.setUnique(subType.getContainerType() == Kind.SET
                || subType.getContainerType() == Kind.ORD);
        } else {
            eContainerFeature.setOrdered(false);
            eContainerFeature.setUnique(true);
        }

        EObject eType = add(container.getType());
        eContainerFeature.setEType((EClassifier) eType);

        eContainerFeature.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
        eContainerFeature.setLowerBound(0);

        result.getEStructuralFeatures().add(eContainerFeature);
        return result;
    }

    @Override
    protected EObject addEnum(Enum envm) {
        EEnum eEnum = g_EcoreFactory.createEEnum();
        put(envm, eEnum);

        eEnum.setName(envm.getId().getName().toString());
        EPackage enumPackage = packageFromId(envm.getId().getNamespace());
        enumPackage.getEClassifiers().add(eEnum);

        List<EEnumLiteral> eLiterals = eEnum.getELiterals();
        for (Name litName : envm.getLiterals()) {
            EEnumLiteral eEnumLit = g_EcoreFactory.createEEnumLiteral();
            eEnumLit.setName(litName.toString());
            //eEnumLit.setLiteral(litName.toString());

            eLiterals.add(eEnumLit);
        }
        return eEnum;
    }

    @Override
    protected EObject addTuple(Tuple tuple) {
        EPackage firstRoot = this.m_rootPackages.iterator().next();
        EClass result = g_EcoreFactory.createEClass();
        put(tuple, result);
        result.setName(getGlossary().getTupleName(tuple));

        firstRoot.getEClassifiers().add(result);

        int typeIndex = 1;
        for (Type type : tuple.getTypes()) {
            Field typeField =
                new Field(Name.getName(getTupleElementName(tuple, typeIndex++)), type, 1, 1);
            EStructuralFeature eStructFeat = (EStructuralFeature) add(typeField);
            result.getEStructuralFeatures().add(eStructFeat);
        }
        return result;
    }

    @Override
    protected EObject addAbstractProp(AbstractProperty prop) {
        put(prop, null);
        EClass eClass = (EClass) add(prop.getAbstractClass());
        eClass.setAbstract(true);
        return null;
    }

    @Override
    protected EObject addContainmentProp(ContainmentProperty prop) {
        put(prop, null);
        EObject obj = add(prop.getField());
        if (obj instanceof EReference) {
            EReference eRef = (EReference) obj;
            eRef.setContainment(true);
            // Force containment to be unique. This may cause issues down the road for instances,
            // but GROOVE should forbid this anyway when implemented
            eRef.setUnique(true);
        } else {
            // TODO
        }
        return null;
    }

    @Override
    protected EObject addIdentityProp(IdentityProperty prop) {
        put(prop, null);
        for (Field field : prop.getFields()) {
            EObject obj = add(field);
            if (obj instanceof EAttribute) {
                EAttribute eAttr = (EAttribute) obj;
                eAttr.setID(true);
                // Only set one field as ID, Ecore doesn't support multiple IDs
                break;
            } else {
                // TODO
            }
        }
        return null;
    }

    @Override
    protected EObject addKeysetProp(KeysetProperty prop) {
        put(prop, null);
        EObject objRef = add(prop.getRelField());
        if (objRef instanceof EReference) {
            EReference eRef = (EReference) objRef;
            List<EAttribute> keyAttribs = eRef.getEKeys();

            for (Field field : prop.getKeyFields()) {
                EObject obj = add(field);
                if (obj instanceof EAttribute) {
                    EAttribute eAttr = (EAttribute) obj;
                    keyAttribs.add(eAttr);
                } else {
                    // TODO
                }
            }
        } else {
            // TODO
        }
        return null;
    }

    @Override
    protected EObject addOppositeProp(OppositeProperty prop) {
        put(prop, null);
        EObject obj1 = add(prop.getField1());
        EObject obj2 = add(prop.getField2());
        if (obj1 instanceof EReference && obj2 instanceof EReference) {
            EReference eRef1 = (EReference) obj1;
            EReference eRef2 = (EReference) obj2;
            eRef1.setEOpposite(eRef2);
        } else {
            // TODO
        }
        return null;
    }

    @Override
    protected EObject addDefaultValueProp(DefaultValueProperty prop) {
        add(prop, null);
        EObject obj = add(prop.getField());
        if (obj instanceof EAttribute) {
            EAttribute eAttr = (EAttribute) obj;
            eAttr.setDefaultValueLiteral(prop.getDefaultValue().toString());
        } else {
            // TODO
        }
        return null;
    }

    private static final EcoreFactory g_EcoreFactory = EcoreFactory.eINSTANCE;
    private static final EcorePackage g_EcorePackage = EcorePackage.eINSTANCE;
}
