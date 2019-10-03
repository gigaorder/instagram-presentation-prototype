pipeline {
  agent any
  stages {
    stage('Build APK') {
      steps {
        sh "yes | $ANDROID_HOME/bin/sdkmanager --licenses"
        sh "./gradlew clean build"
      }
    }
  }
}