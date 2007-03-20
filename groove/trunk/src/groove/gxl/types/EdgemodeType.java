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
 * $Id: EdgemodeType.java,v 1.1.1.2 2007-03-20 10:42:50 kastenberg Exp $
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
 * Class EdgemodeType.
 * 
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:50 $
 */
public class EdgemodeType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The directed type
     */
    public static final int DIRECTED_TYPE = 0;

    /**
     * The instance of the directed type
     */
    public static final EdgemodeType DIRECTED = new EdgemodeType(DIRECTED_TYPE, "directed");

    /**
     * The undirected type
     */
    public static final int UNDIRECTED_TYPE = 1;

    /**
     * The instance of the undirected type
     */
    public static final EdgemodeType UNDIRECTED = new EdgemodeType(UNDIRECTED_TYPE, "undirected");

    /**
     * The defaultdirected type
     */
    public static final int DEFAULTDIRECTED_TYPE = 2;

    /**
     * The instance of the defaultdirected type
     */
    public static final EdgemodeType DEFAULTDIRECTED = new EdgemodeType(DEFAULTDIRECTED_TYPE, "defaultdirected");

    /**
     * The defaultundirected type
     */
    public static final int DEFAULTUNDIRECTED_TYPE = 3;

    /**
     * The instance of the defaultundirected type
     */
    public static final EdgemodeType DEFAULTUNDIRECTED = new EdgemodeType(DEFAULTUNDIRECTED_TYPE, "defaultundirected");

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

    private EdgemodeType(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- groove.gxl.types.EdgemodeType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerateReturns an enumeration of all possible
     * instances of EdgemodeType
     */
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Method getTypeReturns the type of this EdgemodeType
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
        members.put("directed", DIRECTED);
        members.put("undirected", UNDIRECTED);
        members.put("defaultdirected", DEFAULTDIRECTED);
        members.put("defaultundirected", DEFAULTUNDIRECTED);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Method toStringReturns the String representation of this
     * EdgemodeType
     */
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Method valueOfReturns a new EdgemodeType based on the given
     * String value.
     * 
     * @param string
     */
    public static groove.gxl.types.EdgemodeType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid EdgemodeType";
            throw new IllegalArgumentException(err);
        }
        return (EdgemodeType) obj;
    } //-- groove.gxl.types.EdgemodeType valueOf(java.lang.String) 

}
