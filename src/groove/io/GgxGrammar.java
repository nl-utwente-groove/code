// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: GgxGrammar.java,v 1.7 2007-04-18 08:36:21 rensink Exp $
 */
package groove.io;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.trans.DefaultNAC;
import groove.trans.GraphGrammar;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.RuleFactory;
import groove.trans.SystemProperties;
import groove.trans.StructuredRuleName;
import groove.util.FormatException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

/**
 * Class to convert graphs to GGX format and back.
 * GGX is the "proprietary" AGG format.
 * @deprecated experimental, not supported
 * @author Arend Rensink
 * @version $Revision: 1.7 $
 */
@Deprecated
public class GgxGrammar implements XmlGrammar {
    // DOM required definitions; current values are dummies
	/** */
    static public final String NAMESPACE = "GGX namespace";
    /** */
    static public final String PUBLIC_ID = "GGX public ID";
    /** */
    static public final String SYSTEM_ID = "GGX system ID";

    /** The name of the XML format */
    static public final String FORMAT_NAME = "ggx";
    /** Tag for graphs */
    static public final String GRAPH_TAG = "Graph";
    /** Tag for graph transformation systems */
    static public final String GTS_TAG = "GraphTransformationSystem";
    /** Tag for the collection of types */
    static public final String TYPES_TAG = "Types";
    /** Tag for a single type */
    static public final String TYPE_TAG = "Type";
    /** Tag for nodes */
    static public final String NODE_TAG = "Node";
    /** Tag for edges */
    static public final String EDGE_TAG = "Edge";
    /** Tag for rules */
    static public final String RULE_TAG = "Rule";
    /** Tag for morphisms */
    static public final String MORPHISM_TAG = "Morphism";
    /** Tag for mapping pais within morphisms */
    static public final String MAPPING_TAG = "Mapping";
    /** Tag for the application condition section */
    static public final String APPL_CONDITION_TAG = "ApplCondition";
    /** Tag for individual negative application conditions */
    static public final String NAC_TAG = "NAC";

    /** Attribute name for node and edge ids */
    static public final String ID_ATTR_NAME = "ID";
    /** Attribute name for edge sources */
    static public final String SOURCE_ATTR_NAME = "source";
    /** Attribute name for edge targets */
    static public final String TARGET_ATTR_NAME = "target";
    /** Attribute name for graph and morphism names */
    static public final String NAME_ATTR_NAME = "name";
    /** Attribute name for node and edge types */
    static public final String TYPE_ATTR_NAME = "type";
    /** Attribute name for mapping keys */
    static public final String KEY_ATTR_NAME = "orig";
    /** Attribute name for mapping images */
    static public final String IMAGE_ATTR_NAME = "image";

    /** The name of the lhs graph in a rule */
    static public final String LHS_GRAPH_NAME = "Left.graph";
    /** The name of the rhs graph in a rule */
    static public final String RHS_GRAPH_NAME = "Right.graph";
    /** The name of the start graph in a rule */
    static public final String START_GRAPH_NAME = "Graph";

    /**   */
    static private GraphFactory defaultGraphFactory = GraphFactory.getInstance();
    /**
     * Returns a default graph factory for the xonstruction of graphs
     * during unmarshalling.
     * @return the default graph factory
     * @see #unmarshal(Document)
     */
    static public GraphFactory getDefaultGraphFactory() {
        return defaultGraphFactory;
    }

    /**
     * Constructs a GgxGrammar transformer with a given graph factory
     * for the graphs constructed by unmarshalling.
     * @param graphFactory 
     * @throws XmlRuntimeException if setting up the document builder 
     * fails for some internal reason
     * @see #unmarshal(Document)
     */
    public GgxGrammar(GraphFactory graphFactory) throws XmlRuntimeException {
        this.graphFactory = graphFactory;
    }

    /**
     * Constructs a GgxGrammar transformer with a default graph factory.
     * @throws XmlRuntimeException if setting up the document builder 
     * fails for some internal reason
     * @see #getDefaultGraphFactory()
     */
    public GgxGrammar() throws XmlRuntimeException {
        this(getDefaultGraphFactory());
    }

