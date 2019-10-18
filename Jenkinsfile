pipeline {
  agent any
  stages {
    stage('Build APK') {
      steps {
        sh "cp /var/jenkins_home/files/feed2wall/google-services.json ./app/google-services.json"
        sh "./gradlew clean build -x lint"
      }
    }

    stage('Store built APK file') {
      steps {
        sh "sh ./script/save-apk.sh"
      }
    }

    stage('Build Tinker patch') {
      steps {
        sh "mkdir -p ./originalBuild"
        sh "sh ./script/move-original-apk.sh"
        sh "./gradlew tinkerPatchDebug"
      }
    }

    stage('Send patch files to Tinker server') {
      steps {
        script {
            VERSION = sh (
                      script: "cat gradle.properties | grep VERSION | sed 's/VERSION=//'",
                      returnStdout: true
                    ).trim()
        }

        sh "cp /var/jenkins_home/files/feed2wall/ssh.cfg ./ssh.cfg"
        sh "./copyPatch -v ${VERSION} -t instagramPatching"
      }
    }
  }
}