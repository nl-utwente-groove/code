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
 * $Id: CompositeValueTypeItem.java,v 1.1.1.2 2007-03-20 10:42:48 kastenberg Exp $
 */

package groove.gxl;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class CompositeValueTypeItem.
 * 
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:48 $
 */
public class CompositeValueTypeItem implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _value
     */
    private groove.gxl.Value _value;


      //----------------/
     //- Constructors -/
    //----------------/

    public CompositeValueTypeItem() {
        super();
    } //-- groove.gxl.CompositeValueTypeItem()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'value'.
     * 
     * @return the value of field 'value'.
     */
    public groove.gxl.Value getValue()
    {
        return this._value;
    } //-- groove.gxl.Value getValue() 

    /**
     * Sets the value of field 'value'.
     * 
     * @param value the value of field 'value'.
     */
    public void setValue(groove.gxl.Value value)
    {
        this._value = value;
    } //-- void setValue(groove.gxl.Value) 

}
