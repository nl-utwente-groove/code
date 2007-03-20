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
 * $Id: Converter.java,v 1.1.1.2 2007-03-20 10:42:58 kastenberg Exp $
 */
package groove.util;

import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.Node;

import java.awt.Color;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Performs conversions to and from groove.graph.Graph.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class Converter {
    static public void graphToFsm(GraphShape graph, PrintWriter writer) {
        // mapping from nodes of grapg to integers
        Map<Node,Integer> nodeMap = new HashMap<Node,Integer>();
        writer.println("NodeNumber(0)");
        writer.println("---");
        int nr = 1;
        for (Node node: graph.nodeSet()) {
            nodeMap.put(node, nr);
            writer.println(nr);
            nr++;
        }
        writer.println("---");
        for (Edge edge: graph.edgeSet()) {
            writer.println(
                nodeMap.get(edge.source()) + " " + nodeMap.get(edge.opposite()) + " " + "\"" + edge.label() + "\"");
        }
    }

    // html defs

    static public String HTML_TAG_NAME = "html";
    static public String LINEBREAK_TAG_NAME = "br";
    static public String HORIZONTAL_LINE_TAG_NAME = "hr";

    /** The <code>html</code> tag to insert a line break. */
    static public String HTML_LINEBREAK = createHtmlTag(LINEBREAK_TAG_NAME).tagBegin;
    /** The <code>html</code> tag to insert a horizontal line. */
    static public String HTML_HORIZONTAL_LINE = createHtmlTag(HORIZONTAL_LINE_TAG_NAME).tagBegin;

    /**
     * Converts a piece of text to HTML by replacing special characters
     * to their HTML encodings.
     */
    static public String toHtml(Object text) {
    	String res = ""+text;
        res = res.replaceAll("<", "&lt;");
        res = res.replaceAll(">", "&gt;");
    	res = res.replaceAll("\n", HTML_LINEBREAK);
        return res;
    }

    /**
     * Class that allows some handling of HTML text.
     */
    static public class HTMLTag {
        private HTMLTag(String tag) {
            this.tagBegin = "<" + tag + ">";
            this.tagEnd = "</" + tag + ">";
        }

        private HTMLTag(String tag, String arguments) {
            this.tagBegin = String.format("<%s %s>", tag, arguments);
            this.tagEnd = "</" + tag + ">";
        }

        private HTMLTag(String tag, String attrName, String attrValue) {
            this.tagBegin = "<" + tag + " " + attrName + "=\"" + toHtml(attrValue) + "\">";
            this.tagEnd = "</" + tag + ">";
        }

        /**
         * Puts the tag around a given object description, and returns the result.
         * The description is assumed to be in HTML format.
         * @param text the object from which the description is to be abstracted
         */
        public String on(Object text) {
            return tagBegin + text + tagEnd;
        }

        /**
         * Puts the tag around a given string, first converting special HTML
         * characters if required, and returns the result.
         * @param text the object from which the description is to be abstracted
         * @param convert if true, text is converted to HTML first. 
         */
        public String on(Object text, boolean convert) {
            if (convert)
                return on(toHtml(text));
            else
                return on(text);
        }

        /**
         * Puts the tag around the strings in a given array, and returns the result.
         * The description is assumed to be in HTML format.
         * @param text the array of objects from which the description is to be abstracted
         */
        public String[] on(Object[] text) {
        	return on(text, false);
        }

        /**
         * Puts the tag around the strings in a given array, first converting special HTML
         * characters if required, and returns the result.
         * @param text the array of objects from which the description is to be abstracted
         * @param convert if true, text is converted to HTML first. 
         */
        public String[] on(Object[] text, boolean convert) {
        	String[] result = new String[text.length];
        	for (int labelIndex = 0; labelIndex < text.length; labelIndex ++) {
        		result[labelIndex] = on(text[labelIndex], convert);
        	}
        	return result;
        }

        private final String tagBegin;
        private final String tagEnd;
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
    static public HTMLTag createHtmlTag(String tag, String arguments) {
        return new HTMLTag(tag, arguments);
    }

    static public HTMLTag createColorTag(Color color) {
        String colorString =
            toHex(color.getRed()) + toHex(color.getGreen()) + toHex(color.getBlue()) ;
        return new HTMLTag("font", "color", colorString);
    }

    static private int HEX = 16;
    
    static String toHex(int number) {
        return "" + Character.forDigit((number / HEX)%HEX, HEX) + Character.forDigit(number % HEX, HEX);
    }
    
    static public void main(String[] args) {
        HTMLTag blue = createColorTag(Color.blue);
        HTMLTag green = createColorTag(Color.green);
        HTMLTag red = createColorTag(Color.red);
        System.out.println(blue.on("Text"));
        System.out.println(red.on("Text"));
        System.out.println(green.on("Text"));
    }
}