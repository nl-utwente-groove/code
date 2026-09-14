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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import nl.utwente.groove.io.external.format.ecore.EcoreKey;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping.Bounds;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping.FeatureData;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping.Kind;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping.PackageData;
import nl.utwente.groove.io.external.format.ecore.EcoreMappingSchema;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Tests the round-trip metadata half of the {@code ecore} settings vocabulary:
 * the {@code package}, {@code kind}, {@code feature} and {@code opposite} key
 * forms, their composite values, the errors they reject, and the round trip
 * between the parsed records and the entry lines they render to.
 * @author Arend Rensink
 */
@AIGenerated("Claude Opus 5, 2026-09")
public class EcoreMappingTest {
    /** Tests the parsing of the {@code package} form. */
    @Test
    public void testPackageForm() throws Exception {
        EcoreMapping mapping = mapping("""
            shop.package = nsURI=http://example.org/shop nsPrefix=sh
            shop.stock.package = nsURI=http://example.org/stock
            """);
        assertEquals(Map
            .of("shop", new PackageData("http://example.org/shop", "sh"), "shop.stock",
                new PackageData("http://example.org/stock", null)),
                     mapping.packages());
        // the prefix is optional, and the field order is free
        assertEquals(mapping("p.package = nsURI=u nsPrefix=x").packages(),
                     mapping("p.package = nsPrefix=x nsURI=u").packages());
    }

    /** Tests the parsing of the {@code kind} form. */
    @Test
    public void testKindForm() throws Exception {
        EcoreMapping mapping = mapping("""
            Item.kind = class
            Named.kind = interface
            shop.Colour.kind = enum
            Money.kind = datatype
            """);
        assertEquals(Map
            .of("Item", Kind.CLASS, "Named", Kind.INTERFACE, "shop.Colour", Kind.ENUM, "Money",
                Kind.DATATYPE),
                     mapping.kinds());
    }

    /** Tests the parsing of the {@code feature} form, including the
     * optionality and order-freeness of its fields. */
    @Test
    public void testFeatureForm() throws Exception {
        EcoreMapping mapping = mapping("""
            Order.items.feature = type=EInt ordered=true unique=false bounds=1..* name=it-ems
            shop.Order.total.feature = bounds=0..1
            Order.tag.feature =
            """);
        assertEquals(Map
            .of("Order.items",
                new FeatureData("EInt", true, false, new Bounds(1, Bounds.UNBOUNDED), "it-ems"),
                "shop.Order.total", new FeatureData(null, null, null, new Bounds(0, 1), null),
                "Order.tag", new FeatureData(null, null, null, null, null)),
                     mapping.features());
        // the fields may come in any order
        assertEquals(mapping("A.b.feature = type=EInt ordered=true bounds=0..3").features(),
                     mapping("A.b.feature = bounds=0..3 ordered=true type=EInt").features());
        // an unbounded upper bound may also be written as -1
        assertEquals(mapping("A.b.feature = bounds=2..*").features(),
                     mapping("A.b.feature = bounds=2..-1").features());
        assertTrue(new Bounds(2, Bounds.UNBOUNDED).isUnbounded());
        assertFalse(new Bounds(2, 3).isUnbounded());
    }

    /** Tests the parsing of the {@code opposite} form. */
    @Test
    public void testOppositeForm() throws Exception {
        EcoreMapping mapping = mapping("""
            Order.shop.opposite = Shop.orders
            shop.Shop.orders.opposite = shop.Order.shop
            """);
        assertEquals(Map
            .of("Order.shop", "Shop.orders", "shop.Shop.orders", "shop.Order.shop"),
                     mapping.opposites());
    }

