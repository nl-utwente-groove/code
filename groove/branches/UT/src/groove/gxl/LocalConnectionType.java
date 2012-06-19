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
 * $Id: LocalConnectionType.java,v 1.1.1.2 2007-03-20 10:42:49 kastenberg Exp $
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
 * Class LocalConnectionType.
 * 
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:49 $
 */
public class LocalConnectionType extends groove.gxl.GraphElementType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _isDirected
     */
    private boolean _isDirected;

    /**
     * keeps track of state for field: _isDirected
     */
    private boolean _has_isDirected;


      //----------------/
     //- Constructors -/
    //----------------/

    public LocalConnectionType() {
        super();
    } //-- groove.gxl.LocalConnectionType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteIsDirected
     */
    public void deleteIsDirected()
    {
        this._has_isDirected= false;
    } //-- void deleteIsDirected() 

    /**
     * Returns the value of field 'isDirected'.
     * 
     * @return the value of field 'isDirected'.
     */
    public boolean getIsDirected()
    {
        return this._isDirected;
    } //-- boolean getIsDirected() 

    /**
     * Method hasIsDirected
     */
    public boolean hasIsDirected()
    {
        return this._has_isDirected;
    } //-- boolean hasIsDirected() 

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
     * Sets the value of field 'isDirected'.
     * 
     * @param isDirected the value of field 'isDirected'.
     */
    public void setIsDirected(boolean isDirected)
    {
        this._isDirected = isDirected;
        this._has_isDirected = true;
    } //-- void setIsDirected(boolean) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (groove.gxl.LocalConnectionType) Unmarshaller.unmarshal(groove.gxl.LocalConnectionType.class, reader);
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