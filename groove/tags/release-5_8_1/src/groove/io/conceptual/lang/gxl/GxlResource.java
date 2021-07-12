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

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import de.gupro.gxl.gxl_1_0.EdgemodeType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.GxlType;
import groove.io.conceptual.Timer;
import groove.io.conceptual.lang.ExportException;
import groove.io.conceptual.lang.ExportableResource;

@SuppressWarnings("javadoc")
public class GxlResource extends ExportableResource {
    // For exported resource
    private GxlType m_gxlTypeType;
    private GxlType m_gxlTypeInstance;
    // For temp resource (type for instance that does not export type as well)
    private GxlType m_gxlTypeTemp;
    private Map<String,GraphType> m_graphs = new HashMap<>();

    private File m_typeFile;
    private File m_instanceFile;

    private String relPath;

    public GxlResource(File typeTarget, File instanceTarget) {
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
            this.relPath =
                groove.io.Util.getRelativePath(new File(this.m_instanceFile.getAbsoluteFile()
                    .getParent()), this.m_typeFile.getAbsoluteFile())
                    .toString();
        }

        this.m_gxlTypeType = new GxlType();
        this.m_gxlTypeTemp = new GxlType();
        if (this.m_typeFile == this.m_instanceFile) {
            this.m_gxlTypeInstance = this.m_gxlTypeType;
        } else {
            this.m_gxlTypeInstance = new GxlType();
        }
    }

    public GraphType getTypeGraph(String graphName) {
        if (this.m_graphs.containsKey(graphName)) {
            return this.m_graphs.get(graphName);
        }
        GraphType graph = new GraphType();
        graph.setId(graphName);
        graph.setEdgeids(true);
        GxlUtil.setElemType(graph, GxlUtil.g_gxlTypeGraphURI + "#gxl-1.0");

        if (this.m_typeFile != null) {
            this.m_gxlTypeType.getGraph()
                .add(graph);
        } else {
            this.m_gxlTypeTemp.getGraph()
                .add(graph);
        }

        this.m_graphs.put(graphName, graph);

        return graph;
    }

    public GraphType getInstanceGraph(String graphName, String typeId) {
        if (this.m_graphs.containsKey(graphName)) {
            return this.m_graphs.get(graphName);
        }
        GraphType graph = new GraphType();
        graph.setId(graphName);
        GxlUtil.setElemType(graph, getTypePath() + "#" + typeId);
        // No edge IDs in instance graphs
        graph.setEdgeids(false);
        graph.setEdgemode(EdgemodeType.DEFAULTDIRECTED);

        this.m_gxlTypeInstance.getGraph()
            .add(graph);

        this.m_graphs.put(graphName, graph);

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

        OutputStream os;
        try {
            if (!oldStyle) {
                // Regular export
                if (this.m_typeFile != null) {
                    os = new FileOutputStream(this.m_typeFile);
                    GxlUtil.g_marshaller.marshal(mainElement, os);
                    os.close();
                }

                if (this.m_instanceFile != null && instanceElement != null) {
                    os = new FileOutputStream(this.m_instanceFile);
                    GxlUtil.g_marshaller.marshal(instanceElement, os);
                    os.close();
                }
            } else {
                // Insert doctype, move xmlns:xlink around and remove standalone
                // Really hacky, but the old gxlvalidator wont accept the document otherwise
                // I'm no XML expert ;)
                if (this.m_typeFile != null) {
                    os = new ByteArrayOutputStream();
                    GxlUtil.g_marshaller.marshal(mainElement, os);

                    String xmlString = ((ByteArrayOutputStream) os).toString("UTF-8");
                    xmlString = xmlString.replaceAll("standalone=\"yes\"", "")
                        .replaceAll("xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "")
                        .replaceAll("<gxl[^>]*>",
                            "<!DOCTYPE gxl SYSTEM \"http://www.gupro.de/GXL/gxl-1.0.dtd\">\n"
                                + "<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
                    try (BufferedWriter out = new BufferedWriter(new FileWriter(this.m_typeFile))) {
                        out.write(xmlString);
                    }
                }

                if (this.m_instanceFile != null && instanceElement != null) {
                    os = new ByteArrayOutputStream();
                    GxlUtil.g_marshaller.marshal(instanceElement, os);

                    String xmlString = ((ByteArrayOutputStream) os).toString("UTF-8");
                    xmlString = xmlString.replaceAll("standalone=\"yes\"", "")
                        .replaceAll("xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "")
                        .replaceAll("<gxl[^>]*>",
                            "<!DOCTYPE gxl SYSTEM \"http://www.gupro.de/GXL/gxl-1.0.dtd\">\n"
                                + "<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
                    try (BufferedWriter out =
                        new BufferedWriter(new FileWriter(this.m_instanceFile))) {
                        out.write(xmlString);
                    }
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

}
