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
package groove.io.conceptual.lang.ecore;

import groove.io.conceptual.Timer;
import groove.io.conceptual.lang.Export;
import groove.io.conceptual.lang.ExportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class EcoreExport extends Export {
    private final ResourceSet m_resourceSet;
    private final Map<String,Resource> m_typeResources = new HashMap<String,Resource>();
    private final Map<String,Resource> m_instanceResources = new HashMap<String,Resource>();

    private final Path m_typeFile;
    private final Path m_instanceFile;

    private final String relPath;

    //files allowed null if instance or type not required
    public EcoreExport(Path typeTarget, Path instanceTarget) {
        this.m_resourceSet = new ResourceSetImpl();
        this.m_resourceSet.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put("*", new XMIResourceFactoryImpl());

        this.m_typeFile = typeTarget;
        this.m_instanceFile = instanceTarget;

        if (this.m_typeFile == this.m_instanceFile || this.m_typeFile == null
            || this.m_instanceFile == null) {
            this.relPath = "";
        } else {
            this.relPath =
                this.m_instanceFile.toAbsolutePath()
                    .relativize(this.m_typeFile.toAbsolutePath())
                    .toString();
        }
    }

    public ResourceSet getResourceSet() {
        return this.m_resourceSet;
    }

    public String getTypePath() {
        return this.relPath;
    }

    /** Returns the ECore metamodel of a given name.
     * The name is either the pre-initialised meta-model name, if non-{@code null},
     * or the parameter name otherwise.
     */
    public Resource getTypeResource(String name) {
        if (this.m_typeFile != null) {
            name = this.m_typeFile.toString();
        }
        Resource result = this.m_typeResources.get(name);
        if (result == null) {
            result = this.m_resourceSet.createResource(URI.createURI(name));
            this.m_typeResources.put(name, result);
        }
        return result;
    }

    /** Returns the ECore instance model of a given name. */
    public Resource getInstanceResource(String name) {
        Resource result = this.m_instanceResources.get(name);
        if (result == null) {
            result = this.m_resourceSet.createResource(URI.createURI(name));
            this.m_instanceResources.put(name, result);
        }
        return result;
    }

    @Override
    public boolean export() throws ExportException {
        try {
            if (this.m_typeFile != null) {
                for (Entry<String,Resource> resourceEntry : this.m_typeResources.entrySet()) {
                    try (OutputStream out = Files.newOutputStream(this.m_typeFile)) {
                        int timer = Timer.cont("Ecore save");
                        resourceEntry.getValue().save(out, null);
                        Timer.stop(timer);
                    }
                }
            }

            if (this.m_instanceFile != null) {
                for (Entry<String,Resource> resourceEntry : this.m_instanceResources.entrySet()) {
                    try (OutputStream out = Files.newOutputStream(this.m_instanceFile)) {
                        Map<Object,Object> opts = new HashMap<Object,Object>();
                        if (this.m_typeFile != null) {
                            // If a target type resource has been defined, use schemaLocation.
                            // Otherwise don't, because it will point to nothing
                            opts.put(XMIResource.OPTION_SCHEMA_LOCATION, true);
                        }

                        int timer = Timer.cont("Ecore save");
                        resourceEntry.getValue().save(out, opts);
                        Timer.stop(timer);
                    }
                }
            }
        } catch (IOException e) {
            // abort
            throw new ExportException(e);
        }
        return true;
    }
}
