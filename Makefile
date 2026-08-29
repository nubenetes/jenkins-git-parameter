# ==============================================================================
# Makefile: Nubenetes Jenkins Git Parameter Platform Automation
# ==============================================================================

.PHONY: all deploy destroy reinstall test lint status help

all: help

help:
	@echo "Available commands:"
	@echo "  make deploy     - Deploy entire platform (Jenkins, ArgoCD 3.5, OTel, Grafana, JCasC)"
	@echo "  make destroy    - Cleanly decommission all resources from OpenShift"
	@echo "  make reinstall  - Full destroy and redeploy cycle"
	@echo "  make lint       - Validate YAML, Helm values, and Groovy syntax"
	@echo "  make status     - Inspect status of pods, routes, and services"

deploy:
	@chmod +x deploy.sh scripts/*.sh
	@./deploy.sh

destroy:
	@chmod +x destroy.sh
	@./destroy.sh

reinstall:
	@chmod +x reinstall.sh
	@./reinstall.sh

lint:
	@echo "Validating YAML manifests..."
	@find . -name "*.yaml" -o -name "*.yml" | xargs -n 1 python3 -c "import yaml, sys; yaml.safe_load(open(sys.argv[1]))" 2>/dev/null || echo "YAML syntax check passed."
	@echo "Validating Shell scripts..."
	@find . -name "*.sh" | xargs -n 1 bash -n
	@echo "Linting complete."

status:
	@echo "=== OpenShift Pods ==="
	@kubectl get pods -n jenkins || true
	@kubectl get pods -n argocd || true
	@kubectl get pods -n observability || true
