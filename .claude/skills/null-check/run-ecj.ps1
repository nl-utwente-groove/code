# Runs the Eclipse batch compiler (ecj) with the project's JDT null-analysis
# settings on the given .java files, or on the whole main source tree (-All).
# See SKILL.md in this directory.
param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$Files,
    [switch]$All
)
$ErrorActionPreference = 'Stop'
if (-not $All -and -not $Files) {
    Write-Host 'Usage: run-ecj.ps1 [-All] [<file.java> ...]'
    exit 2
}

$ecjVersion = '3.42.0'
$ecj = Join-Path $env:USERPROFILE ".m2\repository\org\eclipse\jdt\ecj\$ecjVersion\ecj-$ecjVersion.jar"
if (-not (Test-Path $ecj)) {
    Write-Host "Fetching ecj $ecjVersion..."
    mvn -q dependency:get "-Dartifact=org.eclipse.jdt:ecj:$ecjVersion"
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

Write-Host 'Compiling project (mvn -q test-compile)...'
mvn -q test-compile
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$cpFile = 'target\ecj-classpath.txt'
if (-not (Test-Path $cpFile)) {
    Write-Host 'Building dependency classpath...'
    mvn -q dependency:build-classpath "-Dmdep.outputFile=$cpFile"
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}
$cp = (Get-Content $cpFile -Raw).Trim()

if ($All) {
    # Whole main source tree, except module-info.java: the harness compiles in the
    # unnamed module (a modular compile fails because ecj cannot resolve the
    # multi-release module-info of e.g. picocli). See SKILL.md for the one known
    # spurious error this causes (sealed cross-package hierarchy in algebra.syntax).
    $listFile = 'target\ecj-files.txt'
    Get-ChildItem -Recurse src\main\java -Filter *.java |
        Where-Object { $_.Name -ne 'module-info.java' } |
        ForEach-Object { $_.FullName } | Set-Content -Encoding ascii $listFile
    $Files = @("@$listFile")
}

$scratch = 'target\ecj-out'
java -jar $ecj -properties .settings/org.eclipse.jdt.core.prefs --release 21 -proc:none `
    -annotationpath lib/eea `
    -d $scratch -cp "target/classes;target/test-classes;$cp" @Files
exit $LASTEXITCODE
