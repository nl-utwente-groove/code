// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2023 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package nl.utwente.groove.gui.dialog;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.io.ExtensionFilter;
import nl.utwente.groove.util.io.FileType;

/**
 * Swing file-chooser adapter for the (plain {@link java.io.FileFilter})
 * {@link ExtensionFilter} of a file type.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
public class SwingExtensionFilter extends javax.swing.filechooser.FileFilter {
    private SwingExtensionFilter(FileType fileType) {
        this.inner = fileType.getFilter();
    }

    /** The wrapped extension filter. */
    private final ExtensionFilter inner;

    /** Returns the file type associated with this filter. */
    public FileType getFileType() {
        return this.inner.getFileType();
    }

    @Override
    public boolean accept(@Nullable File file) {
        return file != null && this.inner.accept(file);
    }

    @Override
    public String getDescription() {
        return this.inner.getDescription();
    }

    /** Returns the (unique) Swing filter for a given file type. */
    public static SwingExtensionFilter getFilter(FileType fileType) {
        return filterMap.computeIfAbsent(fileType, SwingExtensionFilter::new);
    }

    /** Map from file types to their Swing filters. */
    private static final Map<FileType,SwingExtensionFilter> filterMap
        = new EnumMap<>(FileType.class);
}
