#!/bin/bash

# Utility script to set environment variable for API token
# Usage: source ./set_token_env_var.sh

# Prompt user for the API token
echo "Please enter your API token (e.g., OpenAI API key):"
read -s API_TOKEN

# Check if token was provided
if [ -z "$API_TOKEN" ]; then
    echo "Error: No token provided"
    exit 1
fi

# Export the environment variable
export OPENAI_API_KEY="$API_TOKEN"

echo "Environment variable OPENAI_API_KEY has been set successfully"
echo "Note: This variable is only available in the current shell session"
echo "To make it permanent, add 'export OPENAI_API_KEY=\"your_token\"' to your ~/.bashrc or ~/.zshrc"