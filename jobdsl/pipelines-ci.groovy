// ==============================================================================
// Job DSL: Application CI & CI/CD Pipelines (Patterns 1 and 2)
// github.com/nubenetes/jenkins-git-parameter
// ==============================================================================

def globalVarsRepoUrl = 'https://github.com/nubenetes/jenkins-git-parameter-global-vars.git'

def apps = [
    [
        name: 'jhipster-microservice',
        repoUrl: 'https://github.com/nubenetes/jhipster-microservice.git',
        defaultBranch: 'main',
        jenkinsfile: 'jenkinsfiles/ci/Jenkinsfile.app-java-maven',
        dualDropdownJenkinsfile: 'jenkinsfiles/ci/Jenkinsfile.app-java-maven-dual-dropdown',
        description: 'Cloud-Native Java 21 / Spring Boot Microservice'
    ],
    [
        name: 'angular-frontend',
        repoUrl: 'https://github.com/nubenetes/angular-frontend.git',
        defaultBranch: 'main',
        jenkinsfile: 'jenkinsfiles/ci/Jenkinsfile.app-angular',
        dualDropdownJenkinsfile: 'jenkinsfiles/ci/Jenkinsfile.app-angular',
        description: 'Modern Angular 18+ Single Page App'
    ]
]

// ==============================================================================
// PATTERN 2 (RECOMMENDED FOR ENTERPRISE/PROD): Decoupled CI Build Pipeline
// ==============================================================================
apps.each { app ->
    pipelineJob("01-CI-Build-Pipelines/${app.name}-ci-build") {
        description("""
        <b>[PATTERN 2: Decoupled CI Build Pipeline - Recommended]</b><br/>
        ${app.description}<br/>
        • <b>App Revision</b>: Dynamic Git Parameter Dropdown on ${app.repoUrl}<br/>
        • <b>Global Vars</b>: Passed via parameter to downstream 02-CD-Release-Orchestrator.
        """.stripIndent())
        
        logRotator {
            numToKeep(30)
            daysToKeep(30)
            artifactNumToKeep(10)
        }

        parameters {
            // Dropdown on Application Repository
            gitParameterDefinition {
                name('APP_GIT_REVISION')
                type('PT_BRANCH_TAG')
                defaultValue(app.defaultBranch)
                description("Select Application Git Branch/Tag from ${app.repoUrl}")
                branch('')
                branchFilter('.*')
                tagFilter('*')
                sortMode('DESCENDING_SMART')
                selectedValue('DEFAULT')
                useRepository(app.repoUrl)
                quickFilterEnabled(true)
            }

            booleanParam('TRIGGER_CD_RELEASE', true, 'Automatically trigger downstream CD Release Orchestrator.')
            stringParam('GLOBAL_VARS_BRANCH', 'main', 'Global Variables branch to pass to downstream CD Release Orchestrator.')
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
            scm('H/10 * * * *')
        }
    }
}

// ==============================================================================
// PATTERN 1 (PREFERRED FOR DEVELOP/SANDBOX): Dual-Dropdown Multi-Remote Pipeline
// ==============================================================================
apps.each { app ->
    pipelineJob("01-CI-Build-Pipelines/${app.name}-ci-dual-dropdown") {
        description("""
        <b>[PATTERN 1: Dual Git Parameter Dropdowns (Multi-Remote SCM)]</b><br/>
        ${app.description}<br/>
        • <b>Dropdown 1 (App)</b>: Queries ${app.repoUrl}<br/>
        • <b>Dropdown 2 (Global Vars)</b>: Queries ${globalVarsRepoUrl}<br/>
        <i>Preferred for developer preview/sandbox environments for all-in-one execution.</i>
        """.stripIndent())

        logRotator {
            numToKeep(20)
            daysToKeep(15)
        }

        parameters {
            // Dropdown 1: Application Repository
            gitParameterDefinition {
                name('APP_GIT_REVISION')
                type('PT_BRANCH_TAG')
                defaultValue(app.defaultBranch)
                description('Select Branch/Tag from Application Repository')
                useRepository('origin-app')
                sortMode('DESCENDING_SMART')
                selectedValue('DEFAULT')
                quickFilterEnabled(true)
            }

            // Dropdown 2: Global Variables Repository
            gitParameterDefinition {
                name('GLOBAL_VARS_REVISION')
                type('PT_BRANCH_TAG')
                defaultValue('main')
                description('Select Branch/Tag from Global Configuration Repository')
                useRepository('origin-vars')
                sortMode('DESCENDING_SMART')
                selectedValue('DEFAULT')
                quickFilterEnabled(true)
            }

            booleanParam('DEPLOY_TO_DEV', true, 'Deploy directly to OCP DEV environment within this pipeline.')
            choiceParam('TARGET_ENVIRONMENT', ['dev', 'staging'], 'Target preview environment.')
        }

        definition {
            cpsScm {
                scm {
                    git {
                        // Multi-Remote SCM configuration in Job DSL
                        remote {
                            name('origin-app')
                            url(app.repoUrl)
                            refspec('+refs/heads/*:refs/remotes/origin-app/* +refs/tags/*:refs/remotes/origin-app/tags/*')
                        }
                        remote {
                            name('origin-vars')
                            url(globalVarsRepoUrl)
                            refspec('+refs/heads/*:refs/remotes/origin-vars/* +refs/tags/*:refs/remotes/origin-vars/tags/*')
                        }
                        branch('origin-app/main')
                    }
                }
                scriptPath(app.dualDropdownJenkinsfile)
                lightweight(false)
            }
        }
    }
}
