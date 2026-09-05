# Decision table for pretool-guard.ps1. Run it after touching the guard:
#
#   powershell -NoProfile -ExecutionPolicy Bypass -File .claude/hooks/pretool-guard.tests.ps1
#
# Exits 0 when every case decides as expected, 1 otherwise. Two throwaway
# repositories are created under the temp directory, one on master and one on
# a branch, so that the master-commit guard is exercised without depending on
# the state of any real checkout.

$ErrorActionPreference = 'Stop'
$hook = Join-Path $PSScriptRoot 'pretool-guard.ps1'

function New-TestRepo($name, $branch) {
    $dir = Join-Path ([System.IO.Path]::GetTempPath()) "guard-test-$name-$PID"
    if (Test-Path $dir) { Remove-Item -Recurse -Force $dir }
    New-Item -ItemType Directory -Path $dir | Out-Null
    git -C $dir init --quiet --initial-branch=master
    git -C $dir -c user.name=t -c user.email=t@t commit --quiet --allow-empty -m init
    if ($branch -ne 'master') { git -C $dir checkout --quiet -b $branch }
    return $dir
}

$master = New-TestRepo 'master' 'master'
$branch = New-TestRepo 'branch' 'work'

try {
    $cases = @(
        # the mvn guard: only unquiet, unredirected build runs are denied
        @{ n='mvn verbose test';           c='mvn test';                                     d=$branch; e='deny'  },
        @{ n='mvn verbose package';        c='cd foo && mvn clean package';                  d=$branch; e='deny'  },
        @{ n='mvn quiet redirected';       c='mvn -q test > log 2>&1';                       d=$branch; e='allow' },
        @{ n='mvn after semicolon';        c='echo hi; mvn verify';                          d=$branch; e='deny'  },
        @{ n='mvn on its own line';        c="cd foo`nmvn site";                             d=$branch; e='deny'  },
        @{ n='mvn without a build goal';   c='mvn compile';                                  d=$branch; e='allow' },
        # a build command named in an argument or a message is not a build run
        @{ n='mvn word in -m message';     c='git commit -m "Documented the mvn test gate"'; d=$branch; e='allow' },
        @{ n='mvn cmd in heredoc msg';     c="git commit -F - <<'EOF'`nSubj`n`nRun mvn test.`nEOF"; d=$branch; e='allow' },
        @{ n='mvn line-start in heredoc';  c="git commit -F - <<'EOF'`nSubj`n`nmvn test -Dx`nEOF";  d=$branch; e='allow' },
        @{ n='unquoted heredoc delimiter'; c="cat > f <<EOF`nmvn package`nEOF";              d=$branch; e='allow' },
        @{ n='tab-indented terminator';    c="cat > f <<-EOF`nmvn package`n`tEOF";           d=$branch; e='allow' },
        @{ n='heredoc then real mvn';      c="git commit -F - <<'EOF'`nmsg mvn test`nEOF`nmvn test"; d=$branch; e='deny' },
        # the master guard, on the cwd and on an explicit -C target
        @{ n='commit on master (cwd)';     c='git commit -m "x"';                            d=$master; e='deny'  },
        @{ n='commit on master via -C';    c="git -C $master commit -m ""x""";                d=$branch; e='deny'  },
        @{ n='commit heredoc on master';   c="git commit -F - <<'EOF'`nmsg mvn test`nEOF";   d=$master; e='deny'  },
        @{ n='commit on a branch';         c='git commit -m "x"';                            d=$branch; e='allow' },
        @{ n='git cmd quoted as argument'; c='echo "git commit -m x"';                       d=$master; e='allow' },
        # the push/PR guard
        @{ n='git push';                   c='git push origin HEAD';                         d=$branch; e='ask'   },
        @{ n='git push after &&';          c='cd foo && git push';                           d=$branch; e='ask'   },
        @{ n='git push named in heredoc';  c="git commit -F - <<'EOF'`nRemember to git push`nEOF"; d=$branch; e='allow' },
        @{ n='git push in -m message';     c='git commit -m "note: git push later"';         d=$branch; e='allow' },
        @{ n='gh pr create';               c='gh pr create --fill';                          d=$branch; e='ask'   },
        @{ n='gh pr create quoted';        c='echo "gh pr create --fill"';                   d=$branch; e='allow' },
        @{ n='plain git status';           c='git status --short';                           d=$branch; e='allow' }
    )

    $fails = 0
    foreach ($case in $cases) {
        $evt = @{ tool_input = @{ command = $case.c }; cwd = $case.d } | ConvertTo-Json -Depth 4 -Compress
        $out = $evt | & powershell -NoProfile -ExecutionPolicy Bypass -File $hook
        if ([string]::IsNullOrWhiteSpace($out)) { $got = 'allow' }
        else { $got = ($out | ConvertFrom-Json).hookSpecificOutput.permissionDecision }
        if ($got -eq $case.e) { $mark = 'ok  ' } else { $fails++; $mark = 'FAIL' }
        Write-Output ("{0} {1,-27} expected={2,-5} got={3}" -f $mark, $case.n, $case.e, $got)
    }
    Write-Output "---"
    Write-Output ("{0} cases, {1} failures" -f $cases.Count, $fails)
    exit ($(if ($fails -eq 0) { 0 } else { 1 }))
} finally {
    foreach ($d in @($master, $branch)) {
        try { Remove-Item -Recurse -Force $d -ErrorAction Stop } catch {}
    }
}