    public ExtensionFilter getExtensionFilter() {
        return new ExtensionFilter("GGX files", ".ggx") {
        	@Override
            public boolean accept(File file) {
                if (!file.isDirectory()) {
                    return super.accept(file);
                } else {
                    return !new GpsGrammar().getExtensionFilter().accept(file);
                }
            }
        };
    }
    
    public void marshalGrammar(GraphGrammar gg, File file) throws FormatException {
        throw new FormatException("");
    }

    public GraphGrammar unmarshalGrammar(File file) throws FormatException {
        return unmarshalGrammar(file, null);
    }

    /**
     * This implementation does not regard the second parameter.
     */
    public GraphGrammar unmarshalGrammar(File file, String startStateName) throws FormatException {
        try {
            Source source = new StreamSource(file);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMResult domResult = new DOMResult();
            transformer.transform(source, domResult);
            return unmarshal((org.w3c.dom.Document) domResult.getNode());
        } catch (TransformerException exc) {
            throw new FormatException(exc.getMessage());
        }
    }
    

    /**
     * @param doc
     * @return The constructed graph grammar
     * @throws FormatException 
     */
    protected GraphGrammar unmarshal(Document doc) throws FormatException {
        GraphGrammar gg = new GraphGrammar();
        org.w3c.dom.Element gxl = doc.getDocumentElement();

        org.w3c.dom.NodeList gtss = gxl.getElementsByTagName(GTS_TAG);
        if (gtss.getLength() != 1)
            throw new FormatException("Only one graph transformation system allowed in document");
        // Get the first and only graph element
        org.w3c.dom.Element gts = (org.w3c.dom.Element) gtss.item(0);

        // Get the types
        org.w3c.dom.NodeList typesLists = gts.getElementsByTagName(TYPES_TAG);
        if (typesLists.getLength() != 1)
            throw new FormatException("Only one types list allowed in a graph transformation system");
        // Get the first and only types list
        org.w3c.dom.Element typesList = (org.w3c.dom.Element) typesLists.item(0);
        org.w3c.dom.NodeList types = typesList.getElementsByTagName(TYPE_TAG);
        // create the type id-to-name map
        Map<String,String> typeMap = new HashMap<String,String>();
        for (int i = 0; i < types.getLength(); i++) {
            org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) types.item(i);
            String typeId = nodeElement.getAttribute(ID_ATTR_NAME);
            if (typeId == null)
                throw new FormatException("Type id not specified");
            String typeName = nodeElement.getAttribute(NAME_ATTR_NAME);
            if (typeName == null)
                throw new FormatException("Type name not specified");
            typeMap.put(typeId, typeName);
        }

        // Get the initial graph
        org.w3c.dom.NodeList graphs = gts.getElementsByTagName(GRAPH_TAG);
        Graph startGraph = null;
        for (int i = 0; i < graphs.getLength(); i++) {
            org.w3c.dom.Element graphElement = (org.w3c.dom.Element) graphs.item(i);
            Graph graph = getGraph(graphElement, typeMap);
            String graphName = graphElement.getAttribute(NAME_ATTR_NAME);
            if (graphName.equals(START_GRAPH_NAME)) {
                startGraph = graph;
            }
        }
        if (startGraph == null) {
            throw new FormatException("No start graph in the rule");
        }
        gg.setStartGraph(startGraph);

