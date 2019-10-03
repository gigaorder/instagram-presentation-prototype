pipeline {
  agent any
  stages {
    stage('Build APK') {
      sh "./gradlew clean build"
    }
  }
}