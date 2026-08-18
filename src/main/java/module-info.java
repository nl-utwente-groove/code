/**
 * Module declaration for GROOVE
 */
module nl.utwente.groove {
    exports nl.utwente.groove.control.template;
    exports nl.utwente.groove.gui.menu;
    exports nl.utwente.groove.gui.display;
    exports nl.utwente.groove.algebra;
    exports nl.utwente.groove.prolog;
    exports nl.utwente.groove.gui.prolog;
    exports nl.utwente.groove.prolog.util;
    exports nl.utwente.groove.io.external.format;
    exports nl.utwente.groove.io.external.format.ecore;
    exports nl.utwente.groove.match.automaton;
    exports nl.utwente.groove.grammar;
    exports nl.utwente.groove.gui.action;
    exports nl.utwente.groove.control.parse;
    exports nl.utwente.groove.prolog.builtin.lts;
    exports nl.utwente.groove.gui.dialog;
    exports nl.utwente.groove.control.instance;
    exports nl.utwente.groove.gui.list;
    exports nl.utwente.groove.gui.look;
    exports nl.utwente.groove.gui.tree;
    exports nl.utwente.groove.algebra.syntax;
    exports nl.utwente.groove.explore;
    exports nl.utwente.groove.util.line;
    exports nl.utwente.groove.match.plan;
    exports nl.utwente.groove.graph;
    exports nl.utwente.groove.graph.layout;
    exports nl.utwente.groove.explore.config;
    exports nl.utwente.groove.explore.feature;
    exports nl.utwente.groove.gui.export;
    exports nl.utwente.groove.explore.engine;
    exports nl.utwente.groove.explore.result;
    exports nl.utwente.groove.io.external;
    exports nl.utwente.groove.explore.verify;
    exports nl.utwente.groove.grammar.rule;
    exports nl.utwente.groove.transform.oracle;
    exports nl.utwente.groove.io.graph;
    exports nl.utwente.groove;
    exports nl.utwente.groove.util.parse;
    exports nl.utwente.groove.io.store;
    exports nl.utwente.groove.transform.criticalpair;
    exports nl.utwente.groove.gui.layout;
    exports nl.utwente.groove.transform;
    exports nl.utwente.groove.graph.iso;
    exports nl.utwente.groove.match;
    exports nl.utwente.groove.prolog.builtin.type;
    exports nl.utwente.groove.gui;
    exports nl.utwente.groove.prolog.builtin.trans;
    exports nl.utwente.groove.grammar.type;
    exports nl.utwente.groove.annotation;
    exports nl.utwente.groove.graph.plain;
    exports nl.utwente.groove.verify;
    exports nl.utwente.groove.gui.jgraph;
    exports nl.utwente.groove.prolog.builtin;
    exports nl.utwente.groove.grammar.aspect;
    exports nl.utwente.groove.io;
    exports nl.utwente.groove.util.antlr;
    exports nl.utwente.groove.control.graph;
    exports nl.utwente.groove.control.term;
    exports nl.utwente.groove.grammar.model;
    exports nl.utwente.groove.util.cli;
    exports nl.utwente.groove.control;
    exports nl.utwente.groove.grammar.host;
    exports nl.utwente.groove.prolog.builtin.algebra;
    exports nl.utwente.groove.prolog.exception;
    exports nl.utwente.groove.util;
    exports nl.utwente.groove.gui.export.util;
    exports nl.utwente.groove.prolog.builtin.graph;
    exports nl.utwente.groove.explore.util;
    exports nl.utwente.groove.prolog.builtin.rule;
    exports nl.utwente.groove.lts;
    exports nl.utwente.groove.util.cache;
    exports nl.utwente.groove.util.collect;
    exports nl.utwente.groove.util.io;

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

    requires antlr.complete;
    requires antlrworks;
    requires transitive info.picocli;
    requires transitive gnuprologjava;
    requires transitive jakarta.xml.bind;
    requires transitive java.desktop;
    requires transitive java.prefs;
    requires java.logging;
    requires java.xml;
    requires com.formdev.flatlaf;
    requires transitive jgraph;
    requires ltl2buchi;
    requires org.eclipse.emf.common;
    requires org.eclipse.emf.ecore;
    requires org.eclipse.emf.ecore.xmi;
    requires transitive nl.utwente.groove.gxl;
    requires com.opencsv;
    requires org.apache.groovy;
    requires transitive org.eclipse.jdt.annotation;
    requires osxadapter;
    requires org.fife.RSyntaxTextArea;
    requires transitive xmlgraphics.commons;
    requires transitive batik.all;
    requires transitive jdk.xml.dom;
    requires transitive fop.core;

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