        try {
			// Get the rules
			org.w3c.dom.NodeList rules = gts.getElementsByTagName(RULE_TAG);
			for (int i = 0; i < rules.getLength(); i++) {
			    org.w3c.dom.Element ruleElement = (org.w3c.dom.Element) rules.item(i);
			    Rule rule = getRule(ruleElement, typeMap);
			    gg.add(rule);
			}
			// done
			return gg;
		} catch (FormatException exc) {
			throw new FormatException(String.format("Format error in rules: %s", exc.getMessage()));
		}
    }

    /**
     * Exctracs a rule from an XML rule element.
     * @param ruleElement
     * @param typeMap
     * @return The rule extracted from <code>ruleElement</code>
     * @throws FormatException
     */
    protected Rule getRule(org.w3c.dom.Element ruleElement, Map<String,String> typeMap) throws FormatException {
        // Get the lhs and rhs graphs
        org.w3c.dom.NodeList graphList = ruleElement.getElementsByTagName(GRAPH_TAG);
        Graph lhs = null;
        Graph rhs = null;
        Map<Object,Element> lhsElementMap = null;
        Map<Object,Element> rhsElementMap = null;
        for (int i = 0; i < graphList.getLength(); i++) {
            org.w3c.dom.Element graphElement = (org.w3c.dom.Element) graphList.item(i);
            Map<Object,Element> elementMap = new HashMap<Object,Element>();
            Graph graph = getGraph(graphElement, typeMap, elementMap);
            String graphName = graphElement.getAttribute(NAME_ATTR_NAME);
            if (graphName.equals(LHS_GRAPH_NAME)) {
                lhs = graph;
                lhsElementMap = elementMap;
            } else if (graphName.equals(RHS_GRAPH_NAME)) {
                rhs = graph;
                rhsElementMap = elementMap;
            }
        }
        if (lhs == null) {
            throw new FormatException("No " + LHS_GRAPH_NAME + " in the rule");
        } else if (rhs == null) {
            throw new FormatException("No " + RHS_GRAPH_NAME + " in the rule");
        }

        org.w3c.dom.NodeList morphismList = ruleElement.getElementsByTagName(MORPHISM_TAG);
        if (morphismList.getLength() == 0)
            throw new FormatException("There must be a morphism in the rule");
        // Get the first 
        org.w3c.dom.Element morphismElement = (org.w3c.dom.Element) morphismList.item(0);

        NameLabel ruleName = new StructuredRuleName(morphismElement.getAttribute(NAME_ATTR_NAME));
        Morphism ruleMorphism = getMorphism(morphismElement, lhs, lhsElementMap, rhs, rhsElementMap);

        Rule result = createRule(ruleMorphism, ruleName);

        // get NACs
        org.w3c.dom.NodeList applConditionList = ruleElement.getElementsByTagName(APPL_CONDITION_TAG);
        if (applConditionList.getLength() != 1)
            throw new FormatException("Wrong number of application condition tags");
        org.w3c.dom.Element applConditionElement = (org.w3c.dom.Element) applConditionList.item(0);
        org.w3c.dom.NodeList nacList = applConditionElement.getElementsByTagName(NAC_TAG);
        // process each NAC
        for (int i = 0; i < nacList.getLength(); i++) {
            org.w3c.dom.Element nacElement = (org.w3c.dom.Element) nacList.item(i);
            // construct the target graph
            org.w3c.dom.NodeList nacGraphList = nacElement.getElementsByTagName(GRAPH_TAG);
            if (nacGraphList.getLength() != 1)
                throw new FormatException("NAC should contain exactly 1 target graph");
            org.w3c.dom.Element nacGraphElement = (org.w3c.dom.Element) nacGraphList.item(0);
            Map<Object,Element> nacElementMap = new HashMap<Object,Element>();
            Graph nacTarget = getGraph(nacGraphElement, typeMap, nacElementMap);
            // construct the NAC morphism
            org.w3c.dom.NodeList nacMorphismList = nacElement.getElementsByTagName(MORPHISM_TAG);
            if (nacMorphismList.getLength() != 1)
                throw new FormatException("NAC should contain exactly 1 morphism");
            org.w3c.dom.Element nacMorphismElement = (org.w3c.dom.Element) nacMorphismList.item(0);
            Morphism nacMorphism = getMorphism(nacMorphismElement, lhs, lhsElementMap, nacTarget, nacElementMap);
            // Construct and add the NAC
            result.setAndNot(new DefaultNAC(nacMorphism, SystemProperties.getInstance(true)));
        }

        return result;
    }

    /**
     * @param graphElement
     * @param lhs
     * @param lhsElementMap
     * @param rhs
     * @param rhsElementMap
     * @return The constructed injective morphism
     * @throws FormatException
     */
    protected Morphism getMorphism(
        org.w3c.dom.Element graphElement,
        Graph lhs,
        Map<Object,Element> lhsElementMap,
        Graph rhs,
        Map<Object,Element> rhsElementMap)
        throws FormatException {
        Morphism prototype = getGraphFactory().newMorphism(lhs, rhs);
        return getMorphism(graphElement, lhs, lhsElementMap, rhs, rhsElementMap, prototype);
    }

    /**
     * @param morphismElement
     * @param lhs
     * @param lhsElementMap
     * @param rhs
     * @param rhsElementMap
     * @param prototypeMorphism
     * @return The constructed morphism.
     * @throws FormatException
     */
    protected Morphism getMorphism(
        org.w3c.dom.Element morphismElement,
        Graph lhs,
        Map<Object,Element> lhsElementMap,
        Graph rhs,
        Map<Object,Element> rhsElementMap,
        Morphism prototypeMorphism)
        throws FormatException {
        Morphism result = prototypeMorphism.createMorphism(lhs, rhs);
        // Get the node tags
        org.w3c.dom.NodeList mappingList = morphismElement.getElementsByTagName(MAPPING_TAG);
        for (int i = 0; i < mappingList.getLength(); i++) {
            org.w3c.dom.Element mappingElement = (org.w3c.dom.Element) mappingList.item(i);
            // Fetch mapping key
            String keyId = mappingElement.getAttribute(KEY_ATTR_NAME);
            if (keyId == null) {
                throw new FormatException("Mapping key not specified");
            }
            Element key = lhsElementMap.get(keyId);
            if (key == null) {
                throw new FormatException("Unknown mapping key " + keyId);
            }
            // Fetch mapping image
            String imageId = mappingElement.getAttribute(IMAGE_ATTR_NAME);
            if (imageId == null) {
                throw new FormatException("Mapping image not specified");
            }
            Element image = rhsElementMap.get(imageId);
            if (image == null) {
                throw new FormatException("Unknown mapping image " + imageId);
            }
            if (key instanceof Node) {
                assert image instanceof Node;
                result.putNode((Node) key, (Node) image);
                result.putEdge((Edge) lhsElementMap.get(key), (Edge) rhsElementMap.get(image));
            } else {
                assert key instanceof Edge && image instanceof Edge;
                result.putEdge((Edge) key, (Edge) image);
            }
        }
        return result;
    }
    /**
     * Constructs a graph from an XML graph element
     * @param graphElement
     * @param typeMap 
     * @return A graph constructed from <code>graphElement</code>
     * @throws FormatException
     */
    protected Graph getGraph(org.w3c.dom.Element graphElement, Map<String,String> typeMap) throws FormatException {
        Map<Object,Element> dummyElementMap = new HashMap<Object,Element>();
        return getGraph(graphElement, typeMap, dummyElementMap);
    }

    
    /**
     * Constructs a graph from an XML graph element
     * @param graphElement The root graph element
     * @param typeMap 
     * @param elementMap Map associating Nodes with node ids, edges with edge ids, and self-edges with nodes
     * @return A graph constructed from <code>graphElement</code>
     * @throws FormatException 
     */
    protected Graph getGraph(org.w3c.dom.Element graphElement, Map<String,String> typeMap, Map<Object,Element> elementMap) throws FormatException {
        Graph result = getGraphFactory().newGraph();
        // Get the node tags
        org.w3c.dom.NodeList nodeList = graphElement.getElementsByTagName(NODE_TAG);
        for (int i = 0; i < nodeList.getLength(); i++) {
            org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) nodeList.item(i);
            if (UNMARSHAL_DEBUG)
                System.out.println("Parsing XML node " + nodeElement);
            // Create graph Node
            // Fetch Node ID
            String nodeId = nodeElement.getAttribute(ID_ATTR_NAME);
            if (nodeId == null)
                throw new FormatException("Node id not specified");
            // Add node to nodemap and graph
            Node node = (Node) elementMap.get(nodeId);
            if (node == null) {
                // Create groove.graph.Node with label
                node = result.addNode();
                // Add ID, groove.graph.Node pair to Map
                elementMap.put(nodeId, node);
            } else {
                throw new FormatException("Node id " + nodeId + " occurs more than once");
            }
            if (UNMARSHAL_DEBUG)
                System.out.println("Added " + node);
            // Fetch Label
            String typeId = nodeElement.getAttribute(TYPE_ATTR_NAME);
            if (typeId == null)
                throw new FormatException("Unspecified edge type in document");
            String labelText = typeMap.get(typeId);
            Edge selfEdge = result.addEdge(node, DefaultLabel.createLabel(labelText), node);
            elementMap.put(node, selfEdge);
        }
        // Get the edge tags
        org.w3c.dom.NodeList edgeList = graphElement.getElementsByTagName(EDGE_TAG);
        for (int i = 0; i < edgeList.getLength(); i++) {
            org.w3c.dom.Element edgeElement = (org.w3c.dom.Element) edgeList.item(i);

            if (UNMARSHAL_DEBUG)
                System.out.println("Parsing XML node " + edgeElement);
            // Create graph Edge
            // Fetch Source node
            String sourceId = edgeElement.getAttribute(SOURCE_ATTR_NAME);
            if (sourceId == null)
                throw new FormatException("Unspecified edge source in document");
            Node sourceNode = (Node) elementMap.get(sourceId);
            if (sourceNode == null)
                throw new FormatException("Unknown edge source " + sourceId + " in document");
            // Fetch target node
            String targetId = edgeElement.getAttribute(TARGET_ATTR_NAME);
            if (targetId == null)
                throw new FormatException("Unspecified edge target in document");
            Node targetNode = (Node) elementMap.get(targetId);
            if (targetNode == null)
                throw new FormatException("Unknown edge target " + targetId + "in document");
            // Fetch Label
            String typeId = edgeElement.getAttribute(TYPE_ATTR_NAME);
            if (typeId == null)
                throw new FormatException("Unspecified edge type in document");
            String labelText = typeMap.get(typeId);
            // decompose labelText and add new graph edges
            Edge edge = result.addEdge(sourceNode, DefaultLabel.createLabel(labelText), targetNode);
            // Fetch edge ID
            String edgeId = edgeElement.getAttribute(ID_ATTR_NAME);
            if (edgeId == null)
                throw new FormatException("Edge id not specified");
            // Add edge to element map
            elementMap.put(edgeId, edge);
            if (UNMARSHAL_DEBUG)
                System.out.println("Added " + edge);
        }
        result.setFixed();
        return result;
    }

    /**
     * @param ruleMorph
     * @param name
     * @return the rule
     */
    protected Rule createRule(Morphism ruleMorph, NameLabel name) throws FormatException {
        return getRuleFactory().createRule(ruleMorph, name, 0, SystemProperties.DEFAULT_PROPERTIES);
    }
    
    /**
     * Implementation of <tt>AbstractXml</tt>'s abstract method.
     * @return Returns the graph factory set by the constructor.
     * @see #GgxGrammar(GraphFactory)
     */
    protected GraphFactory getGraphFactory() {
        return graphFactory;
    }

    /* (non-Javadoc)
     * @see groove.io.XmlGrammar#getRuleFactory()
     */
    public RuleFactory getRuleFactory() {
    	return ruleFactory;
    }

    /** */
    protected final GraphFactory graphFactory;

    /** */
    private RuleFactory ruleFactory; // = SPORuleFactory.getInstance();
    
    // --------------------------- debug switches --------------------------------

    /** */
    static private final boolean UNMARSHAL_DEBUG = false;
}
