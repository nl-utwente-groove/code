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
 * $Id: TypedElementType.java,v 1.1.1.1 2007-03-20 10:05:29 kastenberg Exp $
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
 * Class TypedElementType.
 * 
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:29 $
 */
public class TypedElementType extends groove.gxl.AttributedElementType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _type
     */
    private groove.gxl.Type _type;

    /**
     * Field _attrList
     */
    private java.util.Vector _attrList;


      //----------------/
     //- Constructors -/
    //----------------/

    public TypedElementType() {
        super();
        _attrList = new Vector();
    } //-- groove.gxl.TypedElementType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addAttr
     * 
     * @param vAttr
     */
    public void addAttr(groove.gxl.Attr vAttr)
        throws java.lang.IndexOutOfBoundsException
    {
        _attrList.addElement(vAttr);
    } //-- void addAttr(groove.gxl.Attr) 

    /**
     * Method addAttr
     * 
     * @param index
     * @param vAttr
     */
    public void addAttr(int index, groove.gxl.Attr vAttr)
        throws java.lang.IndexOutOfBoundsException
    {
        _attrList.insertElementAt(vAttr, index);
    } //-- void addAttr(int, groove.gxl.Attr) 

    /**
     * Method enumerateAttr
     */
    public java.util.Enumeration enumerateAttr()
    {
        return _attrList.elements();
    } //-- java.util.Enumeration enumerateAttr() 

    /**
     * Method getAttr
     * 
     * @param index
     */
    public groove.gxl.Attr getAttr(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _attrList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (groove.gxl.Attr) _attrList.elementAt(index);
    } //-- groove.gxl.Attr getAttr(int) 

    /**
     * Method getAttr
     */
    public groove.gxl.Attr[] getAttr()
    {
        int size = _attrList.size();
        groove.gxl.Attr[] mArray = new groove.gxl.Attr[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (groove.gxl.Attr) _attrList.elementAt(index);
        }
        return mArray;
    } //-- groove.gxl.Attr[] getAttr() 

    /**
     * Method getAttrCount
     */
    public int getAttrCount()
    {
        return _attrList.size();
    } //-- int getAttrCount() 

    /**
     * Returns the value of field 'type'.
     * 
     * @return the value of field 'type'.
     */
    public groove.gxl.Type getType()
    {
        return this._type;
    } //-- groove.gxl.Type getType() 

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
     * Method removeAllAttr
     */
    public void removeAllAttr()
    {
        _attrList.removeAllElements();
    } //-- void removeAllAttr() 

    /**
     * Method removeAttr
     * 
     * @param index
     */
    public groove.gxl.Attr removeAttr(int index)
    {
        java.lang.Object obj = _attrList.elementAt(index);
        _attrList.removeElementAt(index);
        return (groove.gxl.Attr) obj;
    } //-- groove.gxl.Attr removeAttr(int) 

    /**
     * Method setAttr
     * 
     * @param index
     * @param vAttr
     */
    public void setAttr(int index, groove.gxl.Attr vAttr)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _attrList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _attrList.setElementAt(vAttr, index);
    } //-- void setAttr(int, groove.gxl.Attr) 

    /**
     * Method setAttr
     * 
     * @param attrArray
     */
    public void setAttr(groove.gxl.Attr[] attrArray)
    {
        //-- copy array
        _attrList.removeAllElements();
        for (int i = 0; i < attrArray.length; i++) {
            _attrList.addElement(attrArray[i]);
        }
    } //-- void setAttr(groove.gxl.Attr) 

    /**
     * Sets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(groove.gxl.Type type)
    {
        this._type = type;
    } //-- void setType(groove.gxl.Type) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (groove.gxl.TypedElementType) Unmarshaller.unmarshal(groove.gxl.TypedElementType.class, reader);
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
