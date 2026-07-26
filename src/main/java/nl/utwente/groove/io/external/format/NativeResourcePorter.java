/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.io.external.format;

import nl.utwente.groove.grammar.model.ResourceKind;

/**
 * Import and export resources native to GROOVE, such as type and host graphs, and control programs
 * @author Harold Bruijntjes
 * @version $Revision$
 */
public class NativeResourcePorter extends AbstractResourcePorter {
    private NativeResourcePorter() {
        register(ResourceKind.TYPE);
        register(ResourceKind.HOST);
        register(ResourceKind.RULE);
        register(ResourceKind.CONTROL);
        register(ResourceKind.PROLOG);
        register(ResourceKind.GROOVY);
    }

    /** Registers a resource kind with its default file type. */
    private void register(ResourceKind kind) {
        register(kind, kind.getFileType());
    }

    /** Returns the singleton instance of this class. */
    public static final NativeResourcePorter getInstance() {
        return instance;
    }

    private static final NativeResourcePorter instance = new NativeResourcePorter();
}
