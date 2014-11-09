package groove.io.conceptual.lang.groove;

import groove.graph.GraphRole;
import groove.io.conceptual.Concept;
import groove.io.conceptual.Field;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.schema.EnumModeType;
import groove.io.conceptual.configuration.schema.NullableType;
import groove.io.conceptual.configuration.schema.OrderType;
import groove.io.conceptual.configuration.schema.TypeModel;
import groove.io.conceptual.graph.AbsEdge;
import groove.io.conceptual.graph.AbsNode;
import groove.io.conceptual.lang.GlossaryExportBuilder;
import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.IdentityProperty;
import groove.io.conceptual.property.KeysetProperty;
import groove.io.conceptual.property.OppositeProperty;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.DataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.type.Type;
import groove.io.conceptual.value.Object;
import groove.io.external.PortException;

/** Bridge from glossary to GROOVE export. */
public class GlossaryToGroove extends GlossaryExportBuilder<GrooveExport,AbsNode> {
    /** Constructs an instance for a given glossary and export object. */
    public GlossaryToGroove(Glossary glos, GrooveExport export) {
        super(glos, export);
        this.m_cfg = export.getConfig();
        this.m_currentGraph = export.getGraph(GrooveUtil.getSafeId(glos.getName()), GraphRole.TYPE);
    }

    private final Config m_cfg;
    private final GrammarGraph m_currentGraph;

    @Override
    public void build() throws PortException {
        int timer = Timer.start("TM to GROOVE");
        this.m_cfg.setGlossary(getGlossary());
        super.build();
        Timer.stop(timer);
    }

    @Override
    protected boolean put(Concept c, AbsNode n) {
        boolean result = super.put(c, n);
        if (result && n != null) {
            this.m_currentGraph.addNodes(n);
        }
        return result;
    }

    @Override
    protected AbsNode addClass(Class c) {
        AbsNode result;
        if (c.isProper()) {
            result = new AbsNode(this.m_cfg.getName(c));
            put(c, result);

            // From here on working with a proper class
            for (Class clazz : c.getSuperClasses()) {
                AbsNode superClassNode = add(clazz.getProperClass());
                new AbsEdge(result, superClassNode, "sub:");
            }

            for (Field f : c.getFields()) {
                AbsNode fieldNode = add(f);

                String edgeLabel = "";
                int lowerBound = f.getLowerBound();
                if (lowerBound == 0 && f.getUpperBound() == 1 && f.getType() instanceof Class) {
                    // Nullable, but in GROOVE always Nil value (unless turned off)
                    if (this.m_cfg.getXMLConfig().getGlobal().getNullable() != NullableType.NONE) {
                        lowerBound = 1;
                    }
                }

                // When using intermediates, ensure each intermediate is linked to one field
                //TODO: temporarily check useIntermediate container, currently out of sync due to multiplicity checks
                if (this.m_cfg.useIntermediate(f) && f.getType() instanceof Container
                    && this.m_cfg.useIntermediate((Container) f.getType())) {
                    edgeLabel += "in=1:";
                }

                // If not 0..* use out multiplicity
                if (f.getUpperBound() != -1 || lowerBound != 0) {
                    edgeLabel += "out=";
                    if (lowerBound != f.getUpperBound()) {
                        edgeLabel +=
                            lowerBound + ".."
                                + ((f.getUpperBound() == -1) ? "*" : f.getUpperBound());
                    } else {
                        edgeLabel += lowerBound;
                    }
                    edgeLabel += ":";
                }
                new AbsEdge(result, fieldNode, edgeLabel + f.getName().toString());
            }
            // If all nullable classes, get node of nullable version too
            if (this.m_cfg.getXMLConfig().getGlobal().getNullable() == NullableType.ALL) {
                add(c.getNullableClass());
            }
        } else if (this.m_cfg.getXMLConfig().getGlobal().getNullable() == NullableType.NONE) {
            // Simply revert to the proper instance
            result = add(c.getProperClass());
            put(c, result);
        } else {
            // If nullable class, just make it a superclass of the proper class, and allow NIL
            result = new AbsNode(this.m_cfg.getName(c));
            put(c, result);

            result.addName("abs:");

            AbsNode nilNode = add(Object.NIL);
            new AbsEdge(nilNode, result, "sub:");

            AbsNode properNode = add(c.getProperClass());
            new AbsEdge(properNode, result, "sub:");
        }

        return result;
    }

