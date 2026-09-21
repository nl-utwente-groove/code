# Merging the view tab into the editor tab

Proposal, written 2026-09-21 from a code survey; decided by Arend to be done **after the
8.0.0 release**. Tracked as gh #921.

## Problem

Every `ResourceDisplay` holds one *main tab* (`AspectViewTab`, or `TextTab` in view mode)
that shows the selected resource read-only and is reused as the selection changes, plus
any number of *editor tabs* (`AspectEditorTab`, or `TextTab` in edit mode) opened by an
explicit edit action. Closing an editor falls back to the main tab.

The original rationale was quick browsing over the rule list without accumulating tabs.
That is a *lifecycle* concern (one reusable slot versus a tab per resource). The code,
however, encodes it as a *capability* distinction (view versus edit), and only the
lifecycle concern has user value:

- users, Arend included, never close an editor to "get back" to a view: after a save the
  grammar works with the saved resource, so the editor tab is as good a view as any;
- the view tab has editing gestures of its own (node drag, written straight through to the
  store as a layout-only change), so it is not read-only either;
- the editor already subsumes the viewer: `AspectEditorTab.updateGrammar` reloads a clean
  tab from the grammar exactly as the view tab does, edits go to a clone and reach the
  grammar only on save, and preview mode is a read-only view model swapped onto the same
  canvas.

The cost of the split is two classes plus `isEditor()` branches in the shared base,
parallel `mainTab`/`editorMap` bookkeeping in `ResourceDisplay`, an `editing` flag in
`TextTab` and `TabLabel`, and every change to the graph tab being made twice.

## Design

1. **One tab class per resource family.** `AspectTab` becomes concrete, absorbing
   `AspectEditorTab`; `TextTab` loses its `editing` flag. Every tab is editable.
2. **Lifecycle at the display level: a preview slot.** A single click in the resource
   list opens the resource in the display's *preview tab* (distinct label, e.g. italic).
   The preview tab becomes permanent on the first edit, on a double click, or on an
   explicit pin; a single click on another resource reuses a still-preview tab. Tabs close
   individually as editors do now. This is the VS Code / IntelliJ / Eclipse pattern and
   keeps the browsing goal without a viewer class. The preview/permanent state lives in
   the display (or a flag on the tab that only the display sets), not in a class.
3. **No read-only mode toggle** on the merged tab: that reintroduces the split under
   another name. Preview mode already covers read-only display of an edited graph.

## Policy points to settle at implementation time

Defaults proposed; each is a one-line decision, not a design.

- **Layout changes.** The view tab persists a drag immediately (`doAddGraph(…, layout =
  true)`); the editor marks it *minor dirt* and saves it with `isDirtMinor`. Default: write
  minor dirt through immediately while no major dirt is pending, so a drag in a preview tab
  neither dirties it nor makes it permanent; with major dirt pending, the layout is saved
  along with it, as now.
- **Label-tree filtering** exists only when viewing (`new TypeTree(canvas, !isEditor())`).
  Editing with hidden elements is hazardous. Default: keep filtering, suspend edit
  gestures while a filter hides elements.
- **Lower info panel.** The nesting-level tree (`RuleLevelTree`, view-only, rules) and
  the syntax help (editor-only) both go into a tabbed lower panel.
- **Backgrounds.** The view tab shades enabled/disabled resources
  (`Values.ACTIVE_BACKGROUND`/`INACTIVE_BACKGROUND`); the editor uses
  `EDITOR_BACKGROUND`. Default: drop the enabled shading (the list icon shows it) and use
  one background.
- **Merged bidirectional host edges** are shown only when the model is not `beingEdited`
  (`AspectGraphViewModel.isMergeBidirectionalEdges`). With an always-editable tab the flag
  would always be set. Default: tie `beingEdited` to the permanent state, so a preview tab
  still merges; or drop the merging. Decide.
- **Grammar switch.** `ResourceDisplay.updateGrammar(fresh = true)` disposes all editors
  (the load action has asked about dirty ones first) and reloads the main tab. Default:
  dispose all tabs, reopen a preview for the selected resource.

## Inventory of what goes or changes

Surveyed 2026-09-21 on master (gui package identical to the `release-8_0_0-prep` merge).

- `gui/display/AspectViewTab.java`: deleted. Its `viewModelMap` becomes unnecessary (a
  tab per resource holds its own model); `storeGraph` becomes the minor-dirt policy.
- `gui/display/AspectTab.java`: the `isEditor()` branches in `getEditArea`,
  `getLabelPanel`, `getLabelTree`, `getPropertiesPanel` (editable table versus
  `EditMouseListener`) and `getLevelTree`; absorbs `AspectEditorTab`.
- `gui/display/AspectEditorTab.java`: folded into `AspectTab`.
- `gui/display/ResourceTab.java`: the throwing defaults `setResource`, `removeResource`,
  `setPropertyKey`; `isEditor()`; the conditional tool bar in `start()`;
  `EditMouseListener`.
- `gui/display/ResourceDisplay.java`: `mainTab` + `editorMap` collapse into one
  `Map<QualName,ResourceTab>` plus a preview reference. Touched: `getMainTab`,
  `createMainTab`, `removeMainTab`, `selectMainTab`, `getMainTabIndex`,
  `startEditResource`, `selectResource`, the `updateGrammar` loop, the detach-menu guard
  `tab != getMainTab()`, `MyTabbedPane.removeTabAt` (falls back to `selectMainTab`),
  `doRepeat` (`instanceof AspectViewTab`), `getListIcon` (keyed on `editorMap`),
  `getMainTabIcon`.
- `gui/display/SettingsDisplay.java`: `getMainTabIcon` override, `isEdited`.
- `gui/display/TextTab.java`: `editing` flag, the two constructors, `getIcon`,
  `setResource`/`removeResource`.
- `gui/display/TabLabel.java`: takes `tab.isEditor()`; needs the preview flag instead.
- `gui/Icons.java`: `getMainTabIcon`/`getEditorTabIcon`, `getListIcon`/`getListEditIcon`
  become preview/permanent pairs.
- `gui/action/FindReplaceAction.java`: casts the main tab to `AspectViewTab`.
- `gui/action`: the edit action (`ResourceDisplay.getEditAction`) becomes "open
  permanent/pin"; `CancelEditAction` becomes "close tab". Not surveyed in detail.
- `gui/view/AspectGraphViewController`: the `editing` constructor flag (editing menus and
  cell-edit actions) becomes always-on or keyed to the permanent state.
- `gui/view/AspectGraphViewModel.beingEdited`: see the policy point above.
- Backend (`yfiles-lib`): `YFilesSimulatorTest` names the tabs in eight places
  (`viewTabDragsAVertex`, `editorTabDragsAVertex`, `editorTabEditsThroughGestures`, …);
  retarget to the merged tab and the preview/permanent states.
- Tests here: none name the tabs; the Jemmy suite drives the displays and will exercise
  the change. Both gates apply (GUI tests, yFiles backend build and tests).
- Documentation: the editor material of the web manual and the change notes.

## Ordering

- After 8.0.0: the release is at checklist stage and this is a visible behaviour change
  that would reopen change notes, manual and figures.
- Independent of yFiles phase 4 (export and Imager): `AspectEditorTab` no longer imports
  JGraph (phase 3), so the merge drags no backend residue into the unified class.
- Before the layout-palette redesign Arend has in mind, so that it is not built against
  the old split; if any of it lands earlier, keep it out of `ResourceDisplay`.
