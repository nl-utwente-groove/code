package groove.io.conceptual.lang.ecore;

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
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;

import groove.io.conceptual.Field;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.lang.Message;
import groove.io.conceptual.lang.Message.MessageType;
import groove.io.conceptual.lang.TypeExporter;
import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.IdentityProperty;
import groove.io.conceptual.property.KeysetProperty;
import groove.io.conceptual.property.OppositeProperty;
import groove.io.conceptual.type.BoolType;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Container.Kind;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.DataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.IntType;
import groove.io.conceptual.type.RealType;
import groove.io.conceptual.type.StringType;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.type.Type;
import groove.io.external.PortException;

@SuppressWarnings("javadoc")
public class TypeToEcore extends TypeExporter<EObject> {
    private static final EcoreFactory g_EcoreFactory = EcoreFactory.eINSTANCE;
    private static final EcorePackage g_EcorePackage = EcorePackage.eINSTANCE;

    // Resource containing Ecore type model
    private EcoreResource m_ecoreResource;

    // Mainly to get tuple names
    private TypeModel m_currentTypeModel;

    // To keep track of generated packages
    private Map<Id,EPackage> m_packages = new HashMap<>();
    private Set<EPackage> m_rootPackages = new HashSet<>();

    private int nrContainer = 0;

    public TypeToEcore(EcoreResource resource) {
        this.m_ecoreResource = resource;
    }

    @Override
    public groove.io.conceptual.lang.ExportableResource getResource() {
        return this.m_ecoreResource;
    }

    @Override
    public void addTypeModel(TypeModel typeModel) throws PortException {
        int timer = Timer.start("TM to Ecore");
        this.m_currentTypeModel = typeModel;
        visitTypeModel(typeModel);
        Timer.stop(timer);

        timer = Timer.start("Ecore save");
        Resource typeResource = this.m_ecoreResource.getTypeResource(typeModel.getQualName());
        EList<EObject> contents = typeResource.getContents();

        for (EPackage pkg : this.m_rootPackages) {
            //pkg.setNsPrefix(pkg.getName().toLowerCase());
            //pkg.setNsURI("file://./" + typeModel.getName() + ".ecore#" + pkg.getName().toLowerCase());
            contents.add(pkg);
        }
        Timer.stop(timer);
    }

    public String getTupleName(Tuple tuple) {
        return this.m_currentTypeModel.getTupleName(tuple);
    }

    public String getTupleElementName(Tuple tuple, int index) {
        return "_" + index;
    }

    // Mainly for InstanceToEcore
    public EClass getTupleClass(Tuple tuple) {
        return (EClass) getElement(tuple);
    }

    // Mainly for InstanceToEcore
    public EClass getContainerClass(Container container) {
        return (EClass) getElement(container);
    }

    // Mainly for InstanceToEcore
    public EDataType getEDataType(DataType dataType) {
        return (EDataType) getElement(dataType);
    }

    // Mainly for InstanceToEcore
    public EClass getEClass(Class cmClass) {
        return (EClass) getElement(cmClass);
    }

    // Mainly for InstanceToEcore
    public EStructuralFeature getEStructuralFeature(Field field) {
        EStructuralFeature eFeature = (EStructuralFeature) getElement(field);
        return eFeature;
    }

    //Does as the name says
    private EPackage packageFromId(Id id) {
        if (this.m_packages.containsKey(id)) {
            return this.m_packages.get(id);
        }

        if (id == Id.ROOT) {
            // This is actually an error in the metamodel
            EPackage idPackage = g_EcoreFactory.createEPackage();
            idPackage.setName("ROOT");
            this.m_packages.put(id, idPackage);
            addMessage(new Message(
                "A package (ROOT) was generated for the root namespace, please check your identifiers",
                MessageType.WARNING));
            this.m_rootPackages.add(idPackage);
            return idPackage;
        }

        // No package yet. If not toplevel Id, recursively get that package
        EPackage idPackage = g_EcoreFactory.createEPackage();
        idPackage.setName(id.getName()
            .toString());
        idPackage.setNsPrefix(id.getName()
            .toString());
        idPackage.setNsURI(id.getName()
            .toString());
        this.m_packages.put(id, idPackage);

        if (id.getNamespace() != Id.ROOT) {
            EPackage topLevel = packageFromId(id.getNamespace());
            topLevel.getESubpackages()
                .add(idPackage);
        } else {
            this.m_rootPackages.add(idPackage);
        }

        return idPackage;
    }

