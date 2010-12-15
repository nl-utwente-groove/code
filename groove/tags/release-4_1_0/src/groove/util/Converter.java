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
 * $Id: Converter.java,v 1.10 2008-01-30 09:32:02 iovka Exp $
 */
package groove.util;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.gui.jgraph.EditorJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JAttr;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.layout.LayoutMap;
import groove.view.aspect.RuleAspect;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Performs conversions to and from groove.graph.Graph.
 * @author Arend Rensink
 * @version $Revision$
 */
public class Converter {
    /** Main method to test this class. */
    static public void main(String[] args) {
        System.out.println(blue.on("Text"));
        System.out.println(red.on("Text"));
        System.out.println(green.on("Text"));
    }

    /** Writes a graph in FSM format to a print writer. */
    static public void graphToFsm(GraphShape graph, PrintWriter writer) {
        // mapping from nodes of graphs to integers
        Map<Node,Integer> nodeMap = new HashMap<Node,Integer>();
        writer.println("NodeNumber(0)");
        writer.println("---");
        int nr = 1;
        for (Node node : graph.nodeSet()) {
            nodeMap.put(node, nr);
            writer.println(nr);
            nr++;
        }
        writer.println("---");
        for (Edge edge : graph.edgeSet()) {
            writer.println(nodeMap.get(edge.source()) + " "
                + nodeMap.get(edge.target()) + " " + "\"" + edge.label() + "\"");
        }
    }

    /** Writes a graph in CADP .aut format to a print writer. */
    static public void graphToAut(GraphShape graph, PrintWriter writer) {
        // collect the node numbers, to be able to number them consecutively
        int nodeCount = graph.nodeCount();
        // list marking which node numbers have been used
        BitSet nodeList = new BitSet(nodeCount);
        // mapping from nodes to node numbers
        Map<Node,Integer> nodeNrMap = new HashMap<Node,Integer>();
        // nodes that do not have a valid number (in the range 0..nodeCount-1)
        Set<Node> restNodes = new HashSet<Node>();
        // iterate over the existing nodes
        for (Node node : graph.nodeSet()) {
            int nodeNr = node.getNumber();
            if (nodeNr >= 0 && nodeNr < nodeCount) {
                nodeList.set(nodeNr);
                nodeNrMap.put(node, nodeNr);
            } else {
                restNodes.add(node);
            }
        }
        int nextNodeNr = -1;
        for (Node restNode : restNodes) {
            do {
                nextNodeNr++;
            } while (nodeList.get(nextNodeNr));
            nodeNrMap.put(restNode, nextNodeNr);
        }
        writer.printf("des (%d, %d, %d)%n", 0, graph.edgeCount(),
            graph.nodeCount());
        for (Edge edge : graph.edgeSet()) {
            String format;
            if (edge.label().text().indexOf(',') >= 0) {
                format = "(%d,\"%s\",%d)%n";
            } else {
                format = "(%d,%s,%d)%n";
            }
            writer.printf(format, nodeNrMap.get(edge.source()), edge.label(),
                nodeNrMap.get(edge.target()));
        }
    }

    /** Reads in a graph from CADP .aut format. */
    static public Map<String,Node> autToGraph(InputStream reader, Graph graph)
        throws IOException {
        Map<String,Node> result = new HashMap<String,Node>();
        BufferedReader in = new BufferedReader(new InputStreamReader(reader));
        int linenr = 0;
        try {
            String line = in.readLine();
            linenr++;
            int rootStart = line.indexOf('(') + 1;
            int edgeCountStart = line.indexOf(',') + 1;
            int root =
                Integer.parseInt(line.substring(rootStart, edgeCountStart - 1).trim());
            Node rootNode = graph.addNode(root);
            result.put("" + root, rootNode);
            graph.addEdge(rootNode, ROOT_LABEL, rootNode);
            for (line = in.readLine(); line != null; line = in.readLine()) {
                linenr++;
                if (line.trim().length() > 0) {
                    int sourceStart = line.indexOf('(') + 1;
                    int labelStart = line.indexOf(',') + 1;
                    int targetStart = line.lastIndexOf(',') + 1;
                    int source =
                        Integer.parseInt(line.substring(sourceStart,
                            labelStart - 1).trim());
                    String label = line.substring(labelStart, targetStart - 1);
                    int target =
                        Integer.parseInt(line.substring(targetStart,
                            line.lastIndexOf(')')).trim());
                    Node sourceNode = graph.addNode(source);
                    Node targetNode = graph.addNode(target);
                    result.put("" + source, sourceNode);
                    result.put("" + target, targetNode);
                    graph.addEdge(sourceNode, label, targetNode);
                }
            }
        } catch (Exception e) {
            throw new IOException(String.format("Format error in line %d: %s",
                linenr, e.getMessage()));
        }
        return result;
    }

