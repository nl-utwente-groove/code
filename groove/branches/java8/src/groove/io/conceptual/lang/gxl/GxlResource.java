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
package groove.io.conceptual.lang.gxl;

import groove.io.conceptual.Timer;
import groove.io.conceptual.lang.ExportException;
import groove.io.conceptual.lang.ExportableResource;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import de.gupro.gxl.gxl_1_0.EdgemodeType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.GxlType;

public class GxlResource extends ExportableResource {
    // For exported resource
    private GxlType m_gxlTypeType;
    private GxlType m_gxlTypeInstance;
    // For temp resource (type for instance that does not export type as well)
    private GxlType m_gxlTypeTemp;
    private Map<String,GraphType> m_graphs = new HashMap<String,GraphType>();

    private Path m_typeFile;
    private Path m_instanceFile;

    private String relPath;

    public GxlResource(Path typeTarget, Path instanceTarget) {
        if (typeTarget != null) {
            this.m_typeFile = typeTarget;
        }
        if (instanceTarget != null) {
            if (instanceTarget.equals(typeTarget)) {
                this.m_instanceFile = this.m_typeFile;
            } else {
                this.m_instanceFile = instanceTarget;
            }
        }

        if (this.m_typeFile == this.m_instanceFile || this.m_typeFile == null) {
            this.relPath = "";
        } else {
            this.relPath = this.m_instanceFile.relativize(this.m_typeFile).toString();
        }

        this.m_gxlTypeType = new GxlType();
        this.m_gxlTypeTemp = new GxlType();
        if (this.m_typeFile == this.m_instanceFile) {
            this.m_gxlTypeInstance = this.m_gxlTypeType;
        } else {
            this.m_gxlTypeInstance = new GxlType();
        }
    }

    public GraphType getTypeGraph(String graphId) {
        if (this.m_graphs.containsKey(graphId)) {
            return this.m_graphs.get(graphId);
        }
        GraphType graph = new GraphType();
        graph.setId(graphId);
        graph.setEdgeids(true);
        GxlUtil.setElemType(graph, GxlUtil.g_gxlTypeGraphURI + "#gxl-1.0");

        if (this.m_typeFile != null) {
            this.m_gxlTypeType.getGraph().add(graph);
        } else {
            this.m_gxlTypeTemp.getGraph().add(graph);
        }

        this.m_graphs.put(graphId, graph);

        return graph;
    }

    public GraphType getInstanceGraph(String typeId, String name) {
        if (this.m_graphs.containsKey(name)) {
            return this.m_graphs.get(name);
        }
        GraphType graph = new GraphType();
        graph.setId(name);
        GxlUtil.setElemType(graph, getTypePath() + "#" + typeId);
        // No edge IDs in instance graphs
        graph.setEdgeids(false);
        graph.setEdgemode(EdgemodeType.DEFAULTDIRECTED);

        this.m_gxlTypeInstance.getGraph().add(graph);

        this.m_graphs.put(name, graph);

        return graph;
    }

    public String getTypePath() {
        return this.relPath;
    }

    @Override
    public boolean export() throws ExportException {
        return export(false);
    }

    public boolean export(boolean oldStyle) throws ExportException {
        // m_gxlType contains all graphs
        int timer = Timer.start("Save GXL");
        JAXBElement<GxlType> mainElement = GxlUtil.g_objectFactory.createGxl(this.m_gxlTypeType);
        JAXBElement<GxlType> instanceElement = null;
        if (this.m_instanceFile != null && this.m_gxlTypeInstance != this.m_gxlTypeType) {
            instanceElement = GxlUtil.g_objectFactory.createGxl(this.m_gxlTypeInstance);
        }

        try {
            if (!oldStyle) {
                // Regular export
                if (this.m_typeFile != null) {
                    try (OutputStream os = Files.newOutputStream(this.m_typeFile)) {
                        GxlUtil.g_marshaller.marshal(mainElement, os);
                    }
                }
                if (this.m_instanceFile != null && instanceElement != null) {
                    try (OutputStream os = Files.newOutputStream(this.m_instanceFile)) {
                        GxlUtil.g_marshaller.marshal(instanceElement, os);
                    }
                }
            } else {
                if (this.m_typeFile != null) {
                    String xmlString = marshalAsString(mainElement);
                    xmlString = convertXmlString(xmlString);
                    Files.write(this.m_typeFile, Collections.singletonList(xmlString));
                }
                if (this.m_instanceFile != null && instanceElement != null) {
                    String xmlString = marshalAsString(instanceElement);
                    xmlString = convertXmlString(xmlString);
                    Files.write(this.m_typeFile, Collections.singletonList(xmlString));
                }
            }
        } catch (FileNotFoundException e) {
            throw new ExportException(e);
        } catch (JAXBException e) {
            throw new ExportException(e);
        } catch (UnsupportedEncodingException e) {
            throw new ExportException(e);
        } catch (IOException e) {
            throw new ExportException(e);
        }

        Timer.stop(timer);

        return true;
    }

    /**
     * Marshals a certain XML subtree into a string.
     */
    private String marshalAsString(JAXBElement<GxlType> mainElement) throws JAXBException,
        UnsupportedEncodingException, IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            GxlUtil.g_marshaller.marshal(mainElement, os);
            return os.toString("UTF-8");
        }
    }

    /**
     * Insert doctype, move xmlns:xlink around and remove standalone
     * Really hacky, but the old gxlvalidator wont accept the document otherwise
     * I'm no XML expert ;)
     */
    private String convertXmlString(String xmlString) {
        xmlString =
            xmlString.replaceAll("standalone=\"yes\"", "")
                .replaceAll("xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "")
                .replaceAll("<gxl[^>]*>",
                    "<!DOCTYPE gxl SYSTEM \"http://www.gupro.de/GXL/gxl-1.0.dtd\">\n"
                        + "<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
        return xmlString;
    }

}
