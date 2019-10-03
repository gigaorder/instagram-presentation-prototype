pipeline {
  agent any
  stages {
    stage('Build APK') {
      steps {
        echo 'building...'
        sh "./gradlew clean build"
        echo 'still building...'
      }
    }
  }
}