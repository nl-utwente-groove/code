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
 * $Id: GraphElementType.java,v 1.1.1.1 2007-03-20 10:05:27 kastenberg Exp $
 */

package groove.gxl;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

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
 * Class GraphElementType.
 * 
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:27 $
 */
public class GraphElementType extends groove.gxl.TypedElementType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _graphList
     */
    private java.util.Vector _graphList;


      //----------------/
     //- Constructors -/
    //----------------/

    public GraphElementType() {
        super();
        _graphList = new Vector();
    } //-- groove.gxl.GraphElementType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addGraph
     * 
     * @param vGraph
     */
    public void addGraph(groove.gxl.Graph vGraph)
        throws java.lang.IndexOutOfBoundsException
    {
        _graphList.addElement(vGraph);
    } //-- void addGraph(groove.gxl.Graph) 

    /**
     * Method addGraph
     * 
     * @param index
     * @param vGraph
     */
    public void addGraph(int index, groove.gxl.Graph vGraph)
        throws java.lang.IndexOutOfBoundsException
    {
        _graphList.insertElementAt(vGraph, index);
    } //-- void addGraph(int, groove.gxl.Graph) 

    /**
     * Method enumerateGraph
     */
    public java.util.Enumeration enumerateGraph()
    {
        return _graphList.elements();
    } //-- java.util.Enumeration enumerateGraph() 

    /**
     * Method getGraph
     * 
     * @param index
     */
    public groove.gxl.Graph getGraph(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _graphList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (groove.gxl.Graph) _graphList.elementAt(index);
    } //-- groove.gxl.Graph getGraph(int) 

    /**
     * Method getGraph
     */
    public groove.gxl.Graph[] getGraph()
    {
        int size = _graphList.size();
        groove.gxl.Graph[] mArray = new groove.gxl.Graph[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (groove.gxl.Graph) _graphList.elementAt(index);
        }
        return mArray;
    } //-- groove.gxl.Graph[] getGraph() 

    /**
     * Method getGraphCount
     */
    public int getGraphCount()
    {
        return _graphList.size();
    } //-- int getGraphCount() 

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
     * Method removeAllGraph
     */
    public void removeAllGraph()
    {
        _graphList.removeAllElements();
    } //-- void removeAllGraph() 

    /**
     * Method removeGraph
     * 
     * @param index
     */
    public groove.gxl.Graph removeGraph(int index)
    {
        java.lang.Object obj = _graphList.elementAt(index);
        _graphList.removeElementAt(index);
        return (groove.gxl.Graph) obj;
    } //-- groove.gxl.Graph removeGraph(int) 

    /**
     * Method setGraph
     * 
     * @param index
     * @param vGraph
     */
    public void setGraph(int index, groove.gxl.Graph vGraph)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _graphList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _graphList.setElementAt(vGraph, index);
    } //-- void setGraph(int, groove.gxl.Graph) 

    /**
     * Method setGraph
     * 
     * @param graphArray
     */
    public void setGraph(groove.gxl.Graph[] graphArray)
    {
        //-- copy array
        _graphList.removeAllElements();
        for (int i = 0; i < graphArray.length; i++) {
            _graphList.addElement(graphArray[i]);
        }
    } //-- void setGraph(groove.gxl.Graph) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (groove.gxl.GraphElementType) Unmarshaller.unmarshal(groove.gxl.GraphElementType.class, reader);
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
