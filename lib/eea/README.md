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

Current contents (all files verbatim from no-npe):

| File(s) | Why |
|---|---|
| `java/util/Queue.eea`, `Deque.eea` | `poll`/`peek` (+ `pollFirst`/`peekLast` etc.) return `@Nullable` |
| `java/util/Map.eea`, `Map$Entry.eea` | `get`/`put`/`remove`/`compute*`/`merge` return `@Nullable` |
| collection interfaces: `Collection`, `List`, `Set`, `SortedSet`, `NavigableSet`, `SortedMap`, `Iterator`, `PrimitiveIterator*`, `Spliterator`, `Comparator` | bulk operations and copy constructors take `@NonNull` arguments; `List.sort` takes a `@NonNull` comparator (stricter than the JDK, which accepts null for natural ordering — call sites use `Comparator.naturalOrder()` instead) |
| abstract bases and implementations: `AbstractCollection`/`AbstractList`/`AbstractMap` (+`$SimpleEntry`/`$SimpleImmutableEntry`)/`AbstractQueue`/`AbstractSequentialList`/`AbstractSet`, `ArrayDeque`, `ArrayList`, `EnumMap`, `EnumSet`, `HashMap`, `HashSet`, `Hashtable`, `LinkedHashMap`, `LinkedHashSet`, `LinkedList`, `TreeMap`, `TreeSet`, `Vector`, `WeakHashMap` | constructor and override contracts consistent with the interfaces |
| helpers: `Arrays.eea`, `Collections.eea`, `Objects.eea` | `@NonNull` parameters on copy/wrap methods; `Objects` null-contract methods |
| `java/util/Properties.eea` | `getProperty` returns `@Nullable` |
| `java/lang/*` (whole package: `Object`, `Class`, `String`, `StringBuilder`, boxed types, `Iterable`, `Thread`, `System`, `Throwable`, exceptions, …) | `Object.equals` takes a `@Nullable` parameter (propagates to every override — equals implementations must null-guard before dereferencing); `clone()` returns `@NonNull`; `Class.getEnumConstants`/`getCanonicalName`/`getClassLoader`/`getPackage`, `Throwable.getCause`/`getMessage`, `System.getProperty` return `@Nullable` |
| `java/lang/reflect/*`, `ref/*`, `annotation/*`, `constant/*`, `invoke/*` | `Method.invoke` returns `@Nullable` (a primitive-returning method still yields a non-null box — assert with that reason); `AnnotatedElement.getAnnotation` returns `@Nullable` |

Planned next (gh #881): java.io (`File.listFiles` genuinely returns null on I/O
errors, plus `readLine`, `File.getParent`, zip/jar entries — ~8 findings).
`PrintStream`/`Formatter` printf (~21 low-value findings) and the javax.swing
files (inheritance conflicts in the `JModel` hierarchy) need a deliberate
trim-or-adopt decision. Once the curated set stabilises, switching to the full
`no-npe-eea-all` artifact is the realistic endgame.

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
