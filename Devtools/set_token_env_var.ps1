# Utility script to set environment variable for API token
# Usage: .\set_token_env_var.ps1

# Prompt user for the API token
$API_TOKEN = Read-Host "Please enter your API token (e.g., OpenAI API key)" -AsSecureString
$PlainToken = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($API_TOKEN)
)

# Check if token was provided
if ([string]::IsNullOrWhiteSpace($PlainToken)) {
    Write-Host "Error: No token provided"
    exit 1
}

# Set environment variable for current session
$env:OPENAI_API_KEY = $PlainToken

Write-Host "Environment variable OPENAI_API_KEY has been set successfully"
Write-Host "Note: This variable is only available in the current PowerShell session"
Write-Host "To make it permanent, run:"
Write-Host 'setx OPENAI_API_KEY "your_token"'