    /** Writes a graph in LaTeX <code>Tikz</code> format to a print writer. */
    static public void graphToTikz(JGraph graph, PrintWriter writer) {
        JModel model = graph.getModel();
        GraphJModel graphModel;
        // if the model is an editor model, self-edges should be displayed as
        // node labels
        if (model instanceof EditorJModel) {
            graphModel =
                GraphJModel.newInstance(model.toPlainGraph(),
                    model.getOptions());
            graphModel.setShowVertexLabels();
        } else {
            graphModel = (GraphJModel) model;
        }
        LayoutMap<Node,Edge> layoutMap =
            GraphInfo.getLayoutMap(graphModel.getGraph());
        writer.print(GraphToTikz.convertGraphToTikzStr(graphModel, layoutMap));
    }

    /** Writes a graph in a simple .kth format to a print writer. */
    static public void graphToKth(GraphShape graph, PrintWriter writer) {
        writer.print(GraphToKth.convertGraph(graph));
    }

    // html defs
    /**
     * Converts a piece of text to HTML by replacing special characters to their
     * HTML encodings.
     */
    static public String toHtml(Object text) {
        return toHtml(new StringBuilder(text.toString())).toString();
    }

    /**
     * Converts a piece of text to HTML by replacing special characters to their
     * HTML encodings.
     */
    static public StringBuilder toHtml(StringBuilder text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
            case '/':
                text.replace(i, i + 1, "&#47;");
                i += 4;
                break;
            case '<':
                text.replace(i, i + 1, "&lt;");
                i += 3;
                break;
            case '>':
                text.replace(i, i + 1, "&gt;");
                i += 3;
                break;
            case '\n':
                text.replace(i, i + 1, HTML_LINEBREAK);
                i += HTML_LINEBREAK.length() - 1;
                break;
            }
        }
        return text;
    }

    /**
     * Returns an HTML tag embedder.
     */
    static public HTMLTag createHtmlTag(String tag) {
        return new HTMLTag(tag);
    }

    /**
     * Returns an HTML tag embedder with an argument string.
     */
    static public HTMLTag createHtmlTag(String tag, String attribute,
            String arguments) {
        return new HTMLTag(tag, attribute, arguments);
    }

    /**
     * Returns a span tag with a style argument.
     */
    static public HTMLTag createSpanTag(String arguments) {
        return new HTMLTag(SPAN_TAG_NAME, STYLE_ATTR_NAME, arguments);
    }

    /**
     * Returns a HTML span tag that imposes a given color on a text.
     */
    static public HTMLTag createColorTag(Color color) {
        HTMLTag result = colorTagMap.get(color);
        if (result == null) {
            StringBuffer arg = new StringBuffer();
            int red = color.getRed();
            int blue = color.getBlue();
            int green = color.getGreen();
            int alpha = color.getAlpha();
            arg.append("color: rgb(");
            arg.append(red);
            arg.append(",");
            arg.append(green);
            arg.append(",");
            arg.append(blue);
            arg.append(");");
            if (alpha != MAX_ALPHA) {
                // the following is taken from the internet; it is to make
                // sure that all html interpretations set the opacity correctly.
                double alphaFraction = ((double) alpha) / MAX_ALPHA;
                arg.append("float:left;filter:alpha(opacity=");
                arg.append((int) (100 * alphaFraction));
                arg.append(");opacity:");
                arg.append(alphaFraction);
                arg.append(";");
            }
            result = Converter.createSpanTag(arg.toString());
            colorTagMap.put(color, result);
        }
        return result;
    }

    /** Converts the first letter of a given string to upper- or lowercase. */
    static public String toUppercase(String text, boolean upper) {
        return toUppercase(new StringBuilder(text), upper).toString();
    }

    /** Converts the first letter of a given string to upper- or lowercase. */
    static public StringBuilder toUppercase(StringBuilder text, boolean upper) {
        Character firstChar = text.charAt(0);
        if (upper) {
            firstChar = Character.toUpperCase(firstChar);
        } else {
            firstChar = Character.toLowerCase(firstChar);
        }
        text.replace(0, 1, firstChar.toString());
        return text;
    }

    /**
     * Strips the color tags from the HTML line.
     * @param htmlLine the line to be striped
     * @return 1 if the line was blue, 2 if green, 3 if red and 0 otherwise.
     */
    public static int removeColorTags(StringBuilder htmlLine) {
        String originalLine = htmlLine.toString();
        int result = 0;
        if (!blue.off(htmlLine).equals(originalLine)) {
            result = 1;
        } else if (!green.off(htmlLine).equals(originalLine)) {
            result = 2;
        } else if (!red.off(htmlLine).equals(originalLine)) {
            result = 3;
        }
        return result;
    }

    /**
     * Strips the font tags from the HTML line.
     * @param htmlLine the line to be striped
     * @return 1 if the line was bold, 2 if the line was italic, 3 if the line
     *         was both bold and italic, and 0 otherwise.
     */
    public static int removeFontTags(StringBuilder htmlLine) {
        String originalLine = htmlLine.toString();
        int bold = 0;
        int italic = 0;
        if (!STRONG_TAG.off(htmlLine).equals(originalLine)) {
            bold = 1;
            originalLine = htmlLine.toString();
        }
        if (!ITALIC_TAG.off(htmlLine).equals(originalLine)) {
            italic = 2;
        }
        return bold + italic;
    }

    /** HTML greater than symbol. */
    // The readable codes do not work on the Mac in some situations. Replaced
    // them with the numeric codes - this fixes it. -- Maarten
    static public final String HTML_GT = "&#62;"; // &gt;
    /** HTML forall symbol. */
    static public final String HTML_FORALL = "&#8704;"; // &forall;
    /** HTML exists symbol. */
    static public final String HTML_EXISTS = "&#8707;"; // &exist;
    /** HTML negation symbol. */
    static public final String HTML_NOT = "&#172;"; // &not;
    /** HTML lambda symbol. */
    static public final String HTML_LAMBDA = "&#955;"; // &lambda;
    /** HTML tau symbol. */
    static public final String HTML_TAU = "&#932;"; // &tau;
    /** HTML epsilon symbol. */
    static public final String HTML_EPSILON = "&#949;"; // &epsilon;
    /** HTML times symbol. */
    static public final String HTML_TIMES = "&#215;"; // &times;
    /** Name of the HTML tag (<code>html</code>). */
    static public final String HTML_TAG_NAME = "html";
    /** HTML tag. */
    static public final HTMLTag HTML_TAG = new HTMLTag(HTML_TAG_NAME);
    /** Name of the span tag (<code>span</code>). */
    static public final String SPAN_TAG_NAME = "span";
    /** Name of the span style attribute. */
    static public final String STYLE_ATTR_NAME = "style";
    /** Name of the linebreak tag (<code>br</code>). */
    static public final String LINEBREAK_TAG_NAME = "br";
    /** Name of the horizontal rule tag (<code>hr</code>). */
    static public final String HORIZONTAL_LINE_TAG_NAME = "hr";
    /** Name of the font underline tag (<code>u</code>). */
    static public final String UNDERLINE_TAG_NAME = "u";
    /** Font underline tag. */
    static public final HTMLTag UNDERLINE_TAG = new HTMLTag(UNDERLINE_TAG_NAME);
    /** Name of the font strikethrough tag (<code>s</code>). */
    static public final String STRIKETHROUGH_TAG_NAME = "s";
    /** Font strikethrough tag. */
    static public final HTMLTag STRIKETHROUGH_TAG = new HTMLTag(
        STRIKETHROUGH_TAG_NAME);
    /** Name of the italic font tag (<code>i</code>). */
    static public final String ITALIC_TAG_NAME = "i";
    /** Italic font tag. */
    static public final HTMLTag ITALIC_TAG = new HTMLTag(ITALIC_TAG_NAME);
    /** Name of the strong font tag (<code>strong</code>). */
    static public final String STRONG_TAG_NAME = "strong";
    /** Strong font tag. */
    static public final HTMLTag STRONG_TAG = new HTMLTag(STRONG_TAG_NAME);
    /** Name of the superscript font tag. */
    static public final String SUPER_TAG_NAME = "sup";
    /** Superscript font tag. */
    static public final HTMLTag SUPER_TAG = new HTMLTag(SUPER_TAG_NAME);
    /** Name of the subscript font tag. */
    static public final String SUB_TAG_NAME = "sub";
    /** Subscript font tag. */
    static public final HTMLTag SUB_TAG = new HTMLTag(SUB_TAG_NAME);

    /** The <code>html</code> tag to insert a line break. */
    static public final String HTML_LINEBREAK =
        createHtmlTag(LINEBREAK_TAG_NAME).tagBegin;
    /** The <code>html</code> tag to insert a horizontal line. */
    static public final String HTML_HORIZONTAL_LINE =
        createHtmlTag(HORIZONTAL_LINE_TAG_NAME).tagBegin;

    /** Map from colours to HTML tags imposing the colour on a text. */
    private static final Map<Color,HTMLTag> colorTagMap =
        new HashMap<Color,HTMLTag>();
    /** The maximum alpha value according to {@link Color#getAlpha()}. */
    private static final int MAX_ALPHA = 255;

    /** Label used to identify the start state, when reading in from .aut */
    private static final String ROOT_LABEL = "$ROOT$";

    /** Blue color tag. */
    public static final HTMLTag blue = createColorTag(Colors.findColor("blue"));
    /** Green color tag. */
    public static final HTMLTag green =
        createColorTag(Colors.findColor("green.darker"));
    /** Red color tag. */
    public static final HTMLTag red = createColorTag(Colors.findColor("red"));
    /** Remark color tag. */
    public static final HTMLTag remark =
        createColorTag(JAttr.RULE_COLOR.get(RuleAspect.REMARK));

    /**
     * Class that allows some handling of HTML text.
     */
    static public class HTMLTag {
        HTMLTag(String tag) {
            this.tagBegin = String.format("<%s>", tag);
            this.tagEnd = String.format("</%s>", tag);
        }

        HTMLTag(String tag, String attrName, String attrValue) {
            this.tagBegin =
                String.format("<%s %s=\"%s\">", tag, attrName,
                    toHtml(attrValue));
            this.tagEnd = String.format("</%s>", tag);
        }

        /**
         * Puts the tag around a given object description, and returns the
         * result. The description is assumed to be in HTML format.
         * @param text the object from which the description is to be abstracted
         */
        public String on(Object text) {
            return on(new StringBuilder(text.toString())).toString();
        }

        /**
         * Puts the tag around a given string builder, and returns the result.
         * The changes are implemented in the string builder itself, i.e., the
         * parameter is modified. The description is assumed to be in HTML
         * format.
         * @param text the string builder that is to be augmented with this tag
         */
        public StringBuilder on(StringBuilder text) {
            text.insert(0, this.tagBegin);
            text.append(this.tagEnd);
            return text;
        }

        /**
         * Puts the tag around a given string, first converting special HTML
         * characters if required, and returns the result.
         * @param text the object from which the description is to be abstracted
         * @param convert if true, text is converted to HTML first.
         */
        public String on(Object text, boolean convert) {
            if (convert) {
                return on(toHtml(new StringBuilder(text.toString()))).toString();
            } else {
                return on(text);
            }
        }

        /**
         * Puts the tag around the strings in a given array, and returns the
         * result. The description is assumed to be in HTML format.
         * @param text the array of objects from which the description is to be
         *        abstracted
         */
        public String[] on(Object[] text) {
            return on(text, false);
        }

        /**
         * Puts the tag around the strings in a given array, first converting
         * special HTML characters if required, and returns the result.
         * @param text the array of objects from which the description is to be
         *        abstracted
         * @param convert if true, text is converted to HTML first.
         */
        public String[] on(Object[] text, boolean convert) {
            String[] result = new String[text.length];
            for (int labelIndex = 0; labelIndex < text.length; labelIndex++) {
                result[labelIndex] = on(text[labelIndex], convert);
            }
            return result;
        }

        /**
         * Strips the HTML tags from the string given.
         * @param text the string to be analyzed.
         * @return the input string unmodified if it did not contain the the
         *         HTML tags or the string striped from the tags.
         */
        public String off(StringBuilder text) {
            int tagEndStart = text.indexOf(this.tagEnd);
            int tagBeginStart = text.indexOf(this.tagBegin);
            if (tagEndStart > -1 && tagBeginStart > -1) {
                int end = tagEndStart + this.tagEnd.length();
                text.replace(tagEndStart, end, "");
                end = tagBeginStart + this.tagBegin.length();
                text.replace(tagBeginStart, end, "");
            }
            return text.toString();
        }

        /** Start text of this tag. */
        final String tagBegin;
        /** End text of this tag. */
        final String tagEnd;
    }

}