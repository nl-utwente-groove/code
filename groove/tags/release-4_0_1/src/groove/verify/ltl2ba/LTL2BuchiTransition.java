/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.verify.ltl2ba;

import groove.verify.BuchiLabel;
import groove.verify.BuchiLocation;
import groove.verify.DefaultBuchiTransition;
import groove.verify.ModelChecking;

import java.util.Set;

import junit.framework.Assert;

import rwth.i2.ltl2ba4j.model.IGraphProposition;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public class LTL2BuchiTransition extends DefaultBuchiTransition
{
    /**
     * Constructor for creating a new Buchi
     * 
     * @param source
     * @param label
     * @param target
     */
    public LTL2BuchiTransition(BuchiLocation source, LTL2BuchiLabel label, BuchiLocation target)
    {
        super(source, label, target);
    }

    @Override
    public boolean isEnabled(Set<String> applicableRules)
    {
        boolean result = true;
        BuchiLabel label = label();
        Assert.assertTrue("Label is of wrong type: " + label.getClass(), label instanceof LTL2BuchiLabel);
        LTL2BuchiLabel castedLabel = (LTL2BuchiLabel) label;
        for (IGraphProposition gp : castedLabel.getLabels()) {
            if (gp.getFullLabel().equals(ModelChecking.SIGMA)) {
                continue;
            }
            boolean applicable = false;
            // only take the label of the proposition - negation will be checked
            // afterwards
            String prop = gp.getLabel();
            for (String ruleName : applicableRules) {
                if (prop.equals(ruleName)) {
                    applicable = true;
                }
            }
            boolean match = (gp.isNegated() ^ applicable);
            result = result && match;
        }
        return result;
    }

    @Override
    public String toString()
    {
        return source().toString() + " --" + label().toString() + "--> " + target().toString();
    }
}
