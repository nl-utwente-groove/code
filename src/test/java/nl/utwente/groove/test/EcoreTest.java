/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package nl.utwente.groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.io.FileType;
import nl.utwente.groove.io.external.Imported;
import nl.utwente.groove.io.external.format.ecore.EcoreOptions.Ordering;
import nl.utwente.groove.io.external.format.ecore.EcorePorter;
import nl.utwente.groove.io.external.format.ecore.EcoreToGraphs;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Tests the import of Ecore meta-models and XMI instance models.
 * @author Arend Rensink
 */
@SuppressWarnings("javadoc")
public class EcoreTest {
    /** Directory of the Ecore test fixtures. */
    static private final String DIR = "junit/ecore/";

    // ----------------------------------------------------------------------
    // Meta-model import
    // ----------------------------------------------------------------------

    /** Tests that the shop meta-model is encoded as the expected type graph. */
    @Test
    public void testMetamodel() throws Exception {
        AspectGraph type = single(importFrom("shop.ecore", Ordering.NONE, true), ResourceKind.TYPE);
        assertEquals("shop", type.getName());
        Map<String,Set<String>> expected = new LinkedHashMap<>();
        expected.put("Shop", labels("type:Shop", "string:name", "bool:open"));
        expected.put("shop$Item", labels("type:shop$Item", "abs:", "int:code", "real:price"));
        expected.put("Book", labels("type:Book", "string:isbn"));
        expected.put("Customer", labels("type:Customer", "string:name"));
        expected.put("Category", labels("type:Category", "abs:"));
        expected.put("Category$UNKNOWN", labels("type:Category$UNKNOWN"));
        expected.put("Category$FICTION", labels("type:Category$FICTION"));
        expected.put("Category$NONFICTION", labels("type:Category$NONFICTION"));
        expected.put("catalog$Item", labels("type:catalog$Item", "string:label"));
        assertEquals(expected, selfLabels(type));
        Set<String> expectedEdges = new TreeSet<>();
        expectedEdges.add("Shop -out=1..*:part:items-> shop$Item");
        expectedEdges.add("Shop -part:customers-> Customer");
        expectedEdges.add("shop$Item -out=1:shop-> Shop");
        expectedEdges.add("Book -sub:-> shop$Item");
        expectedEdges.add("Book -out=0..1:category-> Category");
        expectedEdges.add("Book -out=0..1:entry-> catalog$Item");
        expectedEdges.add("Customer -favourites-> shop$Item");
        expectedEdges.add("Category$UNKNOWN -sub:-> Category");
        expectedEdges.add("Category$FICTION -sub:-> Category");
        expectedEdges.add("Category$NONFICTION -sub:-> Category");
        assertEquals(expectedEdges, binaryEdges(type));
        // the approximations the encoding makes are silent
        assertEquals(Collections.emptyList(), messages(type.getErrors()));
    }

    /** Tests the round-trip metadata recorded on the imported type graph. */
    @Test
    public void testMetadata() throws Exception {
        AspectGraph type = single(importFrom("shop.ecore", Ordering.NONE, true), ResourceKind.TYPE);
        var properties = GraphInfo.getProperties(type);
        assertEquals("shop|http://groove.utwente.nl/ecore/shop|shop;"
            + "shop.catalog|http://groove.utwente.nl/ecore/shop/catalog|catalog",
                     properties.getProperty(EcoreToGraphs.PACKAGES_KEY));
        assertEquals("Shop|shop|Shop|class;shop$Item|shop|Item|class;Book|shop|Book|class;"
            + "Customer|shop|Customer|class;Category|shop|Category|enum;"
            + "Category$UNKNOWN|shop|UNKNOWN|literal;Category$FICTION|shop|FICTION|literal;"
            + "Category$NONFICTION|shop|NONFICTION|literal;Isbn|shop|Isbn|datatype;"
            + "catalog$Item|shop.catalog|Item|class",
                     properties.getProperty(EcoreToGraphs.TYPES_KEY));
        assertEquals("Shop.items|shop$Item.shop",
                     properties.getProperty(EcoreToGraphs.OPPOSITES_KEY));
        // only the features that the type graph does not determine completely:
        // the many-valued ones (whose order and uniqueness are not encoded) and
        // the ones over a data type other than the sort's default
        assertEquals("Shop|customers||false|true;Shop|items||false|true;Book|isbn|Isbn|true|true;"
            + "Customer|favourites||false|true",
                     properties.getProperty(EcoreToGraphs.FEATURES_KEY));
    }

