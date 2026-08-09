# Runs the headless GROOVE Generator (state-space exploration) on a grammar.
# All arguments are passed through to nl.utwente.groove.explore.Generator.
# See SKILL.md in this directory.
param(
    [Parameter(Mandatory = $true, ValueFromRemainingArguments = $true)]
    [string[]]$Args_
)
$ErrorActionPreference = 'Stop'

Write-Host 'Compiling project (mvn -q compile)...'
mvn -q compile
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$cpFile = 'target\ecj-classpath.txt'
if (-not (Test-Path $cpFile)) {
    Write-Host 'Building dependency classpath...'
    mvn -q dependency:build-classpath "-Dmdep.outputFile=$cpFile"
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}
$cp = (Get-Content $cpFile -Raw).Trim()

java -cp "target/classes;$cp" nl.utwente.groove.explore.Generator @Args_
exit $LASTEXITCODE
