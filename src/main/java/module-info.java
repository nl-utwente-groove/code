/**
 * Module declaration for GROOVE
 */
module nl.utwente.groove {
    exports nl.utwente.groove.control.template;
    exports nl.utwente.groove.gui.menu;
    exports nl.utwente.groove.gui.display;
    exports nl.utwente.groove.io.conceptual.configuration;
    exports nl.utwente.groove.algebra;
    exports nl.utwente.groove.prolog;
    exports nl.utwente.groove.grammar.groovy;
    exports nl.utwente.groove.prolog.util;
    exports nl.utwente.groove.io.external.format;
    exports nl.utwente.groove.automaton;
    exports nl.utwente.groove.io.conceptual.graph;
    exports nl.utwente.groove.grammar;
    exports nl.utwente.groove.gui.action;
    exports nl.utwente.groove.control.parse;
    exports nl.utwente.groove.io.conceptual;
    exports nl.utwente.groove.prolog.builtin.lts;
    exports nl.utwente.groove.gui.dialog;
    exports nl.utwente.groove.control.instance;
    exports nl.utwente.groove.gui.list;
    exports nl.utwente.groove.gui.look;
    exports nl.utwente.groove.gui.tree;
    exports nl.utwente.groove.io.conceptual.lang;
    exports nl.utwente.groove.algebra.syntax;
    exports nl.utwente.groove.explore;
    exports nl.utwente.groove.util.line;
    exports nl.utwente.groove.explore.encode;
    exports nl.utwente.groove.match.plan;
    exports nl.utwente.groove.graph;
    exports nl.utwente.groove.gui.dialog.config;
    exports nl.utwente.groove.explore.result;
    exports nl.utwente.groove.io.external;
    exports nl.utwente.groove.explore.strategy;
    exports nl.utwente.groove.grammar.rule;
    exports nl.utwente.groove.io.conceptual.property;
    exports nl.utwente.groove.explore.config;
    exports nl.utwente.groove.io.conceptual.lang.ecore;
    exports nl.utwente.groove.transform.oracle;
    exports nl.utwente.groove.graph.multi;
    exports nl.utwente.groove.io.graph;
    exports nl.utwente.groove;
    exports nl.utwente.groove.io.conceptual.configuration.schema;
    exports nl.utwente.groove.util.parse;
    exports nl.utwente.groove.io.store;
    exports nl.utwente.groove.transform.criticalpair;
    exports nl.utwente.groove.io.conceptual.type;
    exports nl.utwente.groove.match.rete;
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
    exports nl.utwente.groove.sts;
    exports nl.utwente.groove.verify;
    exports nl.utwente.groove.gui.jgraph;
    exports nl.utwente.groove.prolog.builtin;
    exports nl.utwente.groove.grammar.aspect;
    exports nl.utwente.groove.io.conceptual.value;
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
    exports nl.utwente.groove.io.conceptual.lang.groove;
    exports nl.utwente.groove.io.conceptual.lang.gxl;
    exports nl.utwente.groove.util;
    exports nl.utwente.groove.io.external.util;
    exports nl.utwente.groove.prolog.builtin.graph;
    exports nl.utwente.groove.explore.util;
    exports nl.utwente.groove.prolog.builtin.rule;
    exports nl.utwente.groove.explore.prettyparse;
    exports nl.utwente.groove.lts;
    exports nl.utwente.groove.util.cache;
    exports nl.utwente.groove.util.collect;

    requires antlr.complete;
    requires antlrworks;
    requires transitive args4j;
    requires transitive gnuprologjava;
    requires transitive jakarta.xml.bind;
    requires transitive java.desktop;
    requires transitive java.prefs;
    requires java.xml;
    requires jgoodies.looks;
    requires transitive jgraph;
    requires ltl2buchi;
    requires transitive nl.utwente.groove.gxl;
    requires opencsv;
    requires org.apache.groovy;
    requires org.eclipse.emf.common;
    requires org.eclipse.emf.ecore;
    requires org.eclipse.emf.ecore.xmi;
    requires transitive org.eclipse.jdt.annotation;
    requires osxadapter;
    requires rsyntaxtextarea;
    requires transitive xmlgraphics.commons;
    requires transitive batik.all;
    requires transitive jdk.xml.dom;
    requires transitive fop.core;

    opens nl.utwente.groove.explore to args4j;
    opens nl.utwente.groove.verify to args4j;
    opens nl.utwente.groove.gui to args4j;
    opens nl.utwente.groove.io to args4j;
    opens nl.utwente.groove.match.rete to args4j;
    opens nl.utwente.groove.prolog to args4j;
    opens nl.utwente.groove.util to args4j;
    opens nl.utwente.groove.util.cli to args4j;

    // the following opens clauses are required to allow GROOVE to access
    // these resources at runtime
    opens nl.utwente.groove.resource;
    opens nl.utwente.groove.resource.icon;
    opens nl.utwente.groove.resource.font;
    opens nl.utwente.groove.resource.version;
    opens nl.utwente.groove.resource.antlr;
}