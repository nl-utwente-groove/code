GROOVE Change Log
=================

This document describes the major changes in the GROOVE tool set

Release 7.0.4, 14 February 2025
-------------------------------
- Resolved gh issues #813 and #814

Release 7.0.3, 5 February 2025
-------------------------------
- Resolved gh issue #811

Release 7.0.2, 12 December 2024
-------------------------------
- Resolved gh issues #771, #779, #795, #802-#804, #807-#809

Release 7.0.1, 31 October 2024
-------------------------------
- Solved regression bug in combination of recipes + properties

Release 7.0.0, 26 September 2024
-------------------------------
- Changed to Java 21 (hence the major version update)
- Improved control automaton construction for functions, esp. i.c.w. properties
- Improvements in display of LTS (popped stack locations included in grey, start call nesting optional
- Improvements in error reporting for control programs & type errors

Release 6.9.4, 14 September 2024
-------------------------------
- Complete reimplementation of transient state exploration
- Theory documented in doc/exploration
- Resolved gh issue #800

Release 6.9.3, 2 August 2024
-------------------------------
- Repaired parsing of grammar control properties for Generator
- Repaired rule level tree functionality

Release 6.9.2, 1 August 2024
-------------------------------
- Bug fix release (don't use 6.9.1)

Release 6.9.1, 1 August 2024
-------------------------------
- Hugely improved behaviour of (label and) type tree (gh issue #786)
- Bug fixes: GH issues #796, #797

Release 6.9.0, 20 July 2024
-------------------------------
- Added .dot export as well as .gcp export for LTSs
- "halt" added to control language (hence minor version update)
- Refactored the Im-/Exporter library
- Bug fixes: GH issues #78, #783 #788-#791

Release 6.8.4, 10 July 2024
-------------------------------
- Errors in property keys now reported by highlighting and switching
- Bug fixes: GH issues #770, #774, #775, #776, #777, #783

Release 6.8.3, 25 June 2024
-------------------------------
- Bug fixes: GH issues #772
- New attempt to create Maven artefact

Release 6.8.2, 25 June 2024
-------------------------------
- Bug fixes: GH issues 1 and 2 (nl-utwente-groove.github.io)

Release 6.8.1, 8 April 2024
-------------------------------
- Feature extension: rule nodes _only_ typed by a label variable are treated more
  leniently
- Bug fix (github #769)

Release 6.8.0, 25 March 2024
-------------------------------
- Project moved to github.com/nl-utwente-groove
- Project web site moved to groove.cs.utwente.nl
- Releases now available at github.com/nl-utwente-groove/code/releases/latest
- Maven-based testing now working correctly
- Release repository integrated into code repository
- Some small bug fixes

Release 6.7.3, 26 February 2024
-------------------------------
- Bug fix release (don't use 6.7.2)
- result transitions & traces shown; final state look changed

Release 6.7.2, 23 February 2024
-------------------------------
- Slightly better visibility for LTS exploration results
- Gray background for inactive resources

Release 6.7.1, 20 February 2024
-------------------------------
- View on recipe fragments in rule tree separated from the recipe transitions
- Recipe matches and ongoing recipes displayed separately, linked to the fragment matches
- Absent and recipe states and transitions properly hidden when optional view deselected
- Hatched background for absent states (transient or otherwise)

Release 6.7.0, 4 February 2024
-------------------------------
- User-defined operations now supported (SF Feature Request #205):
  specify "User Operations" system property
- Added keyboard shortcuts for switching between displays and showing current state from LTS
- Introduced error values, e.g. on computing division by zero
- Bug fixes SF #520-525

Release 6.6.5, 28 December 2023
-------------------------------
- Bug fixes SF #486, #516, #517 and #518
- Recipe transition parameters now stored in transition rather than target state

Release 6.6.4, 29 November 2023
-------------------------------
- Some bug fixes in the new node (re)numbering scheme
- Renumbering will only happen automatically for > 10_000_000, and will always
  be signalled as an error in the graph
- Regression bug resolved regarding type imports
- Regression bug resolved regarding layout loss for attributed start graphs

Release 6.6.3, 22 November 2023
-------------------------------
- Added grammar property USE_STORED_NODE_IDS to allow consecutive node numbering
  when loading (this will be the default behaviour from now on)
- Increased GROOVE_GRAMMAR_VERSION to 3.10 to accommodate the above
- Improved node number load mechanism so stored IDs >1_000_000 will not be
  used in any case
- Refactored FormatErrorSet to reduce performance problems in case of
  very many errors

Release 6.6.2, 15 November 2023
-------------------------------
- Bug fix release (don't use 6.6.1)
- Errors in type graphs highlighted correctly
- Layout correctly copied to start state

Release 6.6.1, 13 November 2023
-------------------------------
- Bug fix release (don't use 6.6.0)
- Errors in type graphs highlighted correctly

Release 6.6.0, 13 November 2023
-------------------------------
- Removed the iTextPdf library in favour of Apache FOP
  (All (A)GPL licenced library dependencies are now gone)
- Fixed SF Bugs 418, 492, 494, 507 and 510
- Implemented SF Feature Request 196

Release 6.5.2, 31 October 2023
------------------------------
- Improved type derivation for expressions containing attribute fields

Release 6.5.1, 26 October 2023
------------------------------
- Grammar and rule properties that influence the behaviour are now more visible,
  in several ways:
  * While editing, the non-default values of notable keys have a special "info" colour
  * The Properties tab in the rule display as well as the Grammar properties tab
    are also info-coloured when there are non-default values of notable keys
  * Rules as well as the tab component of the systems properties display are merked
    with a special info symbol (encircled i) if they contain non-default values of
    notable keys 
  * Where there is an info symbol, the tool tips are also extended with a summary of
    the non-default values of notable keys
- Minor bug fixes

Release 6.5.0, 25 October 2023
------------------------------
- Removed the epsgraphics library in favour of Apache Batik
- Added support for SVG export (thanks, Apache!)

Release 6.4.0, 24 October 2023
------------------------------
- Removed the letnew: aspect, as planned (see CHANGES for 6.3.0)
  The reason to do this in a separate release is for transparency,
  as this is non-backwards-compatible

Release 6.3.0, 24 October 2023
------------------------------
- Implemented SF request #200 by enabling
  * new:let:name=expr to create a new name-attribute initialised to expr
      (rather than changing the existing attribute)
  * cnew:let:name=expr to create a new name-attribute initialised to expr,
      in case no such attribute (with that value) exists
  * del:let:name=expr to delete a name-attribute in case it equals expr
  * use:int:name to test for the existence of an int-sorted name-attribute
  * not:int:name to test for the absence of an int-sorted name-attribute
  * del:int:name to test for and delete an int-sorted name-attribute
      (irregardless of its value)
  The next step (to be taken in 6.4.0, released immediately after this one)
  is to remove the now spurious letnew: aspect, which existed since 6.1.0;
  hopefully this has not yet found any users...
- Substantially improved the in-editor syntax help for expressions
- Added prefix notations (real) for toReal and (string) for toString,
  analogous to (int) for toInt
  
Release 6.2.1, 17 October 2023
------------------------------
- Minor bug fixes

Release 6.2.0, 14 October 2023
------------------------------
- Value and variables nodes in rules can have proper expressions
  (allowing real local variables and (almost?) getting rid of the old attribute syntax

Release 6.1.0, 16 August 2023
-----------------------------
- Resolved SF bug #508
- Implemented SF request #193 and #197
- cnew:-nodes can now have adjacent not:- or new:-edges

Release 6.0.3, 11 April 2023
------------------------------
- Resolved SF bug #504

Release 6.0.2, 5 April 2023
------------------------------
- Included BoundedModelCheckingDialog patch submitted by Tim Kräuter by mail

Release 6.0.1, 5 April 2023
------------------------------
- Resolved SF bug #503
- Quite some Java 17-enabled refactoring

Release 6.0.0, 26 October 2022
------------------------------
- Upgraded to Java 17, making project modular (module: nl.utwente.groove)
- Package names all prefixed with nl.utwente in line with the best practices for modular code
- Changed the dependency management and build process to Maven

Release 5.8.0, 18 June 2021
----------------------------
- Added a bunch of operators
  * string:isBool, string:isInt and string:isReal to test if a string is a valid representation of another type
  * string:toBool, string:toInt and string:toReal to convert a string to another type
  * int:ite, real:ite and string:ite as (ternary) if-then-else operators
  * string:substring, string:suffix and string:lookup for more sophisticated string manipulation
- Improved messaging regarding ask parameters (i.e., value oracles)
- Added oracle sample

Release 5.7.4, 30 May 2018
----------------------------
- Resolved SF Bugs #485 and #486
- Improved control automaton generation for tries within loops, circumpassing exponential blowup

Release 5.7.3, 17 April 2018
----------------------------
- Bug fix: Generator now closes all states, so clearing caches can't destroy the GPS

Release 5.7.2, 29 August 2017
---------------------------
- Bug fix: NPE when using programmatically created start graph 

Release 5.7.1, 26 June 2017
---------------------------
- Bug fix 479 

Release 5.7.0, 19 May 2017
--------------------------
- Set operators added (SF feature request 144). For a sample see junit/rules/set/sums.gps

Release 5.6.0, 5 April 2017
---------------------------
- Match filters added, which prevent a match through a call to Java or Groovy code
  (Set through rule properties)
- Updated Groovy library
- Bug fixes 474-475 

Release 5.5.8, 6 February 2017
------------------------------
- Bug fixes SF 472-473
- Value oracles added (set through grammar properties)

Release 5.5.7, 26 October 2016
------------------------------
- Bug fixes SF 468-471
- Cleaned up code: used Java 7 diamond operator throughout
- Implemented some of the SonerCube style rules

Release 5.5.6, 20 July 2016
---------------------------
- Bug fixes SF 466, 467

Release 5.5.5, 3 November 2015
- Repaired error in type inference of typed expressions

Release 5.5.4, 25 August 2015
-----------------------------
- Minor bug fixes (SF 465)

Release 5.5.3, 24 June 2015
----------------------------
- Conversion of old grammar versions improved
- Minor bug fixes (SF 464)

Release 5.5.2, 24 March 2015
----------------------------
- Bug fix for model checking recipe transitions (labels were not generated correctly)

Release 5.5.1, 17 March 2015
----------------------------
- Bug fix that caused exception when running on 1-core machine

Release 5.5.0, 9 February 2015
------------------------------
- Added (real) and (int) coercion operators
- Abstraction capabilities removed
- Stricter type control on negated edges
- Remark edges no longer get inappropriate loop symbol
- Minor bug fixes

Release 5.4.0, 7 December 2014
------------------------------
- Generator now has -traces and -spanning options for saving only result traces or spanning tree
- Save LTS... from Simulator also regards result traces/spanning tree filter setting
- Bug fix in LTS display filtering in Simulator

Release 5.3.6, 4 November 2014
------------------------------
- Bug fix in control template construction

Release 5.3.5, 29 October 2014
------------------------------
- Bug fix in Ecore import

Release 5.3.4, 22 October 2014
------------------------------
- Improved Ecore import

Release 5.3.3, 22 October 2014
------------------------------
- This is a bug fix release (fixing regression in 5.3.2)

Release 5.3.2, 16 October 2014
------------------------------
- Performance improvements through parallellisation
- Fixed bugs 455, 456

Release 5.3.1, 19 September 2014
--------------------------------
- This is a bug fix release

Release 5.3.0, 28 August 2014
-----------------------------
- Assignment syntax for control output parameters of rules/recipes,
  e.g. a := rule(b) rather than rule(b, out a)
- Bug fixes

Release 5.2.4, 23 August 2014
-----------------------------
- Reserved words can now be used as names in control program by backward quoting, e.g. `any`
- Bug fixes

Release 5.2.3, 16 August 2014
-----------------------------
- This is a bug fix release

Release 5.2.2, 14 August 2014
-----------------------------
- This is a bug fix release

Release 5.2.1, 12 August 2014
-----------------------------
- This is a bug fix release

Release 5.2.0, 17 July 2014
---------------------------
- New features:
  * Extended use of ANY and OTHER (SF Feature Request #170)
  * LTL/CTL properties now edited on text area rather than one-line text field (SF Feature Request #176)
  * Rule conditions can now have parameters (SF Feature Request #177)
- GUI improvements in the rule tree
- Bug fixes for SF Bugs

Release 5.1.2, 16 July 2014
---------------------------
- Bug fixes, including SF bugs 433 - 436
- Parameters now allowed on rule conditions (but not constraints)
- Regular expressions now properly typeset

Release 5.1.1, 10 July 2014
---------------------------
- Lots of minor GUI improvements, especially surrounding graph properties
- Solved several nasty regression bugs in graph editing (empty type graph, edge deletion)
- Layout for flag edge not stored rather than error

Release 5.1.0, 9 July 2014
--------------------------
- Error states are now properly reported in the Simulator
- Property errors are now properly reported in the Simulator
- Introduced the notion of graph property, which is an unmodifying rule that is
  tested at every state, irregardless of priority or explicit control:
  * Conditions are just tested (neutrally)
  * Invariant properties should be satisfied at every state
  * Forbidden properties should never be satisfied
  The last two cause an error at every state that violates them
- Grammar property for setting the policy of a graph property:
  * off for not testing it
  * silent for testing it without consequence
  * error for the default behaviour
  * remove to be treated as a postcondition, meaning the state is removed from the state space
- Containment cycles are detected
- Grammar property for treating dynamic type errors: off, error, remove as above
- Bug fixes 431, 432

Release 5.0.1, 17 June 2014
---------------------------
- Further integration of new control concepts
  * Only "real" (non-internal, non-absent) states shown & reported by default
  * State status optionally displayed on state node in LTS
  * Improved treatment of error states (in case of multiplicity errors)
- Bug fixes
  * SF Bugs 429, 430
  * Raster & vector exporters re-enabled

Release 5.0.0, 10 June 2014
---------------------------
- Major upgrade of the control language:
  * Recipes and functions with parameters
  * Recursion in recipes and functions
  * Explicit atomicity construct < a;b; >
  * Recipes and priorities can be mixed
- transitionBracket grammar property deprecated
- Bug fixes

Release 4.10.0, 25 May 2014
---------------------------
- Bug fixes (422-426), esp. in recipe execution
- Changed minor version number because of functionality change in LTS saving (see 4.9.4)

Release 4.9.4, 20 May 2014
--------------------------
- Bug fixes (416, 418, 420)
- Saved LTS is now a MultiGraph (may have equi-labelled transitions between the same states)
- Host graphs can be exported as rules or types, meaning that literal :-prefixes are stripped
- Viewer command-line utility for viewing graph files and graphs

Release 4.9.3, 26 February 2014
-------------------------------
- Bug fixes (SF 410-415)
- Added -D option to Generator to pass in grammar properties such as checkIsomorphism
- Labels for special states can now be set from Save LTS As dialog
- Minor improvements in Simulator GUI
- New Prolog predicates: show_graph and save_graph
- New command-line tool: PrologChecker allows you to run Prolog queries from the terminal
- Legacy Ecore2Groove tool removed

Release 4.9.2, 10 December 2013
-------------------------------
- Bug fixes (SF 407-409)

Release 4.9.1, 9 December 2013
------------------------------
- CTL model checker can now also check previously saved (LTS) graphs

Release 4.9.0, 2 December 2013
------------------------------
- Bug fixes
- Added term algebra
- Improved support for parsing expressions
- Refactoring of command-line tools: some changes in options

Release 4.8.7, 29 April 2013
----------------------------
- Bug fixes
- Information message after Generator is done; especially for LTL checking

Release 4.8.6, 15 April 2013
----------------------------
- Bug fixes
- Close editor dialog is back

Release 4.8.5, 25 March 2013
----------------------------
- Fixed bug in ecore exporter

Release 4.8.4, 25 March 2013
----------------------------
- Imported (ecore and dot) graphs now named after filename
- Fixed bug in count edges

Release 4.8.3, 21 March 2013
----------------------------
- Added xsd resource to jar, to allow ecore import

Release 4.8.2, 19 March 2013
----------------------------
- Fixed datatype operators

Release 4.8.1, 18 March 2013
----------------------------
- Fixed missing font bug (preventing startup)

Release 4.8.0, 16 March 2013
----------------------------
- Support for importing and exporting ECORE, DOT, GXL
- Bug fixes (see SourceForge)
- Major package refactoring
- GUI attribute handling completely restructured
- Tikz style file generation improved in line with previous item

Release 4.7.1, 16 November 2012
-------------------------------
- Bugfixes of the 4.7.0 version

Release 4.7.0, 15 November 2012
-------------------------------
BUGGY; DELETED
- Bug fixes (see SourceForge)
- Exploration now always halted after 1000 states giving the user choice to proceed
- LTS display always limited to displaying up to used-defined state number (disabling gone)
- LTS and state tabs are now main display tabs
- Recipe support much improved, recipes shown in rule list
- Improved display of enabled/explored matches in rule list
- Support for GROOVY added (see http://groovy.codehaus.org/)

Release 4.6.0, 14 October 2012
------------------------------
- Lots of bug fixes (see SourceForge)
- Abs function added to algebra operations
- Multiline text in remark nodes now kept in order
- Resource name ordering adapted to a more human-friendly treatment of numbers
- Layout changes outside graph editor saved automatically & undoably
- Automatic exploration in the presence of input parameters enabled for point algebra

Release 4.5.2, 6 June 2012
--------------------------
- Bug fix: proper behaviour of Hide LTS and Filter LTS buttons.

Release 4.5.1, 26 May 2012
--------------------------
- Bug fix: creating new control program no longer crashes the Simulator

Release 4.5.0, 16 May 2012
--------------------------
- Bug fixes.
- Grammar resources must now be named by a valid identifier: spaces are no
  longer allowed. Increased grammar version to 3.1.
- Several performance improvements in neighbourhood abstraction (ShapeGenerator).
- Added multiplicity check in typing.
- Enabled loading and saving of host graphs, rules and type graphs in subfolders.
- Multiple start graphs can now be enabled and are merged in the initial state.
  Start graphs are now stored in the grammar properties.
- GUI improvements:
  * Display of host graphs is now layered.
  * New find/replace dialog (shortcut Ctrl+F).
  * LTS panel can be completely disabled to allow exploration of large state
    spaces in the Simulator while avoiding rendering performance problems.
  * Edges with same label going back and forth from node pairs are now rendered
    as a single bi-directional edge. Controlled by an option in View menu.
  * Added button to collapse all nodes of the trees.
  * Traces that are explicitly selected in the LTS are now also visualised in
    the rule tree.

Release 4.4.5, 10 October 2011
------------------------------
- Font changed back from bold to standard

Release 4.4.4, 7 October 2011
-----------------------------
- Bug fixes.

Release 4.4.3, 5 October 2011
-----------------------------
- Bug fixes.
- Untyped (attr:) nodes no longer supported
- Type specialisation now supported by merging with newly created node
  of specialised type

Release 4.4.2, 29 June 2011
---------------------------
- Bug fixes.
- The default exploration strategy is now a grammar property, and can be set
  through the exploration dialog.
- Node colours were extended beyond the type graph:
  * Specify color:name in a rule to change the colour upon rule application.
  * Specify color:name in a host graph to set the initial colouring.
  These colours supersede any colouring specified in the type graph.
- Nodified edges are now rendered as a small dot to improve visualisation.
  Use the new prefix 'edge:' in a node of the type graph to indicate that the
  node type represents an nodified edge.
- New textual syntax for attribute manipulation. There are two new prefixes:
  * 'test:' that specify a condition for a rule to be applied.
  * 'let:' that can be used in rules to modify attributes and in host graphs to
    set the attribute values.
  (See the example grammar at http://groove.cs.utwente.nl for a sample on how
   to use this new attribute syntax.)

Release 4.4.1, 17 June 2011
---------------------------
- Fixed a deadly bug that prevented editing rules.
- New 'Find a Final State' action (keyboard shortcut Ctrl-End) that takes the
  predefined exploration strategy and after exploring switches to a final state.
- New keyboard shortcut (Ctrl-L) for layouting graphs.

Release 4.4.0, 16 June 2011
---------------------------
- Bug fixes.
- Major refactoring of the groove.io package to provide better import/export
  functionality to external formats.
- Integration of a Prolog interpreter into the Simulator.
- Unified syntax for LTL and CTL formulas.
- Writing to stdout supported through 'printFormat' rule property.
- Forall quantifiers now support counting.
- Edges with especial label 'or:' can be used to connect distinct NACs.
- Conditional existential quantifiers (prefix existsx:).
- Help documentation integrated in the Simulator for all editable elements of
  a grammar.
- Major refactoring of the Simulator with many improvements on the GUI.
- The graph editor is fully integrated in the Simulator and is no longer
  provided as an stand alone tool (Editor.jar no longer exists).
- A state space exploration can now be played as an animation.

Release 4.3.1, 9 March 2011
---------------------------
- Bug fixes:
  * Fixed bug on the Exploration Dialog where the parameters of the strategies
    were not shown.
  * Fixed conflict between rem: aspects and del: and new: .

Release 4.3.0, 28 February 2011
-------------------------------
- Bug fixes
- Added prefixes "id:" and "color:" for rule node identities (in preparation of
  improvement in attribute specification), and node type colouring.
- Improved the input of formulas in model checking and also the result dialog.
- Selection and editing modes in the Editor are merged.
- Removal of .gl files. Graph layout is now stored in the .gxl files.
  IMPORTANT: this change make is not backward compatible!

Release 4.2.0, 20 January 2011
------------------------------
- Bug fixes.
- Editor view brought in line with display view.
- Editors now available as tabbed panels in the Simulator.
- Editor behaviour simplified (node and edge editing modes merged).
- View panels can be detached from the Simulator and displayed in
  separate windows (except for editor panels).
- Rule parameters rendered as node ornaments rather than separate lines.
- Graph display prettified (rounded rectangles, background grading).
- Major refactoring of GUI classes.

Release 4.1.0, 15 December 2010
-------------------------------
- Bug fixes.
- Major code refactoring, including complete re-implementation of control
  automata and re-organisation of core graph classes and interfaces.
- Type graphs now may have abstract node and edge types (prefix abs:).
- New command line tool: Ecore2Groove - used to convert from Ecore models to
  GROOVE graphs and back.
- Editor now has an option to snap nodes to grid.
- Added importer from the .col format.

Release 4.0.2, 25 August 2010
-----------------------------
- Bug fixes.
- Changed the default graph layouter to Forest.
- Updates in label parsing and label display.
- Added RuleFormula acceptor.
- Created exploration statistics dialog in the Simulator.
- Added precise type matching (syntax: type:#Name).
- Implemented type specialisation and typed node merging.

Release 4.0.1, 7 June 2010
--------------------------
- Bug fixes.
- History in the regular expression dialog.

Release 4.0.0, 3 June 2010
--------------------------
- Inclusion of type graphs and type checking.
- Support for boolean conditions on nodes: flags.
- Wrapper shows a bug report dialog for uncaught exceptions in the GUI.
- New Exploration Dialog.
- Input and output parameters in the control language.
- Format errors are highlighted in the graph and selectable in the error list.
- Added an option renumber all nodes to a consecutive sequence starting at 0.
- Grammar versions, and versioning checks.
- Result states are now shown in orange in the LTS.
- Unified the Generator exploration options with the Simulator.
- Rules now have a transition label property that are shown in LTS.
- Added the export simulation option to the generator.

Release 3.3.1, 7 January 2010
-----------------------------
- Bug fix in label renaming
- Bug fix in LTS Export
- Minor GUI improvements (menu cleanup)

Release 3.3.0, 22 December 2009
-------------------------------
- LaTeX tikz export for graphs and rules (nice!!)
- Node types labels distinguished (syntax: "node:type")
- Node type inheritance supported as partial relation over node type labels
- GUI for rule and graph sub-panels improved
- Label list improved; drag-and-drop for node type inheritance
- Exploration dialog added to Simulator
- Auto-completion of label identifiers in editor

Release 3.2.2, 11 May 2009
--------------------------
- All file choosers remember last location
- Start graph sub-panel added
- Option in Show/Hide menu to load labels from file
- Improvements to ModelChecker command line tool
- Rules can be declared confluent, to restrict exploration order
- Drag-and-clone now works in the editor
- Cut-and-paste across editors
- LTS export from Simulator through dialog
- Improvements to the Control editor
- Final states are now those without outgoing *modifying* rules

Release 3.2.1, 5 January 2009
-----------------------------
- Start state loading from rule directory
- Better error messaging for parameter nodes
- "big" algebra family added (system property: algebraFamily)
- Read support for .aut format (CADP)
- Bug repair in anchor computation for nested rules


Release 3.2.0, 21 November 2008
-------------------------------
- Point algebra is now supported (system property: algebraFamily)
- Extension for loading grammars from URLs
- User preferences (automatically) for window positioning
- Bug fix for saving graphs
- Default name for property files is now grammar.properties
- Default name for control files is now control.gcp

Release 3.1.1, 12 November 2008
-------------------------------
- Bug fix for saving rules and graphs

Release 3.1.0, 11 November 2008
-------------------------------
- Code cleanup
- More user preferences stored
- Isomorphism check improved
- Grammar input from JAR files
- Support for control extended

Release 3.0.1, 17 June 2008
-------------------------------
- Bug and other fixes

Release 3.0.0, 18 March 2008
-------------------------------
- New features:
  * Support of control programs
  * Possibility for defining parameters in rules,
    to be instantiated with data values by matchings

- New GUI feature:
  * Some GUI preferences are saved into a preference file

- Refactoring
  * Explore strategies replaced by exploration scenarios
    ( scenario : combination of an exploration strategy and a goal to be reached )

Release 2.0.3, 10 January 2008
-------------------------------
- Some bug fixes

Release 2.0.2, 19 December 2007
-------------------------------
- Some bug fixes

Release 2.0.0, 19 November 2007
-------------------------------
- Format for regular expressions changed to braces (hence the major version increase)
- Positive and negative guards for wildcards
- GXL files versioned (attribute $version)
- GUI performance improvements

Release 1.8.0, 24 October 2007
--------------------------
- Quantified rules now supported! See samples.
- Additional system properties:
  * Checking for dangling edges
  * Checking for creator edges
  * Checking for RHS as NAC
  * Global injectivity check
- Further performance improvements (due to search plan-based matching)
- Isomorphism check can now be switched off (in system properties)
- One-line remarks in rule and system properties

Release 1.6.0, 5 June 2007
--------------------------
- New GUI features:
  * Improved rule editing from Simulator (adding, deleting, renaming, disabling)
  * New grammar and Save grammar actions in Simulator
  * Support for Graph properties and Rule System properties
  * Support for user remarks in rules (prefix with "rem:")
  * Attributes optionally shown as assignments in Simulator
  * Label-based hiding ("filtering") of nodes and edges in Simulator view
  * Improved graph format error handling in Editor
  * Manhattan line style
  * Additional key accelerators in Editor
- Bugs in rule system nesting fixed
- Value-related nodes displayed as ovals/diamonds
- State graph layout and graying-out maintained during transformation
- Refactorings:
  * Exploration decoupled from GTS
  * Graph decoupled from GraphState
  * Aspect prefix handling unified
  * GrammarView decoupled from GraphGrammar

Release 1.4.2, 15 March 2007
----------------------------
- Model Checker integrated in Simulator GUI
- Added eps support for exporting graphs and rules
- Ported to JGraph 5.9.2
- Options introduced for showing node identities
- Bug in usage of independence relation between rules fixed (Generator)
- Search plans introduced to speed up the matching process
- Minor bugs resolved (emphasizing, etc.)

Release 1.4.1, 1 February 2007
----------------------------
- Depends on Java 5
- Option in Simulator to show node numbers
- Node numbers partitioned for ordinary nodes and algebra-nodes
- Rule and Graph Factory bugs fixed, occurred when (re)loading a (different) grammar
- Usage of RuleFactory more transparant
- Error message occurs when loading a grammar not including the default start-graph
- Attributed graph are dealt with more naturally (specialized Java-classes only at rule-level)
- boolean constants changed to lower case (consistent with documenation)
- Minor bugs resolved (especially in rule application)

Release 1.4.0, 17 March 2006
---------------------------

- Attributed graphs supported

- Minor bugs resolved

Release 1.2.0, 14 June 2005
---------------------------

- Regular expression matching extended with inverse operator, wildcard (?) and variables (?-)

- Second-level negations introduced (!- and !a)

- Garbage collection monitoring in Generator supported: call java with -Xloggc:gc.log and the -l option in the Generator

- Noticeable performance improvements (space and time) in the Graph hierarchy, in SPORule and in the Simulation hierarchy

- Diverse bugs resolved (see SourceForge)

- Single-quoted atoms in production rules now treated correctly

- Generator will now save final state(s)

- Fairly extensive EditorMarquee refactoring; minor irritations in Editor resolved

- Bug in "linear" exploration repaired (again :-()

- Forest layout now takes selected node as suggestion for root; in LTS view it takes the initial state

Release 1.1.2, 20 January 2005
------------------------------

- Documentation (format.pdf) rigorously overhauled

- Regular expressions changed: optional postfix operator (?) removed, wildcard (?) and empty expression (-) constants added; use single quotes (') around literal atoms

- Editor now starts with empty graph when invoked with non-existent file

- Graph export added to editor and simulator (CTRL+ALS+S, in PNG or JPG formats); also respects hiding and emphasis

Release 1.1.1, 13 January 2005
------------------------------

- Imager has received a GUI (invoke it without parameters)

- label lists now distinguish between empty label lists and empty labels

- several bug fixes

Release 1.1.0, 11 January 2005
------------------------------

This release involves a host of (major) changes to the GUI. We have moved to jgraph 5.2! Be sure to update your class path to the correct jar. The classes in groove.gui have changed, in some case drastically; apologies.

Furthermore, there is now an extra utility "Imager", for creating images from a whole directory of rules and graphs at a time.

Main GUI iprovements:

- graph panels have an associated list of labels, through which nodes and
  edges can be emphasized and hiding can be controlled better

- layouting no longer affects the hidden parts of the graph

- the editor can now be invoked directly from the simulator, so graphs and
  rules can be edited more easily

- node and edge label editing use a multiline editor

- there is much better control over the routing and line stype of edges

- production rule layout is stored more completely

- the editor has received a menu bar


Release 1.0.0, 25 October 2004
------------------

Because of moving to SourceForge, we decided to bring out a new release. No features are added with respect to release 0.2.4

Release 0.2.4, 13 July 2004
------------------

- rule priorities: a file name of the form <number.text.gpr> is interpreted as a rule named <text>, with priority <number>. Lower priority rules can only be enabled if no higher priority rules are applicable. Rules with no priority in the name have priority 0 (the lowest).

Release 0.2.3, 28 June 2004
------------------

- further improvement in memory consumption

Release 0.2.2, 24 May 2004
----------------------------

- editor layouts are re-used in the simulator

Release 0.2.1, 28 April 2004
----------------------------

- the lts now supports final states

- start, open and final states are indicated in the gxl format

- many more exploration strategies:
  * Invariant (halt when a given rule becomes enabled or disabled)
  * Bounded (ignore states in which a given rule is enabled or disabled)
  * Node-bounded (ignore states in which the node count exceeds a given bound)
  * Edge-bounded (ignore states in which one or more edge counts exceed their given bounds)
  * Live (halt when a final state is found)

- a command line tool for state space generation, with many nice options: groove.util.Generator

- huge performance improvements, esp. in space consumption

Release 0.2.0, 24 March 2004
----------------------------

- Migrated to jgraph 3.1
  (resulting in an appreciable speedup of the GUI,
   but now the spring layouting does no live preview of edges any more!)

- Support for saving of LTS and states in the Simulator

- Support for regular expressions in rules (reader and embargo edges)
  Documentation not yet adapted!

- Layout serialization format changed (necessitated by the move to jgraph 3.1). Old .gp layout files are no longer usable; however, use groove.io.Reformatter to get from old to new files

Release 0.1.7, 9 March 2004
---------------------------

- Introduced "graying out" in the graph views using selection based on edges, including also regular expressions

- Repaired some bugs in the forest layouter and (especially) the editor

Release 0.1.6, 29 February 2004
------------------------------

- Added a forest layout routine

- Major restructuring of the gui package, in preparation for a migration of the underlying jgraph version to 3.1

Release 0.1.5, February 2004
----------------------------

- Bug in Editor layouting repaired
- Forest layouting now also takes care of edge points
- Spring layouting cleaned up

Release 0.1.4, February 2004
----------------------------

- Forest layouting added
- Added node merging

Release 0.1.3, February 2004
----------------------------

- Linear and branching state space exploration strategies added

Release 0.1.1, 1 December 2003
------------------------------

- The GPS reader assumes an extension of .gps

Release 0.1.0, 5 July 2003
--------------------------

I've made the following changes:

1. Introduction of java.io
2. Reading and writing GXL files now implemented in java.io.Gxl
3. Conversion between graphs and rules now implemented in java.trans.RuleGraph
4. Conversion methods in java.util.Converter deprecated
5. Graph structure change: No more dangling edges allowed!
6. Application java.io.Validator to test correct formatting of graph and rule files
7. Preliminary setup graph.java

1. Introduction of java.io
------------------------

This packege contains the following functionality:

- The classes ExtensionFilter and GrooveFileView, previousy in groove.util
- The class Gxl for marshalling and unmarshalling GXL documents (see below)
- The new application java.io.Validator (see below)

2. Reading and writing GXL files now implemented in java.io.Gxl
-----------------------------------------------------------

This setup relies on the org.w3c.dom interfaces, using javax.xml.parser and javax.xml.transform for the actual marshalling/unmarshalling. The class is programmed as an implementation of a more general interface Xml.

I expect this solution to be much more maintainable and flexible, for instance if another XML format for graphs is to be supported (there are several candidates).

To read/write (unmarshal/marshal in OMG terms) a GXL-formatted document construct an instance of Gxl and call its methods for actual transformations. The Gxl instance itself is state-less.

3. Conversion between graphs and rules now implemented in java.trans.RuleGraph
---------------------------------------------------------------------------

RuleGraph instances can be created either from (ordininary) graphs with not:, del:, new: and use:-prefixes to indicate the roles of the nodes and edges in the usual way (however, see below for a change in the allowed format) or from Rule instances. They also have methods to convert into Graphs or Rules.

In the meanwhile, RuleGraph instances are themselves graphs with nodes and edges of a special kind. This allows them to be displayed easily as production rules in Simulator and Editor (rule preview mode during save). As a consequence java.jgraph.RuleJModel has been simplified considerably, and might be simplified further in the future (I'm thinking to merge GraphJModel and JModel and make
RuleJModel a subclass).

I foresee that RuleGraphs will be the preferred method to generate production rules.

4. Conversion methods in java.util.Converter deprecated
----------------------------------------------------

Due to the above improvements it is no longer intended to use groove.util.Converter for graph reading/writing in the future; in fact the class might disappear entirely. I've kept some of the existing methods as deprecated methods, primarily to show how the conversion should be done now.

5. Graph structure change: No more dangling edges allowed!
--------------------------------------------------------

Up to now it was allowed to have dangling edges in graphs, especially rule graphs. These were abbreviations: the real meaning is that there is an end node with the same role (eraser, creator, embargo) as the edge itself. The main usage was for embargo edges: in fact, it was not even allowed to include embargo nodes.

Along similar lines, it was allowed to specify edge sequences by specifying a "."-separated label sequence. Also here the intermediate nodes could be omitted. Again, this was mainly used for embargo edges.

These abbreviations are no longer allowed. You should explicity include all intermediate and end nodes in rule graphs. Embargo nodes have been added so there is no loss of expressivity.

I have adapted all samples provided under CVS to the new format.

Reason: In the future I want to use dangling edges to model unary predicates. I might reintroduce label sequences, but maybe not: they were not used all that much and the "."-separated list notation is overloaded for qualified names.

6. Application groove.io.Validator to test correct formatting of graph and rule files
---------------------------------------------------------------------------------

Change 5. above means that many of the graph and rule files you have are no longer formatted correctly. Obviously this is a pain. To help a little bit I have included a facility to validate the correctness of the graphs in a hierarchical file structure. It is called groove.io.Validator and should be run from the command line. Run it with option -h to get a synopsis of its capabilities.

7. Preliminary setup graph.java
-----------------------------

The CVS head branch now includes a package graph.java, with a first version of the promised OperatorRule. Currently this does not yet compile.
