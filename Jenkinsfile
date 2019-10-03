pipeline {
  agent any
  stages {
    stage('Git Checkout') {
      steps {
        checkout scm
        echo 'checkout'
      }
    }

    stage('test') {
      steps {
        echo "My branch is: ${env.BRANCH_NAME}"
      }
    }
  }
}