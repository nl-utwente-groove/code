package groove.gui.jgraph;

import static groove.io.HTMLConverter.ITALIC_TAG;
import static groove.io.HTMLConverter.STRONG_TAG;
import static groove.io.HTMLConverter.toHtml;
import static groove.view.aspect.AspectKind.REMARK;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.Element;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.LabelPattern;
import groove.graph.Node;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.graph.algebra.VariableNode;
import groove.gui.RuleLevelTree;
import groove.gui.jgraph.JAttr.AttributeMap;
import groove.io.HTMLConverter;
import groove.io.HTMLConverter.HTMLTag;
import groove.io.Util;
import groove.util.ChangeCount;
import groove.util.ChangeCount.Tracker;
import groove.util.Colors;
import groove.view.FormatError;
import groove.view.GraphBasedModel.TypeModelMap;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
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
    public AspectJVertex(AspectJGraph jGraph, AspectJModel jModel,
            AspectNode node) {
        super(jGraph, jModel, node);
        setUserObject(null);
        if (node != null) {
            this.aspect = node.getKind();
            this.errors.addAll(node.getErrors());
        }
        resetTracker();
    }

    @Override
    public AspectJGraph getJGraph() {
        return (AspectJGraph) super.getJGraph();
    }

    @Override
    public AspectJModel getJModel() {
        return (AspectJModel) super.getJModel();
    }

    @Override
    public AspectNode getNode() {
        return (AspectNode) super.getNode();
    }

    @Override
    public AspectKind getAspect() {
        return this.aspect;
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
        clearExtraErrors();
        this.aspect = AspectKind.DEFAULT;
    }

    @Override
    public AspectJVertex clone() {
        AspectJVertex result = (AspectJVertex) super.clone();
        result.errors = new ArrayList<FormatError>();
        result.extraErrors = new ArrayList<FormatError>();
        result.resetTracker();
        return result;
    }

    @Override
    public AspectJVertex newJVertex(GraphJModel<?,?> jModel, Node node) {
        return new AspectJVertex(getJGraph(), (AspectJModel) jModel,
            (AspectNode) node);
    }

    @Override
    public boolean addJVertexLabel(Edge edge) {
        boolean result = super.addJVertexLabel(edge);
        if (result) {
            this.errors.addAll(((AspectEdge) edge).getErrors());
        }
        return result;
    }

    @Override
    protected boolean isJVertexLabel(Edge edge) {
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

    /** 
     * Retrieves the node type corresponding to the node type label,
     * if the type graph is not implicit. 
     */
    private TypeNode getNodeType() {
        TypeNode result = null;
        TypeGraph typeGraph = getJModel().getTypeGraph();
        for (AspectEdge edge : getJVertexLabels()) {
            if (typeGraph.isNodeType(edge)) {
                result = typeGraph.getNode(edge.getTypeLabel());
                break;
            }
        }
        return result == null ? typeGraph.getFactory().getTopNode() : result;
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
            AspectKind attrKind = getNode().getAttrKind();
            if (attrKind.hasSignature()) {
                Object content = getNode().getAttrAspect().getContent();
                if (content == null) {
                    return VariableNode.TO_STRING_PREFIX
                        + getNode().getNumber();
                } else {
                    return content.toString();
                }
            } else {
                assert attrKind == AspectKind.PRODUCT;
                // delegate the identity string to a corresponding product node
                return "p" + getNode().getNumber();
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
        if (hasError()) {
            for (FormatError error : this.extraErrors) {
                if (result.length() > 0) {
                    result.append("<br>");
                }
                result.append(error.toString());
            }
            HTMLConverter.red.on(result);
        } else {
            if (getNode().getAttrKind().hasSignature()) {
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
        }
        return result;
    }

    /** Adds a quantifier, if the nesting aspect justifies this. */
    @Override
    final public List<StringBuilder> getLines() {
        updateCachedValues();
        return this.lines;
    }

    @Override
    public Collection<Element> getKeys() {
        updateCachedValues();
        return this.keys;
    }

    /** 
     * Updates the cached values of {@link #lines} and {@link #keys},
     * if the model has been modified in the meantime.
     */
    private void updateCachedValues() {
        if (this.jModelTracker.isStale()) {
            this.keys = computeKeys();
            this.lines = computeLines();
        }
    }

    /** Recomputes the set of node lines for this aspect node. */
    private List<StringBuilder> computeLines() {
        getNode().testFixed(true);
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        // show the node identity
        result.addAll(getNodeIdLines());
        // the following used to include hasError() as a disjunct
        if (getJGraph().isShowAspects()) {
            result.addAll(getUserObject().toLines());
            for (AspectEdge edge : getExtraSelfEdges()) {
                if (!isFiltered(edge)) {
                    result.add(getLine(edge));
                }
            }
            if (getNode().getGraphRole() == GraphRole.RULE
                && getNode().getColor() != null) {
                result.add(new StringBuilder(getNode().getColor().toString()));
            }
        } else {
            // show the main aspect correctly
            result.addAll(getAspectLines());
            // show data constants and variables correctly
            result.addAll(getDataLines());
            // show the visible self-edges
            String id =
                getNode().hasId()
                        ? ITALIC_TAG.on(getNode().getId().getContent()) : null;
            for (AspectEdge edge : getJVertexLabels()) {
                if (!isFiltered(edge)) {
                    StringBuilder line = getLine(edge);
                    if (id != null) {
                        if (edge.getDisplayLabel().isNodeType()) {
                            line.insert(0, " : ");
                            line.insert(0, id);
                        } else {
                            // we're not going to have any node types:
                            // add the node id on a separate line
                            result.add(new StringBuilder(id));
                        }
                        id = null;
                    }
                    result.add(line);
                }
            }
            for (AspectEdge edge : getExtraSelfEdges()) {
                if (!isFiltered(edge)) {
                    result.add(getLine(edge));
                }
            }
            if (getNode().getGraphRole() == GraphRole.RULE
                && getNode().hasColor()) {
                StringBuilder line = new StringBuilder("& ");
                line.append(AspectKind.COLOR.getName());
                HTMLTag colorTag =
                    HTMLConverter.createColorTag(Colors.findColor(getNode().getColor().getContentString()));
                result.add(colorTag.on(line));
            }
            if (getNode().getGraphRole() == GraphRole.TYPE
                && getNode().isEdge()) {
                StringBuilder line = new StringBuilder();
                LabelPattern pattern = getNode().getEdgePattern();
                line.append(">> ");
                line.append(pattern.getLabel(pattern.getArgNames().toArray()));
                result.add(line);
            }
        }
        return result;
    }

    @Override
    protected List<StringBuilder> getNodeIdLines() {
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        // if the node has an ID, that will be displayed part of the type
        if (!getNode().hasId()) {
            result.addAll(super.getNodeIdLines());
            if (getNode().hasImport()) {
                result.add(new StringBuilder(ITALIC_TAG.on(IMPORT_TEXT)));
            }
        }
        return result;
    }

    /** Returns lines describing any data content of the JVertex. */
    private List<StringBuilder> getDataLines() {
        List<StringBuilder> result = new ArrayList<StringBuilder>();
        Aspect attrAspect = getNode().getAttrAspect();
        if (attrAspect.getKind().hasSignature()) {
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
                line.append(HTML_FORALL);
                break;
            case FORALL_POS:
                line.append(HTML_FORALL);
                line.append(HTMLConverter.SUPER_TAG.on(HTML_GT + "0"));
                break;
            case EXISTS:
                line.append(HTML_EXISTS);
                break;
            case EXISTS_OPT:
                line.append(HTML_EXISTS);
                line.append(HTMLConverter.SUPER_TAG.on("?"));
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
    protected StringBuilder getLine(Edge edge) {
        AspectEdge aspectEdge = (AspectEdge) edge;
        aspectEdge.testFixed(true);
        StringBuilder result = new StringBuilder();
        Label label =
            getJGraph().isShowAspects() ? aspectEdge.label()
                    : aspectEdge.getDisplayLabel();
        result.append(TypeLabel.toHtmlString(label));
        if (aspectEdge.getKind() == AspectKind.ABSTRACT && label.isNodeType()) {
            result = ITALIC_TAG.on(result);
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
        } else {
            if (getNode().getGraphRole() == GraphRole.TYPE
                && aspectEdge.getAttrKind().hasSignature()) {
                // this is a field declaration
                result.append(TYPE_TEXT);
                result.append(STRONG_TAG.on(aspectEdge.getAttrKind().getName()));
            } else if (aspectEdge.getKind().isRole()) {
                String levelName = aspectEdge.getLevelName();
                if (levelName != null && levelName.length() != 0) {
                    result.append(LEVEL_NAME_SEPARATOR + levelName);
                }
            }
        }
        // use special node label prefixes to indicate edge role
        Aspect edgeAspect = aspectEdge.getAspect();
        if (edgeAspect != null
            && !edgeAspect.equals(getNode().getAspect())
            && (getNode().getGraphRole() == GraphRole.RULE || edgeAspect.getKind() == REMARK)) {
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
        case LET:
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

    /** Recomputes the set of list labels for this aspect node. */
    private Collection<Element> computeKeys() {
        getNode().testFixed(true);
        Collection<Element> result = new ArrayList<Element>();
        if (!this.aspect.isMeta()) {
            for (Edge edge : getJVertexLabels()) {
                Edge key = getKey(edge);
                if (key != null) {
                    result.add(key);
                }
            }
            for (AspectEdge edge : getExtraSelfEdges()) {
                Edge key = getKey(edge);
                if (key != null) {
                    result.add(key);
                }
            }
            Node nodeKey = getNodeKey();
            if (result.isEmpty() || nodeKey instanceof TypeNode
                && !((TypeNode) nodeKey).isTopType()) {
                result.add(nodeKey);
            }
        }
        return result;
    }

    @Override
    protected Node getNodeKey() {
        TypeModelMap typeMap = getJModel().getResourceModel().getTypeMap();
        return typeMap == null ? getNode() : typeMap.getNode(getNode());
    }

    @Override
    protected Edge getKey(Edge edge) {
        TypeModelMap typeMap = getJModel().getResourceModel().getTypeMap();
        return typeMap == null ? edge : typeMap.getEdge((AspectEdge) edge);
    }

    @Override
    public boolean isVisible() {
        // remark nodes are always visible
        if (this.aspect == REMARK) {
            return true;
        }
        // anything explicitly filtered by the level tree is not visible
        RuleLevelTree levelTree = getJGraph().getLevelTree();
        if (levelTree != null && !levelTree.isVisible(this)) {
            return false;
        }
        // parameter nodes, quantifiers and error nodes are always visible
        if (getNode().hasParam() || this.aspect.isQuantifier() || hasError()) {
            return true;
        }
        // anything declared invisible by the super method is not visible
        if (!super.isVisible()) {
            return false;
        }
        Aspect attr = getNode().getAttrAspect();
        // explicit product nodes should be visible
        if (!attr.getKind().hasSignature()) {
            return true;
        }
        // in addition, value nodes or data type nodes may be filtered
        if (getJGraph().isShowValueNodes()) {
            return true;
        }
        // we are now sure that the underlying node has a data type;
        // only variable nodes should be shown
        return getNode().getGraphRole() != GraphRole.TYPE && !attr.hasContent();
    }

    @Override
    public final boolean hasError() {
        return !this.extraErrors.isEmpty() || !this.errors.isEmpty();
    }

    @Override
    public void clearExtraErrors() {
        this.extraErrors.clear();
    }

    @Override
    public void addExtraError(FormatError error) {
        this.extraErrors.add(error);
        refreshAttributes();
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
        AttributeMap result;
        result = AspectJGraph.ASPECT_NODE_ATTR.get(this.aspect).clone();
        if (getJGraph().hasActiveEditor()) {
            GraphConstants.setEditable(result, true);
        }
        return result;
    }

    /** Indicates if this vertex is in fact a nodified edge. */
    public boolean isEdge() {
        return getJGraph().getMode() != JGraphMode.EDIT_MODE
            && getEdgeLabelPattern() != null;
    }

    /**
     * Returns the (possibly {@code null}) edge label pattern, if
     * this node is a nodified edge. 
     */
    public LabelPattern getEdgeLabelPattern() {
        LabelPattern result = null;
        if (getNode().getGraphRole() == GraphRole.HOST) {
            TypeNode typeNode = getNodeType();
            result = typeNode.getLabelPattern();
        }
        return result;
    }

    /** Retrieves a node colour from the model's label store, if any. */
    @Override
    public Color getColor() {
        Color result = super.getColor();
        if (result == null && getNode().getGraphRole() != GraphRole.RULE) {
            if (getNode() != null && getNode().getColor() != null) {
                result = (Color) getNode().getColor().getContent();
            } else {
                result = getNodeType().getColor();
            }
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

    /** 
     * Sets the {@link #jModelTracker} to a fresh value.
     * This is delegated to a separate method because it needs
     * to be invoked upon cloning as well as in the constructor.
     * @see #clone()
     */
    private void resetTracker() {
        this.jModelTracker =
            getJModel() == null ? ChangeCount.DUMMY_TRACKER
                    : getJModel().getModCount().createTracker();
    }

    /** Cached lines. */
    private List<StringBuilder> lines;
    /** Cached tree entries. */
    private Collection<Element> keys;
    /** JModel modification tracker. */
    private Tracker jModelTracker;
    /** The role of the underlying rule node. */
    private AspectKind aspect;
    private Collection<FormatError> errors = new LinkedHashSet<FormatError>();
    private List<FormatError> extraErrors = new ArrayList<FormatError>();

    /** Returns a prototype {@link AspectJVertex} for a given {@link AspectJGraph}. */
    public static AspectJVertex getPrototype(AspectJGraph jGraph) {
        return new AspectJVertex(jGraph, null, null);
    }

    static private final String ASSIGN_TEXT = " = ";
    static private final String TYPE_TEXT = ": ";
    static private final String IMPORT_TEXT = String.format("%simport%s",
        HTMLConverter.toHtml(Util.FRENCH_QUOTES_OPEN),
        HTMLConverter.toHtml(Util.FRENCH_QUOTES_CLOSED));
    static private final String HTML_EXISTS = toHtml(Util.EXISTS);
    static private final String HTML_FORALL = toHtml(Util.FORALL);
    static private final String HTML_GT = toHtml('>');
}