    /** Tests that the new forms leave the pre-existing ones alone: a mapping
     * with metadata entries has no overrides, and vice versa. */
    @Test
    public void testFormSeparation() throws Exception {
        EcoreMapping metadata = mapping("""
            shop.package = nsURI=u
            Item.kind = class
            Item.name.feature = name=na-me
            Item.owner.opposite = Shop.items
            """);
        assertTrue(metadata.typeNames().isEmpty());
        assertTrue(metadata.featureOrdering().isEmpty());
        assertTrue(metadata.literalStyles().isEmpty());
        EcoreMapping overrides = mapping("""
            Item.typeName = Ding
            Item.owner.ordering = index
            """);
        assertTrue(overrides.packages().isEmpty());
        assertTrue(overrides.kinds().isEmpty());
        assertTrue(overrides.features().isEmpty());
        assertTrue(overrides.opposites().isEmpty());
    }

    /** Tests that malformed composite values are rejected, with an error
     * naming the offending key and field. */
    @Test
    public void testValueErrors() throws Exception {
        // unknown field
        assertMappingError("Item.name.feature = colour=red", "Item.name.feature", "colour");
        assertMappingError("shop.package = nsURI=u prefix=p", "shop.package", "prefix");
        // duplicate field
        assertMappingError("Item.name.feature = ordered=true ordered=false", "Item.name.feature",
                           "ordered");
        // malformed pair
        assertMappingError("Item.name.feature = ordered", "Item.name.feature", "ordered");
        assertMappingError("Item.name.feature = =true", "Item.name.feature", "=true");
        // missing mandatory field
        assertMappingError("shop.package = nsPrefix=p", "shop.package", "nsURI");
        // bad kind
        assertMappingError("Item.kind = klass", "Item.kind", "class");
        // bad bounds
        assertMappingError("Item.name.feature = bounds=1", "Item.name.feature", "bounds");
        assertMappingError("Item.name.feature = bounds=1..x", "Item.name.feature", "bounds");
        assertMappingError("Item.name.feature = bounds=3..1", "Item.name.feature", "bounds");
        assertMappingError("Item.name.feature = bounds=-2..3", "Item.name.feature", "bounds");
        // bad boolean
        assertMappingError("Item.name.feature = unique=yes", "Item.name.feature", "unique");
        // opposite with too few segments
        assertMappingError("Item.owner.opposite = items", "Item.owner.opposite", "segments");
        assertMappingError("Item.owner.opposite = Shop.", "Item.owner.opposite", "segments");
    }

    /** Tests that the new forms are rejected with an element path of the wrong
     * length, the error naming the key and the expected pattern. */
    @Test
    public void testPathLengthErrors() throws Exception {
        assertMappingError("package = nsURI=u", "package", EcoreKey.PACKAGE.pattern());
        assertMappingError("kind = class", "kind", EcoreKey.KIND.pattern());
        assertMappingError("Item.feature = type=EInt", "Item.feature", EcoreKey.FEATURE.pattern());
        assertMappingError("Item.opposite = A.b", "Item.opposite", EcoreKey.OPPOSITE.pattern());
    }

