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
package groove.abs;

/**
 * Represents the type of a node of an abstract graph, which is a couple of a
 * GraphPattern and a MultiplicityInformation.
 * @author Iovka Boneva
 * @version $Revision $
 */
@Deprecated
public class NodeType {

    /** */
    private GraphPattern pattern;
    /** */
    private MultiplicityInformation mult;

    /**
     * @param pattern
     * @param mult
     */
    public NodeType(GraphPattern pattern, MultiplicityInformation mult) {
        super();
        this.pattern = pattern;
        this.mult = mult;
    }

    /**
     * @return the pattern component
     */
    public GraphPattern getPattern() {
        return this.pattern;
    }

    /**
     * @param pattern
     */
    public void setPattern(GraphPattern pattern) {
        this.pattern = pattern;
    }

    /**
     * @return The multiplicity component
     */
    public MultiplicityInformation getMult() {
        return this.mult;
    }

    /**
     * @param mult
     */
    public void setMult(MultiplicityInformation mult) {
        this.mult = mult;
    }

    @Override
    public String toString() {
        return "(" + this.mult + ", " + this.pattern.edgeSet() + ")";
    }

}
