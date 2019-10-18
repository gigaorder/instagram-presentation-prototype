pipeline {
    agent any

    environment {
        VERSION = """${
            sh(
                    script: "cat gradle.properties | grep VERSION | sed 's/VERSION=//'",
                    returnStdout: true
            ).trim()
        }"""

        FOLDER_PATH = "/var/jenkins_home/files/feed2wall/apk/$VERSION"

        PATCH_VERSION_LIST = """${
            sh(
                    script: "ls /var/jenkins_home/files/feed2wall/apk/",
                    returnStdout: true
            ).trim()
        }"""
    }

    stages {
        stage('Prepare config files') {
            steps {
                sh "cp /var/jenkins_home/files/feed2wall/google-services.json ./app/google-services.json"
                sh "cp /var/jenkins_home/files/feed2wall/ssh.cfg ./ssh.cfg"
            }
        }

        stage('Build APK') {
            steps {
                sh "./gradlew clean build -x lint"
            }
        }

        stage('Store built APK file') {
            steps {
                sh "./script/save-apk.sh"
            }
        }

        stage('Build Tinker patch') {
            steps {
                sh "mkdir -p ./originalBuild"
                sh "cp ${FOLDER_PATH}/app.apk ./originalBuild/app.apk"
                sh "./gradlew tinkerPatchDebug"
            }
        }

        stage('Send patch files to Tinker server') {
            steps {
                sh "./copyPatch -v ${VERSION} -t instagramPatching"
            }
        }
    }
}