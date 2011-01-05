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
    public AJVertex clone() {
        AJVertex result = (AJVertex) super.clone();
        result.errors = new ArrayList<FormatError>();
        return result;
    }

    @Override
    public boolean addSelfEdge(AspectEdge edge) {
        assert edge.source() == getNode();
        assert edge.target() == getNode();
        this.errors.addAll(edge.getErrors());
        return super.addSelfEdge(edge);
    }

    /** 
     * Collects a set of edges that under the current
     * display settings are also to be shown on this label.
     * These are obtained from the outgoing JEdges that
     * have this JVertex as their source label and for which
     * {@link AJEdge#isSourceLabel()} holds.
     */
    private Set<AspectEdge> getExtraSelfEdges() {
        Set<AspectEdge> result = new TreeSet<AspectEdge>();
        // add all outgoing JEdges that are source labels
        for (Object edgeObject : getPort().getEdges()) {
            AJEdge jEdge = (AJEdge) edgeObject;
            if (jEdge.getSourceVertex() == this && jEdge.isSourceLabel()) {
                result.addAll(jEdge.getEdges());
            }
        }
        return result;
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
    protected String getNodeIdentity() {
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
        if (hasError() || getJModel().isShowAspects()) {
            result = getUserObject().toLines();
        } else {
            result = new ArrayList<StringBuilder>();
            // show the node identity
            result.addAll(getNodeIdLines());
            // show the main aspect correctly
            result.addAll(getAspectLines());
            // show data constants and variables correctly
            Aspect attrAspect = getNode().getAttrAspect();
            if (attrAspect.getKind().isTypedData()) {
                String dataLine;
                if (attrAspect.hasContent()) {
                    dataLine = attrAspect.getContentString();
                } else {
                    dataLine =
                        TypeLabel.toHtmlString(TypeLabel.createLabel(
                            LabelKind.NODE_TYPE, attrAspect.getKind().getName()));
                }
                result.add(new StringBuilder(dataLine));
            }
            // show the visible self-edges
            for (AspectEdge edge : getSelfEdges()) {
                if (!isFiltered(edge)) {
                    result.add(getLine(edge));
                }
            }
            for (AspectEdge edge : getExtraSelfEdges()) {
                if (!isFiltered(edge)) {
                    result.add(getLine(edge));
                }
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
     * Returns the lines describing this node's main aspect.
     * Currently this just concerns a possible quantifier.
     */
    private List<StringBuilder> getAspectLines() {
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        if (this.aspect.isQuantifier()) {
            StringBuilder line = new StringBuilder();
            switch (this.aspect) {
            case FORALL:
                line.append(Converter.HTML_FORALL);
                break;
            case FORALL_POS:
                line.append(Converter.HTML_FORALL);
                line.append(Converter.SUPER_TAG.on(Converter.HTML_GT + "0"));
                break;
            case EXISTS:
                line.append(Converter.HTML_EXISTS);
            }
            String level = (String) getNode().getAspect().getContent();
            if (level != null && level.length() != 0) {
                line.append(Converter.SUB_TAG.on(level));
            }
            if (line.length() > 0) {
                result.add(line);
            }
        }
        return result;
    }

    /**
     * On demand prefixes the label with the edge's aspect values.
     */
    @Override
    protected StringBuilder getLine(AspectEdge edge) {
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
            for (AspectEdge edge : getExtraSelfEdges()) {
                result.addAll(getListLabels(edge));
            }
        }
        return result;
    }

    @Override
    protected Set<? extends Label> getListLabels(AspectEdge edge) {
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
     * This implementation makes remark edges invisible as demanded by the
     * {@link Options#SHOW_REMARKS_OPTION}.
     */
    @Override
    public boolean isFiltered() {
        if (this.aspect == REMARK) {
            return !getJModel().isShowRemarks();
        }
        if (getNode().hasParam()) {
            return false;
        }
        if (super.isFiltered()) {
            return true;
        }
        // in addition, value nodes may be filtered
        if (getJModel().isShowValueNodes()) {
            return false;
        }
        Aspect attr = getNode().getAttrAspect();
        return attr.getKind().isTypedData() && attr.hasContent();
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
        if (getJModel().isEditing()) {
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