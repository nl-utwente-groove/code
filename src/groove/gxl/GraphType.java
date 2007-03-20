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
 * $Id: GraphType.java,v 1.1.1.2 2007-03-20 10:42:48 kastenberg Exp $
 */

package groove.gxl;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import groove.gxl.types.EdgemodeType;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class GraphType.
 * 
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:48 $
 */
public class GraphType extends groove.gxl.TypedElementType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _role
     */
    private java.lang.String _role;

    /**
     * Field _edgeids
     */
    private boolean _edgeids = false;

    /**
     * keeps track of state for field: _edgeids
     */
    private boolean _has_edgeids;

    /**
     * Field _hypergraph
     */
    private boolean _hypergraph = false;

    /**
     * keeps track of state for field: _hypergraph
     */
    private boolean _has_hypergraph;

    /**
     * Field _edgemode
     */
    private groove.gxl.types.EdgemodeType _edgemode = groove.gxl.types.EdgemodeType.valueOf("directed");

    /**
     * Field _items
     */
    private java.util.Vector _items;


      //----------------/
     //- Constructors -/
    //----------------/

    public GraphType() {
        super();
        setEdgemode(groove.gxl.types.EdgemodeType.valueOf("directed"));
        _items = new Vector();
    } //-- groove.gxl.GraphType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addGraphTypeItem
     * 
     * @param vGraphTypeItem
     */
    public void addGraphTypeItem(groove.gxl.GraphTypeItem vGraphTypeItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.addElement(vGraphTypeItem);
    } //-- void addGraphTypeItem(groove.gxl.GraphTypeItem) 

    /**
     * Method addGraphTypeItem
     * 
     * @param index
     * @param vGraphTypeItem
     */
    public void addGraphTypeItem(int index, groove.gxl.GraphTypeItem vGraphTypeItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.insertElementAt(vGraphTypeItem, index);
    } //-- void addGraphTypeItem(int, groove.gxl.GraphTypeItem) 

    /**
     * Method deleteEdgeids
     */
    public void deleteEdgeids()
    {
        this._has_edgeids= false;
    } //-- void deleteEdgeids() 

    /**
     * Method deleteHypergraph
     */
    public void deleteHypergraph()
    {
        this._has_hypergraph= false;
    } //-- void deleteHypergraph() 

    /**
     * Method enumerateGraphTypeItem
     */
    public java.util.Enumeration enumerateGraphTypeItem()
    {
        return _items.elements();
    } //-- java.util.Enumeration enumerateGraphTypeItem() 

    /**
     * Returns the value of field 'edgeids'.
     * 
     * @return the value of field 'edgeids'.
     */
    public boolean getEdgeids()
    {
        return this._edgeids;
    } //-- boolean getEdgeids() 

    /**
     * Returns the value of field 'edgemode'.
     * 
     * @return the value of field 'edgemode'.
     */
    public groove.gxl.types.EdgemodeType getEdgemode()
    {
        return this._edgemode;
    } //-- groove.gxl.types.EdgemodeType getEdgemode() 

    /**
     * Method getGraphTypeItem
     * 
     * @param index
     */
    public groove.gxl.GraphTypeItem getGraphTypeItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (groove.gxl.GraphTypeItem) _items.elementAt(index);
    } //-- groove.gxl.GraphTypeItem getGraphTypeItem(int) 

    /**
     * Method getGraphTypeItem
     */
    public groove.gxl.GraphTypeItem[] getGraphTypeItem()
    {
        int size = _items.size();
        groove.gxl.GraphTypeItem[] mArray = new groove.gxl.GraphTypeItem[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (groove.gxl.GraphTypeItem) _items.elementAt(index);
        }
        return mArray;
    } //-- groove.gxl.GraphTypeItem[] getGraphTypeItem() 

    /**
     * Method getGraphTypeItemCount
     */
    public int getGraphTypeItemCount()
    {
        return _items.size();
    } //-- int getGraphTypeItemCount() 

    /**
     * Returns the value of field 'hypergraph'.
     * 
     * @return the value of field 'hypergraph'.
     */
    public boolean getHypergraph()
    {
        return this._hypergraph;
    } //-- boolean getHypergraph() 

    /**
     * Returns the value of field 'role'.
     * 
     * @return the value of field 'role'.
     */
    public java.lang.String getRole()
    {
        return this._role;
    } //-- java.lang.String getRole() 

    /**
     * Method hasEdgeids
     */
    public boolean hasEdgeids()
    {
        return this._has_edgeids;
    } //-- boolean hasEdgeids() 

    /**
     * Method hasHypergraph
     */
    public boolean hasHypergraph()
    {
        return this._has_hypergraph;
    } //-- boolean hasHypergraph() 

    /**
     * Method isValid
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method removeAllGraphTypeItem
     */
    public void removeAllGraphTypeItem()
    {
        _items.removeAllElements();
    } //-- void removeAllGraphTypeItem() 

    /**
     * Method removeGraphTypeItem
     * 
     * @param index
     */
    public groove.gxl.GraphTypeItem removeGraphTypeItem(int index)
    {
        java.lang.Object obj = _items.elementAt(index);
        _items.removeElementAt(index);
        return (groove.gxl.GraphTypeItem) obj;
    } //-- groove.gxl.GraphTypeItem removeGraphTypeItem(int) 

    /**
     * Sets the value of field 'edgeids'.
     * 
     * @param edgeids the value of field 'edgeids'.
     */
    public void setEdgeids(boolean edgeids)
    {
        this._edgeids = edgeids;
        this._has_edgeids = true;
    } //-- void setEdgeids(boolean) 

    /**
     * Sets the value of field 'edgemode'.
     * 
     * @param edgemode the value of field 'edgemode'.
     */
    public void setEdgemode(groove.gxl.types.EdgemodeType edgemode)
    {
        this._edgemode = edgemode;
    } //-- void setEdgemode(groove.gxl.types.EdgemodeType) 

    /**
     * Method setGraphTypeItem
     * 
     * @param index
     * @param vGraphTypeItem
     */
    public void setGraphTypeItem(int index, groove.gxl.GraphTypeItem vGraphTypeItem)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException();
        }
        _items.setElementAt(vGraphTypeItem, index);
    } //-- void setGraphTypeItem(int, groove.gxl.GraphTypeItem) 

    /**
     * Method setGraphTypeItem
     * 
     * @param graphTypeItemArray
     */
    public void setGraphTypeItem(groove.gxl.GraphTypeItem[] graphTypeItemArray)
    {
        //-- copy array
        _items.removeAllElements();
        for (int i = 0; i < graphTypeItemArray.length; i++) {
            _items.addElement(graphTypeItemArray[i]);
        }
    } //-- void setGraphTypeItem(groove.gxl.GraphTypeItem) 

    /**
     * Sets the value of field 'hypergraph'.
     * 
     * @param hypergraph the value of field 'hypergraph'.
     */
    public void setHypergraph(boolean hypergraph)
    {
        this._hypergraph = hypergraph;
        this._has_hypergraph = true;
    } //-- void setHypergraph(boolean) 

    /**
     * Sets the value of field 'role'.
     * 
     * @param role the value of field 'role'.
     */
    public void setRole(java.lang.String role)
    {
        this._role = role;
    } //-- void setRole(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (groove.gxl.GraphType) Unmarshaller.unmarshal(groove.gxl.GraphType.class, reader);
    } //-- java.lang.Object unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
