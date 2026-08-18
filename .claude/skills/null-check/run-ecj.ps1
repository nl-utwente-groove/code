# Runs the Eclipse batch compiler (ecj) with the project's JDT null-analysis
# settings on the given .java files (quick check, unnamed module), or on the
# whole project (-All): the main source tree compiled as the named module
# nl.utwente.groove, then the test tree in the unnamed module.
# See SKILL.md in this directory.
param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$Files,
    [switch]$All
)
$ErrorActionPreference = 'Stop'
$usage = 'Usage: run-ecj.ps1 -All | <file.java> ...'
if (-not $All -and -not $Files) {
    Write-Host $usage
    exit 2
}
if ($All -and $Files) {
    Write-Host "-All does not combine with explicit files: $($Files -join ' ')"
    Write-Host $usage
    exit 2
}
# An unrecognized switch lands in $Files; fail fast rather than pass it to ecj
# as a file name.
$unknown = @($Files | Where-Object { $_ -like '-*' })
if ($unknown) {
    Write-Host "Unknown switch(es): $($unknown -join ' ')"
    Write-Host $usage
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

$scratch = 'target\ecj-out'

if ($All) {
    # Whole main source tree, compiled as the named module nl.utwente.groove:
    # module-info.java is included, dependencies go on the module path, and the
    # generated ANTLR sources join the module via --patch-module (they are also
    # part of the compiled file list, since ecj does not associate sourcepath
    # units with the module under compilation).
    #
    # ecj (up to at least 3.44.0) cannot read a module descriptor that exists
    # only under META-INF/versions/ (multi-release jars, e.g. picocli), so such
    # jars are replaced on the module path by a copy in target\ecj-mp with the
    # versioned module-info.class duplicated at the jar root.
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $mpDir = 'target\ecj-mp'
    New-Item -ItemType Directory -Force $mpDir | Out-Null
    $mp = (($cp -split ';') | ForEach-Object {
        $jar = $_
        $patched = Join-Path $mpDir ([IO.Path]::GetFileName($jar))
        if ((Test-Path $patched) -and
                ((Get-Item $patched).LastWriteTime -ge (Get-Item $jar).LastWriteTime)) {
            return (Resolve-Path $patched).Path
        }
        $zip = [System.IO.Compression.ZipFile]::OpenRead($jar)
        try {
            if ($zip.GetEntry('module-info.class')) { return $jar }
            $versioned = @($zip.Entries | ForEach-Object {
                if ($_.FullName -match '^META-INF/versions/(\d+)/module-info\.class$') {
                    [pscustomobject]@{ Entry = $_; Version = [int]$Matches[1] }
                }
            }) | Where-Object { $_.Version -le 21 } | Sort-Object Version | Select-Object -Last 1
            if (-not $versioned) { return $jar }
            $ms = New-Object IO.MemoryStream
            $s = $versioned.Entry.Open()
            $s.CopyTo($ms)
            $s.Close()
            $descriptor = $ms.ToArray()
        } finally {
            $zip.Dispose()
        }
        Copy-Item $jar $patched -Force
        $zip = [System.IO.Compression.ZipFile]::Open((Resolve-Path $patched), 'Update')
        try {
            $os = $zip.CreateEntry('module-info.class').Open()
            $os.Write($descriptor, 0, $descriptor.Length)
            $os.Close()
        } finally {
            $zip.Dispose()
        }
        Write-Host "Patched multi-release module descriptor: $([IO.Path]::GetFileName($jar))"
        return (Resolve-Path $patched).Path
    }) -join ';'

    $listFile = 'target\ecj-files.txt'
    @(Get-ChildItem -Recurse src\main\java -Filter *.java) +
        @(Get-ChildItem -Recurse target\generated-sources\antlr3 -Filter *.java) |
        ForEach-Object { $_.FullName } | Set-Content -Encoding ascii $listFile

    Write-Host 'Checking main tree (named module)...'
    java -jar $ecj -properties .settings/org.eclipse.jdt.core.prefs --release 21 -proc:none `
        -annotationpath lib/eea `
        -d $scratch --module-path $mp `
        --patch-module "nl.utwente.groove=target\generated-sources\antlr3" "@$listFile"
    $mainExit = $LASTEXITCODE

    # The test tree compiles in the unnamed module, like the per-file mode: the
    # test dependencies (JUnit) are not required by module-info, and the test
    # sources contain no cross-package sealed hierarchies, so the modular/unnamed
    # distinction does not affect the analysis there.
    $testListFile = 'target\ecj-test-files.txt'
    Get-ChildItem -Recurse src\test\java -Filter *.java |
        ForEach-Object { $_.FullName } | Set-Content -Encoding ascii $testListFile
    Write-Host 'Checking test tree (unnamed module)...'
    java -jar $ecj -properties .settings/org.eclipse.jdt.core.prefs --release 21 -proc:none `
        -annotationpath lib/eea `
        -d $scratch -cp "target/classes;target/test-classes;$cp" "@$testListFile"
    if ($mainExit -ne 0) { exit $mainExit }
    exit $LASTEXITCODE
}

java -jar $ecj -properties .settings/org.eclipse.jdt.core.prefs --release 21 -proc:none `
    -annotationpath lib/eea `
    -d $scratch -cp "target/classes;target/test-classes;$cp" @Files
exit $LASTEXITCODE