    @Override
    public void visit(DataType t, String param) {
        if (hasElement(t)) {
            return;
        }

        if (t instanceof StringType) {
            setElement(t, g_EcorePackage.getEString());
        } else if (t instanceof BoolType) {
            setElement(t, g_EcorePackage.getEBoolean());
        } else if (t instanceof IntType) {
            setElement(t, g_EcorePackage.getEInt());
        } else if (t instanceof RealType) {
            setElement(t, g_EcorePackage.getEFloat());
        } else if (t instanceof CustomDataType) {
            EDataType eDataType = g_EcoreFactory.createEDataType();
            setElement(t, eDataType);

            CustomDataType cmDataType = (CustomDataType) t;
            eDataType.setName(cmDataType.getId()
                .getName()
                .toString());
            // Forcing to the string class, as it always has a string representation.
            // This is a limitation of the conceptual model (doesn't store other type information)
            eDataType.setInstanceClass(String.class);
            EPackage typePackage = packageFromId(cmDataType.getId()
                .getNamespace());
            typePackage.getEClassifiers()
                .add(eDataType);
        }
    }

    @Override
    public void visit(Class cmClass, String param) {
        if (hasElement(cmClass)) {
            return;
        }

        if (!cmClass.isProper()) {
            EObject propElem = getElement(cmClass.getProperClass());
            if (!hasElement(cmClass)) {
                setElement(cmClass, propElem);
            }
            return;
        }

        EClass eClass = g_EcoreFactory.createEClass();
        setElement(cmClass, eClass);

        eClass.setName(cmClass.getId()
            .getName()
            .toString());

        EPackage classPackage = packageFromId(cmClass.getId()
            .getNamespace());
        classPackage.getEClassifiers()
            .add(eClass);

        // Map supertypes to ecore supertypes
        for (Class superClass : cmClass.getSuperClasses()) {
            EObject eObj = getElement(superClass);
            eClass.getESuperTypes()
                .add((EClass) eObj);
        }

        for (Field field : cmClass.getFields()) {
            EStructuralFeature eStructFeat = (EStructuralFeature) getElement(field);
            eClass.getEStructuralFeatures()
                .add(eStructFeat);
        }
    }

    // Map fields, either as attribute or reference
    @Override
    public void visit(Field field, String param) {
        if (hasElement(field)) {
            return;
        }

        Type fieldType = field.getType();
        // Ecore defaults, only changed if an container
        boolean ordered = true;
        boolean unique = true;
        if (fieldType instanceof Container) {
            Container cmContainer = (Container) fieldType;
            fieldType = cmContainer.getType();

            if (cmContainer.getContainerType() == Kind.SET
                || cmContainer.getContainerType() == Kind.BAG) {
                ordered = false;
            }
            if (cmContainer.getContainerType() == Kind.BAG
                || cmContainer.getContainerType() == Kind.SEQ) {
                unique = false;
            }
        }

        EStructuralFeature eFieldFeature = null;
        // Tuples/Containers are represented by classes, so would be a reference
        if (fieldType instanceof Class || fieldType instanceof Tuple
            || fieldType instanceof Container) {
            // Create EReference
            eFieldFeature = g_EcoreFactory.createEReference();
        } else {
            // Create EAttribute
            eFieldFeature = g_EcoreFactory.createEAttribute();
        }
        setElement(field, eFieldFeature);

        eFieldFeature.setName(field.getName()
            .toString());
        eFieldFeature.setOrdered(ordered);
        eFieldFeature.setUnique(unique);

        EObject eType = getElement(fieldType);
        eFieldFeature.setEType((EClassifier) eType);

        if (field.getUpperBound() == -1) {
            eFieldFeature.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
        } else {
            eFieldFeature.setUpperBound(field.getUpperBound());
        }
        if (field.getType() instanceof Class && !((Class) field.getType()).isProper()) {
            eFieldFeature.setLowerBound(0);
        } else {
            eFieldFeature.setLowerBound(field.getLowerBound());
        }
    }

    @Override
    public void visit(Container container, String param) {
        if (hasElement(container)) {
            return;
        }

        // Create class for container
        EClass eContainerClass = g_EcoreFactory.createEClass();
        setElement(container, eContainerClass);

        this.nrContainer++;
        eContainerClass.setName("ContainerClass_" + this.nrContainer);

        // Find matching field and package
        EPackage containerPackage = null;
        Container topContainer = container;
        while (topContainer.getParent() != null) {
            topContainer = topContainer.getParent();
        }
        if (topContainer.getField() != null) {
            Field f = topContainer.getField();
            Class c = f.getDefiningClass();
            EClass eClass = (EClass) getElement(c);
            containerPackage = eClass.getEPackage();
        } else {
            containerPackage = packageFromId(Id.ROOT);
        }
        containerPackage.getEClassifiers()
            .add(eContainerClass);

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
            eContainerFeature.setOrdered(
                subType.getContainerType() == Kind.ORD || subType.getContainerType() == Kind.SEQ);
            eContainerFeature.setUnique(
                subType.getContainerType() == Kind.SET || subType.getContainerType() == Kind.ORD);
        } else {
            eContainerFeature.setOrdered(false);
            eContainerFeature.setUnique(true);
        }

        EObject eType = getElement(container.getType());
        eContainerFeature.setEType((EClassifier) eType);

