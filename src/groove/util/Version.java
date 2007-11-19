/**
 * 
 */
package groove.util;

import javax.swing.JOptionPane;

/**
 * Class to include version info in a maintainable way.
 * Taken from <a href="http://forum.java.sun.com/thread.jspa?forumID=31&threadID=583820">here</a>
 * @author Arend Rensink, at the suggestion of Christian Hofmann
 * @version $Revision$
 */
public class Version {
	/**
	 * Print version information to system console (System.out).
	 * @param args Not required.
	 */
	public static void main(String[] args) {
		System.out.println(getAbout());
		JOptionPane.showMessageDialog(null, getAboutHTML(), "About",
				JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}

	/**
	 * Get about information of this project (plain text).
	 * 
	 * @return 'About' information.
	 */
	static public String getAbout() {
		return TITLE + " " + NUMBER + " (Date: " + DATE + ", build " + BUILD + ") - (C) University of Twente";
	}

	/**
	 * Get about information of this project (HTML formatted). 
	 * @return 'About' information.
	 */
	static public String getAboutHTML() {
		StringBuffer sb = new StringBuffer("<html><center><font size=+2>");
		sb.append(TITLE);
		sb.append("</font></center><br>Version: ");
		sb.append(NUMBER);
		sb.append("<br><font size=-2>(Date: ");
		sb.append(DATE);
		sb.append(", build: ");
		sb.append(BUILD);
		sb.append(")</font><br>Java required: ");
		sb.append(NUMBER_JAVAMIN);
		sb.append("<hr size=1>\u00a9 ");
		sb.append("University of Twente");
		sb.append("</html>");

		return sb.toString();
	}
	
	/** Tests if a given string represents a known GXL file format. */
	static public boolean isKnownGxlVersion(String version) {
	    return version == null || GXL_VERSION.equals(version);
	}
    //	/** 
    //	 * The set of known info keys.
    //	 * These keys are the ones <i>not</i> stored as part of the graph properties.
    //	 */
    //	public static final Set<String> KNOWN_KEYS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(ERRORS_KEY, FILE_KEY, ROLE_KEY, NAME_KEY, PROPERTIES_KEY, LAYOUT_KEY)));
    //	/** 
    //	 * Start character that distinguishes user-defined property keys from graph info keys.
    //	 * Any string starting with this character is a graph info key. 
    //	 * @see #getInfoKey(String)
    //	 */ 
    //	static public final char INFO_KEY_START = '$';
    /** Build number (timestamp with format yyyyMMddHHmmssSSS). */
    public static final String BUILD = "2007"; //eventually automatically set during Ant compilation!

    /** Release date of this version (date format dd.MM.yyyy). */
    public static final String DATE = "18.04.2007"; //eventually automatically set during Ant compilation!

    /**
     * Version number of format x.y.z, with
     * <ul>
     * <li>x = major version
     * <li>y = minor version
     * <li>z = bug fix version
     * </ul>
     */
    public static final String NUMBER = "1.6.0";

    /** Minimum Java JRE version required. */
    static public final String NUMBER_JAVAMIN = "1.5";

    /** Title of this project. */
    static public final String TITLE = "GROOVE";

    /** 
     * Version number of the GXL format used for storing rules and graphs.
     * Known version are:
     * <ul>
     * <li> <b>null</b>: no version info.
     * <li> <b>curly</b>: use curly braces for regular expressions; quotes are taken literally in
     * graphs, but surround atoms in rules.
     * </ul>
     */
    static public final String GXL_VERSION = "curly";
}
