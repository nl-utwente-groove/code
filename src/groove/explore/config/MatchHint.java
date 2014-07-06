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
package groove.explore.config;

import groove.util.Duo;
import groove.util.Parser;

import java.util.Collections;
import java.util.List;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class MatchHint extends Duo<List<String>> implements SettingContent {
    /**
     * Constructs an empty match hint.
     */
    public MatchHint() {
        super(Collections.<String>emptyList(), Collections.<String>emptyList());
    }

    /** Constructs a hint from lists of common and control labels. */
    public MatchHint(List<String> common, List<String> control) {
        super(common, control);
    }

    /** Parser for match hints. */
    public static final Parser<MatchHint> PARSER = new Parser<MatchHint>() {
        @Override
        public String getDescription(boolean uppercase) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean accepts(String text) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public MatchHint parse(String text) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String toParsableString(Object value) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isValue(Object value) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public MatchHint getDefaultValue() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getDefaultString() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isDefault(Object value) {
            // TODO Auto-generated method stub
            return false;
        }

    };
}
