# Working on the yFiles backend

What a Claude session needs for work on the unit in `yfiles/`, on top of `claude/CLAUDE.md`.
`yfiles/README.md` is the reference for what the unit is, how it depends on the core and
how it is built; `yfiles/CLAUDE.md` imports this note, so it is loaded whenever a session
reads files in that directory. The content came with the unit from the private repository
`nl-utwente-groove/yfiles-lib` in 2026-11.

## Licence ground rules

Before working with the yFiles API, read `claude/yfiles-migration.md`, sections *License
constraints* and *LLM usage ground rules*. They are binding for every session: no reverse
engineering, permitted input sources, secret handling. In particular:

- The library jar and the licence files are never in this repository. They are in `lib/`
  of the private repository (clone at `C:\Groove\yfiles-lib` on the development machine);
  refer to that directory by path, never read the licence files, never inspect the jar.
- The package `nl.utwente.groove.gui.yfiles` must never re-expose the yFiles API.

## Building and testing

- The gate command and its conditions are in `claude/CLAUDE.md`, *Building and testing*.
- The Robot-driven tests of `YFilesSimulatorTest` run only with `-Dgroove.test.robot=true`:
  they take over the mouse and keyboard of whoever works on the machine. Without the flag
  they skip, as in CI.
- `-Dgroove.install.skip=true` is safe only right after the core was installed from the
  same commit, since it also skips the freshness guard and the core jar in the local Maven
  repository may be days older than the checkout.
- A Windows session that was disconnected (RDP) when the JVM started gives a headless JVM,
  and the Simulator tests then skip by assumption. Check with `quser` (`Disc` or `Active`)
  before relying on a display run.

## GUI tests

`YFilesSimulatorTest` drives a real Simulator window. Two rules keep it free of timing races
(the cause of a red CI run on 2026-09-15):

- A tab or display that was just selected is showing before its pending validation has laid
  it out. Wait for layout (`waitForLayout`, called by the canvas helpers) before reading any
  geometry.
- Read geometry and use it in the same event-thread task: component size, zoom, view
  coordinates, hit tests, painting and synthetic clicks (`paint`, `clickableSpot` and
  `dispatchClick` are event-thread helpers for this). A failing pixel check saves its image
  in `target/surefire-reports`, which CI keeps for a failed run.

Synthetic `MouseEvent`s reach yFiles only when dispatched to its input surface, the
descendant of the `GraphComponent` that carries the mouse listeners (`SyntheticMouse` in the
tests does this, headless and on screen alike), and only on the event thread: dispatching
runs the whole gesture on the calling thread, and the continuation a gesture queues with
`invokeLater` then races its tail from any other thread (the cause of a red CI run on
2026-09-21, which waiting longer did not cure). Dispatched to the `GraphComponent` itself
they reach only the Swing listeners on it, which is how the LTS display hears clicks, never
yFiles' input modes: a lightweight container routes nothing to its children. The editor
canvas replaces yFiles' drop input modes so that it also works headless
(`YFilesAspectEditorCanvas`).

## Null analysis

The unit follows the null-annotation conventions of the core and has a null check of its
own, `run-ecj-yfiles.ps1` of the `null-check` skill; zero problems is the bar. A clean run is
no proof, though: the batch compiler misses at least one error that Eclipse reports (a
`@Nullable G` result assigned to `var` through a `YFilesCanvas<?>`, 2026-09), with ecj
3.41.50, 3.42.0 and 3.46.0 alike and with the core from the jar or from Eclipse's own
output, while it does flag the same call through a `GraphCanvas<?>`; the cause is unknown.
Eclipse's Problems view is the authority.
