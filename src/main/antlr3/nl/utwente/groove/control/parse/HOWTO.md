How the parser and checker files are generated from the `?.g` grammars
---------

`CtrlLexer`, `CtrlParser` and `CtrlChecker` (package `nl.utwente.groove.control.parse`)
are generated automatically at build time by the `antlr3-maven-plugin`, from the grammars
`Ctrl.g` and `CtrlChecker.g` in this directory, into the same package under
`target/generated-sources/antlr3`. They are not checked in to version control.
(`Ctrl.g` is additionally packaged into the jar as resource
`nl/utwente/groove/resource/antlr/Ctrl.g`, where `CtrlDoc` reads it at runtime.)
There is no need to run ANTLR manually:

- `mvn compile` (or any later build phase) regenerates them whenever a grammar has changed
- In Eclipse, m2e runs the generation as part of the project build

To change the parser or checker, edit the grammar files; never edit the generated `.java` files.
