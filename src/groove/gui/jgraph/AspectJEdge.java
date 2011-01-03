package groove.gui.jgraph;

import static groove.gui.jgraph.JAttr.RULE_EDGE_ATTR;
import static groove.view.aspect.AspectKind.ARGUMENT;
import static groove.view.aspect.AspectKind.REMARK;
import groove.graph.Label;
import groove.gui.Options;
import groove.gui.jgraph.JAttr.AttributeMap;
import groove.trans.RuleLabel;
import groove.util.Converter;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

import java.awt.Font;
import java.util.Collections;
import java.util.Set;

import org.jgraph.graph.GraphConstants;

/**
 * Specialized j-edge for rule graphs, with its own tool tip text.
 */
public class AspectJEdge extends GraphJEdge<AspectNode,AspectEdge> {
    /** Creates a j-edge on the basis of a given (aspectual) edge. */
    public AspectJEdge(AspectJModel jModel, AspectEdge edge) {
        super(jModel, edge);
        this.aspect = edge.getKind();
    }

    @Override
    public AspectJModel getJModel() {
        return (AspectJModel) super.getJModel();
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
     * Returns <tt>true</tt> only if the aspect values of the edge to be
     * added equal those of this j-edge, and the superclass is also willing.
     */
    @Override
    public boolean addEdge(AspectEdge edge) {
        if (edge.equalsAspects(getEdge())) {
            return super.addEdge(edge);
        } else {
            return false;
        }
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
        return this.error;
    }

    /** Sets the error flag of this edge. */
    final void setError(boolean error) {
        this.error = error;
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

    private final AspectKind aspect;
    private boolean error;

    /** Separator between level name and edge label. */
    private static final char LEVEL_NAME_SEPARATOR = ':';
}