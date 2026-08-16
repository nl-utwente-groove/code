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
package nl.utwente.groove.util;

import static nl.utwente.groove.util.Resources.RESOURCE_PACKAGE;
import static nl.utwente.groove.util.Resources.getResource;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.UIManager;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.QualName;

/**
 * Provider of the fonts used for text formatting throughout the tool,
 * in particular in the HTML rendering of labels and messages.
 * The GUI can register an initialiser (see {@link #setInitializer}) that is
 * invoked before the first font is computed, to make sure the look-and-feel
 * from which the fonts derive has been set up by then; headless runs never
 * register one, and get the plain UIManager defaults.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class Fonts {
    /**
     * Registers a hook to be invoked once, before the first font is computed.
     * Registration only has an effect if no font has been computed yet.
     */
    public static void setInitializer(Runnable initializer) {
        Fonts.initializer = initializer;
    }

    /** Runs and clears the registered initialiser, if any. */
    private static void init() {
        Runnable init = initializer;
        initializer = null;
        if (init != null) {
            init.run();
        }
    }

    /** The registered initialiser hook, invoked lazily by {@link #init()}. */
    private static @Nullable Runnable initializer;

    /** Returns the default font used for node and edge labels. */
    public static Font getLabelFont() {
        Font result = labelFont;
        if (result == null) {
            init();
            result = UIManager.getFont("Label.font");
            if (result == null) {
                result = UIManager.getDefaults().getFont("SansSerif");
            }
            if (result == null) {
                result = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
            }
            labelFont = result;
        }
        return result;
    }

    /** The default font used for node and edge labels. */
    private static @Nullable Font labelFont;

    /** Returns the font for special (arrow-like) characters,
     * or {@code null} if no suitable font can be found. */
    public static @Nullable Font getSymbolFont() {
        Font result = symbolFont;
        if (result == null) {
            init();
            result = getLabelFont();
            if (!result.canDisplay(Unicode.DT)) {
                result = UIManager.getDefaults().getFont("SansSerif");
            }
            if (result == null || !result.canDisplay(Unicode.DT)) {
                result = loadFont("stixgeneralregular.ttf");
            }
            symbolFont = result;
        }
        return result;
    }

    /** The font for special (arrow-like) characters. */
    private static @Nullable Font symbolFont;

    /** Loads in a TrueType font of a given name, at the label font size. */
    private static @Nullable Font loadFont(String name) {
        Font result = null;
        try (InputStream stream = getResource(FONT_PACKAGE.extend(name)).openStream()) {
            result = Font.createFont(Font.TRUETYPE_FONT, stream);
            result = result.deriveFont(getLabelFont().getSize2D());
        } catch (FileNotFoundException e) {
            // do nothing
        } catch (FontFormatException e) {
            // do nothing
        } catch (IOException e) {
            // do nothing
        }
        return result;
    }

    /** Name of the font sub-package of the GROOVE resource package. */
    static public final String FONT_PACKAGE_TOKEN = "font";
    /** Absolute qualified name of the font resource package. */
    static public final QualName FONT_PACKAGE = RESOURCE_PACKAGE.extend(FONT_PACKAGE_TOKEN);
}
