package groove.gui.jgraph;

import static groove.view.aspect.AspectKind.REMARK;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.LabelKind;
import groove.graph.TypeLabel;
import groove.gui.Options;
import groove.gui.jgraph.JAttr.AttributeMap;
import groove.trans.RuleLabel;
import groove.util.Converter;
import groove.view.FormatError;
import groove.view.FormatException;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgraph.graph.GraphConstants;

/**
 * Specialized j-vertex for rule graphs, with its own tool tip text.
 */
public class AJVertex extends GraphJVertex<AspectNode,AspectEdge> implements
        AJCell {
    /** Creates a j-vertex on the basis of a given (aspectual) node. */
    public AJVertex(AJModel jModel, AspectNode node) {
        super(jModel, node);
        setUserObject(null);
        this.aspect = node.getKind();
    }

    @Override
    public AJModel getJModel() {
        return (AJModel) super.getJModel();
    }

    /** Clears the errors and the aspect, in addition to calling the super method. */
    @Override
    void reset(AspectNode node) {
        super.reset(node);
        this.errors.clear();
        this.aspect = AspectKind.NONE;
    }

    @Override
    public boolean addSelfEdge(AspectEdge edge) {
        assert edge.source() == getNode();
        assert edge.target() == getNode();
        this.errors.addAll(edge.getErrors());
        return super.addSelfEdge(edge);
    }

    void setNodeFixed() {
        try {
            getNode().setFixed();
        } catch (FormatException e) {
            this.errors.addAll(e.getErrors());
        }
        refreshAttributes();
    }

    @Override
    public String getNodeIdentity() {
        if (this.aspect.isMeta()) {
            return null;
        } else {
            return super.getNodeIdentity();
        }
    }

    /**
     * This implementation prefixes the node description with an indication
     * of the role, if the model is a rule.
     */
    @Override
    StringBuilder getNodeDescription() {
        StringBuilder result = new StringBuilder();
        if (getNode().getAttrKind().isData()) {
            if (getNode().getAttrAspect().hasContent()) {
                result.append("Constant node");
            } else {
                result.append("Variable node");
            }
        } else if (getNode().hasAttrAspect()) {
            result.append("Product node");
        } else {
            result.append(super.getNodeDescription());
        }
        if (AJModel.ROLE_NAMES.containsKey(this.aspect)) {
            Converter.toUppercase(result, false);
            result.insert(0, " ");
            result.insert(0, AJModel.ROLE_NAMES.get(this.aspect));
            result.append("<br>" + AJModel.ROLE_DESCRIPTIONS.get(this.aspect));
        }
        return result;
    }

    /** Adds a quantifier, if the nesting aspect justifies this. */
    @Override
    public List<StringBuilder> getLines() {
        getNode().testFixed(true);
        List<StringBuilder> result;
        if (hasError()) {
            result = getUserObject().toLines();
        } else {
            result = super.getLines();
            Aspect attrAspect = getNode().getAttrAspect();
            if (attrAspect.getKind().isTypedData()) {
                String dataLine;
                if (getJModel().isShowAspects()) {
                    dataLine = attrAspect.toString();
                } else if (attrAspect.hasContent()) {
                    dataLine = attrAspect.getContentString();
                } else {
                    dataLine =
                        TypeLabel.toHtmlString(TypeLabel.createLabel(
                            LabelKind.NODE_TYPE, attrAspect.getKind().getName()));
                }
                result.add(0, new StringBuilder(dataLine));
            }
            for (AspectEdge edge : getDataEdges()) {
                if (!getJModel().isFiltering(edge.getDisplayLabel())) {
                    result.add(getLine(edge));
                }
            }
            if (getJModel().isShowAspects()) {
                result.add(0, getRoleLine());
            }
            // adds a quantor if the node is a nesting node
            if (this.aspect.isQuantifier()) {
                result.add(0, getQuantifierLine(getNode().getAspect()));
            }
            // adds a parameter string if the node is a rule parameter
            Aspect param = getNode().getParam();
            if (param != null && param.hasContent()) {
                result.add(new StringBuilder(param.getContentString()));
            }
        }
        return result;
    }

    /**
     * Returns a string with the aspect prefix for this node's role, of the
     * empty string if the node has no special role.
     */
    private StringBuilder getRoleLine() {
        boolean hasRoleValue =
            this.aspect.isRole() && this.aspect != AspectKind.READER;
        return new StringBuilder(hasRoleValue
                ? getNode().getAspect().toString() : "");
    }

    /** Returns an HTML-formatted line describing a given quantifier value. */
    private StringBuilder getQuantifierLine(Aspect nesting) {
        StringBuilder result = new StringBuilder();
        AspectKind kind = nesting.getKind();
        if (kind == AspectKind.FORALL) {
            result.append(Converter.HTML_FORALL);
        } else if (kind == AspectKind.FORALL_POS) {
            result.append(Converter.HTML_FORALL);
            result.append(Converter.SUPER_TAG.on(Converter.HTML_GT + "0"));
        } else if (kind == AspectKind.EXISTS) {
            result.append(Converter.HTML_EXISTS);
        }
        String level = (String) nesting.getContent();
        if (level != null && level.length() != 0) {
            result.append(Converter.SUB_TAG.on(level));
        }
        return result;
    }

    /**
     * On demand prefixes the label with the edge's aspect values.
     */
    @Override
    public StringBuilder getLine(AspectEdge edge) {
        edge.testFixed(true);
        StringBuilder result = new StringBuilder();
        if (getJModel().isShowAspects()) {
            result.append(TypeLabel.toHtmlString(edge.label()));
        } else {
            result.append(TypeLabel.toHtmlString(edge.getDisplayLabel()));
        }
        if (edge.getKind() == AspectKind.ABSTRACT) {
            result = Converter.ITALIC_TAG.on(result);
        }
        if (edge.target() != edge.source()) {
            // this is an attribute edge displayed as a node label
            String suffix;
            AspectNode actualTarget = edge.target();
            if (getNode().getGraphRole() == GraphRole.TYPE) {
                suffix = TYPE_TEXT + actualTarget.getAttrKind().getName();
            } else {
                suffix =
                    ASSIGN_TEXT + actualTarget.getAttrAspect().getContent();
            }
            result.append(Converter.toHtml(suffix));
        }
        // use special node label prefixes to indicate edge role
        Aspect edgeAspect = edge.getAspect();
        if (!edgeAspect.equals(edge.source().getAspect())) {
            addRoleIndicator(result, edgeAspect.getKind());
        }
        return result;
    }

    /**
     * Adds a textual prefix and a HTML colour to a given node line,
     * depending on an edge role.
     */
    private void addRoleIndicator(StringBuilder text, AspectKind edgeRole) {
        switch (edgeRole) {
        case ERASER:
            text.insert(0, "- ");
            Converter.blue.on(text);
            break;
        case ADDER:
            text.insert(0, "+! ");
            Converter.green.on(text);
            break;
        case CREATOR:
            text.insert(0, "+ ");
            Converter.green.on(text);
            break;
        case EMBARGO:
            text.insert(0, "! ");
            Converter.red.on(text);
            break;
        case REMARK:
            text.insert(0, "// ");
            Converter.remark.on(text);
            break;
        }
    }

    @Override
    public Collection<? extends Label> getListLabels() {
        getNode().testFixed(true);
        Collection<Label> result;
        if (hasError()) {
            result = getUserObject().toLabels();
        } else {
            result = new ArrayList<Label>();
            result.addAll(super.getListLabels());
            Aspect attrAspect = getNode().getAttrAspect();
            if (attrAspect.getKind().isTypedData()) {
                if (attrAspect.hasContent()) {
                    result.add(TypeLabel.createLabel(attrAspect.getContentString()));
                } else {
                    result.add(TypeLabel.createLabel(LabelKind.NODE_TYPE,
                        attrAspect.getKind().getName()));
                }
            }
            for (AspectEdge edge : getDataEdges()) {
                result.addAll(getListLabels(edge));
            }
        }
        return result;
    }

    @Override
    public Set<? extends Label> getListLabels(AspectEdge edge) {
        Set<? extends Label> result;
        Label label = edge.getDisplayLabel();
        if (label instanceof RuleLabel && ((RuleLabel) label).isMatchable()) {
            result = ((RuleLabel) label).getMatchExpr().getTypeLabels();
            if (result.isEmpty()) {
                result = Collections.singleton(NO_LABEL);
            }
        } else {
            result = Collections.singleton(label);
        }
        return result;
    }

    /**
     * Returns an ordered set of outgoing edges going to constants.
     */
    Set<AspectEdge> getDataEdges() {
        Set<AspectEdge> result = new TreeSet<AspectEdge>();
        if (!getJModel().isShowValueNodes()) {
            for (Object edgeObject : getPort().getEdges()) {
                AJEdge jEdge = (AJEdge) edgeObject;
                if (jEdge.getSourceVertex() == this
                    && jEdge.isDataEdgeSourceLabel()) {
                    for (AspectEdge edge : jEdge.getEdges()) {
                        result.add(edge);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String getLabelText(AspectEdge edge) {
        return edge.getDisplayLabel().text();
    }

    /**
     * This implementation makes remark edges invisible as demanded by the
     * {@link Options#SHOW_REMARKS_OPTION}.
     */
    @Override
    public boolean isVisible() {
        if (this.aspect == REMARK) {
            return getJModel().isShowRemarks();
        } else if (getNode().hasParam()) {
            return true;
        } else {
            boolean result = super.isVisible();
            if (getNode().getAttrKind().isTypedData()
                && !getJModel().isShowValueNodes()) {
                result = hasVisibleIncidentEdge();
            }
            return result;
        }
    }

    @Override
    public final boolean hasError() {
        return !this.errors.isEmpty();
    }

    /** Returns the (possibly empty) set of errors in this JVertex. */
    public Collection<FormatError> getErrors() {
        return this.errors;
    }

    @Override
    protected AttributeMap createAttributes() {
        AttributeMap result = JAttr.RULE_NODE_ATTR.get(this.aspect).clone();
        if (getJModel().isForEditor()) {
            GraphConstants.setEditable(result, true);
        }
        return result;
    }

    public void saveToUserObject() {
        // collect the node and edge information
        AJObject userObject = getUserObject();
        userObject.clear();
        userObject.addLabels(getNode().getNodeLabels());
        userObject.addEdges(getSelfEdges());
    }

    @Override
    public void loadFromUserObject(GraphRole role) {
        AspectNode node = new AspectNode(getNode().getNumber(), role);
        reset(node);
        AspectParser parser = AspectParser.getInstance(role);
        for (String text : getUserObject()) {
            AspectLabel label = parser.parse(text);
            if (label.isNodeOnly()) {
                try {
                    node.setAspects(label);
                } catch (FormatException e) {
                    // do nothing; the errors in the node will be processed later
                }
            } else {
                AspectEdge edge = new AspectEdge(node, label, node, role);
                try {
                    edge.setFixed();
                } catch (FormatException e) {
                    // do nothing; the errors in the edge will be processed later
                }
                addSelfEdge(edge);
            }
        }
        this.aspect = node.getKind();
    }

    /**
     * Creates a new used object, and initialises it from a given value.
     * If the value is a collection or a string, loads the user object from it.
     */
    @Override
    public void setUserObject(Object value) {
        // we do need to create a new object, otherwise undos do not work
        AJObject myObject = new AJObject(false);
        if (value instanceof AJObject) {
            myObject.addAll((AJObject) value);
        } else if (value != null) {
            myObject.load(value.toString());
        }
        super.setUserObject(myObject);
    }

    /** Specialises the return type. */
    @Override
    public AJObject getUserObject() {
        return (AJObject) super.getUserObject();
    }

    /** The role of the underlying rule node. */
    private AspectKind aspect;
    private List<FormatError> errors = new ArrayList<FormatError>();
    static private final String ASSIGN_TEXT = " = ";
    static private final String TYPE_TEXT = ": ";
}