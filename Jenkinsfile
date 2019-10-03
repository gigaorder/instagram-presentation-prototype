pipeline {
  agent any
  stages {
    stage('Build APK') {
      steps {
        sh "yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses"
        sh "./gradlew clean build"
      }
    }
  }
}