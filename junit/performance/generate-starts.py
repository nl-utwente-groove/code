#!/usr/bin/env python3
"""Generates the larger start graphs of the performance grammars.

The grammars under junit/performance are copies of junit/samples grammars,
kept only for the exploration benchmark (test/performance/ExplorationBenchmark).
Seven of them have start graphs with a regular structure, so larger instances
are generated here rather than drawn by hand. Run from the repository root:

    python junit/performance/generate-starts.py

The SIZES table at the bottom lists what gets generated; the calibration
behind the chosen sizes is recorded in claude/exploration-performance.md.
Regenerating overwrites the listed files and nothing else.
"""

import os

GXL_NS = "http://www.gupro.de/GXL/gxl-1.0.dtd"
ROOT = os.path.dirname(os.path.abspath(__file__))


class Graph:
    """Plain graph with string-labelled edges; self-edges are node labels."""

    def __init__(self, name):
        self.name = name
        self.nodes = []
        self.edges = []

    def node(self, *labels):
        n = "n%d" % len(self.nodes)
        self.nodes.append(n)
        for l in labels:
            self.edges.append((n, l, n))
        return n

    def edge(self, src, label, tgt):
        self.edges.append((src, label, tgt))

    def write(self, grammar):
        cols = max(4, int(len(self.nodes) ** 0.5) + 1)
        out = ['<?xml version="1.0" encoding="UTF-8" standalone="yes"?>',
               '<gxl xmlns="%s">' % GXL_NS,
               '    <graph edgemode="directed" edgeids="false" role="graph" id="%s">'
               % self.name,
               '        <attr name="$version">',
               '            <string>curly</string>',
               '        </attr>']
        for i, n in enumerate(self.nodes):
            x, y = 40 + 90 * (i % cols), 40 + 70 * (i // cols)
            out += ['        <node id="%s">' % n,
                    '            <attr name="layout">',
                    '                <string>%d %d 30 30</string>' % (x, y),
                    '            </attr>',
                    '        </node>']
        for s, l, t in self.edges:
            out += ['        <edge from="%s" to="%s">' % (s, t),
                    '            <attr name="label">',
                    '                <string>%s</string>' % l,
                    '            </attr>',
                    '        </edge>']
        out += ['    </graph>', '</gxl>', '']
        path = os.path.join(ROOT, grammar, self.name + ".gst")
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            f.write("\n".join(out))
        print("%s/%s.gst: %d nodes, %d edges" % (grammar, self.name,
              len(self.nodes), len(self.edges)))


def mark_unmark(n):
    """A `next`-tree of n nodes below the root: node i hangs under node
    (i-1)//2, so the tree is a complete binary tree like the sample's
    branching, only regular."""
    g = Graph("tree-%d" % n)
    nodes = [g.node("root")] + [g.node() for _ in range(n - 1)]
    for i in range(1, n):
        g.edge(nodes[(i - 1) // 2], "next", nodes[i])
    return g


def as_and_bs(na, nb):
    """na A-nodes and nb B-nodes with a `b` edge from every B to every A,
    as in the sample's 3+3 start graph."""
    g = Graph("start-%d-%d" % (na, nb))
    a_nodes = [g.node("A") for _ in range(na)]
    b_nodes = [g.node("B") for _ in range(nb)]
    for b in b_nodes:
        for a in a_nodes:
            g.edge(b, "b", a)
    return g


def inheritance(n):
    """n nodes typed A, B, C in turn; `a` edges from every node to its
    successor and from every second node to the node three further on,
    an `a` self-edge on every fourth node. Roughly the sample's density
    of ten edges on seven nodes."""
    g = Graph("start-%d" % n)
    nodes = [g.node("type:" + "ABC"[i % 3]) for i in range(n)]
    for i in range(n):
        g.edge(nodes[i], "a", nodes[(i + 1) % n])
        if i % 2 == 0:
            g.edge(nodes[i], "a", nodes[(i + 3) % n])
        if i % 4 == 0:
            g.edge(nodes[i], "a", nodes[i])
    return g


def append(appenders, length):
    """A list of the given length with values 1..length, and the given
    number of appenders, each with its own fresh value, in the sample's
    naming scheme append-<appenders>-list-<length>."""
    g = Graph("append-%d-list-%d" % (appenders, length))
    root = g.node("root")
    cells = [g.node() for _ in range(length)]
    g.edge(root, "list", cells[0])
    for i, c in enumerate(cells):
        g.edge(c, "val", g.node(str(i + 1)))
        if i + 1 < length:
            g.edge(c, "next", cells[i + 1])
    for j in range(appenders):
        a = g.node("append", "control")
        g.edge(a, "caller", root)
        g.edge(a, "this", cells[0])
        g.edge(a, "x", g.node(str(length + 1 + j)))
    return g


def leader_election(n):
    """A ring of n active processes as the sample's plain start graphs look
    after the number-picking stage: every process has `next` to its
    successor, `number` and `max` to its own unique value in 1..n, and
    `left` to a shared -1; a Scheduler has an `init` edge to every process,
    and the `Numbers` pool is present but empty, so `pick-number` never
    fires and the factorial assignment stage is skipped. (The sample's
    hand-drawn `-init` graphs meant the same but carry `type:` and `flag:`
    prefixes the rules do not use, so they explore to a single state.) The
    values are assigned around the ring in a fixed pseudo-random order; a
    sorted order would make the election trivially short."""
    g = Graph("ring-%d" % n)
    values = list(range(1, n + 1))
    seed = 12345
    for i in range(n - 1, 0, -1):  # Fisher-Yates with a fixed LCG
        seed = (seed * 1103515245 + 12345) % 2 ** 31
        j = seed % (i + 1)
        values[i], values[j] = values[j], values[i]
    scheduler = g.node("Scheduler")
    g.node("Numbers")
    left = g.node("int:-1")
    procs = [g.node("Process", "active") for _ in range(n)]
    for i, p in enumerate(procs):
        v = g.node("int:%d" % values[i])
        g.edge(p, "number", v)
        g.edge(p, "max", v)
        g.edge(p, "left", left)
        g.edge(p, "next", procs[(i + 1) % n])
        g.edge(scheduler, "init", p)
    return g


def count_to_n(bound):
    """The counter of attribute-count-to-n at 0 with the given bound: the
    state space is one state per value, so it grows linearly in the bound
    and measures the pure algebra path."""
    g = Graph("bound-%d" % bound)
    counter = g.node("counter", "let:bound=%d" % bound)
    g.edge(counter, "this", g.node("int:0"))
    return g


def fibonacci(x):
    """The single node of fibonacci carrying the argument x; both the
    recipe and the function control program compute fib(x) by the naive
    exponential recursion, so the state space grows with fib(x) itself,
    about 1.6-fold per step. The recipe keeps it in transient states, the
    function in plain ones; the larger sizes are for the function."""
    g = Graph("fib-%d" % x)
    g.node("let:x=%d" % x)
    return g


SIZES = [
    ("Mark-Unmark-List-regexp-benchmark.gps", mark_unmark, [(18,), (21,), (22,)]),
    ("As-and-Bs-reg-exp-benchmark.gps", as_and_bs, [(4, 3)]),
    ("inheritance.gps", inheritance, [(12,)]),
    ("append.gps", append, [(4, 10)]),
    ("leader-election.gps", leader_election, [(8,), (14,), (16,), (18,)]),
    ("attribute-count-to-n.gps", count_to_n, [(10000,), (100000,), (300000,), (600000,)]),
    ("fibonacci.gps", fibonacci, [(12,), (15,), (22,)]),
]

if __name__ == "__main__":
    for grammar, gen, sizes in SIZES:
        for args in sizes:
            gen(*args).write(grammar)