        eContainerFeature.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
        eContainerFeature.setLowerBound(0);

        eContainerClass.getEStructuralFeatures()
            .add(eContainerFeature);
    }

    @Override
    public void visit(Enum enum1, String param) {
        if (hasElement(enum1)) {
            return;
        }

        EEnum eEnum = g_EcoreFactory.createEEnum();
        setElement(enum1, eEnum);

        eEnum.setName(enum1.getId()
            .getName()
            .toString());

        EPackage enumPackage = packageFromId(enum1.getId()
            .getNamespace());
        enumPackage.getEClassifiers()
            .add(eEnum);

        List<EEnumLiteral> eLiterals = eEnum.getELiterals();
        for (Name litName : enum1.getLiterals()) {
            EEnumLiteral eEnumLit = g_EcoreFactory.createEEnumLiteral();
            eEnumLit.setName(litName.toString());
            //eEnumLit.setLiteral(litName.toString());

            eLiterals.add(eEnumLit);
        }
    }

    @Override
    public void visit(Tuple tuple, String param) {
        if (hasElement(tuple)) {
            return;
        }

        EPackage firstRoot = this.m_rootPackages.iterator()
            .next();
        EClass eClass = g_EcoreFactory.createEClass();
        setElement(tuple, eClass);

        eClass.setName(getTupleName(tuple));

        firstRoot.getEClassifiers()
            .add(eClass);

        int typeIndex = 1;
        for (Type type : tuple.getTypes()) {
            Field typeField =
                new Field(Name.getName(getTupleElementName(tuple, typeIndex++)), type, 1, 1);
            EStructuralFeature eStructFeat = (EStructuralFeature) getElement(typeField);
            eClass.getEStructuralFeatures()
                .add(eStructFeat);
        }
    }

    @Override
    public void visit(AbstractProperty abstractProperty, String param) {
        if (hasElement(abstractProperty)) {
            return;
        }

        EClass eClass = (EClass) getElement(abstractProperty.getAbstractClass());
        eClass.setAbstract(true);

        setElement(abstractProperty, null);
    }

    @Override
    public void visit(ContainmentProperty containmentProperty, String param) {
        if (hasElement(containmentProperty)) {
            return;
        }

        EObject obj = getElement(containmentProperty.getField());
        if (!(obj instanceof EReference)) {
            //TODO
            return;
        }

        EReference eRef = (EReference) obj;
        eRef.setContainment(true);

        // Force containment to be unique. This may cause issues down the road for instances,
        // but GROOVE should forbid this anyway when implemented
        eRef.setUnique(true);

        setElement(containmentProperty, null);
    }

    @Override
    public void visit(IdentityProperty identityProperty, String param) {
        if (hasElement(identityProperty)) {
            return;
        }

        for (Field field : identityProperty.getFields()) {
            EObject obj = getElement(field);
            if (!(obj instanceof EAttribute)) {
                //TODO
                continue;
            }

            EAttribute eAttr = (EAttribute) obj;
            eAttr.setID(true);

            // Only set one field as ID, Ecore doesn't support multiple IDs
            break;
        }

        setElement(identityProperty, null);
    }

    @Override
    public void visit(KeysetProperty keysetProperty, String param) {
        if (hasElement(keysetProperty)) {
            return;
        }

        EObject objRef = getElement(keysetProperty.getRelField());
        if (!(objRef instanceof EReference)) {
            //TODO
        }

        EReference eRef = (EReference) objRef;
        List<EAttribute> keyAttribs = eRef.getEKeys();

        for (Field field : keysetProperty.getKeyFields()) {
            EObject obj = getElement(field);
            if (!(obj instanceof EAttribute)) {
                //TODO
            }

            EAttribute eAttr = (EAttribute) obj;
            keyAttribs.add(eAttr);
        }

        setElement(keysetProperty, null);
    }

    @Override
    public void visit(OppositeProperty oppositeProperty, String param) {
        if (hasElement(oppositeProperty)) {
            return;
        }

        EObject obj1 = getElement(oppositeProperty.getField1());
        EObject obj2 = getElement(oppositeProperty.getField2());
        if (!(obj1 instanceof EReference) || !(obj2 instanceof EReference)) {
            //TODO
        }

        EReference eRef1 = (EReference) obj1;
        EReference eRef2 = (EReference) obj2;
        eRef1.setEOpposite(eRef2);

        setElement(oppositeProperty, null);
    }

    @Override
    public void visit(DefaultValueProperty defaultValueProperty, String param) {
        if (hasElement(defaultValueProperty)) {
            return;
        }

        EObject obj = getElement(defaultValueProperty.getField());
        if (!(obj instanceof EAttribute)) {
            //TODO
            return;
        }

        EAttribute eAttr = (EAttribute) obj;
        eAttr.setDefaultValueLiteral(defaultValueProperty.getDefaultValue()
            .toString());

        setElement(defaultValueProperty, null);
    }
}
