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
package groove.abstraction;

import groove.graph.Label;
import groove.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * EDUARDO
 * @author Eduardo Zambon
 * @version $Revision $
 */
public class EdgeMultConf extends
        Pair<Map<EdgeSignature,Multiplicity>,Map<EdgeSignature,Multiplicity>> {

    /** EDUARDO */
    public EdgeMultConf(Map<EdgeSignature,Multiplicity> first,
            Map<EdgeSignature,Multiplicity> second) {
        super(first, second);
    }

    /** EDUARDO */
    @SuppressWarnings("unchecked")
    public static Set<EdgeMultConf> computeConfs(Shape shape, ShapeNode node,
            EquivClass<ShapeNode> ec) {
        // Singleton equivalence class.
        EquivClass<ShapeNode> singEc = new EquivClass<ShapeNode>();
        singEc.add(node);
        // Remaining equivalence class.
        EquivClass<ShapeNode> remainEc = ec.clone();
        remainEc.remove(node);

        Set<EdgeSignature> sigs = shape.getEdgeSignatures(ec);
        int sigsSize = sigs.size();
        Pair<EdgeSignature,Multiplicity> outSigs[][][] = new Pair[sigsSize][][];
        Pair<EdgeSignature,Multiplicity> inSigs[][][] = new Pair[sigsSize][][];
        int i = 0;
        for (EdgeSignature es : sigs) {
            ShapeNode sigNode = es.getNode();
            Label label = es.getLabel();

            EdgeSignature singEs = new EdgeSignature(sigNode, label, singEc);
            EdgeSignature remainEs =
                new EdgeSignature(sigNode, label, remainEc);

            int j;

            // Outgoing multiplicities.
            Multiplicity origOutMult = shape.getEdgeSigOutMult(es);
            Set<Pair<Multiplicity,Multiplicity>> outMultPairs =
                shuffleMults(origOutMult);
            int outMultPairsSize = outMultPairs.size();
            outSigs[i] = new Pair[outMultPairsSize][2];
            j = 0;
            for (Pair<Multiplicity,Multiplicity> outMultPair : outMultPairs) {
                Multiplicity singMult = outMultPair.first();
                Multiplicity remainMult = outMultPair.second();
                Pair<EdgeSignature,Multiplicity> singPair =
                    new Pair<EdgeSignature,Multiplicity>(singEs, singMult);
                Pair<EdgeSignature,Multiplicity> remainPair =
                    new Pair<EdgeSignature,Multiplicity>(remainEs, remainMult);
                outSigs[i][j][0] = singPair;
                outSigs[i][j][1] = remainPair;
                j++;
            }

            // Incoming multiplicities.
            Multiplicity origInMult = shape.getEdgeSigInMult(es);
            Set<Pair<Multiplicity,Multiplicity>> inMultPairs =
                shuffleMults(origInMult);
            int inMultPairsSize = inMultPairs.size();
            inSigs[i] = new Pair[inMultPairsSize][2];
            j = 0;
            for (Pair<Multiplicity,Multiplicity> inMultPair : inMultPairs) {
                Multiplicity singMult = inMultPair.first();
                Multiplicity remainMult = inMultPair.second();
                Pair<EdgeSignature,Multiplicity> singPair =
                    new Pair<EdgeSignature,Multiplicity>(singEs, singMult);
                Pair<EdgeSignature,Multiplicity> remainPair =
                    new Pair<EdgeSignature,Multiplicity>(remainEs, remainMult);
                inSigs[i][j][0] = singPair;
                inSigs[i][j][1] = remainPair;
                j++;
            }

            i++;
        }

        return finishConstruction(outSigs, inSigs);
    }

    private static Set<Pair<Multiplicity,Multiplicity>> shuffleMults(
            Multiplicity mult) {
        Multiplicity zero = Multiplicity.getMultOf(0);
        Multiplicity one = Multiplicity.getMultOf(1);

        Set<Pair<Multiplicity,Multiplicity>> result =
            new HashSet<Pair<Multiplicity,Multiplicity>>();

        result.add(new Pair<Multiplicity,Multiplicity>(mult, zero));
        result.add(new Pair<Multiplicity,Multiplicity>(zero, mult));
        if (mult.isPositive()) {
            for (Multiplicity newMult : mult.sub(one,
                Parameters.getEdgeMultBound())) {
                result.add(new Pair<Multiplicity,Multiplicity>(newMult, mult));
                result.add(new Pair<Multiplicity,Multiplicity>(mult, newMult));
            }
        }

        return result;
    }

    private static Set<EdgeMultConf> finishConstruction(
            Pair<EdgeSignature,Multiplicity> outSigs[][][],
            Pair<EdgeSignature,Multiplicity> inSigs[][][]) {
        assert outSigs.length == inSigs.length : "Given arrays should have the same length.";
        Set<EdgeMultConf> result = new HashSet<EdgeMultConf>();

        for (int io = 0; io < outSigs.length; io++) {
            for (int ii = 0; ii < inSigs.length; ii++) {
                for (int jo = 0; jo < outSigs[io].length; jo++) {
                    for (int ji = 0; ji < inSigs[ii].length; ji++) {
                        System.out.println(io + " " + ii + " " + jo + " " + ji);
                    }
                }
            }
        }

        Map<EdgeSignature,Multiplicity> outMult =
            new HashMap<EdgeSignature,Multiplicity>();
        Map<EdgeSignature,Multiplicity> inMult =
            new HashMap<EdgeSignature,Multiplicity>();

        return result;
    }

}
