pipeline {
    agent any

    environment {
        BUILD_VERSION = """${
            sh(
                    script: "cat gradle.properties | grep VERSION | sed 's/VERSION=//'",
                    returnStdout: true
            ).trim()
        }"""

        ORIGINAL_APK_FOLDER_PATH = "/var/jenkins_home/files/feed2wall/apk/$BUILD_VERSION"
    }

    stages {
        stage('Download original apk files') {
            steps {
                sh "./download-original-apks.sh"
            }
        }

        stage('Prepare config files') {
            steps {
                sh "cp /var/jenkins_home/files/feed2wall/google-services.json ./app/google-services.json"
                sh "cp /var/jenkins_home/files/feed2wall/ssh.cfg ./ssh.cfg"
            }
        }

        stage('Build APK') {
            steps {
                sh "./gradlew clean assembleDebug"
            }
        }

        stage('Create patch files for all available versions') {
            steps {
                sh "./script/create-patch-files.sh"
            }
        }

        stage('Notify update') {
            steps {
                sh "./notifyUpdate instagramPatching"
            }
        }
    }
}