/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.gui.jgraphx;

import java.util.HashMap;
import java.util.Map;

import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public final class JGraphXStyles {

    public static final String READER_NODE_STYLE = "readerNode";
    public static final String READER_EDGE_STYLE = "readerEdge";
    public static final String CREATOR_NODE_STYLE = "creatorNode";
    public static final String CREATOR_EDGE_STYLE = "creatorEdge";
    public static final String ERASER_NODE_STYLE = "eraserNode";
    public static final String ERASER_EDGE_STYLE = "eraserEdge";

    public static final mxStylesheet ruleStylesheet;

    static {
        ruleStylesheet = new mxStylesheet();

        HashMap<String,Object> skelNodeStyle = new HashMap<String,Object>();
        skelNodeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        skelNodeStyle.put(mxConstants.STYLE_OPACITY, 90);

        Map<String,Object> readerNodeStyle =
            (Map<String,Object>) skelNodeStyle.clone();
        readerNodeStyle.put(mxConstants.STYLE_FONTCOLOR, "black");
        readerNodeStyle.put(mxConstants.STYLE_STROKECOLOR, "black");
        readerNodeStyle.put(mxConstants.STYLE_FILLCOLOR, "gray");
        ruleStylesheet.putCellStyle(READER_NODE_STYLE, readerNodeStyle);

        Map<String,Object> creatorNodeStyle =
            (Map<String,Object>) skelNodeStyle.clone();
        creatorNodeStyle.put(mxConstants.STYLE_FONTCOLOR, "green");
        creatorNodeStyle.put(mxConstants.STYLE_STROKECOLOR, "green");
        creatorNodeStyle.put(mxConstants.STYLE_FILLCOLOR, "green");
        ruleStylesheet.putCellStyle(CREATOR_NODE_STYLE, creatorNodeStyle);

        Map<String,Object> eraserNodeStyle =
            (Map<String,Object>) skelNodeStyle.clone();
        eraserNodeStyle.put(mxConstants.STYLE_FONTCOLOR, "blue");
        eraserNodeStyle.put(mxConstants.STYLE_STROKECOLOR, "blue");
        eraserNodeStyle.put(mxConstants.STYLE_FILLCOLOR, "blue");
        ruleStylesheet.putCellStyle(ERASER_NODE_STYLE, eraserNodeStyle);

        HashMap<String,Object> skelEdgeStyle = new HashMap<String,Object>();
        skelEdgeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
        skelEdgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        skelEdgeStyle.put(mxConstants.STYLE_VERTICAL_ALIGN,
            mxConstants.ALIGN_MIDDLE);
        skelEdgeStyle.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);

        Map<String,Object> readerEdgeStyle =
            (Map<String,Object>) skelEdgeStyle.clone();
        readerEdgeStyle.put(mxConstants.STYLE_STROKECOLOR, "black");
        readerEdgeStyle.put(mxConstants.STYLE_FONTCOLOR, "black");
        ruleStylesheet.putCellStyle(READER_EDGE_STYLE, readerEdgeStyle);

        Map<String,Object> creatorEdgeStyle =
            (Map<String,Object>) skelEdgeStyle.clone();
        readerEdgeStyle.put(mxConstants.STYLE_STROKECOLOR, "green");
        readerEdgeStyle.put(mxConstants.STYLE_FONTCOLOR, "green");
        ruleStylesheet.putCellStyle(CREATOR_EDGE_STYLE, creatorEdgeStyle);

        Map<String,Object> eraserEdgeStyle =
            (Map<String,Object>) skelEdgeStyle.clone();
        readerEdgeStyle.put(mxConstants.STYLE_STROKECOLOR, "blue");
        readerEdgeStyle.put(mxConstants.STYLE_FONTCOLOR, "blue");
        ruleStylesheet.putCellStyle(ERASER_EDGE_STYLE, eraserEdgeStyle);
    }

}
