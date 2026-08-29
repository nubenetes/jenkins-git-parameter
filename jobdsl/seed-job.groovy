// ==============================================================================
// Master Seed Job: Job DSL Entrypoint for Nubenetes IaC Platform
// github.com/nubenetes/jenkins-git-parameter
// ==============================================================================

// Create Folders to organize CI, CD, and Administrative pipelines
folder('01-CI-Build-Pipelines') {
    description('Continuous Integration pipelines: compile, unit-test, security scan, container build & push to OCP DEV registry.')
}

folder('02-CD-Release-Orchestrators') {
    description('Continuous Delivery & GitOps Release Orchestrators: Global Vars Git Parameter, Skopeo multi-cluster promotion, ArgoCD 3.5 sync.')
}

folder('03-Platform-Maintenance') {
    description('Platform maintenance, token rotation, and multi-cluster health check pipelines.')
}

// Evaluate sub-scripts
println "===> Loading CI Build Pipelines DSL..."
// pipelines-ci.groovy and pipelines-cd.groovy are executed in the Job DSL step targets
