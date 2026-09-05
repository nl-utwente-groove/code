# PreToolUse guard for Bash/PowerShell tool calls (see claude/CLAUDE.md).
# Reads the hook event as JSON on stdin; emits a JSON permission decision on
# stdout to deny or force a user prompt, or exits silently to allow the call.
#
# Guards:
#   1. deny  - git commit while the target checkout is on master
#   2. ask   - git push / gh pr create|merge (explicit user confirmation)
#   3. deny  - mvn test/package/install/verify/site runs that are neither
#              quiet (-q) nor redirected (build output floods the context)
#
# Only text that is really a command is matched: heredoc bodies are stripped
# first, and git/gh/mvn must stand at the start of a command. Otherwise a
# commit message that quotes a build command is read as one, and so is a
# command that merely passes one along as an argument.

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

# A heredoc body is data the command consumes, not commands to run: drop it
# before matching, so a commit whose message is fed in on stdin is judged on
# the git invocation alone rather than on whatever the message quotes. The
# delimiter may be quoted and the terminator tab-indented; <<< is a
# herestring, not a heredoc, and is left alone.
$heredoc = '<<(?!<)-?[ \t]*(\x27|\x22|)([A-Za-z_][A-Za-z0-9_]*)\1[^\n]*\n[\s\S]*?(?:\n[ \t]*\2[ \t]*(?=\n|\z)|\z)'
$scan = [regex]::Replace($cmd, $heredoc, ' <<HEREDOC ')

# Start of a command: string start, or just after a shell separator. Tells a
# real invocation from the same word quoted inside an argument.
$cmdStart = '(?:^|[\n;&|(){}`]|\$\()\s*'

# Matches "git [options] <subcommand>" at the start of a command, so that
# subcommand-like words elsewhere on the line - in a commit message, or in a
# command that passes a git invocation along as a string - do not match.
$gitPrefix = $cmdStart + 'git(\s+-C\s+("[^"]+"|\S+)|\s+--?[A-Za-z][\w=./\-]*)*\s+'

if ($scan -match ($gitPrefix + 'commit\b')) {
    $dir = $evt.cwd
    if ($scan -match '\bgit\s+-C\s+"([^"]+)"') { $dir = $Matches[1] }
    elseif ($scan -match '\bgit\s+-C\s+(\S+)') { $dir = $Matches[1] }
    $branch = $null
    try { $branch = git -C $dir rev-parse --abbrev-ref HEAD } catch {}
    if ($LASTEXITCODE -eq 0 -and $branch -eq 'master') {
        Emit-Decision 'deny' ("Refusing to commit: '$dir' is on branch master, and committing to master directly " +
            "is never allowed. The cwd may have silently reverted to the main checkout (worktree-cwd hazard): " +
            "verify the working directory, or create a branch first.")
    }
}

if ($scan -match ($gitPrefix + 'push\b') -or $scan -match ($cmdStart + 'gh\s+pr\s+(create|merge)\b')) {
    Emit-Decision 'ask' 'Project policy: git push and PR creation/merge require explicit user confirmation.'
}

if ($scan -match ($cmdStart + 'mvn\b') -and $scan -match '\b(test|package|install|verify|site)\b') {
    $quiet      = $scan -match '(^|\s)(-q|--quiet)\b'
    $redirected = $scan -match '>'
    if (-not ($quiet -or $redirected)) {
        Emit-Decision 'deny' ('Verbose mvn output floods the model context. Rerun quietly, e.g. ' +
            'mvn -q <goals> > <scratchpad>\mvn.log 2>&1, then grep the log for failures ' +
            '(details land in target\surefire-reports). For full-suite runs, prefer delegating to an ' +
            'Opus subagent that runs the build and reports only failures.')
    }
}

exit 0
