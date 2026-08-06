// Windows agent. Assumes 'java' (JDK 21) and 'mvn' are already on the agent's PATH.
// Mirrors .github/workflows/api-tests.yml: same params, same mvn goals, same artifacts.

pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '30'))
        disableConcurrentBuilds()
    }

    parameters {
        choice(name: 'ENV', choices: ['qa', 'prod'], description: 'Target environment (matches config/<env>.properties)')
        choice(name: 'SUITE', choices: ['testng', 'testng-sequential', 'testng-dependency'],
               description: 'testng = parallel, testng-sequential = one scenario at a time, testng-dependency = smoke gates regression')
        string(name: 'TAGS', defaultValue: '', description: 'Cucumber tag filter, e.g. "@smoke" (leave blank to use each runner\'s own tags)')
        string(name: 'THREADS', defaultValue: '4', description: 'Parallel thread count (data-provider-thread-count)')
        string(name: 'AUTH_CREDENTIALS_ID', defaultValue: '',
               description: 'Optional Jenkins "Username with password" credential ID for auth.username/auth.password. Leave blank to use the demo values in config.properties.')
    }

    triggers {
        // Nightly regression, mirrors the GitHub Actions '0 2 * * *' schedule.
        cron('0 2 * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    // Works both ways: if the job is "Pipeline script from SCM", this
                    // checks out the repo normally. If the job is a pasted "Pipeline
                    // script" with a custom workspace already pointed at the project
                    // folder, there is no SCM configured, so we just use what's there.
                    try {
                        checkout scm
                    } catch (ignored) {
                        echo 'No SCM configured for this job — using the existing workspace contents.'
                    }
                }
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    def tagArg = params.TAGS?.trim() ? "-Dcucumber.filter.tags=\"${params.TAGS}\"" : ''
                    def mvnCmd = "mvn -B --no-transfer-progress clean verify -Denv=${params.ENV} -Dsuite=${params.SUITE} -Dthread.count=${params.THREADS} ${tagArg}"

                    if (params.AUTH_CREDENTIALS_ID?.trim()) {
                        withCredentials([usernamePassword(credentialsId: params.AUTH_CREDENTIALS_ID,
                                                           usernameVariable: 'AUTH_USERNAME',
                                                           passwordVariable: 'AUTH_PASSWORD')]) {
                            bat "${mvnCmd} -Dauth.username=%AUTH_USERNAME% -Dauth.password=%AUTH_PASSWORD%"
                        }
                    } else {
                        bat mvnCmd
                    }
                }
            }
        }
    }

    post {
        always {
            // TestNG/Failsafe results as a build check (JUnit plugin, ships with Jenkins core).
            junit testResults: 'target/failsafe-reports/TEST-*.xml', allowEmptyResults: true

            // Allure Jenkins Plugin trend + report link. Requires the plugin installed and
            // a tool named 'allure' under Manage Jenkins -> Tools -> Allure Commandline.
            allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]

            archiveArtifacts artifacts: 'target/allure-report/**, target/cucumber-report.html, target/failsafe-reports/**, target/logs/**',
                              allowEmptyArchive: true,
                              fingerprint: false
        }
    }
}
