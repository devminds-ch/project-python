pipeline {
    agent {
        /*docker {
            image 'mcr.microsoft.com/devcontainers/python:1-3.12-bookworm'
        }*/
        dockerfile {
            filename '.devcontainer/Dockerfile'
            dir '.'
            args  '--net="jenkins_default"' // required for accessing the PyPI server
        }
    }
    options {
        disableConcurrentBuilds()
        //skipDefaultCheckout() // default checkout is required for .devcontainer/Dockerfile
        //newContainerPerStage()
    }
    parameters {
        booleanParam(name: 'DEPLOY_PACKAGE', defaultValue: false, description: 'Flag indicating if Python package should be deployed')
    }
    stages {
        stage('Cleanup') {
            steps {
                sh 'rm -rf dist build docs/_build'
            }
        }
        stage('Build documentation') {
            steps {
                sh 'pip install -e .' // required for python_training_project.version
                sh 'cd docs && pipenv run make html'
                archiveArtifacts(
                    artifacts: 'docs/_build/html/**',
                    onlyIfSuccessful: true
                )
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: false,
                    reportDir: 'docs/_build/html/',
                    reportFiles: 'index.html',
                    reportName: 'Documentation',
                    reportTitles: '',
                    useWrapperFileDirectly: true
                ])
            }
        }
        stage('Static code analysis') {
            steps {
                warnError('flake8 issues found') {
                    sh 'flake8 src/python_training_project --format=pylint > flake8.log'
                }
                warnError('pylint issues found') {
                    sh 'pylint --msg-template="{path}:{line}: [{msg_id}, {obj}] {msg} ({symbol})" src/python_training_project > pylint.log'
                }
                warnError('mypy issues found') {
                    sh 'mypy src/python_training_project > mypy.log'
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
                        flake8(pattern: 'flake8.log'),
                        pyLint(pattern: 'pylint.log'),
                        myPy(pattern: 'mypy.log')
                    ]
                )
            }
        }
        stage('Build Python package') {
            steps {
                sh 'python -m build --wheel'
                archiveArtifacts(
                    artifacts: 'dist/**/*.whl',
                    onlyIfSuccessful: true
                )
            }
        }
        stage('Test Python package') {
            steps {
                sh 'pip install -e .'
                sh 'pytest'
                junit(
                    testResults: 'report.xml'
                )
                recordCoverage(
                    tools: [
                        [parser: 'JUNIT', pattern: 'report.xml'],
                        [parser: 'COBERTURA', pattern: 'coverage.xml']
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
            // https://twine.readthedocs.io/en/stable/
            //environment {
            //    TWINE_REPOSITORY = 'pypiserver'
            //}
            //steps {
            //    sh 'twine upload --config-file .pypirc dist/*'
            //}
            environment {
                TWINE_REPOSITORY_URL = 'http://pypiserver.lan:8082'
                TWINE_CREDENTIALS = credentials('pypiserver')
            }
            steps {
                sh 'twine upload --user $TWINE_CREDENTIALS_USR --password $TWINE_CREDENTIALS_PSW dist/*'
            }
        }
    }
}
