#!/usr/bin/env python3
"""Generates the larger start graphs of the performance grammars.

The grammars under junit/performance are copies of junit/samples grammars,
kept only for the exploration benchmark (test/performance/ExplorationBenchmark).
Nine of them have start graphs with a regular structure, so larger instances
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
    and measures the pure algebra path. The probe-odd rule adds a self-loop
    per odd state and an ErrorValue (division by zero) per even one."""
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


def hub(n, k, shape):
    """The hub grammar's star: one Hub with a "to" edge to each of n Leaf
    nodes, a "token" flag on the first k. Under "run" the tokens move
    between leaves through the hub, and since the leaves are
    interchangeable every move yields an isomorphic graph: a one-state LTS
    whose k(n-k) transitions each certify an n+1-node graph with an
    (n-k)-fold symmetric leaf class, the row for finding 5.6. With shape
    "chain", consecutive leaves are linked by "next" and the "chain"
    program moves tokens along the chain only, never onto an occupied
    leaf: the leaves are then distinguishable and the states are the token
    placements, n for one token and about n^2/2 for two, each a graph of
    n+1 nodes. Shape "ring" closes the chain, so that a single token walks
    for ever: the shape for the unstored, bounded rows. The hub carries the
    "moves" counter that "moveCounted" increments (finding 3.7)."""
    g = Graph("%s-%d-%d" % (shape, n, k))
    hub = g.node("type:Hub", "let:leaves=0", "let:tokens=0", "let:moves=0")
    first = prev = None
    for i in range(n):
        leaf = g.node("type:Leaf", "flag:token") if i < k else g.node("type:Leaf")
        g.edge(hub, "to", leaf)
        if shape != "star" and prev is not None:
            g.edge(prev, "next", leaf)
        if first is None:
            first = leaf
        prev = leaf
    if shape == "ring":
        g.edge(prev, "next", first)
    return g


def field(hubs, n, m):
    """The hub grammar's field: several stars, each Hub with n Leaf nodes
    linked both ways ("to" and "from") and numbered by a "pos" attribute,
    plus m Stub nodes hanging off the hub by "stub" edges, half of them
    pointing towards the hub and half away from it. The stubs give the hub
    mixed incident edges (finding 5.1) and the graph a large majority of
    nodes of a type no rule binds (finding 5.2); nothing ever touches them.
    A token sits on the first leaf of the first hub, for "hop", which moves
    it to the leaf with the next position modulo the hub's "leaves" count,
    and another on the first hub itself, for "jump", which moves it to any
    other hub. Both rules have exactly one match per state with one token
    and two hubs, so a bounded unstored run is a single path."""
    g = Graph("field-%d-%d-%d" % (hubs, n, m))
    for h in range(hubs):
        labels = ["type:Hub", "let:leaves=%d" % n, "let:tokens=0", "let:moves=0"]
        if h == 0:
            labels.append("flag:token")
        hub = g.node(*labels)
        for i in range(n):
            labels = ["type:Leaf", "let:pos=%d" % i]
            if h == 0 and i == 0:
                labels.append("flag:token")
            leaf = g.node(*labels)
            g.edge(hub, "to", leaf)
            g.edge(leaf, "from", hub)
        for i in range(m):
            stub = g.node("type:Stub")
            if i % 2 == 0:
                g.edge(hub, "stub", stub)
            else:
                g.edge(stub, "stub", hub)
    return g


def petri_pipe(k, n):
    """The petrinet grammar's pipeline: k transitions in a row between k+1
    places, and n tokens on the first place. Every token moves forward
    independently, so the reachable markings are the ways of distributing
    n tokens over k+1 distinguishable places, C(n+k, k) of them, every
    marking a state; the graph stays at 2k+n+1 nodes. The quantified rule
    fires with a one-place universal domain per transition, so the row
    measures the nested-condition machinery per match and the composite
    events, at a known state count."""
    g = Graph("pipe-%d-%d" % (k, n))
    places = [g.node("place") for _ in range(k + 1)]
    for i in range(k):
        t = g.node("transition")
        g.edge(places[i], "in", t)
        g.edge(t, "out", places[i + 1])
    for _ in range(n):
        g.edge(places[0], "mark", g.node("token"))
    return g


