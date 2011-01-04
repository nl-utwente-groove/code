package groove.gui.jgraph;

import static groove.gui.jgraph.JAttr.RULE_EDGE_ATTR;
import static groove.view.aspect.AspectKind.ARGUMENT;
import static groove.view.aspect.AspectKind.REMARK;
import groove.graph.GraphRole;
import groove.graph.Label;
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

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgraph.graph.GraphConstants;

/**
 * Specialized j-edge for rule graphs, with its own tool tip text.
 */
public class AJEdge extends GraphJEdge<AspectNode,AspectEdge> implements AJCell {
    /** Creates a j-edge on the basis of a given (aspectual) edge. */
    public AJEdge(AJModel jModel, AspectEdge edge) {
        super(jModel, edge);
        setUserObject(null);
        this.aspect = edge.getKind();
    }

    @Override
    public AJModel getJModel() {
        return (AJModel) super.getJModel();
    }

    /** Clears the errors and the aspect, in addition to calling the super method. */
    @Override
    void reset() {
        super.reset();
        this.errors.clear();
        this.aspect = null;
    }

    /**
     * Returns <tt>true</tt> only if the aspect values of the edge to be
     * added equal those of this j-edge, and the superclass is also willing.
     */
    @Override
    public boolean addEdge(AspectEdge edge) {
        boolean first = getEdges().isEmpty();
        super.addEdge(edge);
        this.errors.addAll(edge.getErrors());
        if (first) {
            this.aspect = edge.getKind();
        } else if (!edge.equalsAspects(getEdge())) {
            this.errors.add(new FormatError(
                "Conflicting aspects in edge labels %s and %s",
                getEdge().label(), edge.label(), this));
        }
        return true;
    }

    @Override
    StringBuilder getEdgeDescription() {
        StringBuilder result = new StringBuilder();
        AspectKind attrKind = getEdge().getAttrKind();
        if (attrKind == ARGUMENT) {
            result.append(new StringBuilder("Argument edge"));
        } else if (attrKind.isTypedData()) {
            result.append(new StringBuilder("Operation edge"));
        } else {
            result.append(super.getEdgeDescription());
        }
        if (AspectJModel.ROLE_DESCRIPTIONS.containsKey(this.aspect)) {
            result.append("<br>"
                + AspectJModel.ROLE_DESCRIPTIONS.get(this.aspect));
        }
        return result;
    }

    @Override
    StringBuilder getEdgeKindDescription() {
        StringBuilder result = super.getEdgeKindDescription();
        if (AspectJModel.ROLE_NAMES.containsKey(this.aspect)) {
            Converter.toUppercase(result, false);
            result.insert(0, " ");
            result.insert(0, AspectJModel.ROLE_NAMES.get(this.aspect));
        }
        return result;
    }

    /** This implementation returns the (unparsed) label of the model edge. */
    @Override
    public Label getLabel(AspectEdge edge) {
        return edge.getDisplayLabel();
    }

    @Override
    public Set<? extends Label> getListLabels(AspectEdge edge) {
        Set<? extends Label> result;
        Label label = edge.getRuleLabel();
        if (label != null && ((RuleLabel) label).isMatchable()) {
            result = ((RuleLabel) label).getMatchExpr().getTypeLabels();
        } else {
            result = Collections.singleton(edge.getDisplayLabel());
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
            for (Aspect aspect : edge.label().getAspects()) {
                result.append(aspect);
            }
        } else if (this.aspect.isRole()) {
            // add nesting level, if any
            String levelName = edge.getLevelName();
            if (levelName != null && levelName.length() != 0) {
                result.append(levelName + LEVEL_NAME_SEPARATOR);
            }
        }
        result.append(super.getLine(edge));
        return result;
    }

    /**
     * This implementation makes remark edges invisible as demanded by the
     * {@link Options#SHOW_REMARKS_OPTION}.
     */
    @Override
    public boolean isVisible() {
        return super.isVisible()
            && (getJModel().isShowRemarks() || this.aspect != REMARK);
    }

    @Override
    public boolean isSourceLabel() {
        boolean result = super.isSourceLabel();
        if (!result) {
            result = isDataEdgeSourceLabel();
        }
        return result;
    }

    /**
     * Only returns <code>true</code> if this edge has the same aspect
     * values as the source node. This is to prevent ambiguities.
     */
    public boolean isDataEdgeSourceLabel() {
        boolean result =
            !getJModel().isShowValueNodes() && this.aspect != REMARK;
        if (result) {
            if (this.aspect.isRole()) {
                // we're in a rule graph; watch for parameters and variable nodes
                result =
                    getTargetVertex().getNode().getAttrAspect().hasContent()
                        && !getTargetVertex().getNode().hasParam();
            } else {
                result =
                    getTargetVertex().getNode().getAttrKind().isTypedData();
            }
        }
        return result;
    }

    @Override
    public final boolean hasError() {
        return !this.errors.isEmpty();
    }

    /** Returns the (possibly empty) set of errors in this JEdge. */
    public Collection<FormatError> getErrors() {
        return this.errors;
    }

    @Override
    protected AttributeMap createAttributes() {
        AspectEdge edge = getEdge();
        AttributeMap result = RULE_EDGE_ATTR.get(edge.getKind()).clone();
        RuleLabel ruleModelLabel = edge.getRuleLabel();
        if (ruleModelLabel != null) {
            if (ruleModelLabel.isEmpty() || ruleModelLabel.isNeg()
                && ruleModelLabel.getNegOperand().isEmpty()) {
                // remove edge arrow
                GraphConstants.setLineEnd(result, GraphConstants.ARROW_NONE);
            } else if (!ruleModelLabel.isAtom()) {
                setFontAttr(result, Font.ITALIC);
            }
        }
        return result;
    }

    /** Modifies the font attribute in the given attribute map. */
    final protected void setFontAttr(AttributeMap result, int fontAttr) {
        Font currentFont = GraphConstants.getFont(result);
        GraphConstants.setFont(result, currentFont.deriveFont(fontAttr));
    }

    public void saveToUserObject() {
        // collect the edge information
        AJObject userObject = getUserObject();
        userObject.clear();
        userObject.addEdges(getEdges());
    }

    @Override
    public void loadFromUserObject(GraphRole role) {
        reset();
        AspectParser parser = AspectParser.getInstance(role);
        for (String text : getUserObject()) {
            AspectLabel label = parser.parse(text);
            AspectEdge edge =
                new AspectEdge(getSourceNode(), label, getTargetNode(), role);
            try {
                edge.setFixed();
            } catch (FormatException e) {
                // do nothing; the errors in the edge will be processed later
            }
            addEdge(edge);
        }
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

    private AspectKind aspect;

    private List<FormatError> errors = new ArrayList<FormatError>();

    /** Separator between level name and edge label. */
    private static final char LEVEL_NAME_SEPARATOR = ':';
}