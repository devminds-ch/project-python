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
            sh 'rm -rf dist build'
        }
        stage('Build documentation') {
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
        stage('Build Python package') {
            sh './tools/build-package.sh'
            archiveArtifacts(
                artifacts: 'dist/*.whl',
                onlyIfSuccessful: true
            )
        }
        stage('Static code analysis') {
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
                    myPy(pattern: 'build/mypy.txt'),
                    pyLint(pattern: 'build/ruff.txt', id: 'ruff', name: 'Ruff')
                ]
            )
        }
        stage('Test Python package') {
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
        stage('Deploy Python package') {
            //withEnv([
            //    'TWINE_REPOSITORY="gitea"'
            //]) {
            //    sh 'twine upload --config-file .pypirc dist/*'
            //}
            withEnv([
                'UV_PUBLISH_URL=http://gitea.lan:3000/api/packages/root/pypi'
            ]) {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'gitea',
                        usernameVariable: 'UV_PUBLISH_USERNAME',
                        passwordVariable: 'UV_PUBLISH_PASSWORD'
                    )
                ]) {
                    sh './tools/deploy-package.sh'
                }
            }
        }
    }
}
