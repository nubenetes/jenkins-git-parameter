#!/usr/bin/env bash
# ==============================================================================
# 🧹 Nubenetes 1-Click Platform Decommission Script
# ==============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "======================================================================"
echo "🧹 DECOMMISSIONING NUBENETES PLATFORM ON OPENSHIFT 4.20+"
echo "======================================================================"

kubectl delete -f "${SCRIPT_DIR}/argocd-apps/" --ignore-not-found=true || true

if command -v helm &>/dev/null; then
    echo "Uninstalling Helm Releases..."
    helm uninstall jenkins -n jenkins || true
    helm uninstall argocd -n argocd || true
    helm uninstall grafana -n observability || true
    helm uninstall prometheus -n observability || true
    helm uninstall otel-collector -n observability || true
fi

echo "Removing Kubernetes Namespaces..."
kubectl delete namespace jenkins --ignore-not-found=true || true
kubectl delete namespace argocd --ignore-not-found=true || true
kubectl delete namespace observability --ignore-not-found=true || true
kubectl delete namespace nubenetes-dev-apps --ignore-not-found=true || true
kubectl delete namespace nubenetes-staging-apps --ignore-not-found=true || true
kubectl delete namespace nubenetes-prod-apps --ignore-not-found=true || true

echo "======================================================================"
echo "✅ All resources cleanly destroyed."
echo "======================================================================"
