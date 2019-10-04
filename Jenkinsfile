pipeline {
  agent any
  stages {
    stage('Build APK') {
      steps {
        sh "cp /var/jenkins_home/files/google-services.json ./app/google-services.json"
        sh "./gradlew clean assemble"
      }
    }
  }
}