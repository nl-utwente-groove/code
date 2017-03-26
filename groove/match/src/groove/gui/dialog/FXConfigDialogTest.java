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
package groove.gui.dialog;

import groove.explore.config.Null;
import groove.util.parse.Fallible;
import groove.util.parse.FormatErrorSet;
import javafx.application.Application;
import javafx.beans.value.ObservableValueBase;
import javafx.stage.Stage;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class FXConfigDialogTest extends Application {
    @Override
    public void start(Stage arg0) throws Exception {
        new FXConfigDialog<Config>() {
            @Override
            public Config createConfig() {
                return new Config();
            }
        }.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }

    private static class Config extends ObservableValueBase<Null> implements Fallible {
        @Override
        public Null getValue() {
            return null;
        }

        @Override
        public FormatErrorSet getErrors() {
            return this.errors;
        }

        private final FormatErrorSet errors = new FormatErrorSet();
    }
}