    /**
     * Tests that the imported type graph is a valid GROOVE type graph, and that
     * the imported host graph type-checks against it. The graphs are used as
     * imported: since the encoding's approximations are silent, an import of
     * well-formed input carries no format errors at all.
     */
    @Test
    public void testCompilation() throws Exception {
        Set<Imported> imported = importFrom("shop.xmi", Ordering.NONE, true);
        AspectGraph type = single(imported, ResourceKind.TYPE);
        AspectGraph host = single(imported, ResourceKind.HOST).rename(QualName.name("start"));
        GrammarModel grammar = newGrammar(type, host);
        assertEquals(Collections.emptyList(), messages(grammar.getTypeModel().getErrors()));
        var hostModel = grammar.getHostModel(QualName.name("start"));
        assertNotNull(hostModel);
        assertEquals(Collections.emptyList(), messages(hostModel.getErrors()));
        assertEquals(Collections.emptyList(), messages(grammar.getErrors()));
    }

    // ----------------------------------------------------------------------
    // Instance import
    // ----------------------------------------------------------------------

    /** Tests that the shop instance model is encoded as the expected host graph,
     * and that it comes with its regenerated type graph. */
    @Test
    public void testInstance() throws Exception {
        Set<Imported> imported = importFrom("shop.xmi", Ordering.NONE, true);
        assertEquals(2, imported.size());
        assertEquals("shop", single(imported, ResourceKind.TYPE).getName());
        AspectGraph host = single(imported, ResourceKind.HOST);
        assertEquals("shop", host.getName());
        Map<String,Set<String>> expected = new LinkedHashMap<>();
        expected
            .put("theShop",
                 labels("type:Shop", "id:theShop", "let:name=\"Books&Co\"", "let:open=true"));
        expected
            .put("book1", labels("type:Book", "id:book1", "let:code=1", "let:price=9.99",
                                 "let:isbn=\"978-1\""));
        expected.put("book2", labels("type:Book", "id:book2", "let:code=2", "let:price=19.5"));
        expected.put("alice", labels("type:Customer", "id:alice", "let:name=\"Alice\""));
        expected.put("bob", labels("type:Customer", "id:bob", "let:name=\"Bob\""));
        expected.put("Category$FICTION", labels("type:Category$FICTION"));
        expected.put("Category$NONFICTION", labels("type:Category$NONFICTION"));
        assertEquals(expected, selfLabels(host));
        Set<String> expectedEdges = new TreeSet<>();
        expectedEdges.add("theShop -items-> book1");
        expectedEdges.add("theShop -items-> book2");
        expectedEdges.add("theShop -customers-> alice");
        expectedEdges.add("theShop -customers-> bob");
        expectedEdges.add("book1 -shop-> theShop");
        expectedEdges.add("book2 -shop-> theShop");
        expectedEdges.add("book1 -category-> Category$FICTION");
        expectedEdges.add("book2 -category-> Category$NONFICTION");
        expectedEdges.add("alice -favourites-> book1");
        expectedEdges.add("alice -favourites-> book2");
        expectedEdges.add("bob -favourites-> book2");
        assertEquals(expectedEdges, binaryEdges(host));
        assertEquals(Collections.emptyList(), messages(host.getErrors()));
    }

    /** Tests the effect of the {@code ecoreUseIdentifiers} option. */
    @Test
    public void testUseIdentifiers() throws Exception {
        AspectGraph with = single(importFrom("shop.xmi", Ordering.NONE, true), ResourceKind.HOST);
        assertEquals(Set.of("theShop", "book1", "book2", "alice", "bob"), identifiers(with));
        AspectGraph without
            = single(importFrom("shop.xmi", Ordering.NONE, false), ResourceKind.HOST);
        assertEquals(Collections.emptySet(), identifiers(without));
        // apart from the identifiers, the graph is unchanged
        assertEquals(with.nodeCount(), without.nodeCount());
        assertEquals(with.edgeCount(), without.edgeCount());
    }

