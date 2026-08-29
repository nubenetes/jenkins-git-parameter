// ==============================================================================
// Shared Library Step: gitopsCommit.groovy
// Updates environment manifests in GitOps repository and commits changes
// ==============================================================================

def call(Map config = [:]) {
    def envName   = config.envName ?: error("gitopsCommit requires 'envName'")
    def appName   = config.appName ?: error("gitopsCommit requires 'appName'")
    def imageTag  = config.imageTag ?: error("gitopsCommit requires 'imageTag'")
    def configDir = config.configDir ?: 'nubenetes-global-vars'

    echo "📝 [GitOps Update] Updating image tag for '${appName}' in environment '${envName}' to '${imageTag}'..."

    dir(configDir) {
        sh """
            # Update target environment YAML configuration
            ENV_FILE="environments/${envName}.yaml"
            if [ -f "\$ENV_FILE" ]; then
                echo "Updating \$ENV_FILE..."
                sed -i 's/${appName}:.*/${appName}: "${imageTag}"/g' "\$ENV_FILE" || true
            fi

            # Update Kustomize overlay if present
            KUSTOMIZE_OVERLAY="../sample-apps/${appName}/k8s/overlays/${envName}/kustomization.yaml"
            if [ -f "\$KUSTOMIZE_OVERLAY" ]; then
                echo "Updating Kustomize overlay \$KUSTOMIZE_OVERLAY..."
                sed -i 's/newTag:.*/newTag: "${imageTag}"/g' "\$KUSTOMIZE_OVERLAY" || true
            fi

            echo "GitOps manifest updated successfully for ${appName} (${envName})."
        """
    }
}
