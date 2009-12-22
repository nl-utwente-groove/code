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
 * $Id$
 */
package groove.explore.result;

import groove.lts.LTSAdapter;

/**
 * Listens to a GTS and adds accepted elements to a result.
 */
public class Acceptor extends LTSAdapter {
    /** Creates an instance with a default {@link Result}. */
    public Acceptor() {
        this(new Result());
    }

    /** Constructs an instance with a given result. */
    public Acceptor(Result result) {
        this.result = result;
    }

    /**
     * Sets the result that collects accepted elements.
     * @param result the result that collects accepted elements
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * Retrieves the result.
     * @return The result
     */
    public Result getResult() {
        return this.result;
    }
    
    /**
     * Factory method to create a fresh instance of this acceptor, with a fresh
     * result instance.
     */
    public Acceptor newInstance() {
        return new Acceptor(this.result.newInstance());
    }

    private Result result;
}
