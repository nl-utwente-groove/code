/**
 * 
 */
package groove.util;

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
     * A '+' sign at the end of the number indicates a development version.
     */
    // TODO: Change this to 4.0.0 when making the new release.
    public static final String NUMBER = "3.3.1+";

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
     * @return the latest grammar version.
     */
    public static String getCurrentGrammarVersion() {
        return GRAMMAR_VERSION_2_0;
    }

    /**
     * @return current Groove version.
     */
    public static String getCurrentGrooveVersion() {
        return NUMBER;
    }

    /**
     * @return the grammar version that is to be used when the grammar
     * properties has no entry for the version. 
     */
    public static String getInitialGrammarVersion() {
        return GRAMMAR_VERSION_1_0;
    }

    /**
     * @return the Groove version that is to be used when the grammar
     * properties has no entry for the version. 
     */
    public static String getInitialGrooveVersion() {
        return "0.0.0";
    }

    /**
     * Compares two strings with version information and returns an integer
     * from the comparison. The strings should be well formed version strings: 
     * numbers separated with dots, with same length.
     * @param s1 Version string of the form 0.0.0...
     * @param s2 Version string of the form 0.0.0...
     * @return 0 if the versions are equal, 1 if s1 > s2, and -1 otherwise.
     */
    public static int compareGrammarVersions(String s1, String s2) {
        int result = -1;
        if (s1.equals(s2)) {
            // The strings are equal, no need to look into version numbers.
            result = 0;
        } else {
            String[] as1 = s1.split("\\.");
            String[] as2 = s2.split("\\.");
            if (as1.length == as2.length) {
                for (int i = 0; i < as1.length; i++) {
                    int n1 = Integer.parseInt(as1[i]);
                    int n2 = Integer.parseInt(as2[i]);
                    if (n1 > n2) {
                        result = 1;
                        break;
                    } else if (n1 < n2) {
                        result = -1;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Checks whether version s1 can open version s2.
     * @param s1
     * @param s2
     * @return true if s1 can open s2, false otherwise.
     */
    public static boolean canOpen(String s1, String s2) {
        return compareGrammarVersions(s1, s2) != -1;
    }

    // Grammar Versions
    // IMPORTANT: Do not forget to create a proper FileFilterAction for the
    // save grammar as option.

    /**
     * This is the grammar version associated with Groove version 3.3.1 or less.
     * This version may contain all functionality except types.
     */
    public static final String GRAMMAR_VERSION_1_0 = "1.0";
    /**
     * This is the grammar version associated with Groove version 4.0.0 or more.
     * This version introduced typing.
     */
    public static final String GRAMMAR_VERSION_2_0 = "2.0";

}
