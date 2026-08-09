# PreToolUse guard for Bash/PowerShell tool calls (see claude/CLAUDE.md).
# Reads the hook event as JSON on stdin; emits a JSON permission decision on
# stdout to deny or force a user prompt, or exits silently to allow the call.
#
# Guards:
#   1. deny  - git commit while the target checkout is on master
#   2. ask   - git push / gh pr create|merge (explicit user confirmation)
#   3. deny  - mvn test/package/install/verify/site runs that are neither
#              quiet (-q) nor redirected (build output floods the context)

$ErrorActionPreference = 'Stop'

function Emit-Decision($decision, $reason) {
    @{
        hookSpecificOutput = @{
            hookEventName            = 'PreToolUse'
            permissionDecision       = $decision
            permissionDecisionReason = $reason
        }
    } | ConvertTo-Json -Depth 4 -Compress
    exit 0
}

try {
    $evt = [Console]::In.ReadToEnd() | ConvertFrom-Json
} catch {
    exit 0
}
$cmd = $evt.tool_input.command
if (-not $cmd) { exit 0 }

# Matches "git [options] <subcommand>" without matching subcommand-like words
# elsewhere in the command line (e.g. inside a commit message).
$gitPrefix = '\bgit(\s+-C\s+("[^"]+"|\S+)|\s+--?[A-Za-z][\w=./\\-]*)*\s+'

if ($cmd -match ($gitPrefix + 'commit\b')) {
    $dir = $evt.cwd
    if ($cmd -match '\bgit\s+-C\s+"([^"]+)"') { $dir = $Matches[1] }
    elseif ($cmd -match '\bgit\s+-C\s+(\S+)') { $dir = $Matches[1] }
    $branch = $null
    try { $branch = git -C $dir rev-parse --abbrev-ref HEAD } catch {}
    if ($LASTEXITCODE -eq 0 -and $branch -eq 'master') {
        Emit-Decision 'deny' ("Refusing to commit: '$dir' is on branch master, and committing to master directly " +
            "is never allowed. The cwd may have silently reverted to the main checkout (worktree-cwd hazard): " +
            "verify the working directory, or create a branch first.")
    }
}

if ($cmd -match ($gitPrefix + 'push\b') -or $cmd -match '\bgh\s+pr\s+(create|merge)\b') {
    Emit-Decision 'ask' 'Project policy: git push and PR creation/merge require explicit user confirmation.'
}

if ($cmd -match '\bmvn\b' -and $cmd -match '\b(test|package|install|verify|site)\b') {
    $quiet      = $cmd -match '(^|\s)(-q|--quiet)\b'
    $redirected = $cmd -match '>'
    if (-not ($quiet -or $redirected)) {
        Emit-Decision 'deny' ('Verbose mvn output floods the model context. Rerun quietly, e.g. ' +
            'mvn -q <goals> > <scratchpad>\mvn.log 2>&1, then grep the log for failures ' +
            '(details land in target\surefire-reports). For full-suite runs, prefer delegating to an ' +
            'Opus subagent that runs the build and reports only failures.')
    }
}

exit 0
