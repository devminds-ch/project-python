pipeline {
    agent {
        /*docker {
            image 'mcr.microsoft.com/devcontainers/python:1-3.12-bookworm'
        }*/
        dockerfile {
            filename '.devcontainer/Dockerfile'
            dir '.'
            args  '--net="jenkins_default"' // required for accessing the Gitea server
        }
    }
    options {
        disableConcurrentBuilds()
        //skipDefaultCheckout() // default checkout is required for .devcontainer/Dockerfile
        //newContainerPerStage()
    }
    parameters {
        booleanParam(
            name: 'DEPLOY_PACKAGE',
            defaultValue: false,
            description: 'Flag indicating if Python package should be deployed'
        )
    }
    stages {
        stage('Cleanup') {
            steps {
                sh 'rm -rf dist build'
            }
        }
        stage('Build documentation') {
            steps {
                sh './tools/build-docs.sh'
                archiveArtifacts(
                    artifacts: 'build/html/**',
                    onlyIfSuccessful: true
                )
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: false,
                    reportDir: 'build/html/',
                    reportFiles: 'index.html',
                    reportName: 'Documentation',
                    reportTitles: '',
                    useWrapperFileDirectly: true
                ])
            }
        }
        stage('Build Python package') {
            steps {
                sh './tools/build-package.sh'
                archiveArtifacts(
                    artifacts: 'dist/*.whl',
                    onlyIfSuccessful: true
                )
            }
        }
        stage('Static code analysis') {
            steps {
                warnError('lint issues found') {
                    sh './tools/lint-package.sh'
                }
                recordIssues(
                    sourceCodeRetention: 'LAST_BUILD',
                    tools: [
                        taskScanner(
                            highTags: 'FIXME',
                            includePattern: 'src/**/*.py',
                            lowTags: 'HACK',
                            normalTags: 'TODO'
                        ),
                        flake8(pattern: 'build/flake8.txt'),
                        pyLint(pattern: 'build/pylint.txt'),
                        myPy(pattern: 'build/mypy.txt')
                    ]
                )
            }
        }
        stage('Test Python package') {
            steps {
                sh './tools/test-package.sh'
                junit(
                    testResults: 'build/test-report.xml'
                )
                recordCoverage(
                    tools: [
                        [parser: 'JUNIT', pattern: 'build/test-report.xml'],
                        [parser: 'COBERTURA', pattern: 'build/test-coverage.xml']
                    ]
                )
            }
        }
        stage('Deploy Python package') {
            when {
                expression {
                    params.DEPLOY_PACKAGE == true
                }
            }
            //environment {
            //    TWINE_REPOSITORY = 'gitea'
            //}
            //steps {
            //    sh 'twine upload --config-file .pypirc dist/*'
            //}
            environment {
                TWINE_REPOSITORY_URL = 'http://gitea.lan:3000/api/packages/root/pypi'
                TWINE_CREDENTIALS = credentials('gitea')
            }
            steps {
                sh 'twine upload --user $TWINE_CREDENTIALS_USR --password $TWINE_CREDENTIALS_PSW dist/*'
            }
        }
    }
}
