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
package groove.legacy;

/**
 * The goal of the legacy package is to provide functionality for loading
 * old grammar versions. The idea is as follows:
 * <ul>
 * <li> When the grammar version increases, the old loader should be isolated
 *      and copied into a separate legacy package.
 * <li> If necessary, a conversion between 'old' graphs and 'new' graphs
 *      should also be written. This converter should then be added to the
 *      legacy package as well.
 * <li> There should be one method within Groove that is responsible for
 *      loading a graph. This method should take the grammar version as an
 *      argument, as well as the file(s) to load the grammar from, and should
 *      return the loaded graph. The method should behave as follows:
 *      <ul>
 *      <li> If the grammar has the same version as Groove, then load it (with
 *           the standard loader). If it has a higher version, than display a
 *           warning message first.
 *      <li> If the grammar has a lower version, then choose the lowest
 *           available loader from the legacy package. Afterwards, convert the
 *           output to the current version by consecutively applying all
 *           legacy converters.
 *      </ul>
 * <li> Saving the grammar should only be available using the latest grammar
 *      version.
 * </li>
 * This has not been implemented yet. The following pointers might help:
 * <ul>
 * <li> XML and GxlIO are interfaces that define 'unmarshallGraph',
 *      'loadGraph' and 'loadGraphWithMap' methods;
 * <li> JaxbGxlIO implements the 'loadGraphWithMap' and 'loadGraph' methods,
 *      but only loads a '.gxl' file (no layout);
 * <li> DefaultGXL implements 'unmarshallGraph', which calls 'loadGraph' on
 *      JaxbGxlIO, and thus only loads a '.gxl' file;
 * <li> LayoutIO implements 'readLayout', which reads a '.gl' file;
 * <li> LayedOutXML overrides 'unmarshallGraph' of its parent, by first
 *      calling 'unmarshallGraph' from its parent, then calling 'readLayout'
 *      from LayoutIO, and then combining the two;
 *      [if LayedOutXML is called with a GraphFactory as argument, it creates
 *       a DefaultGXL instance for loading the .gxl file, and adds the
 *       LayoutIO.readLayout to it for reading the layout];
 * <li> DefaultFileSystemStore creates a marshaller either of type LayedOutXML
 *      or of type DefaultGXL;
 * <li> DefaultArchiveSystem does not create a marshaller internally, but
 *      instead explicitly creates a JaxbGxlIO instance for reading the .gxl
 *      and explicitly calls 'readLayout' to add the layout to it.
 *      Reason to do it differently here: in a .zip file, you cannot derive
 *      the location of the .gl file out of the location of the .gxl file.
 *      And, the unmarshaller only operates on the .gxl file (as an input
 *      stream). It can therefore not be overloaded to work both for the layout
 *      and the non-layout case.
 * </ul>
 * Conclusion: none yet. I dont know how to implement the ideas above, because
 * DefaultArchiveSystem behaves differently than DefaultFileSystemStore (and
 * also because the latter can do both layout and no-layout, by means of a
 * boolean argument). Ideally, only 'LayoutIO', 'LayedOutXML' and 'JaxbGxlIO'
 * should go in the legacy package.
 * 
 * @author Maarten de Mol
 */
public class Info {
    // empty - see JavaDoc
}
