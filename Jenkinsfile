pipeline {
    agent {
        /*docker {
            image 'mcr.microsoft.com/devcontainers/python:1-3.12-bookworm'
        }*/
        dockerfile {
            filename '.devcontainer/Dockerfile'
            dir '.'
        }
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
    }
}
