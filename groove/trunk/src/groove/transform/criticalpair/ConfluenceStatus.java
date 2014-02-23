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
 * Value indicating what is known about the confluence of a critical pair.
 * @author Ruud Welling
 */
public enum ConfluenceStatus {
    //the declaration order is important for the result of getWorstStatus
    /**
     * The critical pair is not confluent.
     */
    NOT_STICTLY_CONFLUENT,
    /**
     * Analysis was interrupted because it was taking too long.
     */
    UNDECIDED,
    /**
     * The critical pair is strictly confluent.
     */
    STRICTLY_CONFLUENT,
    /**
     * Initial value, before any analysis is done.
     */
    UNTESTED;

    /**
     * Return the "worst" status of the two.
     * i.e. the lowest status in the declaration order of the ConfluenceStatus enum
     */
    public static ConfluenceStatus getWorstStatus(ConfluenceStatus first, ConfluenceStatus second) {
        return first.compareTo(second) < 0 ? first : second;
    }
}