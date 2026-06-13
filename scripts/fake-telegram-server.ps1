param(
    [string]$Prefix = "http://localhost:19090/"
)

$ErrorActionPreference = "Stop"

$listener = [System.Net.HttpListener]::new()
$listener.Prefixes.Add($Prefix)
$listener.Start()

Write-Host "FAKE_TELEGRAM_STARTED $Prefix"

try {
    while ($listener.IsListening) {
        $context = $listener.GetContext()
        $request = $context.Request
        $response = $context.Response

        if ($request.HttpMethod -eq "POST" -and $request.RawUrl -match "/bot[^/]+/sendMessage") {
            $body = @{
                ok = $true
                result = @{
                    message_id = [int]([DateTimeOffset]::UtcNow.ToUnixTimeSeconds() % 100000)
                }
            } | ConvertTo-Json -Depth 10 -Compress
            $bytes = [System.Text.Encoding]::UTF8.GetBytes($body)
            $response.ContentType = "application/json"
            $response.StatusCode = 200
            $response.OutputStream.Write($bytes, 0, $bytes.Length)
        }
        else {
            $bytes = [System.Text.Encoding]::UTF8.GetBytes('{"ok":false,"description":"not found"}')
            $response.ContentType = "application/json"
            $response.StatusCode = 404
            $response.OutputStream.Write($bytes, 0, $bytes.Length)
        }

        $response.OutputStream.Close()
    }
}
finally {
    if ($listener.IsListening) {
        $listener.Stop()
    }
    $listener.Close()
}
