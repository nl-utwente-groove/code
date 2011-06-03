package groove.gui;

/** Type of edits that are distinguished. */
public enum EditType {
    /** Indicates that an element was created. */
    CREATE("New"),
    /** Indicates that an element was deleted. */
    DELETE("Delete"),
    /** Indicates that an element was renamed. */
    RENAME("Rename"),
    /** Indicates that an element was copied. */
    COPY("Copy"),
    /** Indicates that an element was modified. */
    MODIFY("Change");

    private EditType(String name) {
        this.name = name;
    }

    /** Returns the description of this kind of edit. */
    public String getName() {
        return this.name;
    }

    final private String name;
}