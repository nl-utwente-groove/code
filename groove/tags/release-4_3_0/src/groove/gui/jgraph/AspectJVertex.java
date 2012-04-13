package groove.gui.jgraph;

import static groove.io.HTMLConverter.ITALIC_TAG;
import static groove.view.aspect.AspectKind.REMARK;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.graph.algebra.ProductNode;
import groove.gui.Options;
import groove.gui.jgraph.JAttr.AttributeMap;
import groove.io.HTMLConverter;
import groove.trans.RuleLabel;
import groove.view.FormatError;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgraph.graph.GraphConstants;

/**
 * Specialized j-vertex for rule graphs, with its own tool tip text.
 */
public class AspectJVertex extends GraphJVertex implements AspectJCell {
    /** Creates a j-vertex on the basis of a given (aspectual) node. */
    public AspectJVertex(AspectJGraph jGraph, AspectNode node) {
        super(jGraph, node);
        setUserObject(null);
        if (node != null) {
            this.aspect = node.getKind();
            this.errors.addAll(node.getErrors());
        }
    }

    @Override
    public AspectJGraph getJGraph() {
        return (AspectJGraph) super.getJGraph();
    }

    @Override
    public AspectNode getNode() {
        return (AspectNode) super.getNode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<AspectEdge> getJVertexLabels() {
        return (Set<AspectEdge>) super.getJVertexLabels();
    }

    /** Clears the errors and the aspect, in addition to calling the super method. */
    @Override
    void reset(Node node) {
        super.reset(node);
        this.errors.clear();
        this.extraError = false;
        this.aspect = AspectKind.NONE;
    }

    @Override
    public AspectJVertex clone() {
        AspectJVertex result = (AspectJVertex) super.clone();
        result.errors = new ArrayList<FormatError>();
        return result;
    }

    @Override
    public AspectJVertex newJVertex(Node node) {
        return new AspectJVertex(getJGraph(), (AspectNode) node);
    }

    @Override
    public boolean addJVertexLabel(Edge<?> edge) {
        boolean result = super.addJVertexLabel(edge);
        if (result) {
            this.errors.addAll(((AspectEdge) edge).getErrors());
        }
        return result;
    }

    @Override
    protected boolean isJVertexLabel(Edge<?> edge) {
        return super.isJVertexLabel(edge)
            || ((AspectEdge) edge).getKind() == REMARK;
    }

    /** 
     * Collects a set of edges that under the current
     * display settings are also to be shown on this label.
     * These are obtained from the outgoing JEdges that
     * have this JVertex as their source label and for which
     * {@link AspectJEdge#isSourceLabel()} holds.
     */
    private Set<AspectEdge> getExtraSelfEdges() {
        Set<AspectEdge> result = new TreeSet<AspectEdge>();
        // add all outgoing JEdges that are source labels
        for (Object edgeObject : getPort().getEdges()) {
            AspectJEdge jEdge = (AspectJEdge) edgeObject;
            if (jEdge.getSourceVertex() == this && jEdge.isSourceLabel()) {
                result.addAll(jEdge.getEdges());
            }
        }
        return result;
    }

    /** Retrieves the (first) node type label of this JVertex, if any. */
    TypeLabel getNodeType() {
        TypeLabel result = null;
        for (AspectEdge edge : getJVertexLabels()) {
            if (edge.getRole() == EdgeRole.NODE_TYPE) {
                result = edge.getTypeLabel();
                break;
            }
        }
        return result;
    }

    void setNodeFixed() {
        getNode().setFixed();
        this.errors.addAll(getNode().getErrors());
        refreshAttributes();
    }

    @Override
    protected String getNodeIdString() {
        if (this.aspect.isMeta()) {
            return null;
        } else if (getNode().hasAttrAspect()) {
            Aspect attrAspect = getNode().getAttrAspect();
            if (attrAspect.getKind().isData()) {
                // delegate the identity string to a corresponding variable node
                return attrAspect.getVariableNode(getNode().getNumber()).toString();
            } else {
                assert attrAspect.getKind() == AspectKind.PRODUCT;
                // delegate the identity string to a corresponding product node
                return new ProductNode(getNode().getNumber(), 0).toString();
            }
        } else {
            return super.getNodeIdString();
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
        if (AspectJModel.ROLE_NAMES.containsKey(this.aspect)) {
            HTMLConverter.toUppercase(result, false);
            result.insert(0, " ");
            result.insert(0, AspectJModel.ROLE_NAMES.get(this.aspect));
            result.append("<br>"
                + AspectJModel.ROLE_DESCRIPTIONS.get(this.aspect));
        }
        return result;
    }

    /** Adds a quantifier, if the nesting aspect justifies this. */
    @Override
    public List<StringBuilder> getLines() {
        getNode().testFixed(true);
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        // show the node identity
        result.addAll(getNodeIdLines());
        if (hasError() || getJGraph().isShowAspects()) {
            result.addAll(getUserObject().toLines());
            for (AspectEdge edge : getExtraSelfEdges()) {
                if (!isFiltered(edge)) {
                    result.add(getLine(edge));
                }
            }
        } else {
            // show the main aspect correctly
            result.addAll(getAspectLines());
            // show data constants and variables correctly
            result.addAll(getDataLines());
            // show the visible self-edges
            for (AspectEdge edge : getJVertexLabels()) {
                if (!isFiltered(edge)) {
                    result.add(getLine(edge));
                }
            }
            for (AspectEdge edge : getExtraSelfEdges()) {
                if (!isFiltered(edge)) {
                    result.add(getLine(edge));
                }
            }
        }
        return result;
    }

    @Override
    protected List<StringBuilder> getNodeIdLines() {
        if (getNode().hasId()) {
            List<StringBuilder> result = new ArrayList<StringBuilder>();
            result.add(ITALIC_TAG.on(new StringBuilder(
                getNode().getId().getContentString())));
            return result;
        } else {
            return super.getNodeIdLines();
        }
    }

    /** Returns lines describing any data content of the JVertex. */
    private List<StringBuilder> getDataLines() {
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        Aspect attrAspect = getNode().getAttrAspect();
        if (attrAspect.getKind().isTypedData()) {
            String dataLine = null;
            if (!attrAspect.hasContent()) {
                dataLine =
                    TypeLabel.toHtmlString(TypeLabel.createLabel(
                        EdgeRole.NODE_TYPE, attrAspect.getKind().getName()));
            } else if (!getJGraph().isShowNodeIdentities()) {
                // show constants only if they are not already shown as node identities
                dataLine = attrAspect.getContentString();
            }
            if (dataLine != null) {
                result.add(new StringBuilder(dataLine));
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
                line.append(HTMLConverter.HTML_FORALL);
                break;
            case FORALL_POS:
                line.append(HTMLConverter.HTML_FORALL);
                line.append(HTMLConverter.SUPER_TAG.on(HTMLConverter.HTML_GT + "0"));
                break;
            case EXISTS:
                line.append(HTMLConverter.HTML_EXISTS);
            }
            String level = (String) getNode().getAspect().getContent();
            if (level != null && level.length() != 0) {
                line.append(HTMLConverter.SUB_TAG.on(level));
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
    protected StringBuilder getLine(Edge<?> edge) {
        AspectEdge aspectEdge = (AspectEdge) edge;
        aspectEdge.testFixed(true);
        StringBuilder result = new StringBuilder();
        if (getJGraph().isShowAspects()) {
            result.append(TypeLabel.toHtmlString(aspectEdge.label()));
        } else {
            result.append(TypeLabel.toHtmlString(aspectEdge.getDisplayLabel()));
        }
        if (aspectEdge.getKind() == AspectKind.ABSTRACT) {
            result = HTMLConverter.ITALIC_TAG.on(result);
        }
        if (edge.target() != edge.source()) {
            // this is an attribute edge displayed as a node label
            String suffix;
            AspectNode actualTarget = aspectEdge.target();
            if (getNode().getGraphRole() == GraphRole.TYPE) {
                suffix = TYPE_TEXT + actualTarget.getAttrKind().getName();
            } else {
                suffix =
                    ASSIGN_TEXT
                        + actualTarget.getAttrAspect().getContentString();
            }
            result.append(HTMLConverter.toHtml(suffix));
        }
        // use special node label prefixes to indicate edge role
        Aspect edgeAspect = aspectEdge.getAspect();
        if (!edgeAspect.equals(aspectEdge.source().getAspect())) {
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
            HTMLConverter.blue.on(text);
            break;
        case ADDER:
            text.insert(0, "+! ");
            HTMLConverter.green.on(text);
            break;
        case CREATOR:
            text.insert(0, "+ ");
            HTMLConverter.green.on(text);
            break;
        case EMBARGO:
            text.insert(0, "! ");
            HTMLConverter.red.on(text);
            break;
        case REMARK:
            text.insert(0, "// ");
            HTMLConverter.remark.on(text);
            break;
        }
    }

    @Override
    public Collection<? extends Label> getListLabels() {
        getNode().testFixed(true);
        Collection<Label> result;
        if (hasError()) {
            result = getUserObject().toLabels();
        } else if (this.aspect.isMeta()) {
            return Collections.emptySet();
        } else {
            result = new ArrayList<Label>();
            for (Edge<?> edge : getJVertexLabels()) {
                result.addAll(getListLabels(edge));
            }
            Aspect attrAspect = getNode().getAttrAspect();
            if (attrAspect.getKind().isTypedData()) {
                if (attrAspect.hasContent()) {
                    result.add(TypeLabel.createLabel(attrAspect.getContentString()));
                } else {
                    result.add(TypeLabel.createLabel(EdgeRole.NODE_TYPE,
                        attrAspect.getKind().getName()));
                }
            }
            for (AspectEdge edge : getExtraSelfEdges()) {
                result.addAll(getListLabels(edge));
            }
            if (result.isEmpty()) {
                result.add(NO_LABEL);
            }
        }
        return result;
    }

    @Override
    protected Set<? extends Label> getListLabels(Edge<?> edge) {
        AspectEdge aspectEdge = (AspectEdge) edge;
        Set<? extends Label> result;
        Label label = aspectEdge.getDisplayLabel();
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
            return !getJGraph().isShowRemarks();
        }
        if (getNode().hasParam() || this.aspect.isQuantifier() || hasError()) {
            return false;
        }
        if (super.isFiltered()) {
            return true;
        }
        // in addition, value nodes or data type nodes may be filtered
        if (getJGraph().isShowValueNodes()) {
            return false;
        }
        Aspect attr = getNode().getAttrAspect();
        if (!attr.getKind().isTypedData()) {
            return false;
        }
        return getNode().getGraphRole() == GraphRole.TYPE || attr.hasContent();
    }

    @Override
    public final boolean hasError() {
        return this.extraError || !this.errors.isEmpty();
    }

    @Override
    public void setExtraError(boolean error) {
        if (this.extraError != error) {
            this.extraError = error;
            refreshAttributes();
        }
    }

    /** Returns the (possibly empty) set of errors in this JVertex. */
    public Collection<FormatError> getErrors() {
        return this.errors;
    }

    @Override
    public String getAdornment() {
        if (getNode().hasParam()) {
            Aspect param = getNode().getParam();
            StringBuilder result = new StringBuilder(5);
            switch (param.getKind()) {
            case PARAM_IN:
                result.append("?");
                break;
            case PARAM_OUT:
                result.append("!");
            }
            result.append(param.getContentString());
            return result.toString();
        } else {
            return null;
        }
    }

    @Override
    protected AttributeMap createAttributes() {
        AttributeMap result =
            AspectJGraph.ASPECT_NODE_ATTR.get(this.aspect).clone();
        if (getJGraph().hasActiveEditor()) {
            GraphConstants.setEditable(result, true);
        }
        if (getNode().getGraphRole() != GraphRole.RULE) {
            Color nodeColor = getNodeColor();
            if (nodeColor != null) {
                GraphConstants.setForeground(result, nodeColor);
                GraphConstants.setLineColor(result, nodeColor);
                GraphConstants.setBackground(result, JAttr.whitewash(nodeColor));
            }
        }
        return result;
    }

    /** Retrieves a node color from the model's label store, if any. */
    Color getNodeColor() {
        Color result = null;
        if (getNode() != null && getNode().getColor() != null) {
            result = (Color) getNode().getColor().getContent();
        } else if (getJGraph().getLabelStore() != null) {
            result = getJGraph().getLabelStore().getColor(getNodeType());
        }
        return result;
    }

    public void saveToUserObject() {
        // collect the node and edge information
        AspectJObject userObject = getUserObject();
        userObject.clear();
        userObject.addLabels(getNode().getNodeLabels());
        userObject.addEdges(getJVertexLabels());
    }

    @Override
    public void loadFromUserObject(GraphRole role) {
        AspectNode node = new AspectNode(getNode().getNumber(), role);
        reset(node);
        AspectParser parser = AspectParser.getInstance();
        List<AspectLabel> edgeLabels = new ArrayList<AspectLabel>();
        for (String text : getUserObject()) {
            AspectLabel label = parser.parse(text, role);
            if (label.isNodeOnly()) {
                node.setAspects(label);
            } else {
                // don't process the edge labels yet, as the node is not
                // yet completely determined
                edgeLabels.add(label);
            }
        }
        // now process the edge labels
        for (AspectLabel label : edgeLabels) {
            AspectEdge edge = new AspectEdge(node, label, node);
            edge.setFixed();
            boolean added = addJVertexLabel(edge);
            assert added;
        }
        this.aspect = node.getKind();
        // attributes will be refreshed upon the call to setNodeFixed()
    }

    /**
     * Creates a new used object, and initialises it from a given value.
     * If the value is a collection or a string, loads the user object from it.
     */
    @Override
    public void setUserObject(Object value) {
        // we do need to create a new object, otherwise undos do not work
        AspectJObject myObject = new AspectJObject();
        if (value instanceof AspectJObject) {
            myObject.addAll((AspectJObject) value);
        } else if (value != null) {
            myObject.load(value.toString());
        }
        super.setUserObject(myObject);
    }

    /** Specialises the return type. */
    @Override
    public AspectJObject getUserObject() {
        return (AspectJObject) super.getUserObject();
    }

    /** The role of the underlying rule node. */
    private AspectKind aspect;
    private Collection<FormatError> errors = new LinkedHashSet<FormatError>();
    private boolean extraError;

    /** Returns a prototype {@link AspectJVertex} for a given {@link AspectJGraph}. */
    public static AspectJVertex getPrototype(AspectJGraph jGraph) {
        return new AspectJVertex(jGraph, null);
    }

    static private final String ASSIGN_TEXT = " = ";
    static private final String TYPE_TEXT = ": ";
}