    /** Tests that rendering a set of records and parsing the result back
     * reproduces the records, in the syntax and order of the vocabulary. */
    @Test
    public void testRenderRoundTrip() throws Exception {
        Map<String,PackageData> packages = new LinkedHashMap<>();
        packages.put("shop", new PackageData("http://example.org/shop", "sh"));
        packages.put("shop.stock", new PackageData("http://example.org/stock", null));
        Map<String,Kind> kinds = new LinkedHashMap<>();
        kinds.put("shop.Item", Kind.CLASS);
        kinds.put("shop.Named", Kind.INTERFACE);
        kinds.put("shop.stock.Colour", Kind.ENUM);
        kinds.put("shop.Money", Kind.DATATYPE);
        Map<String,FeatureData> features = new LinkedHashMap<>();
        features
            .put("shop.Item.tags",
                 new FeatureData("EString", true, false, new Bounds(0, Bounds.UNBOUNDED), "ta-gs"));
        features.put("shop.Item.price", new FeatureData("EInt", null, null, null, null));
        features.put("shop.Item.owner", new FeatureData(null, null, null, new Bounds(1, 1), null));
        Map<String,String> opposites = new LinkedHashMap<>();
        opposites.put("shop.Item.owner", "shop.Shop.items");
        opposites.put("shop.Shop.items", "shop.Item.owner");
        List<String> lines = EcoreMapping.entryLines(packages, kinds, features, opposites);
        assertEquals(packages.size() + kinds.size() + features.size() + opposites.size(),
                     lines.size());
        // the groups come in a fixed order, and the entries within a group in map order
        assertEquals("shop.package = nsURI=http://example.org/shop nsPrefix=sh", lines.get(0));
        assertEquals("shop.stock.package = nsURI=http://example.org/stock", lines.get(1));
        assertEquals("shop.Item.kind = class", lines.get(2));
        assertEquals("shop.Item.price.feature = type=EInt", lines.get(7));
        assertEquals("shop.Shop.items.opposite = shop.Item.owner", lines.get(lines.size() - 1));
        // rendering is deterministic
        assertEquals(lines, EcoreMapping.entryLines(packages, kinds, features, opposites));
        EcoreMapping reparsed = mapping(String.join("\n", lines) + "\n");
        assertEquals(packages, reparsed.packages());
        assertEquals(kinds, reparsed.kinds());
        assertEquals(features, reparsed.features());
        assertEquals(opposites, reparsed.opposites());
        // and the instance renderer reproduces the same lines, up to the
        // alphabetical key order that parsing imposes
        assertEquals(lines.stream().sorted().toList(),
                     reparsed.entryLines().stream().sorted().toList());
    }

    /** Tests that the new forms take part in the key-form machinery: lookup by
     * choice key and path length, the generated template, and the help map. */
    @Test
    public void testKeyMachinery() throws Exception {
        for (EcoreKey key : List
            .of(EcoreKey.PACKAGE, EcoreKey.KIND, EcoreKey.FEATURE, EcoreKey.OPPOSITE)) {
            assertFalse(key.isGlobal(), key.name());
            assertEquals(List.of(key), EcoreKey.withText(key.text()), key.name());
            // the pattern determines the admissible path length
            int pathLength = key.pattern().split("\\.").length - 1;
            assertEquals(key, EcoreKey.lookup(key.text(), pathLength), key.name());
            assertEquals(key, EcoreKey.lookup(key.text(), pathLength + 3), key.name());
            assertEquals(null, EcoreKey.lookup(key.text(), pathLength - 1), key.name());
        }
        // the schema template picks the new forms up, and stays parseable
        String text = EcoreMappingSchema.INSTANCE.getNewText();
        for (EcoreKey key : EcoreKey.values()) {
            assertTrue(text.contains("# " + key.templateLine()), key.name());
        }
        EcoreMapping fresh = mapping(text);
        assertTrue(fresh.packages().isEmpty());
        assertTrue(fresh.kinds().isEmpty());
        assertTrue(fresh.features().isEmpty());
        assertTrue(fresh.opposites().isEmpty());
        // every form is documented, with a tool tip
        var docMap = EcoreMappingSchema.INSTANCE.getHelpMap();
        assertEquals(EcoreKey.values().length, docMap.size());
        docMap.forEach((item, tip) -> assertNotNull(tip, item));
    }

    /** Parses a mapping from a given settings text. */
    static private EcoreMapping mapping(String text) throws FormatException, IOException {
        Properties props = new Properties();
        props.load(new StringReader(text));
        return new EcoreMapping(props);
    }

    /**
     * Asserts that a given settings text is rejected, with an error message
     * naming the offending key as well as a given further fragment.
     */
    static private void assertMappingError(String text, String key,
                                           String fragment) throws IOException {
        try {
            mapping(text);
            fail("Mapping entry '" + text + "' should be rejected");
        } catch (FormatException expected) {
            String message = expected.getMessage();
            assert message != null; // a format exception always carries its errors
            assertTrue(message.contains(key), message);
            assertTrue(message.contains(fragment), message);
        }
    }
}
