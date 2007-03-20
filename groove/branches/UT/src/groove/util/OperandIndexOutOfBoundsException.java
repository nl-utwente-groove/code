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
 * Created on 2005/01/12 at 11:01:12
 * Author: Harmen Kastenberg
 * Revision: $(revision 1.1)
 */
package groove.util;


public class OperandIndexOutOfBoundsException extends Exception {
    /**
     * 
     */
    public OperandIndexOutOfBoundsException() {
        super();
    }

    /**
     * @param message
     */
    public OperandIndexOutOfBoundsException(String message) {
        super(message);
    }
}