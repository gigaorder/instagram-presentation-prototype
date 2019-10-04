pipeline {
  agent any
  stages {
    stage('Build APK') {
      steps {
        sh "cp /var/jenkins_home/files/google-services.json ./app/google-services.json"
        sh "./gradlew clean build -x lint"
      }
    }

    stage('Build Tinker patch') {
      steps {
        sh "mkdir -p ./originalBuild"
        sh "cp ./app/build/bakApk/app-1.02.apk ./originalBuild/app.apk"
        sh "./gradlew tinkerPatchDebug"
      }
    }

    stage('Send patch files to Tinker server') {
      steps {
        sh "cp /var/jenkins_home/files/ssh.cfg ./ssh.cfg"
        sh "./copyPatch"
      }
    }
  }
}