    // ----------------------------------------------------------------------
    // Ordering
    // ----------------------------------------------------------------------

    /**
     * Tests that an ordered many-valued feature becomes a plain edge in
     * {@code none} mode, without complaint: dropping the order is documented
     * behaviour of that mode, not an error.
     */
    @Test
    public void testOrderingNone() throws Exception {
        AspectGraph type
            = single(importFrom("ordered.ecore", Ordering.NONE, true), ResourceKind.TYPE);
        assertEquals(Set.of("List -part:elements-> Element"), binaryEdges(type));
        assertEquals(Collections.emptyList(), messages(type.getErrors()));
    }

    /** Tests the intermediate encoding of an ordered feature in {@code index} mode. */
    @Test
    public void testOrderingIndex() throws Exception {
        Set<Imported> imported = importFrom("ordered.xmi", Ordering.INDEX, true);
        AspectGraph type = single(imported, ResourceKind.TYPE);
        Map<String,Set<String>> expectedTypes = new LinkedHashMap<>();
        expectedTypes.put("List", labels("type:List"));
        expectedTypes.put("Element", labels("type:Element", "string:name"));
        expectedTypes
            .put("List$elements",
                 labels("type:List$elements", "edge:\"elements\"", "int:index"));
        assertEquals(expectedTypes, selfLabels(type));
        assertEquals(Set.of("List -in=1:elements-> List$elements",
                            "List$elements -out=1:part:val-> Element"),
                     binaryEdges(type));
        assertEquals(Collections.emptyList(), messages(type.getErrors()));
        AspectGraph host = single(imported, ResourceKind.HOST);
        Map<String,Set<String>> expectedHost = new LinkedHashMap<>();
        expectedHost.put("theList", labels("type:List", "id:theList"));
        expectedHost.put("first", labels("type:Element", "id:first", "let:name=\"a\""));
        expectedHost.put("second", labels("type:Element", "id:second", "let:name=\"b\""));
        expectedHost.put("third", labels("type:Element", "id:third", "let:name=\"c\""));
        for (int i = 1; i <= 3; i++) {
            expectedHost
                .put("List$elements#" + i, labels("type:List$elements", "let:index=" + i));
        }
        assertEquals(expectedHost, selfLabels(host));
        assertEquals(Set.of("theList -elements-> List$elements#1",
                            "theList -elements-> List$elements#2",
                            "theList -elements-> List$elements#3",
                            "List$elements#1 -val-> first", "List$elements#2 -val-> second",
                            "List$elements#3 -val-> third"),
                     binaryEdges(host));
        // the indexed encoding is a valid type graph as well
        GrammarModel grammar
            = newGrammar(type, host.rename(QualName.name("start")));
        assertEquals(Collections.emptyList(), messages(grammar.getTypeModel().getErrors()));
        assertEquals(Collections.emptyList(), messages(grammar.getErrors()));
    }

    // ----------------------------------------------------------------------
    // Helper methods
    // ----------------------------------------------------------------------

    /** Imports a fixture file with given encoding options. */
    static private Set<Imported> importFrom(String fixture, Ordering ordering,
                                            boolean useIds) throws Exception {
        File file = new File(DIR + fixture);
        FileType fileType = FileType.getType(file);
        assertNotNull(fileType);
        return EcorePorter.instance().doImport(file, fileType, newGrammar(ordering, useIds));
    }

    /** Returns the unique imported resource of a given kind. */
    static private AspectGraph single(Set<Imported> imported, ResourceKind kind) {
        AspectGraph result = null;
        for (var res : imported) {
            if (res.kind() == kind) {
                assertEquals(null, result);
                result = res.graph();
            }
        }
        assertNotNull(result);
        return result;
    }

