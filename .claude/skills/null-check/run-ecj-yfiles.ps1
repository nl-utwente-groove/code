# Runs the Eclipse batch compiler (ecj) with the JDT null-analysis settings of
# the yFiles backend (the unit in yfiles/) over that unit's whole source tree.
# Needs the licensed yFiles library in the local Maven repository.
# See SKILL.md in this directory, section "The yFiles backend".
param(
    [switch]$UseInstalledJar,
    [switch]$Refresh
)
$ErrorActionPreference = 'Stop'

# the enclosing checkout provides the external JDK annotations and, if built,
# the core classes, as the groove project does in the Eclipse workspace
$root = (Resolve-Path (Join-Path $PSScriptRoot '..\..\..')).Path
$unit = Join-Path $root 'yfiles'
$eea = Join-Path $root 'lib\eea'
Set-Location $unit

$ecjVersion = '3.44.0'
$ecj = Join-Path $env:USERPROFILE ".m2\repository\org\eclipse\jdt\ecj\$ecjVersion\ecj-$ecjVersion.jar"
if (-not (Test-Path $ecj)) {
    Write-Host "Fetching ecj $ecjVersion..."
    mvn -q dependency:get "-Dartifact=org.eclipse.jdt:ecj:$ecjVersion"
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

# Invoking the goal directly runs no lifecycle phase, so none of the guards of
# the unit's pom (revision, licence file, freshness of the core) is triggered.
New-Item -ItemType Directory -Force (Join-Path $unit 'target') | Out-Null
$cpFile = Join-Path $unit 'target\ecj-classpath.txt'
if ($Refresh -or -not (Test-Path $cpFile) -or
        (Get-Item $cpFile).LastWriteTime -lt (Get-Item (Join-Path $unit 'pom.xml')).LastWriteTime) {
    Write-Host 'Building dependency classpath...'
    mvn -q dependency:build-classpath "-Dmdep.outputFile=$cpFile"
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}
$entries = @((Get-Content $cpFile -Raw).Trim() -split ';' | Where-Object { $_ })
$isGrooveJar = { $_ -like '*\nl\utwente\groove\groove\*' }

$classes = Join-Path $root 'target\classes'
if (-not $UseInstalledJar -and (Test-Path (Join-Path $classes 'nl\utwente\groove'))) {
    $entries = @($classes) + @($entries | Where-Object { -not (& $isGrooveJar) })
    Write-Host "GROOVE classes: $classes"
} else {
    Write-Host "GROOVE classes: $(@($entries | Where-Object $isGrooveJar) -join ';')"
}

$log = Join-Path $unit 'target\ecj-problems.txt'
Remove-Item $log -Force -ErrorAction SilentlyContinue
Write-Host 'Checking yfiles\src\main\java and yfiles\src\test\java...'
# The unit is a named module, but it is checked in the unnamed module, on the
# class path: its module descriptor is left out. The null analysis does not
# depend on it, and with it the test tree would need the same --add-exports as
# in the pom, and ecj a readable descriptor for every jar on the module path
# (see run-ecj.ps1). What the descriptor is there for, access between the
# modules, the Maven build of the unit checks.
$listFile = Join-Path $unit 'target\ecj-sources.txt'
$sources = Get-ChildItem src\main\java, src\test\java -Recurse -Filter *.java |
    Where-Object { $_.Name -ne 'module-info.java' } |
    ForEach-Object { '"' + ($_.FullName -replace '\\', '/') + '"' }
[IO.File]::WriteAllLines($listFile, $sources)
java -jar $ecj -properties .settings/org.eclipse.jdt.core.prefs --release 21 -proc:none `
    -encoding UTF-8 -annotationpath $eea -d none -log $log `
    -cp ($entries -join ';') "@$listFile"
$exit = $LASTEXITCODE

# ecj has printed any problems itself; the log decides the outcome, since ecj
# exits with 0 on warnings alone. Its first lines are comments naming the date
# and the compiler.
$problems = @(if (Test-Path $log) { Get-Content $log | Where-Object { $_ -notmatch '^#' } })
if (($problems -join '').Trim()) {
    Write-Host "Problems found; also written to $log."
    exit 1
}
if ($exit -ne 0) { exit $exit }
Write-Host 'No problems.'
exit 0
