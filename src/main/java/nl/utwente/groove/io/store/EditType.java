package nl.utwente.groove.io.store;

import nl.utwente.groove.grammar.model.ResourceKind;

/** Type of edits that are distinguished. */
public enum EditType {
    /** Creation of a new resource. */
    CREATE("New"),
    /** IDeletion of a resource. */
    DELETE("Delete"),
    /** Renaming of a resource. */
    RENAME("Rename"),
    /** Copying of a resource. */
    COPY("Copy"),
    /** Modification of a resource. */
    MODIFY("Edit"),
    /** Enabling or disabling of a resource. */
    ENABLE("Enable"),
    /** Layout change. */
    LAYOUT("Layout");

    private EditType(String name) {
        this.name = name;
    }

    /** Returns the name of this type of edit. */
    public String getName() {
        return this.name;
    }

    final private String name;

    /** Returns the enabling or disabling name. */
    public static String getEnableName(boolean enable) {
        return enable ? ENABLE.getName() : "Disable";
    }

    /**
     * Returns the action name for a resource edit.
     * A further parameter determines if the name is a description <i>before</i>
     * the action occurs, or after.
     * @param edit the edit for which the name is required
     * @param resource the kind of resource that is edited
     * @param dots if {@code true}, a ... prefix is appended
     * @return The appropriate action name
     */
    public static String getEditActionName(EditType edit, ResourceKind resource, boolean dots) {
        StringBuilder result = new StringBuilder(edit.getName());
        result.append(' ');
        result.append(resource.getName());
        if (dots) {
            result.append(" ...");
        }
        return result.toString();
    }

    /** Redo action name */
    public static final String REDO_ACTION_NAME = "Redo";
    /** Renumber action name */
    public static final String RENUMBER_ACTION_NAME = "Renumber Nodes";
    /** Replace action name */
    public static final String REPLACE_ACTION_NAME = "Replace Label";
    /** Grammar properties action name */
    public static final String SYSTEM_PROPERTIES_ACTION_NAME = "Grammar Properties ...";
    /** Undo action name */
    public static final String UNDO_ACTION_NAME = "Undo";
}