/**
 * Module declaration for GROOVE
 */
module nl.utwente.groove {
    // Exported packages form the API contract of the library. The set is kept
    // deliberately small: extending it on request is cheap, retracting an export
    // is a breaking change. -Xlint:exports (pom.xml) reports every exported
    // signature that mentions a type of an unexported package, so the set must
    // stay closed under signature reachability; extend it only for that reason
    // or for a real client need. Not exported: the Simulator and its view layer
    // (gui.*, under reconstruction for gh #909), the matching engines
    // (match.plan, match.automaton), the control compiler (control.parse, with
    // its generated ANTLR classes), the exploration engine (explore.engine,
    // explore.util), the concrete import/export formats, the Prolog predicate
    // implementations and the remaining utility packages.
    //
    // -- the pipeline from grammar on disk to state space
    exports nl.utwente.groove;
    exports nl.utwente.groove.io.store;
    exports nl.utwente.groove.io.graph;
    exports nl.utwente.groove.io.external;
    exports nl.utwente.groove.grammar;
    exports nl.utwente.groove.grammar.model;
    exports nl.utwente.groove.grammar.aspect;
    exports nl.utwente.groove.grammar.host;
    exports nl.utwente.groove.grammar.type;
    exports nl.utwente.groove.grammar.rule;
    exports nl.utwente.groove.graph;
    exports nl.utwente.groove.graph.plain;
    exports nl.utwente.groove.graph.iso;
    exports nl.utwente.groove.graph.layout;
    exports nl.utwente.groove.match;
    exports nl.utwente.groove.transform;
    exports nl.utwente.groove.transform.oracle;
    exports nl.utwente.groove.lts;
    exports nl.utwente.groove.explore;
    exports nl.utwente.groove.explore.config;
    exports nl.utwente.groove.explore.feature;
    exports nl.utwente.groove.explore.result;
    exports nl.utwente.groove.explore.engine;
    exports nl.utwente.groove.verify;
    exports nl.utwente.groove.prolog;
    exports nl.utwente.groove.prolog.builtin;
    // -- graph-backend SPI: an add-on jar is loaded into the unnamed module and
    //    implements/extends these types, so they must be exported for GROOVE
    //    to run from the module path (ExtensionsTest); GraphViewController still
    //    leaks Simulator types into this tier, see claude/module-exports.md
    exports nl.utwente.groove.gui.view;
    exports nl.utwente.groove.gui.view.cell;
    exports nl.utwente.groove.gui.look;
    exports nl.utwente.groove.gui.layout;
    // -- Prolog predicate classes are instantiated by the Prolog engine
    exports nl.utwente.groove.prolog.builtin.algebra to gnuprologjava;
    exports nl.utwente.groove.prolog.builtin.graph to gnuprologjava;
    exports nl.utwente.groove.prolog.builtin.lts to gnuprologjava;
    exports nl.utwente.groove.prolog.builtin.rule to gnuprologjava;
    exports nl.utwente.groove.prolog.builtin.trans to gnuprologjava;
    exports nl.utwente.groove.prolog.builtin.type to gnuprologjava;
    // -- data values and their expressions
    exports nl.utwente.groove.algebra;
    exports nl.utwente.groove.algebra.syntax;
    exports nl.utwente.groove.annotation;
    // -- control programs: declarations, compiled templates, runtime automata
    exports nl.utwente.groove.control;
    exports nl.utwente.groove.control.term;
    exports nl.utwente.groove.control.template;
    exports nl.utwente.groove.control.instance;
    exports nl.utwente.groove.control.graph;
    // -- utilities reachable from the above
    exports nl.utwente.groove.util;
    exports nl.utwente.groove.util.parse;
    exports nl.utwente.groove.util.line;
    exports nl.utwente.groove.util.cache;
    exports nl.utwente.groove.util.collect;
    exports nl.utwente.groove.util.io;
    exports nl.utwente.groove.util.cli;

    // service contributions; each provider is also declared in META-INF/services,
    // which takes over when GROOVE runs from the class path (as the installed
    // application does)
    uses nl.utwente.groove.grammar.model.SettingsSchema.Provider;
    provides nl.utwente.groove.grammar.model.SettingsSchema.Provider
        with nl.utwente.groove.io.external.format.ecore.EcoreMappingSchema.Provider,
        nl.utwente.groove.explore.config.ExploreConfigSchema.Provider;
    uses nl.utwente.groove.grammar.model.ResourceValidator;
    provides nl.utwente.groove.grammar.model.ResourceValidator
        with nl.utwente.groove.prolog.PrologValidator;
    uses nl.utwente.groove.gui.view.GraphBackend;
    provides nl.utwente.groove.gui.view.GraphBackend
        with nl.utwente.groove.gui.jgraph.JGraphBackend;

    requires antlr.complete;
    requires antlrworks;
    requires transitive info.picocli;
    requires transitive gnuprologjava;
    requires transitive jakarta.xml.bind;
    requires transitive java.desktop;
    requires transitive java.prefs;
    requires java.logging;
    requires java.net.http;
    requires java.xml;
    requires com.formdev.flatlaf;
    requires jgraph;
    requires ltl2buchi;
    requires org.eclipse.emf.common;
    requires org.eclipse.emf.ecore;
    requires org.eclipse.emf.ecore.xmi;
    requires transitive nl.utwente.groove.gxl;
    requires com.opencsv;
    requires org.apache.groovy;
    requires transitive org.eclipse.jdt.annotation;
    requires org.fife.RSyntaxTextArea;
    requires xmlgraphics.commons;
    requires batik.all;
    requires jdk.xml.dom;
    requires fop.core;

    opens nl.utwente.groove.explore to info.picocli;
    opens nl.utwente.groove.verify to info.picocli;
    opens nl.utwente.groove.gui to info.picocli;
    opens nl.utwente.groove.prolog to info.picocli;
    opens nl.utwente.groove.util to info.picocli;
    opens nl.utwente.groove.util.cli to info.picocli;

    // the following opens clauses are required to allow GROOVE to access
    // these resources at runtime
    opens nl.utwente.groove.resource;
    opens nl.utwente.groove.resource.icon;
    opens nl.utwente.groove.resource.font;
    opens nl.utwente.groove.resource.version;
    opens nl.utwente.groove.resource.antlr;
}
