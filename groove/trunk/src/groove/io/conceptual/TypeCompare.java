/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.io.conceptual;

public class TypeCompare {
    TypeModel model1;
    TypeModel model2;

    public TypeCompare(TypeModel model1, TypeModel model2) {
        this.model1 = model1;
        this.model2 = model2;
    }

    public static String printModel(TypeModel model1) {
        StringBuilder sb = new StringBuilder();

        sb.append("TypeModel: " + model1.getName() + "\n");
        sb.append("Classes:\n");
        for (groove.io.conceptual.type.Class c : model1.getClasses()) {
            sb.append(" " + c + "\n");
            for (Field f : c.getFields()) {
                sb.append("  " + f + " : " + f.getType() + "\n");
            }
        }
        sb.append("Enums:\n");
        for (groove.io.conceptual.type.Enum c : model1.getEnums()) {
            sb.append(" " + c + "\n");
            for (Name n : c.getLiterals()) {
                sb.append("  " + n + "\n");
            }
        }
        sb.append("Datatypes:\n");
        for (groove.io.conceptual.type.CustomDataType c : model1.getDatatypes()) {
            sb.append(" " + c + "\n");
        }

        return sb.toString();
    }

    /*
        public ArrayList<Duo<Object>> matchNames(Map<String,Object> map1, Map<String,Object> map2) {
            Set<String> usedMap1 = new HashSet<String>();
            Set<String> usedMap2 = new HashSet<String>();

            int bestScore = Integer.MAX_VALUE;
            
        }
    */
    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    //from wikipedia
    public static int computeLevenshteinDistance(CharSequence str1,
            CharSequence str2) {
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            distance[i][0] = i;
        }
        for (int j = 1; j <= str2.length(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                distance[i][j] = minimum(
                    distance[i - 1][j] + 1,
                    distance[i][j - 1] + 1,
                    distance[i - 1][j - 1]
                        + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                                : 1));
            }
        }

        return distance[str1.length()][str2.length()];
    }
}
