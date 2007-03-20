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
 * $Id: RelendType.java,v 1.1.1.2 2007-03-20 10:42:49 kastenberg Exp $
 */

package groove.gxl;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import groove.gxl.types.DirectionType;
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
 * Class RelendType.
 * 
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:49 $
 */
public class RelendType extends groove.gxl.AttributedElementType 
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _target
     */
    private java.lang.Object _target;

    /**
     * Field _role
     */
    private java.lang.String _role;

    /**
     * Field _direction
     */
    private groove.gxl.types.DirectionType _direction;

    /**
     * Field _startorder
     */
    private int _startorder;

    /**
     * keeps track of state for field: _startorder
     */
    private boolean _has_startorder;

    /**
     * Field _endorder
     */
    private int _endorder;

    /**
     * keeps track of state for field: _endorder
     */
    private boolean _has_endorder;


      //----------------/
     //- Constructors -/
    //----------------/

    public RelendType() {
        super();
    } //-- groove.gxl.RelendType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteEndorder
     */
    public void deleteEndorder()
    {
        this._has_endorder= false;
    } //-- void deleteEndorder() 

    /**
     * Method deleteStartorder
     */
    public void deleteStartorder()
    {
        this._has_startorder= false;
    } //-- void deleteStartorder() 

    /**
     * Returns the value of field 'direction'.
     * 
     * @return the value of field 'direction'.
     */
    public groove.gxl.types.DirectionType getDirection()
    {
        return this._direction;
    } //-- groove.gxl.types.DirectionType getDirection() 

    /**
     * Returns the value of field 'endorder'.
     * 
     * @return the value of field 'endorder'.
     */
    public int getEndorder()
    {
        return this._endorder;
    } //-- int getEndorder() 

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
     * Returns the value of field 'startorder'.
     * 
     * @return the value of field 'startorder'.
     */
    public int getStartorder()
    {
        return this._startorder;
    } //-- int getStartorder() 

    /**
     * Returns the value of field 'target'.
     * 
     * @return the value of field 'target'.
     */
    public java.lang.Object getTarget()
    {
        return this._target;
    } //-- java.lang.Object getTarget() 

    /**
     * Method hasEndorder
     */
    public boolean hasEndorder()
    {
        return this._has_endorder;
    } //-- boolean hasEndorder() 

    /**
     * Method hasStartorder
     */
    public boolean hasStartorder()
    {
        return this._has_startorder;
    } //-- boolean hasStartorder() 

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
     * Sets the value of field 'direction'.
     * 
     * @param direction the value of field 'direction'.
     */
    public void setDirection(groove.gxl.types.DirectionType direction)
    {
        this._direction = direction;
    } //-- void setDirection(groove.gxl.types.DirectionType) 

    /**
     * Sets the value of field 'endorder'.
     * 
     * @param endorder the value of field 'endorder'.
     */
    public void setEndorder(int endorder)
    {
        this._endorder = endorder;
        this._has_endorder = true;
    } //-- void setEndorder(int) 

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
     * Sets the value of field 'startorder'.
     * 
     * @param startorder the value of field 'startorder'.
     */
    public void setStartorder(int startorder)
    {
        this._startorder = startorder;
        this._has_startorder = true;
    } //-- void setStartorder(int) 

    /**
     * Sets the value of field 'target'.
     * 
     * @param target the value of field 'target'.
     */
    public void setTarget(java.lang.Object target)
    {
        this._target = target;
    } //-- void setTarget(java.lang.Object) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (groove.gxl.RelendType) Unmarshaller.unmarshal(groove.gxl.RelendType.class, reader);
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
