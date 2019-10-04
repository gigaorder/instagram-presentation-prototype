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
        sh "mkdir ./orginalBuild"
        sh "cp ./app/build/bakApk/app-1.02.apk ./originalBuild/app-1.02.apk"
        sh "./gradlew tinkerPatchDebug"
        sh "cp /var/jenkins_home/files/ssh.cfg ./app/ssh.cfg"
        sh "./copyPatch"
      }
    }
  }
}