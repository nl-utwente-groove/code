/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction.gui.jgraph;

import static groove.util.Converter.HTML_TAG;
import static groove.util.Converter.ITALIC_TAG;
import groove.abstraction.Shape;
import groove.abstraction.ShapeNode;
import groove.abstraction.Util;
import groove.graph.TypeLabel;
import groove.gui.Options;
import groove.gui.jgraph.JAttr;
import groove.util.Converter;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Set;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

/**
 * EDUARDO: Comment this...
 * @author Eduardo Zambon
 */
public class ShapeJVertex extends DefaultGraphCell {

    private Shape shape;
    private ShapeNode node;

    /**
     * EDUARDO: Comment this...
     */
    public ShapeJVertex(Shape shape, ShapeNode node, Options options) {
        super(null);
        this.shape = shape;
        this.node = node;
        this.setUserObject(getLines(options));
        this.setAttributes();
    }

    private String getLines(Options options) {
        StringBuilder result = new StringBuilder();
        if (options != null
            && options.getValue(Options.SHOW_NODE_IDS_OPTION) == 1) {
            int nr = this.node.getNumber();
            result.append(ITALIC_TAG.on("n" + nr));
            result.append(Converter.HTML_LINEBREAK);
        }
        String mult = this.shape.getNodeMult(this.node).toString();
        result.append(Converter.createSpanTag("color: rgb(50,50,255)").on(
            ITALIC_TAG.on(mult)));
        Set<TypeLabel> labels = Util.getNodeLabels(this.shape, this.node);
        for (TypeLabel label : labels) {
            result.append(Converter.HTML_LINEBREAK);
            result.append(label.toString());
        }
        return HTML_TAG.on(result).toString();
    }

    private void setAttributes() {
        AttributeMap attrMap = this.getAttributes();
        GraphConstants.setBounds(attrMap, new Rectangle2D.Double(20, 20, 0, 0));
        GraphConstants.setAutoSize(attrMap, true);
        GraphConstants.setBackground(attrMap, Color.LIGHT_GRAY);
        GraphConstants.setOpaque(attrMap, true);
        GraphConstants.setInset(attrMap, 3);
        GraphConstants.setBorder(attrMap, JAttr.DEFAULT_BORDER);
    }

}
