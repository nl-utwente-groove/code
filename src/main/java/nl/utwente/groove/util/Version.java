/**
 *
 */
package nl.utwente.groove.util;

import static nl.utwente.groove.util.Groove.RESOURCE_PACKAGE;
import static nl.utwente.groove.util.Groove.getResourceStream;

import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.JOptionPane;

import nl.utwente.groove.grammar.QualName;

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
        JOptionPane
            .showMessageDialog(null, getAboutHTML(), "About", JOptionPane.INFORMATION_MESSAGE);
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
        return version == null || version.isEmpty() || GXL_VERSION.equals(version);
    }

    /** Name of the version sub-package of the Groove resource package. */
    public static final String VERSION_PACKAGE_TOKEN = "version";

    /** Qualified absolute name of the version package. */
    public static final QualName VERSION_PACKAGE = RESOURCE_PACKAGE.extend(VERSION_PACKAGE_TOKEN);

    /** Returns the first line of a named resource file. */
    private static String read(String filename) {
        try (var file = new BufferedReader(getResourceStream(VERSION_PACKAGE.extend(filename)))) {
            return file.readLine();
        } catch (IOException exc) {
            throw Exceptions.illegalArg("Can't read from %s", filename);
        }
    }

    /** Build number (timestamp with format yyyymmdd). */
    public static final String BUILD = read("GROOVE_BUILD");

    /** Release date of this version (date format dd.mm.yyyy). */
    public static final String DATE;

    static {
        String year = BUILD.substring(0, 4);
        String month = BUILD.substring(4, 6);
        String day = BUILD.substring(6, 8);
        DATE = day + "." + month + "." + year;
    }

    /** Suffix to the {@link #NUMBER} that indicates this is a development version. */
    static private final String SNAPSHOT_SUFFIX = "-SNAPSHOT";

    /**
     * Groove Version number of format x.y.z, with
     * <ul>
     * <li>x = major version
     * <li>y = minor version
     * <li>z = bug fix version
     * </ul>
     * The suffix {@link #SNAPSHOT_SUFFIX} indicates a development version.
     */
    public static final String NUMBER = read("GROOVE_VERSION");

    /** Minimum Java JRE version required. */
    static public final String NUMBER_JAVAMIN = read("JAVA_VERSION");

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
        return GRAMMAR_VERSION_3_11;
    }

    /**
     * @return current Groove version.
     */
    public static String getCurrentGrooveVersion() {
        return NUMBER;
    }

    /**
     * @return <code>true</code> if the current version is a development
     *         version, <code>false</code> otherwise
     */
    public static boolean isDevelopmentVersion() {
        return NUMBER.endsWith(SNAPSHOT_SUFFIX);
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
     * Compares the given grammar version with the current grammar version.
     * Only the first digit of the version is compared (a difference in the
     * second digit is not supposed to affect loading/saving graphs).
     * The strings should be well formed version strings:
     * numbers separated with dots, with same length.
     * @param version String of the form 0.0.0...
     * @return 0 if the major versions are equal,
     *         1 if current > version,
     *         -1 if version < current
     */
    public static int compareGrammarVersion(String version) {
        String current = getCurrentGrammarVersion();
        if (current.equals(version)) {
            // The strings are equal, no need to look into version numbers.
            return 0;
        } else {
            String[] as1 = current.split("\\.");
            String[] as2 = version.split("\\.");
            int n1 = 0, n2 = 0;
            if (as1.length > 0) {
                n1 = Integer.parseInt(as1[0]);
            }
            if (as2.length > 0) {
                n2 = Integer.parseInt(as2[0]);
            }
            if (n1 < n2) {
                return -1;
            } else if (n1 == n2) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    /**
     * Compare to arbitrary grammar versions, also looking at the non-major
     * version numbers.
     * @param version1 String of the form 0.0.0...
     * @param version2 String of the form 0.0.0...
     * @return 0 if versions are equal,
     *         1 if version1 > version2,
     *         -1 if version1 < version2
     */
    public static int compareGrammarVersions(String version1, String version2) {
        String[] as1 = version1.split("\\.");
        String[] as2 = version2.split("\\.");
        for (int i = 0; i < Math.max(as1.length, as2.length); i++) {
            int n1 = 0, n2 = 0;
            if (i < as1.length) {
                n1 = Integer.parseInt(as1[i]);
            }
            if (i < as2.length) {
                n2 = Integer.parseInt(as2[i]);
            }
            if (n1 < n2) {
                return -1;
            } else if (n1 > n2) {
                return 1;
            }
        }
        return 0;
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
     * This is the grammar version introduced with Groove version 4.0.0.
     * This version introduced typing.
     */
    public static final String GRAMMAR_VERSION_2_0 = "2.0";
    /**
     * This is the grammar version introduced with Groove version 4.2.0.
     * This version integrated layout into the .gxl files.
     */
    public static final String GRAMMAR_VERSION_3_0 = "3.0";
    /**
     * This is the grammar version introduced with Groove version 4.5.0.
     * This version added restrictions to the names of resources.
     */
    public static final String GRAMMAR_VERSION_3_1 = "3.1";
    /**
     * This is the grammar version introduced with Groove version 4.5.3.
     * From this version onward, the start graph name must be set explicitly
     * in the grammar properties.
     */
    public static final String GRAMMAR_VERSION_3_2 = "3.2";
    /**
     * This is the grammar version introduced with Groove version 4.9.3.
     * Attribute expressions are now stored in a more user-friendly format.
     */
    public static final String GRAMMAR_VERSION_3_3 = "3.3";
    /**
     * This is the grammar version introduced with Groove version 5.1.0.
     * Control much improved (atomicity, recursion);
     * Rule properties (conditions, constraints);
     * Several grammar properties added
     */
    public static final String GRAMMAR_VERSION_3_4 = "3.4";
    /**
     * This is the grammar version introduced with Groove version 5.3.0.
     * Much more flexible use of any (package.any, package.*.any)
     */
    public static final String GRAMMAR_VERSION_3_5 = "3.5";
    /**
     * This is the grammar version introduced with Groove version 5.2.0.
     * Assignment syntax for rule and recipe invocations, also in combination with declaration:
     * Instead of {@code node a; rule(1, out a)} use {@code node a; a := rule(1)}
     * or {@code node a := rule(1)}.
     */
    public static final String GRAMMAR_VERSION_3_6 = "3.6";
    /**
     * This is the grammar version introduced with Groove version 5.8.0.
     * It allows more operations:
     * <ul>
     * <li> {@code string:isBool}, {@code string:isInt} and {@code string:isReal} to test if a string is formatted as a data type value
     * <li> {@code string:toBool}, {@code string:toInt} and {@code string:toReal} to convert a string to a data type value
     * <li> {@code int:ite}, {@code real:ite} and {@code string:ite} to encode if-then-else expressions
     * <li> {@code string:substring}, {@code string:suffix} and {@code string:lookup} for substring manipulation
     * </ul>
     */
    public static final String GRAMMAR_VERSION_3_7 = "3.7";
    /**
     * This is the grammar version introduced with Groove version 6.1.0.
     * It introduces more flexibility in the use of cnew: edges adjacent to cnew:-nodes may now be
     * characterised as not: and new:, meaning that they are not considered to be part of the RHS resp. NAC
     */
    public static final String GRAMMAR_VERSION_3_8 = "3.8";
    /**
     * This is the grammar version introduced with Groove version 6.4.0.
     * letnew: was removed after a very short lifetime, in favour of new:let: and many more.
     * Moreover, named variables were introduced.
     */
    public static final String GRAMMAR_VERSION_3_9 = "3.9";
    /**
     * This is the grammar version introduced with Groove version 6.6.3.
     * The GrammarKey#USE_STORED_NODE_IDS was introduced to determine whether node numbers
     * should be based on the stored node IDs, as was the default behaviour before this version.
     * For older grammars, the property is automatically set to {@code true}; the default is
     * {@code false}.
     */
    public static final String GRAMMAR_VERSION_3_10 = "3.10";
    /**
     * This is the grammar version introduced with Groove version 6.9.0.
     * The {@code halt} keyword was added to the control language.
     */
    public static final String GRAMMAR_VERSION_3_11 = "3.11";
}