def petri_join(f):
    """The petrinet grammar's join: one transition with f input places, each
    holding a token, and f output places, plus a second transition firing
    the tokens back. Exactly one transition is enabled at any time, so an
    unstored bounded run is a single path alternating the two, and every
    step matches a universal domain of f places on each side and applies
    a delta of 2f tokens: the fan-in axis of the quantifier cost."""
    g = Graph("join-%d" % f)
    fwd = g.node("transition")
    back = g.node("transition")
    for _ in range(f):
        src = g.node("place")
        tgt = g.node("place")
        g.edge(src, "in", fwd)
        g.edge(fwd, "out", tgt)
        g.edge(tgt, "in", back)
        g.edge(back, "out", src)
        g.edge(src, "mark", g.node("token"))
    return g


def parallel_pump(k, m):
    """The parallel-pump grammar's start graph: one hub carrying k parallel
    "c" loops (a single mult=k edge) and a "b" edge to each of m target
    nodes. "pump" turns a "c" into an "a" edge from the hub to a target,
    "drain" deletes one, "trim" deletes one of two parallel "a" edges to
    the same target and "fold" merges two targets, so the states are the
    distributions of the pumped edges over the targets that are left, and
    the graph never has more than m+1 nodes: the parallel-edge machinery
    per match, application and certificate, under DPO (the grammar's own
    semantics) and SPO-multi (a row override)."""
    g = Graph("pump-%d-%d" % (k, m))
    hub = g.node("mult=%d:c" % k)
    for _ in range(m):
        g.edge(hub, "b", g.node())
    return g


def mergers(n):
    """The mergers grammar's start graph scaled up: a ring of n nodes flagged
    a, b, c in turn, every node with an edge to its successor and every
    second node with a chord to the node three further on, the edges
    labelled by the flags of their endpoints as in the sample (a_to_b and
    so on). The rules merge a-nodes into b- and c-nodes and delete a-nodes,
    so every step shrinks the graph and the states are the reachable
    quotients of the ring; merging nodes with shared neighbours creates
    parallel edges, which the SPO-simple semantics of the sample collapses
    and the SPO-multi override keeps."""
    g = Graph("ring-%d" % n)
    flags = ["abc"[i % 3] for i in range(n)]
    nodes = [g.node("flag:" + f) for f in flags]
    for i in range(n):
        for j in [(i + 1) % n] + ([(i + 3) % n] if i % 2 == 0 else []):
            g.edge(nodes[i], "%s_to_%s" % (flags[i], flags[j]), nodes[j])
    return g


SIZES = [
    ("Mark-Unmark-List-regexp-benchmark.gps", mark_unmark, [(18,), (21,), (22,)]),
    ("As-and-Bs-reg-exp-benchmark.gps", as_and_bs, [(4, 3)]),
    ("inheritance.gps", inheritance, [(12,)]),
    ("append.gps", append, [(4, 10)]),
    ("leader-election.gps", leader_election, [(8,), (14,), (16,), (18,)]),
    ("attribute-count-to-n.gps", count_to_n, [(10000,), (100000,), (300000,), (600000,)]),
    ("fibonacci.gps", fibonacci, [(12,), (15,), (22,)]),
    ("hub.gps", hub, [(300, 3, "star"), (1000, 1, "chain"), (200, 2, "chain"),
                      (1000, 1, "ring")]),
    ("hub.gps", field, [(2, 100, 2500)]),
    ("petrinet.gps", petri_pipe, [(8, 8), (9, 9), (11, 11)]),
    ("petrinet.gps", petri_join, [(100,), (1000,)]),
    ("parallel-pump.gps", parallel_pump, [(8, 4), (12, 6), (16, 8)]),
    ("mergers.gps", mergers, [(6,), (9,), (10,), (11,)]),
]

if __name__ == "__main__":
    for grammar, gen, sizes in SIZES:
        for args in sizes:
            gen(*args).write(grammar)
