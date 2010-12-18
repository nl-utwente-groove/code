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
 * $Id: RuleApplication.java,v 1.11 2008-02-06 17:04:38 rensink Exp $
 */
package groove.trans;


/**
 * Interface to wrap the computation involved in applying a production rule.
 * This is used in two different phases: when constructing a derivation, and to
 * reconstruct the matching and the target graph after they have been minimised,
 * if the cached representation has been discarded.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface RuleApplication extends Derivation, DeltaApplier {
    /**
     * Returns the rule for which this is an application.
     */
    public Rule getRule();

    /**
     * Returns the event underlying this application.
     */
    public RuleEvent getEvent();

    /**
     * Returns the source graph to which the rule is applied.
     */
    public HostGraph getSource();

    /**
     * Returns a target graph created as a result of the application. The target
     * is typically created lazily.
     */
    public HostGraph getTarget();

    /**
     * Returns the image of the rule's creator nodes in the target graph.
     */
    public HostNode[] getCreatedNodes();

    /**
     * Applies the rule to a given delta target. This is presumably the host
     * graph to which the underlying rule is to be applied. The source should
     * coincide with that for which the footprint was originally created
     * @param target the target object on which the modifications are to be
     *        performed
     */
    public void applyDelta(DeltaTarget target);
}
