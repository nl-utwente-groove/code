# Runs the Eclipse batch compiler (ecj) with the project's JDT null-analysis
# settings on the given .java files. See SKILL.md in this directory.
param(
    [Parameter(Mandatory = $true, ValueFromRemainingArguments = $true)]
    [string[]]$Files
)
$ErrorActionPreference = 'Stop'

$ecjVersion = '3.37.0'
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

$scratch = 'target\ecj-out'
java -jar $ecj -properties .settings/org.eclipse.jdt.core.prefs --release 21 -proc:none `
    -d $scratch -cp "target/classes;target/test-classes;$cp" @Files
exit $LASTEXITCODE
