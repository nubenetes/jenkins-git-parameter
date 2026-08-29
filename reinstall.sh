#!/usr/bin/env bash
# ==============================================================================
# 🔄 Nubenetes 1-Click Platform Reinstall Script
# ==============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "===> Triggering Platform Reinstallation..."
"${SCRIPT_DIR}/destroy.sh"
echo "===> Waiting 10s for namespace finalization..."
sleep 10
"${SCRIPT_DIR}/deploy.sh"
