$ErrorActionPreference = 'Stop'
$audioDirectory = Join-Path $PSScriptRoot 'src\main\resources\audio'
New-Item -ItemType Directory -Force -Path $audioDirectory | Out-Null
Add-Type -AssemblyName System.Speech
$synthesizer = New-Object System.Speech.Synthesis.SpeechSynthesizer
$synthesizer.SelectVoice('Microsoft Zira Desktop')
$synthesizer.Rate = 0
$synthesizer.Volume = 100
foreach ($letter in ([char[]]'ABCDEFGHIJKLMNOPQRSTUVWXYZ')) {
    $file = Join-Path $audioDirectory ("{0}.wav" -f $letter)
    if (-not (Test-Path $file) -or (Get-Item $file).Length -eq 0) {
        $synthesizer.SetOutputToWaveFile($file)
        $synthesizer.Speak([string]$letter)
        $synthesizer.SetOutputToNull()
    }
}
$synthesizer.Dispose()
Write-Host "Audio files ready."
