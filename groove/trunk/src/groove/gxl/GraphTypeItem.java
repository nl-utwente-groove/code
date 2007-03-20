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
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.5.2</a>, using an XML
 * Schema.
 * $Id: GraphTypeItem.java,v 1.1.1.2 2007-03-20 10:42:48 kastenberg Exp $
 */

package groove.gxl;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class GraphTypeItem.
 * 
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:48 $
 */
public class GraphTypeItem implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _node
     */
    private groove.gxl.Node _node;

    /**
     * Field _edge
     */
    private groove.gxl.Edge _edge;

    /**
     * Field _rel
     */
    private groove.gxl.Rel _rel;


      //----------------/
     //- Constructors -/
    //----------------/

    public GraphTypeItem() {
        super();
    } //-- groove.gxl.GraphTypeItem()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'edge'.
     * 
     * @return the value of field 'edge'.
     */
    public groove.gxl.Edge getEdge()
    {
        return this._edge;
    } //-- groove.gxl.Edge getEdge() 

    /**
     * Returns the value of field 'node'.
     * 
     * @return the value of field 'node'.
     */
    public groove.gxl.Node getNode()
    {
        return this._node;
    } //-- groove.gxl.Node getNode() 

    /**
     * Returns the value of field 'rel'.
     * 
     * @return the value of field 'rel'.
     */
    public groove.gxl.Rel getRel()
    {
        return this._rel;
    } //-- groove.gxl.Rel getRel() 

    /**
     * Sets the value of field 'edge'.
     * 
     * @param edge the value of field 'edge'.
     */
    public void setEdge(groove.gxl.Edge edge)
    {
        this._edge = edge;
    } //-- void setEdge(groove.gxl.Edge) 

    /**
     * Sets the value of field 'node'.
     * 
     * @param node the value of field 'node'.
     */
    public void setNode(groove.gxl.Node node)
    {
        this._node = node;
    } //-- void setNode(groove.gxl.Node) 

    /**
     * Sets the value of field 'rel'.
     * 
     * @param rel the value of field 'rel'.
     */
    public void setRel(groove.gxl.Rel rel)
    {
        this._rel = rel;
    } //-- void setRel(groove.gxl.Rel) 

}
