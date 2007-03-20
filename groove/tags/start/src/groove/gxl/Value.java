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
 * $Id: Value.java,v 1.1.1.2 2007-03-20 10:42:50 kastenberg Exp $
 */

package groove.gxl;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class Value.
 * 
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:50 $
 */
public class Value implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _bag
     */
    private groove.gxl.Bag _bag;

    /**
     * Field _set
     */
    private groove.gxl.Set _set;

    /**
     * Field _seq
     */
    private groove.gxl.Seq _seq;

    /**
     * Field _tup
     */
    private groove.gxl.Tup _tup;

    /**
     * Field _bool
     */
    private boolean _bool;

    /**
     * keeps track of state for field: _bool
     */
    private boolean _has_bool;

    /**
     * Field _int
     */
    private int _int;

    /**
     * keeps track of state for field: _int
     */
    private boolean _has_int;

    /**
     * Field _float
     */
    private float _float;

    /**
     * keeps track of state for field: _float
     */
    private boolean _has_float;

    /**
     * Field _string
     */
    private java.lang.String _string;

    /**
     * Field _locator
     */
    private groove.gxl.Locator _locator;

    /**
     * Field _enum
     */
    private java.lang.String _enum;


      //----------------/
     //- Constructors -/
    //----------------/

    public Value() {
        super();
    } //-- groove.gxl.Value()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'bag'.
     * 
     * @return the value of field 'bag'.
     */
    public groove.gxl.Bag getBag()
    {
        return this._bag;
    } //-- groove.gxl.Bag getBag() 

    /**
     * Returns the value of field 'bool'.
     * 
     * @return the value of field 'bool'.
     */
    public boolean getBool()
    {
        return this._bool;
    } //-- boolean getBool() 

    /**
     * Returns the value of field 'enum'.
     * 
     * @return the value of field 'enum'.
     */
    public java.lang.String getEnum()
    {
        return this._enum;
    } //-- java.lang.String getEnum() 

    /**
     * Returns the value of field 'float'.
     * 
     * @return the value of field 'float'.
     */
    public float getFloat()
    {
        return this._float;
    } //-- float getFloat() 

    /**
     * Returns the value of field 'int'.
     * 
     * @return the value of field 'int'.
     */
    public int getInt()
    {
        return this._int;
    } //-- int getInt() 

    /**
     * Returns the value of field 'locator'.
     * 
     * @return the value of field 'locator'.
     */
    public groove.gxl.Locator getLocator()
    {
        return this._locator;
    } //-- groove.gxl.Locator getLocator() 

    /**
     * Returns the value of field 'seq'.
     * 
     * @return the value of field 'seq'.
     */
    public groove.gxl.Seq getSeq()
    {
        return this._seq;
    } //-- groove.gxl.Seq getSeq() 

    /**
     * Returns the value of field 'set'.
     * 
     * @return the value of field 'set'.
     */
    public groove.gxl.Set getSet()
    {
        return this._set;
    } //-- groove.gxl.Set getSet() 

    /**
     * Returns the value of field 'string'.
     * 
     * @return the value of field 'string'.
     */
    public java.lang.String getString()
    {
        return this._string;
    } //-- java.lang.String getString() 

    /**
     * Returns the value of field 'tup'.
     * 
     * @return the value of field 'tup'.
     */
    public groove.gxl.Tup getTup()
    {
        return this._tup;
    } //-- groove.gxl.Tup getTup() 

    /**
     * Method hasBool
     */
    public boolean hasBool()
    {
        return this._has_bool;
    } //-- boolean hasBool() 

    /**
     * Method hasFloat
     */
    public boolean hasFloat()
    {
        return this._has_float;
    } //-- boolean hasFloat() 

    /**
     * Method hasInt
     */
    public boolean hasInt()
    {
        return this._has_int;
    } //-- boolean hasInt() 

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
     * Sets the value of field 'bag'.
     * 
     * @param bag the value of field 'bag'.
     */
    public void setBag(groove.gxl.Bag bag)
    {
        this._bag = bag;
    } //-- void setBag(groove.gxl.Bag) 

    /**
     * Sets the value of field 'bool'.
     * 
     * @param bool the value of field 'bool'.
     */
    public void setBool(boolean bool)
    {
        this._bool = bool;
        this._has_bool = true;
    } //-- void setBool(boolean) 

    /**
     * Sets the value of field 'enum'.
     * 
     * @param enum1 the value of field 'enum'.
     */
    public void setEnum(java.lang.String enum1)
    {
        this._enum = enum1;
    } //-- void setEnum(java.lang.String) 

    /**
     * Sets the value of field 'float'.
     * 
     * @param _float the value of field 'float'.
     */
    public void setFloat(float _float)
    {
        this._float = _float;
        this._has_float = true;
    } //-- void setFloat(float) 

    /**
     * Sets the value of field 'int'.
     * 
     * @param _int the value of field 'int'.
     */
    public void setInt(int _int)
    {
        this._int = _int;
        this._has_int = true;
    } //-- void setInt(int) 

    /**
     * Sets the value of field 'locator'.
     * 
     * @param locator the value of field 'locator'.
     */
    public void setLocator(groove.gxl.Locator locator)
    {
        this._locator = locator;
    } //-- void setLocator(groove.gxl.Locator) 

    /**
     * Sets the value of field 'seq'.
     * 
     * @param seq the value of field 'seq'.
     */
    public void setSeq(groove.gxl.Seq seq)
    {
        this._seq = seq;
    } //-- void setSeq(groove.gxl.Seq) 

    /**
     * Sets the value of field 'set'.
     * 
     * @param set the value of field 'set'.
     */
    public void setSet(groove.gxl.Set set)
    {
        this._set = set;
    } //-- void setSet(groove.gxl.Set) 

    /**
     * Sets the value of field 'string'.
     * 
     * @param string the value of field 'string'.
     */
    public void setString(java.lang.String string)
    {
        this._string = string;
    } //-- void setString(java.lang.String) 

    /**
     * Sets the value of field 'tup'.
     * 
     * @param tup the value of field 'tup'.
     */
    public void setTup(groove.gxl.Tup tup)
    {
        this._tup = tup;
    } //-- void setTup(groove.gxl.Tup) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (groove.gxl.Value) Unmarshaller.unmarshal(groove.gxl.Value.class, reader);
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
