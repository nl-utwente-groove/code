# External null annotations (`.eea`)

This directory holds a *curated subset* of Eclipse external null annotations for the JDK,
giving the JDT null analysis correct nullness contracts for library methods it would
otherwise treat as `@NonNull` by type-argument substitution (gh #881). It is wired up in
two places:

- **Eclipse**: the `annotationpath` classpath attribute on the JRE container in
  `.classpath` points here (`/groove/lib/eea`). Current m2e explicitly preserves this
  attribute across *Maven &gt; Update Project*.
- **ecj harness**: `.claude/skills/null-check/run-ecj.ps1` passes `-annotationpath lib/eea`.

The Maven build is unaffected; it does not run null analysis, and nothing in this
directory is shipped in release artifacts.

## Provenance and license

The `.eea` files are taken from the [no-npe](https://github.com/vegardit/no-npe) project
(artifact `com.vegardit.no-npe:no-npe-eea-java-21`, version 1.1.0), successor of
lastnpe.org. They are licensed under the Eclipse Public License 2.0 (see `LICENSE`);
they are development-time data only and are not distributed with GROOVE.

## Curation policy

Files are adopted *deliberately*, not wholesale: every annotated method makes previously
unflagged call sites into warnings, so each added file is a triage commitment. Two format
properties keep the surface controllable:

- An `.eea` file annotates only the members it lists; absent members stay unannotated,
  so files may be trimmed per member.
- Annotations propagate to overriding declarations, so annotating an interface
  (`Queue.poll`) also covers all implementations (`LinkedList.poll`, `ArrayDeque.poll`, …).

Current contents:

| File | Why |
|---|---|
| `java/util/Queue.eea` | `poll`/`peek` return `@Nullable` (verbatim from no-npe) |
| `java/util/Deque.eea` | idem, plus `pollFirst`/`peekLast` etc. (verbatim from no-npe) |

Planned next (gh #881): `java/util/Map.eea` (`get` returns `@Nullable`), which surfaces
roughly 200 sites needing triage.

## Reviewing `.eea` files

The raw format (JVM signature pairs) is not reviewable at a glance. `eea-render.py`
decodes a file into annotated Java-like declarations:

```
python lib/eea/eea-render.py lib/eea/java/util/Queue.eea
```

yields

```java
type Queue<E> {
    boolean add(@NonNull E);
    @Nullable E poll();
    ...
}
```

`--raw` adds the unannotated original under each member; `--all` includes members
without annotations.
