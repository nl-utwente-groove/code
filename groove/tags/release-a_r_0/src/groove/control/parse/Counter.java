/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: Counter.java,v 1.2 2008-01-30 09:33:33 iovka Exp $
 */
package groove.control.parse;

/**
 * This class generates unique ID's for ControlState's.
 * 
 * @author Tom Staijen
 * @version $Revision $
 */
public class Counter {
    /**
     * Returns a unique integer which can be used as an ID.
     */
    public static int inc() {
        return INSTANCE.increase();
    }

    private int increase() {
        int retval = this.current;
        this.current++;
        return retval;
    }

    /** Resets the counter to zero. */
    public static void reset() {
        INSTANCE.current = 0;
    }

    private int current = 0;

    private static Counter INSTANCE = new Counter();
}