    @Override
    protected AbsNode addField(Field field) {
        AbsNode result;
        if (field.getType() instanceof Container) {
            result = add(field.getType(), this.m_cfg.getName(field));
        } else {
            boolean isNullable = false;
            if (this.m_cfg.useIntermediate(field) && field.getType() instanceof Class) {
                isNullable = !((Class) field.getType()).isProper();
                result = add(((Class) field.getType()).getProperClass());
            } else {
                result = add(field.getType());
            }
            if (this.m_cfg.useIntermediate(field)) {
                String valName = this.m_cfg.getStrings().getValueEdge();
                AbsNode interNode = new AbsNode(this.m_cfg.getName(field));
                interNode.addName("edge:\"" + field.getName() + "\"");
                String out = isNullable ? "out=0..1:" : "out=1:";
                /*AbsEdge valEdge = */new AbsEdge(interNode, result, out + valName);
                result = interNode;
            }
        }
        put(field, result);
        return result;
    }

    @Override
    protected AbsNode addDataType(DataType dt) {
        AbsNode result;
        if (dt instanceof CustomDataType) {
            String valueName = this.m_cfg.getStrings().getDataValue();
            result = new AbsNode(this.m_cfg.getName(dt), "string:" + valueName);
        } else {
            result = new AbsNode(this.m_cfg.getName(dt));
        }
        put(dt, result);
        return result;
    }

    @Override
    protected AbsNode addEnum(Enum e) {
        AbsNode result;
        if (this.m_cfg.getXMLConfig().getTypeModel().getEnumMode() == EnumModeType.NODE) {
            String sep = this.m_cfg.getXMLConfig().getGlobal().getIdSeparator();
            result = new AbsNode(this.m_cfg.getName(e), "abs:");
            for (Name n : e.getLiterals()) {
                String litName = "type:" + this.m_cfg.idToName(e.getId()) + sep + n.toString();
                AbsNode valNode = new AbsNode(litName);
                new AbsEdge(valNode, result, "sub:");
            }
        } else {
            result = new AbsNode(this.m_cfg.getName(e));
            for (Name n : e.getLiterals()) {
                result.addName("flag:" + n.toString());
            }
        }
        put(e, result);
        return result;
    }

    @Override
    protected AbsNode addContainer(Container c, String base) {
        AbsNode typeNode;
        if (c.getType() instanceof Container) {
            assert base != null;
            typeNode = add(c.getType(), this.m_cfg.getContainerName(base, c));
        } else {
            typeNode = add(c.getType());
        }

        boolean useIndex = this.m_cfg.useIndex(c);
        boolean indexValue =
            (this.m_cfg.getXMLConfig()
                .getTypeModel()
                .getFields()
                .getContainers()
                .getOrdering()
                .getType() == OrderType.INDEX);

        AbsNode result = null;
        if (this.m_cfg.useIntermediate(c)) {
            assert base != null;
            result = new AbsNode(base + this.m_cfg.getContainerPostfix(c));

            // Use just the last part of the container id as the edge name
            int lastIndex =
                base.lastIndexOf(this.m_cfg.getXMLConfig().getGlobal().getIdSeparator());
            String edgeName = base;
            if (lastIndex != -1) {
                edgeName = base.substring(lastIndex + 1);
            }

            if (useIndex && indexValue) {
                String indexName = this.m_cfg.getStrings().getIndexEdge();
                result.addName("edge:\"" + edgeName + " %s\"," + indexName);
            } else {
                result.addName("edge:\"" + edgeName + "\"");
            }

            // If subtype is another container, allow more nodes. Otherwise, just one
            String valName = /*"in=1:" + */this.m_cfg.getStrings().getValueEdge();
            if (c.getType() instanceof Container) {
                valName = "out=1..*:" + valName;
            } else {
                valName = "out=1:" + valName;
            }
            new AbsEdge(result, typeNode, valName);
        } else {
            result = typeNode;
        }

        if (useIndex) {
            if (indexValue) {
                String indexName = this.m_cfg.getStrings().getIndexEdge();
                result.addName("out=1:int:" + indexName);
            } else {
                String nextName = this.m_cfg.getStrings().getNextEdge();
                new AbsEdge(result, result, "out=0..1:" + nextName);

                if (this.m_cfg.getXMLConfig()
                    .getTypeModel()
                    .getFields()
                    .getContainers()
                    .getOrdering()
                    .isUsePrevEdge()) {
                    String prevName = this.m_cfg.getStrings().getPrevEdge();
                    new AbsEdge(result, result, "out=0..1:" + prevName);
                }
            }
        }
        put(c, result);
        return result;
    }

