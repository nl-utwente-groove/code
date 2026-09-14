# Windows installer and a running GROOVE

*Status (2026-09-14)*: done on branch `installer-files-in-use` off `yworks-migration`,
handed over for review. The close-GROOVE prompt replaced the `DisableShutdown` approach
of 6bb4f80d0; verified with the real installer in five runs (below). Nothing deferred.

## Goal

Make the per-user GROOVE MSI (jpackage + WiX 3.14) behave when GROOVE is running at
install time (upgrade, repair, uninstall). `MSIRESTARTMANAGERCONTROL=DisableShutdown`
(6bb4f80d0) turned out not to do what its comment and `release/README.md` claimed.

## What the 2026-09-14 tests established (laptop UT184971, per-user 99.0.3 ↔ 99.0.4)

- GROOVE not running: both InstallValidates take ~1 s, no dialog, upgrade fine.
- Simulator running throughout: two 1610 "reboot required" boxes (the old version's own
  removal session and the new session both run a Restart Manager check, 9 s and 2 s);
  then InstallFinalize blocks silently on `app\bin\Generator.jar` ("Info 1603 … held in
  use") until GROOVE is closed. The JVM opens class-path jars without delete sharing, so
  they can neither be replaced nor moved aside; the reboot-time replacement that
  DisableShutdown relies on also needs privileges a per-user install lacks (the removal
  session logged 1321/access denied on `C:\Config.Msi\*.rbf`). After GROOVE was closed
  the copy failed: 1310 "System error 0" on Generator.jar, then a 1304 on
  `runtime\legal\jdk.xml.dom\ASSEMBLY_EXCEPTION` that Retry never cleared; Cancel rolled
  back (result 1603), the cancel logging seven "I/O on thread … could not be cancelled.
  Error: 1168" — msiexec's copy workers were wedged by the wait, not a permission
  problem. Because jpackage schedules `RemoveExistingProducts` before `CostInitialize`
  (outside the transaction), the old version had already gone: **no GROOVE installed at
  all**, and the 64 files the JVM had held (jars, `runtime\lib\modules`, 105 MB) left
  orphaned.
- The original complaint (closing the Simulator while "Validating install" showed →
  minutes, then the legacy FilesInUse dialog listing Teams, Firefox, Eclipse…) was not
  reproduced; presumably Windows Installer's fallback when the Restart Manager query is
  disturbed. Moot with the fix, since the prompt precedes validation.
- Component GUIDs are stable across versions (270 of 271 identical between 99.0.3 and
  99.0.4; only `groove-<version>.jar` differs), so `RemoveExistingProducts` *could* be
  moved inside the transaction — but jpackage put it before costing on purpose
  (JDK-8248264: after costing, a downgrade skips versioned DLLs that are newer on disk,
  then removes them). We allow downgrades, so it stays; a failed upgrade leaving nothing
  installed is documented in `release/README.md` instead, and the fix prevents the
  failure.

## The fix

`release/jpackage/wix/files-in-use.wxf`: one `util:CloseApplication` row per launcher
exe (`GROOVE.exe`, `Simulator.exe`, `Generator.exe`, `ModelChecker.exe`, `Imager.exe`,
`Viewer.exe`) with `PromptToContinue="yes"` and `RebootPrompt="no"` and nothing else, so
that WiX's immediate action `WixCloseApplications` (read in `CloseApps.cpp` of wix3)
only looks for the exe among the running processes, shows the Cancel/Retry/Ignore box
with the Description, loops on Retry, and never schedules its deferred half — which
lets it be rescheduled `Before="RemoveExistingProducts"`, before anything is touched.
Rows are conditioned on `Installed OR JP_UPGRADABLE_FOUND OR JP_DOWNGRADABLE_FOUND OR
GROOVE_SAME_VERSION_FOUND` (exe-name matching only). `DisableShutdown` is kept for the
Ignore case. `build-installer.sh` gained grep guards for the util namespace, the rows,
jpackage's REP line and the rescheduling; comments and README:113 rewritten.

## Verification (real installer, 99.0.5 built locally, Arend at the dialogs)

- Upgrade 99.0.3 → 99.0.5 with the Simulator running: box before validation
  (`WixCloseApplications` at 797 held the sequence 23 s), close + Retry → upgrade
  through, both InstallValidates ~1 s, no 1610, result 0, 271 files.
- Uninstall with the Simulator running, Cancel: 1602, nothing touched. Windows'
  message box labels the buttons Cancel/Retry/Ignore (not Abort), and a cancelled
  uninstall ends silently, having no wizard to return to.
- Uninstall with the Simulator running, close + Retry: through, folder gone. The first
  Retry right after closing the window comes back, since the JVM takes a few seconds
  to exit; the text says so now.
- Upgrade with the Simulator running, Cancel: 1602 before `RemoveExistingProducts`,
  99.0.3 intact, WiX's "ended prematurely" page.
- Upgrade with nothing running: no box, straight through; the finish-page launch
  works after an upgrade.

Test logs (`runD`–`runI`) and the failed-upgrade log (`runB-running.log`) live in the
session scratchpads, not in the repository. The machine ended with a per-user 99.0.5
test install.
