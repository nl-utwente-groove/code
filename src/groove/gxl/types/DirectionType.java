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
 * $Id: DirectionType.java,v 1.1.1.1 2007-03-20 10:05:29 kastenberg Exp $
 */

package groove.gxl.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class DirectionType.
 * 
 * @version $Revision: 1.1.1.1 $ $Date: 2007-03-20 10:05:29 $
 */
public class DirectionType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The in type
     */
    public static final int IN_TYPE = 0;

    /**
     * The instance of the in type
     */
    public static final DirectionType IN = new DirectionType(IN_TYPE, "in");

    /**
     * The out type
     */
    public static final int OUT_TYPE = 1;

    /**
     * The instance of the out type
     */
    public static final DirectionType OUT = new DirectionType(OUT_TYPE, "out");

    /**
     * The none type
     */
    public static final int NONE_TYPE = 2;

    /**
     * The instance of the none type
     */
    public static final DirectionType NONE = new DirectionType(NONE_TYPE, "none");

    /**
     * Field _memberTable
     */
    private static java.util.Hashtable _memberTable = init();

    /**
     * Field type
     */
    private int type = -1;

    /**
     * Field stringValue
     */
    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private DirectionType(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- groove.gxl.types.DirectionType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerateReturns an enumeration of all possible
     * instances of DirectionType
     */
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Method getTypeReturns the type of this DirectionType
     */
    public int getType()
    {
        return this.type;
    } //-- int getType() 

    /**
     * Method init
     */
    private static java.util.Hashtable init()
    {
        Hashtable members = new Hashtable();
        members.put("in", IN);
        members.put("out", OUT);
        members.put("none", NONE);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Method toStringReturns the String representation of this
     * DirectionType
     */
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Method valueOfReturns a new DirectionType based on the given
     * String value.
     * 
     * @param string
     */
    public static groove.gxl.types.DirectionType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid DirectionType";
            throw new IllegalArgumentException(err);
        }
        return (DirectionType) obj;
    } //-- groove.gxl.types.DirectionType valueOf(java.lang.String) 

}
