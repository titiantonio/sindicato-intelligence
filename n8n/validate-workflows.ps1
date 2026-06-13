param(
    [string]$WorkflowDirectory = (Join-Path $PSScriptRoot "workflows")
)

$ErrorActionPreference = "Stop"

$workflowChecks = @(
    @{
        File = "wf_01_capture_news.json"
        Name = "WF-01 Capture News"
        Endpoints = @("/api/v1/auth/login", "/api/v1/sources", "/api/v1/news/bulk")
        RequiresBearer = $true
    },
    @{
        File = "wf_02_classify_news.json"
        Name = "WF-02 Classify News"
        Endpoints = @("/api/v1/auth/login", "/api/v1/news", "/api/v1/classifications/classify")
        RequiresBearer = $true
    },
    @{
        File = "wf_03_detect_events.json"
        Name = "WF-03 Detect Events"
        Endpoints = @("/api/v1/auth/login", "/api/v1/news", "/api/v1/events/detect")
        RequiresBearer = $true
    },
    @{
        File = "wf_04_generate_analysis.json"
        Name = "WF-04 Generate Analysis"
        Endpoints = @("/api/v1/auth/login", "/api/v1/analysis/generate")
        RequiresBearer = $true
    },
    @{
        File = "wf_05_generate_content.json"
        Name = "WF-05 Generate Content"
        Endpoints = @("/api/v1/auth/login", "/api/v1/content/generate")
        RequiresBearer = $true
    },
    @{
        File = "wf_06_publish_telegram.json"
        Name = "WF-06 Publish Telegram"
        Endpoints = @("/api/v1/auth/login", "/api/v1/publications", "/publish")
        RequiresBearer = $true
    }
)

$failures = New-Object System.Collections.Generic.List[string]

foreach ($check in $workflowChecks) {
    $path = Join-Path $WorkflowDirectory $check.File

    if (-not (Test-Path -LiteralPath $path)) {
        $failures.Add("$($check.Name): missing file $($check.File)")
        continue
    }

    $raw = Get-Content -LiteralPath $path -Raw

    try {
        $workflow = $raw | ConvertFrom-Json
    }
    catch {
        $failures.Add("$($check.Name): invalid JSON - $($_.Exception.Message)")
        continue
    }

    $nodeNames = @($workflow.nodes | ForEach-Object { $_.name })

    if (-not ($nodeNames -contains "Authenticate Backend")) {
        $failures.Add("$($check.Name): missing 'Authenticate Backend' node")
    }

    foreach ($endpoint in $check.Endpoints) {
        if ($raw -notlike "*$endpoint*") {
            $failures.Add("$($check.Name): missing expected endpoint fragment '$endpoint'")
        }
    }

    if ($check.RequiresBearer) {
        if ($raw -notmatch '(?i)"name"\s*:\s*"Authorization"') {
            $failures.Add("$($check.Name): missing Authorization header")
        }

        if ($raw -notmatch "Bearer" -or $raw -notmatch "accessToken") {
            $failures.Add("$($check.Name): missing Bearer accessToken usage")
        }
    }

    Write-Host "OK $($check.Name): JSON, auth node, Bearer header and expected endpoints validated"
}

if ($failures.Count -gt 0) {
    Write-Host ""
    Write-Host "n8n workflow validation failed:" -ForegroundColor Red
    foreach ($failure in $failures) {
        Write-Host "- $failure" -ForegroundColor Red
    }
    exit 1
}

Write-Host ""
Write-Host "OK n8n workflows WF-01..WF-06 validated successfully"
