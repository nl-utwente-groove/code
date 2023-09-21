/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.grammar.aspect;

import static nl.utwente.groove.grammar.aspect.AspectKind.ABSTRACT;
import static nl.utwente.groove.grammar.aspect.AspectKind.ARGUMENT;
import static nl.utwente.groove.grammar.aspect.AspectKind.COLOR;
import static nl.utwente.groove.grammar.aspect.AspectKind.CONNECT;
import static nl.utwente.groove.grammar.aspect.AspectKind.DEFAULT;
import static nl.utwente.groove.grammar.aspect.AspectKind.EDGE;
import static nl.utwente.groove.grammar.aspect.AspectKind.EMBARGO;
import static nl.utwente.groove.grammar.aspect.AspectKind.ID;
import static nl.utwente.groove.grammar.aspect.AspectKind.IMPORT;
import static nl.utwente.groove.grammar.aspect.AspectKind.PARAM_ASK;
import static nl.utwente.groove.grammar.aspect.AspectKind.PRODUCT;
import static nl.utwente.groove.grammar.aspect.AspectKind.READER;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.grammar.aspect.AspectContent.ColorContent;
import nl.utwente.groove.grammar.aspect.AspectContent.ConstContent;
import nl.utwente.groove.grammar.aspect.AspectContent.ExprContent;
import nl.utwente.groove.grammar.aspect.AspectContent.IdContent;
import nl.utwente.groove.grammar.aspect.AspectContent.LabelPatternContent;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.rule.OperatorNode;
import nl.utwente.groove.grammar.rule.VariableNode;
import nl.utwente.groove.grammar.type.LabelPattern;
import nl.utwente.groove.graph.ANode;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainLabel;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Fixable;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Graph node implementation that supports aspects.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectNode extends ANode implements AspectElement, Fixable {
    /** Constructs an aspect node with a given number. */
    public AspectNode(int nr, AspectGraph graph) {
        super(nr);
        assert graph.getRole().inGrammar();
        this.graph = graph;
    }

    @Override
    public AspectGraph getGraph() {
        return this.graph;
    }

    /** The aspect graph to which this element belongs. */
    private final AspectGraph graph;

    /** Returns the graph role set for this aspect node. */
    public GraphRole getGraphRole() {
        return getGraph().getRole();
    }

    /**
     * This class does not guarantee unique representatives for the same number,
     * so we need to override {@link #hashCode()} and {@link #equals(Object)}.
     */
    @Override
    protected int computeHashCode() {
        return getNumber() ^ getClass().hashCode();
    }

    /**
     * Use the same prefix as for default nodes, so the error messages
     * remain understandable.
     */
    @Override
    protected String getToStringPrefix() {
        if (has(Category.SORT)) {
            return VariableNode.TO_STRING_PREFIX;
        } else if (has(Category.ATTR)) {
            return OperatorNode.TO_STRING_PREFIX;
        } else {
            return super.getToStringPrefix();
        }
    }

    /**
     * This class does not guarantee unique representatives for the same number,
     * so we need to override {@link #hashCode()} and {@link #equals(Object)}.
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && obj.getClass().equals(getClass())
            && ((AspectNode) obj).getNumber() == getNumber();
    }

    @Override
    public Aspect.Map getAspects() {
        return this.aspects;
    }

    /** The initially empty aspect map. */
    private final Aspect.Map aspects = new Aspect.Map();

    @Override
    public boolean setParsed() {
        boolean result = !isParsed();
        if (result) {
            this.status = Status.PARSED;
            try {
                checkAspects();
                if (isProduct()) {
                    checkSignature();
                }
            } catch (FormatException exc) {
                addErrors(exc.getErrors());
            }
        }
        return result;
    }

    /**
     * Checks for the correctness of product node signatures.
     */
    private void checkSignature() throws FormatException {
        if (this.argNodes == null) {
            throw new FormatException("Product node has no arguments", this);
        }
        if (this.operatorEdge == null) {
            throw new FormatException("Product node has no operators", this);
        }
        int arity = this.argNodes.size();
        Operator operator = this.operatorEdge.getOperator();
        if (arity != operator.getArity()) {
            throw new FormatException("Product node arity %d is incorrect for operator %s", arity,
                operator, this);
        }
        for (int i = 0; i < arity; i++) {
            AspectNode argNode = this.argNodes.get(i);
            if (argNode == null) {
                throw new FormatException("Missing product argument %d", i, this);
            }
        }
        // type correctness of the parameters and result has already been tested for
        // as part of inferInAspect and inferOutAspect
    }

    @Override
    public boolean setTyped() {
        boolean result = !isTyped();
        if (result) {
            setParsed();
            this.status = Status.TYPED;
            if (!hasErrors()) {
                typeExpression();
            }
        }
        return result;
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    /** Indicates that the entire node is fixed. */
    private Status status = Status.NEW;

    /** Types the expression on this node, if any. */
    private void typeExpression() {
        try {
            if (hasValue()) {
                this.value = createValue();
            }
        } catch (FormatException exc) {
            for (FormatError error : exc.getErrors()) {
                this.errors.add(error.extend(this));
            }
        }
    }

    /**
     * Creates a clone of this node, for a given aspect graph.
     * The clone is not yet parsed.
     * @param graph the graph to which the new node belongs
     */
    public AspectNode clone(AspectGraph graph) {
        return clone(graph, getNumber());
    }

    /**
     * Clones an {@link AspectNode}, and also renumbers it.
     * The clone is not yet parsed.
     * @param graph the graph to which the new node belongs
     * @param newNr the number for the new aspect node.
     */
    public AspectNode clone(AspectGraph graph, int newNr) {
        AspectNode result = new AspectNode(newNr, graph);
        for (AspectLabel label : this.nodeLabels) {
            result.setAspects(label);
        }
        return result;
    }

    @Override
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    /**
     * Adds a node label to this node, and processes the resulting aspects.
     */
    public void setAspects(AspectLabel label) {
        assert label.isFixed();
        assert getGraphRole() == label.getGraphRole();
        testFixed(false);
        this.nodeLabels.add(label);
        if (label.hasErrors()) {
            addErrors(label.getErrors());
        } else {
            for (Aspect aspect : label.getAspects()) {
                try {
                    addAspect(aspect);
                } catch (FormatException exc) {
                    this.errors.addAll(exc.getErrors());
                }
            }
        }
    }

    /**
     * Adds a declared aspect value to this node.
     * @throws FormatException if the added value conflicts with a previously
     * declared one
     */
    private void addAspect(Aspect aspect) throws FormatException {
        assert aspect.isForNode(getGraphRole()) : String
            .format("Inappropriate node aspect %s", aspect);
        AspectKind kind = aspect.getKind();
        switch (kind) {
        case PARAM_BI, PARAM_IN, PARAM_OUT, PARAM_ASK -> setPar(aspect);
        case ID -> setIdAspect(aspect);
        case EDGE -> setEdgeAspect(aspect);
        case COLOR -> setColorAspect(aspect);
        case IMPORT -> setImportAspect(aspect);
        case ABSTRACT -> setAbstractAspect(aspect);
        case PRODUCT -> setProductAspect(aspect);
        case INT, REAL, BOOL, STRING -> setDataAspect(aspect);
        default -> // this aspect determines the type of node
            setAspect(aspect);
        }
        //        if (kind.isAttrKind()) {
        //            if (hasAttrAspect() && !isAttrConsistent(getAttrAspect(), aspect)) {
        //                throw new FormatException("Conflicting node aspects %s and %s", getAttrKind(),
        //                    aspect, this);
        //            }
        //            setAttrAspect(aspect);
        //        } else if (kind.isParam()) {
        //            if (hasPar()) {
        //                throw new FormatException("Conflicting parameter aspects %s and %s", this.parAspect,
        //                    aspect, this);
        //            } else {
        //                setPar(aspect);
        //            }
        //        } else if (kind == ID) {
        //            setIdAspect(aspect);
        //        } else if (kind == EDGE) {
        //            setEdgeAspect(aspect);
        //        } else if (kind == COLOR) {
        //            setColorAspect(aspect);
        //        } else if (kind == IMPORT) {
        //            setImportAspect(aspect);
        //        } else if (hasAspect()) {
        //            throw new FormatException("Conflicting node aspects %s and %s", getAspect(), aspect,
        //                this);
        //        } else if (kind.isRole() && aspect.hasContent()) {
        //            throw new FormatException("Node aspect %s should not have quantifier name", aspect,
        //                this);
        //        } else {
        //            setAspect(aspect);
        //            if (kind.isQuantifier() && aspect.hasContent()) {
        //                setId(aspect.getContentString());
        //            }
        //        }
    }

    /**
     * Concludes the processing of the node labels.
     * Afterwards {@link #setAspects(AspectLabel)} should not be called
     * any more.
     */
    private void checkAspects() throws FormatException {
        if (getGraphRole() == GraphRole.RULE) {
            if (getParKind() == PARAM_ASK && !hasDataAspect()) {
                throw new FormatException("User-provided parameter must be a data value", this);
            }
            if (hasDataAspect() && getKind() != READER && getKind() != EMBARGO) {
                throw new FormatException("Data node can't be %s", getAspect(), this);
            }
        }
        if (isImported()) {
            if (hasDataAspect()) {
                throw new FormatException("Can't import data type node", getSort(), this);
            }
            if (getKind() == ABSTRACT) {
                throw new FormatException("Can't abstract an imported type", getSort(), this);
            }
        }
        if (isProduct()) {
            if (hasId()) {
                throw new FormatException("Node identifier ('%s') not allowed for operator node",
                    getId(), this);
            }
            if (hasDataAspect()) {
                throw new FormatException("Operator node can't have data type ('%s')", getSort(),
                    this);
            }
            if (getKind() != READER && getKind() != EMBARGO) {
                throw new FormatException("Operator node can't be '%s'", getAspect(), this);
            }
        }
        if (getGraphRole() == GraphRole.HOST && hasDataAspect() && hasId()) {
            throw new FormatException("Node identifier ('%s') not allowed for value node %s",
                getId(), getValue(), this);
        }
    }

    /**
     * Infers aspect information from an incoming edge for this node.
     * Inferences from this node to the edge have already been drawn.
     */
    public void inferInAspect(AspectEdge edge) throws FormatException {
        assert edge.target() == this;
        testFixed(false);
        if (edge.getAttrKind() == ARGUMENT) {
            if (!hasAttrAspect()) {
                throw new FormatException("Target node of %s-edge should be attribute",
                    edge.label(), this);
            }
        } else if (edge.getKind() == CONNECT) {
            if (getKind() != EMBARGO) {
                throw new FormatException("Target node of %s-edge should be embargo", edge.label(),
                    this);
            }
        } else if ((edge.isNestedAt() || edge.isNestedIn()) && !getKind().isQuantifier()) {
            throw new FormatException("Target node of %s-edge should be quantifier", edge.label(),
                this);
        } else if (edge.isNestedCount()) {
            if (getSort() != Sort.INT) {
                throw new FormatException("Target node of %s-edge should be int-node", edge.label(),
                    this);
            }
        } else if (edge.isOperator()) {
            Sort operSort = edge.getOperator().getResultType();
            if (!hasDataAspect()) {
                throw new FormatException("Target node of %s-edge should be %s-attribute",
                    edge.label(), operSort, this);
            } else if (getSort() != operSort) {
                throw new FormatException(
                    "Inferred type %s of %s-target conflicts with declared type %s", operSort,
                    edge.label(), getSort(), this);
            }
        }
    }

    /**
     * Infers aspect information from an outgoing edge for this node.
     * Inferences from this node to the edge have already been drawn.
     */
    public void inferOutAspect(AspectEdge edge) throws FormatException {
        assert edge.source() == this;
        testFixed(false);
        //setNodeLabelsFixed();
        AspectLabel edgeLabel = edge.label();
        if (edge.getKind() == CONNECT) {
            if (getKind() != EMBARGO) {
                throw new FormatException("Source node of %s-edge should be embargo", edge.label(),
                    this);
            }
        } else if (edge.isNestedAt()) {
            if (getKind().isMeta()) {
                throw new FormatException("Source node of %s-edge should be rule element",
                    edgeLabel, this);
            }
            this.nestingLevelEdge = edge;
        } else if (edge.isNestedIn()) {
            if (!getKind().isQuantifier()) {
                throw new FormatException("Source node of %s-edge should be quantifier", edgeLabel,
                    this);
            }
            // collect collective nesting grandparents to test for circularity
            Set<AspectNode> grandparents = new HashSet<>();
            AspectNode parent = edge.target();
            while (parent != null) {
                grandparents.add(parent);
                parent = parent.getNestingParent();
            }
            if (grandparents.contains(this)) {
                throw new FormatException("Circularity in the nesting hierarchy", this);
            }
            this.nestingParentEdge = edge;
        } else if (edge.isNestedCount()) {
            if (getKind() != AspectKind.FORALL && getKind() != AspectKind.FORALL_POS) {
                throw new FormatException("Source node of %s-edge should be universal quantifier",
                    edgeLabel, this);
            }
            this.matchCount = edge.target();
        } else if (edge.isArgument()) {
            setProductAspect(PRODUCT.getAspect());
            if (this.argNodes == null) {
                this.argNodes = new ArrayList<>();
            }
            int index = edge.getArgument();
            // extend the list if necessary
            while (this.argNodes.size() <= index) {
                this.argNodes.add(null);
            }
            if (this.argNodes.get(index) != null) {
                throw new FormatException("Duplicate %s-edge", edge.label(), this);
            }
            this.argNodes.set(index, edge.target());
            // infer target type if an operator edge is already present
            if (this.operatorEdge != null) {
                List<Sort> paramTypes = this.operatorEdge.getOperator().getParamTypes();
                if (index < paramTypes.size()) {
                    edge.target().setDataAspect(Aspect.getAspect(paramTypes.get(index)));
                }
            }
        } else if (edge.isOperator()) {
            setProductAspect(PRODUCT.getAspect());
            if (this.operatorEdge == null) {
                this.operatorEdge = edge;
            } else if (!this.operatorEdge
                .getOperator()
                .getParamTypes()
                .equals(edge.getOperator().getParamTypes())) {
                throw new FormatException("Conflicting operator signatures for %s and %s",
                    this.operatorEdge.label(), edgeLabel, this);
            } else if (!hasErrors() && this.argNodes != null) {
                // only go here if there are no (signature) errors
                // infer operand types of present argument edges
                for (int i = 0; i < this.argNodes.size(); i++) {
                    AspectNode argNode = this.argNodes.get(i);
                    if (argNode != null) {
                        Sort paramType = this.operatorEdge.getOperator().getParamTypes().get(i);
                        argNode.setDataAspect(Aspect.getAspect(paramType));
                    }
                }
            }
        } else if (edge.getKind() == ABSTRACT
            && edge.getTypeLabel().getRole() == EdgeRole.NODE_TYPE) {
            setAspect(ABSTRACT.getAspect());
        }
    }

    /**
     * Returns the list of node labels added to this node.
     */
    public List<AspectLabel> getNodeLabels() {
        return this.nodeLabels;
    }

    /**
     * Returns the list of (plain) labels that should be put on this
     * node in the plain graph view.
     */
    public List<PlainLabel> getPlainLabels() {
        List<PlainLabel> result = new ArrayList<>();
        for (AspectLabel label : this.nodeLabels) {
            String text = label.toString();
            if (text.length() > 0) {
                result.add(PlainLabel.parseLabel(text));
            }
        }
        return result;
    }

    /** Sets the (aspect) type of this node. */
    void setAspect(Aspect aspect) throws FormatException {
        assert !aspect.getKind().isAttrKind() && aspect.getKind() != PRODUCT
            && !aspect.getKind().isParam() : String
                .format("Aspect %s is not a valid node type", aspect);
        if (aspect.getKind().isRole() && aspect.hasContent()) {
            throw new FormatException("Node aspect %s should not have quantifier name", aspect,
                this);
        }
        if (!getAspect().equals(aspect)) {
            throw new FormatException("Conflicting (inferred) node aspects %s and %s", getAspect(),
                aspect, this);
        }
        this.aspect = aspect;
        if (aspect.getKind().isQuantifier() && aspect.hasContent()) {
            String id = aspect.getContentString();
            Aspect idAspect = AspectKind.ID.getAspect().newInstance(id, GraphRole.RULE);
            setIdAspect(idAspect);
        }
    }

    @Override
    public @NonNull Aspect getAspect() {
        Aspect result = this.aspect;
        if (result == null) {
            if (getGraphRole() == GraphRole.RULE) {
                result = READER.getAspect();
            } else {
                result = DEFAULT.getAspect();
            }
        }
        return result;
    }

    /** Checks if this is a quantifier node.
     * @return {@code true} if this is a quantifier node.
     */
    public boolean isQuantifier() {
        return getAspect().getKind().isQuantifier();
    }

    /** Sets or specialises the data aspect of this node. */
    private void setDataAspect(Aspect aspect) throws FormatException {
        AspectKind attrKind = aspect.getKind();
        assert attrKind.hasSort() : String.format("Aspect %s has no data sort", aspect);
        // it may be the new attribute is inferred from an incoming edge
        // but then we only change the attribute if the new one is "better"
        Aspect currAspect = getDataAspect();
        if (currAspect == null) {
            this.dataAspect = aspect;
        } else if (currAspect.getKind() != attrKind) {
            throw new FormatException("Conflicting (inferred) types %s and %s",
                currAspect.getKind(), attrKind, this);
        } else if (!currAspect.hasContent()) {
            this.dataAspect = aspect;
        } else if (aspect.hasContent()) {
            throw new FormatException("Duplicate attribute content %s and %s",
                currAspect.getContentString(), aspect.getContentString(), this);
        }
    }

    /** Returns the data aspect of this node, if any. */
    public @Nullable Aspect getDataAspect() {
        return getAspects().get(AspectKind.Category.TYPE);
    }

    /** Indicates if this represents a data attribute. */
    public boolean hasDataAspect() {
        return has(AspectKind.Category.TYPE);
    }

    /** Returns the data sort of this node, if any. */
    public @Nullable Sort getSort() {
        Aspect data = this.dataAspect;
        return data == null
            ? null
            : data.getKind().getSort();
    }

    /** Indicates if this stands for a data value or expression. */
    public boolean hasValue() {
        assert isParsed();
        var data = getDataAspect();
        return data != null && (data.getContent() instanceof ConstContent
            || data.getContent() instanceof ExprContent);
    }

    private Expression createValue() throws FormatException {
        Aspect data = getDataAspect();
        assert data != null;
        AspectContent content = data.getContent();
        if (content instanceof ConstContent c) {
            return c.get();
        } else if (content instanceof ExprContent e) {
            return e.get().toExpression(getGraph().getTyping());
        } else {
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Returns the expression contained in the attribute aspect, if any. */
    public Expression getValue() {
        var result = this.value;
        if (result == null && isParsed() && getGraph().isNodeComplete()) {
            setTyped();
            result = this.value;
        }
        return result;
    }

    /** The data aspect of this node, if any. */
    private @Nullable Aspect dataAspect;

    private @Nullable Expression value;

    /** Returns the line describing the data value of this node, if any. */
    public Line getValueLine() {
        assert hasValue();
        assert isParsed();
        return getValue() == null
            ? ((ExprContent) getAspect().getContent()).get().toLine()
            : getValue().toLine();
    }

    /**
     * If this is a product node, returns the list of
     * argument nodes reached by outgoing argument edges.
     * @return an ordered list of argument nodes, or {@code null} if
     * this is not a product node.
     */
    public List<AspectNode> getArgNodes() {
        testFixed(true);
        return this.argNodes;
    }

    /** Changes the parameter aspect of this node. */
    private void setPar(Aspect parAspect) throws FormatException {
        assert parAspect.getKind() == DEFAULT || parAspect.getKind().isParam() : String
            .format("Aspect %s is not a parameter", parAspect);
        if (hasPar()) {
            throw new FormatException("Conflicting parameter aspects %s and %s", this.parAspect,
                parAspect, this);
        }
        this.parAspect = parAspect;
    }

    /** Returns the parameter aspect of this node, if any. */
    public Aspect getParAspect() {
        return this.parAspect;
    }

    /** Indicates if this represents a rule parameter. */
    public boolean hasPar() {
        return this.parAspect != null;
    }

    /** Returns the parameter kind of this node, if any. */
    public AspectKind getParKind() {
        return hasPar()
            ? getParAspect().getKind()
            : DEFAULT;
    }

    /** The parameter aspect of this node, if any. */
    private Aspect parAspect;

    /** Sets the identifier aspect of this node. */
    private void setIdAspect(Aspect aspect) throws FormatException {
        assert aspect.getKind() == ID : String.format("Aspect %s is not an identifier", aspect);
        if (this.idAspect != null) {
            throw new FormatException("Duplicate node identifiers %s and %s",
                this.idAspect.getContentString(), aspect.getContentString(), this);
        }
        this.idAspect = aspect;
    }

    /** Returns the identifier aspect of this node, if any. */
    public @Nullable Aspect getIdAspect() {
        return this.idAspect;
    }

    /** Indicates if this node has an identifier. */
    public boolean hasId() {
        return this.idAspect != null;
    }

    /** The identifier aspect of this node, if any. */
    private @Nullable Aspect idAspect;

    /** Returns the identifier of this node, if any. */
    public @Nullable String getId() {
        Aspect idAspect = getIdAspect();
        return idAspect == null
            ? null
            : idAspect.getContentString();
    }

    /** Sets the colour aspect of this node. */
    private void setColorAspect(Aspect color) throws FormatException {
        assert color.getKind() == COLOR : String.format("Aspect %s is not a color", color);
        if (this.colorAspect != null) {
            throw new FormatException("Duplicate colour specification");
        }
        this.colorAspect = color;
    }

    /** Returns the colour aspect of this node, if any. */
    public @Nullable Aspect getColorAspect() {
        return this.colorAspect;
    }

    /** Returns the colour of this node, if the colour aspect has been set. */
    public @Nullable Color getColor() {
        Aspect colorAspect = getColorAspect();
        return colorAspect == null
            ? null
            : ((ColorContent) colorAspect.getContent()).get();
    }

    /** Indicates if this node has an colour. */
    public boolean hasColor() {
        return this.colorAspect != null;
    }

    /** The colour aspect of this node, if any. */
    private @Nullable Aspect colorAspect;

    /** Sets the import aspect of this node. */
    private void setImportAspect(Aspect aspect) throws FormatException {
        assert aspect.getKind() == IMPORT : String.format("Aspect %s is not an import", aspect);
        if (this.importAspect != null) {
            throw new FormatException("Duplicate import specification");
        }
        this.importAspect = aspect;
    }

    /** Returns the import aspect of this node, if any. */
    public Aspect getImport() {
        return this.importAspect;
    }

    /** Indicates if this node is imported (i.e., has an import aspect). */
    public boolean isImported() {
        return this.importAspect != null;
    }

    /** The import aspect of this node, if any. */
    private Aspect importAspect;

    /** Sets the abstract aspect of this node. */
    private void setAbstractAspect(Aspect aspect) throws FormatException {
        assert aspect.getKind() == ABSTRACT : String.format("Aspect %s is not an abstract", aspect);
        if (this.abstractAspect != null) {
            throw new FormatException("Duplicate import specification");
        }
        this.abstractAspect = aspect;
    }

    /** Returns the import aspect of this node, if any. */
    public Aspect getAbstract() {
        return this.abstractAspect;
    }

    /** Indicates if this node is abstract (i.e., has an abstract aspect). */
    public boolean isAbstract() {
        return this.abstractAspect != null;
    }

    /** The abstract aspect of this node, if any. */
    private Aspect abstractAspect;

    /** Sets the product aspect of this node. */
    private void setProductAspect(Aspect aspect) throws FormatException {
        assert aspect.getKind() == ABSTRACT : String.format("Aspect %s is not an abstract", aspect);
        if (this.productAspect != null) {
            throw new FormatException("Duplicate import specification");
        }
        this.productAspect = aspect;
    }

    /** Indicates if this node is an operator node (i.e., has a product aspect). */
    public boolean isProduct() {
        return this.productAspect != null;
    }

    /** The product aspect of this node, if any. */
    private Aspect productAspect;

    /** Indicates if this is a nodified edge. */
    public boolean isEdge() {
        return getEdge() != null;
    }

    /** Sets the edge aspect of this node. */
    private void setEdgeAspect(Aspect edge) throws FormatException {
        assert edge.getKind() == EDGE : String.format("Aspect %s is not an edge declaration", edge);
        if (this.edge != null) {
            throw new FormatException("Duplicate edge pattern specification");
        }
        this.edge = edge;
    }

    /** Returns the colour aspect of this node, if any. */
    public Aspect getEdge() {
        return this.edge;
    }

    /** Returns the edge label pattern of this node, if any. */
    public LabelPattern getEdgePattern() {
        return isEdge()
            ? ((LabelPatternContent) getEdge().getContent()).get()
            : null;
    }

    /** The edge declaration of this node, if any. */
    private Aspect edge;

    /**
     * Retrieves the nesting level of this aspect node.
     * Only non-{@code null} if this node is a rule node.
     */
    public AspectNode getNestingLevel() {
        AspectEdge edge = getNestingLevelEdge();
        return edge == null
            ? null
            : edge.target();
    }

    /**
     * Retrieves the edge to the nesting level of this aspect node.
     * Only non-{@code null} if this node is a rule node.
     */
    public AspectEdge getNestingLevelEdge() {
        return this.nestingLevelEdge;
    }

    /**
     * Retrieves the parent of this node in the nesting hierarchy.
     * Only non-{@code null} if this node is a quantifier node.
     */
    public AspectNode getNestingParent() {
        AspectEdge edge = getNestingParentEdge();
        return edge == null
            ? null
            : edge.target();
    }

    /**
     * Retrieves edge to the parent of this node in the nesting hierarchy.
     * Only non-{@code null} if this node is a quantifier node.
     */
    public AspectEdge getNestingParentEdge() {
        return this.nestingParentEdge;
    }

    /** Returns the optional level name, if this is a quantifier node. */
    public String getLevelName() {
        if (getKind().isQuantifier() && getAspect().hasContent()) {
            return ((IdContent) getAspect().getContent()).get();
        } else {
            return null;
        }
    }

    /** The aspect node representing the nesting level of this node. */
    private AspectEdge nestingLevelEdge;

    /** The aspect node representing the parent of this node in the nesting
     * hierarchy. */
    private AspectEdge nestingParentEdge;

    /**
     * Retrieves the node encapsulating the match count for this node.
     * Only non-{@code null} if this node is a universal quantifier node.
     */
    public AspectNode getMatchCount() {
        return this.matchCount;
    }

    /** The aspect node representing the match count of a universal quantifier. */
    private AspectNode matchCount;

    /** The list of aspect labels defining node aspects. */
    private final List<AspectLabel> nodeLabels = new ArrayList<>();
    /** A list of argument types, if this represents a product node. */
    private List<AspectNode> argNodes;
    /** The operator of an outgoing operator edge. */
    private AspectEdge operatorEdge;
    /** List of syntax errors in this node. */
    private final FormatErrorSet errors = new FormatErrorSet();
}