    @Override
    protected AbsNode addTuple(Tuple tuple) {
        //TODO: Nodified edge style might suit tuple better
        AbsNode result = new AbsNode(this.m_cfg.getName(tuple));
        put(tuple, result);

        int index = 1;
        for (Type t : tuple.getTypes()) {
            AbsNode typeNode = add(t);
            new AbsEdge(result, typeNode, "_" + index++);
        }

        return result;
    }

    @Override
    protected AbsNode addObject(Object object) {
        if (object != Object.NIL) {
            throw new IllegalArgumentException("Cannot create object node in type model");
        }

        String name = this.m_cfg.getStrings().getNilName();
        AbsNode result = new AbsNode("type:" + name);
        put(object, result);
        return result;
    }

    @Override
    protected AbsNode addAbstractProp(AbstractProperty prop) {
        if (this.m_cfg.getXMLConfig().getTypeModel().getProperties().isUseAbstract()) {
            AbsNode classNode = add(prop.getAbstractClass().getProperClass());
            classNode.addName("abs:");
        }
        put(prop, null);
        return null;
    }

    @Override
    protected AbsNode addContainmentProp(ContainmentProperty prop) {
        if (this.m_cfg.getXMLConfig().getTypeModel().getProperties().isUseContainment()) {
            // Add containment to field edge
            String edgeName = prop.getField().getName().toString();
            AbsNode containmentNode = add(prop.getContainerClass());
            for (AbsEdge edge : containmentNode.getEdges()) {
                if (edge.getName().endsWith(edgeName)) {
                    edge.setName("part:" + edge.getName());
                }
            }

            // Add to intermediate node as well if required
            if (this.m_cfg.useIntermediate(prop.getField())) {
                edgeName = this.m_cfg.getStrings().getValueEdge();
                containmentNode = add(prop.getField());

                for (AbsEdge edge : containmentNode.getEdges()) {
                    if (edge.getName().endsWith(edgeName)) {
                        edge.setName("part:" + edge.getName());
                    }
                }
            }
        }
        put(prop, null);
        return null;
    }

    @Override
    protected AbsNode addIdentityProp(IdentityProperty prop) {
        // Nothing to do for type graph
        put(prop, null);
        return null;
    }

    @Override
    protected AbsNode addKeysetProp(KeysetProperty prop) {
        // Nothing to do for type graph
        put(prop, null);
        return null;
    }

    @Override
    // Called twice for each opposite pair, opposite has a reverse
    // So only handle a single direction
    protected AbsNode addOppositeProp(OppositeProperty prop) {
        TypeModel typeModel = this.m_cfg.getXMLConfig().getTypeModel();
        boolean useOpposites =
            typeModel.getProperties().isUseOpposite() && typeModel.getFields().isOpposites();
        if (useOpposites) {
            //TODO: possible for self referential opposites, make sure the field nodes are split in that case
            AbsNode class1Node = add(prop.getClass1());
            AbsNode class2Node = add(prop.getClass2());

            AbsNode field1Node = add(prop.getField1());
            AbsNode field2Node = add(prop.getField2());

            AbsNode source = this.m_cfg.useIntermediate(prop.getField1()) ? field1Node : class1Node;
            AbsNode target = this.m_cfg.useIntermediate(prop.getField2()) ? field2Node : class2Node;

            String oppositeName = this.m_cfg.getStrings().getOppositeEdge();
            new AbsEdge(source, target, "out=1:" + oppositeName);
        }
        put(prop, null);
        return null;
    }

    @Override
    protected AbsNode addDefaultValueProp(DefaultValueProperty prop) {
        // Nothing to do for type graph
        put(prop, null);
        return null;
    }
}
