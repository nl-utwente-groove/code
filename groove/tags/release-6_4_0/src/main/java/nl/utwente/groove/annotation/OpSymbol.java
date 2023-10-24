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
package nl.utwente.groove.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import nl.utwente.groove.util.parse.OpKind;

/**
 * Annotation superclass for (prefix or infix) operators.
 * @author Rensink
 * @version $Revision $
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface OpSymbol {
    /** The operator symbol. */
    public String symbol();

    /**
     * The operator kind.
     * This contains further information, such as the pre- or infix nature of the operator.
     */
    public OpKind kind();
}
