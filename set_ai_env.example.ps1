param(
    [Parameter(Mandatory = $false)]
    [string]$Provider = "gemini"
)

$providers = @{
    gemini = @{
        AI_PROVIDER    = "gemini"
        GEMINI_API_KEY = "replace_with_local_secret"
        GEMINI_MODEL   = "models/gemma-4-31b-it"
    }
}

if (-not $providers.ContainsKey($Provider)) {
    $available = ($providers.Keys | Sort-Object) -join ", "
    throw "Proveedor '$Provider' no configurado. Disponibles: $available"
}

foreach ($item in $providers[$Provider].GetEnumerator()) {
    if ($item.Value -like "replace_with_*") {
        throw "Configura $($item.Key) con un secreto local antes de ejecutar este script."
    }

    Set-Item -Path ("Env:" + $item.Key) -Value $item.Value
}

Write-Host "Variables cargadas para proveedor: $Provider"
