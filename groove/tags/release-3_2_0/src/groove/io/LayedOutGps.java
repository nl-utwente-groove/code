/* $Id: LayedOutGps.java,v 1.3 2008-01-30 09:33:42 iovka Exp $ */
package groove.io;


/**
 * Grammar marshaller that reads in and constructs layout information for the
 * files.
 * @author Arend Rensink
 * @version $Revision $
 */
@Deprecated
public class LayedOutGps extends FileGps {
    /**
     * Constructs an instance of the grammar marshaller.
     */
    public LayedOutGps() {
        super(true);
    }
}
