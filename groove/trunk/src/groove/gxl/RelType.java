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
 * $Id: RelType.java,v 1.1.1.2 2007-03-20 10:42:49 kastenberg Exp $
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
 * Class RelType.
 * 
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:49 $
 */
public class RelType extends groove.gxl.LocalConnectionType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _relendList
     */
    private java.util.Vector _relendList;


      //----------------/
     //- Constructors -/
    //----------------/

    public RelType() {
        super();
        _relendList = new Vector();
    } //-- groove.gxl.RelType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addRelend
     * 
     * @param vRelend
     */
    public void addRelend(groove.gxl.Relend vRelend)
        throws java.lang.IndexOutOfBoundsException
    {
        _relendList.addElement(vRelend);
    } //-- void addRelend(groove.gxl.Relend) 

    /**
     * Method addRelend
     * 
     * @param index
     * @param vRelend
     */
    public void addRelend(int index, groove.gxl.Relend vRelend)
        throws java.lang.IndexOutOfBoundsException
    {
        _relendList.insertElementAt(vRelend, index);
    } //-- void addRelend(int, groove.gxl.Relend) 

    /**
     * Method enumerateRelend
     */
    public java.util.Enumeration enumerateRelend()
    {
        return _relendList.elements();
    } //-- java.util.Enumeration enumerateRelend() 

    /**
     * Method getRelend
     * 
     * @param index
     */
    public groove.gxl.Relend getRelend(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _relendList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (groove.gxl.Relend) _relendList.elementAt(index);
    } //-- groove.gxl.Relend getRelend(int) 

    /**
     * Method getRelend
     */
    public groove.gxl.Relend[] getRelend()
    {
        int size = _relendList.size();
        groove.gxl.Relend[] mArray = new groove.gxl.Relend[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (groove.gxl.Relend) _relendList.elementAt(index);
        }
        return mArray;
    } //-- groove.gxl.Relend[] getRelend() 

    /**
     * Method getRelendCount
     */
    public int getRelendCount()
    {
        return _relendList.size();
    } //-- int getRelendCount() 

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
     * Method removeAllRelend
     */
    public void removeAllRelend()
    {
        _relendList.removeAllElements();
    } //-- void removeAllRelend() 

    /**
     * Method removeRelend
     * 
     * @param index
     */
    public groove.gxl.Relend removeRelend(int index)
    {
        java.lang.Object obj = _relendList.elementAt(index);
        _relendList.removeElementAt(index);
        return (groove.gxl.Relend) obj;
    } //-- groove.gxl.Relend removeRelend(int) 

    /**
     * Method setRelend
     * 
     * @param index
     * @param vRelend
     */
    public void setRelend(int index, groove.gxl.Relend vRelend)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _relendList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _relendList.setElementAt(vRelend, index);
    } //-- void setRelend(int, groove.gxl.Relend) 

    /**
     * Method setRelend
     * 
     * @param relendArray
     */
    public void setRelend(groove.gxl.Relend[] relendArray)
    {
        //-- copy array
        _relendList.removeAllElements();
        for (int i = 0; i < relendArray.length; i++) {
            _relendList.addElement(relendArray[i]);
        }
    } //-- void setRelend(groove.gxl.Relend) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (groove.gxl.RelType) Unmarshaller.unmarshal(groove.gxl.RelType.class, reader);
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
