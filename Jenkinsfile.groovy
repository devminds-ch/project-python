node {
    properties([
        disableConcurrentBuilds()
    ])
    stage('Checkout SCM') {
        checkout scm
    }
    stage('Agent Setup') {
        docker.withRegistry('http://gitea.lan:3000', 'gitea') {
            customImage = docker.build(
                "root/jenkins-python:latest",
                "-f .devcontainer/Dockerfile ./")
            customImage.push() // push custom image to the own registry
        }
    }
    customImage.inside('--net="jenkins_default"') { // required for accessing the Gitea server
        stage('Cleanup') {
            sh 'rm -rf dist build docs/_build'
        }
        stage('Build documentation') {
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
        stage('Build Python package') {
            sh 'python -m build --wheel'
            archiveArtifacts(
                artifacts: 'dist/**/*.whl',
                onlyIfSuccessful: true
            )
        }
        stage('Static code analysis') {
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
        stage('Test Python package') {
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
        stage('Deploy Python package') {
            //withEnv([
            //    'TWINE_REPOSITORY="gitea"'
            //]) {
            //    sh 'twine upload --config-file .pypirc dist/*'
            //}
            withEnv([
                'TWINE_REPOSITORY_URL=http://gitea.lan:3000/api/packages/root/pypi'
            ]) {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'gitea',
                        usernameVariable: 'TWINE_USERNAME',
                        passwordVariable: 'TWINE_PASSWORD'
                    )
                ]) {
                    sh 'twine upload dist/*'
                }
            }
        }
    }
}
