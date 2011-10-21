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
 * $Id: MinimalAnchorFactory.java,v 1.8 2008-02-29 11:02:20 fladder Exp $
 */
package groove.abstraction.neigh.trans;

import groove.trans.AnchorFactory;
import groove.trans.Rule;
import groove.trans.RuleGraph;

/**
 * Anchor factory used in abstraction.
 * The anchor is taken to be the entire LHS of the rule.
 * 
 * @author Eduardo Zambon
 */
public class TotalAnchorFactory implements AnchorFactory<Rule> {
    /** Private empty constructor to make this a singleton class. */
    private TotalAnchorFactory() {
        // empty constructor
    }

    /**
     * This implementation assumes that the rule is an <tt>SPORule</tt>, and
     * that the rule's internal sets of <tt>lhsOnlyNodes</tt> etc. have been
     * initialised already.
     */
    public RuleGraph newAnchor(Rule rule) {
        RuleGraph result = rule.lhs();
        return result;
    }

    /**
     * Returns the singleton instance of this class.
     */
    static public TotalAnchorFactory getInstance() {
        return prototype;
    }

    /** The singleton instance of this class. */
    static private TotalAnchorFactory prototype = new TotalAnchorFactory();
}
