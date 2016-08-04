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
package groove.control.template;

import groove.util.ThreadPool;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalisation mapping from locations to locations.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Relocation extends HashMap<Location,Location> {
    /** Adds a template to the map of relocated templates.
     */
    public void addTemplate(Template key, Template result) {
        this.templates.put(key, result);
    }

    /** Tests if a given template is in the set of relocated templates. */
    public boolean hasTemplate(Template template) {
        return this.templates.containsKey(template);
    }

    private final Map<Template,Template> templates = new HashMap<>();

    /**
     * Builds the target templates, by
     * setting the attempts (relocated using this map).
     */
    public void build() {
        ThreadPool threads = ThreadPool.instance();
        for (Map.Entry<Template,Template> e : this.templates.entrySet()) {
            final Template source = e.getKey();
            final Template target = e.getValue();
            threads.start(new Runnable() {
                @Override
                public void run() {
                    for (Location sourceLoc : source.getLocations()) {
                        if (sourceLoc.isTrial()) {
                            Location targetLoc = get(sourceLoc);
                            targetLoc.setAttempt(sourceLoc.getAttempt().relocate(Relocation.this));
                        }
                    }
                    target.initVars();
                }
            });
        }
        threads.sync();
    }
}
