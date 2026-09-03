# Changelog

All notable changes to the **Jenkins Git Parameter Platform** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-09-03

### Summary
Initial production-ready release of the **Jenkins Git Parameter** multi-cluster enterprise platform on **Red Hat OpenShift 4.20+** and **Kubernetes 1.31+**. Reviewed, audited, and hardened using **Gemini 3.8 Flash**.

This architecture orchestrates dynamic interactive branch/tag builds powered by the Jenkins Git Parameter plugin and synchronized with the externalized configuration SSOT repository ([`jenkins-git-parameter-global-vars`](https://github.com/nubenetes/jenkins-git-parameter-global-vars)).

---

### 🚀 Added
- **JCasC GitHub App Credentials**:
  - Mounted credentials configmap at `/var/jenkins_home/casc_configs/github-app-credentials.yaml` in `helm/jenkins/values-openshift.yaml`.
  - Packaged `jenkins-github-credentials` in `deploy.sh`.
- **Clean Decommission Ordering**:
  - `destroy.sh` explicitly deletes `argocd-apps/` resources before terminating namespaces to prevent stuck namespace finalizers.

---

### 🛠️ Fixed
- **Resolved JCasC Startup Crash**:
  - Moved cluster topology mount from `/var/jenkins_home/casc_configs/clusters.yaml` to `/var/jenkins_home/config/clusters.yaml`, eliminating JCasC schema validation crashes.
- **Fixed ArgoCD Pull Request Preview Namespace**:
  - Updated `argocd-apps/applicationset-pull-request-preview.yaml` to deploy into `argocd` namespace instead of `openshift-gitops`.
- **Aligned Container Naming in Ephemeral Security Agents**:
  - Renamed container to `security-tools` in `jcasc/pod-templates.yaml`.
  - Updated `shared-library/vars/cosignSign.groovy` and `shared-library/vars/sbomGenerate.groovy` to reference `container('security-tools')`.
- **Modernized Kustomize Overlays**:
  - Replaced deprecated `bases:` with `resources:` across all overlays in `sample-apps/jhipster-microservice/k8s/overlays/`.

---

### 🧹 Removed
- **Redundant Duplicate Directory**:
  - Removed orphaned duplicate folder `sample-apps/nubenetes-global-vars/` (the actual SSOT is maintained in companion repository `jenkins-git-parameter-global-vars`).
