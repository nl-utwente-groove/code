package groove.io.store;

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
}