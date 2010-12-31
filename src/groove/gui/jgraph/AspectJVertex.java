package groove.gui.jgraph;

import static groove.view.aspect.AspectKind.REMARK;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.LabelKind;
import groove.graph.TypeLabel;
import groove.gui.Options;
import groove.trans.RuleLabel;
import groove.util.Converter;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgraph.graph.AttributeMap;

/**
 * Specialized j-vertex for rule graphs, with its own tool tip text.
 */
public class AspectJVertex extends GraphJVertex<AspectNode,AspectEdge> {
    /** Creates a j-vertex on the basis of a given (aspectual) node. */
    public AspectJVertex(AspectJModel jModel, AspectNode node) {
        super(jModel, node);
        this.aspect = node.getKind();
    }

    @Override
    public AspectJModel getJModel() {
        return (AspectJModel) super.getJModel();
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
        if (AspectJModel.ROLE_NAMES.containsKey(this.aspect)) {
            Converter.toUppercase(result, false);
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
        List<StringBuilder> result = super.getLines();
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
            setRole(result, edgeAspect.getKind());
        }
        return result;
    }

    /**
     * Adds a textual prefix and a HTML colour to a given node line,
     * depending on an edge role.
     */
    public void setRole(StringBuilder text, AspectKind edgeRole) {
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
        Collection<Label> result = new ArrayList<Label>();
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
                AspectJEdge jEdge = (AspectJEdge) edgeObject;
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
    public Label getLabel(AspectEdge edge) {
        return edge.getDisplayLabel();
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
        return this.error;
    }

    /** Sets the error flag of this vertex. */
    final void setError(boolean error) {
        this.error = error;
    }

    @Override
    protected AttributeMap createAttributes() {
        return JAttr.RULE_NODE_ATTR.get(this.aspect).clone();
    }

    /** The role of the underlying rule node. */
    private final AspectKind aspect;
    private boolean error;
    static private final String ASSIGN_TEXT = " = ";
    static private final String TYPE_TEXT = ": ";
}