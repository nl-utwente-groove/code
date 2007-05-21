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
 * $Id: Converter.java,v 1.2 2007-05-21 22:19:36 rensink Exp $
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
 * @version $Revision: 1.2 $
 */
public class Converter {
	/** Writes a graph in FSM format to a print writer. */
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
    /**
     * Converts a piece of text to HTML by replacing special characters
     * to their HTML encodings.
     */
    static public String toHtml(Object text) {
        return toHtml(new StringBuilder(text.toString())).toString();
    }

    /**
     * Converts a piece of text to HTML by replacing special characters
     * to their HTML encodings.
     */
    static public StringBuilder toHtml(StringBuilder text) {
    	for (int i = 0; i < text.length(); i++) {
    		char c = text.charAt(i);
    		switch (c) {
    		case '<': text.replace(i, i+1, "&lt;"); i += 3; break;
    		case '>': text.replace(i, i+1, "&gt;"); i += 3; break;
    		case '\n': text.replace(i, i+1, HTML_LINEBREAK); i += HTML_LINEBREAK.length()-1; break;
    		}
    	}
        return text;
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
            return on(new StringBuilder(text.toString()));
        }

        /**
         * Puts the tag around a given string builder, and returns the result.
         * The description is assumed to be in HTML format.
         * @param text the string builder from which the description is to be abstracted
         */
        public String on(StringBuilder text) {
            text.insert(0, tagBegin);
            text.append(tagEnd);
            return text.toString();
        }

        /**
         * Puts the tag around a given string, first converting special HTML
         * characters if required, and returns the result.
         * @param text the object from which the description is to be abstracted
         * @param convert if true, text is converted to HTML first. 
         */
        public String on(Object text, boolean convert) {
            if (convert)
                return on(toHtml(new StringBuilder(text.toString())));
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

    /** Creates a font colour tag for a given colour. */
    static public HTMLTag createColorTag(Color color) {
        String colorString =
            toHex(color.getRed()) + toHex(color.getGreen()) + toHex(color.getBlue()) ;
        return new HTMLTag("font", "color", colorString);
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
    
    /** Name of the HTML tag (<code>html</code>). */
    static public String HTML_TAG_NAME = "html";
    /** Name of the linebreak tag (<code>br</code>). */
    static public String LINEBREAK_TAG_NAME = "br";
    /** Name of the horizontal rule tag (<code>hr</code>). */
    static public String HORIZONTAL_LINE_TAG_NAME = "hr";

    /** The <code>html</code> tag to insert a line break. */
    static public String HTML_LINEBREAK = createHtmlTag(LINEBREAK_TAG_NAME).tagBegin;
    /** The <code>html</code> tag to insert a horizontal line. */
    static public String HTML_HORIZONTAL_LINE = createHtmlTag(HORIZONTAL_LINE_TAG_NAME).tagBegin;


    static private int HEX = 16;
    
    static String toHex(int number) {
        return "" + Character.forDigit((number / HEX)%HEX, HEX) + Character.forDigit(number % HEX, HEX);
    }
    
    /** Main method to test this class. */
    static public void main(String[] args) {
        HTMLTag blue = createColorTag(Color.blue);
        HTMLTag green = createColorTag(Color.green);
        HTMLTag red = createColorTag(Color.red);
        System.out.println(blue.on("Text"));
        System.out.println(red.on("Text"));
        System.out.println(green.on("Text"));
    }
}