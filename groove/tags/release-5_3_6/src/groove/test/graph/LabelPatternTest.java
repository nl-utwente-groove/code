/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.test.graph;

import static org.junit.Assert.fail;
import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.algebra.Sort;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.type.LabelPattern;
import groove.util.parse.FormatException;
import junit.framework.Assert;

import org.junit.Test;

@SuppressWarnings("all")
public class LabelPatternTest {
    @Test
    public void testParsing() {
        parseTest("\"simple text\"", true);
        parseTest("\"one par %s\",id1", true);
        parseTest("\"two pars %2$s and %1$s\",id1,id2", true);
        // quoting errors
        parseTest("No quotes", false);
        parseTest("\"Unbalanced quotes", false);
        parseTest("\"Too many\",\"quotes\"", false);
        parseTest("Wrongly,\"placed quotes\"", false);
        // separator and identifier errors
        parseTest("\"Correct,\"", true);
        parseTest("\"Wrong\",", false);
        parseTest("\"Wrong\",id1,", false);
        parseTest("\"Wrong\",,id1", false);
        parseTest("\"Correct\",id_1$", true);
        parseTest("\"Wrong\",id:1$", false);
        // too few parameters
        parseTest("\"one par %s\"", false);
        parseTest("\"two pars %2$s and %1$s\",id1", false);
    }

    @Test
    public void testDirectInstantiation() {
        directInstanceTest("No pars", "No pars");
        directInstanceTest("One par: 23", "One par: %s", 23);
        directInstanceTest("One par: null", "One par: %s", (Object) null);
        directInstanceTest("Two pars: 3-1", "Two pars: %2$s-%1$s", 1, 3);
        // too few pars
        directInstanceTest(null, "One par: %s");
        directInstanceTest(null, "Two pars: %2$s-%1$s", 1);
    }

    @Test
    public void testGraphInstantiation() {
        // construct the graph
        HostGraph graph = new DefaultHostGraph("test");
        HostNode n1 = graph.addNode();
        Algebra<?> intAlgebra =
            AlgebraFamily.getInstance().getAlgebra(Sort.INT);
        Algebra<?> stringAlgebra =
            AlgebraFamily.getInstance().getAlgebra(Sort.STRING);
        HostNode i1 =
            graph.getFactory().createNode(intAlgebra,
                intAlgebra.toValueFromJava(11));
        graph.addNode(i1);
        HostNode i2 =
            graph.getFactory().createNode(intAlgebra,
                intAlgebra.toValueFromJava(22));
        graph.addNode(i2);
        HostNode s =
            graph.getFactory().createNode(stringAlgebra,
                stringAlgebra.toValueFromJava("text"));
        graph.addNode(s);
        graph.addEdge(n1, "i1", i1);
        graph.addEdge(n1, "i2", i2);
        graph.addEdge(n1, "s", s);
        // run tests
        graphInstanceTest("No pars", "\"No pars\"", graph, n1);
        graphInstanceTest("One par: 11", "\"One par: %s\",i1", graph, n1);
        graphInstanceTest("One par: null", "\"One par: %s\",i3", graph, n1);
        graphInstanceTest("One par: \"text\"", "\"One par: %s\",s", graph, n1);
        graphInstanceTest("Two pars: 22-11", "\"Two pars: %2$s-%1$s\",i1,i2",
            graph, n1);
    }

    private void parseTest(String text, boolean succeed) {
        try {
            LabelPattern.parse(text);
            if (!succeed) {
                fail(String.format(
                    "Text %s should not parse correctly but does", text));
            }
        } catch (FormatException e) {
            if (succeed) {
                fail(String.format(
                    "Text %s should parse correctly but throws %s", text,
                    e.getMessage()));
            }
        }
    }

    private void directInstanceTest(String label, String format,
            Object... values) {
        StringBuilder text = new StringBuilder();
        text.append('"');
        text.append(format);
        text.append('"');
        for (int i = 0; i < values.length; i++) {
            text.append(',');
            text.append("id");
            text.append(i);
        }
        try {
            LabelPattern pattern = LabelPattern.parse(text.toString());
            if (label == null) {
                fail(String.format(
                    "Text %s with parameters %s should not expand correctly but does",
                    text, values));

            } else {
                Assert.assertEquals(label, pattern.getLabel(values));
            }
        } catch (FormatException e) {
            if (label != null) {
                fail(String.format(
                    "Text %s with parameters %s should expand correctly but throws %s",
                    text, values, e.getMessage()));
            }
        }
    }

    private void graphInstanceTest(String label, String format,
            HostGraph graph, HostNode node) {
        try {
            LabelPattern pattern = LabelPattern.parse(format);
            if (label == null) {
                fail(String.format(
                    "Pattern %s evaluated for node %s of graph %s should not expand correctly but does",
                    format, node, graph));

            } else {
                Assert.assertEquals(label, pattern.getLabel(graph, node));
            }
        } catch (FormatException e) {
            if (label != null) {
                fail(String.format(
                    "Pattern %s evaluated for node %s of graph %s should expand correctly but throws %s",
                    format, node, graph, e.getMessage()));
            }
        }
    }
}
