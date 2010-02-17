/**
 * 
 */
package groove.util;

import java.util.HashMap;

import javax.swing.JOptionPane;

/**
 * Class to include version info in a maintainable way. Taken from <a
 * href="http://forum.java.sun.com/thread.jspa?forumID=31&threadID=583820">here</a>
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
        return TITLE + " " + NUMBER + " (Date: " + DATE + ", build " + BUILD
            + ") - (C) University of Twente";
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

    /** Build number (timestamp with format yyyyMMddHHmmssSSS). */
    public static final String BUILD = "2010"; // eventually automatically set
    // during Ant compilation!

    /** Release date of this version (date format dd.MM.yyyy). */
    public static final String DATE = "03.02.2010"; // eventually automatically
    // set during Ant
    // compilation!

    /**
     * Groove Version number of format x.y.z, with
     * <ul>
     * <li>x = major version
     * <li>y = minor version
     * <li>z = bug fix version
     * </ul>
     * or developer version.
     */
    public static final String NUMBER = "Development";

    /** Minimum Java JRE version required. */
    static public final String NUMBER_JAVAMIN = "1.5";

    /** Title of this project. */
    static public final String TITLE = "GROOVE";

    /**
     * Version number of the GXL format used for storing rules and graphs. Known
     * version are:
     * <ul>
     * <li> <b>null</b>: no version info.
     * <li> <b>curly</b>: use curly braces for regular expressions; quotes are
     * taken literally in graphs, but surround atoms in rules.
     * </ul>
     */
    static public final String GXL_VERSION = "curly";

    /**
     * Map that relates the current grammar version to the Groove version where
     * it was introduced.
     */
    public static HashMap<String,String> GRAMMAR_GROOVE_VERSION_MAP;

    static {
        GRAMMAR_GROOVE_VERSION_MAP = new HashMap<String,String>();
        GRAMMAR_GROOVE_VERSION_MAP.put("1.0", "0.0.0");
        GRAMMAR_GROOVE_VERSION_MAP.put("2.0", "3.3.2");
    }

    /**
     * @return the latest grammar version.
     */
    public static String getLastGrammarVersion() {
        return "2.0";
    }

    /**
     * @return the grammar version that is to be used when the grammar
     * properties has no entry for the version. 
     */
    public static String getInitialGrammarVersion() {
        return "1.0";
    }

    /**
     * @return current Groove version.
     */
    public static String getGrooveVersion() {
        return NUMBER;
    }

    /**
     * Returns the minimum groove version that can open the grammar version
     * given.
     */
    public static String getMinGrooveVersionForGrammar(String grammarVersion) {
        return GRAMMAR_GROOVE_VERSION_MAP.get(grammarVersion);
    }

    /**
     * @return the Groove version that is to be used when the grammar
     * properties has no entry for the version. 
     */
    public static String getApproximateGrooveVersion() {
        return getMinGrooveVersionForGrammar(getInitialGrammarVersion());
    }
}
