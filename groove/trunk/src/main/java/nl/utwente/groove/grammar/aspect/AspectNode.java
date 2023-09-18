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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.grammar.aspect.AspectContent.ColorContent;
import nl.utwente.groove.grammar.aspect.AspectContent.ConstContent;
import nl.utwente.groove.grammar.aspect.AspectContent.ExprContent;
import nl.utwente.groove.grammar.aspect.AspectContent.IdContent;
import nl.utwente.groove.grammar.aspect.AspectContent.LabelPatternContent;
import nl.utwente.groove.grammar.aspect.AspectContent.NullContent;
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
        if (hasAttrAspect()) {
            if (getAttrKind().hasSort()) {
                return VariableNode.TO_STRING_PREFIX;
            } else {
                return OperatorNode.TO_STRING_PREFIX;
            }
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
    public boolean setParsed() {
        boolean result = !isParsed();
        if (result) {
            this.status = Status.PARSED;
            try {
                checkAspects();
                if (getAttrKind() == PRODUCT) {
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
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    @Override
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    private void addErrors(Collection<FormatError> errors) {
        for (FormatError error : errors) {
            this.errors.add(error.extend(this));
        }
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
            try {
                for (Aspect aspect : label.getAspects()) {
                    addAspect(aspect);
                }
            } catch (FormatException exc) {
                this.errors.addAll(exc.getErrors());
            }
        }
    }

    /**
     * Concludes the processing of the node labels.
     * Afterwards {@link #setAspects(AspectLabel)} should not be called
     * any more.
     */
    private void checkAspects() throws FormatException {
        try {
            if (getGraphRole() == GraphRole.RULE) {
                // rule nodes that are not explicitly typed must be readers
                if (!hasAspect()) {
                    setAspect(READER.getAspect());
                }
                if (getParKind() == PARAM_ASK && !getAttrKind().hasSort()) {
                    throw new FormatException("User-provided parameter must be a data value");
                }
                if (hasAttrAspect() && getKind() != READER && getKind() != EMBARGO) {
                    throw new FormatException("Conflicting aspects %s and %s", getAttrAspect(),
                        getAspect());
                }
            } else if (getKind().isRole()) {
                throw new FormatException("Node aspect %s only allowed in rules", getAspect(),
                    this);
            } else if (!hasAspect()) {
                setAspect(AspectKind.DEFAULT.getAspect());
            }
            if (hasImport()) {
                if (getAttrKind().hasSort()) {
                    throw new FormatException("Can't import data type", getAttrKind(), this);
                } else if (getKind() == ABSTRACT) {
                    throw new FormatException("Can't abstract an imported type", getAttrKind(),
                        this);
                }
            }
            if (getGraphRole() == GraphRole.HOST && hasAttrAspect() && hasId()) {
                throw new FormatException("Node identifier ('%s') not allowed for value node %s",
                    getId(), getAttrAspect().getContentString(), this);
            }
        } finally {
            if (!hasAttrAspect()) {
                setAttrAspect(AspectKind.DEFAULT.getAspect());
            }
        }
    }

    /**
     * Adds a declared aspect value to this node.
     * @throws FormatException if the added value conflicts with a previously
     * declared one
     */
    private void addAspect(Aspect value) throws FormatException {
        assert value.isForNode(getGraphRole()) : String
            .format("Inappropriate node aspect %s", value);
        AspectKind kind = value.getKind();
        if (kind.isAttrKind()) {
            if (hasAttrAspect() && !isAttrConsistent(getAttrAspect(), value)) {
                throw new FormatException("Conflicting node aspects %s and %s", getAttrKind(),
                    value, this);
            }
            setAttrAspect(value);
        } else if (kind.isParam()) {
            if (hasPar()) {
                throw new FormatException("Conflicting parameter aspects %s and %s", this.parAspect,
                    value, this);
            } else {
                setPar(value);
            }
        } else if (kind == ID) {
            setId(value);
        } else if (kind == EDGE) {
            setEdge(value);
        } else if (kind == COLOR) {
            setColor(value);
        } else if (kind == IMPORT) {
            setImport(value);
        } else if (hasAspect()) {
            throw new FormatException("Conflicting node aspects %s and %s", getAspect(), value,
                this);
        } else if (kind.isRole() && value.hasContent()) {
            throw new FormatException("Node aspect %s should not have quantifier name", value,
                this);
        } else {
            setAspect(value);
            if (kind.isQuantifier() && value.hasContent()) {
                setId(value.getContentString());
            }
        }
    }

    /**
     * Tests if two attribute aspects are consistent.
     * This is only the case if they are equal,
     * or one specifies a data value whereas the other specifies its type.
     */
    private boolean isAttrConsistent(Aspect one, Aspect two) {
        assert one.getKind().isAttrKind() && two.getKind().isAttrKind();
        if (one.equals(two)) {
            return true;
        }
        if (!one.getKind().hasSort() || !two.getKind().hasSort()) {
            return false;
        }
        if (!one.getKind().equals(two.getKind())) {
            return false;
        }
        return one.getContent() instanceof NullContent || two.getContent() instanceof NullContent;
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
            if (getAttrKind() != AspectKind.INT) {
                throw new FormatException("Target node of %s-edge should be int-node", edge.label(),
                    this);
            }
        } else if (edge.isOperator()) {
            Operator operator = edge.getOperator();
            Aspect operType = Aspect.getAspect(operator.getResultType().getName());
            AspectKind operKind = operType.getKind();
            if (!hasAttrAspect()) {
                throw new FormatException("Target node of %s-edge should be %s-attribute",
                    edge.label(), operKind, this);
            } else if (getAttrKind() != operKind) {
                throw new FormatException(
                    "Inferred type %s of %s-target conflicts with declared type %s", operKind,
                    edge.label(), getAttrKind(), this);
            }
        }
    }

    /** Attempts to set the aspect type of this node to a given data type. */
    private void setDataType(Sort type) throws FormatException {
        assert !isFixed();
        Aspect newType = Aspect.getAspect(type.getName());
        assert newType.getKind().hasSort();
        setAttrAspect(newType);
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
            if (!hasAttrAspect()) {
                setAttrAspect(PRODUCT.getAspect());
            } else if (getAttrKind() != PRODUCT) {
                throw new FormatException("Source node of %s-edge should be product node",
                    edgeLabel, this);
            }
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
                    edge.target().setDataType(paramTypes.get(index));
                }
            }
        } else if (edge.isOperator()) {
            if (!hasAttrAspect()) {
                setAttrAspect(PRODUCT.getAspect());
            } else if (getAttrKind() != PRODUCT) {
                throw new FormatException("Source node of %s-edge should be product node",
                    edgeLabel, this);
            }
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
                        argNode.setDataType(paramType);
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

    /** Sets or specialises the attribute aspect of this node. */
    private void setAttrAspect(Aspect newAttr) throws FormatException {
        AspectKind attrKind = newAttr.getKind();
        assert attrKind == DEFAULT || attrKind.isAttrKind() : String
            .format("Aspect %s is not attribute-related", newAttr);
        // it may be the new attribute is inferred from an incoming edge
        // but then we only change the attribute if the new one is "better"
        if (!hasAttrAspect()) {
            this.attr = newAttr;
        } else if (getAttrKind() != attrKind) {
            throw new FormatException("Conflicting (inferred) types %s and %s", getAttrKind(),
                attrKind, this);
        } else if (!getAttrAspect().hasContent() && newAttr.hasContent()) {
            this.attr = newAttr;
        } else if (getAttrAspect().hasContent() && newAttr.hasContent()) {
            throw new FormatException("Conflicting (inferred) types %s and %s", getAttrKind(),
                attrKind, this);
        }
    }

    /** Returns the attribute aspect of this node, if any. */
    @Override
    public Aspect getAttrAspect() {
        return this.attr;
    }

    /** Indicates if this represents a data attribute. */
    @Override
    public boolean hasAttrAspect() {
        return this.attr != null && this.attr.getKind() != DEFAULT;
    }

    @Override
    public AspectKind getAttrKind() {
        return hasAttrAspect()
            ? getAttrAspect().getKind()
            : DEFAULT;
    }

    /** Indicates if this stands for a data value or expression. */
    public boolean hasValue() {
        assert isParsed();
        var attr = getAttrAspect();
        return attr != null && (attr.getContent() instanceof ConstContent
            || attr.getContent() instanceof ExprContent);
    }

    private Expression createValue() throws FormatException {
        AspectContent content = getAttrAspect().getContent();
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

    /** The attribute-related aspect. */
    private Aspect attr;

    private Expression value;

    /** Returns the line describing the value of this node, if any. */
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
    private void setPar(Aspect parAspect) {
        assert parAspect.getKind() == DEFAULT || parAspect.getKind().isParam() : String
            .format("Aspect %s is not a parameter", parAspect);
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

    /** Sets the identifier aspect from a string representation. */
    private void setId(String id) throws FormatException {
        Aspect idAspect = AspectKind.ID.getAspect().newInstance(id, GraphRole.RULE);
        setId(idAspect);
    }

    /** Sets the identifier aspect of this node. */
    private void setId(Aspect idAspect) throws FormatException {
        assert idAspect.getKind() == ID : String.format("Aspect %s is not an identifier", idAspect);
        if (this.idAspect != null) {
            throw new FormatException("Duplicate node identifiers %s and %s",
                this.idAspect.getContent(), idAspect.getContent(), this);
        }
        this.idAspect = idAspect;
        this.id = ((IdContent) idAspect.getContent()).get();
    }

    /** Returns the identifier aspect of this node, if any. */
    public Aspect getIdAspect() {
        return this.idAspect;
    }

    /** Returns the identifier of this node, if any. */
    public String getId() {
        return this.id;
    }

    /** Indicates if this node has an identifier. */
    public boolean hasId() {
        return this.idAspect != null;
    }

    /** The identifier aspect of this node, if any. */
    private Aspect idAspect;

    /** The actual identifier of this node, if any. */
    private String id;

    /** Sets the colour aspect of this node. */
    private void setColor(Aspect color) throws FormatException {
        assert color.getKind() == COLOR : String.format("Aspect %s is not a color", color);
        if (this.colorAspect != null) {
            throw new FormatException("Duplicate colour specification");
        }
        this.colorAspect = color;
        this.color = ((ColorContent) color.getContent()).get();
    }

    /** Returns the colour aspect of this node, if any. */
    public Aspect getColorAspect() {
        return this.colorAspect;
    }

    /** Returns the colour of this node, if the colour aspect has been set. */
    public Color getColor() {
        return this.color;
    }

    /** Indicates if this node has an colour. */
    public boolean hasColor() {
        return this.colorAspect != null;
    }

    /** The colour aspect of this node, if any. */
    private Aspect colorAspect;

    /** The actual colour of this node, if any. */
    private Color color;

    /** Sets the colour aspect of this node. */
    private void setImport(Aspect imported) throws FormatException {
        assert imported.getKind() == IMPORT : String.format("Aspect %s is not an import", imported);
        if (this.imported != null) {
            throw new FormatException("Duplicate import specification");
        }
        this.imported = imported;
    }

    /** Returns the import aspect of this node, if any. */
    public Aspect getImport() {
        return this.imported;
    }

    /** Indicates if this node has an import aspect. */
    public boolean hasImport() {
        return this.imported != null;
    }

    /** The import aspect of this node, if any. */
    private Aspect imported;

    /** Indicates if this is a nodified edge. */
    public boolean isEdge() {
        return getEdge() != null;
    }

    /** Sets the edge aspect of this node. */
    private void setEdge(Aspect edge) throws FormatException {
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

    /** Changes the (aspect) type of this node. */
    void setAspect(Aspect type) throws FormatException {
        assert !type.getKind().isAttrKind() && !type.getKind().isParam() : String
            .format("Aspect %s is not a valid node type", type);
        if (this.aspect == null) {
            this.aspect = type;
        } else if (!this.aspect.equals(type)) {
            throw new FormatException("Conflicting aspects %s and %s", this.aspect, type, this);
        }
    }

    @Override
    public Aspect getAspect() {
        return this.aspect;
    }

    @Override
    public boolean hasAspect() {
        return getAspect() != null;
    }

    /**
     * Returns the determining aspect kind of this node.
     * This is one of {@link AspectKind#REMARK}, a role, a quantifier,
     * or {@link AspectKind#ABSTRACT}.
     */
    @Override
    public AspectKind getKind() {
        return hasAspect()
            ? getAspect().getKind()
            : DEFAULT;
    }

    /** The type of the aspect node. */
    private Aspect aspect;

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

    /** The aspect graph to which this element belongs. */
    private final AspectGraph graph;
    /** The list of aspect labels defining node aspects. */
    private final List<AspectLabel> nodeLabels = new ArrayList<>();
    /** A list of argument types, if this represents a product node. */
    private List<AspectNode> argNodes;
    /** The operator of an outgoing operator edge. */
    private AspectEdge operatorEdge;
    /** List of syntax errors in this node. */
    private final FormatErrorSet errors = new FormatErrorSet();
}
