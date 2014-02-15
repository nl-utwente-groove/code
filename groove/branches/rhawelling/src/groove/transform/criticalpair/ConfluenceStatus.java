/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2014 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.transform.criticalpair;

/**
 * @author Ruud Welling
 */
public enum ConfluenceStatus {
    //the declaration order is important for the result of getWorstStatus
    NOT_STICTLY_CONFLUENT, UNDECIDED, STRICTLY_CONFLUENT, UNTESTED;

    /**
     * Return the "worst" status of the two.
     * i.e. the lowest status in the declaration order of the ConfluenceStatus enum
     */
    public static ConfluenceStatus getWorstStatus(ConfluenceStatus first,
            ConfluenceStatus second) {
        return first.compareTo(second) < 0 ? first : second;
    }
}