    /** Returns the map from node keys to the labels on the node itself. */
    static private Map<String,Set<String>> selfLabels(AspectGraph graph) {
        Map<String,Set<String>> result = new LinkedHashMap<>();
        for (var node : graph.nodeSet()) {
            Set<String> labels = new TreeSet<>();
            node.getPlainLabels().forEach(l -> labels.add(l.text()));
            for (var edge : graph.outEdgeSet(node)) {
                if (edge.source() == edge.target()) {
                    labels.add(edge.label().text());
                }
            }
            result.put(key(graph, node), labels);
        }
        return result;
    }

    /** Returns the set of non-self edges, described in terms of the node keys. */
    static private Set<String> binaryEdges(AspectGraph graph) {
        Set<String> result = new TreeSet<>();
        for (var edge : graph.edgeSet()) {
            if (edge.source() != edge.target()) {
                result
                    .add(key(graph, edge.source()) + " -" + edge.label().text() + "-> "
                        + key(graph, edge.target()));
            }
        }
        return result;
    }

    /** Returns the identifiers declared in a graph. */
    static private Set<String> identifiers(AspectGraph graph) {
        Set<String> result = new LinkedHashSet<>();
        for (var node : graph.nodeSet()) {
            for (var label : node.getPlainLabels()) {
                if (label.text().startsWith("id:")) {
                    result.add(label.text().substring(3));
                }
            }
        }
        return result;
    }

    /**
     * Returns a stable, number-free key for a node: its identifier if it has one,
     * otherwise its type label, optionally suffixed with its index attribute.
     */
    static private String key(AspectGraph graph, AspectNode node) {
        String type = null;
        String id = null;
        String index = null;
        for (var label : node.getPlainLabels()) {
            if (label.text().startsWith("id:")) {
                id = label.text().substring(3);
            }
        }
        for (var edge : graph.outEdgeSet(node)) {
            if (edge.source() != edge.target()) {
                continue;
            }
            String text = edge.label().text();
            if (text.startsWith("type:")) {
                type = text.substring(5);
            } else if (text.startsWith("let:index=")) {
                index = text.substring(10);
            }
        }
        if (id != null) {
            return id;
        }
        return index == null
            ? String.valueOf(type)
            : type + "#" + index;
    }

    /** Returns the messages of a set of format errors, for readable assertions. */
    static private List<String> messages(FormatErrorSet errors) {
        return errors.stream().map(FormatError::toString).toList();
    }

    /** Convenience method to build a set of labels. */
    static private Set<String> labels(String... labels) {
        return new TreeSet<>(List.of(labels));
    }

    /** Creates an empty grammar model with given Ecore encoding options. */
    static private GrammarModel newGrammar(Ordering ordering, boolean useIds) throws Exception {
        GrammarModel result = newGrammar();
        GrammarProperties properties = new GrammarProperties();
        properties.setEcoreOrdering(ordering);
        properties.setEcoreUseIdentifiers(useIds);
        result.setProperties(properties);
        return result;
    }

    /** Creates a grammar model containing a given type graph and start graph. */
    static private GrammarModel newGrammar(AspectGraph type, AspectGraph host) throws Exception {
        SystemStore store = newStore();
        store.putGraphs(ResourceKind.TYPE, List.of(type), false);
        store.putGraphs(ResourceKind.HOST, List.of(host), false);
        GrammarModel result = new GrammarModel(store);
        result.setLocalActiveNames(ResourceKind.TYPE, type.getQualName());
        result.setLocalActiveNames(ResourceKind.HOST, host.getQualName());
        return result;
    }

    /** Creates an empty grammar model backed by a fresh temporary store. */
    static private GrammarModel newGrammar() throws Exception {
        return new GrammarModel(newStore());
    }

    /** Creates a fresh, empty grammar store in a temporary directory. */
    static private SystemStore newStore() throws Exception {
        File dir = Files.createTempDirectory("ecore-test").toFile();
        dir.deleteOnExit();
        File gps = new File(dir, "ecore" + FileType.GRAMMAR.getExtension());
        return SystemStore.newStore(gps, true, true);
    }
}
