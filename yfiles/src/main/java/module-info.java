/**
 * Module declaration for the yFiles graph-visualisation backend of GROOVE.
 * <p>
 * The module exports nothing: GROOVE finds the backend as a service, and nothing
 * else is to build on it. GROOVE exports the packages of its view layer to this
 * module by name, and to no other.
 */
module nl.utwente.groove.yfiles {
    requires nl.utwente.groove;
    // the yFiles library, which is a named module
    requires com.yworks.yfiles.complete;

    provides nl.utwente.groove.gui.view.GraphBackend
        with nl.utwente.groove.gui.yfiles.YFilesBackend;
}
