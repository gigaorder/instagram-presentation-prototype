pipeline {
  agent any
  stages {
    stage('Build APK') {
      steps {
        sh "./gradlew clean build"
      }
    }
  }
}