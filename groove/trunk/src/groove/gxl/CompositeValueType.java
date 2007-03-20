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
 * $Id: CompositeValueType.java,v 1.1.1.2 2007-03-20 10:42:48 kastenberg Exp $
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
 * Class CompositeValueType.
 * 
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:48 $
 */
public class CompositeValueType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _items
     */
    private java.util.Vector _items;


      //----------------/
     //- Constructors -/
    //----------------/

    public CompositeValueType() {
        super();
        _items = new Vector();
    } //-- groove.gxl.CompositeValueType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addCompositeValueTypeItem
     * 
     * @param vCompositeValueTypeItem
     */
    public void addCompositeValueTypeItem(groove.gxl.CompositeValueTypeItem vCompositeValueTypeItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.addElement(vCompositeValueTypeItem);
    } //-- void addCompositeValueTypeItem(groove.gxl.CompositeValueTypeItem) 

    /**
     * Method addCompositeValueTypeItem
     * 
     * @param index
     * @param vCompositeValueTypeItem
     */
    public void addCompositeValueTypeItem(int index, groove.gxl.CompositeValueTypeItem vCompositeValueTypeItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.insertElementAt(vCompositeValueTypeItem, index);
    } //-- void addCompositeValueTypeItem(int, groove.gxl.CompositeValueTypeItem) 

    /**
     * Method enumerateCompositeValueTypeItem
     */
    public java.util.Enumeration enumerateCompositeValueTypeItem()
    {
        return _items.elements();
    } //-- java.util.Enumeration enumerateCompositeValueTypeItem() 

    /**
     * Method getCompositeValueTypeItem
     * 
     * @param index
     */
    public groove.gxl.CompositeValueTypeItem getCompositeValueTypeItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (groove.gxl.CompositeValueTypeItem) _items.elementAt(index);
    } //-- groove.gxl.CompositeValueTypeItem getCompositeValueTypeItem(int) 

    /**
     * Method getCompositeValueTypeItem
     */
    public groove.gxl.CompositeValueTypeItem[] getCompositeValueTypeItem()
    {
        int size = _items.size();
        groove.gxl.CompositeValueTypeItem[] mArray = new groove.gxl.CompositeValueTypeItem[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (groove.gxl.CompositeValueTypeItem) _items.elementAt(index);
        }
        return mArray;
    } //-- groove.gxl.CompositeValueTypeItem[] getCompositeValueTypeItem() 

    /**
     * Method getCompositeValueTypeItemCount
     */
    public int getCompositeValueTypeItemCount()
    {
        return _items.size();
    } //-- int getCompositeValueTypeItemCount() 

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
     * Method removeAllCompositeValueTypeItem
     */
    public void removeAllCompositeValueTypeItem()
    {
        _items.removeAllElements();
    } //-- void removeAllCompositeValueTypeItem() 

    /**
     * Method removeCompositeValueTypeItem
     * 
     * @param index
     */
    public groove.gxl.CompositeValueTypeItem removeCompositeValueTypeItem(int index)
    {
        java.lang.Object obj = _items.elementAt(index);
        _items.removeElementAt(index);
        return (groove.gxl.CompositeValueTypeItem) obj;
    } //-- groove.gxl.CompositeValueTypeItem removeCompositeValueTypeItem(int) 

    /**
     * Method setCompositeValueTypeItem
     * 
     * @param index
     * @param vCompositeValueTypeItem
     */
    public void setCompositeValueTypeItem(int index, groove.gxl.CompositeValueTypeItem vCompositeValueTypeItem)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException();
        }
        _items.setElementAt(vCompositeValueTypeItem, index);
    } //-- void setCompositeValueTypeItem(int, groove.gxl.CompositeValueTypeItem) 

    /**
     * Method setCompositeValueTypeItem
     * 
     * @param compositeValueTypeItemArray
     */
    public void setCompositeValueTypeItem(groove.gxl.CompositeValueTypeItem[] compositeValueTypeItemArray)
    {
        //-- copy array
        _items.removeAllElements();
        for (int i = 0; i < compositeValueTypeItemArray.length; i++) {
            _items.addElement(compositeValueTypeItemArray[i]);
        }
    } //-- void setCompositeValueTypeItem(groove.gxl.CompositeValueTypeItem) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (groove.gxl.CompositeValueType) Unmarshaller.unmarshal(groove.gxl.CompositeValueType.class, reader);
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
