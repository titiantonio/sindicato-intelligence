param(
    [Parameter(Mandatory = $false)]
    [string]$Provider = "gemini"
)

$providers = @{
    gemini = @{
        AI_PROVIDER   = "gemini"
        GEMINI_API_KEY = "AQ.Ab8RN6LgXMd-LyRm1M6G4zSOHMxQMLMU0_F86nD5sKK88-hN-Q"
        GEMINI_MODEL   = "models/gemma-4-31b-it"
    }

    # Ejemplo para futuros proveedores.
    # openai = @{
    #     AI_PROVIDER    = "openai"
    #     OPENAI_API_KEY = "pega_aqui_tu_key"
    #     OPENAI_MODEL   = "gpt-5"
    # }
}

if (-not $providers.ContainsKey($Provider)) {
    $available = ($providers.Keys | Sort-Object) -join ", "
    throw "Proveedor '$Provider' no configurado. Disponibles: $available"
}

foreach ($item in $providers[$Provider].GetEnumerator()) {
    Set-Item -Path ("Env:" + $item.Key) -Value $item.Value
}

Write-Host "Variables cargadas para proveedor: $Provider"