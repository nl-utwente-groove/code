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
 * $Id: EdgeType.java,v 1.1.1.2 2007-03-20 10:42:48 kastenberg Exp $
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
 * Class EdgeType.
 * 
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:48 $
 */
public class EdgeType extends groove.gxl.LocalConnectionType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _from
     */
    private java.lang.Object _from;

    /**
     * Field _to
     */
    private java.lang.Object _to;

    /**
     * Field _fromorder
     */
    private int _fromorder;

    /**
     * keeps track of state for field: _fromorder
     */
    private boolean _has_fromorder;

    /**
     * Field _toorder
     */
    private int _toorder;

    /**
     * keeps track of state for field: _toorder
     */
    private boolean _has_toorder;


      //----------------/
     //- Constructors -/
    //----------------/

    public EdgeType() {
        super();
    } //-- groove.gxl.EdgeType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteFromorder
     */
    public void deleteFromorder()
    {
        this._has_fromorder= false;
    } //-- void deleteFromorder() 

    /**
     * Method deleteToorder
     */
    public void deleteToorder()
    {
        this._has_toorder= false;
    } //-- void deleteToorder() 

    /**
     * Returns the value of field 'from'.
     * 
     * @return the value of field 'from'.
     */
    public java.lang.Object getFrom()
    {
        return this._from;
    } //-- java.lang.Object getFrom() 

    /**
     * Returns the value of field 'fromorder'.
     * 
     * @return the value of field 'fromorder'.
     */
    public int getFromorder()
    {
        return this._fromorder;
    } //-- int getFromorder() 

    /**
     * Returns the value of field 'to'.
     * 
     * @return the value of field 'to'.
     */
    public java.lang.Object getTo()
    {
        return this._to;
    } //-- java.lang.Object getTo() 

    /**
     * Returns the value of field 'toorder'.
     * 
     * @return the value of field 'toorder'.
     */
    public int getToorder()
    {
        return this._toorder;
    } //-- int getToorder() 

    /**
     * Method hasFromorder
     */
    public boolean hasFromorder()
    {
        return this._has_fromorder;
    } //-- boolean hasFromorder() 

    /**
     * Method hasToorder
     */
    public boolean hasToorder()
    {
        return this._has_toorder;
    } //-- boolean hasToorder() 

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
     * Sets the value of field 'from'.
     * 
     * @param from the value of field 'from'.
     */
    public void setFrom(java.lang.Object from)
    {
        this._from = from;
    } //-- void setFrom(java.lang.Object) 

    /**
     * Sets the value of field 'fromorder'.
     * 
     * @param fromorder the value of field 'fromorder'.
     */
    public void setFromorder(int fromorder)
    {
        this._fromorder = fromorder;
        this._has_fromorder = true;
    } //-- void setFromorder(int) 

    /**
     * Sets the value of field 'to'.
     * 
     * @param to the value of field 'to'.
     */
    public void setTo(java.lang.Object to)
    {
        this._to = to;
    } //-- void setTo(java.lang.Object) 

    /**
     * Sets the value of field 'toorder'.
     * 
     * @param toorder the value of field 'toorder'.
     */
    public void setToorder(int toorder)
    {
        this._toorder = toorder;
        this._has_toorder = true;
    } //-- void setToorder(int) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (groove.gxl.EdgeType) Unmarshaller.unmarshal(groove.gxl.EdgeType.class, reader);
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
