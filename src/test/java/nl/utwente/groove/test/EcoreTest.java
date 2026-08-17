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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.Test;

import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.iso.IsoChecker;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.graph.plain.PlainNode;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Imported;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.io.external.format.ecore.EcoreKey;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping.LiteralStyle;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping.Ordering;
import nl.utwente.groove.io.external.format.ecore.EcoreMappingSchema;
import nl.utwente.groove.io.external.format.ecore.EcoreNames;
import nl.utwente.groove.io.external.format.ecore.EcorePorter;
import nl.utwente.groove.io.external.format.ecore.EcoreToGraphs;
import nl.utwente.groove.io.external.format.ecore.GraphsToEcore;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.io.FileType;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the import of Ecore meta-models and XMI instance models.
 * @author Arend Rensink
 */
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
        expected.put("Book", labels("type:Book", "string:isbn", "string:tags"));
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
        // the many-valued ones (whose order, uniqueness and bounds are not
        // encoded), the ones over a data type other than the sort's default, and
        // the ones whose name had to be repaired — none, in this meta-model,
        // which is why every record ends in an empty field
        assertEquals("Shop|customers||false|true|0|-1|;Shop|items||false|true|1|-1|;"
            + "Book|isbn|Isbn|true|true|0|1|;Book|tags||false|true|0|-1|;"
            + "Customer|favourites||false|true|0|-1|",
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
        // the values of the many-valued 'tags' attribute cannot go into a
        // let:-assignment, and become shared constant nodes instead
        expected.put("string:\"fiction\"", labels("string:\"fiction\""));
        expected.put("string:\"classic\"", labels("string:\"classic\""));
        expected.put("Category$FICTION", labels("type:Category$FICTION"));
        expected.put("Category$NONFICTION", labels("type:Category$NONFICTION"));
        assertEquals(expected, selfLabels(host));
        Set<String> expectedEdges = new TreeSet<>();
        expectedEdges.add("book1 -tags-> string:\"fiction\"");
        expectedEdges.add("book1 -tags-> string:\"classic\"");
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

    /**
     * Tests that a data value which GROOVE's algebras cannot represent at all
     * is reported rather than approximated. This is the one place where the
     * instance encoding does raise a format error: unlike the mappings it makes
     * by design, {@code NaN} has no GROOVE counterpart to be lossy about.
     */
    @Test
    public void testUnrepresentableValue() throws Exception {
        AspectGraph host = single(importFrom("broken.xmi", Ordering.NONE, true), ResourceKind.HOST);
        List<String> errors = messages(host.getErrors());
        assertEquals(1, errors.size());
        assertTrue(errors.get(0),
                   errors
                       .get(0)
                       .contains("Value 'NaN' of attribute 'value' has no GROOVE representation"));
        // the value is dropped, not silently substituted by 0.0
        assertEquals(Map.of("m1", labels("type:Measurement", "id:m1")), selfLabels(host));
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

    /**
     * Tests that many-valued features with set semantics keep the direct
     * encoding even in {@code index} mode. Every many-valued feature of the shop
     * meta-model — the {@code items} and {@code customers} containments, the
     * {@code favourites} reference, the {@code tags} attribute — is declared
     * {@code ordered="false"} and is unique, so there is nothing for an
     * intermediate node to preserve and the two modes have to agree.
     */
    @Test
    public void testOrderingSetSemantics() throws Exception {
        AspectGraph none = single(importFrom("shop.xmi", Ordering.NONE, true), ResourceKind.HOST);
        AspectGraph index = single(importFrom("shop.xmi", Ordering.INDEX, true), ResourceKind.HOST);
        assertEquals(selfLabels(none), selfLabels(index));
        assertEquals(binaryEdges(none), binaryEdges(index));
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
            .put("List$elements", labels("type:List$elements", "edge:\"elements\"", "int:index"));
        expectedTypes
            .put("List$labels",
                 labels("type:List$labels", "edge:\"labels\"", "int:index", "string:val"));
        assertEquals(expectedTypes, selfLabels(type));
        assertEquals(Set
            .of("List -in=1:elements-> List$elements", "List$elements -out=1:part:val-> Element",
                "List -in=1:labels-> List$labels"), binaryEdges(type));
        assertEquals(Collections.emptyList(), messages(type.getErrors()));
        AspectGraph host = single(imported, ResourceKind.HOST);
        Map<String,Set<String>> expectedHost = new LinkedHashMap<>();
        expectedHost.put("theList", labels("type:List", "id:theList"));
        expectedHost.put("first", labels("type:Element", "id:first", "let:name=\"a\""));
        expectedHost.put("second", labels("type:Element", "id:second", "let:name=\"b\""));
        expectedHost.put("third", labels("type:Element", "id:third", "let:name=\"c\""));
        for (int i = 1; i <= 3; i++) {
            expectedHost.put("List$elements#" + i, labels("type:List$elements", "let:index=" + i));
            expectedHost.put("List$labels#" + i, labels("type:List$labels", "let:index=" + i));
        }
        expectedHost.put("string:\"x\"", labels("string:\"x\""));
        expectedHost.put("string:\"y\"", labels("string:\"y\""));
        assertEquals(expectedHost, selfLabels(host));
        assertEquals(Set
            .of("theList -elements-> List$elements#1", "theList -elements-> List$elements#2",
                "theList -elements-> List$elements#3", "List$elements#1 -val-> first",
                "List$elements#2 -val-> second", "List$elements#3 -val-> third",
                "theList -labels-> List$labels#1", "theList -labels-> List$labels#2",
                "theList -labels-> List$labels#3",
                // the duplicate value survives: the intermediates differ
                "List$labels#1 -val-> string:\"x\"", "List$labels#2 -val-> string:\"y\"",
                "List$labels#3 -val-> string:\"x\""), binaryEdges(host));
        // the indexed encoding is a valid type graph as well
        GrammarModel grammar = newGrammar(type, host.rename(QualName.name("start")));
        assertEquals(Collections.emptyList(), messages(grammar.getTypeModel().getErrors()));
        assertEquals(Collections.emptyList(), messages(grammar.getErrors()));
    }

    // ----------------------------------------------------------------------
    // Export
    // ----------------------------------------------------------------------

    /**
     * Tests that the shop meta-model survives an export and a re-import: the
     * type graph is isomorphic to the original and carries the same round-trip
     * metadata.
     */
    @Test
    public void testMetamodelExport() throws Exception {
        assertRoundTrip("shop.ecore", Ordering.NONE);
    }

    /**
     * Tests that the shop instance model survives an export and a re-import,
     * identifiers included.
     */
    @Test
    public void testInstanceExport() throws Exception {
        assertRoundTrip("shop.xmi", Ordering.NONE);
    }

    /** Tests that the element order of an indexed feature survives a round trip. */
    @Test
    public void testOrderingExport() throws Exception {
        AspectGraph resultHost
            = single(assertRoundTrip("ordered.xmi", Ordering.INDEX), ResourceKind.HOST);
        // spelled out, since isomorphism alone reads as a weak statement about
        // order — and the duplicate value of the non-unique 'labels' attribute
        // must not be swallowed on the way out either
        assertEquals(Set
            .of("theList -elements-> List$elements#1", "theList -elements-> List$elements#2",
                "theList -elements-> List$elements#3", "List$elements#1 -val-> first",
                "List$elements#2 -val-> second", "List$elements#3 -val-> third",
                "theList -labels-> List$labels#1", "theList -labels-> List$labels#2",
                "theList -labels-> List$labels#3", "List$labels#1 -val-> string:\"x\"",
                "List$labels#2 -val-> string:\"y\"", "List$labels#3 -val-> string:\"x\""),
                     binaryEdges(resultHost));
    }

    /**
     * Tests that all values of a many-valued attribute survive the import and the
     * round trip, and that the recorded metadata is reproduced exactly.
     */
    @Test
    public void testManyValuedAttribute() throws Exception {
        AspectGraph host = single(importFrom("shop.xmi", Ordering.NONE, true), ResourceKind.HOST);
        Set<String> tags = Set.of("string:\"fiction\"", "string:\"classic\"");
        assertEquals(tags, targets(host, "tags"));
        Set<Imported> result = assertRoundTrip("shop.xmi", Ordering.NONE);
        assertEquals(tags, targets(single(result, ResourceKind.HOST), "tags"));
    }

    // ----------------------------------------------------------------------
    // Feature-group examples
    //
    // One fixture pair per group of Ecore features, each round-tripped in the
    // ordering modes it can tell apart, and each with spot assertions on the
    // labels that showcase its group. The fixtures are documented in
    // junit/ecore/README.md; the assertions below are what that documentation
    // is checked against.
    // ----------------------------------------------------------------------

    /** Tests the encoding of the supported EMF data types, and of the data
     * values in an instance of them. */
    @Test
    public void testDatatypes() throws Exception {
        Set<Imported> imported = importFrom("datatypes.xmi", Ordering.NONE, true);
        AspectGraph type = single(imported, ResourceKind.TYPE);
        // every data type maps to the sort of its attribute self-loop;
        // the custom data type 'Colour' and 'EDate' are approximated by strings
        assertEquals(Map
            .of("Values",
                labels("type:Values", "bool:booleanValue", "bool:booleanObject", "int:byteValue",
                       "int:byteObject", "int:shortValue", "int:shortObject", "int:intValue",
                       "int:integerObject", "int:longValue", "int:longObject", "int:bigInteger",
                       "real:floatValue", "real:floatObject", "real:doubleValue",
                       "real:doubleObject", "real:bigDecimal", "string:stringValue",
                       "string:charValue", "string:characterObject", "string:dateValue",
                       "string:customValue", "string:aliases")),
                     selfLabels(type));
        AspectGraph host = single(imported, ResourceKind.HOST);
        Set<String> values = new TreeSet<>(selfLabels(host).get("values"));
        // EMF renders an EDate in the time zone of the machine reading it,
        // so only the shape of that one value can be pinned down here
        Set<String> dates = new TreeSet<>();
        values.stream().filter(l -> l.startsWith("let:dateValue=")).forEach(dates::add);
        assertEquals(1, dates.size());
        assertTrue(dates.toString(),
                   dates.iterator().next().matches("let:dateValue=\"\\d{4}-\\d{2}-\\d{2}T.*\""));
        values.removeAll(dates);
        assertEquals(labels("type:Values", "id:values", "let:booleanValue=true",
                            "let:booleanObject=false", "let:byteValue=-128", "let:byteObject=127",
                            "let:shortValue=-32768", "let:shortObject=32767",
                            "let:intValue=-2147483648", "let:integerObject=2147483647",
                            "let:longValue=-9223372036854775808",
                            "let:longObject=9223372036854775807",
                            "let:bigInteger=170141183460469231731687303715884105727",
                            "let:floatValue=-1.5", "let:floatObject=0.25", "let:doubleValue=-0.5",
                            "let:doubleObject=0.001", "let:bigDecimal=-123.456",
                            // the quote and the backslash are both escaped (grammar 3.12)
                            "let:stringValue=\"He said \\\"hi\\\", path C:\\\\temp\"",
                            // EMF serialises a character as its numeric code
                            "let:charValue=\"65\"", "let:characterObject=\"122\"",
                            "let:customValue=\"#ff8800\""),
                     values);
        // the many-valued attribute goes to shared constant nodes instead
        assertEquals(Set.of("string:\"plain\"", "string:\"quo\\\"ted\""), targets(host, "aliases"));
        assertRoundTrip("datatypes.xmi", Ordering.NONE);
    }

    /** Tests the indexed encoding of the many-valued attribute of the data type
     * example, and its round trip. */
    @Test
    public void testDatatypesIndexed() throws Exception {
        Set<Imported> imported = assertRoundTrip("datatypes.xmi", Ordering.INDEX);
        AspectGraph type = single(imported, ResourceKind.TYPE);
        assertEquals(labels("type:Values$aliases", "edge:\"aliases\"", "int:index", "string:val"),
                     selfLabels(type).get("Values$aliases"));
        assertEquals(Set.of("Values -in=1:aliases-> Values$aliases"), binaryEdges(type));
        AspectGraph host = single(imported, ResourceKind.HOST);
        assertEquals(Set
            .of("values -aliases-> Values$aliases#1", "values -aliases-> Values$aliases#2",
                "Values$aliases#1 -val-> string:\"plain\"",
                "Values$aliases#2 -val-> string:\"quo\\\"ted\""), binaryEdges(host));
    }

    /** Tests the encoding of abstract classes, interfaces, multiple inheritance
     * and enumerations. */
    @Test
    public void testHierarchy() throws Exception {
        Set<Imported> imported = importFrom("hierarchy.xmi", Ordering.NONE, true);
        AspectGraph type = single(imported, ResourceKind.TYPE);
        Map<String,Set<String>> expected = new LinkedHashMap<>();
        // an interface is encoded exactly like an abstract class
        expected.put("Named", labels("type:Named", "abs:", "string:name"));
        expected.put("Trackable", labels("type:Trackable", "abs:"));
        expected.put("Element", labels("type:Element", "abs:", "int:rank"));
        expected.put("Task", labels("type:Task"));
        expected.put("Subtask", labels("type:Subtask", "int:depth"));
        expected.put("Project", labels("type:Project"));
        expected.put("Status", labels("type:Status", "abs:"));
        expected.put("Status$NEW", labels("type:Status$NEW"));
        // the hyphen is not a legal Java identifier character
        expected.put("Status$IN_HYPH_PROGRESS", labels("type:Status$IN_HYPH_PROGRESS"));
        expected.put("Status$DONE", labels("type:Status$DONE"));
        assertEquals(expected, selfLabels(type));
        assertEquals(Set
            .of("Element -sub:-> Named",
                // the two super-types of the multiply inheriting class
                "Task -sub:-> Element", "Task -sub:-> Trackable", "Subtask -sub:-> Task",
                "Project -sub:-> Named", "Project -part:tasks-> Task",
                // an enum-typed attribute is an edge, not a self-loop
                "Trackable -out=0..1:status-> Status", "Status$NEW -sub:-> Status",
                "Status$IN_HYPH_PROGRESS -sub:-> Status", "Status$DONE -sub:-> Status"),
                     binaryEdges(type));
        AspectGraph host = single(imported, ResourceKind.HOST);
        // only the literals used in the instance get a node
        assertEquals(Set.of("Status$IN_HYPH_PROGRESS", "Status$DONE"), targets(host, "status"));
        assertRoundTrip("hierarchy.xmi", Ordering.NONE);
    }

    /** Tests that the containment of the hierarchy example moves to the value
     * edge of the intermediate node under {@code index} ordering. */
    @Test
    public void testHierarchyIndexed() throws Exception {
        AspectGraph type
            = single(assertRoundTrip("hierarchy.xmi", Ordering.INDEX), ResourceKind.TYPE);
        assertEquals(labels("type:Project$tasks", "edge:\"tasks\"", "int:index"),
                     selfLabels(type).get("Project$tasks"));
        assertTrue(binaryEdges(type).toString(),
                   binaryEdges(type).contains("Project -in=1:tasks-> Project$tasks"));
        assertTrue(binaryEdges(type).toString(),
                   binaryEdges(type).contains("Project$tasks -out=1:part:val-> Task"));
    }

    /** Tests the encoding of self-references, opposite pairs and explicitly
     * bounded multiplicities. */
    @Test
    public void testNetwork() throws Exception {
        Set<Imported> imported = importFrom("network.xmi", Ordering.NONE, true);
        AspectGraph type = single(imported, ResourceKind.TYPE);
        // the self-references of a class are loops of its type node
        assertEquals(labels("type:Station", "string:name", "out=0..1:next", "out=0..1:previous",
                            "route"),
                     selfLabels(type).get("Station"));
        assertEquals(Set.of("Network -out=2..4:part:stations-> Station"), binaryEdges(type));
        // the opposite pairing is not structural: it lives in the metadata
        assertEquals("Station.next|Station.previous",
                     GraphInfo.getProperties(type).getProperty(EcoreToGraphs.OPPOSITES_KEY));
        AspectGraph host = single(imported, ResourceKind.HOST);
        // both directions of an opposite pair are present as ordinary edges
        assertEquals(Set.of("north -next-> middle", "middle -next-> south"),
                     edgesWith(host, "next"));
        assertEquals(Set.of("middle -previous-> north", "south -previous-> middle"),
                     edgesWith(host, "previous"));
        assertRoundTrip("network.xmi", Ordering.NONE);
    }

    /** Tests that the order of the cross-reference of the network example
     * survives a round trip in {@code index} mode. */
    @Test
    public void testNetworkIndexed() throws Exception {
        AspectGraph host
            = single(assertRoundTrip("network.xmi", Ordering.INDEX), ResourceKind.HOST);
        assertEquals(Set
            .of("north -route-> Station$route#1", "north -route-> Station$route#2",
                "Station$route#1 -val-> south", "Station$route#2 -val-> middle"),
                     edgesAt(host, "Station$route"));
    }

    /** Tests the qualification of colliding classifier names over nested
     * packages, and the repair of names that are not GROOVE identifiers. */
    @Test
    public void testPackages() throws Exception {
        Set<Imported> imported = importFrom("packages.xmi", Ordering.NONE, true);
        AspectGraph type = single(imported, ResourceKind.TYPE);
        Map<String,Set<String>> expected = new LinkedHashMap<>();
        // the three colliding 'Item' classes are qualified by one package segment
        expected.put("packages$Item", labels("type:packages$Item", "string:name"));
        expected.put("core$Item", labels("type:core$Item", "int:code"));
        // the class name is repaired as a type label, which may contain a
        // hyphen; the feature names as attribute field names, which may not
        expected
            .put("Line_HYPH_Item", labels("type:Line_HYPH_Item", "real:unit_UNKN_price",
                                          "real:unit_price", "string:_self_"));
        expected.put("detail$Item", labels("type:detail$Item", "string:note"));
        assertEquals(expected, selfLabels(type));
        assertEquals(Set
            .of("packages$Item -part:entries-> core$Item", "core$Item -part:details-> detail$Item",
                "Line_HYPH_Item -sub:-> core$Item"), binaryEdges(type));
        var properties = GraphInfo.getProperties(type);
        assertEquals("packages|http://groove.utwente.nl/ecore/packages|packages;"
            + "packages.core|http://groove.utwente.nl/ecore/packages/core|core;"
            + "packages.core.detail|http://groove.utwente.nl/ecore/packages/core/detail|detail",
                     properties.getProperty(EcoreToGraphs.PACKAGES_KEY));
        // the metadata keeps the original names, so an export restores them
        assertEquals("packages$Item|packages|Item|class;core$Item|packages.core|Item|class;"
            + "Line_HYPH_Item|packages.core|Line-Item|class;"
            + "detail$Item|packages.core.detail|Item|class",
                     properties.getProperty(EcoreToGraphs.TYPES_KEY));
        assertRoundTrip("packages.xmi", Ordering.NONE);
    }

    /**
     * Tests that a feature name that the encoding had to repair is recorded and
     * restored. The repair is not reversible by rule — {@code unit-price} and
     * {@code unit.price} both become an identifier, and only one of them keeps
     * enough of its shape to be guessed back — so the name is carried in the
     * metadata, exactly as a classifier name is.
     */
    @Test
    public void testPackagesNames() throws Exception {
        AspectGraph type
            = single(importFrom("packages.xmi", Ordering.NONE, true), ResourceKind.TYPE);
        String expected
            = "packages$Item|entries||false|true|0|-1|;" + "core$Item|details||true|true|0|-1|;"
            // the repaired names, each with the Ecore name it came from
                + "Line_HYPH_Item|_self_||true|true|0|1|self;"
                + "Line_HYPH_Item|unit_UNKN_price||true|true|0|1|unit.price;"
                + "Line_HYPH_Item|unit_price||true|true|0|1|unit-price";
        assertEquals(expected,
                     GraphInfo.getProperties(type).getProperty(EcoreToGraphs.FEATURES_KEY));
        // the re-import can only record 'unit-price' again if the export wrote
        // the attribute back under that name
        AspectGraph result
            = single(assertRoundTrip("packages.xmi", Ordering.NONE), ResourceKind.TYPE);
        assertEquals(expected,
                     GraphInfo.getProperties(result).getProperty(EcoreToGraphs.FEATURES_KEY));
    }

    /** Tests that the intermediate node of an indexed feature is named after the
     * (already qualified) label of its owner. */
    @Test
    public void testPackagesIndexed() throws Exception {
        AspectGraph type
            = single(assertRoundTrip("packages.xmi", Ordering.INDEX), ResourceKind.TYPE);
        assertEquals(labels("type:core$Item$details", "edge:\"details\"", "int:index"),
                     selfLabels(type).get("core$Item$details"));
    }

    // ----------------------------------------------------------------------
    // Mapping settings
    // ----------------------------------------------------------------------

    /** Tests the per-feature ordering override in both directions. */
    @Test
    public void testOrderingOverride() throws Exception {
        // an index override for the elements feature only, under global none
        AspectGraph type = single(
                                  importFrom("ordered.ecore",
                                             mappingText(Ordering.NONE, true,
                                                         "List.elements.ordering = index")),
                                  ResourceKind.TYPE);
        assertEquals(Set.of("List", "Element", "List$elements"), selfLabels(type).keySet());
        assertTrue(selfLabels(type).get("List").contains("string:labels"));
        assertEquals(Collections.emptyList(), messages(type.getErrors()));
        // a none override for the labels feature, under global index;
        // the element path may be package-qualified
        type = single(importFrom("ordered.ecore",
                                 mappingText(Ordering.INDEX, true,
                                             "ordered.List.labels.ordering = none")),
                      ResourceKind.TYPE);
        assertEquals(Set.of("List", "Element", "List$elements"), selfLabels(type).keySet());
        assertTrue(selfLabels(type).get("List").contains("string:labels"));
        assertEquals(Collections.emptyList(), messages(type.getErrors()));
    }

    /** Tests the instance encoding and the round trip under an override. */
    @Test
    public void testOrderingOverrideRoundTrip() throws Exception {
        String mappingText = mappingText(Ordering.NONE, true, "List.elements.ordering = index");
        Set<Imported> imported = assertRoundTrip("ordered.xmi", mappingText);
        AspectGraph host = single(imported, ResourceKind.HOST);
        // the elements containment is nodified, the labels attribute is not
        assertTrue(selfLabels(host).keySet().stream().anyMatch(k -> k.startsWith("List$elements")));
        assertTrue(selfLabels(host).keySet().stream().noneMatch(k -> k.startsWith("List$labels")));
    }

    /** Tests that more than one mapping resource makes the port fail. */
    @Test
    public void testMultipleMappings() throws Exception {
        SystemStore store = newStore();
        store
            .putTexts(ResourceKind.SETTINGS,
                      Map
                          .of(EcoreMapping.RESOURCE_QUAL_NAME, mappingText(Ordering.NONE, true),
                              QualName.parse("ecore.extra"), mappingText(Ordering.INDEX, true)));
        GrammarModel grammar = new GrammarModel(store);
        try {
            EcorePorter.instance().doImport(new File(DIR + "shop.ecore"), FileType.ECORE, grammar);
            fail("Import with multiple mapping resources should not succeed");
        } catch (PortException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Multiple"));
        }
    }

    /** Tests classifier and literal naming overrides. */
    @Test
    public void testNamingOverrides() throws Exception {
        AspectGraph type
            = single(importFrom("shop.ecore",
                                mappingText(Ordering.NONE, true, "Category.typeName = Genre",
                                            "Category.literalStyle = plain",
                                            "Category.UNKNOWN.typeName = Misc")),
                     ResourceKind.TYPE);
        var labels = selfLabels(type).keySet();
        assertTrue(labels.toString(), labels.contains("Genre"));
        assertTrue(labels.toString(), labels.contains("FICTION")); // plain literal style
        assertTrue(labels.toString(), labels.contains("Misc")); // literal override wins
        assertFalse(labels.toString(), labels.contains("Category"));
        assertEquals(Collections.emptyList(), messages(type.getErrors()));
    }

    /** Tests the instance round trip under naming overrides. */
    @Test
    public void testNamingOverrideRoundTrip() throws Exception {
        assertRoundTrip("shop.xmi",
                        mappingText(Ordering.NONE, true, "Category.typeName = Genre",
                                    "Category.literalStyle = plain",
                                    "Category.UNKNOWN.typeName = Misc", "Book.typeName = Boek"));
    }

    /** Tests that colliding naming overrides are an error. */
    @Test
    public void testNamingCollision() throws Exception {
        AspectGraph type = single(
                                  importFrom("shop.ecore",
                                             mappingText(Ordering.NONE, true,
                                                         "Book.typeName = Ware",
                                                         "Category.typeName = Ware")),
                                  ResourceKind.TYPE);
        List<String> errors = messages(type.getErrors());
        assertEquals(errors.toString(), 1, errors.size());
        assertTrue(errors.get(0), errors.get(0).contains("Colliding"));
    }

    /** Tests the reverse application of typeName overrides on a metadata-free export. */
    @Test
    public void testNamingReverseExport() throws Exception {
        PlainGraph plain = new PlainGraph("mini", GraphRole.TYPE);
        PlainNode node = plain.addNode();
        plain.addEdge(node, "type:Genre", node);
        GraphsToEcore converter = new GraphsToEcore(mapping("Category.typeName = Genre"));
        List<EPackage> packages = converter.addTypeGraph(AspectGraph.newInstance(plain));
        assertEquals(Collections.emptyList(), messages(converter.getErrors()));
        assertEquals(1, packages.size());
        assertEquals("Category", packages.get(0).getEClassifiers().get(0).getName());
    }

    /** Tests the leniency and the errors of mapping resolution. */
    @Test
    public void testOrderingResolution() throws Exception {
        // an entry about another metamodel is silently ignored
        AspectGraph type = single(
                                  importFrom("ordered.ecore",
                                             mappingText(Ordering.NONE, true,
                                                         "Shop.orders.ordering = index")),
                                  ResourceKind.TYPE);
        assertEquals(Collections.emptyList(), messages(type.getErrors()));
        // an entry resolving to a single-valued feature is an error
        type = single(importFrom("ordered.ecore",
                                 mappingText(Ordering.NONE, true, "Element.name.ordering = index")),
                      ResourceKind.TYPE);
        List<String> errors = messages(type.getErrors());
        assertEquals(errors.toString(), 1, errors.size());
        assertTrue(errors.get(0), errors.get(0).contains("single-valued"));
    }

    /** Tests that an ambiguously resolving entry is an error, and that
     * package qualification resolves the ambiguity. */
    @Test
    public void testAmbiguousResolution() throws Exception {
        // two packages, each holding a class Thing with a many-valued feature items
        List<EPackage> roots = new ArrayList<>();
        for (String pkgName : List.of("one", "two")) {
            EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
            pkg.setName(pkgName);
            EClass thing = EcoreFactory.eINSTANCE.createEClass();
            thing.setName("Thing");
            EReference items = EcoreFactory.eINSTANCE.createEReference();
            items.setName("items");
            items.setUpperBound(-1);
            items.setEType(thing);
            thing.getEStructuralFeatures().add(items);
            pkg.getEClassifiers().add(thing);
            roots.add(pkg);
        }
        EcoreNames names = new EcoreNames(roots, mapping("Thing.items.ordering = index"));
        List<String> errors = messages(names.getErrors());
        assertEquals(errors.toString(), 1, errors.size());
        assertTrue(errors.get(0), errors.get(0).contains("Ambiguous"));
        names = new EcoreNames(roots, mapping("one.Thing.items.ordering = index"));
        assertEquals(Collections.emptyList(), messages(names.getErrors()));
    }

    /** Tests that a grammar without an {@code ecore} settings resource, and a
     * {@code null} grammar, both yield the default mapping. */
    @Test
    public void testMappingDefaults() throws Exception {
        for (var mapping : List.of(EcoreMapping.of(newGrammar()), EcoreMapping.of(null))) {
            assertEquals(Ordering.NONE, mapping.ordering());
            assertTrue(mapping.useIdentifiers());
            assertTrue(mapping.featureOrdering().isEmpty());
        }
    }

    /**
     * Tests the generated initial text of a new {@code ecore} settings
     * resource: valid, semantically empty (all key lines commented out), with
     * every key form present as an example, and with the global examples
     * showing exactly the default values.
     */
    @Test
    public void testMappingTemplate() throws Exception {
        String text = EcoreMappingSchema.INSTANCE.getNewText();
        EcoreMapping fresh = mapping(text);
        assertEquals(Ordering.NONE, fresh.ordering());
        assertTrue(fresh.useIdentifiers());
        assertTrue(fresh.featureOrdering().isEmpty());
        assertTrue(fresh.typeNames().isEmpty());
        for (EcoreKey key : EcoreKey.values()) {
            assertTrue(key.name(), text.contains("# " + key.templateLine()));
        }
        String globals = Arrays
            .stream(EcoreKey.values())
            .filter(EcoreKey::isGlobal)
            .map(EcoreKey::templateLine)
            .collect(Collectors.joining("\n"));
        EcoreMapping defaults = mapping(globals);
        assertEquals(Ordering.NONE, defaults.ordering());
        assertTrue(defaults.useIdentifiers());
    }

    /** Tests that the schema help map is harvested from the key annotations:
     * one documented syntax line per key form, each with a tool tip. */
    @Test
    public void testMappingDocMap() throws Exception {
        HelpMap docMap = EcoreMappingSchema.INSTANCE.getHelpMap();
        assertEquals(EcoreKey.values().length, docMap.size());
        docMap.forEach((item, tip) -> {
            assertTrue(item, item.startsWith("<html>"));
            assertNotNull(item, tip);
        });
    }

    /** Tests that a mapping resource with a rejected value, and one whose
     * declared schema contradicts its location, both make the port fail. */
    @Test
    public void testBrokenMapping() throws Exception {
        for (String text : List
            .of("$schema = " + EcoreMappingSchema.NAME + "\nordering = sideways\n",
                "$schema = no-such-schema\n")) {
            SystemStore store = newStore();
            store.putTexts(ResourceKind.SETTINGS, Map.of(EcoreMapping.RESOURCE_QUAL_NAME, text));
            GrammarModel grammar = new GrammarModel(store);
            try {
                EcorePorter
                    .instance()
                    .doImport(new File(DIR + "shop.ecore"), FileType.ECORE, grammar);
                fail("Import with broken mapping resource should not succeed");
            } catch (PortException expected) {
                assertTrue(expected.getMessage().contains(EcoreMapping.RESOURCE_NAME));
            }
        }
    }

    /** Tests the parsing of the per-element vocabulary. */
    @Test
    public void testMappingVocabulary() throws Exception {
        EcoreMapping mapping = mapping("""
            pkg.Order.items.ordering = index
            Colour.typeName = Kleur
            Colour.literalStyle = plain
            Colour.RED.typeName = Rood
            """);
        assertEquals(Map.of("pkg.Order.items", Ordering.INDEX), mapping.featureOrdering());
        assertEquals(Map.of("Colour", "Kleur", "Colour.RED", "Rood"), mapping.typeNames());
        assertEquals(Map.of("Colour", LiteralStyle.PLAIN), mapping.literalStyles());
        assertMappingError("items.ordering = index"); // feature key needs a class part
        assertMappingError("Shop.useIdentifiers = true"); // global option with a prefix
        assertMappingError("typeName = X"); // type name without an element path
        assertMappingError("Colour.typeName = not an id"); // invalid GROOVE name
        assertMappingError("Colour.literalStyle = fancy"); // unknown style
        assertMappingError("Colour.colour = red"); // unknown choice key
        assertMappingError("Colour..typeName = X"); // empty path segment
    }

    /** Tests that setting the globals preserves comments and per-element entries. */
    @Test
    public void testMappingSetGlobals() throws Exception {
        // a fresh text parses back to the requested globals
        EcoreMapping fresh = mapping(EcoreMapping.setGlobals(null, Ordering.INDEX, false));
        assertEquals(Ordering.INDEX, fresh.ordering());
        assertTrue(!fresh.useIdentifiers());
        // a targeted edit leaves all other lines untouched
        String old = "# comment\n$schema = " + EcoreMappingSchema.NAME
            + "\nordering = none\nShop.orders.ordering = index\n";
        String edited = EcoreMapping.setGlobals(old, Ordering.INDEX, false);
        assertEquals("# comment\n$schema = " + EcoreMappingSchema.NAME
            + "\nordering = index\nShop.orders.ordering = index\nuseIdentifiers = false\n", edited);
    }

    /** Parses a mapping from a given settings text. */
    static private EcoreMapping mapping(String text) throws Exception {
        Properties props = new Properties();
        props.load(new StringReader(text));
        return new EcoreMapping(props);
    }

    /** Asserts that a given settings text does not parse as a mapping. */
    static private void assertMappingError(String text) throws Exception {
        try {
            mapping(text);
            fail("Mapping entry '" + text + "' should be rejected");
        } catch (FormatException expected) {
            // this is the expected outcome
        }
    }

    // ----------------------------------------------------------------------
    // Helper methods
    // ----------------------------------------------------------------------

    /**
     * Imports a fixture, checks that what comes out is a compilable grammar,
     * exports it again and imports the result back, and asserts that the two
     * imports agree: isomorphic graphs, the same round-trip metadata and the
     * same identifiers.
     * @param fixture the name of the fixture file in {@link #DIR}
     * @param ordering the ordering mode to use throughout
     * @return the resources of the <i>second</i> import, for further assertions
     */
    static private Set<Imported> assertRoundTrip(String fixture,
                                                 Ordering ordering) throws Exception {
        return assertRoundTrip(fixture, mappingText(ordering, true));
    }

    /** Variant of {@link #assertRoundTrip(String, Ordering)} that threads a
     * full mapping resource text through both imports and the export. */
    static private Set<Imported> assertRoundTrip(String fixture,
                                                 String mappingText) throws Exception {
        Set<Imported> imported = importFrom(new File(DIR + fixture), mappingText);
        AspectGraph type = single(imported, ResourceKind.TYPE);
        AspectGraph host = optional(imported, ResourceKind.HOST);
        assertEquals(Collections.emptyList(), messages(type.getErrors()));
        // the imported graphs are used as they are: since the approximations
        // of the encoding are silent, a well-formed input carries no errors
        GrammarModel grammar = newGrammar(type, host == null
            ? null
            : host.rename(QualName.name("start")), mappingText);
        assertEquals(Collections.emptyList(), messages(grammar.getTypeModel().getErrors()));
        if (host != null) {
            assertEquals(Collections.emptyList(), messages(host.getErrors()));
            var hostModel = grammar.getHostModel(QualName.name("start"));
            assertNotNull(hostModel);
            assertEquals(Collections.emptyList(), messages(hostModel.getErrors()));
            assertEquals(Collections.emptyList(), messages(grammar.getErrors()));
        }
        File dir = newDir();
        exportTo(newGrammar(type, host, mappingText), dir);
        Set<Imported> result = importFrom(new File(dir, fixture), mappingText);
        AspectGraph resultType = single(result, ResourceKind.TYPE);
        assertIsomorphic(type, resultType);
        assertEquals(metadata(type), metadata(resultType));
        if (host != null) {
            AspectGraph resultHost = single(result, ResourceKind.HOST);
            assertIsomorphic(host, resultHost);
            assertEquals(identifiers(host), identifiers(resultHost));
        }
        return result;
    }

    /** Imports a fixture file with given encoding options. */
    static private Set<Imported> importFrom(String fixture, Ordering ordering,
                                            boolean useIds) throws Exception {
        return importFrom(new File(DIR + fixture), mappingText(ordering, useIds));
    }

    /** Imports a fixture file with a given mapping resource text. */
    static private Set<Imported> importFrom(String fixture, String mappingText) throws Exception {
        return importFrom(new File(DIR + fixture), mappingText);
    }

    /** Imports a file with a given mapping resource text. */
    static private Set<Imported> importFrom(File file, String mappingText) throws Exception {
        FileType fileType = FileType.getType(file);
        assertNotNull(fileType);
        return EcorePorter.instance().doImport(file, fileType, newGrammar(mappingText));
    }

    /** Exports the active type graph and start graph of a grammar to a directory. */
    static private void exportTo(GrammarModel grammar, File dir) throws Exception {
        for (var kind : List.of(ResourceKind.TYPE, ResourceKind.HOST)) {
            FileType fileType = kind == ResourceKind.TYPE
                ? FileType.ECORE
                : FileType.XMI;
            for (var name : grammar.getActiveNames(kind)) {
                var model = grammar.getResource(kind, name);
                assertNotNull(model);
                File file = new File(dir, name + fileType.getExtension());
                EcorePorter.instance().doExport(Exportable.resource(model), file, fileType);
            }
        }
    }

    /** Asserts that two aspect graphs are isomorphic. */
    static private void assertIsomorphic(AspectGraph one, AspectGraph two) {
        assertEquals(Collections.emptyList(), messages(two.getErrors()));
        assertTrue("Graphs are not isomorphic",
                   IsoChecker
                       .getInstance(true)
                       .areIsomorphic(one.toPlainGraph(), two.toPlainGraph()));
    }

    /** Returns the Ecore round-trip metadata of a graph, as a key-to-value map. */
    static private Map<String,String> metadata(AspectGraph graph) {
        var properties = GraphInfo.getProperties(graph);
        Map<String,String> result = new LinkedHashMap<>();
        for (var key : List
            .of(EcoreToGraphs.PACKAGES_KEY, EcoreToGraphs.TYPES_KEY, EcoreToGraphs.FEATURES_KEY,
                EcoreToGraphs.OPPOSITES_KEY)) {
            result.put(key, properties.getProperty(key));
        }
        return result;
    }

    /** Creates a fresh temporary directory for exported files. */
    static private File newDir() throws Exception {
        File result = Files.createTempDirectory("ecore-export").toFile();
        result.deleteOnExit();
        return result;
    }

    /** Returns the unique imported resource of a given kind. */
    static private AspectGraph single(Set<Imported> imported, ResourceKind kind) {
        AspectGraph result = optional(imported, kind);
        assertNotNull(result);
        return result;
    }

    /** Returns the unique imported resource of a given kind, if there is one. */
    static private AspectGraph optional(Set<Imported> imported, ResourceKind kind) {
        AspectGraph result = null;
        for (var res : imported) {
            if (res.kind() == kind) {
                assertEquals(null, result);
                result = res.graph();
            }
        }
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

    /** Returns the descriptions of the non-self edges with a given label. */
    static private Set<String> edgesWith(AspectGraph graph, String label) {
        Set<String> result = new TreeSet<>();
        for (var edge : binaryEdges(graph)) {
            if (edge.contains(" -" + label + "-> ")) {
                result.add(edge);
            }
        }
        return result;
    }

    /** Returns the descriptions of the non-self edges incident with a node of a
     * given type: those with such a node as source as well as those with one as
     * target. */
    static private Set<String> edgesAt(AspectGraph graph, String type) {
        Set<String> result = new TreeSet<>();
        for (var edge : binaryEdges(graph)) {
            if (edge.contains(type + "#")) {
                result.add(edge);
            }
        }
        return result;
    }

    /** Returns the keys of the targets of the edges with a given label. */
    static private Set<String> targets(AspectGraph graph, String label) {
        Set<String> result = new TreeSet<>();
        for (var edge : graph.edgeSet()) {
            if (edge.source() != edge.target() && edge.label().text().equals(label)) {
                result.add(key(graph, edge.target()));
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
        var value = node.getValue();
        if (value != null) {
            // this is a constant node, holding a value of a many-valued attribute
            return value.toString();
        }
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

    /** Creates an empty grammar model with a given mapping resource text. */
    static private GrammarModel newGrammar(String mappingText) throws Exception {
        SystemStore store = newStore();
        putMapping(store, mappingText);
        return new GrammarModel(store);
    }

    /** Stores an {@code ecore} settings resource with a given text. */
    static private void putMapping(SystemStore store, String mappingText) throws Exception {
        store.putTexts(ResourceKind.SETTINGS, Map.of(EcoreMapping.RESOURCE_QUAL_NAME, mappingText));
    }

    /** Builds the text of an {@code ecore} settings resource from global
     * options and optional per-element entries. */
    static private String mappingText(Ordering ordering, boolean useIds, String... extras) {
        StringBuilder result = new StringBuilder();
        result.append("$schema = " + EcoreMappingSchema.NAME + "\n");
        result.append(EcoreMapping.ORDERING_KEY + " = " + ordering.text() + "\n");
        result.append(EcoreMapping.USE_IDENTIFIERS_KEY + " = " + useIds + "\n");
        for (String extra : extras) {
            result.append(extra + "\n");
        }
        return result.toString();
    }

    /** Creates a grammar model containing a given type graph and start graph. */
    static private GrammarModel newGrammar(AspectGraph type, AspectGraph host) throws Exception {
        return newGrammar(type, host, mappingText(Ordering.NONE, true));
    }

    /** Creates a grammar model containing a given type graph and optional start
     * graph, with a given mapping resource text. */
    static private GrammarModel newGrammar(AspectGraph type, AspectGraph host,
                                           String mappingText) throws Exception {
        SystemStore store = newStore();
        store.putGraphs(ResourceKind.TYPE, List.of(type), false);
        if (host != null) {
            store.putGraphs(ResourceKind.HOST, List.of(host), false);
        }
        putMapping(store, mappingText);
        GrammarModel result = new GrammarModel(store);
        result.setLocalActiveNames(ResourceKind.TYPE, type.getQualName());
        result
            .setLocalActiveNames(ResourceKind.HOST, host == null
                ? new QualName[0]
                : new QualName[] {host.getQualName()});
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
