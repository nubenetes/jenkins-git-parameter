// ==============================================================================
// Job DSL: Application CI Build Pipelines
// ==============================================================================

def apps = [
    [
        name: 'jhipster-microservice',
        repoUrl: 'https://github.com/nubenetes/jhipster-microservice.git',
        defaultBranch: 'main',
        jenkinsfile: 'jenkinsfiles/ci/Jenkinsfile.app-java-maven',
        description: 'Cloud-Native Java 21 / Spring Boot Microservice CI Pipeline'
    ],
    [
        name: 'angular-frontend',
        repoUrl: 'https://github.com/nubenetes/angular-frontend.git',
        defaultBranch: 'main',
        jenkinsfile: 'jenkinsfiles/ci/Jenkinsfile.app-angular',
        description: 'Modern Angular 18+ Single Page App CI Pipeline'
    ]
]

apps.each { app ->
    pipelineJob("01-CI-Build-Pipelines/${app.name}-ci-build") {
        description("${app.description}\n\nManaged automatically via Job DSL Seed Job.")
        
        logRotator {
            numToKeep(30)
            daysToKeep(30)
            artifactNumToKeep(10)
        }

        parameters {
            // Git Parameter Plugin on Application Repository
            gitParameterDefinition {
                name('APP_GIT_REVISION')
                type('PT_BRANCH_TAG')
                defaultValue(app.defaultBranch)
                description("Select the Application Git Branch, Tag, or Pull Request to build from ${app.repoUrl}")
                branch('')
                branchFilter('.*')
                tagFilter('*')
                sortMode('DESCENDING_SMART')
                selectedValue('DEFAULT')
                useRepository(app.repoUrl)
                quickFilterEnabled(true)
            }

            booleanParam('TRIGGER_CD_RELEASE', true, 'Automatically trigger CD Release Orchestrator upon successful build and scan.')
            
            stringParam('GLOBAL_VARS_BRANCH', 'main', 'Global Variables branch to use when triggering downstream CD Release Orchestrator.')
            
            choiceParam('TARGET_ENVIRONMENT', ['dev', 'staging', 'prod'], 'Initial deployment target environment for downstream CD release.')
        }

        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            url('https://github.com/nubenetes/jenkins-git-parameter.git')
                        }
                        branch('*/main')
                    }
                }
                scriptPath(app.jenkinsfile)
                lightweight(true)
            }
        }

        triggers {
            // Webhook trigger for automated CI on Git Push / PR
            scm('H/10 * * * *')
        }
    }
}
